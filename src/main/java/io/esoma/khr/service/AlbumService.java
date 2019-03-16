package io.esoma.khr.service;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.esoma.khr.dao.AlbumDao;
import io.esoma.khr.dao.KoalibeeDao;
import io.esoma.khr.dao.TrackDao;
import io.esoma.khr.model.Album;
import io.esoma.khr.model.Genre;
import io.esoma.khr.model.Koalibee;
import io.esoma.khr.model.Track;
import io.esoma.khr.utility.DataUtility;

/**
 * 
 * The service class that handles album related functionalities.
 * 
 * @author Eddy Soma
 *
 */
@Service(value = "albumService")
public class AlbumService {

	private KoalibeeDao koalibeeDao;
	private TrackDao trackDao;
	private AlbumDao albumDao;

	@Autowired
	@Qualifier(value = "koalibeeDaoImplBasic")
	public void setKoalibeeDao(KoalibeeDao koalibeeDao) {
		this.koalibeeDao = koalibeeDao;
	}

	@Autowired
	@Qualifier(value = "trackDaoImplBasic")
	public void setTrackDao(TrackDao trackDao) {
		this.trackDao = trackDao;
	}

	@Autowired
	@Qualifier(value = "albumDaoImplBasic")
	public void setAlbumDao(AlbumDao albumDao) {
		this.albumDao = albumDao;
	}

	/**
	 * 
	 * Retrieves the data of an album by its ID. Proxy variables which cannot be
	 * serialized will be properly truncated here.
	 * 
	 * @param albumId the ID of the album.
	 * @return a serializable album object, or null if the ID is invalid.
	 */
	public Album getOne(int albumId) {

		Album album = this.albumDao.getAlbumById(albumId);

		// Truncate proxies.
		if (album != null) {
			album.setArtwork(null);
			album.setArtworkType(null);
			album.setReviewList(null);
			album.setTrackList(null);
			if (album.getKoalibee() != null) {
				// Replace the proxy with a real POJO.
				Koalibee pojoKoalibee = new Koalibee(album.getKoalibee().getKoalibeeId());
				pojoKoalibee.setFirstName(album.getKoalibee().getFirstName());
				pojoKoalibee.setLastName(album.getKoalibee().getLastName());
				pojoKoalibee.setEmail(album.getKoalibee().getEmail());
				album.setKoalibee(pojoKoalibee);
			}
		}

		return album;

	}

	/**
	 * 
	 * Attempts to create a new album under the ownership of a koalibee with the
	 * given data encoded in a JSON string. The koalibee should have been
	 * authenticated first.
	 * 
	 * @param koalibeeId the ID of the koalibee.
	 * @param albumData  the JSON string sent from the request.
	 * @return a positive integer if the creation succeeds, or 0 if it fails.
	 */
	public int create(int koalibeeId, String albumData) {

		JSONObject jo;

		// Parse the JSON string.
		try {
			jo = new JSONObject(albumData);
		} catch (Exception e) {
			return 0;
		}

		Koalibee koalibee;
		Album album = new Album();

		// Validate ID.
		koalibee = this.koalibeeDao.getKoalibeeById(koalibeeId);
		if (koalibee == null) {
			return 0;
		}

		// Extract album data.
		try {
			String albumName = jo.getString("albumName");
			album.setAlbumName(albumName);
		} catch (Exception e) {
			return 0;
		}

		try {
			String artist = jo.getString("artist");
			album.setArtist(artist);
		} catch (Exception e) {
			return 0;
		}

		try {
			int genreId = jo.getInt("genreId");
			album.setGenre(new Genre(genreId));
		} catch (Exception e) {
			return 0;
		}

		// Set other default values.
		album.setKoalibee(koalibee);
		album.setIsPublished("F");
		album.setIsPromoted("F");

		return this.albumDao.addAlbum(album);

	}

	/**
	 * 
	 * Attempts to update the basic information of an unpublished album. KoalibeeId
	 * is passed from the user's JWS and used to verify ownership.
	 * 
	 * @param koalibeeId the ID of the owner.
	 * @param albumId    the ID of the album to be updated.
	 * @param albumData  the JSON string containing the update information.
	 * @return true if the update is successful, or false if it fails.
	 */
	public boolean update(int koalibeeId, int albumId, String albumData) {

		JSONObject jo;

		// Parse the JSON string.
		try {
			jo = new JSONObject(albumData);
		} catch (Exception e) {
			return false;
		}

		Album album = new Album(albumId);

		// Verify ownership.
		List<Album> albumList = this.albumDao.getUnpublishedAlbumsByKoalibee(koalibeeId);
		if (!albumList.contains(album)) {
			return false;
		}

		// Extract album data.
		try {
			String albumName = jo.getString("albumName");
			album.setAlbumName(albumName);
		} catch (Exception e) {
			album.setAlbumName(null);
		}

		try {
			String artist = jo.getString("artist");
			album.setArtist(artist);
		} catch (Exception e) {
			album.setArtist(null);
		}

		try {
			int genreId = jo.getInt("genreId");
			album.setGenre(new Genre(genreId));
		} catch (Exception e) {
			album.setGenre(null);
		}

		return this.albumDao.updateAlbum(album);

	}

