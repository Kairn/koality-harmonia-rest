package io.esoma.khr.configuration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.hibernate.SessionFactory;
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
	private SessionFactory h2DBSessionFactory;

	public SessionFactory getOracleDBSessionFactory() {
		return oracleDBSessionFactory;
	}

	@Autowired
	@Qualifier(value = "oracleDBSessionFactory")
	public void setOracleDBSessionFactory(SessionFactory oracleDBSessionFactory) {
		this.oracleDBSessionFactory = oracleDBSessionFactory;
	}

	public SessionFactory getH2DBSessionFactory() {
		return h2DBSessionFactory;
	}

	@Autowired
	@Qualifier(value = "h2DBSessionFactory")
	public void setH2DBSessionFactory(SessionFactory h2dbSessionFactory) {
		h2DBSessionFactory = h2dbSessionFactory;
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

		assertNotNull(oracleDBSessionFactory.openSession());
		assertTrue(oracleDBSessionFactory.isOpen());
		assertNotNull(oracleDBSessionFactory.getCurrentSession().beginTransaction());
		oracleDBSessionFactory.getCurrentSession().getTransaction().commit();
		oracleDBSessionFactory.getCurrentSession().close();
		oracleDBSessionFactory.close();
		assertTrue(oracleDBSessionFactory.isClosed());

	}

	// Test H2 database session factory connection.
	@Test
	public void testH2DBSessionFactory() throws Exception {

		assertNotNull(h2DBSessionFactory.openSession());
		assertTrue(h2DBSessionFactory.isOpen());
		assertNotNull(h2DBSessionFactory.getCurrentSession().beginTransaction());
		h2DBSessionFactory.getCurrentSession().getTransaction().commit();
		h2DBSessionFactory.getCurrentSession().close();
		h2DBSessionFactory.close();
		assertTrue(h2DBSessionFactory.isClosed());

	}

}
