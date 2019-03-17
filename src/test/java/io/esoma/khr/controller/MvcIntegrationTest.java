package io.esoma.khr.controller;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import io.esoma.khr.service.AuthService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("file:src/main/webapp/WEB-INF")
@ContextHierarchy({ @ContextConfiguration("file:src/main/webapp/WEB-INF/spring-context.xml"),
		@ContextConfiguration("file:src/main/webapp/WEB-INF/spring-webmvc.xml"), })
public class MvcIntegrationTest {

	private static AuthService authService;

	private WebApplicationContext wac;

	private MockMvc mvc;

	private KoalibeeController koalibeeController;
	private MomentController momentController;
	private AlbumController albumController;
	private TrackController trackController;
	private ReviewController reviewController;

	@Autowired
	public void setWac(WebApplicationContext wac) {
		this.wac = wac;
	}

	@Autowired
	@Qualifier(value = "koalibeeController")
	public void setKoalibeeController(KoalibeeController koalibeeController) {
		this.koalibeeController = koalibeeController;
	}

	@Autowired
	@Qualifier(value = "momentController")
	public void setMomentController(MomentController momentController) {
		this.momentController = momentController;
	}

	@Autowired
	@Qualifier(value = "albumController")
	public void setAlbumController(AlbumController albumController) {
		this.albumController = albumController;
	}

	@Autowired
	@Qualifier(value = "trackController")
	public void setTrackController(TrackController trackController) {
		this.trackController = trackController;
	}

	@Autowired
	@Qualifier(value = "reviewController")
	public void setReviewController(ReviewController reviewController) {
		this.reviewController = reviewController;
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		authService = mock(AuthService.class);
		when(authService.reauthenticate("adj")).thenReturn(-777);

	}

	@Before
	public void setUp() throws Exception {

		this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		this.koalibeeController.setAuthService(authService);
		this.momentController.setAuthService(authService);
		this.albumController.setAuthService(authService);
		this.trackController.setAuthService(authService);
		this.reviewController.setAuthService(authService);

	}

	@Test
	public void testGetFirstMoment() throws Exception {

		MvcResult result = this.mvc.perform(get("/moment/get/1").accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk()).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("Welcome to Koality Harmonia!"));

	}

	@Test
	public void testGetFirstKoalibee() throws Exception {

		MvcResult result = this.mvc
				.perform(get("/koalibee/get/1").accept(MediaType.APPLICATION_JSON_UTF8).header("Auth-Token", "adj"))
				.andExpect(status().isOk()).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("Eddy"));

		assertTrue(result.getResponse().getContentAsString().contains("Soma"));

	}

	@Test
	public void testGetFirstAlbum() throws Exception {

		MvcResult result = this.mvc
				.perform(get("/album/get/1").accept(MediaType.APPLICATION_JSON_UTF8).header("Auth-Token", "adj"))
				.andExpect(status().isOk()).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("Études d'exécution transcendante"));

	}

	@Test
	public void testGetFirstReview() throws Exception {

		MvcResult result = this.mvc.perform(get("/review/get/1").accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk()).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("Wonderful composition, thanks for sharing!"));

	}

	@Test
	public void testGetAllTracks() throws Exception {

		MvcResult result = this.mvc
				.perform(get("/track/get/all").accept(MediaType.APPLICATION_JSON_UTF8).header("Auth-Token", "adj"))
				.andExpect(status().isOk()).andReturn();

		assertTrue(result.getResponse().getContentAsString().contains("No. 2 (Fusées)"));

		assertTrue(result.getResponse().getContentAsString().contains("No. 8 Sunshine"));

		assertTrue(result.getResponse().getContentAsString().contains("No. 1 Aeolian Harp"));

	}

}
