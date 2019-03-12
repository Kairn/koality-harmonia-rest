package io.esoma.khr.controller;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

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
	public void testGetMoment() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testPostMoment() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testGetAllPostedMomentsChrono() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testFindMomentsByDate() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testDeleteMoment() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

}
