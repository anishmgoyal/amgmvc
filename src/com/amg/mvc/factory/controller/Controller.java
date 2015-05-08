package com.amg.mvc.factory.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amg.mvc.factory.view.View;

public abstract class Controller {

	public abstract View handleRequest(HttpServletRequest request, HttpServletResponse response);
	
}