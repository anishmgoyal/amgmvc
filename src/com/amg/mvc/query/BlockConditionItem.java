package com.amg.mvc.query;

import java.util.LinkedList;

public class BlockConditionItem implements ConditionItem {

	private ConditionType type;
	private LinkedList<ConditionItem> children;
	
	public BlockConditionItem(ConditionType type, LinkedList<ConditionItem> children) {
		this.type = type;
		this.children = children;
	}
	
	public ConditionType getType() {
		return type;
	}
	
	public LinkedList<ConditionItem> getChildren() {
		return children;
	}
	
}
