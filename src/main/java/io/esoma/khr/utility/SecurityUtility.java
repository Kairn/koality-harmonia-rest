package io.esoma.khr.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * 
 * The utility class with static methods that handle security related
 * functionalities such as password hashing and JWT processing.
 * 
 * @author Eddy Soma
 *
 */
public class SecurityUtility {

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

}
