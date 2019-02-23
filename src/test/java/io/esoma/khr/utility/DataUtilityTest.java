package io.esoma.khr.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DataUtilityTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// No content yet.
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// No content yet.
	}

	@Test
	public void testEncodeBytesToDataUrlImage1() throws Exception {

		final String source = "rigorous test 1";
		final byte[] bytes = source.getBytes();
		final String mimeType = "JPG";

		assertEquals("data:image/jpg;base64,cmlnb3JvdXMgdGVzdCAx",
				DataUtility.encodeBytesToDataUrlImage(bytes, mimeType));

	}

	@Test
	public void testEncodeBytesToDataUrlImage2() throws Exception {

		final String source = "even more rigorous test 2";
		final byte[] bytes = source.getBytes();
		final String mimeType = "PNG";

		assertEquals("data:image/png;base64,ZXZlbiBtb3JlIHJpZ29yb3VzIHRlc3QgMg==",
				DataUtility.encodeBytesToDataUrlImage(bytes, mimeType));

	}

	@Test
	public void testEncodeBytesToDataUrlAudio1() throws Exception {

		final String source = "rigorous audio test 1";
		final byte[] bytes = source.getBytes();
		final String mimeType = "MP3";

		assertEquals("data:audio/mp3;base64,cmlnb3JvdXMgYXVkaW8gdGVzdCAx",
				DataUtility.encodeBytesToDataUrlAudio(bytes, mimeType));

	}

	@Test
	public void testEncodeBytesToDataUrlAudio2() throws Exception {

		final String source = "even more rigorous audio test 2";
		final byte[] bytes = source.getBytes();
		final String mimeType = "OGG";

		assertEquals("data:audio/ogg;base64,ZXZlbiBtb3JlIHJpZ29yb3VzIGF1ZGlvIHRlc3QgMg==",
				DataUtility.encodeBytesToDataUrlAudio(bytes, mimeType));

	}

	@Test
	public void testDecodeDataUrlToBytes1() throws Exception {

		final String source = "my rigorous decode test 1";
		final byte[] bytes = source.getBytes();

		assertTrue(Arrays.equals(bytes,
				DataUtility.decodeDataUrlToBytes("data:audio/wav;base64,bXkgcmlnb3JvdXMgZGVjb2RlIHRlc3QgMQ==")));

	}

	@Test
	public void testDecodeDataUrlToBytes2() throws Exception {

		final String source = "my more rigorous decode test 2";
		final byte[] bytes = source.getBytes();

		assertTrue(Arrays.equals(bytes,
				DataUtility.decodeDataUrlToBytes("data:image/???;base64,bXkgbW9yZSByaWdvcm91cyBkZWNvZGUgdGVzdCAy")));

	}

	@Test
	public void testDecodeDataUrlToBytes3() throws Exception {

		final String source = "the hardest decode test you've ever seen";
		final byte[] bytes = source.getBytes();

		assertTrue(Arrays.equals(bytes, DataUtility.decodeDataUrlToBytes(
				"data:?????/???;base64,dGhlIGhhcmRlc3QgZGVjb2RlIHRlc3QgeW91J3ZlIGV2ZXIgc2Vlbg==")));

	}

}
