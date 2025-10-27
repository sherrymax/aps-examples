package com.activiti.extension.bean;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateHelper;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.ExpressionManager;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlElementDecl;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component("oAuth20TokenGenerator")
public class OAuth20TokenGenerator implements JavaDelegate {
    protected static final String EXPRESSION_ACCESS_TOKEN_URL = "accessTokenURL";
    protected static final String EXPRESSION_CLIENT_ID = "clientId";
    protected static final String EXPRESSION_CLIENT_SECRET = "clientSecret";
    protected static final String EXPRESSION_SCOPE = "scope";
    protected static final String EXPRESSION_ACCESS_TOKEN = "accessToken";
    protected static final Logger logger = LoggerFactory.getLogger(OAuth20TokenGenerator.class);

    public void execute(DelegateExecution execution) throws Exception {

        Expression accessTokenURLExpression = DelegateHelper.getFieldExpression(execution, EXPRESSION_ACCESS_TOKEN_URL);
        Expression clientIdExpression = DelegateHelper.getFieldExpression(execution, EXPRESSION_CLIENT_ID);
        Expression clientSecretExpression = DelegateHelper.getFieldExpression(execution, EXPRESSION_CLIENT_SECRET);
        Expression scopeExpression = DelegateHelper.getFieldExpression(execution, EXPRESSION_SCOPE);
        Expression accessTokenExpression = DelegateHelper.getFieldExpression(execution, EXPRESSION_ACCESS_TOKEN);

        // Values pulled from process variables
        String accessTokenURL = (String) getExpressionValue(execution, accessTokenURLExpression);
        String clientId = (String) getExpressionValue(execution, clientIdExpression);
        String clientSecret = (String) getExpressionValue(execution, clientSecretExpression);
        String scope = (String) getExpressionValue(execution, scopeExpression); // optional, but often required
        String accessTokenVariable = (String) getExpressionValue(execution, accessTokenExpression);

//        String tokenUrl = "https://auth.iam.experience.hyland.com/idp/connect/token";

        String maskedClientID = clientId.substring(0,clientId.length() - 6).replaceAll("[a-zA-Z0-9]", "*") + clientId.substring(clientId.length()-5, clientId.length());
        String maskedClientSecret = clientSecret.substring(0,clientSecret.length() - 6).replaceAll("[a-zA-Z0-9]", "*") + clientSecret.substring(clientSecret.length()-5, clientSecret.length());

        System.out.println("======= API Call - Sending Data =========");
        System.out.println("Access TokenURL:" + accessTokenURL);
        System.out.println("clientId:" + maskedClientID);
        System.out.println("clientSecret:" + maskedClientSecret);
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
            HttpPost post = new HttpPost(accessTokenURL);
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
                String accessTokenValue = json.get("access_token").asText();

                System.out.println("TOKEN >>> " + accessTokenValue);

                // Save token into process variable
                execution.setVariable(accessTokenVariable, accessTokenValue);
            }
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
}