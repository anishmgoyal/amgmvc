package com.amg.mvc.factory.model;

import java.lang.reflect.Field;
import java.util.HashMap;

import com.amg.mvc.annotations.Map;

public class MetaData {

	//The name of the table in the data source.
	private String tableName;
	//Id column required.
	private String idColumn;
	//Field Name to Column Name.
	private HashMap<String, String> mapping;
	//This is the class object which is necessary for working the interface
	private Class<?> classObj;
	
	public MetaData(Class<?> classObj) throws InvalidModelFormatException {
		if(!Model.class.isAssignableFrom(classObj))
			throw new InvalidModelFormatException("Invalid model " + classObj.getSimpleName() + "; Does not extend Model.");
		this.classObj = classObj;
		tableName = (String) getFieldVal(null, "tableName");
		if(tableName == null) throw new InvalidModelFormatException("Invalid model " + classObj.getSimpleName() + "; field tableName is required.");
		mapping = processFields();
	}
	
	public HashMap<String, String> processFields() {
		HashMap<String, String> retVal = new HashMap<String, String>();
		Field[] fields = classObj.getDeclaredFields();
		for(int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			Map map = field.getAnnotation(Map.class);
			if(map == null) continue;
			String fieldName = field.getName();
			if(map.id()) idColumn = fieldName;
			String columnName = map.value();
			retVal.put(fieldName, columnName);
		}
		return retVal;
	}
	
	public Object getFieldVal(Object obj, String fieldName) {
		try {
			Field field = classObj.getDeclaredField(fieldName);
			field.setAccessible(true);
			Object retVal = field.get(obj);
			field.setAccessible(false);
			return retVal;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getTableName() {return tableName;}
	public String getIdColumn() {return idColumn;}
	public HashMap<String, String> getMapping() {return mapping;}
	public Class<?> getClassObj() {return classObj;}
	
	
}
