package io.esoma.khr.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.esoma.khr.dao.AlbumDao;
import io.esoma.khr.dao.KoalibeeDao;
import io.esoma.khr.model.Album;
import io.esoma.khr.model.Credentials;
import io.esoma.khr.model.Koalibee;
import io.esoma.khr.utility.DataUtility;
import io.esoma.khr.utility.LogUtility;
import io.esoma.khr.utility.SecurityUtility;

/**
 * 
 * The service class that handles user related functionalities.
 * 
 * @author Eddy Soma
 *
 */
@Service(value = "koalibeeService")
public class KoalibeeService {

	// Error messages
	public static final String BAD_REQUEST = "bad request";
	public static final String BAD_FIRST_NAME = "error while reading firstName";
	public static final String BAD_LAST_NAME = "error while reading lastName";
	public static final String BAD_EMAIL = "error while reading email";
	public static final String BAD_PS = "error while reading password";
	public static final String DUPLICATE_EMAIL = "email is already registered";
	public static final String NULL_EMAIL = "email does not exist";
	public static final String DATABASE_ERROR = "unknown database error occurred";

	// Data element names
	static final String FIRST_NAME = "firstName";
	static final String LAST_NAME = "lastName";
	static final String EMAIL = "email";
	static final String PS = "password";
	static final String ALBUM_ID = "albumId";

	private KoalibeeDao koalibeeDao;
	private AlbumDao albumDao;

	private AuthService authService;

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
	@Qualifier(value = "authService")
	public void setAuthService(AuthService authService) {
		this.authService = authService;
	}

	/**
	 * 
	 * Attempts to register a new koalibee with the given data encoded in a JSON
	 * string. The value of the return string depends on the result of the process.
	 * 
	 * @param koalibeeData the JSON string sent from the request.
	 * @return a JSON web token representing the new koalibee if the registration is
	 *         successful, or a generic string with an error message when the
	 *         process fails for a specific reason, or null when an unknown error
	 *         occurs.
	 */
	public String register(String koalibeeData) {

		JSONObject jo;

		Map<String, String> authData = new HashMap<>();

		// Parse the JSON string.
		try {
			jo = new JSONObject(koalibeeData);
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(LogUtility.BAD_JSON);
			return BAD_REQUEST;
		}

		Credentials credentials = new Credentials();
		Koalibee koalibee = new Koalibee();

		// Validate first name.
		try {
			String firstName = jo.getString(FIRST_NAME);
			if (firstName.length() < 1) {
				return BAD_FIRST_NAME;
			} else {
				koalibee.setFirstName(firstName);
			}
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(String.format(LogUtility.MISSING_JSON_ELEMENT, FIRST_NAME));
			return BAD_FIRST_NAME;
		}
		// Validate last name.
		try {
			String lastName = jo.getString(LAST_NAME);
			if (lastName.length() < 1) {
				return BAD_LAST_NAME;
			} else {
				koalibee.setLastName(lastName);
			}
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(String.format(LogUtility.MISSING_JSON_ELEMENT, LAST_NAME));
			return BAD_LAST_NAME;
		}
		// Validate email.
		try {
			String email = jo.getString(EMAIL);
			if (!email.contains("@") || email.length() < 5) {
				return BAD_EMAIL;
			} else {
				if (this.koalibeeDao.getAllEmails().contains(email)) {
					return DUPLICATE_EMAIL;
				} else {
					koalibee.setEmail(email);
					credentials.setEmail(email);
					authData.put(EMAIL, email);
				}
			}
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(String.format(LogUtility.MISSING_JSON_ELEMENT, EMAIL));
			return BAD_EMAIL;
		}

		// Validate password.
		try {
			String password = jo.getString(PS);
			if (password.length() < 6) {
				return BAD_PS;
			} else {
				String passwordSalt = SecurityUtility.getPasswordSaltStandard();
				String passwordHash = SecurityUtility.getSHA256Digest(password + passwordSalt);
				credentials.setPasswordSalt(passwordSalt);
				credentials.setPasswordHash(passwordHash);
				authData.put(PS, password);
				authData.put("passwordSalt", passwordSalt);
				authData.put("passwordHash", passwordHash);
				koalibee.setCredentials(credentials);
			}
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(String.format(LogUtility.MISSING_JSON_ELEMENT, PS));
			return BAD_PS;
		}

		// Each new account receives 100 free ETA coins.
		koalibee.setEtaBalance(100);
		// Persist the new user in the database.
		Integer koalibeeId = this.koalibeeDao.addKoalibee(koalibee);
		if (koalibeeId != 0) {
			authData.put("koalibeeId", koalibeeId.toString());
			return authService.authenticate(authData);
		} else {
			return DATABASE_ERROR;
		}

	}

