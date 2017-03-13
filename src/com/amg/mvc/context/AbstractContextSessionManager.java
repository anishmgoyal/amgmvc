package com.amg.mvc.context;

import com.amg.mvc.bean.controller.Controller;
import com.amg.mvc.bean.service.Service;

public abstract class AbstractContextSessionManager {
	
	private String controllerPackage;
	private String servicePackage;
	
	//These methods are abstract, and should be defined by ContextManagers for implementations of this class.
	
	//Gets a controller, initialized.
	public abstract Controller getController(String controllerName);
	
	//Gets a service, initialized.
	public abstract Service getService(String serviceName);
	
	//Gets a service for injection, initialized.
	public abstract Service getInjectService(String serviceName);
	
	//Scans and injects dependencies in a controller
	public abstract void inject(Controller controller);
	
	//Ensures that only authorized users use a method or controller.
	public abstract Controller verifySecurity(Controller controller);
	
	//These are methods that should not change much, if at all.
	
	public String filterClassName(String className) {
		//Do not doctor packaged classes.
		if(className.contains(".")) return className;
		else {
			if(className.endsWith("Service")) className = servicePackage + "." + className;
			else if(className.endsWith("Controller")) className = controllerPackage + "." + className;
		}
		return className;
	}

	public Class<?> getClass(String className) {
		className = filterClassName(className);
		try {
			return Class.forName(className);
		} catch(ClassNotFoundException e) {
			System.out.println("Class " + className + " not found.");
			System.out.println("If this is due to an incorrect package location, please use setServicePackage(String), or " +
					"manually set the package in the class name.");
			e.printStackTrace();
			return null;
		}
	}

	public void setControllerPackage(String packageName) {
		this.controllerPackage = packageName;
	}
	
	public void setServicePackage(String packageName) {
		this.servicePackage = packageName;
	}
	
}
