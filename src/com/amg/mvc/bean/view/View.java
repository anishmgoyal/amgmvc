package com.amg.mvc.bean.view;

import java.util.LinkedList;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class View {

	public abstract LinkedList<ViewData> getViewData();
	
	public abstract void generateView(HttpServletRequest request, HttpServletResponse response);
	
	public abstract void bindParam(String key, Object param);
	
	public abstract Object getParam(String key);
	
	public abstract Set<String> getKeys();
	
}