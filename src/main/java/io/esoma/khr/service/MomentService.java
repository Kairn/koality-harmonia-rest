package io.esoma.khr.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.esoma.khr.dao.KoalibeeDao;
import io.esoma.khr.dao.MomentDao;
import io.esoma.khr.model.Koalibee;
import io.esoma.khr.model.Moment;
import io.esoma.khr.utility.LogUtility;

/**
 * 
 * The service class that handles moment related functionalities.
 * 
 * @author Eddy Soma
 *
 */
@Service(value = "momentService")
public class MomentService {

	private MomentDao momentDao;
	private KoalibeeDao koalibeeDao;

	@Autowired
	@Qualifier(value = "momentDaoImplBasic")
	public void setMomentDao(MomentDao momentDao) {
		this.momentDao = momentDao;
	}

	@Autowired
	@Qualifier(value = "koalibeeDaoImplBasic")
	public void setKoalibeeDao(KoalibeeDao koalibeeDao) {
		this.koalibeeDao = koalibeeDao;
	}

	/**
	 * 
	 * Retrieves the data of a moment by its ID. Proxy variables which cannot be
	 * serialized will be properly truncated here.
	 * 
	 * @param momentId the ID of the moment.
	 * @return a serializable moment object, or null if the ID is invalid.
	 */
	public Moment getOne(int momentId) {

		Moment moment = this.momentDao.getMomentById(momentId);

		// Truncate proxies.
		if (moment != null) {
			moment.setKoalibee(null);
		}

		return moment;

	}

	/**
	 * 
	 * Searches for a specific moment by its poster and post date.
	 * 
	 * @param momentData the JSON string containing the search conditions sent from
	 *                   the request.
	 * @return the found moment object, or null if the search yields no result.
	 */
	public Moment searchOne(String momentData) {

		// Data element names
		final String KOALIBEE_ID = "koalibeeId";
		final String POST_DATE = "postDate";

		JSONObject jo;

		// Parse the JSON string.
		try {
			jo = new JSONObject(momentData);
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(LogUtility.BAD_JSON);
			return null;
		}

		int koalibeeId;
		LocalDate postDate;

		// Extract koalibee ID.
		try {
			koalibeeId = jo.getInt(KOALIBEE_ID);
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(String.format(LogUtility.MISSING_JSON_ELEMENT, KOALIBEE_ID));
			return null;
		}

		// Extract ISO date string and convert it to a LocalDate.
		try {
			String dateString = jo.getString(POST_DATE).substring(0, 10);
			postDate = LocalDate.parse(dateString);
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(String.format(LogUtility.MISSING_JSON_ELEMENT, POST_DATE));
			return null;
		}

		Moment moment = this.momentDao.getMomentByKoalibeeAndDate(koalibeeId, postDate);

		// Truncate proxies.
		if (moment != null) {
			moment.setKoalibee(null);
		}

		return moment;

	}

	/**
	 * 
	 * Attempts to post a new moment by a koalibee with the given data encoded in a
	 * JSON string. The koalibee should have been authenticated first.
	 * 
	 * @param koalibeeId the ID of the poster.
	 * @param moment     the JSON string sent from the request.
	 * @return a positive integer if the posting succeeds, or 0 if it fails.
	 */
	public int postOne(int koalibeeId, String momentData) {

		// Data element names
		final String POST_COMMENT = "postComment";

		JSONObject jo;

		// Parse the JSON string.
		try {
			jo = new JSONObject(momentData);
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(LogUtility.BAD_JSON);
			return 0;
		}

		Koalibee koalibee = new Koalibee();
		Moment moment = new Moment();

		Koalibee persistKoalibee;

		// Validate ID.
		persistKoalibee = this.koalibeeDao.getKoalibeeById(koalibeeId);
		if (persistKoalibee == null) {
			return 0;
		}
		koalibee.setKoalibeeId(koalibeeId);

		// Extract moment data.
		try {
			String postComment = jo.getString(POST_COMMENT);
			moment.setPostComment(postComment);
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(String.format(LogUtility.MISSING_JSON_ELEMENT, POST_COMMENT));
			return 0;
		}

		// Check for double posting.
		Moment persistMoment = this.momentDao.getMomentByKoalibeeAndDate(koalibee.getKoalibeeId(), LocalDate.now());
		if (persistMoment != null) {
			return 0;
		}

		moment.setPostDate(LocalDate.now());
		moment.setKoalibee(koalibee);

		int momentId = this.momentDao.addMoment(moment);
		if (momentId == 0) {
			return 0;
		}

		// Add ETA coins to the koalibee's balance.
		int newBalance = persistKoalibee.getEtaBalance() + 20;
		persistKoalibee.setEtaBalance(newBalance);
		this.koalibeeDao.updateEtaBalance(persistKoalibee);

		return momentId;

	}

	/**
	 * 
	 * Retrieves all posted moments. Will truncate all proxy variables.
	 * 
	 * @return the moment list.
	 */
	public List<Moment> getAll() {

		List<Moment> momentList = this.momentDao.getAllMoments();

		// Truncate proxies.
		momentList.forEach(m -> m.setKoalibee(null));

		return momentList;

	}

	/**
	 * 
	 * Retrieves all moments posted on the given date.
	 * 
	 * @param dateData the JSON string containing the date information.
	 * @return the moment list, or null if the date information is invalid.
	 */
	public List<Moment> getByDate(String dateData) {

		// Data element names
		final String POST_DATE = "postDate";

		JSONObject jo;

		// Parse the JSON string.
		try {
			jo = new JSONObject(dateData);
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(LogUtility.BAD_JSON);
			return new ArrayList<>();
		}

		// Extract the date information.
		LocalDate postDate;
		try {
			String dateString = jo.getString(POST_DATE).substring(0, 10);
			postDate = LocalDate.parse(dateString);
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(String.format(LogUtility.MISSING_JSON_ELEMENT, POST_DATE));
			return new ArrayList<>();
		}

		List<Moment> momentList = this.momentDao.getAllMomentsByDate(postDate);

		// Truncate proxies.
		momentList.forEach(m -> m.setKoalibee(null));

		return momentList;

	}

	/**
	 * 
	 * Deletes the moment with the given ID from the database. This can only be
	 * performed by a system administrator.
	 * 
	 * @param momentId the ID of the moment.
	 * @return true if the deletion is successful, or false if it fails.
	 */
	public boolean delete(int momentId) {

		return this.momentDao.deleteMoment(momentId);

	}

}
