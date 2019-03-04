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
			return "bad request";
		}

		Credentials credentials = new Credentials();
		Koalibee koalibee = new Koalibee();

		// Validate first name.
		try {
			String firstName = jo.getString("firstName");
			if (firstName.length() < 1) {
				return "bad first name";
			} else {
				koalibee.setFirstName(firstName);
			}
		} catch (Exception e) {
			return "bad first name";
		}
		// Validate last name.
		try {
			String lastName = jo.getString("lastName");
			if (lastName.length() < 1) {
				return "bad last name";
			} else {
				koalibee.setLastName(lastName);
			}
		} catch (Exception e) {
			return "bad last name";
		}
		// Validate email.
		try {
			String email = jo.getString("email");
			if (!email.contains("@") || email.length() < 5) {
				return "bad email";
			} else {
				if (this.koalibeeDao.getAllEmails().contains(email)) {
					return "duplicate email";
				} else {
					koalibee.setEmail(email);
					credentials.setEmail(email);
					authData.put("email", email);
				}
			}
		} catch (Exception e) {
			return "bad email";
		}

		// Validate password.
		try {
			String password = jo.getString("password");
			if (password.length() < 6) {
				return "bad password";
			} else {
				String passwordSalt = SecurityUtility.getPasswordSaltStandard();
				String passwordHash = SecurityUtility.getSHA256Digest(password + passwordSalt);
				credentials.setPasswordSalt(passwordSalt);
				credentials.setPasswordHash(passwordHash);
				authData.put("password", password);
				authData.put("passwordSalt", passwordSalt);
				authData.put("passwordHash", passwordHash);
			}
		} catch (Exception e) {
			return "bad password";
		}

		// Each new account receives 100 free ETA coins.
		koalibee.setEtaBalance(100);
		// Persist the new user in the database;
		Integer koalibeeId = this.koalibeeDao.addKoalibee(koalibee);
		if (koalibeeId != 0) {
			authData.put("koalibeeId", koalibeeId.toString());
			return authService.authenticate(authData);
		} else {
			return "database error";
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
			return "bad request";
		}

		Map<String, String> authData = new HashMap<>();

		// Retrieve email.
		try {
			String email = jo.getString("email");
			authData.put("email", email);
		} catch (Exception e) {
			return "bad email";
		}
		// Retrieve password.
		try {
			String password = jo.getString("password");
			authData.put("password", password);
		} catch (Exception e) {
			return "bad password";
		}

		// Check if the user is a system administrator.
		if (authData.get("email").equals("admin")) {
			return authService.authenticate(authData);
		} else {
			Koalibee koalibee = this.koalibeeDao.getKoalibeeByEmail(authData.get("email"));
			if (koalibee != null) {
				authData.put("koalibeeId", new Integer(koalibee.getKoalibeeId()).toString());
				authData.put("passwordSalt", koalibee.getCredentials().getPasswordSalt());
				authData.put("passwordHash", koalibee.getCredentials().getPasswordHash());
				return authService.authenticate(authData);
			} else {
				return "null email";
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
			return false;
		}

		Koalibee koalibee = new Koalibee(koalibeeId);

		// Try update the first name.
		try {
			String firstName = jo.getString("firstName");
			if (firstName.length() > 0) {
				koalibee.setFirstName(firstName);
			}
		} catch (Exception e) {
			koalibee.setFirstName(null);
		}
		// Try update the last name.
		try {
			String lastName = jo.getString("lastName");
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
			koalibee.setAvatarType(avatarDataUrl.substring(avatarDataUrl.indexOf("/") + 1, avatarDataUrl.indexOf(";")));
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
			return false;
		}

		Credentials credentials = new Credentials();
		Koalibee koalibee = new Koalibee(koalibeeId);

		// Try update the email.
		try {
			String email = jo.getString("email");
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
			String password = jo.getString("password");
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
			return false;
		}

		// Try get the album ID.
		int albumId;
		try {
			albumId = jo.getInt("albumId");
		} catch (Exception e) {
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
			if (this.koalibeeDao.updateEtaBalance(koalibee)) {
				if (this.koalibeeDao.purchaseAlbum(koalibeeId, albumId)) {
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
