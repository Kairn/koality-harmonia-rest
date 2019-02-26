package io.esoma.khr.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
	public void testGetReviewByKoalibeeAndAlbum() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testAddReview() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testDeleteReview() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testGetAllReviews() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testGetAllReviewsByAlbum() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testGetAllReviewsByKoalibee() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

}