	/**
	 * 
	 * Attempts to login a koalibee with the given credentials encoded in a JSON
	 * string.
	 * 
	 * @param credentialsData the JSON string sent from the request.
	 * @return a JSON web token representing the koalibee if the credentials are
	 *         valid, or a generic string with an error message.
	 */
	public String login(String credentialsData) {

		JSONObject jo;

		// Parse the JSON string.
		try {
			jo = new JSONObject(credentialsData);
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(LogUtility.BAD_JSON);
			return BAD_REQUEST;
		}

		Map<String, String> authData = new HashMap<>();

		// Retrieve email.
		try {
			String email = jo.getString(EMAIL);
			authData.put(EMAIL, email);
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(String.format(LogUtility.MISSING_JSON_ELEMENT, EMAIL));
			return BAD_EMAIL;
		}
		// Retrieve password.
		try {
			String password = jo.getString(PS);
			authData.put(PS, password);
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(String.format(LogUtility.MISSING_JSON_ELEMENT, PS));
			return BAD_PS;
		}

		// Check if the user is a system administrator.
		if (authData.get(EMAIL).equals(AuthService.ADMIN_NAME)) {
			return authService.authenticate(authData);
		} else {
			Koalibee koalibee = this.koalibeeDao.getKoalibeeByEmail(authData.get(EMAIL));
			if (koalibee != null) {
				authData.put("koalibeeId", Integer.toString(koalibee.getKoalibeeId()));
				authData.put("passwordSalt", koalibee.getCredentials().getPasswordSalt());
				authData.put("passwordHash", koalibee.getCredentials().getPasswordHash());
				return authService.authenticate(authData);
			} else {
				return NULL_EMAIL;
			}
		}

	}

	/**
	 * 
	 * Retrieves the data of a koalibee by its ID. Proxy variables which cannot be
	 * serialized will be properly truncated here.
	 * 
	 * @param koalibeeId the ID of the koalibee.
	 * @return a serializable koalibee object, or null if the ID is invalid.
	 */
	public Koalibee getOne(int koalibeeId) {

		Koalibee koalibee = this.koalibeeDao.getKoalibeeById(koalibeeId);

		// Truncate proxies.
		if (koalibee != null) {
			koalibee.setAvatar(null);
			koalibee.setAvatarType(null);
			koalibee.setCredentials(null);
			koalibee.setAlbumList(null);
		}

		return koalibee;

	}

	/**
	 * 
	 * Updates the basic information of a koalibee with the given data encoded in a
	 * JSON string.
	 * 
	 * @param koalibeeId   the ID of the koalibee.
	 * @param koalibeeData the JSON string sent from the request.
	 * @return true if the update is successful, or false if it fails.
	 */
	public boolean updateInformation(int koalibeeId, String koalibeeData) {

		JSONObject jo;

		// Parse the JSON string.
		try {
			jo = new JSONObject(koalibeeData);
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(LogUtility.BAD_JSON);
			return false;
		}

		Koalibee koalibee = new Koalibee(koalibeeId);

		// Try update the first name.
		try {
			String firstName = jo.getString(FIRST_NAME);
			if (firstName.length() > 0) {
				koalibee.setFirstName(firstName);
			}
		} catch (Exception e) {
			koalibee.setFirstName(null);
		}
		// Try update the last name.
		try {
			String lastName = jo.getString(LAST_NAME);
			if (lastName.length() > 0) {
				koalibee.setLastName(lastName);
			}
		} catch (Exception e) {
			koalibee.setLastName(null);
		}

		// Try update the avatar.
		try {
			String avatarDataUrl = jo.getString("avatarDataUrl");
			koalibee.setAvatar(DataUtility.decodeDataUrlToBytes(avatarDataUrl));
			koalibee.setAvatarType(
					avatarDataUrl.substring(avatarDataUrl.indexOf('/') + 1, avatarDataUrl.indexOf(';')).toUpperCase());
		} catch (Exception e) {
			koalibee.setAvatar(null);
		}

		return this.koalibeeDao.updateKoalibee(koalibee);

	}

