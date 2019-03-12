package io.esoma.khr.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import io.esoma.khr.model.Album;
import io.esoma.khr.model.Koalibee;
import io.esoma.khr.service.AuthService;
import io.esoma.khr.service.KoalibeeService;
import io.esoma.khr.service.KoalibeeServiceTest;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class KoalibeeControllerTest {

	private KoalibeeController koalibeeController;

	@Mock
	private AuthService authService;
	@Mock
	private KoalibeeService koalibeeService;

	{
		this.koalibeeController = new KoalibeeController();

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

		when(this.koalibeeService.getOne(anyInt())).thenReturn(null);
		when(this.koalibeeService.getOne(1)).thenReturn(new Koalibee(1));
		when(this.koalibeeService.updateInformation(anyInt(), anyString())).thenReturn(false);
		when(this.koalibeeService.updateCredentials(anyInt(), anyString())).thenReturn(false);
		when(this.koalibeeService.purchaseAlbum(anyInt(), anyString())).thenReturn(false);
		when(this.koalibeeService.delete(anyInt())).thenReturn(false);

		this.koalibeeController.setAuthService(this.authService);
		this.koalibeeController.setKoalibeeService(this.koalibeeService);

	}

	@Test
	public void testSetAuthService() throws Exception {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testSetKoalibeeService() throws Exception {
		// Intentionally left blank.
		assertTrue(true);
	}

	@Test
	public void testRegisterKoalibeeBQ() throws Exception {

		final String badRequest = "bq";

		when(this.koalibeeService.register(badRequest)).thenReturn(null);

		ResponseEntity<String> result = this.koalibeeController.registerKoalibee(badRequest);

		assertEquals(400, result.getStatusCodeValue());

		assertEquals("bad request, unknown reason", result.getBody());

	}

	@Test
	public void testRegisterKoalibeeBD() throws Exception {

		final String badData = "bd";

		when(this.koalibeeService.register(badData)).thenReturn("bad");

		ResponseEntity<String> result = this.koalibeeController.registerKoalibee(badData);

		assertEquals(422, result.getStatusCodeValue());

		assertEquals("bad", result.getBody());

	}

	@Test
	public void testRegisterKoalibeeS() throws Exception {

		final String goodData = "gd";

		when(this.koalibeeService.register(goodData)).thenReturn("a.good.jws");

		ResponseEntity<String> result = this.koalibeeController.registerKoalibee(goodData);

		assertEquals(201, result.getStatusCodeValue());

		assertEquals("a.good.jws", result.getBody());

	}

	@Test
	public void testLoginKoalibeeBQ() throws Exception {

		final String badRequest = "bq";

		when(this.koalibeeService.login(badRequest)).thenReturn(null);

		ResponseEntity<String> result = this.koalibeeController.loginKoalibee(badRequest);

		assertEquals(400, result.getStatusCodeValue());

		assertEquals("bad request, unknown reason", result.getBody());

	}

	@Test
	public void testLoginKoalibeeBD() throws Exception {

		final String badData = "bd";

		when(this.koalibeeService.login(badData)).thenReturn("bad");

		ResponseEntity<String> result = this.koalibeeController.loginKoalibee(badData);

		assertEquals(401, result.getStatusCodeValue());

		assertEquals("bad", result.getBody());

	}

	@Test
	public void testLoginKoalibeeS() throws Exception {

		final String goodData = "gd";

		when(this.koalibeeService.login(goodData)).thenReturn("a.good.jws");

		ResponseEntity<String> result = this.koalibeeController.loginKoalibee(goodData);

		assertEquals(200, result.getStatusCodeValue());

		assertEquals("a.good.jws", result.getBody());

	}

	@Test
	public void testLogout() throws Exception {

		Mockito.doNothing().when(this.authService).setToExpire();

		ResponseEntity<String> result = this.koalibeeController.logout();

		assertEquals(200, result.getStatusCodeValue());

		assertEquals("successfully logged out", result.getBody());

	}

	@Test
	public void testGetKoalibeeEx() throws Exception {

		final String expiredJws = "exj";

		when(this.authService.reauthenticate(expiredJws)).thenReturn(-1);

		ResponseEntity<Koalibee> result = this.koalibeeController.getKoalibee(1, expiredJws);

		assertEquals(417, result.getStatusCodeValue());

		assertEquals(0, result.getBody().getKoalibeeId());

	}

	@Test
	public void testGetKoalibeeIn() throws Exception {

		final String invalidJws = "ivj";

		when(this.authService.reauthenticate(invalidJws)).thenReturn(0);

		ResponseEntity<Koalibee> result = this.koalibeeController.getKoalibee(1, invalidJws);

		assertEquals(401, result.getStatusCodeValue());

		assertEquals(0, result.getBody().getKoalibeeId());

	}

	@Test
	public void testGetKoalibeeNF() throws Exception {

		final String adminJws = "adj";

		when(this.authService.reauthenticate(adminJws)).thenReturn(-777);

		ResponseEntity<Koalibee> result = this.koalibeeController.getKoalibee(5, adminJws);

		assertEquals(404, result.getStatusCodeValue());

		assertEquals(0, result.getBody().getKoalibeeId());

	}

	@Test
	public void testGetKoalibeeS() throws Exception {

		final String koalibeeJws = "koj";

		when(this.authService.reauthenticate(koalibeeJws)).thenReturn(1);

		ResponseEntity<Koalibee> result = this.koalibeeController.getKoalibee(1, koalibeeJws);

		assertEquals(200, result.getStatusCodeValue());

		assertEquals(1, result.getBody().getKoalibeeId());

	}

	@Test
	public void testUpdateKoalibeeEx() throws Exception {

		final String expiredJws = "exj";

		when(this.authService.reauthenticate(expiredJws)).thenReturn(-1);

		ResponseEntity<String> result = this.koalibeeController.updateKoalibee(1, "data", expiredJws);

		assertEquals(417, result.getStatusCodeValue());

		assertEquals("authentication token expired", result.getBody());

	}

	@Test
	public void testUpdateKoalibeeIn() throws Exception {

		final String invalidJws = "ivj";

		when(this.authService.reauthenticate(invalidJws)).thenReturn(0);

		ResponseEntity<String> result = this.koalibeeController.updateKoalibee(1, "data", invalidJws);

		assertEquals(401, result.getStatusCodeValue());

		assertEquals("not authorized", result.getBody());

	}

	@Test
	public void testUpdateKoalibeeF() throws Exception {

		final String koalibeeJws = "koj";

		when(this.authService.reauthenticate(koalibeeJws)).thenReturn(1);

		ResponseEntity<String> result = this.koalibeeController.updateKoalibee(1, "data", koalibeeJws);

		assertEquals(422, result.getStatusCodeValue());

		assertEquals("failed to update", result.getBody());

	}

	@Test
	public void testUpdateKoalibeeS() throws Exception {

		final String koalibeeJws = "koj";

		when(this.authService.reauthenticate(koalibeeJws)).thenReturn(1);

		when(this.koalibeeService.updateInformation(1, "good data")).thenReturn(true);

		ResponseEntity<String> result = this.koalibeeController.updateKoalibee(1, "good data", koalibeeJws);

		assertEquals(200, result.getStatusCodeValue());

		assertEquals("profile updated successfully", result.getBody());

	}

	@Test
	public void testChangeKoalibeeCredentialsEx() throws Exception {

		final String expiredJws = "exj";

		when(this.authService.reauthenticate(expiredJws)).thenReturn(-1);

		ResponseEntity<String> result = this.koalibeeController.changeKoalibeeCredentials(1, "data", expiredJws);

		assertEquals(417, result.getStatusCodeValue());

		assertEquals("authentication token expired", result.getBody());

	}

	@Test
	public void testChangeKoalibeeCredentialsIn() throws Exception {

		final String invalidJws = "ivj";

		when(this.authService.reauthenticate(invalidJws)).thenReturn(0);

		ResponseEntity<String> result = this.koalibeeController.changeKoalibeeCredentials(1, "data", invalidJws);

		assertEquals(401, result.getStatusCodeValue());

		assertEquals("not authorized", result.getBody());

	}

	@Test
	public void testChangeKoalibeeCredentialsF() throws Exception {

		final String koalibeeJws = "koj";

		when(this.authService.reauthenticate(koalibeeJws)).thenReturn(1);

		ResponseEntity<String> result = this.koalibeeController.changeKoalibeeCredentials(1, "data", koalibeeJws);

		assertEquals(422, result.getStatusCodeValue());

		assertEquals("failed to change credentials", result.getBody());

	}

	@Test
	public void testChangeKoalibeeCredentialsS() throws Exception {

		final String koalibeeJws = "koj";

		when(this.authService.reauthenticate(koalibeeJws)).thenReturn(1);

		when(this.koalibeeService.updateCredentials(1, "good creds")).thenReturn(true);

		ResponseEntity<String> result = this.koalibeeController.changeKoalibeeCredentials(1, "good creds", koalibeeJws);

		assertEquals(200, result.getStatusCodeValue());

		assertEquals("credentials changed successfully", result.getBody());

	}

	@Test
	public void testPurchaseAlbumForKoalibeeEx() throws Exception {

		final String expiredJws = "exj";

		when(this.authService.reauthenticate(expiredJws)).thenReturn(-1);

		ResponseEntity<String> result = this.koalibeeController.purchaseAlbumForKoalibee(1, "data", expiredJws);

		assertEquals(417, result.getStatusCodeValue());

		assertEquals("authentication token expired", result.getBody());

	}

	@Test
	public void testPurchaseAlbumForKoalibeeIn() throws Exception {

		final String invalidJws = "ivj";

		when(this.authService.reauthenticate(invalidJws)).thenReturn(0);

		ResponseEntity<String> result = this.koalibeeController.purchaseAlbumForKoalibee(1, "data", invalidJws);

		assertEquals(401, result.getStatusCodeValue());

		assertEquals("not authorized", result.getBody());

	}

	@Test
	public void testPurchaseAlbumForKoalibeeF() throws Exception {

		final String koalibeeJws = "koj";

		when(this.authService.reauthenticate(koalibeeJws)).thenReturn(1);

		ResponseEntity<String> result = this.koalibeeController.purchaseAlbumForKoalibee(1, "data", koalibeeJws);

		assertEquals(422, result.getStatusCodeValue());

		assertEquals("failed to make the purchase", result.getBody());

	}

	@Test
	public void testPurchaseAlbumForKoalibeeS() throws Exception {

		final String koalibeeJws = "koj";

		when(this.authService.reauthenticate(koalibeeJws)).thenReturn(1);

		when(this.koalibeeService.purchaseAlbum(1, "album")).thenReturn(true);

		ResponseEntity<String> result = this.koalibeeController.purchaseAlbumForKoalibee(1, "album", koalibeeJws);

		assertEquals(200, result.getStatusCodeValue());

		assertEquals("album purchased successfully", result.getBody());

	}

	@Test
	public void testDeleteKoalibeeAccountEx() throws Exception {

		final String expiredJws = "exj";

		when(this.authService.reauthenticate(expiredJws)).thenReturn(-1);

		ResponseEntity<String> result = this.koalibeeController.deleteKoalibeeAccount(1, expiredJws);

		assertEquals(417, result.getStatusCodeValue());

		assertEquals("authentication token expired", result.getBody());

	}

	@Test
	public void testDeleteKoalibeeAccountUA() throws Exception {

		final String unauthedJws = "uaj";

		when(this.authService.reauthenticate(unauthedJws)).thenReturn(1);

		ResponseEntity<String> result = this.koalibeeController.deleteKoalibeeAccount(1, unauthedJws);

		assertEquals(401, result.getStatusCodeValue());

		assertEquals("administrator privilege required", result.getBody());

	}

	@Test
	public void testDeleteKoalibeeAccountNF() throws Exception {

		final String adminJws = "adj";

		when(this.authService.reauthenticate(adminJws)).thenReturn(-777);

		ResponseEntity<String> result = this.koalibeeController.deleteKoalibeeAccount(2, adminJws);

		assertEquals(404, result.getStatusCodeValue());

		assertEquals("failed to delete koalibee account", result.getBody());

	}

	@Test
	public void testDeleteKoalibeeAccountS() throws Exception {

		final String adminJws = "adj";

		when(this.authService.reauthenticate(adminJws)).thenReturn(-777);

		when(this.koalibeeService.delete(7)).thenReturn(true);

		ResponseEntity<String> result = this.koalibeeController.deleteKoalibeeAccount(7, adminJws);

		assertEquals(200, result.getStatusCodeValue());

		assertEquals("koalibee account deleted successfully", result.getBody());

	}

	@Test
	public void testListAllKoalibeesEx() throws Exception {

		final String expiredJws = "exj";

		when(this.authService.reauthenticate(expiredJws)).thenReturn(-1);

		ResponseEntity<List<Koalibee>> result = this.koalibeeController.listAllKoalibees(expiredJws);

		assertEquals(417, result.getStatusCodeValue());

		assertNull(result.getBody());

	}

	@Test
	public void testListAllKoalibeesUA() throws Exception {

		final String unauthedJws = "uaj";

		when(this.authService.reauthenticate(unauthedJws)).thenReturn(1);

		ResponseEntity<List<Koalibee>> result = this.koalibeeController.listAllKoalibees(unauthedJws);

		assertEquals(401, result.getStatusCodeValue());

		assertNull(result.getBody());

	}

	@Test
	public void testListAllKoalibeesS() throws Exception {

		final String adminJws = "adj";

		when(this.authService.reauthenticate(adminJws)).thenReturn(-777);

		when(this.koalibeeService.getAll()).thenReturn(new ArrayList<Koalibee>());

		ResponseEntity<List<Koalibee>> result = this.koalibeeController.listAllKoalibees(adminJws);

		assertEquals(200, result.getStatusCodeValue());

		assertTrue(result.getBody() instanceof ArrayList);

	}

	@Test
	public void testGetKoalibeeInventoryEx() throws Exception {

		final String expiredJws = "exj";

		when(this.authService.reauthenticate(expiredJws)).thenReturn(-1);

		ResponseEntity<List<Album>> result = this.koalibeeController.getKoalibeeInventory(1, expiredJws);

		assertEquals(417, result.getStatusCodeValue());

		assertNull(result.getBody());

	}

	@Test
	public void testGetKoalibeeInventoryUA() throws Exception {

		final String invalidJws = "ivj";

		when(this.authService.reauthenticate(invalidJws)).thenReturn(0);

		ResponseEntity<List<Album>> result = this.koalibeeController.getKoalibeeInventory(1, invalidJws);

		assertEquals(401, result.getStatusCodeValue());

		assertNull(result.getBody());

	}

	@Test
	public void testGetKoalibeeInventoryS() throws Exception {

		final String koalibeeJws = "koj";

		when(this.authService.reauthenticate(koalibeeJws)).thenReturn(1);

		when(this.koalibeeService.getInventory(1)).thenReturn(new ArrayList<Album>());

		ResponseEntity<List<Album>> result = this.koalibeeController.getKoalibeeInventory(1, koalibeeJws);

		assertEquals(200, result.getStatusCodeValue());

		assertTrue(result.getBody() instanceof ArrayList);

	}

	@Test
	public void testGetKoalibeeCreationsEx() throws Exception {

		final String expiredJws = "exj";

		when(this.authService.reauthenticate(expiredJws)).thenReturn(-1);

		ResponseEntity<List<Album>> result = this.koalibeeController.getKoalibeeCreations(1, expiredJws);

		assertEquals(417, result.getStatusCodeValue());

		assertNull(result.getBody());

	}

	@Test
	public void testGetKoalibeeCreationsUA() throws Exception {

		final String invalidJws = "ivj";

		when(this.authService.reauthenticate(invalidJws)).thenReturn(0);

		ResponseEntity<List<Album>> result = this.koalibeeController.getKoalibeeCreations(1, invalidJws);

		assertEquals(401, result.getStatusCodeValue());

		assertNull(result.getBody());

	}

	@Test
	public void testGetKoalibeeCreationsS() throws Exception {

		final String koalibeeJws = "koj";

		when(this.authService.reauthenticate(koalibeeJws)).thenReturn(1);

		when(this.koalibeeService.getUnpublished(1)).thenReturn(new ArrayList<Album>());

		ResponseEntity<List<Album>> result = this.koalibeeController.getKoalibeeCreations(1, koalibeeJws);

		assertEquals(200, result.getStatusCodeValue());

		assertTrue(result.getBody() instanceof ArrayList);

	}

}
