package com.amg.mvc.manager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import com.amg.mvc.annotations.WireAs;
import com.amg.mvc.config.SettingsManager;
import com.amg.mvc.factory.view.View;
import com.amg.mvc.scan.ItemScanner;

public class ViewManager extends AbstractObjectManager<View> {

	private HashMap<String, Class<? extends View>> views;
	private String defaultPackage;
	
	public ViewManager(SettingsManager sm) {
		views = new HashMap<String, Class<? extends View>>();
		defaultPackage = sm.getSetting("package.project.base");
		if(!"".equals(defaultPackage)) defaultPackage += ".";
		defaultPackage += sm.getSetting("package.bean.views");
		loadObjects();
	}
	
	@Override
	public View getObject(String viewName) {
		viewName = getObjectKey(viewName, "View");
		try {
			Class<? extends View> viewClass = views.get(viewName);
			View view = viewClass.newInstance();
			doWire(view, viewClass);
			return view;
		} catch(Exception e) {
			System.out.println("Unable to get view " + viewName);
			if(e.getClass().isAssignableFrom(InstantiationException.class))
				System.out.println("Make sure there is a null constructor for the view.");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void addObject(Class<? extends View> classObj) {
		String viewName;
		WireAs wireAs = null;
		if(classObj.isAnnotationPresent(WireAs.class)) {
			wireAs = classObj.getAnnotation(WireAs.class);
		}
		if(wireAs != null && !"".equals(wireAs.value())) viewName = wireAs.value();
		else viewName = classObj.getPackage().getName() + "." + classObj.getSimpleName();
		views.put(viewName, classObj);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void loadObjects() {
		LinkedList<Class<?>> classes = ItemScanner.getClasses(View.class, defaultPackage);
		for(Class<?> classObj : classes) {
			//The warning can be ignored since ItemScanner only adds objects that extend the class we offered up.
			//We couldn't cast it in the method, so we cast it here. But, we know that we can cast it.
			//Thus, this is safe.
			addObject((Class<? extends View>)classObj);
		}
	}

	@Override
	public Set<String> getInternalKeys() {
		return views.keySet();
	}

	@Override
	public String getObjectPackage() {
		return defaultPackage;
	}

}
