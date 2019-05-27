package io.esoma.khr.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

import io.esoma.khr.model.Album;
import io.esoma.khr.model.Koalibee;
import io.esoma.khr.service.AlbumService;
import io.esoma.khr.service.AuthService;
import io.esoma.khr.service.KoalibeeServiceTest;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AlbumControllerTest {

	private static List<Album> albumList;

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

		albumList = new ArrayList<Album>();
		albumList.add(new Album(1));
		albumList.add(new Album(5));
		albumList.add(new Album(7));
		albumList.add(new Album(8));

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
		when(this.authService.reauthenticate("1j")).thenReturn(1);
		when(this.authService.reauthenticate("14j")).thenReturn(14);

		when(this.albumService.getOne(anyInt())).thenReturn(null);
		when(this.albumService.create(anyInt(), anyString())).thenReturn(0);
		when(this.albumService.update(anyInt(), anyInt(), anyString())).thenReturn(false);
		when(this.albumService.delete(anyInt(), anyInt())).thenReturn(false);
		when(this.albumService.publish(anyInt(), anyInt(), anyString())).thenReturn(false);
		when(this.albumService.promote(anyInt(), anyInt())).thenReturn(false);
		when(this.albumService.getAll()).thenReturn(new ArrayList<>(albumList));
		when(this.albumService.getPublished()).thenReturn(new ArrayList<>(albumList));

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
	public void testGetAlbumEx() throws Exception {

		ResponseEntity<Album> result = this.albumController.getAlbum(1, "exj");

		assertEquals(417, result.getStatusCodeValue());

		assertEquals(new Album(), result.getBody());

	}

	@Test
	public void testGetAlbumN() throws Exception {

		ResponseEntity<Album> result = this.albumController.getAlbum(1, "ivj");

		assertEquals(401, result.getStatusCodeValue());

		assertEquals(new Album(), result.getBody());

	}

	@Test
	public void testGetAlbumNF() throws Exception {

		ResponseEntity<Album> result = this.albumController.getAlbum(1, "adj");

		assertEquals(404, result.getStatusCodeValue());

		assertEquals(new Album(), result.getBody());

	}

	@Test
	public void testGetAlbumUA() throws Exception {

		final Koalibee koalibee = new Koalibee(6);

		final Album album = new Album(6);
		album.setIsPublished("F");
		album.setKoalibee(koalibee);

		when(this.albumService.getOne(6)).thenReturn(album);

		ResponseEntity<Album> result = this.albumController.getAlbum(6, "1j");

		assertEquals(401, result.getStatusCodeValue());

		assertEquals(new Album(), result.getBody());

	}

	@Test
	public void testGetAlbumPS() throws Exception {

		final Album album = new Album(3);
		album.setIsPublished("T");

		when(this.albumService.getOne(3)).thenReturn(album);

		ResponseEntity<Album> result = this.albumController.getAlbum(3, "1j");

		assertEquals(200, result.getStatusCodeValue());

		assertEquals(album, result.getBody());

	}

	@Test
	public void testGetAlbumS() throws Exception {

		final Koalibee koalibee = new Koalibee(6);

		final Album album = new Album(6);
		album.setIsPublished("F");
		album.setKoalibee(koalibee);

		when(this.authService.reauthenticate("6j")).thenReturn(6);
		when(this.albumService.getOne(6)).thenReturn(album);

		ResponseEntity<Album> result = this.albumController.getAlbum(6, "6j");

		assertEquals(200, result.getStatusCodeValue());

		assertEquals(album, result.getBody());

	}

	@Test
	public void testCreateAlbumEx() throws Exception {

		ResponseEntity<String> result = this.albumController.createAlbum(1, "data", "exj");

		assertEquals(417, result.getStatusCodeValue());

		assertEquals(ExceptionController.AUTH_TOKEN_EXPIRED, result.getBody());

	}

	@Test
	public void testCreateAlbumIn() throws Exception {

		ResponseEntity<String> result = this.albumController.createAlbum(1, "data", "ivj");

		assertEquals(401, result.getStatusCodeValue());

		assertEquals(ExceptionController.UNAUTHORIZED, result.getBody());

	}

	@Test
	public void testCreateAlbumF() throws Exception {

		ResponseEntity<String> result = this.albumController.createAlbum(1, "data", "1j");

		assertEquals(422, result.getStatusCodeValue());

		assertEquals("failed to create album", result.getBody());

	}

	@Test
	public void testCreateAlbumS() throws Exception {

		when(this.albumService.create(1, "good data")).thenReturn(1);

		ResponseEntity<String> result = this.albumController.createAlbum(1, "good data", "1j");

		assertEquals(201, result.getStatusCodeValue());

		assertEquals("new album created successfully", result.getBody());

	}

	@Test
	public void testUpdateAlbumInformationEx() throws Exception {

		ResponseEntity<String> result = this.albumController.updateAlbumInformation(1, "data", "exj");

		assertEquals(417, result.getStatusCodeValue());

		assertEquals(ExceptionController.AUTH_TOKEN_EXPIRED, result.getBody());

	}

	@Test
	public void testUpdateAlbumInformationIn() throws Exception {

		ResponseEntity<String> result = this.albumController.updateAlbumInformation(1, "data", "ivj");

		assertEquals(401, result.getStatusCodeValue());

		assertEquals(ExceptionController.UNAUTHORIZED, result.getBody());

	}

	@Test
	public void testUpdateAlbumInformationF() throws Exception {

		ResponseEntity<String> result = this.albumController.updateAlbumInformation(1, "data", "1j");

		assertEquals(422, result.getStatusCodeValue());

		assertEquals("failed to update the album, or album ID does not exist", result.getBody());

	}

	@Test
	public void testUpdateAlbumInformationS() throws Exception {

		when(this.albumService.update(1, 1, "good data")).thenReturn(true);

		ResponseEntity<String> result = this.albumController.updateAlbumInformation(1, "good data", "1j");

		assertEquals(200, result.getStatusCodeValue());

		assertEquals("album has been successfully updated", result.getBody());

	}

	@Test
	public void testDeleteAlbumEx() throws Exception {

		ResponseEntity<String> result = this.albumController.deleteAlbum(1, "exj");

		assertEquals(417, result.getStatusCodeValue());

		assertEquals(ExceptionController.AUTH_TOKEN_EXPIRED, result.getBody());

	}

	@Test
	public void testDeleteAlbumIn() throws Exception {

		ResponseEntity<String> result = this.albumController.deleteAlbum(1, "ivj");

		assertEquals(401, result.getStatusCodeValue());

		assertEquals(ExceptionController.UNAUTHORIZED, result.getBody());

	}

	@Test
	public void testDeleteAlbumF() throws Exception {

		ResponseEntity<String> result = this.albumController.deleteAlbum(1, "1j");

		assertEquals(422, result.getStatusCodeValue());

		assertEquals("failed to delete the album or the ID does not exist", result.getBody());

	}

	@Test
	public void testDeleteAlbumS() throws Exception {

		when(this.albumService.delete(eq(-777), anyInt())).thenReturn(true);

		ResponseEntity<String> result = this.albumController.deleteAlbum(5, "adj");

		assertEquals(200, result.getStatusCodeValue());

		assertEquals("album has been successfully deleted", result.getBody());

	}

	@Test
	public void testPublishAlbumEx() throws Exception {

		ResponseEntity<String> result = this.albumController.publishAlbum(1, "good data", "exj");

		assertEquals(417, result.getStatusCodeValue());

		assertEquals(ExceptionController.AUTH_TOKEN_EXPIRED, result.getBody());

	}

	@Test
	public void testPublishAlbumIn() throws Exception {

		ResponseEntity<String> result = this.albumController.publishAlbum(1, "good data", "ivj");

		assertEquals(401, result.getStatusCodeValue());

		assertEquals(ExceptionController.UNAUTHORIZED, result.getBody());

	}

	@Test
	public void testPublishAlbumF() throws Exception {

		ResponseEntity<String> result = this.albumController.publishAlbum(1, "good data", "1j");

		assertEquals(422, result.getStatusCodeValue());

		assertEquals("failed to publish album", result.getBody());

	}

	@Test
	public void testPublishAlbumS() throws Exception {

		when(this.albumService.publish(1, 1, "good data")).thenReturn(true);

		ResponseEntity<String> result = this.albumController.publishAlbum(1, "good data", "1j");

		assertEquals(200, result.getStatusCodeValue());

		assertEquals("album has been successfully published", result.getBody());

	}

	@Test
	public void testPromoteAlbumEx() throws Exception {

		ResponseEntity<String> result = this.albumController.promoteAlbum(1, "exj");

		assertEquals(417, result.getStatusCodeValue());

		assertEquals(ExceptionController.AUTH_TOKEN_EXPIRED, result.getBody());

	}

	@Test
	public void testPromoteAlbumIn() throws Exception {

		ResponseEntity<String> result = this.albumController.promoteAlbum(1, "ivj");

		assertEquals(401, result.getStatusCodeValue());

		assertEquals(ExceptionController.UNAUTHORIZED, result.getBody());

	}

	@Test
	public void testPromoteAlbumF() throws Exception {

		ResponseEntity<String> result = this.albumController.promoteAlbum(1, "1j");

		assertEquals(422, result.getStatusCodeValue());

		assertEquals("failed to promote album", result.getBody());

	}

	@Test
	public void testPromoteAlbumS() throws Exception {

		when(this.authService.reauthenticate("2j")).thenReturn(2);
		when(this.albumService.promote(2, 2)).thenReturn(true);

		ResponseEntity<String> result = this.albumController.promoteAlbum(2, "2j");

		assertEquals(200, result.getStatusCodeValue());

		assertEquals("album has been successfully promoted", result.getBody());

	}

	@Test
	public void testListAllAlbumsUA() throws Exception {

		ResponseEntity<List<Album>> result = this.albumController.listAllAlbums("ivj");

		assertEquals(401, result.getStatusCodeValue());

		assertEquals(0, result.getBody().size());

	}

	@Test
	public void testListAllAlbumsS() throws Exception {

		ResponseEntity<List<Album>> result = this.albumController.listAllAlbums("adj");

		assertEquals(200, result.getStatusCodeValue());

		assertEquals(4, result.getBody().size());

	}

	@Test
	public void testGetAllPublicationsUA() throws Exception {

		ResponseEntity<List<Album>> result = this.albumController.getAllPublications("exj");

		assertEquals(401, result.getStatusCodeValue());

		assertEquals(0, result.getBody().size());

	}

	@Test
	public void testGetAllPublicationsPS() throws Exception {

		ResponseEntity<List<Album>> result = this.albumController.getAllPublications("1j");

		assertEquals(200, result.getStatusCodeValue());

		assertEquals(4, result.getBody().size());

	}

	@Test
	public void testGetAllPublicationsNPS() throws Exception {

		ResponseEntity<List<Album>> result = this.albumController.getAllPublications("14j");

		assertEquals(200, result.getStatusCodeValue());

		assertEquals(3, result.getBody().size());

	}

}
