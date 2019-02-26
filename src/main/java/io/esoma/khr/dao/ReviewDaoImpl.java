package io.esoma.khr.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import io.esoma.khr.model.Review;

/**
 * 
 * The basic implementation of ReviewDao interface using Hibernate 5.
 * 
 * @author Eddy Soma
 *
 */
@Repository(value = "reviewDaoImplBasic")
public class ReviewDaoImpl implements ReviewDao {

	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	// Wires a Oracle Database session factory by default. It can be substituted
	// with a H2 session factory during integration testing.
	@Autowired
	@Qualifier(value = "oracleDBSessionFactory")
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public Review getReviewById(int reviewId) {

		Transaction tx = null;
		Review review = null;

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			review = session.get(Review.class, reviewId);
			// Obtain album and koalibee name data.
			review.setAlbumName(review.getAlbum().getAlbumName());
			review.setKoalibeeName(review.getKoalibee().getFirstName() + " " + review.getKoalibee().getLastName());
			tx.commit();
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			review = null;
		}

		return review;

	}

	@Override
	public Review getReviewByKoalibeeAndAlbum(int koalibeeId, int albumId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int addReview(Review review) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean deleteReview(int reviewId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Review> getAllReviews() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Review> getAllReviewsByAlbum(int albumId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Review> getAllReviewsByKoalibee(int koalibeeId) {
		// TODO Auto-generated method stub
		return null;
	}

}
