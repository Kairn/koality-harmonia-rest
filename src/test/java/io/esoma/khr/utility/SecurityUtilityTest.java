package io.esoma.khr.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Base64;

import javax.crypto.SecretKey;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;

public class SecurityUtilityTest {

	private static final String secret = "sfuh8JufwhuDEWfhwuefh23EWhucwieP";
	private static SecretKey testKey;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testKey = Keys.hmacShaKeyFor(secret.getBytes());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// No content yet.
	}

	@Test
	public void testGetPasswordSaltStandard() throws Exception {

		assertNotNull(SecurityUtility.getPasswordSaltStandard());

		assertEquals(4, SecurityUtility.getPasswordSaltStandard().length());

	}

	@Test
	public void testGetPasswordSaltFlexibleLength1() throws Exception {

		assertNotNull(SecurityUtility.getPasswordSaltStandard());

		assertEquals(8, SecurityUtility.getPasswordSaltFlexibleLength(8).length());

	}

	@Test
	public void testGetPasswordSaltFlexibleLength2() throws Exception {

		assertNotNull(SecurityUtility.getPasswordSaltStandard());

		assertEquals(20, SecurityUtility.getPasswordSaltFlexibleLength(20).length());

	}

	@Test
	public void testGetSHA256Digest1() throws Exception {

		final String source = "my awesome test";

		assertEquals("4A267D7E1092445AD84D59668C0BB7CE14C5108F5EFD6D5B0923A66AE1F77AB2",
				SecurityUtility.getSHA256Digest(source));

	}

	@Test
	public void testGetSHA256Digest2() throws Exception {

		final String source = "my really awesome test";

		assertEquals("D21919579A20CBB6DFF468E88F153A44DBEFE7F0235E8DCCF4966E0BBDFF673A",
				SecurityUtility.getSHA256Digest(source));

	}

	@Test
	public void testGetSHA256Digest3() throws Exception {

		final String source = "the most awesome test ever";

		assertEquals("75CBD08DDA7EFA080A50F971C3C07093C196CE7413D88E0343F4E13830D65B5A",
				SecurityUtility.getSHA256Digest(source));

	}

	@Test
	public void testIsValidPasswordY1() throws Exception {

		final String validPassword = "super secret";
		final String salt = "1234";

		assertTrue(SecurityUtility.isValidPassword(validPassword, salt,
				"85A229B2DF222B37718CA2C8D0971C6641B87878261BA6684C94BC6CC772F466"));

	}

	@Test
	public void testIsValidPasswordN1() throws Exception {

		final String invalidPassword = "SUPER secret";
		final String salt = "1234";

		assertFalse(SecurityUtility.isValidPassword(invalidPassword, salt,
				"85A229B2DF222B37718CA2C8D0971C6641B87878261BA6684C94BC6CC772F466"));

	}

	@Test
	public void testIsValidPasswordY2() throws Exception {

		final String validPassword = "genius";
		final String salt = "aSg2";

		assertTrue(SecurityUtility.isValidPassword(validPassword, salt,
				"CEDC02E9FD32E3440B78B52FB10E82D57BC92A88EA76211C4D220296E95318B0"));

	}

	@Test
	public void testIsValidPasswordN2() throws Exception {

		final String invalidPassword = "no good Man";
		final String salt = "tDD4";

		assertFalse(SecurityUtility.isValidPassword(invalidPassword, salt,
				"A3B7AB19B83E6AFA9D26A5E914211A2C6D261EE23A2C0DD6D5046A09C48C4925"));

	}

	@Test
	public void testBuildAuthJws1() throws Exception {

		final int id = 1;
		final String email = "john@mailinator.com";

		final String jws = SecurityUtility.buildAuthJws(id, email, testKey);

		assertNotNull(jws);

		assertEquals(3, jws.split("\\.").length);

		String header = new String(Base64.getDecoder().decode(jws.split("\\.")[0]), "UTF-8");
		String payload = new String(Base64.getDecoder().decode(jws.split("\\.")[1]), "UTF-8");

		assertTrue(header.contains("HS256"));
		assertTrue(payload.contains("\"koalibeeId\":1"));
		assertTrue(payload.contains("@mailinator"));

	}

	@Test
	public void testBuildAuthJws2() throws Exception {

		final int id = 76;
		final String email = "myKoality@mailinator.com";

		final String jws = SecurityUtility.buildAuthJws(id, email, testKey);

		assertNotNull(jws);

		assertEquals(3, jws.split("\\.").length);

		String header = new String(Base64.getDecoder().decode(jws.split("\\.")[0]), "UTF-8");
		String payload = new String(Base64.getDecoder().decode(jws.split("\\.")[1]), "UTF-8");

		assertTrue(header.contains("HS256"));
		assertTrue(payload.contains("\"koalibeeId\":76"));
		assertTrue(payload.contains("myKoality"));

	}

	@Test
	public void testBuildAuthJws3() throws Exception {

		final int id = 999;
		final String email = "test@koality.com";

		final String jws = SecurityUtility.buildAuthJws(id, email, testKey);

		assertNotNull(jws);

		assertEquals(3, jws.split("\\.").length);

		String header = new String(Base64.getDecoder().decode(jws.split("\\.")[0]), "UTF-8");
		String payload = new String(Base64.getDecoder().decode(jws.split("\\.")[1]), "UTF-8");

		assertTrue(header.contains("alg"));
		assertTrue(payload.contains("999"));
		assertTrue(payload.contains("\"email\":\"test@koality.com\""));
		assertTrue(payload.contains("timeCreated"));

	}

	@Test
	public void testParseAuthJwsV1() throws Exception {

		final String source = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJrb2FsaWJlZUlkIjo5OTksImVtYWlsIjoiam9obkBtYWlsaW5hdG9yLmNvbSIsInRpbWVDcmVhdGVkIjoiMjAxNy0wOS0wOVQyMjozMDoxNSJ9.MJ8q7MzjnSb0DvUW3y8U8MlKzDD_2BQjYdWZYBuUMeM";

		Object[] claims = SecurityUtility.parseAuthJws(source, testKey);

		assertNotNull(claims);

		assertEquals(999, claims[0]);

		assertEquals("john@mailinator.com", claims[1]);

	}

	@Test
	public void testParseAuthJwsV2() throws Exception {

		final String source = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJrb2FsaWJlZUlkIjoxLCJlbWFpbCI6InRlc3RAdGVzdC5uZXQiLCJ0aW1lQ3JlYXRlZCI6IjIwMTctMTItMDlUMjI6MzA6MTUifQ.5WIAXaQRw21_o4v_bMliGJLZStIIz-A-1cLRRb0kXDA";

		Object[] claims = SecurityUtility.parseAuthJws(source, testKey);

		assertNotNull(claims);

		assertEquals(1, claims[0]);

		assertEquals("test@test.net", claims[1]);

		assertEquals("2017-12-09T22:30:15", claims[2]);

	}

	@Test(expected = JwtException.class)
	public void testParseAuthJwsN() throws Exception {

		final String source = "eyJhbGciOiJIUzI1NiJ9.eyJrb2FsaWJlZUlkIjo5OTksImVtYWlsIjoidGVzdEB0ZXN0Lm5ldCJ9.Pn_AIBj5mkf0B1CnobEzrKqwfxxynZNrZTm9L4Al-Yk";

		SecurityUtility.parseAuthJws(source, testKey);

	}

	@Test
	public void testJwsHasNotExpiredN1() throws Exception {

		assertTrue(SecurityUtility.jwsHasNotExpired(LocalDateTime.now().minusMinutes(3)));

	}

	@Test
	public void testJwsHasNotExpiredN2() throws Exception {

		assertTrue(SecurityUtility.jwsHasNotExpired(LocalDateTime.now().minusMinutes(14)));

	}

	@Test
	public void testJwsHasNotExpiredY1() throws Exception {

		assertFalse(SecurityUtility.jwsHasNotExpired(LocalDateTime.now().minusMinutes(75)));

	}

	@Test
	public void testJwsHasNotExpiredY2() throws Exception {

		assertFalse(SecurityUtility.jwsHasNotExpired(LocalDateTime.now().minusMinutes(31)));

	}

}
