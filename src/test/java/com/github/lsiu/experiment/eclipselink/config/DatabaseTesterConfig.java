package com.github.lsiu.experiment.eclipselink.config;

import javax.sql.DataSource;

import org.dbunit.DataSourceDatabaseTester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.lsiu.hkrestaurants.importer.RestaurantDataImporter;

@Configuration
public class DatabaseTesterConfig {

	@Autowired
	private DataSource dataSource;
	
	@Bean
	public DataSourceDatabaseTester databaseTester() {
		return new DataSourceDatabaseTester(dataSource);
	}
	
	@Autowired
	
	@Bean
	public RestaurantDataImporter importer() {
		return new RestaurantDataImporter();
	}
}
