package io.esoma.khr.service;

import java.time.LocalDateTime;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.esoma.khr.utility.SecurityUtility;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * 
 * The service class that handles user authentication and authorization.
 * 
 * @author Eddy Soma
 *
 */
@Service(value = "authService")
public class AuthService {

	public static final String ADMIN_NAME = "admin";
	public static final String INVALID_ADMIN_CREDENTIALS = "invalid administrator credentials";
	public static final String INVALID_KOALIBEE_CREDENTIALS = "invalid koalibee credentials";

	private static final String ADMIN_SALT = "ADMIN";
	private static final String ADMIN_HASH = "D780C505B2308EBD462B5622497E1ACAE98B34C12895692404BB0A4AE7B937C7";

	/**
	 * The signing key for issuing and parsing JSON Web Tokens.
	 */
	private SecretKey key;

	{
		/**
		 * Generates a random signing key with sufficient length. It will be different
		 * each time the server reboots.
		 */
		this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

		this.lastAccessed = LocalDateTime.now();
	}

	/**
	 * The moment when a new signing key is generated. It is used to keep track of
	 * the age of the key so the system can automatically refresh it after every 24
	 * hours.
	 */
	private LocalDateTime lastAccessed;

	/**
	 * Refreshes the signing key to prevent brute force attacks.
	 */
	void resetKey() {

		key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

		this.lastAccessed = LocalDateTime.now();

	}

	/**
	 * 
	 * Authenticates a user's credentials data by validating the password. It can
	 * authenticate both regular users and system administrator. It will first
	 * refresh the signing key if the last one has been used for more than 24 hours.
	 * 
	 * @param authData the credentials map prepared by koalibee service.
	 * @return a JSON web token representing the user if the credentials are valid,
	 *         or a generic string with an error message.
	 */
	public String authenticate(Map<String, String> authData) {

		// Data element names
		final String EMAIL = "email";
		final String PS = "password";

		// Check if the signing key needs to be refreshed.
		if (LocalDateTime.now().compareTo(this.lastAccessed.plusHours(24)) > 0) {
			this.resetKey();
		}

		try {
			// Authenticate a system administrator.
			if (authData.get(EMAIL).equals(ADMIN_NAME)) {
				if (SecurityUtility.isValidPassword(authData.get(PS), ADMIN_SALT, ADMIN_HASH)) {
					return SecurityUtility.buildAuthJws(-777, "", this.key);
				} else {
					return INVALID_ADMIN_CREDENTIALS;
				}
			}

			// Authenticate a koalibee.
			if (authData.get(EMAIL).contains("@")) {
				if (SecurityUtility.isValidPassword(authData.get(PS), authData.get("passwordSalt"),
						authData.get("passwordHash"))) {
					return SecurityUtility.buildAuthJws(Integer.parseInt(authData.get("koalibeeId")),
							authData.get(EMAIL), this.key);
				} else {
					return INVALID_KOALIBEE_CREDENTIALS;
				}
			} else {
				return "bad request";
			}
		} catch (Exception e) {
			return "unknown error";
		}

	}

	/**
	 * 
	 * Retrieves the koalibee ID from a JWS. This method will first refresh the
	 * signing key if it is necessary.
	 * 
	 * @param jws the JWS sent from the quest.
	 * @return the koalibee ID if the JWS represents a valid user, or -777 when a
	 *         system administrator accesses the server, or 0 if the JWS is invalid.
	 */
	public int reauthenticate(String jws) {

		// Check if the signing key needs to be refreshed.
		if (LocalDateTime.now().compareTo(this.lastAccessed.plusHours(24)) > 0) {
			this.resetKey();
			return -1;
		}

		Integer id;
		LocalDateTime timeCreated;

		try {
			// Parse claims.
			Object[] claims = SecurityUtility.parseAuthJws(jws, this.key);
			id = (Integer) claims[0];
			timeCreated = LocalDateTime.parse(claims[2].toString());

			// Check session expiration.
			if (SecurityUtility.jwsHasNotExpired(timeCreated)) {
				return id;
			} else {
				return -1;
			}
		} catch (Exception e) {
			return 0;
		}

	}

}
