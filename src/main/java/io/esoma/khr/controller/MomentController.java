package io.esoma.khr.controller;

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

import io.esoma.khr.model.Moment;
import io.esoma.khr.service.AuthService;
import io.esoma.khr.service.MomentService;

/**
 * 
 * The controller class that handles HTTP requests concerning moment
 * functionalities and interactions.
 * 
 * @author Eddy Soma
 *
 */
@RestController(value = "momentController")
@RequestMapping(path = "/moment")
public class MomentController {

	private AuthService authService;
	private MomentService momentService;

	@Autowired
	@Qualifier(value = "authService")
	public void setAuthService(AuthService authService) {
		this.authService = authService;
	}

	@Autowired
	@Qualifier(value = "momentService")
	public void setMomentService(MomentService momentService) {
		this.momentService = momentService;
	}

	/**
	 * 
	 * Responds to a HTTP request of retrieving a moment by its ID. It can be
	 * accessed publicly without an authentication token.
	 * 
	 * @param momentId the ID of the moment.
	 * @return the fetched moment object. An empty object is returned if the ID does
	 *         not exist.
	 */
	@GetMapping(path = "/get/{momentId}")
	public ResponseEntity<Moment> getMoment(@Validated @PathVariable int momentId) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		Moment moment = this.momentService.getOne(momentId);

		if (moment != null) {
			status = HttpStatus.OK;
		} else {
			status = HttpStatus.NOT_FOUND;
			moment = new Moment();
		}

		return ResponseEntity.status(status).body(moment);

	}

	/**
	 * 
	 * Responds to a HTTP request that attempts to post a moment under a koalibee's
	 * name for the current day. An authentication token is required.
	 * 
	 * @param koalibeeId the ID of the poster.
	 * @param momentData the JSON string containing the post message.
	 * @param jws        the signed authentication token.
	 * @return a general message indicating success or failure.
	 */
	@PostMapping(path = "/post/{koalibeeId}")
	public ResponseEntity<String> postMoment(@Validated @PathVariable int koalibeeId,
			@Validated @RequestBody String momentData, @Validated @RequestHeader(name = "Auth-Token") String jws) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		String result = new String();

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		if (authId == -1) {
			status = HttpStatus.EXPECTATION_FAILED;
			result = "authentication token expired";
		} else if (authId == koalibeeId) {
			if (this.momentService.postOne(koalibeeId, momentData) > 0) {
				status = HttpStatus.CREATED;
				result = "new moment successfully posted";
			} else {
				status = HttpStatus.UNPROCESSABLE_ENTITY;
				result = "failed to post moment";
			}
		} else {
			status = HttpStatus.UNAUTHORIZED;
			result = "not authorized";
		}

		return ResponseEntity.status(status).body(result);

	}

	/**
	 * 
	 * Responds to a HTTP request of retrieving all posted moments in a
	 * chronological order (latest first). It can be accessed publicly.
	 * 
	 * @return the list containing all moments in the database. An empty list is
	 *         returned if there are no posted moments.
	 */
	@GetMapping(path = "/get/all")
	public ResponseEntity<List<Moment>> getAllPostedMomentsChrono() {

		return ResponseEntity.ok(this.momentService.getAll());

	}

	/**
	 * 
	 * Responds to a HTTP request of retrieving only moments posted on a specific
	 * day. It can be accessed publicly.
	 * 
	 * @param dateData the JSON string containing an ISO formatted date string.
	 * @return the list of all fetched moments. An empty list is returned if no
	 *         moments are found or if the date cannot be parsed.
	 */
	@PostMapping(path = "/find/bydate")
	public ResponseEntity<List<Moment>> findMomentsByDate(@Validated @RequestBody String dateData) {

		return ResponseEntity.ok(this.momentService.getByDate(dateData));

	}

	/**
	 * 
	 * Responds to a HTTP request that attempts to delete a moment from the database
	 * permanently. Its access requires an authentication token of a system
	 * administrator.
	 * 
	 * @param momentId the ID of the moment to be deleted.
	 * @param jws      the signed authentication token.
	 * @return a general message indicating success or failure.
	 */
	@DeleteMapping(path = "/delete/{momentId}")
	public ResponseEntity<String> deleteMoment(@Validated @PathVariable int momentId,
			@Validated @RequestHeader(name = "Auth-Token") String jws) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		String result = new String();

		// Validate the JWS.
		int authId = this.authService.reauthenticate(jws);

		if (authId == -1) {
			status = HttpStatus.EXPECTATION_FAILED;
			result = "authentication token expired";
		} else if (authId == -777) {
			if (this.momentService.delete(momentId)) {
				status = HttpStatus.OK;
				result = "moment successfully deleted";
			} else {
				status = HttpStatus.NOT_FOUND;
				result = "moment not found or unable to delete";
			}
		} else {
			status = HttpStatus.UNAUTHORIZED;
			result = "administrator privilege required";
		}

		return ResponseEntity.status(status).body(result);

	}

}
