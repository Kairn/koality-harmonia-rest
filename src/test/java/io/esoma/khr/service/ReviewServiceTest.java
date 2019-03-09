package io.esoma.khr.service;

import static org.junit.Assert.assertEquals;
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

import io.esoma.khr.dao.KoalibeeDao;
import io.esoma.khr.dao.ReviewDao;
import io.esoma.khr.model.Album;
import io.esoma.khr.model.Koalibee;
import io.esoma.khr.model.Review;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReviewServiceTest {

	private static List<Review> reviewList;
	private static List<Album> albumList;
	private static List<Koalibee> koalibeeList;

	private ReviewService reviewService;

	@Mock
	private KoalibeeDao koalibeeDao;
	@Mock
	private ReviewDao reviewDao;

	{
		reviewService = new ReviewService();

		// Initialize mock objects.
		MockitoAnnotations.initMocks(KoalibeeServiceTest.class);
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		koalibeeList = new ArrayList<Koalibee>();
		koalibeeList.add(new Koalibee(1));
		koalibeeList.add(new Koalibee(2));

		albumList = new ArrayList<Album>();
		albumList.add(new Album(1));
		albumList.add(new Album(2));

		reviewList = new ArrayList<Review>();
		reviewList.add(new Review(1));
		reviewList.add(new Review(2));
		reviewList.add(new Review(3));

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// No content yet.
	}

	@Before
	public void setUp() throws Exception {

		reviewList.get(0).setRating(9);
		reviewList.get(0).setReviewComment("hahaha");
		reviewList.get(0).setAlbumName("album");
		reviewList.get(0).setKoalibeeName("a koalibee");
		reviewList.get(1).setRating(4);
		reviewList.get(1).setReviewComment("nothing to say");
		reviewList.get(1).setAlbumName("album");
		reviewList.get(1).setKoalibeeName("a koalibee");
		reviewList.get(2).setRating(7);
		reviewList.get(2).setReviewComment("I wanna fly");
		reviewList.get(2).setAlbumName("album");
		reviewList.get(2).setKoalibeeName("a koalibee");

		when(this.reviewDao.getReviewById(anyInt())).thenReturn(null);
		when(this.koalibeeDao.getKoalibeeById(anyInt())).thenReturn(null);
		when(this.reviewDao.getReviewByAlbumAndKoalibee(anyInt(), anyInt())).thenReturn(null);
		when(this.koalibeeDao.getAllPurchasedAlbumsByKoalibeeId(1)).thenReturn(albumList);

		when(this.reviewDao.getReviewById(1)).thenReturn(reviewList.get(0));
		when(this.koalibeeDao.getKoalibeeById(1)).thenReturn(koalibeeList.get(0));

		when(this.koalibeeDao.updateEtaBalance(isA(Koalibee.class))).thenReturn(true);
		when(this.reviewDao.addReview(isA(Review.class))).thenReturn(4);
		when(this.reviewDao.deleteReview(anyInt())).thenReturn(true);

		this.reviewService.setKoalibeeDao(this.koalibeeDao);
		this.reviewService.setReviewDao(this.reviewDao);

	}

	@Test
	public void mockTest() throws Exception {

		when(this.koalibeeDao.toString()).thenReturn("koalibeeDaoTest");
		when(this.reviewDao.toString()).thenReturn("reviewDaoTest");

		assertEquals("koalibeeDaoTest", this.koalibeeDao.toString());
		assertEquals("reviewDaoTest", this.reviewDao.toString());

	}

	@Test
	public void testSetKoalibeeDao() throws Exception {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testSetReviewDao() throws Exception {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testGetOneN() throws Exception {

		assertNull(this.reviewService.getOne(6));

	}

	@Test
	public void testGetOneS() throws Exception {

		Review review = this.reviewService.getOne(1);

		assertNotNull(review);

		assertNull(review.getKoalibee());

		assertNull(review.getAlbum());

		assertNotNull(review.getAlbumName());

		assertNotNull(review.getKoalibeeName());

	}

	@Test
	public void testSearchOneBadJSON() throws Exception {

		final String source = "bad JSON string";

		assertNull(this.reviewService.searchOne(source));

	}

	@Test
	public void testSearchOneBadA() throws Exception {

		final String source = "{\"koalibeeId\":1}";

		assertNull(this.reviewService.searchOne(source));

	}

	@Test
	public void testSearchOneBadK() throws Exception {

		final String source = "{\"albumId\":1}";

		assertNull(this.reviewService.searchOne(source));

	}

	@Test
	public void testSearchOneN() throws Exception {

		final String source = "{\"albumId\":1,\"koalibeeId\":1}";

		assertNull(this.reviewService.searchOne(source));

	}

	@Test
	public void testSearchOneS() throws Exception {

		when(this.reviewDao.getReviewByAlbumAndKoalibee(1, 1)).thenReturn(reviewList.get(0));
		this.reviewService.setReviewDao(reviewDao);

		final String source = "{\"albumId\":1,\"koalibeeId\":1}";

		Review review = this.reviewService.searchOne(source);

		assertNotNull(review);

		assertNull(review.getAlbum());

		assertNotNull(review.getKoalibeeName());

	}

	@Test
	public void testPostBadJSON() throws Exception {

		final String source = "bad bad bad";

		assertEquals(0, this.reviewService.post(1, 1, source));

	}

	@Test
	public void testPostDP() throws Exception {

		when(this.reviewDao.getReviewByAlbumAndKoalibee(1, 1)).thenReturn(reviewList.get(0));
		this.reviewService.setReviewDao(reviewDao);

		final String source = "{\"rating\":8,\"reviewComment\":\"this is a comment\"}";

		assertEquals(0, this.reviewService.post(1, 1, source));

	}

	@Test
	public void testPostNO() throws Exception {

		when(this.koalibeeDao.getAllPurchasedAlbumsByKoalibeeId(2)).thenReturn(new ArrayList<Album>());
		this.reviewService.setKoalibeeDao(koalibeeDao);

		final String source = "{\"rating\":8,\"reviewComment\":\"this is a comment\"}";

		assertEquals(0, this.reviewService.post(2, 1, source));

	}

	@Test
	public void testPostBadR() throws Exception {

		final String source = "{{\"ratasaing\":8,\"reviewComment\":\"this is a comment\"}";

		assertEquals(0, this.reviewService.post(1, 2, source));

	}

	@Test
	public void testPostBadC() throws Exception {

		final String source = "{\"rating\":8,\"Comment\":\"this is a comment\"}";

		assertEquals(0, this.reviewService.post(1, 2, source));

	}

	@Test
	public void testPostS() throws Exception {

		final String source = "{\"rating\":8,\"reviewComment\":\"this is a comment\"}";

		assertEquals(4, this.reviewService.post(1, 2, source));

	}

	@Test
	public void testDelete() throws Exception {

		assertTrue(this.reviewService.delete(3));

	}

	@Test
	public void testGetAll() throws Exception {

		when(this.reviewDao.getAllReviews()).thenReturn(reviewList);
		this.reviewService.setReviewDao(reviewDao);

		List<Review> reviewList = this.reviewService.getAll();

		assertEquals(3, reviewList.size());

		assertNotNull(reviewList.get(0).getAlbumName());

		assertNull(reviewList.get(0).getKoalibee());

	}

	@Test
	public void testGetByAlbum() throws Exception {

		when(this.reviewDao.getAllReviewsByAlbum(anyInt())).thenReturn(reviewList);
		this.reviewService.setReviewDao(reviewDao);

		List<Review> reviewList = this.reviewService.getByAlbum(2);

		assertEquals(3, reviewList.size());

		assertNotNull(reviewList.get(1).getAlbumName());

		assertNull(reviewList.get(2).getKoalibee());

	}

	@Test
	public void testGetByKoalibee() throws Exception {

		when(this.reviewDao.getAllReviewsByKoalibee(anyInt())).thenReturn(reviewList);
		this.reviewService.setReviewDao(reviewDao);

		List<Review> reviewList = this.reviewService.getByKoalibee(2);

		assertEquals(3, reviewList.size());

		assertNotNull(reviewList.get(2).getKoalibeeName());

		assertNull(reviewList.get(1).getAlbum());

	}

}
