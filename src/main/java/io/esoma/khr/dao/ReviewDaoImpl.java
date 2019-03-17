package io.esoma.khr.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import io.esoma.khr.model.Review;
import io.esoma.khr.utility.LogUtility;

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
			LogUtility.MASTER_LOGGER.error("error in getReviewById, stack trace:", e);
			review = null;
		}

		return review;

	}

	@Override
	public Review getReviewByAlbumAndKoalibee(int albumId, int koalibeeId) {

		Transaction tx = null;
		Review review = null;

		final String hql = "FROM Review AS r WHERE r.album.albumId = :albumId AND r.koalibee.koalibeeId = :koalibeeId";

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			review = session.createQuery(hql, Review.class).setParameter("albumId", albumId)
					.setParameter("koalibeeId", koalibeeId).getSingleResult();
			tx.commit();
		} catch (Exception e) {
			// Debug message
			LogUtility.MASTER_LOGGER.error("error in getReviewByAlbumAndKoalibee, stack trace:", e);
			review = null;
		}

		return review;

	}

	@Override
	public int addReview(Review review) {

		Transaction tx = null;
		int id = 0;

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			id = (int) session.save(review);
			tx.commit();
		} catch (Exception e) {
			// Debug message
			LogUtility.MASTER_LOGGER.error("error in addReview, stack trace:", e);
			id = 0;
		}

		return id;

	}

	@Override
	public boolean deleteReview(int reviewId) {

		Transaction tx = null;
		boolean success = false;

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			Review review = session.load(Review.class, reviewId);
			session.delete(review);
			tx.commit();
			success = true;
		} catch (Exception e) {
			// Debug message
			LogUtility.MASTER_LOGGER.error("error in deleteReview, stack trace:", e);
			success = false;
		}

		return success;

	}

	@Override
	public List<Review> getAllReviews() {

		Transaction tx = null;
		List<Review> reviewList = new ArrayList<>();

		final String hql = "FROM Review";

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			for (Review r : session.createQuery(hql, Review.class).getResultList()) {
				r.setAlbumName(r.getAlbum().getAlbumName());
				r.setKoalibeeName(r.getKoalibee().getFirstName() + " " + r.getKoalibee().getLastName());
				reviewList.add(r);
			}
			tx.commit();
		} catch (Exception e) {
			// Debug message
			LogUtility.MASTER_LOGGER.error("error in getAllReviews, stack trace:", e);
			reviewList.clear();
		}

		return reviewList;

	}

	@Override
	public List<Review> getAllReviewsByAlbum(int albumId) {

		Transaction tx = null;
		List<Review> reviewList = new ArrayList<>();

		final String hql = "FROM Review AS r WHERE r.album.albumId = :albumId";

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			for (Review r : session.createQuery(hql, Review.class).setParameter("albumId", albumId).getResultList()) {
				r.setKoalibeeName(r.getKoalibee().getFirstName() + " " + r.getKoalibee().getLastName());
				reviewList.add(r);
			}
			tx.commit();
		} catch (Exception e) {
			// Debug message
			LogUtility.MASTER_LOGGER.error("error in getAllReviewsByAlbum, stack trace:", e);
			reviewList.clear();
		}

		return reviewList;

	}

	@Override
	public List<Review> getAllReviewsByKoalibee(int koalibeeId) {

		Transaction tx = null;
		List<Review> reviewList = new ArrayList<>();

		final String hql = "FROM Review AS r WHERE r.koalibee.koalibeeId = :koalibeeId";

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			for (Review r : session.createQuery(hql, Review.class).setParameter("koalibeeId", koalibeeId)
					.getResultList()) {
				r.setAlbumName(r.getAlbum().getAlbumName());
				reviewList.add(r);
			}
			tx.commit();
		} catch (Exception e) {
			// Debug message
			LogUtility.MASTER_LOGGER.error("error in getAllReviewsByKoalibee, stack trace:", e);
			reviewList.clear();
		}

		return reviewList;

	}

}
