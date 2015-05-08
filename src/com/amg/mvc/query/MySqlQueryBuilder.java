package com.amg.mvc.query;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.LinkedList;

import com.amg.mvc.factory.model.MetaData;
import com.amg.mvc.factory.model.Model;

public class MySqlQueryBuilder extends QueryBuilder {

	@Override
	public PreparedStatement createInsertStatement(Connection conn, Model model, MetaData meta) throws Exception {
		String sql_seg1 = "INSERT INTO " + meta.getTableName() + " ";
		String sql_seg2 = "(";
		String sql_seg3 = ") VALUES (";
		String sql_seg4 = ")";
		LinkedList<Object> values = new LinkedList<Object>();
		int i = 0;
		for(String field : meta.getMapping().keySet()) {
			if(i++ > 0) {
				sql_seg2 += ", ";
				sql_seg3 += ", ";
			}
			sql_seg2 += meta.getMapping().get(field);
			sql_seg3 += "?";
			values.add(getVal(model, model.getClass().getDeclaredField(field)));
		}
		String sql = sql_seg1 + sql_seg2 + sql_seg3 + sql_seg4;
		PreparedStatement stmt = conn.prepareStatement(sql);
		i = 1;
		for(Object obj : values) {
			stmt.setObject(i++, obj);
		}
		return stmt;
	}

	@Override
	public PreparedStatement createUpdateStatement(Connection conn, Model model, MetaData meta) throws Exception {
		String sql_seg1 = "UPDATE " + meta.getTableName() + " ";
		String sql_seg2 = "SET ";
		String sql_seg3 = " WHERE " + meta.getMapping().get(meta.getIdColumn()) + " = ?";
		int i = 0;
		LinkedList<Object> values = new LinkedList<Object>();
		for(String field : meta.getMapping().keySet()) {
			if(i++ > 0) {
				sql_seg2 += ", ";
			}
			sql_seg2 += meta.getMapping().get(field) + " = ?";
			values.add(getVal(model, model.getClass().getDeclaredField(field)));
		}
		values.add(getVal(model, model.getClass().getDeclaredField(meta.getIdColumn())));
		String sql = sql_seg1 + sql_seg2 + sql_seg3;
		PreparedStatement stmt = conn.prepareStatement(sql);
		i = 1;
		for(Object value : values) {
			stmt.setObject(i++, value);
		}
		return stmt;
	}
	
	@Override
	public PreparedStatement createUpdateStatement(Connection conn, Conditions conditions, MetaData meta) throws Exception {
		LinkedList<Object> values = new LinkedList<Object>();
		String sql_seg1 = "UPDATE " + meta.getTableName() + " SET ";
		String sql_seg2 = buildCommands(conditions, meta, values);
		String sql_seg3 = buildConditions(conditions, meta, values);
		String sql = sql_seg1;
		if(sql_seg2 != null) sql += sql_seg2;
		if(sql_seg3 != null) sql += " WHERE " + sql_seg3;
		else sql += " WHERE 1=1";
		PreparedStatement stmt = conn.prepareStatement(sql);
		int i = 1;
		for(Object value : values) {
			stmt.setObject(i++, value);
		}
		return stmt;
	}

	@Override
	public PreparedStatement createDeleteStatement(Connection conn, Model model, MetaData meta) throws Exception {
		String idColumn = meta.getIdColumn();
		String sql_seg1 = "DELETE FROM " + meta.getTableName() + " ";
		String sql_seg2 = "WHERE " + meta.getMapping().get(idColumn) + " = ?";
		Object value = getVal(model, model.getClass().getDeclaredField(idColumn));
		String sql = sql_seg1 + sql_seg2;
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setObject(1, value);
		return stmt;
	}
	
	@Override
	public PreparedStatement createDeleteStatement(Connection conn, Conditions conditions, MetaData meta) throws Exception {
		String sql_seg1 = "DELETE FROM " + meta.getTableName();
		LinkedList<Object> values = new LinkedList<Object>();
		String sql_seg2 = buildConditions(conditions, meta, values);
		String sql = sql_seg1;
		if(sql_seg2 != null) sql += " WHERE " + sql_seg2;
		else sql += " WHERE 1=1";
		PreparedStatement stmt = conn.prepareStatement(sql);
		int i = 1;
		for(Object value : values) {
			stmt.setObject(i++, value);
		}
		return stmt;
	}

	@Override
	public PreparedStatement createQueryStatement(Connection conn, MetaData meta) throws Exception {
		return createQueryStatement(null, meta);
	}

