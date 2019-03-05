package io.esoma.khr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

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

import io.esoma.khr.dao.AlbumDao;
import io.esoma.khr.dao.KoalibeeDao;
import io.esoma.khr.model.Koalibee;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class KoalibeeServiceTest {

	private static List<String> emailList;

	private KoalibeeService koalibeeService;

	@Mock
	private AuthService authService;

	@Mock
	private KoalibeeDao koalibeeDao;
	@Mock
	private AlbumDao albumDao;

	{
		this.koalibeeService = new KoalibeeService();

		// Initialize mock objects.
		MockitoAnnotations.initMocks(KoalibeeServiceTest.class);
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		// Initialize dummy data.
		emailList = new ArrayList<>();
		emailList.add("lol@koalibee.com");
		emailList.add("other.dup@k.com");

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// No content yet.
	}

	@Before
	public void setUp() throws Exception {

		when(this.koalibeeDao.getAllEmails()).thenReturn(emailList);
		when(this.koalibeeDao.addKoalibee(isA(Koalibee.class))).thenReturn(1);
		when(this.authService.authenticate(anyMap())).thenReturn("valid jws");

		this.koalibeeService.setAuthService(this.authService);
		this.koalibeeService.setKoalibeeDao(this.koalibeeDao);
		this.koalibeeService.setAlbumDao(this.albumDao);

	}

	@Test
	public void mockTest() throws Exception {

		when(this.authService.toString()).thenReturn("authServiceTest");
		when(this.koalibeeDao.toString()).thenReturn("koalibeeDaoTest");
		when(this.albumDao.toString()).thenReturn("albumDaoTest");

		assertEquals("authServiceTest", this.authService.toString());
		assertEquals("koalibeeDaoTest", this.koalibeeDao.toString());
		assertEquals("albumDaoTest", this.albumDao.toString());

	}

	@Test
	public void testSetKoalibeeDao() throws Exception {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testSetAlbumDao() throws Exception {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testSetAuthService() throws Exception {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testRegisterBadJSON() throws Exception {

		assertEquals("bad request", this.koalibeeService.register("bad JSON string"));

	}

	@Test
	public void testRegisterBadFN() throws Exception {

		final String source = "{\"firstName\":\"\"}";

		assertEquals("bad first name", this.koalibeeService.register(source));

	}

	@Test
	public void testRegisterBadLN() throws Exception {

		final String source = "{\"firstName\":\"eddy\"}";

		assertEquals("bad last name", this.koalibeeService.register(source));

	}

	@Test
	public void testRegisterBadE() throws Exception {

		final String source = "{\"firstName\":\"eddy\",\"lastName\":\"soma\",\"email\":\"s.com\"}";

		assertEquals("bad email", this.koalibeeService.register(source));

	}

	@Test
	public void testRegisterBadED() throws Exception {

		final String source = "{\"firstName\":\"eddy\",\"lastName\":\"soma\",\"email\":\"other.dup@k.com\",\"password\":\"nnsss123n\"}";

		assertEquals("duplicate email", this.koalibeeService.register(source));

	}

	@Test
	public void testRegisterBadP() throws Exception {

		final String source = "{\"firstName\":\"eddy\",\"lastName\":\"soma\",\"email\":\"anothers@k.com\",\"password\":\"nnn\"}";

		assertEquals("bad password", this.koalibeeService.register(source));

	}

	@Test
	public void testRegisterS() throws Exception {

		final String source = "{\"firstName\":\"eddy\",\"lastName\":\"soma\",\"email\":\"valid@koality.com\",\"password\":\"strongpassword123\"}";

		assertEquals("valid jws", this.koalibeeService.register(source));

	}

	@Test
	public void testLogin() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testGetOne() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testUpdateInformation() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testUpdateCredentials() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testPurchaseAlbum() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testDelete() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testGetInventory() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testGetUnpublished() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

}
