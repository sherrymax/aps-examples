package com.example.aps;

import com.example.aps.model.*;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);
	private String processDefinitionName;
	private String processDefinitionID;
	private String processInstancesUrl;
	private String processDefinitionsUrl;
	private APSInstanceVariable[] processInitialValues;
	private RestTemplate restTemplate;

	@Autowired
	private Environment env;

	public static void main(String args[]) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
		return args -> {
			this.setRestTemplate(restTemplate);
			this.getProcessDefinitionID();
			this.createProcessInstance();
		};
	}

	private void createProcessInstance() {
		this.getProcessInstancesUrl();
		this.getprocessInitialValues();
		RestTemplate restTemplate = this.getRestTemplate();

		APSInstanceSearch apsInstanceSearch = new APSInstanceSearch(this.processDefinitionID,
				this.processInitialValues);
		// APSInstanceSearch apsInstanceSearch = new
		// APSInstanceSearch(this.processDefinitionID);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", this.getJSONAcceptType());
		headers.set("Authorization", this.getAuthToken());

		HttpEntity<APSInstanceSearch> requestEntity = new HttpEntity<>(apsInstanceSearch, headers);

		try {
			APSInstance myResponse = restTemplate.postForObject(this.processInstancesUrl, requestEntity,
					APSInstance.class);
			System.out.println("Created Instance ID : " + myResponse.getId());
		} catch (HttpServerErrorException errorException) {
			System.out.println(">>>ERROR<<< " + errorException.getResponseBodyAsString());
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			System.out.println("^^^ ERROR ^^^ " + ((RestClientResponseException) ex).getResponseBodyAsString());
		}
	}

	private void getProcessDefinitionID() {
		this.setProcessDefinitionName();
		RestTemplate restTemplate = this.getRestTemplate();
		this.getprocessDefinitionsUrl();

		HttpHeaders processDefinitionHeaders = new HttpHeaders();
		processDefinitionHeaders.set("Accept", this.getJSONAcceptType());
		processDefinitionHeaders.set("Authorization", this.getAuthToken());

		Map<String, String> params = new HashMap<String, String>();

		HttpEntity processDefinitionRequestHTTPEntity = new HttpEntity<>(processDefinitionHeaders);

		try {

			ResponseEntity<APSResponse> myResponse = restTemplate.exchange(this.processDefinitionsUrl, HttpMethod.GET,
					processDefinitionRequestHTTPEntity, APSResponse.class, params);

			if (myResponse.getStatusCodeValue() == 200) {

				for (int i = 0; i < myResponse.getBody().getData().length; i++) {
					if (myResponse.getBody().getData()[i].getName().equals(this.getProcessDefinitionName())) {
						this.processDefinitionID = myResponse.getBody().getData()[i].getId();
						System.out.println("Process Definition : Name : " + this.getProcessDefinitionName() + " : ID : "
								+ this.processDefinitionID);
					}
				}
			}

		} catch (HttpServerErrorException errorException) {
			System.out.println(">>>ERROR<<< " + errorException.getResponseBodyAsString());
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			System.out.println("^^^ ERROR ^^^ " + ((RestClientResponseException) ex).getResponseBodyAsString());
		}

	}

	private void setProcessDefinitionName() {
		this.processDefinitionName = env.getProperty("aps.processDefinitionName");
	}

	private String getProcessDefinitionName() {
		return this.processDefinitionName;
	}

	private void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	private RestTemplate getRestTemplate() {
		return this.restTemplate;
	}

	private void getProcessInstancesUrl() {
		this.processInstancesUrl = env.getProperty("aps.processInstancesUrl");
	}

	private void getprocessDefinitionsUrl() {
		this.processDefinitionsUrl = env.getProperty("aps.processDefinitionsUrl");
	}

	private void getprocessInitialValues() {
		String processInitialValuesJSON = env.getProperty("aps.processInitialValues");
		ObjectMapper mapper = new ObjectMapper();

		try {
			APSInstanceVariable[] participantJsonList = mapper.readValue(processInitialValuesJSON,
					new TypeReference<APSInstanceVariable[]>() {
					});
			this.processInitialValues = participantJsonList;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String getUserName() {
		return env.getProperty("aps.username");
	}

	private String getPassword() {
		return env.getProperty("aps.password");
	}

	private String getAuthToken() {
		String credentials = this.getUserName() + ":" + this.getPassword();
		return "Basic " + java.util.Base64.getEncoder().encodeToString((credentials).getBytes());
	}

	private String getJSONAcceptType() {
		return "application/json";
	}

}