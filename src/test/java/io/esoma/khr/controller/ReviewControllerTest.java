package io.esoma.khr.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.http.ResponseEntity;

import io.esoma.khr.model.Review;
import io.esoma.khr.service.AuthService;
import io.esoma.khr.service.KoalibeeServiceTest;
import io.esoma.khr.service.ReviewService;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReviewControllerTest {

	private static List<Review> reviewList;

	private ReviewController reviewController;

	@Mock
	private AuthService authService;
	@Mock
	private ReviewService reviewService;

	{
		this.reviewController = new ReviewController();

		// Initialize mock objects.
		MockitoAnnotations.initMocks(KoalibeeServiceTest.class);
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		reviewList = new ArrayList<Review>();
		reviewList.add(new Review(1));
		reviewList.add(new Review(4));
		reviewList.add(new Review(8));
		reviewList.add(new Review(10));
		reviewList.add(new Review(11));

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// No content yet.
	}

	@Before
	public void setUp() throws Exception {

		when(this.authService.reauthenticate(anyString())).thenReturn(0);
		when(this.authService.reauthenticate("exj")).thenReturn(-1);
		when(this.authService.reauthenticate("adj")).thenReturn(-777);
		when(this.authService.reauthenticate("1j")).thenReturn(1);
		when(this.authService.reauthenticate("2j")).thenReturn(2);

		when(this.reviewService.getOne(anyInt())).thenReturn(null);
		when(this.reviewService.searchOne(anyString())).thenReturn(null);
		when(this.reviewService.getByAlbum(anyInt())).thenReturn(new ArrayList<Review>());
		when(this.reviewService.post(anyInt(), anyInt(), anyString())).thenReturn(0);
		when(this.reviewService.delete(anyInt())).thenReturn(false);
		when(this.reviewService.getAll()).thenReturn(reviewList);

		this.reviewController.setAuthService(this.authService);
		this.reviewController.setReviewService(this.reviewService);

	}

	@Test
	public void testSetAuthService() throws Exception {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testSetReviewService() throws Exception {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testGetReviewNF() throws Exception {

		ResponseEntity<Review> result = this.reviewController.getReview(1);

		assertEquals(404, result.getStatusCodeValue());

		assertEquals(0, result.getBody().getReviewId());

	}

	@Test
	public void testGetReviewS() throws Exception {

		final Review review = new Review(3);

		when(this.reviewService.getOne(3)).thenReturn(review);

		ResponseEntity<Review> result = this.reviewController.getReview(3);

		assertEquals(200, result.getStatusCodeValue());

		assertEquals(3, result.getBody().getReviewId());

	}

	@Test
	public void testFindReviewNF() throws Exception {

		ResponseEntity<Review> result = this.reviewController.findReview("bad data");

		assertEquals(404, result.getStatusCodeValue());

		assertEquals(0, result.getBody().getReviewId());

	}

	@Test
	public void testFindReviewS() throws Exception {

		final Review review = new Review(4);

		when(this.reviewService.searchOne("good data")).thenReturn(review);

		ResponseEntity<Review> result = this.reviewController.findReview("good data");

		assertEquals(200, result.getStatusCodeValue());

		assertEquals(4, result.getBody().getReviewId());

	}

	@Test
	public void testPostReviewEx() throws Exception {

		ResponseEntity<String> result = this.reviewController.postReview(1, "data", "exj");

		assertEquals(417, result.getStatusCodeValue());

		assertEquals("authentication token expired", result.getBody());

	}

	@Test
	public void testPostReviewIn() throws Exception {

		ResponseEntity<String> result = this.reviewController.postReview(1, "data", "ivj");

		assertEquals(401, result.getStatusCodeValue());

		assertEquals("not authorized", result.getBody());

	}

	@Test
	public void testPostReviewF() throws Exception {

		ResponseEntity<String> result = this.reviewController.postReview(1, "data", "1j");

		assertEquals(422, result.getStatusCodeValue());

		assertEquals("unable to post the review", result.getBody());

	}

	@Test
	public void testPostReviewS() throws Exception {

		when(this.reviewService.post(1, 1, "good")).thenReturn(1);

		ResponseEntity<String> result = this.reviewController.postReview(1, "good", "1j");

		assertEquals(201, result.getStatusCodeValue());

		assertEquals("new review posted successfully", result.getBody());

	}

	@Test
	public void testDeleteReviewEx() throws Exception {

		ResponseEntity<String> result = this.reviewController.deleteReview(1, "exj");

		assertEquals(417, result.getStatusCodeValue());

		assertEquals("authentication token expired", result.getBody());

	}

	@Test
	public void testDeleteReviewUA() throws Exception {

		ResponseEntity<String> result = this.reviewController.deleteReview(1, "2j");

		assertEquals(401, result.getStatusCodeValue());

		assertEquals("not authorized", result.getBody());

	}

	@Test
	public void testDeleteReviewF() throws Exception {

		ResponseEntity<String> result = this.reviewController.deleteReview(1, "adj");

		assertEquals(404, result.getStatusCodeValue());

		assertEquals("unable to find or delete the review", result.getBody());

	}

	@Test
	public void testDeleteReviewS() throws Exception {

		when(this.reviewService.delete(2)).thenReturn(true);

		ResponseEntity<String> result = this.reviewController.deleteReview(2, "adj");

		assertEquals(200, result.getStatusCodeValue());

		assertEquals("review has been successfully deleted", result.getBody());

	}

	@Test
	public void testListAllReviewsEx() throws Exception {

		ResponseEntity<List<Review>> result = this.reviewController.listAllReviews("exj");

		assertEquals(417, result.getStatusCodeValue());

		assertEquals(0, result.getBody().size());

	}

	@Test
	public void testListAllReviewsUA() throws Exception {

		ResponseEntity<List<Review>> result = this.reviewController.listAllReviews("1j");

		assertEquals(401, result.getStatusCodeValue());

		assertEquals(0, result.getBody().size());

	}

	@Test
	public void testListAllReviewsS() throws Exception {

		ResponseEntity<List<Review>> result = this.reviewController.listAllReviews("adj");

		assertEquals(200, result.getStatusCodeValue());

		assertEquals(5, result.getBody().size());

	}

	@Test
	public void testGetReviewsOfAlbumN() throws Exception {

		ResponseEntity<List<Review>> result = this.reviewController.getReviewsOfAlbum(3);

		assertEquals(200, result.getStatusCodeValue());

		assertEquals(0, result.getBody().size());

	}

	@Test
	public void testGetReviewsOfAlbumS() throws Exception {

		when(this.reviewService.getByAlbum(3)).thenReturn(reviewList);

		ResponseEntity<List<Review>> result = this.reviewController.getReviewsOfAlbum(3);

		assertEquals(200, result.getStatusCodeValue());

		assertEquals(5, result.getBody().size());

	}

}
