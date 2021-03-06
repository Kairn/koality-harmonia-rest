package io.esoma.khr.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.esoma.khr.dao.AlbumDao;
import io.esoma.khr.dao.KoalibeeDao;
import io.esoma.khr.dao.TrackDao;
import io.esoma.khr.model.Album;
import io.esoma.khr.model.Track;
import io.esoma.khr.utility.DataUtility;
import io.esoma.khr.utility.LogUtility;

/**
 * 
 * The service class that handles track related functionalities.
 * 
 * @author Eddy Soma
 *
 */
@Service(value = "trackService")
public class TrackService {

	private KoalibeeDao koalibeeDao;
	private AlbumDao albumDao;
	private TrackDao trackDao;

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

	@Autowired
	@Qualifier(value = "trackDaoImplBasic")
	public void setTrackDao(TrackDao trackDao) {
		this.trackDao = trackDao;
	}

	/**
	 * 
	 * Gets a track from a published album with the given ID. The koalibee must own
	 * the album in order to access this method. This is used to load music to be
	 * played in the user interface.
	 * 
	 * @param koalibeeId the ID of the koalibee.
	 * @param trackId    the ID of the track.
	 * @return the track with audio data, or null if the ID is invalid or if the
	 *         user does not have access.
	 */
	public Track getOne(int koalibeeId, int trackId) {

		Track track = this.trackDao.getTrackById(trackId);

		// Check if the track is published.
		if (track != null) {
			if (!track.getAlbum().getIsPublished().equals("T")) {
				return null;
			}
		} else {
			return null;
		}

		// Allow demo tracks to be return without authentication.
		if (track.getIsDemo().equals("T")) {
			// Truncate proxies.
			track.setAlbum(null);
			track.setAudio(null);
			track.setAudioType(null);
			return track;
		}

		// Verify ownership.
		if (koalibeeId != -777) {
			List<Album> albumList = this.koalibeeDao.getAllPurchasedAlbumsByKoalibeeId(koalibeeId);
			if (!albumList.contains(track.getAlbum())) {
				return null;
			}
		}

		// Truncate proxies.
		track.setAlbum(null);
		track.setAudio(null);
		track.setAudioType(null);

		return track;

	}

	/**
	 * 
	 * Adds a track to an unpublished album with the given data encoded in a JSON
	 * string. Only the creator is allowed to access this method.
	 * 
	 * @param koalibeeId the ID of the creator.
	 * @param albumId    the ID of the album to add track to.
	 * @param trackData  the JSON string sent from the request.
	 * @return a positive integer if the posting succeeds, or 0 if it fails.
	 */
	public int addOne(int koalibeeId, int albumId, String trackData) {

		// Data element names
		final String TRACK_NAME = "trackName";
		final String COMPOSER = "composer";
		final String TRACK_LENGTH = "trackLength";
		final String AUDIO_DATA_URL = "audioDataUrl";
		final String IS_DEMO = "isDemo";

		JSONObject jo;

		// Parse the JSON string.
		try {
			jo = new JSONObject(trackData);
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(LogUtility.BAD_JSON);
			return 0;
		}

		// Check if the album is unpublished.
		Album album = this.albumDao.getAlbumById(albumId);
		if (album != null) {
			if (!album.getIsPublished().equals("F")) {
				return 0;
			}
		} else {
			return 0;
		}

		// Verify ownership.
		if (album.getKoalibee().getKoalibeeId() != koalibeeId) {
			return 0;
		}

		Track track = new Track();

		// Extract track data.
		try {
			String trackName = jo.getString(TRACK_NAME);
			track.setTrackName(trackName);
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(String.format(LogUtility.MISSING_JSON_ELEMENT, TRACK_NAME));
			return 0;
		}

		try {
			String composer = jo.getString(COMPOSER);
			track.setComposer(composer);
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(String.format(LogUtility.MISSING_JSON_ELEMENT, COMPOSER));
			return 0;
		}

		try {
			int trackLength = jo.getInt(TRACK_LENGTH);
			track.setTrackLength(trackLength);
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(String.format(LogUtility.MISSING_JSON_ELEMENT, TRACK_LENGTH));
			return 0;
		}

		try {
			String audioDataUrl = jo.getString(AUDIO_DATA_URL);
			track.setAudio(DataUtility.decodeDataUrlToBytes(audioDataUrl));
			track.setAudioType(
					audioDataUrl.substring(audioDataUrl.indexOf('/') + 1, audioDataUrl.indexOf(';')).toUpperCase());
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(String.format(LogUtility.MISSING_JSON_ELEMENT, AUDIO_DATA_URL));
			return 0;
		}

		try {
			String isDemo = jo.getString(IS_DEMO);
			track.setIsDemo(isDemo);
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(String.format(LogUtility.MISSING_JSON_ELEMENT, IS_DEMO));
			return 0;
		}

		track.setAlbum(album);

		return this.trackDao.addTrack(track);

	}

	/**
	 * 
	 * Attempts to delete a track from an unpublished album. Only the creator or a
	 * system administrator has access to this method.
	 * 
	 * @param koalibeeId the ID of the koalibee.
	 * @param trackId    the ID of the track to be deleted.
	 * @return true if the deletion is successful, or false if it fails.
	 */
	public boolean delete(int koalibeeId, int trackId) {

		// For system administrator.
		if (koalibeeId == -777) {
			return this.trackDao.deleteTrack(trackId);
		} else {
			Track track = this.trackDao.getTrackById(trackId);
			if (track != null) {
				// Check if the album is published.
				if (track.getAlbum().getIsPublished().equals("T")) {
					return false;
				}
				// Verify ownership.
				if (this.albumDao.getAlbumById(track.getAlbum().getAlbumId()).getKoalibee()
						.getKoalibeeId() == koalibeeId) {
					return this.trackDao.deleteTrack(trackId);
				} else {
					return false;
				}
			} else {
				return false;
			}
		}

	}

	/**
	 * 
	 * Retrieves all tracks from the database. This can only be accessed by a system
	 * administrator.
	 * 
	 * @return the track list.
	 */
	public List<Track> getAll() {

		List<Track> trackList = this.trackDao.getAllTracks();

		// Truncate proxies.
		for (Track t : trackList) {
			t.setAlbum(null);
			t.setAudio(null);
			t.setAudioType(null);
			t.setAudioDataUrl(null);
		}

		return trackList;

	}

	/**
	 * 
	 * Retrieve tracks from an album without audio data. Only tracks from published
	 * albums can be viewed publicly.
	 * 
	 * @param koalibeeId the ID of the album owner.
	 * @param albumId    the ID of the album.
	 * @return the track list, and list will be empty if the user is not the creator
	 *         of an unpublished album.
	 */
	public List<Track> getFromAlbum(int koalibeeId, int albumId) {

		Album album = this.albumDao.getAlbumById(albumId);

		// Verify ownership.
		if (album != null) {
			if (album.getIsPublished().equals("F") && album.getKoalibee().getKoalibeeId() != koalibeeId
					&& koalibeeId != -777) {
				return new ArrayList<>();
			}
		} else {
			return new ArrayList<>();
		}

		List<Track> trackList = this.trackDao.getAllTracksByAlbum(albumId);

		// Truncate proxies.
		for (Track t : trackList) {
			t.setAlbum(null);
			t.setAudio(null);
			t.setAudioType(null);
			t.setAudioDataUrl(null);
		}

		return trackList;

	}

}
