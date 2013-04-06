package com.github.lsiu.experiment.eclipselink.config;

import java.util.Properties;

import javax.persistence.spi.PersistenceProvider;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.github.lsiu.experiment.eclipselink.model.Restaurant;

@Configuration
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
public class PersistenceJPAConfig {

	private JpaVendorAdapter newJpaVendorAdapter() {
		EclipseLinkJpaVendorAdapter adapter = new EclipseLinkJpaVendorAdapter();
		adapter.setShowSql(false);
//		adapter.setDatabase(Database.HSQL);
//		adapter.setDatabasePlatform(HSQLPlatform.class.getCanonicalName());
		return adapter;
	}

	private static final Properties jpaProperties = new Properties();
	static {
		jpaProperties.put("eclipselink.logging.level", "WARNING");
		jpaProperties.put("eclipselink.weaving", "static");
		jpaProperties.put("eclipselink.ddl-generation", "drop-and-create-tables"); // drop-and-create-tables, create-tables
	}
	
	@Autowired
	private DataSource dataSource;

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(dataSource);
		emf.setPackagesToScan(Restaurant.class.getPackage().getName());
		emf.setJpaDialect(new EclipseLinkJpaDialect());
		emf.setJpaVendorAdapter(newJpaVendorAdapter());
		emf.setJpaProperties(jpaProperties);
		emf.setPersistenceProvider(newPersistenceProvider());
		
		return emf;
	}

	private PersistenceProvider newPersistenceProvider() {
		PersistenceProvider pp = new org.eclipse.persistence.jpa.PersistenceProvider();
		return pp;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactoryBean()
				.getObject());
		return transactionManager;
	}
}
