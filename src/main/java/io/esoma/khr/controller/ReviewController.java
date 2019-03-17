package io.esoma.khr.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.esoma.khr.model.Review;
import io.esoma.khr.service.AuthService;
import io.esoma.khr.service.ReviewService;

/**
 * 
 * The controller class that handles HTTP requests concerning review
 * functionalities and interactions.
 * 
 * @author Eddy Soma
 *
 */
@RestController(value = "reviewController")
@RequestMapping(path = "/review")
public class ReviewController {

	private AuthService authService;
	private ReviewService reviewService;

	@Autowired
	@Qualifier(value = "authService")
	public void setAuthService(AuthService authService) {
		this.authService = authService;
	}

	@Autowired
	@Qualifier(value = "reviewService")
	public void setReviewService(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	/**
	 * 
	 * Responds to a HTTP request of retrieving a review by its ID. It has public
	 * access.
	 * 
	 * @param reviewId the ID of the review.
	 * @return the review object. A blank object will be returned if the ID does not
	 *         exist.
	 */
	@GetMapping(path = "/get/{reviewId}")
	public ResponseEntity<Review> getReview(@Validated @PathVariable int reviewId) {

		Review result = this.reviewService.getOne(reviewId);

		if (result == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Review());
		} else {
			return ResponseEntity.ok(result);
		}

	}

	/**
	 * 
	 * Responds to a HTTP request of searching for a specific review by its poster
	 * and date. It has public access.
	 * 
	 * @param reviewData the JSON string containing the poster's ID and date.
	 * @return the found review object. A blank object is returned if the search
	 *         yields no result.
	 */
	@PostMapping(path = "/find")
	public ResponseEntity<Review> findReview(@Validated @RequestBody String reviewData) {

		Review result = this.reviewService.searchOne(reviewData);

		if (result == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Review());
		} else {
			return ResponseEntity.ok(result);
		}

	}

	/**
	 * 
	 * Responds to a HTTP request that attempts to post a review on an album. The
	 * koalibee must own the album in order to post a review.
	 * 
	 * @param albumId    the ID of the album.
	 * @param reviewData the JSON string containing the review information.
	 * @param jws        the signed authentication token.
	 * @return a general message indicating success or failure.
	 */
	@PostMapping(path = "/post/{albumId}")
	public ResponseEntity<String> postReview(@Validated @PathVariable int albumId,
			@Validated @RequestBody String reviewData, @Validated @RequestHeader(name = "Auth-Token") String jws) {

		HttpStatus status;

		String result = "";

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		if (authId == -1) {
			status = HttpStatus.EXPECTATION_FAILED;
			result = ExceptionController.AUTH_TOKEN_EXPIRED;
		} else if (authId > 0) {
			if (this.reviewService.post(authId, albumId, reviewData) > 0) {
				status = HttpStatus.CREATED;
				result = "new review posted successfully";
			} else {
				status = HttpStatus.UNPROCESSABLE_ENTITY;
				result = "unable to post the review";
			}
		} else {
			status = HttpStatus.UNAUTHORIZED;
			result = ExceptionController.UNAUTHORIZED;
		}

		return ResponseEntity.status(status).body(result);

	}

	/**
	 * 
	 * Responds to a HTTP request that attempts to delete a posted review from the
	 * database. An authentication token from a system administrator is required.
	 * 
	 * @param reviewId the ID of the review.
	 * @param jws      the signed authentication token.
	 * @return a general message indicating success or failure.
	 */
	@DeleteMapping(path = "/delete/{reviewId}")
	public ResponseEntity<String> deleteReview(@Validated @PathVariable int reviewId,
			@Validated @RequestHeader(name = "Auth-Token") String jws) {

		HttpStatus status;

		String result = "";

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		if (authId == -1) {
			status = HttpStatus.EXPECTATION_FAILED;
			result = ExceptionController.AUTH_TOKEN_EXPIRED;
		} else if (authId == -777) {
			if (this.reviewService.delete(reviewId)) {
				status = HttpStatus.OK;
				result = "review has been successfully deleted";
			} else {
				status = HttpStatus.NOT_FOUND;
				result = "unable to find or delete the review";
			}
		} else {
			status = HttpStatus.UNAUTHORIZED;
			result = ExceptionController.UNAUTHORIZED;
		}

		return ResponseEntity.status(status).body(result);

	}

	/**
	 * 
	 * Responds to a HTTP request of retrieving all reviews in the database. An
	 * authentication token from a system administrator is required.
	 * 
	 * @param jws the signed authentication token.
	 * @return the review list. An empty list is returned if the sender does not
	 *         have authorization.
	 */
	@GetMapping(path = "/get/all")
	public ResponseEntity<List<Review>> listAllReviews(@Validated @RequestHeader(name = "Auth-Token") String jws) {

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		if (authId == -1) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ArrayList<Review>());
		} else if (authId == -777) {
			return ResponseEntity.ok(this.reviewService.getAll());
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<Review>());
		}

	}

	/**
	 * 
	 * Responds to a HTTP request of retrieving all reviews posted on an album.
	 * 
	 * @param albumId the ID of the album.
	 * @return the list containing all album reviews.
	 */
	@GetMapping(path = "/get/album/{albumId}")
	public ResponseEntity<List<Review>> getReviewsOfAlbum(@Validated @PathVariable int albumId) {

		return ResponseEntity.ok(this.reviewService.getByAlbum(albumId));

	}

}
