package com.amg.mvc.factory.model;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;

import com.amg.mvc.connection.pool.ConnectionPool;
import com.amg.mvc.query.Conditions;
import com.amg.mvc.query.QueryBuilder;

public abstract class DataModel extends Model {

	private static HashMap<String, MetaData> registry;
	
	private enum QueryType {
		INSERT_INSTANCE,
		UPDATE_INSTANCE,
		DELETE_INSTANCE,
		UPDATE,
		DELETE
	}
	
	static {
		registry = new HashMap<String, MetaData>();
	}
	
	public static void register(Class<?> classObj) {
		try {
			MetaData meta = new MetaData(classObj);
			registry.put(classObj.getSimpleName(), meta);
		} catch(InvalidModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	public static void register() {
		Class<?> classObj;
		classObj = getCaller();
		register(classObj);
	}
	
	public static Class<?> getCaller() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		try {
			return Class.forName(stackTrace[3].getClassName());
		} catch(Exception e) {
			return null;
		}
	}
	
	public boolean insert() {
		return instanceCall(QueryType.INSERT_INSTANCE);
	}
	
	public boolean update() {
		return instanceCall(QueryType.UPDATE_INSTANCE);
	}
	
	public boolean delete() {
		return instanceCall(QueryType.DELETE_INSTANCE);
	}
	
	private boolean instanceCall(QueryType type) {
		boolean successful = false;
		Model model = this;
		MetaData meta = registry.get(getClass().getSimpleName());
		Connection conn = ConnectionPool.getConnection();
		QueryBuilder qb = ConnectionPool.getQueryBuilder();
		PreparedStatement stmt = null;
		try {
			stmt = (type == QueryType.INSERT_INSTANCE)? qb.createInsertStatement(conn, model, meta) : 
					(type == QueryType.UPDATE_INSTANCE)? qb.createUpdateStatement(conn, model, meta) :
					(type == QueryType.DELETE_INSTANCE)? qb.createDeleteStatement(conn, model, meta) : null;
			successful = stmt.executeUpdate() > 0;
		} catch(Exception e) {
			System.out.println("Error creating instance statement of type " + type);
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch(Exception e) {
				//Tried
			}
			try {
				conn.close();
			} catch(Exception e) {
				//Tried
			}
		}
		return successful;
	}
	
	public static boolean update(Class<? extends Model> classObj) {
		return staticCall(QueryType.UPDATE, classObj, null);
	}
	
	public static boolean update(Class<? extends Model> classObj, Conditions conditions) {
		return staticCall(QueryType.UPDATE, classObj, conditions);
	}
	
	public static boolean delete(Class<? extends Model> classObj) {
		return staticCall(QueryType.DELETE, classObj, null);
	}
	
	public static boolean delete(Class<? extends Model> classObj, Conditions conditions) {
		return staticCall(QueryType.DELETE, classObj, conditions);
	}
	
	private static boolean staticCall(QueryType type, Class<? extends Model> classObj, Conditions conditions) {
		boolean successful = false;
		MetaData meta = registry.get(classObj.getSimpleName());
		Connection conn = ConnectionPool.getConnection();
		QueryBuilder qb = ConnectionPool.getQueryBuilder();
		PreparedStatement stmt = null;
		try {
			stmt = (type == QueryType.UPDATE)? qb.createUpdateStatement(conn, conditions, meta) :
					(type == QueryType.DELETE)? qb.createDeleteStatement(conn, conditions, meta) : null;
			successful = stmt.executeUpdate() > 0;
		} catch(Exception e) {
			System.out.println("Error creating static method of type " + type);
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch(Exception e) {
				//Tried
			}
			try {
				conn.close();
			} catch(Exception e) {
				//Tried
			}
		}
		return successful;
	}
	
	public static LinkedList<Model> query(Class<? extends Model> classObj) {
		return query(classObj, null);
	}
	
	public static LinkedList<Model> query(Class<? extends Model> classObj, Conditions c) {
		LinkedList<Model> retVal = new LinkedList<Model>();
		Connection conn = ConnectionPool.getConnection();
		MetaData meta = registry.get(classObj.getSimpleName());
		QueryBuilder qb = ConnectionPool.getQueryBuilder();
		PreparedStatement stmt = null;
		try {
			stmt = qb.createQueryStatement(conn, c, meta);
			ResultSet set = stmt.executeQuery();
			while(set.next()) {
				Model model = initializeModelFromResultSet(meta, set);
				if(model != null) retVal.add(model);
			}
		} catch(Exception e) {
			System.out.println("Error building query");
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch(Exception e) {
				//Tried
			}
			try {
				conn.close();
			} catch(Exception e) {
				//Tried
			}
		}
		return retVal;
	}
	
	private static Model initializeModelFromResultSet(MetaData meta, ResultSet set) {
		try {
			Class<?> classObj = meta.getClassObj();
			Model model = (Model) classObj.newInstance();
			HashMap<String, String> mapping = meta.getMapping();
			for(String field : mapping.keySet()) {
				Field fieldObj = classObj.getDeclaredField(field);
				fieldObj.setAccessible(true);
				fieldObj.set(model, fieldObj.getType().cast(set.getObject(mapping.get(field))));
				fieldObj.setAccessible(false);
			}
			return model;
		} catch(Exception e) {
			System.out.println("Exception while loading a model from query data");
			e.printStackTrace();
			return null;
		}
	}
	
}