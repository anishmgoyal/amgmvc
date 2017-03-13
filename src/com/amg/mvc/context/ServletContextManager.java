package com.amg.mvc.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.amg.mvc.bean.controller.Controller;
import com.amg.mvc.bean.model.Model;
import com.amg.mvc.bean.service.Service;
import com.amg.mvc.bean.view.View;
import com.amg.mvc.config.SettingsManager;
import com.amg.mvc.manager.ControllerManager;
import com.amg.mvc.manager.ModelManager;
import com.amg.mvc.manager.ServiceManager;
import com.amg.mvc.manager.ViewManager;
import com.amg.mvc.security.SecurityManager;

public class ServletContextManager implements ServletContextListener {

	public static void init(ServletContext context) {
		context.setAttribute("ServletContextManager", new ServletContextManager(context));
	}
	
	public static void destroy(ServletContext context) {
		//As contractually obligated, destroy the services. If there's any cleanup to be done,
		//Now's the time for it to be done.
		((ServletContextManager) context.getAttribute("ServletContextManager"))
										.getServiceManager().destroyServices();
	}
	
	private ServletContext servletContext = null;
	
	private ControllerManager controllerManager = null;
	private ServiceManager serviceManager = null;
	private SecurityManager securityManager = null;
	private ViewManager viewManager = null;
	private ModelManager modelManager = null;
	
	private SettingsManager settingsManager = null;
	
	public ServletContextManager() {
		//Null constructor specifically to allow the program to use this as an interface
		//In a program's life cycle, there will be two of these objects.
		//One that holds actual substance, and another that actually
		//serves as a context listener. This constructor is for the latter.
	}

	public ServletContextManager(ServletContext context) {
		
		this.servletContext = context;
		
		setSettingsManager(new SettingsManager(context));
		
		setControllerManager(new ControllerManager(getSettingsManager()));
		setModelManager(new ModelManager(getSettingsManager()));
		setServiceManager(new ServiceManager(getSettingsManager()));
		setViewManager(new ViewManager(getSettingsManager()));
		controllerManager.setServiceManager(serviceManager);
		modelManager.setServiceManager(serviceManager);
		viewManager.setServiceManager(serviceManager);
		
	}
	
	public void setController(String name, Class<? extends Controller> controller) {
		
	}
	
	public void setModel(String name, Class<? extends Model> model) {
		
	}
	
	public void setService(String name, Class<? extends Service> service) {
		
	}
	
	public void setView(String name, Class<? extends View> view) {
		
	}
	
	//GETTERS
	public ServletContext getServletContext() {
		return servletContext;
	}
	public ControllerManager getControllerManager() {
		return controllerManager;
	}
	public ModelManager getModelManager() {
		return modelManager;
	}
	public ServiceManager getServiceManager() {
		return serviceManager;
	}
	public ViewManager getViewManager() {
		return viewManager;
	}
	public SecurityManager getSecurityManager() {
		return securityManager;
	}
	
	public SettingsManager getSettingsManager() {
		return settingsManager;
	}
	
	//SETTERS
	public void setServletContext(ServletContext context) {
		this.servletContext = context;
		context.setAttribute("ServletContextManager", this);
	}
	public void setControllerManager(ControllerManager controllerManager) {
		this.controllerManager = controllerManager;
	}
	public void setModelManager(ModelManager modelManager) {
		this.modelManager = modelManager;
	}
	public void setServiceManager(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	public void setViewManager(ViewManager viewManager) {
		this.viewManager = viewManager;
	}
	public void setSecurityManager(SecurityManager securityManager) {
		this.securityManager = securityManager;
	}

	public void setSettingsManager(SettingsManager settingsManager) {
		this.settingsManager = settingsManager;
	}

	@Override
	public void contextDestroyed(ServletContextEvent ev) {
		destroy(ev.getServletContext());
	}

	@Override
	public void contextInitialized(ServletContextEvent ev) {
		init(ev.getServletContext());
	}

}
