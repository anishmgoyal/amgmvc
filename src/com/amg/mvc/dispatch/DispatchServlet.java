package com.amg.mvc.dispatch;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amg.mvc.context.ServletContextSessionManager;
import com.amg.mvc.factory.controller.Controller;

public class DispatchServlet extends HttpServlet {

	private static final long serialVersionUID = -6989575519042581279L;
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		try {
			ServletContextSessionManager cm = new ServletContextSessionManager(getServletContext());
			Controller c = cm.getController("TEST");
			c.handleRequest(request, response);
		} catch(Exception e) {
			
		}
		DispatchRequest dr = processRequest(request);
		System.out.println("DISPATCH TYPE: " + dr.dispatchType);
		System.out.println("DISPATCH OBJECT: " + dr.dispatchObject);
		System.out.println("DISPATCH METHOD: " + dr.dispatchMethod);
	}
	
	public DispatchRequest processRequest(HttpServletRequest request) {
		DispatchType dispatchType = (request.getServletPath().equals("/page"))? DispatchType.VIEW : DispatchType.CONTROLLER;
		String[] params = request.getPathInfo().split("/");
		String dispatchObject = null;
		String dispatchMethod = null;
		for(int i = 0; i < params.length; i++) {
			if(params[i].trim().length() == 0) continue;
			if(dispatchObject == null) {
				dispatchObject = params[i];
				//TODO: Replace with the view action url
				if(dispatchType.equals(DispatchType.VIEW)) {
					break;
				}
			} else if(dispatchMethod == null) {
				dispatchMethod = params[i];
			}
		}
		DispatchRequest dispatchRequest = new DispatchRequest(dispatchType, dispatchObject, dispatchMethod);
		return dispatchRequest;
	}

}
