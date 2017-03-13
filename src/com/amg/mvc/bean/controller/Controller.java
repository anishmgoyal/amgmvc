package com.amg.mvc.bean.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amg.mvc.bean.view.View;

public abstract class Controller {

	public abstract View handleRequest(HttpServletRequest request, HttpServletResponse response);
	
}