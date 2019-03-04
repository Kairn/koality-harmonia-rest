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

	private static final String ADMIN_NAME = "admin";
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
		key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	}

	/**
	 * The most recent moment when a valid token is processed. It is used to keep
	 * track of session expiration time.
	 */
	private LocalDateTime lastAccessed;

	/**
	 * Refreshes the signing key to prevent the client from resuing the same token
	 * after a session expires.
	 */
	private void resetKey() {

		key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	}

	/**
	 * 
	 * Authenticates a user's credentials data by validating the password. It can
	 * authenticate both regular users and system administrator. It will update the
	 * time of last access if the credentials are valid.
	 * 
	 * @param authData the credentials map prepared by koalibee service.
	 * @return a JSON web token representing the user if the credentials are valid,
	 *         or a generic string with an error message.
	 */
	public String authenticate(Map<String, String> authData) {

		try {
			// Authenticate a system administrator.
			if (authData.get("email").equals(ADMIN_NAME)) {
				if (SecurityUtility.isValidPassword(authData.get("password"), ADMIN_SALT, ADMIN_HASH)) {
					this.lastAccessed = LocalDateTime.now();
					return SecurityUtility.buildAuthJws(-777, "", this.key);
				} else {
					return "unauthorized admin";
				}
			}

			// Authenticate a koalibee.
			if (authData.get("email").contains("@")) {
				if (SecurityUtility.isValidPassword(authData.get("password"), authData.get("passwordSalt"),
						authData.get("passwordHash"))) {
					this.lastAccessed = LocalDateTime.now();
					return SecurityUtility.buildAuthJws(Integer.parseInt(authData.get("koalibeeId")),
							authData.get("email"), this.key);
				} else {
					return "unauthorized koalibee";
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
	 * Retrieves the koalibee ID from a JWS. This method will update the time of
	 * last access if the token has not expired, and it automatically generates a
	 * new key if the session has expired.
	 * 
	 * @param jws the JWS sent from the quest.
	 * @return the koalibee ID if the JWS represents a valid user, or -777 when a
	 *         system administrator accesses the server, or 0 for unauthorized
	 *         users.
	 */
	public int reauthenticate(String jws) {

		// Check session expiration.
		if (!SecurityUtility.jwsHasNotExpired(this.lastAccessed)) {
			this.resetKey();
			return 0;
		}

		Integer id;

		try {
			id = (Integer) SecurityUtility.parseAuthJws(jws, this.key)[0];
			this.lastAccessed = LocalDateTime.now();
			return id;
		} catch (Exception e) {
			return 0;
		}

	}

}
