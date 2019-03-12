package io.esoma.khr.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExceptionControllerTest {

	private ExceptionController exceptionController;

	{
		this.exceptionController = new ExceptionController();
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testHandleMissingHeader() throws Exception {

		final MissingRequestHeaderException mockException = new MissingRequestHeaderException("Auth-Token",
				mock(MethodParameter.class));

		ResponseEntity<String> result = this.exceptionController.handleMissingHeader(mockException);

		assertEquals(400, result.getStatusCodeValue());

		assertEquals("This request is missing <Auth-Token> header element, authentication may be required.",
				result.getBody());

	}

	@Test
	public void testHandleArgumentTypeMismatch() throws Exception {

		final MethodArgumentTypeMismatchException mockException = new MethodArgumentTypeMismatchException(
				mock(Object.class), int.class, "koalibeeId", mock(MethodParameter.class), mock(Throwable.class));

		ResponseEntity<String> result = this.exceptionController.handleArgumentTypeMismatch(mockException);

		assertEquals(400, result.getStatusCodeValue());

		assertEquals(
				"The url path cannot be processed due to a type mismatch for the parameter <koalibeeId>, required type is <int>.",
				result.getBody());

	}

	@Test
	public void testHandleException() throws Exception {

		ResponseEntity<String> result = this.exceptionController.handleException(new Exception());

		assertEquals(400, result.getStatusCodeValue());

		assertEquals(
				"The server has encountered an unknown error, please contact the server maintainer if you have questions.",
				result.getBody());

	}

}
