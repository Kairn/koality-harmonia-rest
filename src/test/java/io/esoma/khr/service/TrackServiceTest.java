package io.esoma.khr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
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
import io.esoma.khr.dao.TrackDao;
import io.esoma.khr.model.Album;
import io.esoma.khr.model.Koalibee;
import io.esoma.khr.model.Track;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TrackServiceTest {

	private static List<Album> albumList;
	private static List<Track> trackList;
	private static List<Koalibee> koalibeeList;

	private TrackService trackService;

	@Mock
	private TrackDao trackDao;
	@Mock
	private KoalibeeDao koalibeeDao;
	@Mock
	private AlbumDao albumdao;

	{
		this.trackService = new TrackService();

		// Initialize mock objects.
		MockitoAnnotations.initMocks(KoalibeeServiceTest.class);
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		albumList = new ArrayList<Album>();
		albumList.add(new Album(1));
		albumList.add(new Album(2));

		trackList = new ArrayList<Track>();
		trackList.add(new Track(1));
		trackList.add(new Track(2));
		trackList.add(new Track(3));

		koalibeeList = new ArrayList<Koalibee>();
		koalibeeList.add(new Koalibee(1));
		koalibeeList.add(new Koalibee(2));

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// No content yet.
	}

	@Before
	public void setUp() throws Exception {

		albumList.get(0).setIsPublished("T");
		albumList.get(0).setKoalibee(koalibeeList.get(0));
		albumList.get(1).setIsPublished("F");
		albumList.get(1).setKoalibee(koalibeeList.get(1));

		trackList.get(0).setIsDemo("T");
		trackList.get(0).setAudio("audo".getBytes());
		trackList.get(0).setAudioType("OGG");
		trackList.get(0).setAudioDataUrl("data:audio/ogg;base64,hYsde9Ji==");
		trackList.get(0).setAlbum(albumList.get(0));
		trackList.get(1).setIsDemo("F");
		trackList.get(1).setAudio("io9".getBytes());
		trackList.get(1).setAudioDataUrl("data:audio/ogg;base64,Huh8hjG09==");
		trackList.get(1).setAlbum(albumList.get(0));
		trackList.get(2).setIsDemo("F");
		trackList.get(2).setAudio("cpo".getBytes());
		trackList.get(2).setAudioDataUrl("data:audio/ogg;base64,723hfsah72=");
		trackList.get(2).setAlbum(albumList.get(1));

		when(this.albumdao.getAlbumById(anyInt())).thenReturn(null);
		when(this.trackDao.getTrackById(anyInt())).thenReturn(null);
		when(this.koalibeeDao.getAllPurchasedAlbumsByKoalibeeId(anyInt())).thenReturn(new ArrayList<Album>());
		when(this.koalibeeDao.getAllPurchasedAlbumsByKoalibeeId(1)).thenReturn(albumList);
		when(this.albumdao.getAlbumById(1)).thenReturn(albumList.get(0));
		when(this.albumdao.getAlbumById(2)).thenReturn(albumList.get(1));
		when(this.trackDao.getTrackById(1)).thenReturn(trackList.get(0));
		when(this.trackDao.getTrackById(2)).thenReturn(trackList.get(1));
		when(this.trackDao.getTrackById(3)).thenReturn(trackList.get(2));

		when(this.trackDao.addTrack(isA(Track.class))).thenReturn(4);
		when(this.trackDao.deleteTrack(anyInt())).thenReturn(true);
		when(this.trackDao.getAllTracks()).thenReturn(trackList);
		when(this.trackDao.getAllTracksByAlbum(anyInt())).thenReturn(new ArrayList<Track>());
		when(this.trackDao.getAllTracksByAlbum(7)).thenReturn(trackList);

		this.trackService.setAlbumDao(this.albumdao);
		this.trackService.setKoalibeeDao(this.koalibeeDao);
		this.trackService.setTrackDao(this.trackDao);

	}

	@Test
	public void mockTest() throws Exception {

		when(this.trackDao.toString()).thenReturn("trackDaoTest");
		when(this.koalibeeDao.toString()).thenReturn("koalibeeDaoTest");
		when(this.albumdao.toString()).thenReturn("albumDaoTest");

		assertEquals("trackDaoTest", this.trackDao.toString());
		assertEquals("koalibeeDaoTest", this.koalibeeDao.toString());
		assertEquals("albumDaoTest", this.albumdao.toString());

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
	public void testSetTrackDao() throws Exception {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testGetOneNP() throws Exception {

		assertNull(this.trackService.getOne(1, 3));

	}

	@Test
	public void testGetOneN() throws Exception {

		assertNull(this.trackService.getOne(1, 6));

	}

	@Test
	public void testGetOneT() throws Exception {

		assertNotNull(this.trackService.getOne(0, 1));

	}

	@Test
	public void testGetOneNO() throws Exception {

		assertNull(this.trackService.getOne(2, 2));

	}

	@Test
	public void testGetOneS() throws Exception {

		Track track = this.trackService.getOne(1, 2);

		assertNotNull(track);

		assertNull(track.getAudio());

		assertNotNull(track.getAudioDataUrl());

	}

	@Test
	public void testAddOneBadJSON() throws Exception {

		final String source = "bad json";

		assertEquals(0, this.trackService.addOne(1, 1, source));

	}

	@Test
	public void testAddOneAP() throws Exception {

		final String source = "{\"trackName\":\"my track\",\"composer\":\"Beethoven\",\"trackLength\":120,\"audioDataUrl\":\"data:audio/ogg;base64,Uhe7eU=\",\"isDemo\":\"F\"}";

		assertEquals(0, this.trackService.addOne(1, 1, source));

	}

	@Test
	public void testAddOneN() throws Exception {

		final String source = "{\"trackName\":\"my track\",\"composer\":\"Beethoven\",\"trackLength\":120,\"audioDataUrl\":\"data:audio/ogg;base64,Uhe7eU=\",\"isDemo\":\"F\"}";

		assertEquals(0, this.trackService.addOne(1, 3, source));

	}

	@Test
	public void testAddOneNO() throws Exception {

		final String source = "{\"trackName\":\"my track\",\"composer\":\"Beethoven\",\"trackLength\":120,\"audioDataUrl\":\"data:audio/ogg;base64,c2FmMTIzdXNodTgzdGFjaw==\",\"isDemo\":\"F\"}";

		assertEquals(0, this.trackService.addOne(1, 2, source));

	}

	@Test
	public void testAddOneBadN() throws Exception {

		final String source = "{\"composer\":\"Beethoven\",\"trackLength\":120,\"audioDataUrl\":\"data:audio/ogg;base64,c2FmMTIzdXNodTgzdGFjaw==\",\"isDemo\":\"F\"}";

		assertEquals(0, this.trackService.addOne(2, 2, source));

	}

	@Test
	public void testAddOneBadC() throws Exception {

		final String source = "{\"trackName\":\"my track\",\"trackLength\":120,\"audioDataUrl\":\"data:audio/ogg;base64,c2FmMTIzdXNodTgzdGFjaw==\",\"isDemo\":\"F\"}";

		assertEquals(0, this.trackService.addOne(2, 2, source));

	}

	@Test
	public void testAddOneBadTL() throws Exception {

		final String source = "{\"trackName\":\"my track\",\"composer\":\"Beethoven\",\"audioDataUrl\":\"data:audio/ogg;base64,c2FmMTIzdXNodTgzdGFjaw==\",\"isDemo\":\"F\"}";

		assertEquals(0, this.trackService.addOne(2, 2, source));

	}

	@Test
	public void testAddOneBadA() throws Exception {

		final String source = "{\"trackName\":\"my track\",\"composer\":\"Beethoven\",\"trackLength\":120,\"isDemo\":\"F\"}";

		assertEquals(0, this.trackService.addOne(2, 2, source));

	}

	@Test
	public void testAddOneBadD() throws Exception {

		final String source = "{\"trackName\":\"my track\",\"composer\":\"Beethoven\",\"trackLength\":120,\"audioDataUrl\":\"data:audio/ogg;base64,c2FmMTIzdXNodTgzdGFjaw==\"}";

		assertEquals(0, this.trackService.addOne(2, 2, source));

	}

	@Test
	public void testAddOneS() throws Exception {

		final String source = "{\"trackName\":\"my track\",\"composer\":\"Beethoven\",\"trackLength\":120,\"audioDataUrl\":\"data:audio/ogg;base64,c2FmMTIzdXNodTgzdGFjaw==\",\"isDemo\":\"F\"}";

		assertEquals(4, this.trackService.addOne(2, 2, source));

	}

	@Test
	public void testDeleteSA() throws Exception {

		assertTrue(this.trackService.delete(-777, 23));

	}

	@Test
	public void testDeleteAP() throws Exception {

		assertFalse(this.trackService.delete(1, 1));

	}

	@Test
	public void testDeleteS() throws Exception {

		assertTrue(this.trackService.delete(2, 3));

	}

	@Test
	public void testDeleteN() throws Exception {

		assertFalse(this.trackService.delete(1, 5));

	}

	@Test
	public void testDeleteNO() throws Exception {

		assertFalse(this.trackService.delete(1, 3));

	}

	@Test
	public void testGetAll() throws Exception {

		List<Track> trackList = this.trackService.getAll();

		assertEquals(3, trackList.size());

		assertNull(trackList.get(0).getAudio());

		assertNull(trackList.get(1).getAudioDataUrl());

	}

	@Test
	public void testGetFromAlbumN() throws Exception {

		assertTrue(this.trackService.getFromAlbum(6).isEmpty());

	}

	@Test
	public void testGetFromAlbumS() throws Exception {

		List<Track> trackList = this.trackService.getFromAlbum(7);

		assertNull(trackList.get(2).getAudio());

		assertNull(trackList.get(2).getAlbum());

		assertNull(trackList.get(2).getAudioDataUrl());

	}

}