	@Override
	public PreparedStatement createQueryStatement(Connection conn, Conditions conditions, MetaData meta) throws Exception {
		String sql_seg1 = "SELECT " + buildColumnList(conditions, meta);
		String sql_seg2 = " FROM " + meta.getTableName();
		LinkedList<Object> values = new LinkedList<Object>();
		String sql_seg3 = buildConditions(conditions, meta, values);
		String sql = sql_seg1 + sql_seg2;
		if(sql_seg3 != null) sql += " WHERE " + sql_seg3;
		PreparedStatement stmt = conn.prepareStatement(sql);
		int i = 1;
		for(Object value : values) {
			stmt.setObject(i++, value);
		}
		return stmt;
	}
	
	public String buildColumnList(Conditions conditions, MetaData meta) throws Exception {
		if(conditions == null) return "*";
		String[] columnList = conditions.getColumnList();
		if(columnList == null) return "*";
		String retVal = "";
		for(int i = 0; i < columnList.length; i++) {
			if(i > 0) retVal += ", ";
			retVal += meta.getMapping().get(columnList[i]);
		}
		return retVal;
	}
	
	public String buildCommands(Conditions conditions, MetaData meta, LinkedList<Object> values) throws Exception {
		if(conditions == null) return null;
		String retVal = "";
		int i = 0;
		for(SetCommand command : conditions.getCommandList()) {
			if(i++ > 0) retVal += ", ";
			retVal += meta.getMapping().get(command.getFieldName()) + " = ?";
			values.add(command.getValue());
		}
		if(i == 0) return null;
		return retVal;
	}
	
	public String buildConditions(Conditions conditions, MetaData meta, LinkedList<Object> values) throws Exception {
		if(conditions == null) return null;
		String retVal = "";
		int i = 0;
		for(ConditionItem condition : conditions.getConditionList()) {
			if(i++ > 0) retVal += " AND " ;
			retVal += expandCondition(condition, meta, values);
		}
		if(i == 0) return null;
		return retVal;
	}
	
	public String expandCondition(ConditionItem condition, MetaData meta, LinkedList<Object> values) throws Exception {
		String retVal = "";
		if(condition instanceof BlockConditionItem) {
			retVal = expandBlock((BlockConditionItem)condition, meta, values);
		} else if(condition instanceof SingleConditionItem) {
			retVal = expandSingle((SingleConditionItem)condition, meta, values);
		}
		return retVal;
	}
	
	public String expandBlock(BlockConditionItem block, MetaData meta, LinkedList<Object> values) throws Exception {
		String retVal = "(";
		ConditionType type = block.getType();
		if(type == ConditionType.NOT){
			retVal = "NOT " + retVal;
		}
		String separator = (type == ConditionType.OR)? " OR " : (type == ConditionType.AND)? " AND " : "";
		int i = 0;
		for(ConditionItem item : block.getChildren()) {
			if(i++ > 0) retVal += separator;
			retVal += expandCondition(item, meta, values);
		}
		retVal += ")";
		return retVal;
	}
	
	public String expandSingle(SingleConditionItem single, MetaData meta, LinkedList<Object> values) throws Exception {
		String retVal = "";
		Object[] arr;
		retVal += meta.getMapping().get(single.getFieldName());
		switch(single.getType()) {
		case LK:
			retVal += " LIKE ?";
			values.add(single.getValue());
			break;
		case IN:
			retVal += " IN (";
			arr = (Object[]) single.getValue();
			for(int i = 0; i < arr.length; i++) {
				if(i > 0) retVal += ", ";
				retVal += "?";
				values.add(arr[i]);
			}
			retVal += ")";
			break;
		case BW:
			retVal += " BETWEEN ? AND ?";
			arr = (Object[]) single.getValue();
			values.add(arr[0]);
			values.add(arr[1]);
			break;
		case EQ:
			retVal += " = ?";
			values.add(single.getValue());
			break;
		case NE:
			retVal += " <> ?";
			values.add(single.getValue());
			break;
		case LT:
			retVal += " < ?";
			values.add(single.getValue());
			break;
		case LE:
			retVal += " <= ?";
			values.add(single.getValue());
			break;
		case GT:
			retVal += " > ?";
			values.add(single.getValue());
			break;
		case GE:
			retVal += " >= ?";
			values.add(single.getValue());
			break;
		case NL:
			retVal += " IS NULL";
			break;
		case NN:
			retVal += " IS NOT NULL";
			break;
		default: return "";
		}
		return retVal;
	}
	
	public Object getVal(Object obj, Field field) throws IllegalAccessException {
		field.setAccessible(true);
		Object retVal = field.get(obj);
		field.setAccessible(false);
		return retVal;
	}

}