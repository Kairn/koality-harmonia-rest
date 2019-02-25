package io.esoma.khr.dao;

import java.time.LocalDate;
import java.util.List;

import io.esoma.khr.model.Moment;

/**
 * 
 * The interface used for storing and retrieving data related to daily moments
 * posted by users of the application.
 * 
 * @author Eddy Soma
 *
 */
public interface MomentDao {

	/**
	 * 
	 * Gets the moment object from the database with the given ID.
	 * 
	 * @param momentId the ID of the moment.
	 * @return the moment object, or null if the ID is invalid.
	 */
	Moment getMomentById(int momentId);

	/**
	 * 
	 * Gets the moment object from the database that belongs to a specific koalibee
	 * and has a specific date.
	 * 
	 * @param koalibeeId the ID of the koalibee.
	 * @param postDate   the date the moment was posted.
	 * @return the moment object, or null if no matching record is found.
	 */
	Moment getMomentByKoalibeeAndDate(int koalibeeId, LocalDate postDate);

	/**
	 * 
	 * Adds a new moment record to the database. The input object needs to be
	 * validated by the service first to make sure no koalibee can post more than
	 * one record per day.
	 * 
	 * @param moment the moment object to be persisted.
	 * @return the new ID if record is persisted, or 0 if the process fails.
	 */
	int addMoment(Moment moment);

	/**
	 * 
	 * Gets a list of all moments from the database.
	 * 
	 * @return the moment list.
	 */
	List<Moment> getAllMoments();

	/**
	 * 
	 * Gets a list of all moments that were posted on a specific date.
	 * 
	 * @param postDate the date the moments were posted.
	 * @return the moment list.
	 */
	List<Moment> getAllMomentsByDate(LocalDate postDate);

	/**
	 * 
	 * Deletes a moment record from the database. This method should only be used by
	 * a system administrator.
	 * 
	 * @param momentId the ID of the moment to be deleted.
	 * @return true if record is deleted, or false otherwise.
	 */
	boolean deleteMoment(int momentId);

}
