package io.esoma.khr.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import io.esoma.khr.model.Koalibee;
import io.esoma.khr.model.Moment;

/**
 * 
 * The basic implementation of MomentDao interface using Hibernate 5.
 * 
 * @author Eddy Soma
 *
 */
@Repository(value = "momentDaoImplBasic")
public class MomentDaoImpl implements MomentDao {

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
	public Moment getMomentById(int momentId) {

		Transaction tx = null;
		Moment moment = null;

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			moment = session.get(Moment.class, momentId);
			// Get koalibee's name
			moment.setKoalibeeName(moment.getKoalibee().getFirstName() + " " + moment.getKoalibee().getLastName());
			tx.commit();
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			moment = null;
		}

		return moment;

	}

	@Override
	public Moment getMomentByKoalibeeAndDate(int koalibeeId, LocalDate postDate) {

		Transaction tx = null;
		Moment moment = null;

		final String hql = "FROM Moment AS m WHERE m.koalibee.koalibeeId = :koalibeeId AND m.postDate = :postDate";

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			moment = session.createQuery(hql, Moment.class).setParameter("koalibeeId", koalibeeId)
					.setParameter("postDate", postDate).getSingleResult();
			// Get koalibee's name
			moment.setKoalibeeName(moment.getKoalibee().getFirstName() + " " + moment.getKoalibee().getLastName());
			tx.commit();
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			moment = null;
		}

		return moment;

	}

	@Override
	public int addMoment(Moment moment) {

		Transaction tx = null;
		int id = 0;

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			Koalibee koalibee = session.load(Koalibee.class, moment.getKoalibee().getKoalibeeId());
			moment.setKoalibee(koalibee);
			id = (int) session.save(moment);
			tx.commit();
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			id = 0;
		}

		return id;

	}

	@Override
	public List<Moment> getAllMoments() {

		Transaction tx = null;
		List<Moment> momentList = new ArrayList<>();

		final String hql = "FROM Moment AS m ORDER BY m.postDate DESC";

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			for (Moment m : session.createQuery(hql, Moment.class).getResultList()) {
				m.setKoalibeeName(m.getKoalibee().getFirstName() + " " + m.getKoalibee().getLastName());
				momentList.add(m);
			}
			tx.commit();
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			momentList.clear();
		}

		return momentList;

	}

	@Override
	public List<Moment> getAllMomentsByDate(LocalDate postDate) {

		Transaction tx = null;
		List<Moment> momentList = new ArrayList<>();

		final String hql = "FROM Moment AS m WHERE m.postDate = :postDate";

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			for (Moment m : session.createQuery(hql, Moment.class).setParameter("postDate", postDate).getResultList()) {
				m.setKoalibeeName(m.getKoalibee().getFirstName() + " " + m.getKoalibee().getLastName());
				momentList.add(m);
			}
			tx.commit();
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			momentList.clear();
		}

		return momentList;

	}

	@Override
	public boolean deleteMoment(int momentId) {

		Transaction tx = null;
		boolean success = false;

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			Moment moment = session.load(Moment.class, momentId);
			session.delete(moment);
			tx.commit();
			success = true;
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			success = false;
		}

		return success;

	}

}
