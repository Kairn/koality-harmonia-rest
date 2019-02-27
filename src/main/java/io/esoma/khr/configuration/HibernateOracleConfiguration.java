package io.esoma.khr.configuration;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.esoma.khr.utility.DatabaseUtility;

/**
 * 
 * The Hibernate 5 configuration class that provides bean definitions necessary
 * for Spring ORM to prepare an Oracle Database session factory used by the
 * application runtime.
 * 
 * @author Eddy Soma
 *
 */
@Lazy
@Configuration
@EnableTransactionManagement
public class HibernateOracleConfiguration {

	private static final String MODEL_PACKAGE = "io.esoma.khr.model";

	/**
	 * 
	 * Returns a session factory bean configured with an Oracle Database connection.
	 * It is used by the application runtime.
	 * 
	 * @return the session factory bean.
	 */
	@Bean(name = "oracleDBSessionFactory")
	public LocalSessionFactoryBean getOracleDBSessionFactory() {

		LocalSessionFactoryBean oracleSessionFactory = new LocalSessionFactoryBean();

		oracleSessionFactory.setDataSource(getOracleDBDataSource());
		oracleSessionFactory.setPackagesToScan(MODEL_PACKAGE);
		oracleSessionFactory.setHibernateProperties(getOracleDBHibernateProperties());

		return oracleSessionFactory;

	}

	/**
	 * 
	 * Returns an Oracle Database data source configured with application specific
	 * properties.
	 * 
	 * @return the data source bean.
	 */
	@Bean(name = "oracleDBDataSource")
	public DataSource getOracleDBDataSource() {

		BasicDataSource oracleDataSource = new BasicDataSource();

		oracleDataSource.setDriverClassName(DatabaseUtility.getOracleDBDriverClassName());
		oracleDataSource.setUrl(DatabaseUtility.getOracleDBUrl());
		oracleDataSource.setUsername(DatabaseUtility.getOracleDBUsername());
		oracleDataSource.setPassword(DatabaseUtility.getOracleDBPassword());
		// Rollback on return.
		oracleDataSource.setAutoCommitOnReturn(false);
		oracleDataSource.setRollbackOnReturn(true);

		return oracleDataSource;

	}

	/**
	 * 
	 * Returns the configuration properties defined for an Oracle Database
	 * connection used by Hibernate.
	 * 
	 * @return the configured properties object.
	 */
	private final Properties getOracleDBHibernateProperties() {

		Properties oracleHibernateProperties = new Properties();

		oracleHibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle12cDialect");
		oracleHibernateProperties.setProperty("hibernate.show_sql", "true");
		oracleHibernateProperties.setProperty("hibernate.connection.pool_size", "1");
		oracleHibernateProperties.setProperty("hibernate.current_session_context_class", "thread");
		oracleHibernateProperties.setProperty("hibernate.connection.autocommit", "false");
		oracleHibernateProperties.setProperty("hibernate.hbm2ddl.auto", "validate");

		return oracleHibernateProperties;

	}

	/**
	 * 
	 * Returns a Hibernate transaction manager configured with an Oracle Database
	 * session factory.
	 * 
	 * @return the transaction manager.
	 */
	@Bean(name = "oracleDBHibernateTransactionManager")
	public PlatformTransactionManager getOracleDBHibernateTransactionManager() {

		return new HibernateTransactionManager(getOracleDBSessionFactory().getObject());

	}

}
