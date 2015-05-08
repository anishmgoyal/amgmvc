package com.amg.mvc.manager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import com.amg.mvc.annotations.WireAs;
import com.amg.mvc.config.SettingsManager;
import com.amg.mvc.factory.model.DataModel;
import com.amg.mvc.factory.model.Model;
import com.amg.mvc.scan.ItemScanner;

public class ModelManager extends AbstractObjectManager<Model> {

	private HashMap<String, Class<? extends Model>> models;
	private String defaultPackage;
	
	public ModelManager(SettingsManager sm) {
		models = new HashMap<String, Class<? extends Model>>();
		defaultPackage = sm.getSetting("package.project.base");
		if(!"".equals(defaultPackage)) defaultPackage += ".";
		defaultPackage += sm.getSetting("package.bean.models");
		loadObjects();
	}
	
	@Override
	public Model getObject(String modelName) {
		modelName = getObjectKey(modelName, "Model");
		try {
			Class<? extends Model> modelClass = models.get(modelName);
			Model model = modelClass.newInstance();
			doWire(model, modelClass);
			return model;
		} catch(Exception e) {
			System.out.println("Unable to get model " + modelName);
			if(e.getClass().isAssignableFrom(InstantiationException.class))
				System.out.println("Make sure there is a null constructor for the model.");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void addObject(Class<? extends Model> classObj) {
		if(DataModel.class.isAssignableFrom(classObj))
			DataModel.register(classObj);
		String modelName;
		WireAs wireAs = null;
		if(classObj.isAnnotationPresent(WireAs.class)) {
			wireAs = classObj.getAnnotation(WireAs.class);
		}
		if(wireAs != null && !"".equals(wireAs.value())) modelName = wireAs.value();
		else modelName = classObj.getPackage().getName() + "." + classObj.getSimpleName();
		models.put(modelName, classObj);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void loadObjects() {
		LinkedList<Class<?>> classes = ItemScanner.getClasses(Model.class, defaultPackage);
		for(Class<?> classObj : classes) {
			//The warning can be ignored since ItemScanner only adds objects that extend the class we offered up.
			//We couldn't cast it in the method, so we cast it here. But, we know that we can cast it.
			//Thus, this is safe.
			addObject((Class<? extends Model>)classObj);
		}
	}

	@Override
	public Set<String> getInternalKeys() {
		return models.keySet();
	}

	@Override
	public String getObjectPackage() {
		return defaultPackage;
	}

}
