package io.esoma.khr.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import io.esoma.khr.model.Track;
import io.esoma.khr.utility.DataUtility;

/**
 * 
 * The basic implementation of TrackDao interface using Hibernate 5.
 * 
 * @author Eddy Soma
 *
 */
@Repository(value = "trackDaoImplBasic")
public class TrackDaoImpl implements TrackDao {

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
	public Track getTrackById(int trackId) {

		Transaction tx = null;
		Track track = null;

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			track = session.get(Track.class, trackId);
			// Obtain audio data url if album is published.
			if (track.getAlbum().getIsPublished().equals("T")) {
				if (track.getAudio() != null && track.getAudioType() != null) {
					track.setAudioDataUrl(
							DataUtility.encodeBytesToDataUrlAudio(track.getAudio(), track.getAudioType()));
				}
			}
			// Initialize album details.
			track.setAlbum(track.getAlbum());
			tx.commit();
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			track = null;
		}

		return track;

	}

	@Override
	public int addTrack(Track track) {

		Transaction tx = null;
		int id = 0;

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			id = (int) session.save(track);
			tx.commit();
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			id = 0;
		}

		return id;

	}

	@Override
	public boolean deleteTrack(int trackId) {

		Transaction tx = null;
		boolean success = false;

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			Track track = session.load(Track.class, trackId);
			session.delete(track);
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
	public List<Track> getAllTracks() {

		Transaction tx = null;
		List<Track> trackList = new ArrayList<>();

		final String hql = "FROM Track AS t ORDER BY t.trackName ASC";

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			session.createQuery(hql, Track.class).getResultList().forEach(t -> trackList.add(t));
			tx.commit();
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			trackList.clear();
		}

		return trackList;

	}

	@Override
	public List<Track> getAllTracksByAlbum(int albumId) {

		Transaction tx = null;
		List<Track> trackList = new ArrayList<>();

		final String hql = "FROM Track AS t WHERE t.album.albumId = :albumId ORDER BY t.trackName ASC";

		try (Session session = sessionFactory.openSession()) {
			tx = session.beginTransaction();
			session.createQuery(hql, Track.class).setParameter("albumId", albumId).getResultList()
					.forEach(t -> trackList.add(t));
			tx.commit();
		} catch (Exception e) {
			// Debug message
			System.out.println(e);
			trackList.clear();
		}

		return trackList;

	}

}
