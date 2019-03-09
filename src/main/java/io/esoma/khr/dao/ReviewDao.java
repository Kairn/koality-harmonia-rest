package io.esoma.khr.dao;

import java.util.List;

import io.esoma.khr.model.Review;

/**
 * 
 * The interface used for storing and retrieving data related to reviews posted
 * on published albums.
 * 
 * @author Eddy Soma
 *
 */
public interface ReviewDao {

	/**
	 * 
	 * Gets the review object from the database with the given ID.
	 * 
	 * @param reviewId the ID of the review.
	 * @return the review object, or null if the ID is invalid.
	 */
	Review getReviewById(int reviewId);

	/**
	 * 
	 * Gets the review object posted on a specific album by a specific koalibee.
	 * 
	 * @param koalibeeId the ID of the poster.
	 * @param albumId    the ID of the album.
	 * @return the review object, or null if no matching record if found.
	 */
	Review getReviewByAlbumAndKoalibee(int albumId, int koalibeeId);

	/**
	 * 
	 * Adds a new review record to the database.
	 * 
	 * @param review the review object.
	 * @return the new ID if record is persisted, or 0 if the process fails.
	 */
	int addReview(Review review);

	/**
	 * 
	 * Deletes a review record from the database. It can only be accessed by a
	 * system administrator.
	 * 
	 * @param reviewId the ID of the record to be deleted.
	 * @return true if record is deleted, or false otherwise.
	 */
	boolean deleteReview(int reviewId);

	/**
	 * 
	 * Gets a list of all reviews in the database. It should only be used by a
	 * system administrator to manage reviews.
	 * 
	 * @return the review list.
	 */
	List<Review> getAllReviews();

	/**
	 * 
	 * Gets a list of all reviews posted on a specific album.
	 * 
	 * @param albumId the ID of the album.
	 * @return the review list.
	 */
	List<Review> getAllReviewsByAlbum(int albumId);

	/**
	 * 
	 * Gets a list of all reviews posted by a specific koalibee.
	 * 
	 * @param koalibeeId the ID of the poster.
	 * @return the review list.
	 */
	List<Review> getAllReviewsByKoalibee(int koalibeeId);

}
