package io.esoma.khr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.esoma.khr.dao.KoalibeeDao;

/**
 * 
 * The service class that handles user related functionalities.
 * 
 * @author Eddy Soma
 *
 */
@Service(value = "koalibeeService")
public class KoalibeeService {

	private KoalibeeDao koalibeeDao;

	@Autowired
	@Qualifier("koalibeeDaoImplBasic")
	public void setKoalibeeDao(KoalibeeDao koalibeeDao) {
		this.koalibeeDao = koalibeeDao;
	}

	/**
	 * 
	 * Attempts to register a new koalibee with the given data encoded in a JSON
	 * string. The value of the return string depends on the result of the process.
	 * 
	 * @param koalibeeData the JSON string sent from the request.
	 * @return a JSON web token representing the new koalibee if the registration is
	 *         successful, or a generic string with an error message when the
	 *         process fails for a specific reason, or null when an unknown error
	 *         occurs.
	 */
	public String register(String koalibeeData) {
		//
	}

	/**
	 * 
	 * Attempts to login a koalibee with the given credentials encoded in a JSON
	 * string.
	 * 
	 * @param credentialsData the JSON string sent from the request.
	 * @return a JSON web token representing the koalibee if the credentials are
	 *         valid, or a generic string with an error message.
	 */
	public String login(String credentialsData) {
		//
	}

}
