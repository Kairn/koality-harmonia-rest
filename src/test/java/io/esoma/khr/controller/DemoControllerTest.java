package io.esoma.khr.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

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
import io.esoma.khr.service.AlbumService;
import io.esoma.khr.service.KoalibeeServiceTest;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DemoControllerTest {

	private DemoController demoController;

	@Mock
	private AlbumService albumService;

	{
		this.demoController = new DemoController();

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

		when(this.albumService.getOne(anyInt())).thenAnswer(i -> new Album(i.getArgument(0)));

		this.demoController.setAlbumService(this.albumService);

	}

	@Test
	public void testSetAlbumService() throws Exception {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testGetDemoAlbums() throws Exception {

		ResponseEntity<List<Album>> result = this.demoController.getDemoAlbums();

		assertEquals(200, result.getStatusCodeValue());

		assertEquals(DemoController.demoAlbums.length, result.getBody().size());

		for (int i : DemoController.demoAlbums) {
			assertTrue(result.getBody().contains(new Album(i)));
		}

	}

}
