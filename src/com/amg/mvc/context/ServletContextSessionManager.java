package com.amg.mvc.context;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.servlet.ServletContext;

import com.amg.mvc.annotations.Secure;
import com.amg.mvc.annotations.Wire;
import com.amg.mvc.bean.controller.Controller;
import com.amg.mvc.bean.service.Service;
import com.amg.mvc.manager.ControllerManager;
import com.amg.mvc.manager.ServiceManager;
import com.amg.mvc.security.SecurityManager;
import com.amg.mvc.user.UserRoles;

public class ServletContextSessionManager extends AbstractContextSessionManager {

	ServletContext application;
	ServletContextManager contextManager;
	ControllerManager controllerManager;
	ServiceManager serviceManager;
	SecurityManager securityManager;
	UserRoles userRoles;
	
	public ServletContextSessionManager(ServletContext application) {
		this(application, null);
	}
	
	public ServletContextSessionManager(ServletContext application, UserRoles userRoles) {
		this.application = application;
		this.contextManager = (ServletContextManager) application.getAttribute("ServletContextManager");
		this.controllerManager = contextManager.getControllerManager();
		this.serviceManager = contextManager.getServiceManager();
		this.securityManager = contextManager.getSecurityManager();
		this.userRoles = userRoles;
	}
	
	@Override
	public Controller getController(String controllerName) {
		if(!controllerName.endsWith("Controller")) controllerName += "Controller";
		controllerName = filterClassName(controllerName);
		try {
			Controller retVal = controllerManager.getObject(controllerName);
			Class<? extends Controller> controllerClass = retVal.getClass();
			if(controllerClass.isAnnotationPresent(Secure.class))
				retVal = verifySecurity(retVal);
			if(retVal != null) inject(retVal);
			return retVal;
		} catch(Exception e) {
			System.out.println("Could not get controller.");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Service getService(String serviceName) {
		if(!serviceName.endsWith("Service")) serviceName += "Service";
		serviceName = filterClassName(serviceName);
		Service retVal = serviceManager.getObject(serviceName);
		return retVal;
	}

	@Override
	public void inject(Controller controller) {
		Class<? extends Controller> classObj = controller.getClass();
		boolean enableWireAll = classObj.isAnnotationPresent(Wire.class);
		for(Field field : classObj.getFields()) {
			if(field.getType().getSimpleName().endsWith("Service") && (enableWireAll || field.isAnnotationPresent(Wire.class))) {
				try {
					String serviceName = field.getType().getPackage().getName() + "." + field.getType().getSimpleName();
					Service serviceToInject = getInjectService(serviceName);
					if(field.isAccessible()) field.set(controller, serviceToInject);
				} catch(Exception e) {
					System.out.println("Could not inject service " + field.getType().getSimpleName());
					e.printStackTrace();
				}
			}
		}
		for(Method method : classObj.getMethods()) {
			Class<?>[] params = method.getParameterTypes();
			if(params.length == 1 && params[0].getSimpleName().endsWith("Service") && (enableWireAll || method.isAnnotationPresent(Wire.class))) {
				try {
					String serviceName = params[0].getPackage().getName() + "." + params[0].getSimpleName();
					Service serviceToInject = getInjectService(serviceName);
					method.invoke(controller, serviceToInject);
				} catch(Exception e) {
					System.out.println("Could not inject service " + params[0].getClass().getSimpleName());
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public Service getInjectService(String serviceName) {
		return serviceManager.getObject(serviceName);
	}

	@Override
	public Controller verifySecurity(Controller controller) {
		// TODO Auto-generated method stub
		Class<? extends Controller> classObj = controller.getClass();
		boolean secure = false;
		String redirectAction = "";
		if(classObj.isAnnotationPresent(Secure.class)) {
			Secure securityAnnotation = classObj.getAnnotation(Secure.class);
			String[] roles = securityAnnotation.roles();
			redirectAction = securityAnnotation.redirectAction();
			boolean requireAll = securityAnnotation.requireAll();
			secure = securityManager.verifySecurity(userRoles, roles, requireAll);
		}
		if(!secure) System.out.println("Redirection to " + redirectAction);
		return (secure)? controller : null;
	}

}
