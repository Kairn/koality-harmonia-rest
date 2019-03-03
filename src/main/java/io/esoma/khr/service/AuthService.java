package io.esoma.khr.service;

import java.time.LocalDateTime;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

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
	 * Generates a JSON web token for a newly registered user or an existing user
	 * when logging in. It will always update the time of last access. It should
	 * only be used when a user's information is first validated.
	 * 
	 * @param koalibeeId the ID of the koalibee.
	 * @param email      the email of the koalibee.
	 * @return the JWS representing the koalibee.
	 */
	public String authenticate(int koalibeeId, String email) {
		//
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
		//
	}

}
