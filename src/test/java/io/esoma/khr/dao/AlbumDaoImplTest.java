package io.esoma.khr.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
import io.esoma.khr.model.Genre;
import io.esoma.khr.model.Koalibee;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring-context.xml")
// Run tests in a fixed order to make sure the SQL script is executed before tests.
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AlbumDaoImplTest {

	private static boolean isSet;

	private SessionFactory sessionFactory;
	private AlbumDao albumDao;

	@Autowired
	@Qualifier(value = "h2DBSessionFactory")
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Autowired
	@Qualifier(value = "albumDaoImplBasic")
	public void setAlbumDao(AlbumDao albumDao) {
		this.albumDao = albumDao;
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
			((AlbumDaoImpl) this.albumDao).setSessionFactory(this.sessionFactory);
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

		assertNotNull(((AlbumDaoImpl) this.albumDao).getSessionFactory());

	}

	@Test
	public void testSetSessionFactory() throws Exception {
		// Intentionally left blank.
		// This test automatically passes if autowiring succeeds.
	}

	@Test
	public void testGetAlbumById1() throws Exception {

		Album album = this.albumDao.getAlbumById(1);

		assertNotNull(album);

		assertEquals("Etudes Liszt", album.getAlbumName());

	}

	@Test
	public void testGetAlbumById2() throws Exception {

		Album album = this.albumDao.getAlbumById(5);

		assertNotNull(album);

		assertEquals("Fortissimo", album.getAlbumName());

		assertEquals("data:image/png;base64,SmF2YSwgQVNQLk5FVCwgQysrLCBGb3J0cmFuIGV0Yw==", album.getArtworkDataUrl());

	}

	@Test
	public void testGetAlbumById3() throws Exception {

		Album album = this.albumDao.getAlbumById(6);

		assertNotNull(album);

		assertEquals("The Hazz Man", album.getArtist());

		assertEquals(0, album.getEtaPrice());

	}

	@Test
	public void testAddAlbum1() throws Exception {

		final Genre genre = new Genre(1);

		final Koalibee koalibee = new Koalibee(1);

		final Album album = new Album();
		album.setAlbumName("Test Ts album");
		album.setArtist("Mobeep");
		album.setEtaPrice(0);
		album.setIsPromoted("F");
		album.setIsPublished("F");
		album.setGenre(genre);
		album.setKoalibee(koalibee);

		int id = this.albumDao.addAlbum(album);

		assertEquals(7, id);

		assertEquals("F", this.albumDao.getAlbumById(id).getIsPublished());

	}

	@Test
	public void testAddAlbum2() throws Exception {

		final Genre genre = new Genre(5);

		final Koalibee koalibee = new Koalibee(2);

		final Album album = new Album();
		album.setAlbumName("BMW");
		album.setArtist("violet evergarden");
		album.setEtaPrice(10);
		album.setIsPromoted("F");
		album.setIsPublished("F");
		album.setGenre(genre);
		album.setKoalibee(koalibee);

		int id = this.albumDao.addAlbum(album);

		assertEquals(8, id);

		assertEquals("BMW", this.albumDao.getAlbumById(id).getAlbumName());

		assertEquals("Country", this.albumDao.getAlbumById(id).getGenre().getGenreName());

	}

	@Test
	public void testUpdateAlbum() throws Exception {

		final Album album = new Album(3);
		album.setAlbumName("Actually an awesome name");
		album.setArtist("the GOAT");

		assertTrue(this.albumDao.updateAlbum(album));

	}

	@Test
	public void testUpdateAlbumV() throws Exception {

		assertEquals("Actually an awesome name", this.albumDao.getAlbumById(3).getAlbumName());

		assertEquals("the GOAT", this.albumDao.getAlbumById(3).getArtist());

	}

	@Test
	public void testDeleteAlbum() throws Exception {

		assertNotNull(this.albumDao.getAlbumById(8));

		assertTrue(this.albumDao.deleteAlbum(8));

		assertNull(this.albumDao.getAlbumById(8));

	}

	@Test
	public void testPublishAlbum1() throws Exception {

		final Album album = new Album(2);
		album.setArtwork("my cool art".getBytes());
		album.setArtworkType("JPG");
		album.setEtaPrice(888);

		assertTrue(this.albumDao.publishAlbum(album));

		assertEquals("T", this.albumDao.getAlbumById(2).getIsPublished());

		assertEquals("data:image/jpg;base64,bXkgY29vbCBhcnQ=", this.albumDao.getAlbumById(2).getArtworkDataUrl());

	}

	@Test
	public void testPublishAlbum2() throws Exception {

		final Album album = new Album(1);
		album.setArtwork("Crazy Pianist".getBytes());
		album.setArtworkType("PNG");
		album.setEtaPrice(373);

		assertTrue(this.albumDao.publishAlbum(album));

		assertEquals("T", this.albumDao.getAlbumById(1).getIsPublished());

		assertEquals("data:image/png;base64,Q3JhenkgUGlhbmlzdA==", this.albumDao.getAlbumById(1).getArtworkDataUrl());

		assertEquals(373, this.albumDao.getAlbumById(1).getEtaPrice());

	}

	@Test
	public void testPromoteAlbum1() throws Exception {

		assertTrue(this.albumDao.promoteAlbum(1));

		assertEquals("T", this.albumDao.getAlbumById(1).getIsPromoted());

	}

	@Test
	public void testPromoteAlbum2() throws Exception {

		assertEquals("F", this.albumDao.getAlbumById(5).getIsPromoted());

		assertTrue(this.albumDao.promoteAlbum(5));

		assertEquals("T", this.albumDao.getAlbumById(5).getIsPromoted());

	}

	@Test
	public void testGetAllAlbums() throws Exception {

		assertFalse(this.albumDao.getAllAlbums().isEmpty());

		assertTrue(this.albumDao.getAllAlbums().contains(new Album(1)));

		assertTrue(this.albumDao.getAllAlbums().contains(new Album(2)));

		assertTrue(this.albumDao.getAllAlbums().contains(new Album(4)));

		assertTrue(this.albumDao.getAllAlbums().contains(new Album(6)));

	}

	@Test
	public void testGetUnpublishedAlbumsByKoalibee() throws Exception {

		assertFalse(this.albumDao.getUnpublishedAlbumsByKoalibee(3).isEmpty());

		assertTrue(this.albumDao.getUnpublishedAlbumsByKoalibee(3).contains(new Album(3)));

		assertTrue(this.albumDao.getUnpublishedAlbumsByKoalibee(3).contains(new Album(6)));

	}

	@Test
	public void testGetUnpublishedAlbumsByKoalibeeN() throws Exception {

		assertTrue(this.albumDao.getUnpublishedAlbumsByKoalibee(2).isEmpty());

	}

	@Test
	public void testGetAllPublishedAlbums() throws Exception {

		assertEquals(1, this.albumDao.getAllPublishedAlbums().size());

		assertTrue(this.albumDao.publishAlbum(new Album(2)));

		assertEquals("New Album", this.albumDao.getAllPublishedAlbums().get(1).getAlbumName());

	}

}
