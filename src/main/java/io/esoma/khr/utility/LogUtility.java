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

	public static final Logger rootLogger;
	public static final Logger masterLogger;

	static {
		rootLogger = LogManager.getRootLogger();
		masterLogger = LogManager.getLogger("masterLogger");
	}

}
