package com.example.aps.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ContextEnrichmentApiDelegate implements JavaDelegate {

    private static final String CONTEXT_API_BASE = "https://knowledge-enrichment.ai.experience.hyland.com/latest/api/context-enrichment";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String accessToken = (String) execution.getVariable("accessToken");
        if (accessToken == null || accessToken.isEmpty()) {
            throw new RuntimeException("Missing access token; run OAuthApiCallDelegate first.");
        }

        System.out.println("====>> STEP 1: Get presigned upload URL");
        // === STEP 1: Get presigned upload URL ===
        String presignUrl;
        String resourceName;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = CONTEXT_API_BASE + "/files/upload/presigned-url?contentType=image%2Fjpeg";
            HttpGet presignRequest = new HttpGet(url);
            presignRequest.setHeader("Authorization", "Bearer " + accessToken);

            try (CloseableHttpResponse response = client.execute(presignRequest)) {
                String respText = EntityUtils.toString(response.getEntity());
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("Failed to get presigned URL: "
                            + response.getStatusLine() + " - " + respText);
                }

                System.out.println("Presigned URL response: " + respText);
                JsonNode json = MAPPER.readTree(respText);
                presignUrl = json.get("presignedUrl").asText();
                resourceName = json.get("objectKey").asText();
            }
        }

        System.out.println("====>> STEP 2: Upload image to presigned URL ===");
        // === STEP 2: Upload image to presigned URL ===
        String imageBase64 = (String) execution.getVariable("imageBase64");
        if (imageBase64 == null || imageBase64.isEmpty()) {
            throw new RuntimeException("Missing imageBase64; cannot upload.");
        }

        // Strip prefix if present (e.g. "data:image/jpeg;base64,...")
        //String[] ib = imageBase64.split(",");
        //String ib64 = ib.length > 1 ? ib[1] : ib[0];
        byte[] imageBytes = Base64.getDecoder().decode(imageBase64);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPut uploadRequest = new HttpPut(presignUrl);
            uploadRequest.setEntity(new ByteArrayEntity(imageBytes));
            uploadRequest.setHeader("Content-Type", "image/jpeg");

            try (CloseableHttpResponse uploadResp = client.execute(uploadRequest)) {
                int status = uploadResp.getStatusLine().getStatusCode();
                if (status < 200 || status >= 300) {
                    throw new RuntimeException("Image upload failed: "
                            + status + " - " + EntityUtils.toString(uploadResp.getEntity()));
                }
            }
        }

        // === STEP 3: Call Context API to process image ===
        System.out.println("====>> STEP 3: CALL CONTEXT API");
        String processingId;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost processRequest = new HttpPost(CONTEXT_API_BASE + "/content/process");
            processRequest.setHeader("Authorization", "Bearer " + accessToken);
            processRequest.setHeader("Content-Type", "application/json");
            processRequest.setHeader("accept", "application/json");

            ObjectNode bodyNode = MAPPER.createObjectNode();
            bodyNode.putArray("objectKeys").add(resourceName);
            bodyNode.putArray("actions").add("image-description");
            bodyNode.put("contentType", "application/json");
            bodyNode.put("maxWordCount", 200);

            processRequest.setEntity(new StringEntity(bodyNode.toString(), StandardCharsets.UTF_8));

            try (CloseableHttpResponse resp = client.execute(processRequest)) {
                String respText = EntityUtils.toString(resp.getEntity());
                if (resp.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("Failed to process image: "
                            + resp.getStatusLine() + " - " + respText);
                }

                JsonNode resultJson = MAPPER.readTree(respText);
                processingId = resultJson.get("processingId").asText();
                System.out.println("Received processingId: " + processingId);
            }
        }

        // Wait 2 minutes (log every 30 seconds)
        /*
        System.out.println("====>> Waiting 2 min for image upload and processing...");
        for (int i = 0; i < 4; i++) {
            Thread.sleep(30_000);
            System.out.println("...waiting (" + ((i + 1) * 30) + "s / 120s)");
        }
        */


        // === STEP 4: Poll results using processingId ===
        System.out.println("====>> Polling results using the processingId: " + processingId);

        String description = "";
        int maxAttempts = 12;       // e.g., 12 attempts × 10s = 2 minutes max
        int attempt = 0;
        boolean resultsReady = false;

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String resultsUrl = CONTEXT_API_BASE + "/content/process/" + processingId + "/results";

            while (attempt < maxAttempts && !resultsReady) {
                HttpGet resultsRequest = new HttpGet(resultsUrl);
                resultsRequest.setHeader("Authorization", "Bearer " + accessToken);
                resultsRequest.setHeader("accept", "application/json");

                try (CloseableHttpResponse resp = client.execute(resultsRequest)) {
                    String respText = EntityUtils.toString(resp.getEntity());
                    if (resp.getStatusLine().getStatusCode() != 200 && resp.getStatusLine().getStatusCode() != 202) {
                        throw new RuntimeException("Failed to retrieve results: "
                                + resp.getStatusLine() + " - " + respText);
                    }

                    JsonNode resultsJson = MAPPER.readTree(respText);

                    // Check if processing is finished
                    JsonNode resultsArray = resultsJson.path("results");
                    if (resultsJson.path("status").equals("PROCESSING")) {
                        System.out.println("Results not ready yet, waiting 10 seconds... (attempt " + (attempt + 1) + ")");
                    } else if (resultsArray.isArray() && resultsArray.size() > 0) {
                        JsonNode firstResult = resultsArray.get(0);
                        JsonNode imageDescNode = firstResult.path("imageDescription");
                        if (imageDescNode.has("result")) {
                            description = imageDescNode.get("result").asText();
                            resultsReady = true;
                            System.out.println("Extracted image description: " + description);
                        }
                    }
                }

                if (!resultsReady) {
                    attempt++;
                    Thread.sleep(10_000);  // wait 10 seconds before next poll
                }
            }
        }

        if (!resultsReady) {
            System.out.println("Warning: Results were not ready after max polling attempts.");
            description = "";
        }

        // Store variables
        execution.setVariable("uploadedResourceName", resourceName);
        execution.setVariable("imageDescription", description);

    }
}
