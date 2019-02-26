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
import io.esoma.khr.model.Koalibee;
import io.esoma.khr.model.Review;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring-context.xml")
// Run tests in a fixed order to make sure the SQL script is executed before tests.
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReviewDaoImplTest {

	private static boolean isSet;

	private SessionFactory sessionFactory;
	private ReviewDao reviewDao;

	@Autowired
	@Qualifier(value = "h2DBSessionFactory")
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Autowired
	@Qualifier(value = "reviewDaoImplBasic")
	public void setReviewDao(ReviewDao reviewDao) {
		this.reviewDao = reviewDao;
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
			((ReviewDaoImpl) this.reviewDao).setSessionFactory(this.sessionFactory);
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

		assertNotNull(((ReviewDaoImpl) this.reviewDao).getSessionFactory());

	}

	@Test
	public void testSetSessionFactory() throws Exception {
		// Intentionally left blank.
		// This test automatically passes if autowiring succeeds.
	}

	@Test
	public void testGetReviewById1() throws Exception {

		Review review = this.reviewDao.getReviewById(1);

		assertNotNull(review);

		assertEquals("Just amazing!", review.getReviewComment());

	}

	@Test
	public void testGetReviewById2() throws Exception {

		Review review = this.reviewDao.getReviewById(4);

		assertNotNull(review);

		assertEquals(10, review.getRating());

		assertEquals("New Album", review.getAlbumName());

		assertEquals("Eddy Soma", review.getKoalibeeName());

	}

	@Test
	public void testGetReviewByIdN() throws Exception {

		Review review = this.reviewDao.getReviewById(100);

		assertNull(review);

	}

	@Test
	public void testGetReviewByAlbumAndKoalibee1() throws Exception {

		Review review = this.reviewDao.getReviewByAlbumAndKoalibee(6, 1);

		assertNotNull(review);

		assertEquals(1, review.getRating());

	}

	@Test
	public void testGetReviewByAlbumAndKoalibee2() throws Exception {

		Review review = this.reviewDao.getReviewByAlbumAndKoalibee(1, 3);

		assertEquals(10, review.getRating());

		assertEquals("WTFOMG Psyche", review.getReviewComment());

		assertNull(review.getAlbumName());

		assertNull(review.getKoalibeeName());

	}

	@Test
	public void testGetReviewByAlbumAndKoalibeeN() throws Exception {

		Review review = this.reviewDao.getReviewByAlbumAndKoalibee(6, 3);

		assertNull(review);

	}

	@Test
	public void testAddReview1() throws Exception {

		final Album album = new Album(4);

		final Koalibee koalibee = new Koalibee(3);

		final Review review = new Review();
		review.setRating(5);
		review.setReviewComment("Test comment");
		review.setAlbum(album);
		review.setKoalibee(koalibee);

		int id = this.reviewDao.addReview(review);

		assertEquals(11, id);

		assertEquals("Test comment", this.reviewDao.getReviewById(id).getReviewComment());

	}

	@Test
	public void testAddReview2() throws Exception {

		final Album album = new Album(2);

		final Koalibee koalibee = new Koalibee(2);

		final Review review = new Review();
		review.setRating(9);
		review.setReviewComment("two to 222");
		review.setAlbum(album);
		review.setKoalibee(koalibee);

		int id = this.reviewDao.addReview(review);

		assertNotEquals(0, id);

		assertEquals(9, this.reviewDao.getReviewById(id).getRating());

		assertEquals("Julie Seals", this.reviewDao.getReviewById(id).getKoalibeeName());

	}

	@Test
	public void testDeleteReview() throws Exception {

		assertNotNull(this.reviewDao.getReviewById(12));

		assertTrue(this.reviewDao.deleteReview(12));

		assertNull(this.reviewDao.getReviewById(12));

	}

	@Test
	public void testGetAllReviews() throws Exception {

		List<Review> reviewList = this.reviewDao.getAllReviews();

		assertFalse(reviewList.isEmpty());

		assertTrue(reviewList.contains(new Review(10)));

		assertTrue(reviewList.contains(new Review(9)));

		assertTrue(reviewList.contains(new Review(8)));

		assertTrue(reviewList.contains(new Review(7)));

	}

	@Test
	public void testGetAllReviewsByAlbum() throws Exception {

		List<Review> reviewList = this.reviewDao.getAllReviewsByAlbum(1);

		assertEquals(2, reviewList.size());

		assertEquals("John Smith", reviewList.get(0).getKoalibeeName());

	}

	@Test
	public void testGetAllReviewsByAlbumN() throws Exception {

		List<Review> reviewList = this.reviewDao.getAllReviewsByAlbum(3);

		assertEquals(0, reviewList.size());

	}

	@Test
	public void testGetAllReviewsByKoalibee() throws Exception {

		List<Review> reviewList = this.reviewDao.getAllReviewsByKoalibee(1);

		assertEquals(4, reviewList.size());

		assertTrue(reviewList.contains(new Review(1)));

		reviewList.removeIf(r -> !r.getAlbum().equals(new Album(1)));

		assertEquals("Just amazing!", reviewList.get(0).getReviewComment());

		assertEquals("Etudes Liszt", reviewList.get(0).getAlbumName());

	}

	@Test
	public void testGetAllReviewsByKoalibeeN() throws Exception {

		List<Review> reviewList = this.reviewDao.getAllReviewsByKoalibee(7);

		assertEquals(0, reviewList.size());

	}

}
