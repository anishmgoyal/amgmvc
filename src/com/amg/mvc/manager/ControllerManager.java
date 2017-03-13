package com.amg.mvc.manager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import com.amg.mvc.annotations.WireAs;
import com.amg.mvc.bean.controller.Controller;
import com.amg.mvc.config.SettingsManager;
import com.amg.mvc.scan.ItemScanner;

//This object stores the class objects for controllers, returning a new instance
//on every request.
public class ControllerManager extends AbstractObjectManager<Controller> {

	private HashMap<String, Class<? extends Controller>> controllers;
	private String defaultPackage;
	
	public ControllerManager(SettingsManager sm) {
		controllers = new HashMap<String, Class<? extends Controller>>();
		defaultPackage = sm.getSetting("package.project.base");
		if(!"".equals(defaultPackage)) defaultPackage += ".";
		defaultPackage += sm.getSetting("package.bean.controllers");
		loadObjects();
	}
	
	@Override
	public Controller getObject(String controllerName) {
		controllerName = getObjectKey(controllerName, "Controller");
		try {
			Class<? extends Controller> controllerClass = controllers.get(controllerName);
			Controller controller = controllerClass.newInstance();
			doWire(controller, controllerClass); //Wire the object with any necessary beans.
			return controller;
		} catch(Exception e) {
			System.out.println("Unable to get controller " + controllerName);
			if(e.getClass().isAssignableFrom(InstantiationException.class))
				System.out.println("Make sure there is a null constructor for the controller.");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void addObject(Class<? extends Controller> classObj) {
		String controllerName;
		WireAs wireAs = null;
		if(classObj.isAnnotationPresent(WireAs.class)) {
			wireAs = classObj.getAnnotation(WireAs.class);
		}
		if(wireAs != null && !"".equals(wireAs.value())) controllerName = wireAs.value();
		else controllerName = classObj.getPackage().getName() + "." + classObj.getSimpleName();
		controllers.put(controllerName, classObj);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void loadObjects() {
		LinkedList<Class<?>> classes = ItemScanner.getClasses(Controller.class, defaultPackage);
		for(Class<?> classObj : classes) {
			//The warning can be ignored since ItemScanner only adds objects that extend the class we offered up.
			//We couldn't cast it in the method, so we cast it here. But, we know that we can cast it.
			//Thus, this is safe.
			addObject((Class<? extends Controller>)classObj);
		}
	}

	@Override
	public Set<String> getInternalKeys() {
		return controllers.keySet();
	}

	@Override
	public String getObjectPackage() {
		return defaultPackage;
	}
	
}
