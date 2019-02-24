package io.esoma.khr.configuration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring-context.xml")
public class HibernateOracleConfigurationTest {

	private SessionFactory oracleDBSessionFactory;

	public SessionFactory getOracleDBSessionFactory() {
		return oracleDBSessionFactory;
	}

	@Autowired
	@Qualifier(value = "oracleDBSessionFactory")
	public void setOracleDBSessionFactory(SessionFactory oracleDBSessionFactory) {
		this.oracleDBSessionFactory = oracleDBSessionFactory;
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// No content yet.
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// No content yet.
	}

	// Test Oracle Database session factory connection.
	@Test
	public void testOracleDBSessionFactory() throws Exception {

		assertTrue(oracleDBSessionFactory.isOpen());

		Session session = oracleDBSessionFactory.openSession();

		assertNotNull(session);

		assertTrue(session.isOpen());

		Transaction tx = session.beginTransaction();

		assertNotNull(tx);

		assertTrue(tx.isActive());

		tx.commit();

		assertFalse(tx.isActive());

		session.close();

		assertFalse(session.isOpen());

	}

}
