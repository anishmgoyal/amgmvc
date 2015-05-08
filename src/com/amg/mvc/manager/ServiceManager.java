package com.amg.mvc.manager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import com.amg.mvc.annotations.WireAs;
import com.amg.mvc.config.SettingsManager;
import com.amg.mvc.factory.service.Service;
import com.amg.mvc.scan.ItemScanner;

//This object stores initiated instances of all services used by the application
//as singletons. These are very good for database caching.
public class ServiceManager extends AbstractObjectManager<Service> {

	private HashMap<String, Service> services;
	private String defaultPackage;
	
	public ServiceManager(SettingsManager sm) {
		services = new HashMap<String, Service>();
		defaultPackage = sm.getSetting("package.project.base");
		if(!"".equals(defaultPackage)) defaultPackage += ".";
		defaultPackage += sm.getSetting("package.bean.services");
		System.out.println("Default package: " + defaultPackage);
		loadObjects();
	}
	
	@Override
	public Service getObject(String serviceName) {
		serviceName = getObjectKey(serviceName, "Service");
		return services.get(serviceName);
	}

	@Override
	public void addObject(Class<? extends Service> classObj) {
		WireAs wireAs = null;
		if(classObj.isAnnotationPresent(WireAs.class)) {
			wireAs = classObj.getAnnotation(WireAs.class);
		}
		String serviceName;
		if(wireAs != null && !"".equals(wireAs.value())) serviceName = wireAs.value();
		else serviceName = classObj.getPackage().getName() + "." + classObj.getSimpleName();
		try {
			Service service = classObj.newInstance();
			service.init();
			services.put(serviceName, service);
		} catch(Exception e) {
			System.out.println("Error while instantiating " + serviceName);
			if(e.getClass().isAssignableFrom(InstantiationException.class))
				System.out.println("Make sure there is a null constructor for the service.");
			e.printStackTrace();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void loadObjects() {
		LinkedList<Class<?>> classes = ItemScanner.getClasses(Service.class, defaultPackage);
		System.out.println("Found " + classes.size() + " objects.");
		for(Class<?> classObj : classes) {
			//The warning can be ignored since ItemScanner only adds objects that extend the class we offered up.
			//We couldn't cast it in the method, so we cast it here. But, we know that we can cast it.
			//Thus, this is safe.
			addObject((Class<? extends Service>)classObj);
		}
	}
	
	public void destroyServices() {
		for(String key : services.keySet()) {
			Service service = services.get(key);
			service.destroy();
			services.remove(key);
		}
	}

	@Override
	public Set<String> getInternalKeys() {
		return services.keySet();
	}

	@Override
	public String getObjectPackage() {
		return defaultPackage;
	}

}
