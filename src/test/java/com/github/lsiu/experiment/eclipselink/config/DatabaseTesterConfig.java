package com.github.lsiu.experiment.eclipselink.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.lsiu.hkrestaurants.importer.RestaurantDataImporter;

@Configuration
public class DatabaseTesterConfig {
	
	@Bean
	public RestaurantDataImporter importer() {
		return new RestaurantDataImporter();
	}
}
