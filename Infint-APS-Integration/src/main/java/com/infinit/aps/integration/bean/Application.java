package com.infinit.aps.integration.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.infinit.aps.integration.model.APSInstance;
import com.infinit.aps.integration.model.APSInstanceSearch;


@SpringBootApplication
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String args[]) {
		SpringApplication.run(Application.class);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
		return args -> {
			
			//TBD - Replace hard coding and read from aps-connector.properties file.
			String url = "http://<host-name>:<port>/activiti-app/api/enterprise/process-instances"; //Replace the hostname and port
			String processDefinitionId = "Process_sid-11FCB075-38FD-4BD4-BC66-A5D8B21BE4F1:15:10851"; //Replace with a valid one from your environment
			
			APSInstanceSearch apsInstanceSearch = new APSInstanceSearch(processDefinitionId);
			
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Accept", "application/json");
	        headers.set("Authorization", "Basic ZGVtbzpkZW1v");

    	    		HttpEntity<APSInstanceSearch> requestEntity = new HttpEntity<>(apsInstanceSearch, headers);
	        
	        try{
	        		System.out.println("<<< BEFORE making the REST call >>>");
	        		APSInstance myResponse = restTemplate.postForObject(url, requestEntity, APSInstance.class);
	        		System.out.println("<<< AFTER making the REST call : Instance ID = "+myResponse.getId()+" >>>");	        		
	        }
	        catch(HttpServerErrorException errorException) {
	            System.out.println(">>>ERROR<<< "+errorException.getResponseBodyAsString());
	        }
	        catch(Exception ex){
	            System.out.println(ex.getMessage());
	            System.out.println("^^^ ERROR ^^^ "+((RestClientResponseException) ex).getResponseBodyAsString());
	        }
			
		};
	}
}