package com.activiti.extension.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateHelper;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.ExpressionManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom APS Service Task for API Invocation
 * This extension allows processes to make HTTP API calls and handle responses
 */
@Component("oAuth20ApiDelegate")
public class OAuth20ApiDelegate implements JavaDelegate {

//    @Autowired
//    private EndPointService endPointService;

    // Process variable names for input parameters

    protected static final String EXPRESSION_API_URL = "apiUrl";
    protected static final String EXPRESSION_HTTP_METHOD = "httpMethod";
    protected static final String EXPRESSION_REQUEST_BODY = "requestBody";
    protected static final String EXPRESSION_HEADERS = "headers";
    protected static final String EXPRESSION_TIMEOUT = "timeout";


    // Process variable names for output
    protected static final String EXPRESSION_RESPONSE_CODE = "responseCode";
    protected static final String EXPRESSION_RESPONSE_MESSAGE = "responseMessage";
    protected static final String EXPRESSION_RESPONSE_BODY = "responseBody";
    protected static final String EXPRESSION_API_SUCCESS = "apiSuccess";

    private static final Logger logger = LoggerFactory.getLogger(OAuth20ApiDelegate.class);

    public void execute(DelegateExecution execution) throws Exception {
        logger.info("Starting API invocation for process: {}", execution.getProcessInstanceId());

        Expression apiUrlExpression = DelegateHelper.getFieldExpression(execution, EXPRESSION_API_URL);
        Expression httpMethodExpression = DelegateHelper.getFieldExpression(execution, EXPRESSION_HTTP_METHOD);
        Expression requestBodyExpression = DelegateHelper.getFieldExpression(execution, EXPRESSION_REQUEST_BODY);
        Expression headersExpression = DelegateHelper.getFieldExpression(execution, EXPRESSION_HEADERS);
        Expression timeOutExpression = DelegateHelper.getFieldExpression(execution, EXPRESSION_TIMEOUT);

        Expression responseCodeExpression = DelegateHelper.getFieldExpression(execution, EXPRESSION_RESPONSE_CODE);
        Expression responseMessageExpression = DelegateHelper.getFieldExpression(execution, EXPRESSION_RESPONSE_MESSAGE);
        Expression responseBodyExpression = DelegateHelper.getFieldExpression(execution, EXPRESSION_RESPONSE_BODY);
        Expression apiSuccessExpression = DelegateHelper.getFieldExpression(execution, EXPRESSION_API_SUCCESS);


        // Extract input parameters from process variables
        String url = (String) getExpressionValue(execution, apiUrlExpression);
        String method = (String) getExpressionValue(execution, httpMethodExpression);
        String requestBody = (String) getExpressionValue(execution, requestBodyExpression);
        String headersJson = (String) getExpressionValue(execution, headersExpression);
        int timeout = getIntVariable(execution, (String) getExpressionValue(execution, timeOutExpression), 30000);

        String responseCodeVariable = (String) getExpressionValue(execution, responseCodeExpression);
        String responseMessageVariable = (String) getExpressionValue(execution, responseMessageExpression);
        String responseBodyVariable = (String) getExpressionValue(execution, responseBodyExpression);
        String apiSuccessVariable = (String) getExpressionValue(execution, apiSuccessExpression);

        try {

            // Validate required parameters
            if (url == null || url.trim().isEmpty()) {
                throw new IllegalArgumentException("API URL is required");
            }

//            headersJson = buildRequestHeader(headersJson);

            System.out.println("======= API Call - Sending Data =========");
            System.out.println("URL:" + url);
            System.out.println("method:" + method);
            System.out.println("requestBody:" + requestBody);
            System.out.println("headersJson:" + headersJson);
            System.out.println("timeout:" + timeout);

            // Make the API call
            ApiResponse apiResponse = invokeApi(url, method, requestBody, headersJson, timeout);

            // Set response variables in process
            execution.setVariable(responseCodeVariable, apiResponse.getStatusCode());
            execution.setVariable(responseMessageVariable, apiResponse.getMessage());
            execution.setVariable(responseBodyVariable, apiResponse.getBody());
            execution.setVariable(apiSuccessVariable, apiResponse.isSuccess());

            System.out.println("*** **** **** **** ***");
            logger.info("API call completed. Status: {}, Success: {}", apiResponse.getStatusCode(), apiResponse.isSuccess());
            System.out.println("Response Status Code:" + apiResponse.getStatusCode());
            System.out.println("Response Message:" + apiResponse.getMessage());
            System.out.println("Response Body:" + apiResponse.getBody());
            System.out.println("Is API Call successful ? " + apiResponse.isSuccess());
            System.out.println("*** **** **** **** ***");

            System.out.println("==========  API Call - Sending END ========");


        } catch (Exception e) {
            logger.error("Error during API invocation", e);

            // Set error information in process variables
            execution.setVariable(responseCodeVariable, 500);
            execution.setVariable(responseMessageVariable, "Internal error: " + e.getMessage());
            execution.setVariable(responseBodyVariable, null);
            execution.setVariable(apiSuccessVariable, false);
        }
    }

