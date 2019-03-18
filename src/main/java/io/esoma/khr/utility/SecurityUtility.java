package io.esoma.khr.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

import javax.crypto.SecretKey;

import org.apache.commons.lang3.RandomStringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

/**
 * 
 * The utility class with static methods that handle security related
 * functionalities such as password hashing and JWT processing.
 * 
 * @author Eddy Soma
 *
 */
public class SecurityUtility {

	private SecurityUtility() {
		// Include a private constructor to prevent instantiation.
	}

	/**
	 * 
	 * Returns a randomly generated alphanumeric string with a fixed length of 4
	 * characters. It is the default salt generation used by the application.
	 * 
	 * @return the 4-character string.
	 */
	public static String getPasswordSaltStandard() {

		return RandomStringUtils.randomAlphanumeric(4);

	}

	/**
	 * 
	 * Return a randomly generated alphanumeric string with a flexible length
	 * depending on the input parameter.
	 * 
	 * @param length the length of the string in characters.
	 * @return the random string.
	 */
	public static String getPasswordSaltFlexibleLength(int length) {

		return RandomStringUtils.randomAlphanumeric(length);

	}

	/**
	 * 
	 * Returns a string representation of the hexadecimal digest of a string input
	 * using the SHA-256 algorithm. It is the default hash algorithm used by the
	 * application.
	 * 
	 * @param message the message to be hashed.
	 * @return the hash string.
	 */
	public static String getSHA256Digest(String message) {

		MessageDigest md = null;

		// Get the hash processor instance.
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}

		// Process the message.
		md.update(message.getBytes());
		byte[] bytes = md.digest();

		// Convert the result into a string.
		StringBuilder digest = new StringBuilder("");
		for (byte b : bytes) {
			digest.append(String.format("%02X", b));
		}

		return new String(digest);

	}

	/**
	 * 
	 * Checks if the input password is valid by appending the salt to the end of the
	 * password and comparing the hash of the entire string to the given hash
	 * retrieved from the database. Returns true if the hashes compare equal.
	 * 
	 * @param password the input password to be validated.
	 * @param salt     the random salt stored in the database.
	 * @param hash     the hash string stored in the database.
	 * @return true if the password is valid, or false otherwise.
	 */
	public static boolean isValidPassword(String password, String salt, String hash) {

		return hash.equals(getSHA256Digest(password.concat(salt)));

	}

	/**
	 * 
	 * Returns a cryptographically signed JSON Web Token used for user
	 * authentication. The signing key should be provided by a service method. It
	 * contains a koalibee's ID, email, and the token creation time in the payload.
	 * 
	 * @param koalibeeId the koalibee's ID (primary key in the database)
	 * @param email      the koalibee's email address.
	 * @param key        the secret key used for signing.
	 * @return the signed JWT.
	 */
	public static String buildAuthJws(int koalibeeId, String email, SecretKey key) {

		return Jwts.builder().claim("koalibeeId", koalibeeId).claim("email", email)
				.claim("timeCreated", LocalDateTime.now().toString()).signWith(key).compact();

	}

	/**
	 * 
	 * Returns the ID, email, and creation time claims in a JWS grouped into an
	 * Object array. The claim values are the appropriate Integer and String types
	 * at runtime.
	 * 
	 * @param jws the JWS to be parsed.
	 * @param key the secret key used to validate the signature.
	 * @return the array containing the claims.
	 * @throws JwtException if the JWS cannot be validated.
	 */
	public static Object[] parseAuthJws(String jws, SecretKey key) throws JwtException {

		Object[] claims = new Object[3];

		Jws<Claims> parsedJws = Jwts.parser().setSigningKey(key).parseClaimsJws(jws);

		// The ID claim.
		claims[0] = parsedJws.getBody().get("koalibeeId", Integer.class);
		// The email claim.
		claims[1] = parsedJws.getBody().get("email", String.class);
		// The creation time claim.
		claims[2] = parsedJws.getBody().get("timeCreated", String.class);

		return claims;

	}

	/**
	 * 
	 * Checks if the auth JWS has expired based on the time it was created. This is
	 * effectively the implementation of setting the maximum age of a user session.
	 * By default, a session will expire after 30 minutes.
	 * 
	 * @param timeCreated the time that the server last received the JWS sent from
	 *                    the client.
	 * @return true if the JWS has not expired, or false otherwise.
	 */
	public static boolean jwsHasNotExpired(LocalDateTime timeCreated) {

		LocalDateTime expiration = timeCreated.plusMinutes(30);

		return LocalDateTime.now().compareTo(expiration) < 0;

	}

}
