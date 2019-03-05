package io.esoma.khr.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AuthServiceTest {

	private static String ADMIN_PASSWORD;

	private AuthService authService;

	{
		this.authService = new AuthService();
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ADMIN_PASSWORD = System.getenv("MY_PASSWORDS").split(";@")[0];
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// No content yet.
	}

	public void testSetToExpire() throws Exception {

		this.authService.setToExpire();

		assertEquals(-1, this.authService.reauthenticate("some expired token"));

	}

	@Test
	public void testAuthenticateAdminV() throws Exception {

		final Map<String, String> authData = new HashMap<>();
		authData.put("email", "admin");
		authData.put("password", ADMIN_PASSWORD);

		assertEquals(3, this.authService.authenticate(authData).split("\\.").length);

	}

	@Test
	public void testAuthenticateAdminN() throws Exception {

		final Map<String, String> authData = new HashMap<>();
		authData.put("email", "admin");
		authData.put("password", "wrong password");

		assertEquals("unauthorized admin", this.authService.authenticate(authData));

	}

	@Test
	public void testAuthenticateBad() throws Exception {

		final Map<String, String> authData = new HashMap<>();
		authData.put("email", "invalid email");
		authData.put("password", "123456");

		assertEquals("bad request", this.authService.authenticate(authData));

	}

	@Test
	public void testAuthenticateKoalibeeV() throws Exception {

		final Map<String, String> authData = new HashMap<>();
		authData.put("koalibeeId", "4");
		authData.put("email", "my.email@koalibee.com");
		authData.put("password", "rxh");
		authData.put("passwordSalt", "1234");
		authData.put("passwordHash", "110066E9AF477CDD58A678E33CD4BD19FAAF3AF90A691D3AD981791CBAEE85CB");

		assertEquals(3, this.authService.authenticate(authData).split("\\.").length);

	}

	@Test
	public void testAuthenticateKoalibeeU() throws Exception {

		final Map<String, String> authData = new HashMap<>();

		assertEquals("unknown error", this.authService.authenticate(authData));

	}

	@Test
	public void testAuthenticateKoalibeeN() throws Exception {

		final Map<String, String> authData = new HashMap<>();
		authData.put("email", "to.being@koalibee.com");
		authData.put("password", "tiff");
		authData.put("passwordSalt", "2232");
		authData.put("passwordHash", "6BAF874E281E434682BB7962E5989CC7204B6256C4059DA39D1E97DFF495F9CC");

		assertEquals("unauthorized koalibee", this.authService.authenticate(authData));

	}

	@Test
	public void testReauthenticateA() throws Exception {

		final Map<String, String> authData = new HashMap<>();
		authData.put("email", "admin");
		authData.put("password", ADMIN_PASSWORD);

		String jws = this.authService.authenticate(authData);

		assertEquals(-777, this.authService.reauthenticate(jws));

	}

	@Test
	public void testReauthenticateK() throws Exception {

		final Map<String, String> authData = new HashMap<>();
		authData.put("koalibeeId", "5");
		authData.put("email", "my.email@koalibee.com");
		authData.put("password", "rxh");
		authData.put("passwordSalt", "1234");
		authData.put("passwordHash", "110066E9AF477CDD58A678E33CD4BD19FAAF3AF90A691D3AD981791CBAEE85CB");

		String jws = this.authService.authenticate(authData);

		assertEquals(5, this.authService.reauthenticate(jws));

	}

	@Test
	public void testReauthenticateN() throws Exception {

		assertEquals(0, this.authService.reauthenticate("invalid jws"));

	}

	@Test
	public void testReauthenticateExpK() throws Exception {

		final Map<String, String> authData = new HashMap<>();
		authData.put("koalibeeId", "5");
		authData.put("email", "my.email@koalibee.com");
		authData.put("password", "rxh");
		authData.put("passwordSalt", "1234");
		authData.put("passwordHash", "110066E9AF477CDD58A678E33CD4BD19FAAF3AF90A691D3AD981791CBAEE85CB");

		String jws = this.authService.authenticate(authData);

		this.authService.setToExpire();

		assertEquals(-1, this.authService.reauthenticate(jws));

	}

	@Test
	public void testReauthenticateExpA() throws Exception {

		final Map<String, String> authData = new HashMap<>();
		authData.put("email", "admin");
		authData.put("password", ADMIN_PASSWORD);

		String jws = this.authService.authenticate(authData);

		this.authService.setToExpire();

		assertNotEquals(-777, this.authService.reauthenticate(jws));

	}

}
