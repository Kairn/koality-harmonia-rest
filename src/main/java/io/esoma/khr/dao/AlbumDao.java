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

	Album getAlbumById(int albumId);

	int addAlbum(Album album);

	boolean updateAlbum(Album album);

	boolean deleteAlbum(int albumId);

	boolean publishAlbum(Album album);

	boolean promoteAlbum(int albumId);

	List<Album> getAllAlbums();

	List<Album> getUnpublishedAlbumsByKoalibee(int koalibeeId);

	List<Album> getAllPublishedAlbums();

}
