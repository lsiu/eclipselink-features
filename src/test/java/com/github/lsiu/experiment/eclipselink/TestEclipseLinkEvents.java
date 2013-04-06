package com.github.lsiu.experiment.eclipselink;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
		HsqlDataSourceConfig.class, DatabaseTesterConfig.class })
public class TestEclipseLinkEvents extends AbstractTestNGSpringContextTests {

	private static final Logger log = LoggerFactory
			.getLogger(TestEclipseLinkEvents.class);

	private static final SimpleDateFormat datetimeFormatter = new SimpleDateFormat(
			"yyyy-MM-dd_HH-mm-ss-S");

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private RestaurantDataImporter importer;

	@Resource
	private DataSource ds;
	
	private static final String TEST_LICENSENO = "3711800024";

	@BeforeClass
	public void setupDb() throws Exception {
		log.debug("create RESTAURANT_HIST table");
		String sql = IOUtils.toString(this.getClass().getResourceAsStream(
				"/sql/create-history-table.sql"));
		
		Connection conn = ds.getConnection();
		try {
			conn.setAutoCommit(true);
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
		} finally {
			conn.close();
		}

		String testDataDir = "test-data";
		String testFileName = "restaurntData_20130401_233444_700_UTF-8_subset.xml";
		String testFile = "/" + testDataDir + "/" + testFileName;

		log.debug("Prepare database with test file: {}", testFile);
		importer.importData(this.getClass().getResourceAsStream(testFile));
	}

	@Test
	public void testFind() {
		Restaurant r = em.find(Restaurant.class, TEST_LICENSENO);
		Assert.assertNotNull(r);
		Assert.assertEquals(r.name, "Man Lok");
		Assert.assertEquals(r.getVersion(), (Integer) 1);
	}

	@Transactional
	private Restaurant mergeChanges(Restaurant r) {
		return em.merge(r);
	}

	@Test
	public void testMergeAndVersioning() throws SQLException {
		Restaurant r = em.find(Restaurant.class, TEST_LICENSENO);

		// merge no change, version should not change
		Restaurant merged = mergeChanges(r);
		Assert.assertEquals(merged.getVersion(), (Integer) 1);

		// merge with change, version should increment
		String newName = String.format("Man Lock [name updated %s]",
				datetimeFormatter.format(new Date()));
		merged.name = newName;
		Restaurant merged2 = mergeChanges(merged);

		Assert.assertEquals(merged2.name, newName);
		Assert.assertEquals(merged2.getVersion(), (Integer) 2);

		Restaurant r2 = em.find(Restaurant.class, TEST_LICENSENO);
		log.debug("Re-fetch name: {}, version: {}", r2.name, r2.getVersion());
		Assert.assertEquals(r2.getVersion(), (Integer) 2);
		
		// check history policy
		JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
		Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM RESTAURANT_HIST WHERE LICENSENO = ?",
				Integer.class,
				TEST_LICENSENO);
		Assert.assertEquals(count, (Integer)2);
		
	}
}
