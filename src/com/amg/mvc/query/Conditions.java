package com.amg.mvc.query;

import java.util.LinkedList;

public class Conditions {
	
	private LinkedList<ConditionItem> conditionList = new LinkedList<ConditionItem>();
	private LinkedList<SetCommand> commandList = new LinkedList<SetCommand>();
	private String[] columnList = null;
	
	//These make the generic handlers for single and block conditions
	
	private ConditionItem blockCondition(ConditionType type, ConditionItem...conditions) {
		LinkedList<ConditionItem> children = new LinkedList<ConditionItem>();
		for(int i = 0; i < conditions.length; i++) {
			children.addLast(conditions[i]);;
			if(!conditionList.isEmpty()) conditionList.removeLast();
		}
		BlockConditionItem item = new BlockConditionItem(type, children);
		conditionList.addLast(item);
		return item;
	}
	
	private ConditionItem singleCondition(ConditionType type, String fieldName, Object value) {
		SingleConditionItem item = new SingleConditionItem(type, fieldName, value);
		conditionList.addLast(item);
		return item;
	}
	
	//This is for creating set commands.
	
	public void set(String fieldName, Object value) {
		commandList.add(new SetCommand(fieldName, value));
	}
	
	//This is for limiting query statements to specific columns
	
	public void cols(String...columns) {
		this.columnList = columns;
	}
	
	//Below are the actual condition functions
	
	//These are blocks
	
	public ConditionItem or(ConditionItem...constraints) {
		return blockCondition(ConditionType.OR, constraints);
	}
	
	public ConditionItem and(ConditionItem...constraints) {
		return blockCondition(ConditionType.AND, constraints);
	}
	
	//This is configured LIKE a block... but it's an inversion, not a block.
	
	public ConditionItem not(ConditionItem constraint) {
		return blockCondition(ConditionType.NOT, constraint);
	}
	
	//These are single constraints
	
	public ConditionItem in(String fieldName, Object...value) {
		return singleCondition(ConditionType.IN, fieldName, value);
	}
	
	public ConditionItem bw(String fieldName, Object...value) {
		return singleCondition(ConditionType.BW, fieldName, value);
	}
	
	public ConditionItem lk(String fieldName, Object value) {
		return singleCondition(ConditionType.LK, fieldName, value);
	}
	
	public ConditionItem eq(String fieldName, Object value) {
		return singleCondition(ConditionType.EQ, fieldName, value);
	}
	
	public ConditionItem ne(String fieldName, Object value) {
		return singleCondition(ConditionType.NE, fieldName, value);
	}
	
	public ConditionItem gt(String fieldName, Object value) {
		return singleCondition(ConditionType.GT, fieldName, value);
	}
	
	public ConditionItem ge(String fieldName, Object value) {
		return singleCondition(ConditionType.GE, fieldName, value);
	}
	
	public ConditionItem lt(String fieldName, Object value) {
		return singleCondition(ConditionType.LT, fieldName, value);
	}
	
	public ConditionItem le(String fieldName, Object value) {
		return singleCondition(ConditionType.LE, fieldName, value);
	}
	
	public ConditionItem nl(String fieldName) {
		return singleCondition(ConditionType.NL, fieldName, null);
	}
	
	public ConditionItem nn(String fieldName) {
		return singleCondition(ConditionType.NN, fieldName, null);
	}
	
	public LinkedList<ConditionItem> getConditionList() {
		return conditionList;
	}
	
	public LinkedList<SetCommand> getCommandList() {
		return commandList;
	}
	
	public String[] getColumnList() {
		return columnList;
	}
	
}
