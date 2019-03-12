package io.esoma.khr.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 
 * The controller class that provides global exception handling methods for all
 * other controllers.
 * 
 * @author Eddy Soma
 *
 */
@RestControllerAdvice(basePackages = "io.esoma.khr.controller")
public class ExceptionController {

	/**
	 * 
	 * Handles exceptions occurred due to the request is missing required header
	 * (likely Auth-Token).
	 * 
	 * @param e the exception thrown.
	 * @return an error message reporting on the missing header.
	 */
	@ExceptionHandler(value = MissingRequestHeaderException.class)
	public ResponseEntity<String> handleMissingHeader(MissingRequestHeaderException e) {

		final String missingHeaderMessage = String.format(
				"This request is missing <%s> header element, authentication may be required.", e.getHeaderName());

		return ResponseEntity.badRequest().body(missingHeaderMessage);

	}

	/**
	 * 
	 * Handles exceptions occurred due to the URL containing illegal path variables.
	 * 
	 * @param e the exception thrown.
	 * @return an error message reporting on the type mismatch.
	 */
	@ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
	public ResponseEntity<String> handleArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {

		final String typeMismatchMessage = String.format(
				"The url path cannot be processed due to a type mismatch for the parameter <%s>, required type is <%s>.",
				e.getName(), e.getRequiredType().getName());

		return ResponseEntity.badRequest().body(typeMismatchMessage);

	}

	/**
	 * 
	 * A generic method to handle all other exceptions thrown by the controller
	 * methods.
	 * 
	 * @param e the exception thrown.
	 * @return a generic error message with a bad request status code.
	 */
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<String> handleException(Exception e) {

		final String errorMessage = "The server has encountered an unknown error, please contact the server maintainer if you have questions.";

		// Debug message
		System.out.println("exception encountered, type: " + e.getClass().getName());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);

	}

}
