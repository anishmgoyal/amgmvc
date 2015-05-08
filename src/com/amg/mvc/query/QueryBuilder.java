package com.amg.mvc.query;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.amg.mvc.factory.model.MetaData;
import com.amg.mvc.factory.model.Model;

public abstract class QueryBuilder {

	public abstract PreparedStatement createInsertStatement(Connection conn, Model model, MetaData meta) throws Exception;
	
	public abstract PreparedStatement createUpdateStatement(Connection conn, Model model, MetaData meta) throws Exception;
	
	public abstract PreparedStatement createUpdateStatement(Connection conn, Conditions conditions, MetaData meta) throws Exception;
	
	public abstract PreparedStatement createDeleteStatement(Connection conn, Model model, MetaData meta) throws Exception;
	
	public abstract PreparedStatement createDeleteStatement(Connection conn, Conditions conditions, MetaData meta) throws Exception;
	
	public abstract PreparedStatement createQueryStatement(Connection conn, MetaData meta) throws Exception;
	
	public abstract PreparedStatement createQueryStatement(Connection conn, Conditions conditions, MetaData meta) throws Exception;
	
}
