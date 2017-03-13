package com.amg.mvc.connection.pool;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.tomcat.jdbc.pool.DataSource;

import com.amg.mvc.config.SettingsManager;
import com.amg.mvc.query.QueryBuilder;	

public class ConnectionPool {

	private static DataSource dataSource = null;
	private static QueryBuilder queryBuilder = null;
	
	public static void init(SettingsManager settings) {
		try {
			dataSource = new DataSource();
			initSettings(settings);
			dataSource.createPool();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() {
		try {
			return dataSource.getConnection();
		}
		catch(SQLException e) {
			System.out.println("Error getting connection from pool.");
			e.printStackTrace();
			return null;
		}
	}
	
	public static QueryBuilder getQueryBuilder() {
		return queryBuilder;
	}
	
	public static void initSettings(SettingsManager settings) {
		//Basic configuration
		boolean isEnabled = Boolean.parseBoolean(settings.getSetting("data.db.enabled"));
		if(!isEnabled) return;
		dataSource.setUrl(settings.getSetting("data.db.url"));
		dataSource.setUsername(settings.getSetting("data.db.username"));
		dataSource.setPassword(settings.getSetting("data.db.password"));
		
		//Sets provider defaults if necessary
		String provider = settings.getSetting("data.db.provider");
		String driver = settings.getSetting("data.db.driverclassname");
		if(driver == null || driver.equals("")) driver = settings.getSetting("data.db." + provider + ".driverclassname");
		dataSource.setDriverClassName(driver);
		
		String queryBuilderClass = settings.getSetting("data.db.querybuilder");
		if(queryBuilderClass == null || queryBuilderClass.equals("")) queryBuilderClass = settings.getSetting("data.db." + provider + ".querybuilder");
		try {
			queryBuilder = (QueryBuilder) Class.forName(queryBuilderClass).newInstance();
		} catch(Exception e) {
			System.out.println("QueryBuilder Service Not Available.");
			e.printStackTrace();
		}
		
		try {
			//Bunch of pool settings.
			dataSource.setMaxActive(Integer.parseInt(settings.getSetting("data.db.pool.maxActive")));
			dataSource.setMaxIdle(Integer.parseInt(settings.getSetting("data.db.pool.maxIdle")));
			dataSource.setMinIdle(Integer.parseInt(settings.getSetting("data.db.pool.minIdle")));
			dataSource.setInitialSize(Integer.parseInt(settings.getSetting("data.db.pool.initialSize")));
			dataSource.setMaxWait(Integer.parseInt(settings.getSetting("data.db.pool.maxWait")));
			dataSource.setTestOnBorrow(Boolean.parseBoolean(settings.getSetting("data.db.pool.testOnBorrow")));
			dataSource.setTestOnReturn(Boolean.parseBoolean(settings.getSetting("data.db.pool.testOnReturn")));;
			dataSource.setTestWhileIdle(Boolean.parseBoolean(settings.getSetting("data.db.pool.testWhileIdle")));
			dataSource.setTimeBetweenEvictionRunsMillis(Integer.parseInt(settings.getSetting("data.db.pool.timeBetweenEvictionRunsMillis")));
			dataSource.setMinEvictableIdleTimeMillis(Integer.parseInt(settings.getSetting("data.db.pool.minEvictableIdleTimeMillis")));
			dataSource.setValidationQuery(settings.getSetting("data.db.pool.validationQuery"));
		} catch(Exception e) {
			System.out.println("Connection pool configuration error. Double check your settings file.");
			e.printStackTrace();
		}
	}
	
}