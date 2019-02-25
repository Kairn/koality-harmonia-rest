package io.esoma.khr.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.esoma.khr.model.Credentials;
import io.esoma.khr.model.Koalibee;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring-context.xml")
// Run tests in a fixed order to make sure the SQL script is executed before tests.
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class KoalibeeDaoImplTest {

	private static boolean isSet;

	private SessionFactory sessionFactory;
	private KoalibeeDao koalibeeDao;

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

	// Set to Use H2 database session factory before executing the SQL script.
	@Before
	public void setUp() throws Exception {
		// Will only run once.
		if (!isSet) {
			((KoalibeeDaoImpl) this.koalibeeDao).setSessionFactory(this.sessionFactory);
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
	public void testGetKoalibeeByIdA() throws Exception {

		Koalibee koalibee = this.koalibeeDao.getKoalibeeById(2);

		assertNotNull(koalibee);

		assertEquals("data:image/jpg;base64,cmFuZG9tYnl0ZXM=", koalibee.getAvatarDataUrl());

	}

	@Test
	public void testGetKoalibeeByIdN() throws Exception {

		Koalibee koalibee = this.koalibeeDao.getKoalibeeById(15);

		assertNull(koalibee);

	}

	@Test
	public void testGetKoalibeeByIdC() throws Exception {

		Koalibee koalibee = this.koalibeeDao.getKoalibeeById(3);

		assertNotNull(koalibee);

		assertEquals("f5vg", koalibee.getCredentials().getPasswordSalt());

	}

	@Test
	public void testGetKoalibeeByEmail1() throws Exception {

		Koalibee koalibee = this.koalibeeDao.getKoalibeeByEmail("good.user@koalibee.com");

		assertNotNull(koalibee);

		assertEquals("Smith", koalibee.getLastName());

	}

	@Test
	public void testGetKoalibeeByEmail2() throws Exception {

		Koalibee koalibee = this.koalibeeDao.getKoalibeeByEmail("esoma.aws@jenkins.com");

		assertNotNull(koalibee);

		assertNull(koalibee.getAvatarDataUrl());

		assertEquals(50, koalibee.getEtaBalance());

	}

	@Test
	public void testGetKoalibeeByEmailA() throws Exception {

		Koalibee koalibee = this.koalibeeDao.getKoalibeeByEmail("revature@java.com");

		assertNotNull(koalibee);

		assertEquals("data:image/png;base64,bXlzYW1wbGVBVkFUQVI=", koalibee.getAvatarDataUrl());

	}

	@Test
	public void testGetKoalibeeByEmailN() throws Exception {

		Koalibee koalibee = this.koalibeeDao.getKoalibeeByEmail("no.good@koalibee.com");

		assertNull(koalibee);

	}

	@Test
	public void testAddKoalibee() throws Exception {

		final Credentials credentials = new Credentials();
		credentials.setEmail("revature@java.com");
		credentials.setPasswordHash("FD3A64C5C483C83F73937F53C7C4B67606DBE0E6BC606E560A9ECACB7854776D");
		credentials.setPasswordSalt("jkll");

		final Koalibee koalibee = new Koalibee();
		koalibee.setFirstName("Hana");
		koalibee.setLastName("Kairn");
		koalibee.setEmail("revature@java.com");
		koalibee.setEtaBalance(0);
		koalibee.setAvatar("mysampleAVATAR".getBytes());
		koalibee.setAvatarType("PNG");
		koalibee.setCredentials(credentials);

		assertNotEquals(0, this.koalibeeDao.addKoalibee(koalibee));

	}

	@Test
	public void testAddKoalibeeV() throws Exception {

		final Credentials credentials = new Credentials();
		credentials.setEmail("hjk@oracle.com");
		credentials.setPasswordHash("7EBE02BC40A6DA6C01A9240A65C115790CAA5AD87E672B3B65F127B5C3B4014A");
		credentials.setPasswordSalt("4ee5");

		final Koalibee koalibee = new Koalibee();
		koalibee.setFirstName("Ida");
		koalibee.setLastName("Philips");
		koalibee.setEmail("hjk@oracle.com");
		koalibee.setEtaBalance(20);
		koalibee.setCredentials(credentials);

		assertNotEquals(0, this.koalibeeDao.addKoalibee(koalibee));

		assertEquals(20, this.koalibeeDao.getKoalibeeByEmail("hjk@oracle.com").getEtaBalance());

	}

	@Test
	public void testUpdateKoalibee() throws Exception {

		final Koalibee koalibee = new Koalibee(1);
		koalibee.setLastName("Jones");

		assertTrue(this.koalibeeDao.updateKoalibee(koalibee));

		assertEquals("Jones", this.koalibeeDao.getKoalibeeById(1).getLastName());

	}

	@Test
	public void testUpdateKoalibeeR() throws Exception {

		final Koalibee koalibee = new Koalibee(1);
		koalibee.setLastName("Smith");

		assertTrue(this.koalibeeDao.updateKoalibee(koalibee));

		assertEquals("Smith", this.koalibeeDao.getKoalibeeById(1).getLastName());

	}

	@Test
	public void testUpdateCredentials() throws Exception {

		final Credentials credentials = new Credentials();
		credentials.setPasswordSalt("NMuh");
		credentials.setPasswordHash("D11CEA1F106D386969D9EC6095423D3D4EC62F0CB5C6990A3C4D86E6918A9FA7");

		final Koalibee koalibee = new Koalibee(2);
		koalibee.setCredentials(credentials);

		assertTrue(this.koalibeeDao.updateCredentials(koalibee));

		assertEquals("NMuh", this.koalibeeDao.getKoalibeeById(2).getCredentials().getPasswordSalt());

	}

	@Test
	public void testUpdateCredentialsE() throws Exception {

		final Credentials credentials = new Credentials();
		credentials.setEmail("my.new@email.com");

		final Koalibee koalibee = new Koalibee(2);
		koalibee.setCredentials(credentials);

		assertTrue(this.koalibeeDao.updateCredentials(koalibee));

		assertEquals("my.new@email.com", this.koalibeeDao.getKoalibeeById(2).getCredentials().getEmail());

		assertEquals("my.new@email.com", this.koalibeeDao.getKoalibeeById(2).getEmail());

	}

	@Test
	public void testUpdateEtaBalance() throws Exception {

		final Koalibee koalibee = new Koalibee(3);
		koalibee.setEtaBalance(90);

		assertTrue(this.koalibeeDao.updateEtaBalance(koalibee));

		assertEquals(90, this.koalibeeDao.getKoalibeeById(3).getEtaBalance());

	}

	@Test
	public void testDeleteKoalibee() throws Exception {

		assertTrue(this.koalibeeDao.deleteKoalibee(5));

		assertNull(this.koalibeeDao.getKoalibeeById(5));

	}

	@Test
	public void testGetAllEmails() throws Exception {

		List<String> emailList = this.koalibeeDao.getAllEmails();

		assertFalse(emailList.isEmpty());

		assertTrue(emailList.contains("esoma.aws@jenkins.com"));

		assertTrue(emailList.contains("good.user@koalibee.com"));

	}

	@Test
	public void testGetAllKoalibees() throws Exception {

		List<Koalibee> koalibeeList = this.koalibeeDao.getAllKoalibees();

		assertTrue(koalibeeList.contains(new Koalibee(1)));

		assertTrue(koalibeeList.contains(new Koalibee(2)));

		assertTrue(koalibeeList.contains(new Koalibee(3)));

	}

}
