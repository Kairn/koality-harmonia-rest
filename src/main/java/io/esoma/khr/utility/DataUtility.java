package io.esoma.khr.utility;

import java.util.Base64;

/**
 * 
 * The utility class with static methods that handle data transformation and
 * conversion.
 * 
 * @author Eddy Soma
 *
 */
public class DataUtility {

	/**
	 * 
	 * Returns a base64 encoded data url from a byte array that constitutes an image
	 * file. The url can be used directly in the HTML img element to render the
	 * image in a browser.
	 * 
	 * @param bytes    the input byte array obtained from an image blob stored in
	 *                 the database.
	 * @param mimeType the file type of the image.
	 * @return the encoded url string.
	 */
	public static String encodeBytesToDataUrlImage(byte[] bytes, String mimeType) {

		return "data:image/".concat(mimeType).toLowerCase().concat(";base64,")
				.concat(Base64.getEncoder().encodeToString(bytes));

	}

	/**
	 * 
	 * Returns a base64 encoded data url from a byte array that constitutes an audio
	 * file. The url can be used directly in the HTML5 audio element to load the
	 * audio player in a browser.
	 * 
	 * @param bytes    the input byte array obtained from an audio blob stored in
	 *                 the database.
	 * @param mimeType the file type of the audio.
	 * @return the encoded url string.
	 */
	public static String encodeBytesToDataUrlAudio(byte[] bytes, String mimeType) {

		return "data:audio/".concat(mimeType).toLowerCase().concat(";base64,")
				.concat(Base64.getEncoder().encodeToString(bytes));

	}

	/**
	 * 
	 * Returns a decoded byte array from a base64 encoded data url of any MIME type.
	 * It is used for user to upload image or audio files which are then converted
	 * and stored in the database.
	 * 
	 * @param dataUrl the data url of the uploaded file.
	 * @return the decoded byte array.
	 */
	public static byte[] decodeDataUrlToBytes(String dataUrl) {

		String blobData = dataUrl.substring(dataUrl.indexOf("base64,") + 7);

		return Base64.getDecoder().decode(blobData);

	}

}
