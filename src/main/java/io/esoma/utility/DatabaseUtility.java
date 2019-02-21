package io.esoma.utility;

/**
 * 
 * The Utility class with static methods that return database related values and
 * objects.
 * 
 * @author Eddy Soma
 *
 */
public class DatabaseUtility {

	/**
	 * 
	 * Gets the environmental variables needed to establish a connection to the
	 * Oracle Database.
	 * 
	 * @return the string array containing the database variables.
	 */
	static String[] getOracleDBVariables() {

		return System.getenv("KHR_AGGRE").split(";@");

	}

	/**
	 * 
	 * Gets the class name for the Oracle Database driver.
	 * 
	 * @return the driver class name.
	 */
	public static String getOracleDBDriverClassName() {

		return getOracleDBVariables()[0];

	}

	/**
	 * 
	 * Gets the url string of the Oracle Database used by the application.
	 * 
	 * @return the url string.
	 */
	public static String getOracleDBUrl() {

		return getOracleDBVariables()[1];

	}

	/**
	 * 
	 * Gets the username of the Oracle Database connection.
	 * 
	 * @return the username string.
	 */
	public static String getOracleDBUsername() {

		return getOracleDBVariables()[2];

	}

	/**
	 * 
	 * Gets the password of the Oracle Database connection.
	 * 
	 * @return the password string.
	 */
	public static String getOracleDBPassword() {

		return getOracleDBVariables()[3];

	}

	/**
	 * 
	 * Gets the environmental variables needed to connect to the H2 in-memory
	 * database.
	 * 
	 * @return the string array containing the database variables.
	 */
	static String[] getH2DBVariables() {

		String[] h2Vars = System.getenv("H2_AGGRE").split(";@");

		if (h2Vars.length == 3) {
			return new String[] { h2Vars[0], h2Vars[1], h2Vars[2], "" };
		} else {
			return h2Vars;
		}

	}

	/**
	 * 
	 * Gets the class name for the H2 Database driver.
	 * 
	 * @return the driver class name.
	 */
	public static String getH2DBDriverClassName() {

		return getH2DBVariables()[0];

	}

	/**
	 * 
	 * Gets the url string of the H2 Database.
	 * 
	 * @return the url string.
	 */
	public static String getH2DBUrl() {

		return getH2DBVariables()[1];

	}

	/**
	 * 
	 * Gets the username of the H2 Database connection. Returns "sa" if the
	 * variables is not available.
	 * 
	 * @return the username string.
	 */
	public static String getH2DBUsername() {

		try {
			return getH2DBVariables()[2];
		} catch (Exception e) {
			return "sa";
		}

	}

	/**
	 * 
	 * Gets the password of the H2 Database connection. Returns an empty string is
	 * the variable is not available.
	 * 
	 * @return the password string.
	 */
	public static String getH2DBPassword() {

		try {
			return getH2DBVariables()[3];
		} catch (Exception e) {
			return "";
		}

	}

}
