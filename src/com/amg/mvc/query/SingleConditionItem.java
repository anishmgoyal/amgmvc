package com.amg.mvc.query;

public class SingleConditionItem implements ConditionItem {

	private ConditionType type;
	private String fieldName;
	private Object value;
	
	public SingleConditionItem(ConditionType type, String fieldName, Object value) {
		this.type = type;
		this.fieldName = fieldName;
		this.value = value;
	}
	
	public ConditionType getType() {
		return type;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public Object getValue() {
		return value;
	}

}
