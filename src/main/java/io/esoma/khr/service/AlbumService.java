package io.esoma.khr.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.esoma.khr.dao.AlbumDao;
import io.esoma.khr.dao.KoalibeeDao;
import io.esoma.khr.model.Album;
import io.esoma.khr.model.Genre;
import io.esoma.khr.model.Koalibee;

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
	private AlbumDao albumDao;

	@Autowired
	@Qualifier(value = "koalibeeDaoImplBasic")
	public void setKoalibeeDao(KoalibeeDao koalibeeDao) {
		this.koalibeeDao = koalibeeDao;
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
				album.getKoalibee().setAlbumList(null);
				album.getKoalibee().setAvatar(null);
				album.getKoalibee().setAvatarType(null);
				album.getKoalibee().setAvatarDataUrl(null);
				album.getKoalibee().setCredentials(null);
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
	 * @return a positive integer if the posting succeeds, or 0 if it fails.
	 */
	public int create(int koalibeeId, String albumData) {

		JSONObject jo;

		// parse the JSON string.
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

}
