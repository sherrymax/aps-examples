package com.example.aps.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

public class OAuthApiCallDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Values pulled from process variables
        String clientId = (String) execution.getVariable("clientId");
        String clientSecret = (String) execution.getVariable("clientSecret");
        String scope = (String) execution.getVariable("scope"); // optional, but often required
        String tokenUrl = "https://auth.iam.experience.hyland.com/idp/connect/token";

        System.out.println("======= API Call - Sending Data =========");
        System.out.println("TokenURL:" + tokenUrl);
        System.out.println("clientId:" + clientId);
        System.out.println("clientSecret:" + clientSecret);
        System.out.println("scope:" + scope);
        //System.out.println("apiUrl:" + apiUrl);
        //System.out.println("httpMethod:" + method);
        //System.out.println("requestBody:" + requestBody);
        System.out.println("==========  END ========");


        // Build body (x-www-form-urlencoded)
        StringBuilder body = new StringBuilder();
        body.append("grant_type=client_credentials");
        if (scope != null && !scope.isEmpty()) {
            body.append("&scope=").append(URLEncoder.encode(scope, StandardCharsets.UTF_8));
        }
        body.append("&client_id=").append(URLEncoder.encode(clientId, StandardCharsets.UTF_8));
        body.append("&client_secret=").append(URLEncoder.encode(clientSecret, StandardCharsets.UTF_8));

        // HTTP POST
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(tokenUrl);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setEntity(new StringEntity(body.toString()));

            try (CloseableHttpResponse response = client.execute(post)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("Token request failed: " + response.getStatusLine() + " - " + responseBody);
                }

                // Parse JSON
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(responseBody);
                String accessToken = json.get("access_token").asText();

                // Save token into process variable
                execution.setVariable("accessToken", accessToken);
            }
        }
    }
}
