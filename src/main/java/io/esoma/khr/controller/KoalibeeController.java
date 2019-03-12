package io.esoma.khr.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.esoma.khr.model.Album;
import io.esoma.khr.model.Koalibee;
import io.esoma.khr.service.AuthService;
import io.esoma.khr.service.KoalibeeService;

/**
 * 
 * The controller class that handles HTTP requests concerning user
 * functionalities and interactions.
 * 
 * @author Eddy Soma
 *
 */
@RestController(value = "koalibeeController")
@RequestMapping(path = "/koalibee")
public class KoalibeeController {

	private AuthService authService;
	private KoalibeeService koalibeeService;

	@Autowired
	@Qualifier(value = "authService")
	public void setAuthService(AuthService authService) {
		this.authService = authService;
	}

	@Autowired
	@Qualifier(value = "koalibeeService")
	public void setKoalibeeService(KoalibeeService koalibeeService) {
		this.koalibeeService = koalibeeService;
	}

	/**
	 * 
	 * Handles a HTTP request which attempts to register a new koalibee. The request
	 * body should contain the required information in JSON format.
	 * 
	 * @param koalibeeData the JSON string containing the koalibee information.
	 * @return the JWS that authenticates the new koalibee if the registration is
	 *         successful, or an error message if it fails.
	 */
	@PostMapping(path = "/register")
	public ResponseEntity<String> registerKoalibee(@Validated @RequestBody String koalibeeData) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		String result = this.koalibeeService.register(koalibeeData);

		// Check result.
		if (result != null) {
			if (result.contains(".")) {
				// A new koalibee is created and a JWS returned.
				status = HttpStatus.CREATED;
			} else {
				// The request fails to provide the data necessary for the registration.
				status = HttpStatus.UNPROCESSABLE_ENTITY;
			}
		} else {
			// Bad request without a known reason.
			result = "bad request, unknown reason";
			status = HttpStatus.BAD_REQUEST;
		}