	/**
	 * 
	 * Updates the credentials of a koalibee with the given data encoded in a JSON
	 * string.
	 * 
	 * @param koalibeeId      the ID of the koalibee.
	 * @param credentialsData the JSON string sent from the request.
	 * @return true if the update is successful, or false if it fails.
	 */
	public boolean updateCredentials(int koalibeeId, String credentialsData) {

		JSONObject jo;

		// Parse the JSON string.
		try {
			jo = new JSONObject(credentialsData);
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(LogUtility.BAD_JSON);
			return false;
		}

		Credentials credentials = new Credentials();
		Koalibee koalibee = new Koalibee(koalibeeId);

		// Try update the email.
		try {
			String email = jo.getString(EMAIL);
			if (!this.koalibeeDao.getAllEmails().contains(email)) {
				credentials.setEmail(email);
			} else {
				// Reject the request if a duplicate email is found.
				return false;
			}
		} catch (Exception e) {
			credentials.setEmail(null);
		}

		// Try update the password.
		try {
			String password = jo.getString(PS);
			String passwordSalt = SecurityUtility.getPasswordSaltStandard();
			String passwordHash = SecurityUtility.getSHA256Digest(password + passwordSalt);
			credentials.setPasswordSalt(passwordSalt);
			credentials.setPasswordHash(passwordHash);
		} catch (Exception e) {
			credentials.setPasswordSalt(null);
		}

		koalibee.setCredentials(credentials);

		return this.koalibeeDao.updateCredentials(koalibee);

	}

	/**
	 * 
	 * Adds an album to a koalibee's inventory and deducting the ETA price from the
	 * its balance.
	 * 
	 * @param koalibeeId the ID of the koalibee.
	 * @param albumData  the JSON string containing the album data.
	 * @return true if the purchase is successful, or false if it fails.
	 */
	public boolean purchaseAlbum(int koalibeeId, String albumData) {

		JSONObject jo;

		// Parse the JSON string.
		try {
			jo = new JSONObject(albumData);
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(LogUtility.BAD_JSON);
			return false;
		}

		// Try get the album ID.
		int albumId;
		try {
			albumId = jo.getInt(ALBUM_ID);
		} catch (Exception e) {
			LogUtility.ROOT_LOGGER.warn(String.format(LogUtility.MISSING_JSON_ELEMENT, ALBUM_ID));
			return false;
		}

		// Check if the koalibee already has the album.
		if (this.koalibeeDao.getAllPurchasedAlbumsByKoalibeeId(koalibeeId).contains(new Album(albumId))) {
			return false;
		}

		// Check if the koalibee has sufficient balance.
		Album album = this.albumDao.getAlbumById(albumId);
		Koalibee koalibee = this.koalibeeDao.getKoalibeeById(koalibeeId);

		if (koalibee.getEtaBalance() < album.getEtaPrice()) {
			return false;
		} else {
			koalibee.setEtaBalance(koalibee.getEtaBalance() - album.getEtaPrice());
			// Credit the publisher.
			Koalibee publisher = album.getKoalibee();
			publisher.setEtaBalance(publisher.getEtaBalance() + album.getEtaPrice());
			if (this.koalibeeDao.updateEtaBalance(koalibee)) {
				if (this.koalibeeDao.purchaseAlbum(koalibeeId, albumId)) {
					this.koalibeeDao.updateEtaBalance(publisher);
					return true;
				} else {
					koalibee.setEtaBalance(koalibee.getEtaBalance() + album.getEtaPrice());
					this.koalibeeDao.updateEtaBalance(koalibee);
					return false;
				}
			} else {
				return false;
			}
		}

	}

	/**
	 * 
	 * Deletes the koalibee with the given ID from the database. This can only be
	 * performed by a system administrator.
	 * 
	 * @param koalibeeId the ID of the koalibee.
	 * @return true if the deletion is successful, or false if it fails.
	 */
	public boolean delete(int koalibeeId) {

		return this.koalibeeDao.deleteKoalibee(koalibeeId);

	}

	/**
	 * 
	 * Retrieves all registered koalibees. It can only be access by a system
	 * administrator.
	 * 
	 * @return the koalibee list.
	 */
	public List<Koalibee> getAll() {

		List<Koalibee> koalibeeList = this.koalibeeDao.getAllKoalibees();

		// Truncate proxies.
		for (Koalibee k : koalibeeList) {
			k.setAvatar(null);
			k.setAvatarType(null);
			k.setAvatarDataUrl(null);
			k.setAlbumList(null);
			k.setCredentials(null);
		}

		return koalibeeList;

	}

	/**
	 * 
	 * Gets all albums owned by a koalibee with the given ID. Lazily initialized
	 * data will be truncated before return.
	 * 
	 * @param koalibeeId the ID of the koalibee.
	 * @return the album list.
	 */
	public List<Album> getInventory(int koalibeeId) {

		List<Album> albumList = this.koalibeeDao.getAllPurchasedAlbumsByKoalibeeId(koalibeeId);

		// Truncate proxies.
		for (Album a : albumList) {
			a.setArtwork(null);
			a.setArtworkType(null);
			a.setKoalibee(null);
			a.setReviewList(null);
			a.setTrackList(null);
		}

		return albumList;

	}

	/**
	 * 
	 * Gets all unpublished albums created by a koalibee with the given ID.
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
			a.setKoalibee(null);
			a.setReviewList(null);
			a.setTrackList(null);
		}

		return albumList;

	}

}
