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
import io.esoma.khr.service.AlbumService;
import io.esoma.khr.service.AuthService;

/**
 * 
 * The controller class that handles HTTP requests concerning album
 * functionalities and interactions.
 * 
 * @author Eddy Soma
 *
 */
@RestController(value = "albumController")
@RequestMapping(path = "/album")
public class AlbumController {

	private AuthService authService;
	private AlbumService albumService;

	@Autowired
	@Qualifier(value = "authService")
	public void setAuthService(AuthService authService) {
		this.authService = authService;
	}

	@Autowired
	@Qualifier(value = "albumService")
	public void setAlbumService(AlbumService albumService) {
		this.albumService = albumService;
	}

	/**
	 * 
	 * Handles a HTTP request of retrieving a specific album data. The sender must
	 * be a registered koalibee or a system administrator in order to access this
	 * method. Unpublished albums can only be fetched by their creators.
	 * 
	 * @param albumId the ID of the album.
	 * @param jws     the signed authentication token.
	 * @return the plain album object without track information. A blank object is
	 *         returned if the ID does not exist or if the user does not have proper
	 *         authorization.
	 */
	@GetMapping(path = "/get/{albumId}")
	public ResponseEntity<Album> getAlbum(@Validated @PathVariable int albumId,
			@Validated @RequestHeader(name = "Auth-Token") String jws) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		Album result = new Album();

		boolean valid = false;

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		if (authId == -1) {
			status = HttpStatus.EXPECTATION_FAILED;
		} else if (authId == 0) {
			status = HttpStatus.UNAUTHORIZED;
		} else {
			valid = true;
		}

		if (valid) {
			// Fetch album.
			result = this.albumService.getOne(albumId);

			// Verify access.
			if (result != null) {
				if (result.getIsPublished().equals("T")) {
					status = HttpStatus.OK;
				} else {
					if (authId == -777 || authId == result.getKoalibee().getKoalibeeId()) {
						status = HttpStatus.OK;
					} else {
						status = HttpStatus.UNAUTHORIZED;
						result = new Album();
					}
				}
			} else {
				status = HttpStatus.NOT_FOUND;
				result = new Album();
			}
		}

