package io.esoma.khr.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import io.esoma.khr.model.Album;
import io.esoma.khr.model.Genre;
import io.esoma.khr.utility.DataUtility;

/**
 * 
 * The basic implementation of AlbumDao interface using Hibernate 5.
 * 
 * @author Eddy Soma
 *
 */
@Repository(value = "albumDaoImplBasic")
public class AlbumDaoImpl implements AlbumDao {

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
	public Album getAlbumById(int albumId) {

		Transaction tx = null;
		Album album = null;

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			album = session.get(Album.class, albumId);
			// Obtain artwork data url.
			if (album.getArtwork() != null && album.getArtworkType() != null) {
				album.setArtworkDataUrl(
						DataUtility.encodeBytesToDataUrlImage(album.getArtwork(), album.getArtworkType()));
			}
			// Initialize publisher details.
			album.setKoalibee(album.getKoalibee());
			tx.commit();
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			album = null;
		}

		return album;

	}

	@Override
	public int addAlbum(Album album) {

		Transaction tx = null;
		int id = 0;

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			id = (int) session.save(album);
			tx.commit();
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			id = 0;
		}

		return id;

	}

	@Override
	public boolean updateAlbum(Album album) {

		Transaction tx = null;
		boolean success = false;

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			Album persistAlbum = session.get(Album.class, album.getAlbumId());

			// Update non-null fields.
			if (album.getAlbumName() != null) {
				persistAlbum.setAlbumName(album.getAlbumName());
			}
			if (album.getArtist() != null) {
				persistAlbum.setArtist(album.getArtist());
			}
			if (album.getGenre() != null) {
				persistAlbum.setGenre(session.get(Genre.class, album.getGenre().getGenreId()));
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
	public boolean deleteAlbum(int albumId) {

		Transaction tx = null;
		boolean success = false;

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			Album album = session.load(Album.class, albumId);
			session.delete(album);
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
	public boolean publishAlbum(Album album) {

		Transaction tx = null;
		boolean success = false;

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			Album persistAlbum = session.get(Album.class, album.getAlbumId());

			// Update publish details.
			persistAlbum.setArtwork(album.getArtwork());
			persistAlbum.setArtworkType(album.getArtworkType());
			persistAlbum.setEtaPrice(album.getEtaPrice());
			persistAlbum.setIsPublished("T");
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
	public boolean promoteAlbum(int albumId) {

		Transaction tx = null;
		boolean success = false;

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			Album album = session.get(Album.class, albumId);
			album.setIsPromoted("T");
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
	public List<Album> getAllAlbums() {

		Transaction tx = null;
		List<Album> albumList = new ArrayList<>();

		final String hql = "FROM Album AS a ORDER BY a.albumName ASC";

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			session.createQuery(hql, Album.class).getResultList().forEach(a -> albumList.add(a));
			tx.commit();
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			albumList.clear();
		}

		return albumList;

	}

	@Override
	public List<Album> getUnpublishedAlbumsByKoalibee(int koalibeeId) {

		Transaction tx = null;
		List<Album> albumList = new ArrayList<>();

		final String hql = "FROM Album AS a WHERE a.isPublished = :isPublished AND a.koalibee.koalibeeId = :koalibeeId";

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			session.createQuery(hql, Album.class).setParameter("isPublished", "F")
					.setParameter("koalibeeId", koalibeeId).getResultList().forEach(a -> albumList.add(a));
			tx.commit();
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			albumList.clear();
		}

		return albumList;

	}

	@Override
	public List<Album> getAllPublishedAlbums() {

		Transaction tx = null;
		List<Album> albumList = new ArrayList<>();

		final String hql = "FROM Album AS a WHERE a.isPublished = :isPublished ORDER BY a.albumName ASC";

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			for (Album a : session.createQuery(hql, Album.class).setParameter("isPublished", "T").getResultList()) {
				if (a.getArtwork() != null && a.getArtworkType() != null) {
					a.setArtworkDataUrl(DataUtility.encodeBytesToDataUrlImage(a.getArtwork(), a.getArtworkType()));
				}
				albumList.add(a);
			}
			tx.commit();
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			albumList.clear();
		}

		return albumList;

	}

}
