package io.esoma.khr.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * The utility class that maintains the loggers used by the application.
 * 
 * @author Eddy Soma
 *
 */
public class LogUtility {

	public static final Logger ROOT_LOGGER;
	public static final Logger MASTER_LOGGER;

	// General log messages.
	public static final String BAD_JSON = "invalid JSON string in request body";
	// Use with the format method.
	public static final String MISSING_JSON_ELEMENT = "the following data element is missing or illegal: <%s>";

	static {
		ROOT_LOGGER = LogManager.getRootLogger();
		MASTER_LOGGER = LogManager.getLogger("masterLogger");
	}

}
