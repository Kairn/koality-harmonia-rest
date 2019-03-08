package io.esoma.khr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
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
import io.esoma.khr.dao.TrackDao;
import io.esoma.khr.model.Album;
import io.esoma.khr.model.Koalibee;
import io.esoma.khr.model.Review;
import io.esoma.khr.model.Track;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AlbumServiceTest {

	private static List<Track> trackList;
	private static List<Album> unpublishedList;
	private static List<Album> fancyList;
	private static List<Koalibee> koalibeeList;

	private AlbumService albumService;

	@Mock
	private AlbumDao albumDao;
	@Mock
	private KoalibeeDao koalibeeDao;
	@Mock
	private TrackDao trackDao;

	{
		this.albumService = new AlbumService();

		// Initialize mock objects.
		MockitoAnnotations.initMocks(KoalibeeServiceTest.class);
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		koalibeeList = new ArrayList<Koalibee>();
		koalibeeList.add(new Koalibee(1));
		koalibeeList.add(new Koalibee(2));
		koalibeeList.add(new Koalibee(3));
		koalibeeList.get(2).setEtaBalance(30);

		unpublishedList = new ArrayList<Album>();
		unpublishedList.add(new Album(2));
		unpublishedList.add(new Album(4));
		unpublishedList.add(new Album(6));

		fancyList = new ArrayList<Album>();
		fancyList.add(new Album(1));
		fancyList.add(new Album(3));
		fancyList.add(new Album(5));

		unpublishedList.get(0).setKoalibee(koalibeeList.get(0));
		unpublishedList.get(0).setIsPublished("T");
		unpublishedList.get(0).setIsPromoted("T");
		unpublishedList.get(1).setKoalibee(koalibeeList.get(1));
		unpublishedList.get(1).setIsPublished("T");
		unpublishedList.get(1).setIsPromoted("F");
		unpublishedList.get(2).setKoalibee(koalibeeList.get(2));
		unpublishedList.get(2).setIsPublished("F");

		trackList = new ArrayList<Track>();
		trackList.add(new Track(1));
		trackList.add(new Track(2));

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// No content yet.
	}

	@Before
	public void setUp() throws Exception {

		fancyList.get(0).setReviewList(new ArrayList<Review>());
		fancyList.get(0).setArtwork("work".getBytes());
		fancyList.get(0).setArtworkDataUrl("someurl");
		fancyList.get(1).setTrackList(new ArrayList<Track>());
		fancyList.get(1).setArtworkType("JPG");
		fancyList.get(2).setArtwork("2".getBytes());
		fancyList.get(2).setArtworkType("BMP");
		fancyList.get(2).setArtworkDataUrl("UUU");

		when(this.albumDao.getAlbumById(anyInt())).thenReturn(null);
		when(this.albumDao.getAlbumById(2)).thenReturn(unpublishedList.get(0));
		when(this.albumDao.getAlbumById(4)).thenReturn(unpublishedList.get(1));
		when(this.albumDao.getAlbumById(6)).thenReturn(unpublishedList.get(2));
		when(this.albumDao.getUnpublishedAlbumsByKoalibee(anyInt())).thenReturn(unpublishedList);
		when(this.albumDao.updateAlbum(isA(Album.class))).thenReturn(true);
		when(this.albumDao.deleteAlbum(anyInt())).thenReturn(true);
		when(this.albumDao.publishAlbum(isA(Album.class))).thenReturn(true);
		when(this.albumDao.promoteAlbum(anyInt())).thenReturn(true);

		when(this.koalibeeDao.getKoalibeeById(anyInt())).thenReturn(null);
		when(this.koalibeeDao.getKoalibeeById(1)).thenReturn(koalibeeList.get(0));
		when(this.koalibeeDao.getKoalibeeById(2)).thenReturn(koalibeeList.get(1));
		when(this.koalibeeDao.getKoalibeeById(3)).thenReturn(koalibeeList.get(2));

		when(this.trackDao.getAllTracksByAlbum(2)).thenReturn(new ArrayList<Track>());
		when(this.trackDao.getAllTracksByAlbum(6)).thenReturn(trackList);

		this.albumService.setAlbumDao(this.albumDao);
		this.albumService.setKoalibeeDao(this.koalibeeDao);
		this.albumService.setTrackDao(this.trackDao);

	}

	@Test
	public void mockTest() throws Exception {

		when(this.albumDao.toString()).thenReturn("albumDaoTest");
		when(this.koalibeeDao.toString()).thenReturn("koalibeeDaoTest");
		when(this.trackDao.toString()).thenReturn("trackDaoTest");

		assertEquals("albumDaoTest", this.albumDao.toString());
		assertEquals("koalibeeDaoTest", this.koalibeeDao.toString());
		assertEquals("trackDaoTest", this.trackDao.toString());

	}

	@Test
	public void testSetKoalibeeDao() throws Exception {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testSetTrackDao() throws Exception {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testSetAlbumDao() throws Exception {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testGetOne1() throws Exception {

		final Album album = new Album(1);
		album.setArtwork("art".getBytes());
		album.setArtworkDataUrl("arturl");
		album.setReviewList(new ArrayList<Review>());

		when(this.albumDao.getAlbumById(1)).thenReturn(album);
		this.albumService.setAlbumDao(albumDao);

		assertEquals("arturl", this.albumService.getOne(1).getArtworkDataUrl());

		assertNull(this.albumService.getOne(1).getArtwork());

		assertNull(this.albumService.getOne(1).getReviewList());

	}

	@Test
	public void testGetOne2() throws Exception {

		final Koalibee koalibee = new Koalibee(1);
		koalibee.setAvatar("ava".getBytes());
		koalibee.setAvatarDataUrl("avaurl");
		koalibee.setAlbumList(new ArrayList<Album>());

		final Album album = new Album(1);
		album.setKoalibee(koalibee);

		when(this.albumDao.getAlbumById(1)).thenReturn(album);
		this.albumService.setAlbumDao(albumDao);

		assertNotNull(this.albumService.getOne(1).getKoalibee());

		assertNull(this.albumService.getOne(1).getKoalibee().getAvatar());

		assertNull(this.albumService.getOne(1).getKoalibee().getAvatarDataUrl());

		assertNull(this.albumService.getOne(1).getKoalibee().getAlbumList());

	}

	@Test
	public void testCreateBadJSON() throws Exception {

		final String source = "bad JSON";

		assertEquals(0, this.albumService.create(1, source));

	}

	@Test
	public void testCreateN() throws Exception {

		when(this.koalibeeDao.getKoalibeeById(0)).thenReturn(null);
		this.albumService.setKoalibeeDao(koalibeeDao);

		final String source = "{\"albumName\":\"sample\",\"artist\":\"unana\",\"genreId\":1}";

		assertEquals(0, this.albumService.create(0, source));

	}

	@Test
	public void testCreateBadAN() throws Exception {

		final String source = "{\"artist\":\"unana\",\"genreId\":1}";

		assertEquals(0, this.albumService.create(1, source));

	}

	@Test
	public void testCreateBadAA() throws Exception {

		final String source = "{\"albumName\":\"sample\",\"genreId\":1}";

		assertEquals(0, this.albumService.create(1, source));

	}

	@Test
	public void testCreateBadAG() throws Exception {

		final String source = "{\"albumName\":\"sample\",\"artist\":\"unana\"}";

		assertEquals(0, this.albumService.create(1, source));

	}

	@Test
	public void testCreateS() throws Exception {

		when(this.koalibeeDao.getKoalibeeById(1)).thenReturn(new Koalibee(1));
		when(this.albumDao.addAlbum(isA(Album.class))).thenReturn(1);
		this.albumService.setKoalibeeDao(koalibeeDao);
		this.albumService.setAlbumDao(albumDao);

		final String source = "{\"albumName\":\"sample\",\"artist\":\"unana\",\"genreId\":1}";

		assertEquals(1, this.albumService.create(1, source));

	}

	@Test
	public void testUpdateBadJSON() throws Exception {

		final String source = "bad json";

		assertFalse(this.albumService.update(1, 1, source));

	}

	@Test
	public void testUpdateN() throws Exception {

		final String source = "{\"albumName\":\"sample\",\"artist\":\"unana\",\"genreId\":1}";

		assertFalse(this.albumService.update(1, 1, source));

	}

	@Test
	public void testUpdateS1() throws Exception {

		final String source = "{\"genreId\":2}";

		assertTrue(this.albumService.update(1, 2, source));

	}

	@Test
	public void testUpdateS2() throws Exception {

		final String source = "{\"albumName\":\"big bang\",\"artist\":\"nancy\",\"genreId\":4}";

		assertTrue(this.albumService.update(2, 6, source));

	}

	@Test
	public void testDeleteA() throws Exception {

		assertTrue(this.albumService.delete(-777, 70));

	}

	@Test
	public void testDeleteN() throws Exception {

		assertFalse(this.albumService.delete(1, 70));

	}

	@Test
	public void testDeleteK() throws Exception {

		assertTrue(this.albumService.delete(1, 2));

	}

	@Test
	public void testPublishBadJSON() throws Exception {

		final String source = "bad JSON";

		assertFalse(this.albumService.publish(1, 2, source));

	}

	@Test
	public void testPublishNK() throws Exception {

		final String source = "{\"etaPrice\":400,\"artworkDataUrl\":\"data:image/png;base64,3EssR9oP=\"}";

		assertFalse(this.albumService.publish(0, 0, source));

	}

	@Test
	public void testPublishNO() throws Exception {

		final String source = "{\"etaPrice\":400,\"artworkDataUrl\":\"data:image/png;base64,3EssR9oP=\"}";

		assertFalse(this.albumService.publish(1, 6, source));

	}

	@Test
	public void testPublishP() throws Exception {

		final String source = "{\"etaPrice\":400,\"artworkDataUrl\":\"data:image/png;base64,3EssR9oP=\"}";

		assertFalse(this.albumService.publish(2, 4, source));

	}

	@Test
	public void testPublishETL() throws Exception {

		unpublishedList.get(0).setIsPublished("F");

		final String source = "{\"etaPrice\":400,\"artworkDataUrl\":\"data:image/png;base64,3EssR9oP=\"}";

		assertFalse(this.albumService.publish(1, 2, source));

		unpublishedList.get(0).setIsPublished("T");

	}

	@Test
	public void testPublishBadETA() throws Exception {

		final String source = "{\"etaPrice\":\"ff\",\"artworkDataUrl\":\"data:image/png;base64,3EssR9oP=\"}";

		assertFalse(this.albumService.publish(3, 6, source));

	}

	@Test
	public void testPublishBadURL() throws Exception {

		final String source = "{\"etaPrice\":400,\"artworkDataUrl\":\"dse64,3EssR9oP=\"}";

		assertFalse(this.albumService.publish(3, 6, source));

	}

	@Test
	public void testPublishS() throws Exception {

		final String source = "{\"etaPrice\":200,\"artworkDataUrl\":\"data:image/png;base64,dzM0MzIx\"}";

		assertTrue(this.albumService.publish(3, 6, source));

	}

	@Test
	public void testPromoteN() throws Exception {

		assertFalse(this.albumService.promote(7, 2));

	}

	@Test
	public void testPromoteNO() throws Exception {

		assertFalse(this.albumService.promote(3, 2));

	}

	@Test
	public void testPromoteNP() throws Exception {

		assertFalse(this.albumService.promote(3, 6));

	}

	@Test
	public void testPromoteAP() throws Exception {

		assertFalse(this.albumService.promote(1, 2));

	}

	@Test
	public void testPromoteNB() throws Exception {

		assertFalse(this.albumService.promote(2, 4));

	}

	@Test
	public void testPromoteS() throws Exception {

		koalibeeList.get(1).setEtaBalance(3000);

		assertTrue(this.albumService.promote(2, 4));

		koalibeeList.get(1).setEtaBalance(0);

	}

	@Test
	public void testGetAll() throws Exception {

		when(this.albumDao.getAllAlbums()).thenReturn(fancyList);
		this.albumService.setAlbumDao(albumDao);

		assertNull(this.albumService.getAll().get(0).getArtwork());

		assertNull(this.albumService.getAll().get(2).getArtworkDataUrl());

	}

	@Test
	public void testGetUnpublished() throws Exception {

		when(this.albumDao.getUnpublishedAlbumsByKoalibee(anyInt())).thenReturn(fancyList);
		this.albumService.setAlbumDao(albumDao);

		assertNull(this.albumService.getUnpublished(2).get(1).getArtworkDataUrl());

		assertNull(this.albumService.getUnpublished(1).get(0).getReviewList());

	}

	@Test
	public void testGetPublished() throws Exception {

		when(this.albumDao.getAllPublishedAlbums()).thenReturn(fancyList);
		this.albumService.setAlbumDao(albumDao);

		assertNotNull(this.albumService.getPublished().get(2).getArtworkDataUrl());

	}

}
