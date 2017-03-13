package com.amg.mvc.test;

import java.util.HashSet;
import java.util.LinkedList;

import javax.servlet.*;

import com.amg.mvc.bean.view.View;
import com.amg.mvc.bean.view.ViewData;
import com.amg.mvc.context.ServletContextSessionManager;
import com.amg.mvc.user.UserRoles;

public class TestServlet {

	public static void main(String[] args) throws Exception {
		ServletContext application = new TestServletContext();
		TestInitContext.init(application);
		UserRoles userRoles = new UserRoles() {
			HashSet<String> roles = new HashSet<String>();
			public boolean hasRole(String role) {
				return roles.contains(role);
			}
			public void addRole(String role) {
				roles.add(role);
			}
			public void removeRole(String role) {
				roles.remove(role);
			}
		};
		userRoles.addRole("USER_READ");
		//userRoles.addRole("USER_TEST");
		ServletContextSessionManager manager = new ServletContextSessionManager(application, userRoles);
		manager.setServicePackage("com.amg.mvc.test");
		manager.setControllerPackage("com.amg.mvc.test");
		TestController c = (TestController) manager.getController("TestController");
		View view;
		if(c != null) view = c.handleRequest(null, null);
		else view = new TestUnauthView();
		view.generateView(null, null);
		LinkedList<ViewData> data = view.getViewData();
		for(ViewData vd : data) {
			System.out.println("\n-------------------");
			System.out.println(vd.getTitle() + "\n\n" + vd.getData());
			System.out.println("-------------------");
		}
	}
	
}
