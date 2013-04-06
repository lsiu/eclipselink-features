package com.github.lsiu.experiment.eclipselink;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.dbunit.IDatabaseTester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.lsiu.experiment.eclipselink.config.DatabaseTesterConfig;
import com.github.lsiu.experiment.eclipselink.config.HsqlDataSourceConfig;
import com.github.lsiu.experiment.eclipselink.config.PersistenceJPAConfig;
import com.github.lsiu.experiment.eclipselink.model.Restaurant;
import com.github.lsiu.hkrestaurants.importer.RestaurantDataImporter;

@ContextConfiguration(classes = { PersistenceJPAConfig.class,
		HsqlDataSourceConfig.class, 
		DatabaseTesterConfig.class })
public class TestEclipseLinkEvents extends AbstractTestNGSpringContextTests {

	private static final Logger log = LoggerFactory
			.getLogger(TestEclipseLinkEvents.class);

	private static final SimpleDateFormat datetimeFormatter = new SimpleDateFormat(
			"yyyy-MM-dd_HH-mm-ss-S");

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private IDatabaseTester dbtester;

	@Autowired
	private RestaurantDataImporter importer;

	@BeforeClass
	public void setupDb() throws Exception {
		String testDataDir = "test-data";
		String testFileName = "restaurntData_20130401_233444_700_UTF-8_subset.xml";

		String testFile = "/" + testDataDir + "/" + testFileName;

		log.debug("Prepare database with test file: {}", testFile);

		importer.importData(this.getClass().getResourceAsStream(testFile));
	}

	@Test
	public void testFind() {
		Restaurant r = em.find(Restaurant.class, "3711800024");
		Assert.assertNotNull(r);
		Assert.assertEquals(r.name, "Man Lok");
		Assert.assertEquals(r.getVersion(), (Integer) 1);
	}

	@Transactional
	private Restaurant mergeChanges(Restaurant r) {
		return em.merge(r);
	}

	@Test
	public void testMergeAndVersioning() throws InterruptedException {
		Restaurant r = em.find(Restaurant.class, "3711800024");

		// merge no change, version should not change
		Restaurant merged = mergeChanges(r);
		Assert.assertEquals(merged.getVersion(), (Integer) 1);

		// merge with change, version should increment
		String newName = String.format("Man Lock [name updated %s]",
				datetimeFormatter.format(new Date()));
		merged.name = newName;
		Restaurant merged2 = mergeChanges(merged);

		Assert.assertEquals(merged2.name, newName);
		Assert.assertEquals(merged2.getVersion(), (Integer)2);
		
		Restaurant r2 = em.find(Restaurant.class, "3711800024");
		log.debug("Re-fetch name: {}, version: {}", r2.name, r2.getVersion());
		Assert.assertEquals(r2.getVersion(), (Integer)2);
	}
}