		return ResponseEntity.status(status).body(result);

	}

	/**
	 * 
	 * Handles a HTTP request that attempts to create a new album under a koalibee's
	 * name. An authentication token must be present in order for the request to be
	 * processed.
	 * 
	 * @param koalibeeId the ID of the creator.
	 * @param albumData  the JSON string containing the album information.
	 * @param jws        the signed authentication token.
	 * @return a general message indicating success or failure.
	 */
	@PostMapping(path = "/create/{koalibeeId}")
	public ResponseEntity<String> createAlbum(@Validated @PathVariable int koalibeeId,
			@Validated @RequestBody String albumData, @Validated @RequestHeader(name = "Auth-Token") String jws) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		String result = new String();

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		if (authId == -1) {
			status = HttpStatus.EXPECTATION_FAILED;
			result = "authentication token expired";
		} else if (authId == koalibeeId) {
			if (this.albumService.create(koalibeeId, albumData) > 0) {
				status = HttpStatus.CREATED;
				result = "new album created successfully";
			} else {
				status = HttpStatus.UNPROCESSABLE_ENTITY;
				result = "failed to create album";
			}
		} else {
			status = HttpStatus.UNAUTHORIZED;
			result = "not authorized";
		}

		return ResponseEntity.status(status).body(result);

	}

	/**
	 * 
	 * Responds to a HTTP request that attempts to update information on an
	 * unpublished album. It can only be accessed by the creator.
	 * 
	 * @param albumId   the ID of the album to be updated.
	 * @param albumData the JSON string containing the updated information.
	 * @param jws       the signed authentication token.
	 * @return a general message indicating success or failure.
	 */
	@PutMapping(path = "/update/{albumId}")
	public ResponseEntity<String> updateAlbumInformation(@Validated @PathVariable int albumId,
			@Validated @RequestBody String albumData, @Validated @RequestHeader(name = "Auth-Token") String jws) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		String result = new String();

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		if (authId == -1) {
			status = HttpStatus.EXPECTATION_FAILED;
			result = "authentication token expired";
		} else if (authId > 0) {
			if (this.albumService.update(authId, albumId, albumData)) {
				status = HttpStatus.OK;
				result = "album has been successfully updated";
			} else {
				status = HttpStatus.UNPROCESSABLE_ENTITY;
				result = "failed to update the album, or album ID does not exist";
			}
		} else {
			status = HttpStatus.UNAUTHORIZED;
			result = "not authorized";
		}

		return ResponseEntity.status(status).body(result);

	}

	/**
	 * 
	 * Responds to a HTTP request that attempts to delete an album from the
	 * database. A user is only allowed to delete unpublished albums he/she created,
	 * but a system administrator can delete any album.
	 * 
	 * @param albumId the ID of the album.
	 * @param jws     the signed authentication token.
	 * @return a general message indicating success or failure.
	 */
	@DeleteMapping(path = "/delete/{albumId}")
	public ResponseEntity<String> deleteAlbum(@Validated @PathVariable int albumId,
			@Validated @RequestHeader(name = "Auth-Token") String jws) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		String result = new String();

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		if (authId == -1) {
			status = HttpStatus.EXPECTATION_FAILED;
			result = "authentication token expired";
		} else if (authId == 0) {
			status = HttpStatus.UNAUTHORIZED;
			result = "not authorized";
		} else {
			if (this.albumService.delete(authId, albumId)) {
				status = HttpStatus.OK;
				result = "album has been successfully deleted";
			} else {
				status = HttpStatus.UNPROCESSABLE_ENTITY;
				result = "failed to delete the album or the ID does not exist";
			}
		}

		return ResponseEntity.status(status).body(result);

	}

	/**
	 * 
	 * Responds to a HTTP request that attempts to publish an album. An
	 * authentication token is required in order to verify the identity of the
	 * publisher.
	 * 
	 * @param albumId   the ID of the album.
	 * @param albumData the JSON string containing the additional information for a
	 *                  published album.
	 * @param jws       the signed authentication token.
	 * @return a general message indicating success or failure.
	 */
	@PostMapping(path = "/publish/{albumId}")
	public ResponseEntity<String> publishAlbum(@Validated @PathVariable int albumId,
			@Validated @RequestBody String albumData, @Validated @RequestHeader(name = "Auth-Token") String jws) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		String result = new String();

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		if (authId == -1) {
			status = HttpStatus.EXPECTATION_FAILED;
			result = "authentication token expired";
		} else if (authId > 0) {
			if (this.albumService.publish(authId, albumId, albumData)) {
				status = HttpStatus.OK;
				result = "album has been successfully published";
			} else {
				status = HttpStatus.UNPROCESSABLE_ENTITY;
				result = "failed to publish album";
			}
		} else {
			status = HttpStatus.UNAUTHORIZED;
			result = "not authorized";
		}

		return ResponseEntity.status(status).body(result);

	}

	/**
	 * 
	 * Responds to a HTTP request that attempts to promote an album. Only the
	 * original creator can use this method to promote a published album.
	 * 
	 * @param albumId the ID of the album to be promoted.
	 * @param jws     the signed authentication token.
	 * @return a general message indicating success or failure.
	 */
	@PostMapping(path = "/promote/{albumId}")
	public ResponseEntity<String> promoteAlbum(@Validated @PathVariable int albumId,
			@Validated @RequestHeader(name = "Auth-Token") String jws) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		String result = new String();

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		if (authId == -1) {
			status = HttpStatus.EXPECTATION_FAILED;
			result = "authentication token expired";
		} else if (authId > 0) {
			if (this.albumService.promote(authId, albumId)) {
				status = HttpStatus.OK;
				result = "album has been successfully promoted";
			} else {
				status = HttpStatus.UNPROCESSABLE_ENTITY;
				result = "failed to promote album";
			}
		} else {
			status = HttpStatus.UNAUTHORIZED;
			result = "not authorized";
		}

		return ResponseEntity.status(status).body(result);

	}

	/**
	 * 
	 * Handles a HTTP request of retrieving all albums in the database. An
	 * authentication token from a system administrator is required.
	 * 
	 * @param jws the signed authentication token.
	 * @return the list containing albums' data. An empty list is returned if the
	 *         sender is not authorized or if the database does not have any album.
	 */
	@GetMapping(path = "/get/all")
	public ResponseEntity<List<Album>> listAllAlbums(@Validated @RequestHeader(name = "Auth-Token") String jws) {

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		if (authId == -777) {
			return ResponseEntity.ok(this.albumService.getAll());
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<Album>());
		}

	}

	/**
	 * 
	 * Responds to a HTTP request of retrieving all published albums. An
	 * authentication token from a registered koalibee is required in order to view
	 * the list.
	 * 
	 * @param jws the signed authentication token.
	 * @return the album list. An empty list is returned if no album is found or if
	 *         the sender has not logged in.
	 */
	@GetMapping(path = "/get/published")
	public ResponseEntity<List<Album>> getAllPublications(@Validated @RequestHeader(name = "Auth-Token") String jws) {

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		if (authId == -777 || authId > 0) {
			return ResponseEntity.ok(this.albumService.getPublished());
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<Album>());
		}

	}

}
