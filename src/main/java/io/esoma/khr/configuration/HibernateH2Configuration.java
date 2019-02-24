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
 * for Spring ORM to prepare a H2 in-memory database session factory used for
 * DAO integration testing.
 * 
 * @author Eddy Soma
 *
 */
@Lazy
@Configuration
@EnableTransactionManagement
public class HibernateH2Configuration {

	private static final String modelPackage = "io.esoma.khr.model";

	/**
	 * 
	 * Returns a session factory bean configured with a H2 database connection. It
	 * is used for integration testing.
	 * 
	 * @return the session factory bean.
	 */
	@Bean(name = "h2DBSessionFactory")
	public LocalSessionFactoryBean getH2DBSessionFactory() {

		LocalSessionFactoryBean h2SessionFactory = new LocalSessionFactoryBean();

		h2SessionFactory.setDataSource(getH2DBDataSource());
		h2SessionFactory.setPackagesToScan(modelPackage);
		h2SessionFactory.setHibernateProperties(getH2DBHibernateProperties());

		return h2SessionFactory;

	}

	/**
	 * 
	 * Returns a H2 data source configured with default in-memory setup properties.
	 * 
	 * @return the data source bean.
	 */
	@Bean(name = "h2DBDataSource")
	public DataSource getH2DBDataSource() {

		BasicDataSource h2DataSource = new BasicDataSource();

		h2DataSource.setDriverClassName(DatabaseUtility.getH2DBDriverClassName());
		h2DataSource.setUrl(DatabaseUtility.getH2DBUrl());
		h2DataSource.setUsername(DatabaseUtility.getH2DBUsername());
		h2DataSource.setPassword(DatabaseUtility.getH2DBPassword());
		// Rollback on return.
		h2DataSource.setAutoCommitOnReturn(false);
		h2DataSource.setRollbackOnReturn(true);

		return h2DataSource;

	}

	/**
	 * 
	 * Returns the configuration properties defined for an H2 database connection
	 * used by Hibernate.
	 * 
	 * @return the configured properties object.
	 */
	private Properties getH2DBHibernateProperties() {

		Properties h2HibernateProperties = new Properties();

		h2HibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		h2HibernateProperties.setProperty("hibernate.show_sql", "true");
		h2HibernateProperties.setProperty("hibernate.connection.pool_size", "1");
		h2HibernateProperties.setProperty("hibernate.current_session_context_class", "thread");
		h2HibernateProperties.setProperty("hibernate.connection.autocommit", "false");
		h2HibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create");

		return h2HibernateProperties;

	}

	/**
	 * 
	 * Returns a Hibernate transaction manager configured with a H2 session factory.
	 * 
	 * @return the transaction manager.
	 */
	@Bean(name = "h2DBHibernateTransactionManager")
	public PlatformTransactionManager getH2DBHibernateTransactionManager() {

		return new HibernateTransactionManager(getH2DBSessionFactory().getObject());

	}

}
