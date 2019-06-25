package com.example.aps.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
//import org.springframework.integration.annotation.InboundChannelAdapter;
//import org.springframework.integration.annotation.Poller;
//import org.springframework.integration.mail.MailReceiver;
//import org.springframework.integration.mail.MailReceivingMessageSource;

/*
 * A configuration class which will help get the custom packages scanned
 * 
 */

@Configuration
@PropertySource(value = "classpath:aps-connector.properties", ignoreResourceNotFound = true)
public class RESTAPSCallsConfiguration {

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}