		return ResponseEntity.status(status).body(result);

	}

	/**
	 * 
	 * Handles a HTTP request which attempts to login a koalibee with the given
	 * credentials in the request body.
	 * 
	 * @param credentialsData the credentials in JSON format.
	 * @return a JWS that authenticates the koalibee if the login is successful, or
	 *         an error message.
	 */
	@PostMapping(path = "/login")
	public ResponseEntity<String> loginKoalibee(@Validated @RequestBody String credentialsData) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		String result = this.koalibeeService.login(credentialsData);

		// Check result.
		if (result != null) {
			if (result.contains(".")) {
				// Koalibee is authenticated and a JWS returned.
				status = HttpStatus.OK;
			} else {
				// Authentication fails.
				status = HttpStatus.UNAUTHORIZED;
			}
		} else {
			// Bad request without a known reason.
			result = "bad request, unknown reason";
			status = HttpStatus.BAD_REQUEST;
		}

		return ResponseEntity.status(status).body(result);

	}

	/**
	 * 
	 * Handles a HTTP request of logging out a user in general.
	 * 
	 * @return a message indicating logout success.
	 */
	@PostMapping(path = "/logout")
	public ResponseEntity<String> logout() {

		// Expire all tokens.
		this.authService.setToExpire();

		return ResponseEntity.ok("successfully logged out");

	}

	/**
	 * 
	 * Responds to a HTTP request of retrieving a koalibee's profile information. A
	 * header with the valid authentication token must be present.
	 * 
	 * @param koalibeeId the ID of the koalibee.
	 * @param jws        the signed authentication token.
	 * @return the koalibee object containing the information if the request is
	 *         authenticated, or a blank object.
	 */
	@GetMapping(path = "/get/{koalibeeId}")
	public ResponseEntity<Koalibee> getKoalibee(@Validated @PathVariable int koalibeeId,
			@Validated @RequestHeader(name = "Auth-Token") String jws) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		boolean valid = false;

		// Verify auth ID.
		if (authId == -777) {
			// For System administrator.
			valid = true;
		} else if (authId == -1) {
			// Token expired.
			valid = false;
			status = HttpStatus.EXPECTATION_FAILED;
		} else if (authId == koalibeeId) {
			// Valid token.
			valid = true;
		} else {
			// Invalid token.
			valid = false;
			status = HttpStatus.UNAUTHORIZED;
		}

		Koalibee koalibee;

		// Fetch koalibee data.
		if (valid) {
			koalibee = this.koalibeeService.getOne(koalibeeId);
			if (koalibee != null) {
				// Found data.
				status = HttpStatus.OK;
			} else {
				// No data found for the ID.
				status = HttpStatus.NOT_FOUND;
				koalibee = new Koalibee();
			}
		} else {
			koalibee = new Koalibee();
		}

		return ResponseEntity.status(status).body(koalibee);

	}

	/**
	 * 
	 * Responds to a HTTP request that attempts to update a koalibee's information.
	 * 
	 * @param koalibeeId   the ID of the koalibee.
	 * @param koalibeeData the JSON string containing the update information.
	 * @param jws          the signed authentication token.
	 * @return a general message indicating success or failure.
	 */
	@PutMapping(path = "/profile/{koalibeeId}")
	public ResponseEntity<String> updateKoalibee(@Validated @PathVariable int koalibeeId,
			@Validated @RequestBody String koalibeeData, @Validated @RequestHeader(name = "Auth-Token") String jws) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		String result = new String();

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		boolean valid = false;

		if (authId == -1) {
			status = HttpStatus.EXPECTATION_FAILED;
			result = "authentication token expired";
		} else if (authId == koalibeeId) {
			valid = true;
		} else {
			status = HttpStatus.UNAUTHORIZED;
			result = "not authorized";
		}

		if (valid) {
			if (this.koalibeeService.updateInformation(koalibeeId, koalibeeData)) {
				status = HttpStatus.OK;
				result = "profile updated successfully";
			} else {
				status = HttpStatus.UNPROCESSABLE_ENTITY;
				result = "failed to update";
			}
		}

		return ResponseEntity.status(status).body(result);

	}

	/**
	 * 
	 * Responds to a HTTP request that attempts to change the credentials (email or
	 * password or both) of a koalibee.
	 * 
	 * @param koalibeeId      the ID of the koalibee.
	 * @param credentialsData the JSON string containing the new credentials.
	 * @param jws             the signed authentication token.
	 * @return a general message indicating success or failure.
	 */
	@PutMapping(path = "/credentials/{koalibeeId}")
	public ResponseEntity<String> changeKoalibeeCredentials(@Validated @PathVariable int koalibeeId,
			@Validated @RequestBody String credentialsData, @Validated @RequestHeader(name = "Auth-Token") String jws) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		String result = new String();

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		boolean valid = false;

		if (authId == -1) {
			status = HttpStatus.EXPECTATION_FAILED;
			result = "authentication token expired";
		} else if (authId == koalibeeId) {
			valid = true;
		} else {
			status = HttpStatus.UNAUTHORIZED;
			result = "not authorized";
		}

		if (valid) {
			if (this.koalibeeService.updateCredentials(koalibeeId, credentialsData)) {
				status = HttpStatus.OK;
				result = "credentials changed successfully";
			} else {
				status = HttpStatus.UNPROCESSABLE_ENTITY;
				result = "failed to change credentials";
			}
		}

		return ResponseEntity.status(status).body(result);

	}

	/**
	 * 
	 * Responds to a HTTP request of a koalibee making an album purchase.
	 * 
	 * @param koalibeeId the ID of the koalibee.
	 * @param albumData  the JSON string containing the ID of the album.
	 * @param jws        the signed authentication token.
	 * @return a general message indicating success or failure.
	 */
	@PostMapping(path = "/purchase/{koalibeeId}")
	public ResponseEntity<String> purchaseAlbumForKoalibee(@Validated @PathVariable int koalibeeId,
			@Validated @RequestBody String albumData, @Validated @RequestHeader(name = "Auth-Token") String jws) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		String result = new String();

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		boolean valid = false;

		if (authId == -1) {
			status = HttpStatus.EXPECTATION_FAILED;
			result = "authentication token expired";
		} else if (authId == koalibeeId) {
			valid = true;
		} else {
			status = HttpStatus.UNAUTHORIZED;
			result = "not authorized";
		}

		if (valid) {
			if (this.koalibeeService.purchaseAlbum(koalibeeId, albumData)) {
				status = HttpStatus.OK;
				result = "album purchased successfully";
			} else {
				status = HttpStatus.UNPROCESSABLE_ENTITY;
				result = "failed to make the purchase";
			}
		}

		return ResponseEntity.status(status).body(result);

	}

	/**
	 * 
	 * Responds to a HTTP request from a system administrator to permanently delete
	 * a koalibee's account. All associated albums, moments, and reviews will be
	 * deleted as well.
	 * 
	 * @param koalibeeId the ID of the koalibee.
	 * @param jws        the signed authentication token.
	 * @return a general message indicating success or failure.
	 */
	@DeleteMapping(path = "/delete/{koalibeeId}")
	public ResponseEntity<String> deleteKoalibeeAccount(@Validated @PathVariable int koalibeeId,
			@Validated @RequestHeader(name = "Auth-Token") String jws) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		String result = new String();

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		if (authId == -1) {
			status = HttpStatus.EXPECTATION_FAILED;
			result = "authentication token expired";
		} else if (authId == -777) {
			if (this.koalibeeService.delete(koalibeeId)) {
				status = HttpStatus.OK;
				result = "koalibee account deleted successfully";
			} else {
				status = HttpStatus.NOT_FOUND;
				result = "failed to delete koalibee account";
			}
		} else {
			status = HttpStatus.UNAUTHORIZED;
			result = "administrator privilege required";
		}

		return ResponseEntity.status(status).body(result);

	}

	/**
	 * 
	 * Responds to a HTTP request from a system administrator to fetch a list of all
	 * registered koalibees.
	 * 
	 * @param jws the signed authentication token.
	 * @return a list of koalibee objects found in the database. An empty list is
	 *         returned if there are no registered koalibee. Null is returned if the
	 *         request does not have the authorization.
	 */
	@GetMapping(path = "/get/all")
	public ResponseEntity<List<Koalibee>> listAllKoalibees(@Validated @RequestHeader(name = "Auth-Token") String jws) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		List<Koalibee> result = new ArrayList<>();

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		if (authId == -1) {
			status = HttpStatus.EXPECTATION_FAILED;
			result = null;
		} else if (authId == -777) {
			status = HttpStatus.OK;
			result = this.koalibeeService.getAll();
		} else {
			status = HttpStatus.UNAUTHORIZED;
			result = null;
		}

		return ResponseEntity.status(status).body(result);

	}

	/**
	 * 
	 * Responds to a HTTP request of retrieving all albums owned by a koalibee.
	 * 
	 * @param koalibeeId the ID of the owner.
	 * @param jws        the signed authentication token.
	 * @return a list of album objects. An empty list is returned if the koalibee
	 *         does not own any album. Null is returned if the request cannot be
	 *         authenticated.
	 */
	@GetMapping(path = "/album/owned")
	public ResponseEntity<List<Album>> getKoalibeeInventory(@Validated @PathVariable int koalibeeId,
			@Validated @RequestHeader(name = "Auth-Token") String jws) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		List<Album> result = new ArrayList<>();

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		if (authId == -1) {
			status = HttpStatus.EXPECTATION_FAILED;
			result = null;
		} else if (authId == koalibeeId) {
			status = HttpStatus.OK;
			result = this.koalibeeService.getInventory(koalibeeId);
		} else {
			status = HttpStatus.UNAUTHORIZED;
			result = null;
		}

		return ResponseEntity.status(status).body(result);

	}

	/**
	 * 
	 * Responds to a HTTP request of retrieving all created but unpublished albums.
	 * 
	 * @param koalibeeId the ID of the koalibee.
	 * @param jws        the signed authentication token.
	 * @return a list of album objects. An empty list is returned if the koalibee
	 *         does not have unpublished album. Null is returned if the request
	 *         cannot be authenticated.
	 */
	@GetMapping(path = "/album/unpublished")
	public ResponseEntity<List<Album>> getKoalibeeCreations(@Validated @PathVariable int koalibeeId,
			@Validated @RequestHeader(name = "Auth-Token") String jws) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		List<Album> result = new ArrayList<>();

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		if (authId == -1) {
			status = HttpStatus.EXPECTATION_FAILED;
			result = null;
		} else if (authId == koalibeeId) {
			status = HttpStatus.OK;
			result = this.koalibeeService.getUnpublished(koalibeeId);
		} else {
			status = HttpStatus.UNAUTHORIZED;
			result = null;
		}

		return ResponseEntity.status(status).body(result);

	}

}
