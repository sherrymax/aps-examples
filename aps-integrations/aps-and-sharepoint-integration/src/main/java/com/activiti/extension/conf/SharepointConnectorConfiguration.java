package com.activiti.extension.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/*
 * A configuration class which will help get the custom packages scanned
 * 
 */

@Configuration
@PropertySource(value = "classpath:sharepoint-connector.properties", ignoreResourceNotFound = true)
public class SharepointConnectorConfiguration {

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}