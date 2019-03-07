package io.esoma.khr.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

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

@RunWith(MockitoJUnitRunner.StrictStubs.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MomentServiceTest {

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
		// No content yet.
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// No content yet.
	}

	@Before
	public void setUp() throws Exception {

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
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testSetKoalibeeDao() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testGetOne() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testSearchOne() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testPostOne() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testGetAll() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testGetByDate() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testDelete() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

}
