package io.esoma.khr.dao;

import java.util.List;

import io.esoma.khr.model.Album;
import io.esoma.khr.model.Koalibee;

/**
 * 
 * The interface used for storing and retrieving data related to users of the
 * application.
 * 
 * @author Eddy Soma
 *
 */
public interface KoalibeeDao {

	/**
	 * 
	 * Gets the koalibee object from the database with the given ID.
	 * 
	 * @param koalibeeId the ID of the koalibee.
	 * @return the koalibee object, or null if the ID is invalid.
	 */
	Koalibee getKoalibeeById(int koalibeeId);

	/**
	 * 
	 * Gets the koalibee object from the database with its unique email address.
	 * 
	 * @param email the email of the koalibee.
	 * @return the koalibee object, or null if the email is not found.
	 */
	Koalibee getKoalibeeByEmail(String email);

	/**
	 * 
	 * Adds a new koalibee record to the database. Its credentials will also be
	 * persisted. The input object needs to be validated by the service first.
	 * 
	 * @param koalibee the validated koalibee object.
	 * @return the new ID if record is persisted, or 0 if the process fails.
	 */
	int addKoalibee(Koalibee koalibee);

	/**
	 * 
	 * Updates one or more pieces of basic information of a koalibee. Allowed
	 * columns are FIRST_NAME, LAST_NAME, AVATAR, and AVATAR_TYPE.
	 * 
	 * @param koalibee the koalibee object containing the new information.
	 * @return true if record is updated, or false otherwise.
	 */
	boolean updateKoalibee(Koalibee koalibee);

	/**
	 * 
	 * Updates the email, password, or both of a koalibee's credentials object. A
	 * new salt generated by the service will also be stored in the database. If
	 * email is updated, the underlying koalibee object's EMAIL field will be
	 * updated.
	 * 
	 * @param koalibee the koalibee object containing the new credentials.
	 * @return true if credentials are updated, or false otherwise.
	 */
	boolean updateCredentials(Koalibee koalibee);

	/**
	 * 
	 * Updates the ETA balance of a koalibee. The calculation is done by the
	 * service. This method is only responsible for persisting data.
	 * 
	 * @param koalibee the koalibee object with the new balance.
	 * @return true is balance is updated, or false otherwise.
	 */
	boolean updateEtaBalance(Koalibee koalibee);

	/**
	 * 
	 * Adds a published album into a user's inventory.
	 * 
	 * @param koalibeeId the ID of the purchaser.
	 * @param albumId    the ID of the album being purchased.
	 * @return true if purchase is successful, or false otherwise.
	 */
	boolean purchaseAlbum(int koalibeeId, int albumId);

	/**
	 * 
	 * Deletes a koalibee record from the database. This method can only be accessed
	 * by a system administrator. Authorization is handled by the business logic.
	 * 
	 * @param koalibeeId the ID of the koalibee to be deleted.
	 * @return true if record is deleted, or false otherwise.
	 */
	boolean deleteKoalibee(int koalibeeId);

	/**
	 * 
	 * Gets a list of all emails registered with Koality Harmonia. It is used to
	 * make sure a new user can only register with a new email address that is not
	 * currently in the database.
	 * 
	 * @return the email list.
	 */
	List<String> getAllEmails();

	/**
	 * 
	 * Gets a list of all koalibees in the database. It should only be used by a
	 * system administrator to manage users.
	 * 
	 * @return the koalibee list.
	 */
	List<Koalibee> getAllKoalibees();

	/**
	 * 
	 * Gets a list of all purchased albums of a user.
	 * 
	 * @param koalibeeId the ID of the koalibee.
	 * @return the album list.
	 */
	List<Album> getAllPurchasedAlbumsByKoalibeeId(int koalibeeId);

}
