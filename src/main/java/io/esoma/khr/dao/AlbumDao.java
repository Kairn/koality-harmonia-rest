package io.esoma.khr.dao;

import java.util.List;

import io.esoma.khr.model.Album;

/**
 * 
 * The interface used for storing and retrieving data related to albums.
 * 
 * @author Eddy Soma
 *
 */
public interface AlbumDao {

	/**
	 * 
	 * Gets the album object from the database with the given ID.
	 * 
	 * @param albumId the ID of the album.
	 * @return the album object, or null if the ID is invalid.
	 */
	Album getAlbumById(int albumId);

	/**
	 * 
	 * Adds a new album record to the database. New albums are always unpublished
	 * and containing no tracks or artwork.
	 * 
	 * @param album the album object to be persisted.
	 * @return the new ID if record is persisted, or 0 if the process fails.
	 */
	int addAlbum(Album album);

	/**
	 * 
	 * Updates one or more pieces of basic information of an album. Allowed columns
	 * are ALBUM_NAME, ARTIST, and GENRE.
	 * 
	 * @param album the album object containing the new information.
	 * @return true if record is updated, or false otherwise.
	 */
	boolean updateAlbum(Album album);

	/**
	 * 
	 * Deletes an album record from the database. Koalibees can use this method to
	 * delete unpublished albums that are created by themselves. A system
	 * administrator has the privilege to delete any album in the database.
	 * 
	 * @param albumId the ID of the album to be deleted.
	 * @return true if record is deleted, or false otherwise.
	 */
	boolean deleteAlbum(int albumId);

	/**
	 * 
	 * Makes an album public and available for purchase. The data for ETA_PRICE,
	 * ARTWORK, and ARTWORK_TYPE columns will be entered in the system for the first
	 * time. The album is considered finalized once published.
	 * 
	 * @param album the album object containing publishing details.
	 * @return true if album is published, or false otherwise.
	 */
	boolean publishAlbum(Album album);

	/**
	 * 
	 * Grants a published album promoted status. The status cannot be revoked
	 * without the action of a system administrator.
	 * 
	 * @param albumId the ID of the album to be promoted.
	 * @return true if album is promoted, or false otherwise.
	 */
	boolean promoteAlbum(int albumId);

	/**
	 * 
	 * Gets a list of all albums in the database (published and unpublished). It
	 * should only be used by a system administrator to manage albums.
	 * 
	 * @return the album list.
	 */
	List<Album> getAllAlbums();

	/**
	 * 
	 * Gets a list of all unpublished albums that belong to a specific koalibee.
	 * 
	 * @param koalibeeId the ID of the koalibee who owns the albums.
	 * @return the album list.
	 */
	List<Album> getUnpublishedAlbumsByKoalibee(int koalibeeId);

	/**
	 * 
	 * Gets a list of all published albums.
	 * 
	 * @return the album list.
	 */
	List<Album> getAllPublishedAlbums();

}
