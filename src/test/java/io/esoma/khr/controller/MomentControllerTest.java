package io.esoma.khr.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import io.esoma.khr.model.Moment;
import io.esoma.khr.service.AuthService;
import io.esoma.khr.service.KoalibeeServiceTest;
import io.esoma.khr.service.MomentService;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MomentControllerTest {

	private MomentController momentController;

	@Mock
	private AuthService authService;
	@Mock
	private MomentService momentService;

	{
		this.momentController = new MomentController();

		// Initialize mock objects.
		MockitoAnnotations.initMocks(KoalibeeServiceTest.class);
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// No content yet.
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// No content yet.
	}

	@Before
	public void setUp() throws Exception {

		when(this.authService.reauthenticate(anyString())).thenReturn(0);
		when(this.authService.reauthenticate("exj")).thenReturn(-1);
		when(this.authService.reauthenticate("adj")).thenReturn(-777);

		when(this.momentService.getOne(anyInt())).thenReturn(null);
		when(this.momentService.getByDate(anyString())).thenReturn(new ArrayList<Moment>());
		when(this.momentService.postOne(anyInt(), anyString())).thenReturn(0);
		when(this.momentService.delete(anyInt())).thenReturn(false);

		this.momentController.setAuthService(this.authService);
		this.momentController.setMomentService(this.momentService);

	}

	@Test
	public void testSetAuthService() throws Exception {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testSetMomentService() throws Exception {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testGetMomentN() throws Exception {

		ResponseEntity<Moment> result = this.momentController.getMoment(1);

		assertEquals(404, result.getStatusCodeValue());

		assertEquals(new Moment(), result.getBody());

	}

	@Test
	public void testGetMomentS() throws Exception {

		final Moment moment = new Moment(2);

		when(this.momentService.getOne(2)).thenReturn(moment);

		ResponseEntity<Moment> result = this.momentController.getMoment(2);

		assertEquals(200, result.getStatusCodeValue());

		assertEquals(moment, result.getBody());

	}

	@Test
	public void testPostMomentEx() throws Exception {

		ResponseEntity<String> result = this.momentController.postMoment(1, "data", "exj");

		assertEquals(417, result.getStatusCodeValue());

		assertEquals(ExceptionController.AUTH_TOKEN_EXPIRED, result.getBody());

	}

	@Test
	public void testPostMomentIn() throws Exception {

		ResponseEntity<String> result = this.momentController.postMoment(1, "data", "ivj");

		assertEquals(401, result.getStatusCodeValue());

		assertEquals(ExceptionController.UNAUTHORIZED, result.getBody());

	}

	@Test
	public void testPostMomentF() throws Exception {

		when(this.authService.reauthenticate("1")).thenReturn(1);
		when(this.momentService.postOne(1, "bad")).thenReturn(0);

		ResponseEntity<String> result = this.momentController.postMoment(1, "bad", "1");

		assertEquals(422, result.getStatusCodeValue());

		assertEquals("failed to post moment", result.getBody());

	}

	@Test
	public void testPostMomentS() throws Exception {

		when(this.authService.reauthenticate("1")).thenReturn(1);
		when(this.momentService.postOne(1, "good")).thenReturn(1);

		ResponseEntity<String> result = this.momentController.postMoment(1, "good", "1");

		assertEquals(201, result.getStatusCodeValue());

		assertEquals("new moment successfully posted", result.getBody());

	}

	@Test
	public void testGetAllPostedMomentsChrono() throws Exception {

		final List<Moment> momentList = new ArrayList<>();
		momentList.add(new Moment(1));
		momentList.add(new Moment(3));

		when(this.momentService.getAll()).thenReturn(momentList);

		ResponseEntity<List<Moment>> result = this.momentController.getAllPostedMomentsChrono();

		assertEquals(200, result.getStatusCodeValue());

		assertEquals(2, result.getBody().size());

	}

	@Test
	public void testFindMomentsByDate() throws Exception {

		final List<Moment> momentList = new ArrayList<>();
		momentList.add(new Moment(2));
		momentList.add(new Moment(5));
		momentList.add(new Moment(6));

		when(this.momentService.getByDate("2018-06-09")).thenReturn(momentList);

		ResponseEntity<List<Moment>> result = this.momentController.findMomentsByDate("2018-06-09");

		assertEquals(200, result.getStatusCodeValue());

		assertEquals(3, result.getBody().size());

	}

	@Test
	public void testDeleteMomentEx() throws Exception {

		ResponseEntity<String> result = this.momentController.deleteMoment(1, "exj");

		assertEquals(417, result.getStatusCodeValue());

		assertEquals(ExceptionController.AUTH_TOKEN_EXPIRED, result.getBody());

	}

	@Test
	public void testDeleteMomentUA() throws Exception {

		ResponseEntity<String> result = this.momentController.deleteMoment(1, "ivj");

		assertEquals(401, result.getStatusCodeValue());

		assertEquals(ExceptionController.UNAUTHORIZED, result.getBody());

	}

	@Test
	public void testDeleteMomentF() throws Exception {

		ResponseEntity<String> result = this.momentController.deleteMoment(1, "adj");

		assertEquals(404, result.getStatusCodeValue());

		assertEquals("moment not found or unable to delete", result.getBody());

	}

	@Test
	public void testDeleteMomentS() throws Exception {

		when(this.momentService.delete(7)).thenReturn(true);

		ResponseEntity<String> result = this.momentController.deleteMoment(7, "adj");

		assertEquals(200, result.getStatusCodeValue());

		assertEquals("moment successfully deleted", result.getBody());

	}

}
