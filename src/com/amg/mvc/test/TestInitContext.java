package com.amg.mvc.test;

import javax.servlet.*;

import com.amg.mvc.context.ServletContextManager;

public class TestInitContext {

	public static void init(ServletContext application) {
		ServletContextManager.init(application);
		ServletContextManager man = (ServletContextManager) application.getAttribute("ServletContextManager");
		man.getServiceManager().addObject(TestService.class);
		man.getControllerManager().addObject(TestController.class);
		man.setSecurityManager(new TestSecurityManager());
	}
	
}
