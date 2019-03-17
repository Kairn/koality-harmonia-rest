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

import io.esoma.khr.model.Track;
import io.esoma.khr.service.AuthService;
import io.esoma.khr.service.KoalibeeServiceTest;
import io.esoma.khr.service.TrackService;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TrackControllerTest {

	private static List<Track> trackList;

	private TrackController trackController;

	@Mock
	private AuthService authService;
	@Mock
	private TrackService trackService;

	{
		this.trackController = new TrackController();

		// Initialize mock objects.
		MockitoAnnotations.initMocks(KoalibeeServiceTest.class);
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		trackList = new ArrayList<Track>();
		trackList.add(new Track(1));
		trackList.add(new Track(3));
		trackList.add(new Track(9));

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
		when(this.authService.reauthenticate("2j")).thenReturn(2);

		when(this.trackService.getOne(anyInt(), anyInt())).thenReturn(null);
		when(this.trackService.addOne(anyInt(), anyInt(), anyString())).thenReturn(0);
		when(this.trackService.delete(anyInt(), anyInt())).thenReturn(false);
		when(this.trackService.getAll()).thenReturn(trackList);
		when(this.trackService.getFromAlbum(anyInt(), anyInt())).thenReturn(new ArrayList<Track>());

		this.trackController.setAuthService(this.authService);
		this.trackController.setTrackService(this.trackService);

	}

	@Test
	public void testSetAuthService() throws Exception {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testSetTrackService() throws Exception {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testGetTrackEx() throws Exception {

		ResponseEntity<Track> result = this.trackController.getTrack(1, "exj");

		assertEquals(417, result.getStatusCodeValue());

		assertEquals(0, result.getBody().getTrackId());

	}

	@Test
	public void testGetTrackIn() throws Exception {

		ResponseEntity<Track> result = this.trackController.getTrack(1, "ivj");

		assertEquals(401, result.getStatusCodeValue());

		assertEquals(0, result.getBody().getTrackId());

	}

	@Test
	public void testGetTrackN() throws Exception {

		ResponseEntity<Track> result = this.trackController.getTrack(1, "adj");

		assertEquals(404, result.getStatusCodeValue());

		assertEquals(0, result.getBody().getTrackId());

	}

	@Test
	public void testGetTrackS() throws Exception {

		when(this.trackService.getOne(1, 1)).thenReturn(new Track(1));

		ResponseEntity<Track> result = this.trackController.getTrack(1, "1j");

		assertEquals(200, result.getStatusCodeValue());

		assertEquals(1, result.getBody().getTrackId());

	}

	@Test
	public void testAddTrackToAlbumEx() throws Exception {

		ResponseEntity<String> result = this.trackController.addTrackToAlbum(1, "data", "exj");

		assertEquals(417, result.getStatusCodeValue());

		assertEquals(ExceptionController.AUTH_TOKEN_EXPIRED, result.getBody());

	}

	@Test
	public void testAddTrackToAlbumIn() throws Exception {

		ResponseEntity<String> result = this.trackController.addTrackToAlbum(1, "data", "ivj");

		assertEquals(401, result.getStatusCodeValue());

		assertEquals(ExceptionController.UNAUTHORIZED, result.getBody());

	}

	@Test
	public void testAddTrackToAlbumF() throws Exception {

		ResponseEntity<String> result = this.trackController.addTrackToAlbum(1, "data", "1j");

		assertEquals(422, result.getStatusCodeValue());

		assertEquals("unable to add the track", result.getBody());

	}

	@Test
	public void testAddTrackToAlbumS() throws Exception {

		when(this.trackService.addOne(1, 1, "good")).thenReturn(1);

		ResponseEntity<String> result = this.trackController.addTrackToAlbum(1, "good", "1j");

		assertEquals(201, result.getStatusCodeValue());

		assertEquals("new track has been successfully added", result.getBody());

	}

	@Test
	public void testDeleteTrackFromAlbumEx() throws Exception {

		ResponseEntity<String> result = this.trackController.deleteTrackFromAlbum(1, "exj");

		assertEquals(417, result.getStatusCodeValue());

		assertEquals(ExceptionController.AUTH_TOKEN_EXPIRED, result.getBody());

	}

	@Test
	public void testDeleteTrackFromAlbumIn() throws Exception {

		ResponseEntity<String> result = this.trackController.deleteTrackFromAlbum(1, "ivj");

		assertEquals(401, result.getStatusCodeValue());

		assertEquals(ExceptionController.UNAUTHORIZED, result.getBody());

	}

	@Test
	public void testDeleteTrackFromAlbumF() throws Exception {

		ResponseEntity<String> result = this.trackController.deleteTrackFromAlbum(1, "2j");

		assertEquals(422, result.getStatusCodeValue());

		assertEquals("unable to delete track or track does not exist", result.getBody());

	}

	@Test
	public void testDeleteTrackFromAlbumS() throws Exception {

		when(this.trackService.delete(2, 1)).thenReturn(true);

		ResponseEntity<String> result = this.trackController.deleteTrackFromAlbum(1, "2j");

		assertEquals(200, result.getStatusCodeValue());

		assertEquals("track has been successfully deleted", result.getBody());

	}

	@Test
	public void testListAllTracksUA() throws Exception {

		ResponseEntity<List<Track>> result = this.trackController.listAllTracks("bad");

		assertEquals(401, result.getStatusCodeValue());

		assertEquals(0, result.getBody().size());

	}

	@Test
	public void testListAllTracksS() throws Exception {

		ResponseEntity<List<Track>> result = this.trackController.listAllTracks("adj");

		assertEquals(200, result.getStatusCodeValue());

		assertEquals(3, result.getBody().size());

	}

	@Test
	public void testGetTracksFromAlbumEx() throws Exception {

		ResponseEntity<List<Track>> result = this.trackController.getTracksFromAlbum(1, "exj");

		assertEquals(417, result.getStatusCodeValue());

		assertEquals(0, result.getBody().size());

	}

	@Test
	public void testGetTracksFromAlbumIn() throws Exception {

		ResponseEntity<List<Track>> result = this.trackController.getTracksFromAlbum(1, "ivj");

		assertEquals(401, result.getStatusCodeValue());

		assertEquals(0, result.getBody().size());

	}

	@Test
	public void testGetTracksFromAlbumNF() throws Exception {

		ResponseEntity<List<Track>> result = this.trackController.getTracksFromAlbum(1, "2j");

		assertEquals(404, result.getStatusCodeValue());

		assertEquals(0, result.getBody().size());

	}

	@Test
	public void testGetTracksFromAlbumS() throws Exception {

		when(this.trackService.getFromAlbum(2, 7)).thenReturn(trackList);

		ResponseEntity<List<Track>> result = this.trackController.getTracksFromAlbum(7, "2j");

		assertEquals(200, result.getStatusCodeValue());

		assertEquals(3, result.getBody().size());

	}

}
