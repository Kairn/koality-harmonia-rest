package io.esoma.khr.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.DriverManager;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import io.esoma.khr.utility.DatabaseUtility;

public class DatabaseUtilityTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// No content yet.
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// No content yet.
	}

	@Test
	public void testGetOracleDBVariables() {

		assertNotNull(DatabaseUtility.getOracleDBVariables());

		assertEquals(4, DatabaseUtility.getOracleDBVariables().length);

	}

	@Test
	public void testGetOracleDBDriverClassName() {

		assertNotNull(DatabaseUtility.getOracleDBDriverClassName());

		assertEquals("oracle.jdbc.driver.OracleDriver", DatabaseUtility.getOracleDBDriverClassName());

	}

	@Test
	public void testGetOracleDBUrl() {

		assertNotNull(DatabaseUtility.getOracleDBUrl());

		assertTrue(DatabaseUtility.getOracleDBUrl().startsWith("jdbc:oracle"));

		assertTrue(DatabaseUtility.getOracleDBUrl().endsWith("1521:orcl"));

	}

	@Test
	public void testGetOracleDBUsername() {

		assertNotNull(DatabaseUtility.getOracleDBUsername());

	}

	@Test
	public void testGetOracleDBPassword() {

		assertNotNull(DatabaseUtility.getOracleDBPassword());

	}

	@Test
	public void testGetH2DBVariables() {

		assertNotNull(DatabaseUtility.getH2DBVariables());

		assertEquals(4, DatabaseUtility.getH2DBVariables().length);

	}

	@Test
	public void testGetH2DBDriverClassName() {

		assertNotNull(DatabaseUtility.getH2DBDriverClassName());

		assertEquals("org.h2.Driver", DatabaseUtility.getH2DBDriverClassName());

	}

	@Test
	public void testGetH2DBUrl() {

		assertNotNull(DatabaseUtility.getH2DBUrl());

		assertEquals(2, DatabaseUtility.getH2DBUrl().split(";").length);

		assertTrue(DatabaseUtility.getH2DBUrl().startsWith("jdbc:h2"));

		assertTrue(DatabaseUtility.getH2DBUrl().endsWith("DELAY=-1"));

	}

	@Test
	public void testGetH2DBUsername() {

		assertNotNull(DatabaseUtility.getH2DBUsername());

	}

	@Test
	public void testGetH2DBPassword() {

		assertNotNull(DatabaseUtility.getH2DBPassword());

	}

	@Test
	public void testOracleConnection() throws Exception {

		try {
			Class.forName(DatabaseUtility.getOracleDBDriverClassName());
		} catch (Exception e) {
			fail("Unable to load driver");
			return;
		}

		assertNotNull(DriverManager.getConnection(DatabaseUtility.getOracleDBUrl(),
				DatabaseUtility.getOracleDBUsername(), DatabaseUtility.getOracleDBPassword()));

	}

	@Test
	public void testH2Connection() throws Exception {

		try {
			Class.forName(DatabaseUtility.getH2DBDriverClassName());
		} catch (Exception e) {
			fail("Unable to load driver");
			return;
		}

		assertNotNull(DriverManager.getConnection(DatabaseUtility.getH2DBUrl(), DatabaseUtility.getH2DBUsername(),
				DatabaseUtility.getH2DBPassword()));

	}

}
