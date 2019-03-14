package io.esoma.khr.controller;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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

import io.esoma.khr.service.AlbumService;
import io.esoma.khr.service.AuthService;
import io.esoma.khr.service.KoalibeeServiceTest;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AlbumControllerTest {

	private AlbumController albumController;

	@Mock
	private AuthService authService;
	@Mock
	private AlbumService albumService;

	{
		this.albumController = new AlbumController();

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

		when(this.albumService.getOne(anyInt())).thenReturn(null);
		when(this.albumService.create(anyInt(), anyString())).thenReturn(0);
		when(this.albumService.update(anyInt(), anyInt(), anyString())).thenReturn(false);
		when(this.albumService.delete(anyInt(), anyInt())).thenReturn(false);

		this.albumController.setAuthService(this.authService);
		this.albumController.setAlbumService(this.albumService);

	}

	@Test
	public void testSetAuthService() throws Exception {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testSetAlbumService() throws Exception {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testGetAlbum() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testCreateAlbum() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testUpdateAlbumInformation() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testDeleteAlbum() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testPublishAlbum() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testPromoteAlbum() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testListAllAlbums() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testGetAllPublications() throws Exception {
		throw new RuntimeException("not yet implemented");
	}

}