	/**
	 * 
	 * Attempts to delete an album with the given ID from the database. Unless the
	 * user is a system administrator, only unpublished albums can be deleted by
	 * their creator.
	 * 
	 * @param koalibeeId the ID of the koalibee who requests the deletion.
	 * @param albumId    the ID of the album.
	 * @return true if the deletion is successful, or false if it fails.
	 */
	public boolean delete(int koalibeeId, int albumId) {

		// For system administrator.
		if (koalibeeId == -777) {
			return this.albumDao.deleteAlbum(albumId);
		} else {
			// Check ownership.
			List<Album> albumList = this.albumDao.getUnpublishedAlbumsByKoalibee(koalibeeId);
			if (albumList.contains(new Album(albumId))) {
				return this.albumDao.deleteAlbum(albumId);
			} else {
				return false;
			}
		}

	}

	/**
	 * 
	 * Attempts to publish an album with the given data encoded in a JSON string.
	 * 
	 * @param koalibeeId the ID of the publisher.
	 * @param albumId    the ID of the album to be published.
	 * @param albumData  the JSON string sent from the request.
	 * @return true if the publishing is successful, or false if it fails.
	 */
	public boolean publish(int koalibeeId, int albumId, String albumData) {

		JSONObject jo;

		// Parse the JSON string.
		try {
			jo = new JSONObject(albumData);
		} catch (Exception e) {
			return false;
		}

		Koalibee publisher = this.koalibeeDao.getKoalibeeById(koalibeeId);
		Album album = this.albumDao.getAlbumById(albumId);

		// Verify ownership.
		if (publisher != null && album != null) {
			if (!album.getKoalibee().equals(publisher)) {
				return false;
			}
		} else {
			return false;
		}

		if (album.getIsPublished().equals("T")) {
			return false;
		}

		// Check if the album contains at least one track.
		List<Track> trackList = this.trackDao.getAllTracksByAlbum(albumId);
		if (trackList.isEmpty()) {
			return false;
		}

		// Extract publishing details.
		try {
			int etaPrice = jo.getInt("etaPrice");
			album.setEtaPrice(etaPrice);
		} catch (Exception e) {
			return false;
		}

		try {
			String artworkDataUrl = jo.getString("artworkDataUrl");
			album.setArtwork(DataUtility.decodeDataUrlToBytes(artworkDataUrl));
			album.setArtworkType(artworkDataUrl.substring(artworkDataUrl.indexOf('/') + 1, artworkDataUrl.indexOf(';'))
					.toUpperCase());
		} catch (Exception e) {
			return false;
		}

		// Publish the album and award the publisher.
		if (this.albumDao.publishAlbum(album)) {
			publisher.setEtaBalance(publisher.getEtaBalance() + 40);
			this.koalibeeDao.updateEtaBalance(publisher);
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 
	 * Attempts to set a published album to promoted status at the cost of the
	 * publisher.
	 * 
	 * @param koalibeeId the ID of the publisher.
	 * @param albumId    the ID of the album to be promoted.
	 * @return true if the promotion is successful, or false if it fails.
	 */
	public boolean promote(int koalibeeId, int albumId) {

		Koalibee publisher = this.koalibeeDao.getKoalibeeById(koalibeeId);
		Album album = this.albumDao.getAlbumById(albumId);

		// Verify ownership.
		if (publisher != null && album != null) {
			if (!album.getKoalibee().equals(publisher)) {
				return false;
			}
		} else {
			return false;
		}

		if (!album.getIsPublished().equals("T")) {
			return false;
		}

		if (album.getIsPromoted().equals("T")) {
			return false;
		}

		// Deduct the promotion fee from the publisher.
		if (publisher.getEtaBalance() < 100) {
			return false;
		} else {
			publisher.setEtaBalance(publisher.getEtaBalance() - 100);
			this.koalibeeDao.updateEtaBalance(publisher);
			return this.albumDao.promoteAlbum(albumId);
		}

	}

	/**
	 * 
	 * Retrieves all albums (published and unpublished). It can only be accessed by
	 * a system administrator.
	 * 
	 * @return the album list.
	 */
	public List<Album> getAll() {

		List<Album> albumList = this.albumDao.getAllAlbums();

		// Truncate proxies.
		for (Album a : albumList) {
			a.setArtwork(null);
			a.setArtworkType(null);
			a.setArtworkDataUrl(null);
			a.setTrackList(null);
			a.setReviewList(null);
			a.setKoalibee(null);
		}

		return albumList;

	}

	/**
	 * 
	 * Retrieves all unpublished albums created by the koalibee.
	 * 
	 * @param koalibeeId the ID of the koalibee.
	 * @return the album list.
	 */
	public List<Album> getUnpublished(int koalibeeId) {

		List<Album> albumList = this.albumDao.getUnpublishedAlbumsByKoalibee(koalibeeId);

		// Truncate proxies.
		for (Album a : albumList) {
			a.setArtwork(null);
			a.setArtworkType(null);
			a.setArtworkDataUrl(null);
			a.setTrackList(null);
			a.setReviewList(null);
			a.setKoalibee(null);
		}

		return albumList;

	}

	/**
	 * 
	 * Retrieves all published albums that are visible to the public.
	 * 
	 * @return the album list.
	 */
	public List<Album> getPublished() {

		List<Album> albumList = this.albumDao.getAllPublishedAlbums();

		// Truncate proxies.
		for (Album a : albumList) {
			a.setArtwork(null);
			a.setArtworkType(null);
			a.setTrackList(null);
			a.setReviewList(null);
			a.setKoalibee(null);
		}

		return albumList;

	}

}
