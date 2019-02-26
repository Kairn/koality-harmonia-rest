package io.esoma.khr.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import io.esoma.khr.model.Koalibee;
import io.esoma.khr.utility.DataUtility;

/**
 * 
 * The basic implementation of KoalibeeDao interface using Hibernate 5.
 * 
 * @author Eddy Soma
 *
 */
@Repository(value = "koalibeeDaoImplBasic")
public class KoalibeeDaoImpl implements KoalibeeDao {

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
	public Koalibee getKoalibeeById(int koalibeeId) {

		Transaction tx = null;
		Koalibee koalibee = null;

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			koalibee = session.get(Koalibee.class, koalibeeId);
			// Obtain avatar data url.
			if (koalibee.getAvatar() != null && koalibee.getAvatarType() != null) {
				koalibee.setAvatarDataUrl(
						DataUtility.encodeBytesToDataUrlImage(koalibee.getAvatar(), koalibee.getAvatarType()));
			}
			tx.commit();
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			koalibee = null;
		}

		return koalibee;

	}

	@Override
	public Koalibee getKoalibeeByEmail(String email) {

		Transaction tx = null;
		Koalibee koalibee = null;

		final String hql = "FROM Koalibee AS k WHERE k.email = :email";

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			koalibee = session.createQuery(hql, Koalibee.class).setParameter("email", email).getSingleResult();
			// Obtain avatar data url
			if (koalibee.getAvatar() != null && koalibee.getAvatarType() != null) {
				koalibee.setAvatarDataUrl(
						DataUtility.encodeBytesToDataUrlImage(koalibee.getAvatar(), koalibee.getAvatarType()));
			}
			tx.commit();
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			koalibee = null;
		}

		return koalibee;

	}

	@Override
	public int addKoalibee(Koalibee koalibee) {

		Transaction tx = null;
		int id = 0;

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			id = (int) session.save(koalibee);
			tx.commit();
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			id = 0;
		}

		return id;

	}

	@Override
	public boolean updateKoalibee(Koalibee koalibee) {

		Transaction tx = null;
		boolean success = false;

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			Koalibee persistKoalibee = session.get(Koalibee.class, koalibee.getKoalibeeId());

			// Update non-null fields.
			if (koalibee.getFirstName() != null) {
				persistKoalibee.setFirstName(koalibee.getFirstName());
			}
			if (koalibee.getLastName() != null) {
				persistKoalibee.setLastName(koalibee.getLastName());
			}
			if (koalibee.getAvatar() != null) {
				persistKoalibee.setAvatar(koalibee.getAvatar());
				persistKoalibee.setAvatarType(koalibee.getAvatarType());
			}
			tx.commit();
			success = true;
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			success = false;
		}

		return success;

	}

	@Override
	public boolean updateCredentials(Koalibee koalibee) {

		Transaction tx = null;
		boolean success = false;

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			Koalibee persistKoalibee = session.get(Koalibee.class, koalibee.getKoalibeeId());

			// Update non-null fields
			if (koalibee.getCredentials().getEmail() != null) {
				persistKoalibee.getCredentials().setEmail(koalibee.getCredentials().getEmail());
				persistKoalibee.setEmail(koalibee.getCredentials().getEmail());
			}
			if (koalibee.getCredentials().getPasswordSalt() != null) {
				persistKoalibee.getCredentials().setPasswordSalt(koalibee.getCredentials().getPasswordSalt());
				persistKoalibee.getCredentials().setPasswordHash(koalibee.getCredentials().getPasswordHash());
			}
			tx.commit();
			success = true;
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			success = false;
		}

		return success;

	}

	@Override
	public boolean updateEtaBalance(Koalibee koalibee) {

		Transaction tx = null;
		boolean success = false;

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			Koalibee persistKoalibee = session.get(Koalibee.class, koalibee.getKoalibeeId());
			persistKoalibee.setEtaBalance(koalibee.getEtaBalance());
			tx.commit();
			success = true;
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			success = false;
		}

		return success;

	}

	@Override
	public boolean deleteKoalibee(int koalibeeId) {

		Transaction tx = null;
		boolean success = false;

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			Koalibee persistKoalibee = session.load(Koalibee.class, koalibeeId);
			session.delete(persistKoalibee);
			tx.commit();
			success = true;
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			success = false;
		}

		return success;

	}

	@Override
	public List<String> getAllEmails() {

		Transaction tx = null;
		List<String> emailList = new ArrayList<>();

		final String hql = "SELECT k.email FROM Koalibee AS k";

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			session.createQuery(hql, String.class).getResultList().forEach(e -> emailList.add(e));
			tx.commit();
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			emailList.clear();
		}

		return emailList;

	}

	@Override
	public List<Koalibee> getAllKoalibees() {

		Transaction tx = null;
		List<Koalibee> koalibeeList = new ArrayList<>();

		final String hql = "FROM Koalibee";

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			session.createQuery(hql, Koalibee.class).getResultList().forEach(k -> koalibeeList.add(k));
			tx.commit();
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			koalibeeList.clear();
		}

		return koalibeeList;

	}

}
