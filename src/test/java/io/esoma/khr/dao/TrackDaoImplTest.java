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

import io.esoma.khr.model.Album;
import io.esoma.khr.model.Track;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring-context.xml")
// Run tests in a fixed order to make sure the SQL script is executed before tests.
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TrackDaoImplTest {

	private static boolean isSet;

	private SessionFactory sessionFactory;
	private TrackDao trackDao;

	@Autowired
	@Qualifier(value = "h2DBSessionFactory")
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Autowired
	@Qualifier(value = "trackDaoImplBasic")
	public void setTrackDao(TrackDao trackDao) {
		this.trackDao = trackDao;
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
			((TrackDaoImpl) this.trackDao).setSessionFactory(this.sessionFactory);
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

		assertNotNull(((TrackDaoImpl) this.trackDao).getSessionFactory());

	}

	@Test
	public void testSetSessionFactory() throws Exception {
		// Intentionally left blank.
		// This test automatically passes if autowiring succeeds.
		assertTrue(true);
	}

	@Test
	public void testGetTrackById1() throws Exception {

		Track track = this.trackDao.getTrackById(1);

		assertNotNull(track);

		assertEquals("Mazeppa", track.getTrackName());

		assertEquals("Liszt", track.getComposer());

	}

	@Test
	public void testGetTrackById2() throws Exception {

		Track track = this.trackDao.getTrackById(2);

		assertNotNull(track);

		assertEquals("Liszt", track.getComposer());

		assertEquals(300, track.getTrackLength());

	}

	@Test
	public void testGetTrackById3() throws Exception {

		Track track = this.trackDao.getTrackById(10);

		assertNotNull(track);

		assertEquals("F", track.getIsDemo());

		assertEquals("MP3", track.getAudioType());

		assertEquals("data:audio/mp3;base64,cG9pbnQgMjAgaW4gaGV4LCAzMiBpbiBkZWNpbWFsLg==", track.getAudioDataUrl());

	}

	@Test
	public void testGetTrackByIdN() throws Exception {

		Track track = this.trackDao.getTrackById(25);

		assertNull(track);

	}

	@Test
	public void testAddTrack1() throws Exception {

		final Album album = new Album(1);

		final Track track = new Track();
		track.setTrackName("Feux Follets");
		track.setComposer("Liszt");
		track.setTrackLength(200);
		track.setAudio("Some really intense piano sounds".getBytes());
		track.setAudioType("OGG");
		track.setIsDemo("F");
		track.setAlbum(album);

		int id = this.trackDao.addTrack(track);

		assertEquals(13, id);

		assertEquals("Feux Follets", this.trackDao.getTrackById(id).getTrackName());

	}

	@Test
	public void testAddTrack2() throws Exception {

		final Album album = new Album(5);

		final Track track = new Track();
		track.setTrackName("Black Bullet");
		track.setComposer("SAMA");
		track.setTrackLength(270);
		track.setAudio("Great voice by nanjo".getBytes());
		track.setAudioType("OGG");
		track.setIsDemo("T");
		track.setAlbum(album);

		int id = this.trackDao.addTrack(track);

		assertNotEquals(0, id);

		assertEquals("OGG", this.trackDao.getTrackById(id).getAudioType());

		assertEquals("data:audio/ogg;base64,R3JlYXQgdm9pY2UgYnkgbmFuam8=",
				this.trackDao.getTrackById(id).getAudioDataUrl());

	}

	@Test
	public void testDeleteTrack() throws Exception {

		assertNotNull(this.trackDao.getTrackById(7));

		assertTrue(this.trackDao.deleteTrack(7));

		assertNull(this.trackDao.getTrackById(7));

	}

	@Test
	public void testGetAllTracks() throws Exception {

		List<Track> trackList = this.trackDao.getAllTracks();

		assertFalse(trackList.isEmpty());

		assertTrue(trackList.contains(new Track(1)));

		assertTrue(trackList.contains(new Track(5)));

		assertTrue(trackList.contains(new Track(12)));

	}

	@Test
	public void testGetAllTracksByAlbum1() throws Exception {

		List<Track> trackList = this.trackDao.getAllTracksByAlbum(3);

		assertEquals(2, trackList.size());

		assertTrue(trackList.contains(new Track(6)));

	}

	@Test
	public void testGetAllTracksByAlbum2() throws Exception {

		List<Track> trackList = this.trackDao.getAllTracksByAlbum(6);

		assertEquals(1, trackList.size());

		assertEquals("Jasmine", trackList.get(0).getTrackName());

	}

}
