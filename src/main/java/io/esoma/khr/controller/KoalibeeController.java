package io.esoma.khr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

		// Authenticate the jws.
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

}
