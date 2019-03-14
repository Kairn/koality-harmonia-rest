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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.esoma.khr.model.Track;
import io.esoma.khr.service.AuthService;
import io.esoma.khr.service.TrackService;

/**
 * 
 * The controller class that handles HTTP requests concerning track
 * functionalities and interactions.
 * 
 * @author Eddy Soma
 *
 */
@RestController(value = "trackController")
@RequestMapping(path = "/track")
public class TrackController {

	private AuthService authService;
	private TrackService trackService;

	@Autowired
	@Qualifier(value = "authService")
	public void setAuthService(AuthService authService) {
		this.authService = authService;
	}

	@Autowired
	@Qualifier(value = "trackService")
	public void setTrackService(TrackService trackService) {
		this.trackService = trackService;
	}

	/**
	 * 
	 * Responds to a HTTP request of retrieving a track with full audio data. An
	 * authentication token is required in order to verify ownership of the track.
	 * 
	 * @param trackId the ID of the track.
	 * @param jws     the signed authentication token.
	 * @return the track object with audio data. A blank object is returned is the
	 *         ID is not found or if the user does not have access.
	 */
	@GetMapping(path = "/get/{trackId}")
	public ResponseEntity<Track> getTrack(@Validated @PathVariable int trackId,
			@Validated @RequestHeader(name = "Auth-Token") String jws) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		Track track = new Track();

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		if (authId == -1) {
			status = HttpStatus.EXPECTATION_FAILED;
		} else if (authId == 0) {
			status = HttpStatus.UNAUTHORIZED;
		} else {
			track = this.trackService.getOne(authId, trackId);
			if (track == null) {
				status = HttpStatus.NOT_FOUND;
				track = new Track();
			} else {
				status = HttpStatus.OK;
			}
		}

		return ResponseEntity.status(status).body(track);

	}

	/**
	 * 
	 * Responds to a HTTP request that attempts to add a new track to an unpublished
	 * album. An authentication token is required to verify the ownership of the
	 * album.
	 * 
	 * @param albumId   the ID of the album to add track to.
	 * @param trackData the JSON string containing track information and audio data
	 *                  url.
	 * @param jws       the signed authentication token.
	 * @return a general message indicating success or failure.
	 */
	@PostMapping(path = "/addto/{albumId}")
	public ResponseEntity<String> addTrackToAlbum(@Validated @PathVariable int albumId,
			@Validated @RequestBody String trackData, @Validated @RequestHeader(name = "Auth-Token") String jws) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		String result = new String();

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		if (authId == -1) {
			status = HttpStatus.EXPECTATION_FAILED;
			result = "authentication token expired";
		} else if (authId > 0) {
			if (this.trackService.addOne(authId, albumId, trackData) > 0) {
				status = HttpStatus.CREATED;
				result = "new track has been successfully added";
			} else {
				status = HttpStatus.UNPROCESSABLE_ENTITY;
				result = "unable to add the track";
			}
		} else {
			status = HttpStatus.UNAUTHORIZED;
			result = "not authorized";
		}

		return ResponseEntity.status(status).body(result);

	}

	/**
	 * 
	 * Responds to a HTTP request that attempts to delete a track. An album creator
	 * is allowed to delete tracks from his/her own unpublished albums. A system
	 * administrator can delete any track from the database.
	 * 
	 * @param trackId the ID of the track.
	 * @param jws     the signed authentication token.
	 * @return a general message indicating success or failure.
	 */
	@DeleteMapping(path = "delete/{trackId}")
	public ResponseEntity<String> deleteTrackFromAlbum(@Validated @PathVariable int trackId,
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
			if (this.trackService.delete(authId, trackId)) {
				status = HttpStatus.OK;
				result = "track has been successfully deleted";
			} else {
				status = HttpStatus.UNPROCESSABLE_ENTITY;
				result = "unable to delete track or track does not exist";
			}
		}

		return ResponseEntity.status(status).body(result);

	}

	/**
	 * 
	 * Responds to a HTTP request of retrieving all tracks from the database. An
	 * authentication token from a system administrator is required.
	 * 
	 * @param jws the signed authentication token.
	 * @return the list containing all tracks' information. Audio data will be
	 *         omitted. An empty list is returned if the sender is not authorized.
	 */
	@GetMapping(path = "/get/all")
	public ResponseEntity<List<Track>> listAllTracks(@Validated @RequestHeader(name = "Auth-Token") String jws) {

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		if (authId == -777) {
			return ResponseEntity.ok(this.trackService.getAll());
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<Track>());
		}

	}

	/**
	 * 
	 * Responds to a HTTP request of retrieving all tracks from an album without
	 * audio data. Tracks from published albums can be viewed publicly, but those
	 * that are not published can only be viewed by the creator.
	 * 
	 * @param albumId the ID of the album.
	 * @param jws     the signed authentication token.
	 * @return the list containing the fetched tracks. An empty list is returned if
	 *         the sender cannot be authenticated or if the album does not exist.
	 */
	@GetMapping(path = "/inalbum/{albumId}")
	public ResponseEntity<List<Track>> getTracksFromAlbum(@Validated @PathVariable int albumId,
			@Validated @RequestHeader(name = "Auth-Token") String jws) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		List<Track> result = new ArrayList<>();

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		if (authId == -1) {
			status = HttpStatus.EXPECTATION_FAILED;
		} else if (authId == 0) {
			status = HttpStatus.UNAUTHORIZED;
		} else {
			result = this.trackService.getFromAlbum(authId, albumId);
			if (result.size() > 0) {
				status = HttpStatus.OK;
			} else {
				status = HttpStatus.NOT_FOUND;
			}
		}

		return ResponseEntity.status(status).body(result);

	}

}
