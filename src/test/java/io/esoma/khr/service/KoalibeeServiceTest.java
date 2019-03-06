package io.esoma.khr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
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
import io.esoma.khr.model.Album;
import io.esoma.khr.model.Credentials;
import io.esoma.khr.model.Genre;
import io.esoma.khr.model.Koalibee;
import io.esoma.khr.model.Review;
import io.esoma.khr.model.Track;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class KoalibeeServiceTest {

	private static List<String> emailList;
	private static List<Album> albumList;

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

		albumList = new ArrayList<>();
		albumList.add(new Album(4));
		albumList.add(new Album(5));

		Album fancyAlbum = new Album(99);
		fancyAlbum.setAlbumName("fancy");
		fancyAlbum.setEtaPrice(999);
		fancyAlbum.setArtist("liszt");
		fancyAlbum.setGenre(new Genre(99, "fancy"));
		fancyAlbum.setArtwork("fancy".getBytes());
		fancyAlbum.setArtworkType("PNG");
		fancyAlbum.setArtworkDataUrl("data:image/png;base64,yCNaf=");
		fancyAlbum.setKoalibee(new Koalibee(-777));
		fancyAlbum.setReviewList(new ArrayList<Review>());
		fancyAlbum.setTrackList(new ArrayList<Track>());
		albumList.add(fancyAlbum);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// No content yet.
	}

	@Before
	public void setUp() throws Exception {

		when(this.koalibeeDao.getAllEmails()).thenReturn(emailList);
		when(this.koalibeeDao.getAllPurchasedAlbumsByKoalibeeId(anyInt())).thenReturn(albumList);
		when(this.koalibeeDao.addKoalibee(isA(Koalibee.class))).thenReturn(1);
		when(this.authService.authenticate(anyMap())).thenReturn("valid jws");
		when(this.koalibeeDao.updateKoalibee(isA(Koalibee.class))).thenReturn(true);
		when(this.koalibeeDao.updateCredentials(isA(Koalibee.class))).thenReturn(true);
		when(this.koalibeeDao.updateEtaBalance(isA(Koalibee.class))).thenReturn(true);
		when(this.koalibeeDao.purchaseAlbum(anyInt(), anyInt())).thenReturn(true);

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
	public void testLoginBadJSON() throws Exception {

		final String source = "invalid stuff";

		assertEquals("bad request", this.koalibeeService.login(source));

	}

	@Test
	public void testLoginBadE() throws Exception {

		final String source = "{\"em?ail\":\"john.doe@example.com\",\"password\":\"jdoe123456\"}";

		assertEquals("bad email", this.koalibeeService.login(source));

	}

	@Test
	public void testLoginBadP() throws Exception {

		final String source = "{\"email\":\"john.doe@example.com\"}";

		assertEquals("bad password", this.koalibeeService.login(source));

	}

	@Test
	public void testLoginNE() throws Exception {

		final String nonExistEmail = "email.not.exist@k.com";

		final String source = "{\"email\":\"email.not.exist@k.com\",\"password\":\"jdoe123456\"}";

		when(this.koalibeeDao.getKoalibeeByEmail(nonExistEmail)).thenReturn(null);
		this.koalibeeService.setKoalibeeDao(koalibeeDao);

		assertEquals("null email", this.koalibeeService.login(source));

	}

	@Test
	public void testLoginS() throws Exception {

		final Credentials credentials = new Credentials();
		credentials.setPasswordSalt("yy12");
		credentials.setPasswordHash("random hash");

		final Koalibee koalibee = new Koalibee(7);
		koalibee.setCredentials(credentials);

		final String goodEmail = "good.email@k.com";

		final String source = "{\"email\":\"good.email@k.com\",\"password\":\"jdoe123456\"}";

		when(this.koalibeeDao.getKoalibeeByEmail(goodEmail)).thenReturn(koalibee);
		this.koalibeeService.setKoalibeeDao(koalibeeDao);

		assertEquals("valid jws", this.koalibeeService.login(source));

	}

	@Test
	public void testGetOneN() throws Exception {

		when(this.koalibeeDao.getKoalibeeById(0)).thenReturn(null);
		this.koalibeeService.setKoalibeeDao(koalibeeDao);

		assertNull(this.koalibeeService.getOne(0));

	}

	@Test
	public void testGetOneE() throws Exception {

		final Koalibee koalibee = new Koalibee(1);
		koalibee.setAvatar("bytes".getBytes());
		koalibee.setAvatarType("PNG");
		koalibee.setCredentials(new Credentials(1));
		koalibee.setAlbumList(new ArrayList<Album>());

		when(this.koalibeeDao.getKoalibeeById(1)).thenReturn(koalibee);
		this.koalibeeService.setKoalibeeDao(koalibeeDao);

		Koalibee persistKoalibee = this.koalibeeService.getOne(1);

		assertNotNull(persistKoalibee);

		assertNull(persistKoalibee.getAvatar());

		assertNull(persistKoalibee.getAvatarType());

		assertNull(persistKoalibee.getCredentials());

		assertNull(persistKoalibee.getAlbumList());

	}

	@Test
	public void testUpdateInformationN() throws Exception {

		final String source = "invalid stuff";

		assertFalse(this.koalibeeService.updateInformation(1, source));

	}

	@Test
	public void testUpdateInformationV1() throws Exception {

		final String source = "{\"firstName\":\"\",\"avatarDataUrl\":\"data:??\",\"email\":\"good.email@k.com\",\"password\":\"jdoe123456\"}";

		assertTrue(this.koalibeeService.updateInformation(1, source));

	}

	@Test
	public void testUpdateInformationV2() throws Exception {

		final String source = "{\"firstName\":\"leo\",\"lastName\":\"Hero\",\"avatarDataUrl\":\"data:image/png;base64,8sh237shryyWW=\"}";

		assertTrue(this.koalibeeService.updateInformation(1, source));

	}

	@Test
	public void testUpdateCredentialsBadJSON() throws Exception {

		final String source = "invalid thing";

		assertFalse(this.koalibeeService.updateCredentials(1, source));

	}

	@Test
	public void testUpdateCredentialsDE() throws Exception {

		final String source = "{\"email\":\"lol@koalibee.com\",\"password\":\"123456\"}";

		assertFalse(this.koalibeeService.updateCredentials(1, source));

	}

	@Test
	public void testUpdateCredentialsS1() throws Exception {

		final String source = "{\"password\":\"123456\"}";

		assertTrue(this.koalibeeService.updateCredentials(1, source));

	}

	@Test
	public void testUpdateCredentialsS2() throws Exception {

		final String source = "{\"email\":\"valid@koalibee.com\",\"password\":\"123456\"}";

		assertTrue(this.koalibeeService.updateCredentials(1, source));

	}

	@Test
	public void testPurchaseAlbumBadJSON() throws Exception {

		final String source = "invalid JSON";

		assertFalse(this.koalibeeService.purchaseAlbum(1, source));

	}

	@Test
	public void testPurchaseAlbumNA() throws Exception {

		final String source = "{\"password\":\"123456\"}";

		assertFalse(this.koalibeeService.purchaseAlbum(1, source));

	}

	@Test
	public void testPurchaseAlbumD() throws Exception {

		final String source = "{\"albumId\":\"4\"}";

		assertFalse(this.koalibeeService.purchaseAlbum(1, source));

	}

	@Test
	public void testPurchaseAlbumI() throws Exception {

		final Album album = new Album(3);
		album.setEtaPrice(50);

		final Koalibee koalibee = new Koalibee(1);
		koalibee.setEtaBalance(30);

		when(this.albumDao.getAlbumById(3)).thenReturn(album);
		when(this.koalibeeDao.getKoalibeeById(1)).thenReturn(koalibee);
		this.koalibeeService.setAlbumDao(albumDao);
		this.koalibeeService.setKoalibeeDao(koalibeeDao);

		final String source = "{\"albumId\":\"3\"}";

		assertFalse(this.koalibeeService.purchaseAlbum(1, source));

	}

	@Test
	public void testPurchaseAlbumS() throws Exception {

		final Album album = new Album(8);
		album.setEtaPrice(150);

		final Koalibee koalibee = new Koalibee(8);
		koalibee.setEtaBalance(400);

		when(this.albumDao.getAlbumById(8)).thenReturn(album);
		when(this.koalibeeDao.getKoalibeeById(8)).thenReturn(koalibee);
		this.koalibeeService.setAlbumDao(albumDao);
		this.koalibeeService.setKoalibeeDao(koalibeeDao);

		final String source = "{\"albumId\":\"8\"}";

		assertTrue(this.koalibeeService.purchaseAlbum(8, source));

	}

	@Test
	public void testDelete() throws Exception {

		when(this.koalibeeDao.deleteKoalibee(anyInt())).thenReturn(true);
		this.koalibeeService.setKoalibeeDao(koalibeeDao);

		assertTrue(this.koalibeeService.delete(4));

	}

	@Test
	public void testGetInventoryE() throws Exception {

		when(this.koalibeeDao.getAllPurchasedAlbumsByKoalibeeId(2)).thenReturn(new ArrayList<Album>());
		this.koalibeeService.setKoalibeeDao(koalibeeDao);

		assertTrue(this.koalibeeService.getInventory(2).isEmpty());

	}

	@Test
	public void testGetInventoryO() throws Exception {

		when(this.koalibeeDao.getAllPurchasedAlbumsByKoalibeeId(99)).thenReturn(albumList);
		this.koalibeeService.setKoalibeeDao(koalibeeDao);

		List<Album> albumList = this.koalibeeService.getInventory(99);

		assertFalse(albumList.isEmpty());

		albumList.removeIf(a -> a.getAlbumId() != 99);

		assertEquals("fancy", albumList.get(0).getAlbumName());

		assertEquals(999, albumList.get(0).getEtaPrice());

		assertEquals("data:image/png;base64,yCNaf=", albumList.get(0).getArtworkDataUrl());

		assertNull(albumList.get(0).getArtwork());

		assertNull(albumList.get(0).getArtworkType());

		assertNull(albumList.get(0).getKoalibee());

		assertNull(albumList.get(0).getReviewList());

		assertNull(albumList.get(0).getTrackList());

		albumList.add(new Album(4));
		albumList.add(new Album(5));

	}

	@Test
	public void testGetUnpublishedE() throws Exception {

		when(this.albumDao.getUnpublishedAlbumsByKoalibee(1)).thenReturn(new ArrayList<Album>());
		this.koalibeeService.setAlbumDao(albumDao);

		assertTrue(this.koalibeeService.getUnpublished(1).isEmpty());

	}

	@Test
	public void testGetUnpublishedO() throws Exception {

		final List<Album> albumList = new ArrayList<>();

		final Album album = new Album(70);
		album.setArtworkDataUrl("some url");

		albumList.add(album);

		when(this.albumDao.getUnpublishedAlbumsByKoalibee(7)).thenReturn(albumList);
		this.koalibeeService.setAlbumDao(albumDao);

		assertEquals(1, this.koalibeeService.getUnpublished(7).size());

		assertNull(this.koalibeeService.getUnpublished(7).get(0).getArtworkDataUrl());

	}

}
