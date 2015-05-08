package com.amg.mvc.dispatch;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amg.mvc.config.SettingsManager;
import com.amg.mvc.context.ServletContextManager;
import com.amg.mvc.factory.controller.Controller;
import com.amg.mvc.factory.view.View;

public class RequestFilter implements Filter {

	@Override
	public void destroy() {
		//Do nothing
	}

	@Override
	public void doFilter(ServletRequest srequest, ServletResponse sresponse, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) srequest;
		HttpServletResponse response = (HttpServletResponse) sresponse;
		ServletContext sc = request.getServletContext();
		ServletContextManager scm = new ServletContextManager(sc);
		SettingsManager sm = scm.getSettingsManager();
		DispatchRequest dr = processRequest(request, sm);
		switch(dr.dispatchType) {
		case VIEW: View view = scm.getViewManager().getObject(dr.dispatchObject);
			if(view == null) {
				System.out.println("No such view " + dr.dispatchObject);
			} else view.generateView(request, response);
			break;
		case CONTROLLER: Controller controller = scm.getControllerManager().getObject(dr.dispatchObject);
			if(controller == null) {
				System.out.println("No such controller: " + dr.dispatchObject);
				break;
			}
			View returnedView = null;
			try {
				Method method = controller.getClass().getMethod(dr.dispatchMethod, ServletRequest.class, ServletResponse.class);
				returnedView = (View) method.invoke(controller, request, response);
			} catch(Exception e) {
				returnedView = controller.handleRequest(request, response);
			}
			if(returnedView != null) returnedView.generateView(request, response);
			break;
			default: chain.doFilter(srequest, sresponse);
			break;
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		//Do nothing
	}
	
	public DispatchRequest processRequest(HttpServletRequest request, SettingsManager settings) {
		DispatchType dispatchType = DispatchType.NONE;
		String dispatchObject = null;
		String dispatchMethod = null;
		String[] servletPath = request.getServletPath().split("/");
		if(servletPath.length > 1) {
			String viewPath = settings.getSetting("bean.request.mapping.view.dir").replace("/", "");
			String controllerPath = settings.getSetting("bean.request.mapping.controller.dir").replace("/", "");
			dispatchType = (servletPath[1].equals(viewPath))? DispatchType.VIEW : 
				(servletPath[1].equals(controllerPath))? DispatchType.CONTROLLER : dispatchType;
			if(servletPath.length > 2) {
				dispatchObject = servletPath[2];
				if(servletPath.length > 3) {
					dispatchMethod = servletPath[3];
				}
			}
		}
		return new DispatchRequest(dispatchType, dispatchObject, dispatchMethod);
		/*DispatchType dispatchType = (request.getServletPath().equals(settings.getSetting("bean.request.mapping.view.dir")))? DispatchType.VIEW :
			(request.getServletPath().equals(settings.getSetting("bean.request.mapping.controller.dir")))? DispatchType.CONTROLLER : DispatchType.NONE;
		if(dispatchType == DispatchType.NONE) return new DispatchRequest(dispatchType, null, null); 
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
		return dispatchRequest;*/
	}

}
