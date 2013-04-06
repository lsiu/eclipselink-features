package com.github.lsiu.experiment.eclipselink.config;

import javax.sql.DataSource;

import org.hsqldb.jdbc.JDBCDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HsqlDataSourceConfig {

	@Bean
	public DataSource dataSource() {
		JDBCDataSource ds = new JDBCDataSource();
		// hsqldb.write_delay=false
		// shutdown=true
		ds.setUrl("jdbc:hsqldb:file:target/test-data/testdb;shutdown=true");
		ds.setUser("sa");
		ds.setPassword("");
		return ds;
	}
}
