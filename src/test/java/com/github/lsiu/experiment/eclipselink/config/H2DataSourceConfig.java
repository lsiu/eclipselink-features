package com.github.lsiu.experiment.eclipselink.config;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class H2DataSourceConfig {

	@Bean
	public DataSource dataSource() {
		 JdbcDataSource ds = new JdbcDataSource();
		 ds.setURL("jdbc:h2:target/test-data/h2testdb");
		 ds.setUser("sa");
		 ds.setPassword("");
		 return ds;
	}
}
