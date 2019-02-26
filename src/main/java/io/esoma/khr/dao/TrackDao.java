package io.esoma.khr.dao;

import java.util.List;

import io.esoma.khr.model.Track;

/**
 * 
 * The interface used for storing and retrieving data related to tracks.
 * 
 * @author Eddy Soma
 *
 */
public interface TrackDao {

	/**
	 * 
	 * Gets the track object from the database with the given ID.
	 * 
	 * @param trackId the ID of the track.
	 * @return the track object, or null if the ID is invalid.
	 */
	Track getTrackById(int trackId);

	/**
	 * 
	 * Adds a new track record to the database. Can only be added to an unpublished
	 * album.
	 * 
	 * @param track the track object.
	 * @return the new ID if record is persisted, or 0 if the process fails.
	 */
	int addTrack(Track track);

	/**
	 * 
	 * Deletes a track record from the database. Can only delete from an unpublished
	 * album, unless it is done by a system administrator.
	 * 
	 * @param trackId the ID of the track to be deleted.
	 * @return true if record is deleted, or false otherwise.
	 */
	boolean deleteTrack(int trackId);

	/**
	 * 
	 * Gets a list of all tracks in the database. It should only be used by a system
	 * administrator to manage tracks.
	 * 
	 * @return the track list.
	 */
	List<Track> getAllTracks();

	/**
	 * 
	 * Gets a list of all tracks that belong to an album. Audio data urls will not
	 * be loaded.
	 * 
	 * @param albumId the ID of the album.
	 * @return the track list.
	 */
	List<Track> getAllTracksByAlbum(int albumId);

}
