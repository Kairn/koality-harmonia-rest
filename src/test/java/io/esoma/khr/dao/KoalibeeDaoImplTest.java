package io.esoma.khr.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.esoma.khr.model.Koalibee;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring-context.xml")
public class KoalibeeDaoImplTest {

	private SessionFactory sessionFactory;
	private KoalibeeDao koalibeeDao;
	private boolean isSet;

	@Autowired
	@Qualifier(value = "h2DBSessionFactory")
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Autowired
	@Qualifier(value = "koalibeeDaoImplBasic")
	public void setKoalibeeDao(KoalibeeDao koalibeeDao) {
		this.koalibeeDao = koalibeeDao;
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// No content yet.
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// No content yet.
	}

	// Use H2 database session factory.
	@Before
	public void setUp() throws Exception {
		if (!isSet) {
			((KoalibeeDaoImpl) this.koalibeeDao).setSessionFactory(this.sessionFactory);
			executeSql();
			isSet = true;
		}
	}

	// Run Test SQL script.
	@Test
	@Sql(scripts = "/kh-h2.sql", config = @SqlConfig(transactionManager = "h2DBHibernateTransactionManager"))
	public void executeSql() {
		// Intentionally left blank.
	}

	@Test
	public void testGetSessionFactory() throws Exception {

		assertNotNull(((KoalibeeDaoImpl) this.koalibeeDao).getSessionFactory());

	}

	@Test
	public void testSetSessionFactory() throws Exception {
		// Intentionally left blank.
		// This test automatically passes if autowiring succeeds.
	}

	@Test
	public void testGetKoalibeeById1() throws Exception {

		Koalibee koalibee = this.koalibeeDao.getKoalibeeById(1);

		assertNotNull(koalibee);

		assertEquals("John", koalibee.getFirstName());

	}

	@Test
	public void testGetKoalibeeById2() throws Exception {

		Koalibee koalibee = this.koalibeeDao.getKoalibeeById(2);

		assertNotNull(koalibee);

		assertEquals("my.music@koalibee.com", koalibee.getEmail());

	}

	@Test
	public void testGetKoalibeeByIdN() throws Exception {

		Koalibee koalibee = this.koalibeeDao.getKoalibeeById(5);

		assertNull(koalibee);

	}

	@Test
	public void testGetKoalibeeByIdC() throws Exception {

		Koalibee koalibee = this.koalibeeDao.getKoalibeeById(3);

		assertNotNull(koalibee);

		assertEquals("f5vg", koalibee.getCredentials().getPasswordSalt());

	}

	@Test
	public void testGetKoalibeeByEmail() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testAddKoalibee() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testUpdateKoalibee() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testUpdateCredentials() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testUpdateEtaBalance() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testDeleteKoalibee() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testGetAllEmails() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testGetAllKoalibees() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

}
