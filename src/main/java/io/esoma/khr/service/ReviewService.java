package io.esoma.khr.service;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.esoma.khr.dao.KoalibeeDao;
import io.esoma.khr.dao.ReviewDao;
import io.esoma.khr.model.Album;
import io.esoma.khr.model.Koalibee;
import io.esoma.khr.model.Review;

/**
 * 
 * The service class that handles review related functionalities.
 * 
 * @author Eddy Soma
 *
 */
@Service(value = "reviewService")
public class ReviewService {

	private KoalibeeDao koalibeeDao;
	private ReviewDao reviewDao;

	@Autowired
	@Qualifier(value = "koalibeeDaoImplBasic")
	public void setKoalibeeDao(KoalibeeDao koalibeeDao) {
		this.koalibeeDao = koalibeeDao;
	}

	@Autowired
	@Qualifier(value = "reviewDaoImplBasic")
	public void setReviewDao(ReviewDao reviewDao) {
		this.reviewDao = reviewDao;
	}

	/**
	 * 
	 * Gets a review from the database with the given ID. It can be accessed
	 * publicly.
	 * 
	 * @param reviewId the ID of the review.
	 * @return a serializable review object, or null if the ID is invalid.
	 */
	public Review getOne(int reviewId) {

		Review review = this.reviewDao.getReviewById(reviewId);

		// Truncate proxies.
		if (review != null) {
			review.setAlbum(null);
			review.setKoalibee(null);
		}

		return review;

	}

	/**
	 * 
	 * Searches for a specific review posted by a koalibee on a particular album. It
	 * can be accessed publicly.
	 * 
	 * @param reviewData the JSON string containing the search conditions.
	 * @return the found review object, or null if the search yields no result.
	 */
	public Review searchOne(String reviewData) {

		JSONObject jo;

		// Parse the JSON string.
		try {
			jo = new JSONObject(reviewData);
		} catch (Exception e) {
			return null;
		}

		int koalibeeId;
		int albumId;

		// Extract search conditions.
		try {
			koalibeeId = jo.getInt("koalibeeId");
		} catch (Exception e) {
			return null;
		}

		try {
			albumId = jo.getInt("albumId");
		} catch (Exception e) {
			return null;
		}

		Review review = this.reviewDao.getReviewByAlbumAndKoalibee(albumId, koalibeeId);

		// Truncate proxies.
		if (review != null) {
			review.setAlbum(null);
			review.setKoalibee(null);
		}

		return review;

	}

	/**
	 * 
	 * Attempts to post a new review on an album. Each user is only allowed to post
	 * one review on each album. The album has to be public and owned by the poster.
	 * 
	 * @param koalibeeId the ID of the poster.
	 * @param albumId    the ID of the album.
	 * @param reviewData the JSON string containing the review information.
	 * @return a positive integer if the posting succeeds, or 0 if it fails.
	 */
	public int post(int koalibeeId, int albumId, String reviewData) {

		JSONObject jo;

		// Parse the JSON string.
		try {
			jo = new JSONObject(reviewData);
		} catch (Exception e) {
			return 0;
		}

		// Check for double posting.
		if (this.reviewDao.getReviewByAlbumAndKoalibee(albumId, koalibeeId) != null) {
			return 0;
		}

		// Verify ownership.
		List<Album> albumList = this.koalibeeDao.getAllPurchasedAlbumsByKoalibeeId(koalibeeId);
		albumList.removeIf(a -> a.getAlbumId() != albumId);
		if (albumList.isEmpty()) {
			return 0;
		}

		Review review = new Review();

		// Extract review data.
		try {
			int rating = jo.getInt("rating");
			review.setRating(rating);
		} catch (Exception e) {
			return 0;
		}

		try {
			String reviewComment = jo.getString("reviewComment");
			review.setReviewComment(reviewComment);
		} catch (Exception e) {
			return 0;
		}

		review.setAlbum(new Album(albumId));
		review.setKoalibee(new Koalibee(koalibeeId));

		// Award the poster.
		int reviewId = this.reviewDao.addReview(review);
		if (reviewId > 0) {
			Koalibee koalibee = this.koalibeeDao.getKoalibeeById(koalibeeId);
			koalibee.setEtaBalance(koalibee.getEtaBalance() + 10);
			this.koalibeeDao.updateEtaBalance(koalibee);
		}

		return reviewId;

	}

	/**
	 * 
	 * Deletes a review permanently from the database. It can only be accessed by a
	 * system administrator.
	 * 
	 * @return true if the deletion is successful, or false if it fails.
	 */
	public boolean delete(int reviewId) {

		return this.reviewDao.deleteReview(reviewId);

	}

	/**
	 * 
	 * Retrieves all reviews from the database. It can only be accessed by a system
	 * administrator.
	 * 
	 * @return the review list.
	 */
	public List<Review> getAll() {

		List<Review> reviewList = this.reviewDao.getAllReviews();

		for (Review r : reviewList) {
			r.setAlbum(null);
			r.setKoalibee(null);
		}

		return reviewList;

	}

	/**
	 * 
	 * Gets all reviews posted on an album.
	 * 
	 * @param albumId the ID of the album.
	 * @return the review list.
	 */
	public List<Review> getByAlbum(int albumId) {

		List<Review> reviewList = this.reviewDao.getAllReviewsByAlbum(albumId);

		for (Review r : reviewList) {
			r.setAlbum(null);
			r.setKoalibee(null);
		}

		return reviewList;

	}

	/**
	 * 
	 * Gets all reviews posted by a koalibee.
	 * 
	 * @param koalibeeId the ID of the koalibee.
	 * @return the review list.
	 */
	public List<Review> getByKoalibee(int koalibeeId) {

		List<Review> reviewList = this.reviewDao.getAllReviewsByKoalibee(koalibeeId);

		for (Review r : reviewList) {
			r.setAlbum(null);
			r.setKoalibee(null);
		}

		return reviewList;

	}

}
