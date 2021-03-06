package io.esoma.khr.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import io.esoma.khr.utility.LogUtility;

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

	// General error message.
	public static final String AUTH_TOKEN_EXPIRED = "authentication token has expired, please login again.";
	public static final String UNAUTHORIZED = "invalid authentication token, or access is restricted";

	/**
	 * 
	 * Handles exceptions occurred due to the request URL is missing a required path
	 * variable.
	 * 
	 * @param e the exception thrown.
	 * @return an error message reporting on the missing path variable.
	 */
	@ExceptionHandler(value = MissingPathVariableException.class)
	public ResponseEntity<String> handleMissingPathVariable(MissingPathVariableException e) {

		final String missingPathMessage = String.format("This request is missing <%s> path variable.",
				e.getVariableName());

		return ResponseEntity.badRequest().body(missingPathMessage);

	}

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
	 * Handles exceptions occurred due to the request body is missing or cannot be
	 * converted into the appropriate type.
	 * 
	 * @param e the exception thrown.
	 * @return an error message reporting on the unreadable request body.
	 */
	public ResponseEntity<String> handleBadRequestBody(HttpMessageNotReadableException e) {

		final String badBodyMessage = "This request is missing a request body, or the request body is incompatible with the type <java.lang.String>";

		return ResponseEntity.badRequest().body(badBodyMessage);

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
		LogUtility.MASTER_LOGGER.fatal("Unexpected controller exception, stack trace:", e);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);

	}

}
