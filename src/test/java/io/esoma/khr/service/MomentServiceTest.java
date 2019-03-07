package io.esoma.khr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import io.esoma.khr.dao.KoalibeeDao;
import io.esoma.khr.dao.MomentDao;
import io.esoma.khr.model.Koalibee;
import io.esoma.khr.model.Moment;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MomentServiceTest {

	private static Koalibee koalibee;
	private static List<Moment> momentList;

	private MomentService momentService;

	@Mock
	private KoalibeeDao koalibeeDao;
	@Mock
	private MomentDao momentDao;

	{
		this.momentService = new MomentService();

		// Initialize mock objects.
		MockitoAnnotations.initMocks(KoalibeeServiceTest.class);
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		// Initialize dummy data.
		koalibee = new Koalibee(3);
		koalibee.setEtaBalance(70);

		momentList = new ArrayList<Moment>();

		Moment moment1 = new Moment(1);
		moment1.setKoalibee(new Koalibee(1));

		Moment moment2 = new Moment(2);
		moment2.setKoalibee(new Koalibee(6));

		Moment moment3 = new Moment(3);
		moment3.setKoalibeeName("E Hine");
		moment3.setKoalibee(mock(Koalibee.class));

		momentList.add(moment1);
		momentList.add(moment2);
		momentList.add(moment3);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// No content yet.
	}

	@Before
	public void setUp() throws Exception {

		when(this.koalibeeDao.getKoalibeeById(anyInt())).thenReturn(null);
		when(this.koalibeeDao.getKoalibeeById(7)).thenReturn(koalibee);
		when(this.momentDao.getAllMoments()).thenReturn(momentList);
		when(this.momentDao.getAllMomentsByDate(LocalDate.parse("2015-09-17"))).thenReturn(new ArrayList<Moment>());
		when(this.momentDao.getAllMomentsByDate(LocalDate.parse("2018-11-09"))).thenReturn(momentList);

		this.momentService.setKoalibeeDao(this.koalibeeDao);
		this.momentService.setMomentDao(this.momentDao);

	}

	@Test
	public void mockTest() throws Exception {

		when(this.koalibeeDao.toString()).thenReturn("koalibeeDaoTest");
		when(this.momentDao.toString()).thenReturn("momentDaoTest");

		assertEquals("koalibeeDaoTest", this.koalibeeDao.toString());
		assertEquals("momentDaoTest", this.momentDao.toString());

	}

	@Test
	public void testSetMomentDao() throws Exception {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testSetKoalibeeDao() throws Exception {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testGetOneN() throws Exception {

		when(this.momentDao.getMomentById(111)).thenReturn(null);
		this.momentService.setMomentDao(momentDao);

		assertNull(this.momentService.getOne(111));

	}

	@Test
	public void testGetOne() throws Exception {

		final Moment moment = new Moment(1);
		moment.setKoalibee(new Koalibee(1));
		moment.setPostDate(LocalDate.parse("2015-09-07"));
		moment.setPostComment("yeah comment!");
		moment.setKoalibeeName("Kir Dan");

		when(this.momentDao.getMomentById(1)).thenReturn(moment);
		this.momentService.setMomentDao(momentDao);

		Moment persistMoment = this.momentService.getOne(1);

		assertNotNull(persistMoment);

		assertNull(persistMoment.getKoalibee());

		assertEquals("2015-09-07", persistMoment.getPostDate().toString());

	}

	@Test
	public void testSearchOneBadJSON() throws Exception {

		final String source = "invalid json";

		assertNull(this.momentService.searchOne(source));

	}

	@Test
	public void testSearchOneBadID() throws Exception {

		final String source = "{\"koali\":6,\"postDate\":\"2015-03-05T21:22:59\"}";

		assertNull(this.momentService.searchOne(source));

	}

	@Test
	public void testSearchOneBadD() throws Exception {

		final String source = "{\"koalibeeId\":6,\"post\":\"2015-03-05\"}";

		assertNull(this.momentService.searchOne(source));

	}

	@Test
	public void testSearchOneDF() throws Exception {

		final String source = "{\"koalibeeId\":6,\"postDate\":\"feb201503\"}";

		assertNull(this.momentService.searchOne(source));

	}

	@Test
	public void testSearchOneGN() throws Exception {

		final String source = "{\"koalibeeId\":1,\"postDate\":\"2017-07-07\"}";

		when(this.momentDao.getMomentByKoalibeeAndDate(1, LocalDate.parse("2017-07-07"))).thenReturn(null);
		this.momentService.setMomentDao(momentDao);

		assertNull(this.momentService.searchOne(source));

	}

	@Test
	public void testSearchOneS() throws Exception {

		final Moment moment = new Moment(6);
		moment.setKoalibee(new Koalibee(2));
		moment.setKoalibeeName("Haley Fay");
		moment.setPostComment("MM");

		when(this.momentDao.getMomentByKoalibeeAndDate(6, LocalDate.parse("2016-12-31"))).thenReturn(moment);
		this.momentService.setMomentDao(momentDao);

		final String source = "{\"koalibeeId\":6,\"postDate\":\"2016-12-31\"}";

		Moment persistMoment = this.momentService.searchOne(source);

		assertNotNull(persistMoment);

		assertEquals("MM", persistMoment.getPostComment());

		assertEquals("Haley Fay", persistMoment.getKoalibeeName());

		assertNull(persistMoment.getKoalibee());

	}

	@Test
	public void testPostOneBadJSON() throws Exception {

		final String source = "bad JSON";

		assertEquals(0, this.momentService.postOne(1, source));

	}

	@Test
	public void testPostOneIDN() throws Exception {

		final String source = "{\"koalibeeId\":2,\"postDate\":\"2016-12-31\"}";

		assertEquals(0, this.momentService.postOne(2, source));

	}

	@Test
	public void testPostOneBadC() throws Exception {

		final String source = "{\"koalibeeId\":7,\"Comment\":\"haha\"}";

		assertEquals(0, this.momentService.postOne(7, source));

	}

	@Test
	public void testPostOneDP() throws Exception {

		when(this.momentDao.getMomentByKoalibeeAndDate(7, LocalDate.now())).thenReturn(new Moment(17));
		this.momentService.setMomentDao(momentDao);

		final String source = "{\"koalibeeId\":7,\"postComment\":\"haha\"}";

		assertEquals(0, this.momentService.postOne(7, source));

	}

	@Test
	public void testPostOneF() throws Exception {

		when(this.momentDao.addMoment(new Moment(0))).thenReturn(0);
		this.momentService.setMomentDao(momentDao);

		final String source = "{\"koalibeeId\":7,\"postComment\":\"other comments\"}";

		assertEquals(0, this.momentService.postOne(7, source));

	}

	@Test
	public void testPostOneS() throws Exception {

		when(this.momentDao.addMoment(isA(Moment.class))).thenReturn(77);
		this.momentService.setMomentDao(momentDao);

		final String source = "{\"postComment\":\"good message\"}";

		assertEquals(77, this.momentService.postOne(7, source));

	}

	@Test
	public void testGetAll() throws Exception {

		List<Moment> momentList = this.momentService.getAll();

		assertFalse(momentList.isEmpty());

		assertNull(momentList.get(0).getKoalibee());

	}

	@Test
	public void testGetByDateBD() throws Exception {

		final String source = "bad things";

		assertNull(this.momentService.getByDate(source));

	}

	@Test
	public void testGetByDateDN() throws Exception {

		final String source = "{\"postDate\":\"2015-09-17\"}";

		assertNotNull(this.momentService.getByDate(source));

		assertTrue(this.momentService.getByDate(source).isEmpty());

	}

	@Test
	public void testGetByDateS() throws Exception {

		final String source = "{\"postDate\":\"2018-11-09\"}";

		List<Moment> momentList = this.momentService.getByDate(source);

		assertEquals(3, momentList.size());

		assertNull(momentList.get(2).getKoalibee());

	}

	@Test
	public void testDelete() throws Exception {

		when(this.momentDao.deleteMoment(anyInt())).thenReturn(true);
		this.momentService.setMomentDao(momentDao);

		assertTrue(this.momentService.delete(2));

		assertTrue(this.momentService.delete(5));

	}

}
