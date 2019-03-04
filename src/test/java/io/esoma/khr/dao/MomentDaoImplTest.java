package io.esoma.khr.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
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

import io.esoma.khr.model.Koalibee;
import io.esoma.khr.model.Moment;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring-context.xml")
// Run tests in a fixed order to make sure the SQL script is executed before tests.
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MomentDaoImplTest {

	private static boolean isSet;

	private SessionFactory sessionFactory;
	private MomentDao momentDao;

	@Autowired
	@Qualifier(value = "h2DBSessionFactory")
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Autowired
	@Qualifier(value = "momentDaoImplBasic")
	public void setMomentDao(MomentDao momentDao) {
		this.momentDao = momentDao;
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
			((MomentDaoImpl) this.momentDao).setSessionFactory(this.sessionFactory);
			isSet = true;
		}
	}

	// Run Test SQL script.
	@Test
	@Sql(scripts = "/kh-h2.sql", config = @SqlConfig(transactionManager = "h2DBHibernateTransactionManager"))
	public void executeSql() {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testGetSessionFactory() throws Exception {

		assertNotNull(((MomentDaoImpl) this.momentDao).getSessionFactory());

	}

	@Test
	public void testSetSessionFactory() throws Exception {
		// Intentionally left blank.
		// This test automatically passes if autowiring succeeds.
		assertTrue(true);
	}

	@Test
	public void testGetMomentById1() throws Exception {

		Moment moment = this.momentDao.getMomentById(1);

		assertNotNull(moment);

		assertEquals("Jesus!", moment.getPostComment());

	}

	@Test
	public void testGetMomentById2() throws Exception {

		Moment moment = this.momentDao.getMomentById(3);

		assertNotNull(moment);

		assertEquals("Julie Seals", moment.getKoalibeeName());

	}

	@Test
	public void testGetMomentById3() throws Exception {

		Moment moment = this.momentDao.getMomentById(4);

		assertNotNull(moment);

		assertEquals("man, JavaScript is garbage", moment.getPostComment());

		assertEquals(LocalDate.parse("2019-02-24"), moment.getPostDate());

	}

	@Test
	public void testGetMomentByKoalibeeAndDate1() throws Exception {

		Moment moment = this.momentDao.getMomentByKoalibeeAndDate(2, LocalDate.parse("2017-06-05"));

		assertNotNull(moment);

		assertEquals("Julie Seals", moment.getKoalibeeName());

	}

	@Test
	public void testGetMomentByKoalibeeAndDate2() throws Exception {

		Moment moment = this.momentDao.getMomentByKoalibeeAndDate(3, LocalDate.parse("2019-01-16"));

		assertNotNull(moment);

		assertEquals("Eddy Soma", moment.getKoalibeeName());

	}

	@Test
	public void testAddMoment1() throws Exception {

		final Koalibee koalibee = new Koalibee(1);

		final Moment moment = new Moment();
		moment.setPostDate(LocalDate.parse("2019-01-23"));
		moment.setPostComment("Test yea");
		moment.setKoalibee(koalibee);

		int id = this.momentDao.addMoment(moment);

		assertEquals(6, id);

		assertEquals("John Smith", this.momentDao.getMomentById(id).getKoalibeeName());

		assertEquals("Test yea", this.momentDao.getMomentById(id).getPostComment());

	}

	@Test
	public void testAddMoment2() throws Exception {

		final Koalibee koalibee = new Koalibee(3);

		final Moment moment = new Moment();
		moment.setPostDate(LocalDate.parse("2017-06-05"));
		moment.setPostComment("god bless this test");
		moment.setKoalibee(koalibee);

		int id = this.momentDao.addMoment(moment);

		assertNotEquals(0, id);

		assertEquals("Eddy Soma", this.momentDao.getMomentById(id).getKoalibeeName());

		assertEquals("2017-06-05", this.momentDao.getMomentById(id).getPostDate().toString());

	}

	@Test
	public void testGetAllMoments() throws Exception {

		List<Moment> momentList = this.momentDao.getAllMoments();

		assertFalse(momentList.isEmpty());

		assertTrue(momentList.contains(new Moment(1)));

		assertTrue(momentList.contains(new Moment(2)));

		assertTrue(momentList.contains(new Moment(3)));

	}

	@Test
	public void testGetAllMomentsO() throws Exception {

		List<Moment> momentList = this.momentDao.getAllMoments();

		assertFalse(momentList.isEmpty());

		assertTrue(momentList.get(0).equals(new Moment(4)));

		assertTrue(momentList.get(1).equals(new Moment(6)));

		assertTrue(momentList.get(2).equals(new Moment(2)));

	}

	@Test
	public void testGetAllMomentsByDateN() throws Exception {

		List<Moment> momentList = this.momentDao.getAllMomentsByDate(LocalDate.parse("2015-06-05"));

		assertEquals(0, momentList.size());

	}

	@Test
	public void testGetAllMomentsByDate2() throws Exception {

		List<Moment> momentList = this.momentDao.getAllMomentsByDate(LocalDate.parse("2017-06-05"));

		assertEquals(2, momentList.size());

		assertEquals("2017-06-05", momentList.get(0).getPostDate().toString());

	}

	@Test
	public void testDeleteMoment() throws Exception {

		int momentCountBefore = this.momentDao.getAllMoments().size();

		assertTrue(this.momentDao.deleteMoment(5));

		assertEquals(1, momentCountBefore - this.momentDao.getAllMoments().size());

	}

}
