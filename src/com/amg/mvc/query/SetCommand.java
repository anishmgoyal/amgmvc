package com.amg.mvc.query;

public class SetCommand {

	private String fieldName;
	private Object value;
	
	public SetCommand(String fieldName, Object value) {
		this.fieldName = fieldName;
		this.value = value;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public Object getValue() {
		return value;
	}
	
}