    private String getExpressionValue(DelegateExecution execution, Expression field) {
        if(field != null){
            ExpressionManager expressionManager = Context.getProcessEngineConfiguration().getExpressionManager();
            Expression expression = expressionManager.createExpression(field.getExpressionText());
            return expression.getValue(execution).toString();
        }else{
            return "";
        }
    }

    private ApiResponse invokeApi(String url, String method, String requestBody,
                                  String headersJson, int timeout) throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            // Create HTTP request based on method
            HttpRequestBase request = createHttpRequest(method, url);

            // Set request body for POST/PUT/PATCH
            if (requestBody != null && !requestBody.trim().isEmpty() &&
                    (request instanceof HttpPost || request instanceof HttpPut || request instanceof HttpPatch)) {

                StringEntity entity = new StringEntity(requestBody, "UTF-8");
                entity.setContentType("application/json");

                if (request instanceof HttpPost) {
                    ((HttpPost) request).setEntity(entity);
                } else if (request instanceof HttpPut) {
                    ((HttpPut) request).setEntity(entity);
                } else if (request instanceof HttpPatch) {
                    ((HttpPatch) request).setEntity(entity);
                }
            }

            // Set headers
            setHeaders(request, headersJson);

            // Execute request
            HttpResponse response = httpClient.execute(request);

            // Process response
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
            String statusMessage = generateResponseMessage(response, statusCode);

            boolean isSuccess = statusCode >= 200 && statusCode < 300;

            return new ApiResponse(statusCode, statusMessage, responseBody, isSuccess);

        } finally {
            httpClient.close();
        }
    }

    private HttpRequestBase createHttpRequest(String method, String url) {
        switch (method.toUpperCase()) {
            case "GET":
                return new HttpGet(URI.create(url));
            case "POST":
                return new HttpPost(URI.create(url));
            case "PUT":
                return new HttpPut(URI.create(url));
            case "DELETE":
                return new HttpDelete(URI.create(url));
            case "PATCH":
                return new HttpPatch(URI.create(url));
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
    }

    private void setHeaders(HttpRequestBase request, String headersJson) {
        if (headersJson != null && !headersJson.trim().isEmpty()) {
            try {
                // Parse headers JSON (simplified parsing - consider using Jackson for complex cases)
                Map<String, String> headers = parseHeadersJson(headersJson);
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    request.setHeader(header.getKey(), header.getValue());
                }
            } catch (Exception e) {
                logger.warn("Failed to parse headers JSON: {}", headersJson, e);
            }
        }

        // Set default headers if not provided
        if (request.getFirstHeader("Content-Type") == null) {
            request.setHeader("Content-Type", "application/json");
        }
    }

    private Map<String, String> parseHeadersJson(String headersJson) {
        Map<String, String> headers = new HashMap<>();

        // Simple JSON parsing - replace with proper JSON library for production
        headersJson = headersJson.trim();
        if (headersJson.startsWith("{") && headersJson.endsWith("}")) {
            headersJson = headersJson.substring(1, headersJson.length() - 1);
            String[] pairs = headersJson.split(",");

            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replaceAll("\"", "");
                    String value = keyValue[1].trim().replaceAll("\"", "");
                    headers.put(key, value);
                }
            }
        }

        return headers;
    }

//    private String getStringVariable(DelegateExecution execution, String variableName) {
//        return getStringVariable(execution, variableName, null);
//    }
//
//    private String getStringVariable(DelegateExecution execution, String variableName, String defaultValue) {
//        Object value = execution.getVariable(variableName);
//        return value != null ? value.toString() : defaultValue;
//    }

    private int getIntVariable(DelegateExecution execution, String variableName, int defaultValue) {
        Object value = execution.getVariable(variableName);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }

    private String buildRequestHeader(String headersJson){
        try {
            String authHeaderJson = "{\"Authorization\": \"Basic ZGVtbzpkZW1v\"}";

            System.out.println("authHeaderJson >>> :" + authHeaderJson);
            System.out.println("headersJson >>> :" + headersJson);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode json1 = (ObjectNode) mapper.readTree(headersJson);
            ObjectNode json2 = (ObjectNode) mapper.readTree(authHeaderJson);

            // Merge json2 into json1
            json1.setAll(json2);

            return json1.toPrettyString();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateResponseMessage(HttpResponse response, int statusCode) {
        String statusMessage = response.getStatusLine().getReasonPhrase();

        if(statusMessage.trim().length() == 0){
            if (statusCode >= 200 && statusCode < 300) {
                statusMessage = "Request successful. The server has responded as required.";
            } else if (statusCode == 401) {
                statusMessage = "Unauthorized : The request has not been applied to the target resource because it lacks valid authentication credentials for that resource.";
            }
        }
        return statusMessage;
    }
}