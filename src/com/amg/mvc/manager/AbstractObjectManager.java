package com.amg.mvc.manager;

import java.lang.reflect.Field;
import java.util.Set;

import com.amg.mvc.annotations.Secure;
import com.amg.mvc.annotations.Wire;
import com.amg.mvc.exception.AccessSecurityException;
import com.amg.mvc.factory.service.Service;
import com.amg.mvc.user.UserRoles;

/**
 * This class is used for storing objects in a way that the ServletContextManager
 * can reference and pull objects from.
 * @author Anish Goyal
 *
 * @param <T> This is the type of object which the Manager stores
 */
public abstract class AbstractObjectManager<T> {
	
	/**
	 * This field is here for the "doWire()" method.
	 */
	private ServiceManager serviceManager;
	
	/**
	 * Sets the object manager's source for beans.
	 * @param serviceManager This is the service manager that has the
	 * 						 bean objects for this object manager.
	 */
	public void setServiceManager(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	
	/**
	 * This method gets an object from the manager. It ensures that the
	 * object is in fact initialized.
	 * @param key This is a string that can be used to access objects
	 * 		  stored internally.
	 * @return Whatever this manager stores, be it Controllers, Models,
	 * 		   Services, Views, or something else.
	 */
	public abstract T getObject(String key);
	
	
	/**
	 * This method loads a single object into its internal storage.
	 * By taking just a single class object as a parameter, the manager
	 * retains flexibility in whether or not the object is stored in an
	 * initialized state, or whether it should be initialized on access.
	 * @param classObj The class object for the object to be stored.
	 */
	public abstract void addObject(Class<? extends T> classObj);
	
	
	/**
	 * This method is called when the Object Manager is first initialized.
	 * It can and should be used for scanning and filling the internal
	 * mapping with objects. 
	 */
	public abstract void loadObjects();
	
	
	/**
	 * This method gets a set of keys for objects stored in the manager.
	 * It is necessary for this to be implemented in order for getObjectKey
	 * to work properly.
	 * @return A Set containing the keys under which objects are stored.
	 */
	public abstract Set<String> getInternalKeys();
	
	
	/**
	 * This method interacts with the project's settings manager, or hardcoded
	 * settings, to get the package in which the current manager's objects are
	 * located.
	 * @return A string representing the location of the main package this
	 * 		   manager will interact with. 
	 */
	public abstract String getObjectPackage();

	
	/**
	 * This method helps filter through input keys when retrieving stored objects
	 * and allows for more flexibility in the naming conventions through which
	 * they can be manually mapped.
	 * @param key The requested object - for example, when getting a Model called
	 * 			  "User", "UserModel" may be passed in.
	 * @param suffix The standard suffix for this type of object. Controllers end
	 * 				 with "Controller", etc.
	 * @param packageName This is for if an object is being requested by its class
	 * 				      object. If it is not specified, the default object package
	 * 				      will be used.
	 * @return A key with which the object can be accessed from the internal map,
	 * 		   or null if this method cannot determine one.
	 */
	public String getObjectKey(String key, String suffix) {
		
		//This is the default package under which the relevant objects are stored.
		String packageName = getObjectPackage();
		//This is the set of keys under which objects have been mapped.
		Set<String> keySet = getInternalKeys();
		
		//The baseline case is the easiest. We have the key, just give it back.
		if(keySet.contains(key)) return key;
		
		//Let's check and see if the key was given without the object's suffix (E.G. User instead of UserModel) 
		String keyWithSuffix = key.concat(suffix);
		if(keySet.contains(keyWithSuffix)) return keyWithSuffix;
		
		//Nope, chances are this object wasn't mapped, if it exists.
		//While it is best to use an annotation to set the wire context or alternatively
		//retrieve an object by its fully qualified name, it is worth checking for the
		//object in any case.
		else {
			if (packageName == null) packageName = getObjectPackage();
			if(packageName.length() > 0) {
				if(!packageName.endsWith(".")) packageName += ".";
				key = packageName + key;
				keyWithSuffix = packageName + keyWithSuffix;
				if(keySet.contains(key)) return key;
				else if(keySet.contains(keyWithSuffix)) return keyWithSuffix;
			}
			
		}
		
		//We didn't quite find anything here, so return null to make this clear.
		return null;
	}
	
	/**
	 * This method is used to wire the beans, or in the case of this framework,
	 * services, to controllers, models, and views. If the service manager is not
	 * set for a manager, then there will be no source for the beans and so the
	 * method returns.
	 * @param t This is the object who's fields need to be set.
	 * @param tClass This is the class file for the object, used to get a list of fields
	 */
	public void doWire(T t, Class<? extends T> tClass) {
		if(serviceManager == null) return;
		boolean wireAll = tClass.isAnnotationPresent(Wire.class);
		Field[] fields = tClass.getDeclaredFields();
		for(Field field : fields) {
			if(wireAll || field.isAnnotationPresent(Wire.class)) {
				Wire wire = field.getAnnotation(Wire.class);
				String val = null;
				String altVal = null;
				if(wire != null && !"".equals(wire.value())) {
					val = wire.value();
				} else {
					val = field.getName();
				}
				altVal = tClass.getSimpleName();
				try {
					boolean isAccessible = field.isAccessible();
					field.setAccessible(true);
					//Here, we either go by the Wire value, the field name, or fallback to the classname of the field.
					Service s = serviceManager.getObject(val);
					if(s == null) s = serviceManager.getObject(altVal);
					field.set(t, s);
					field.setAccessible(isAccessible);
				} catch (Exception e) {
					System.out.println("Unable to wire service " + val + ".");
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * If a class has a security annotation, the roles of the current user are verified with the roles
	 * required by the object. If a security error occurs, an exception is thrown.
	 * @param tClass This is the class object for the requested model/view/controller.
	 * @param roles These are the roles 
	 * @throws AccessSecurityException
	 */
	public void checkSecurity(Class<? extends T> tClass, UserRoles roles) throws AccessSecurityException {
		if(!tClass.isAnnotationPresent(Secure.class)) return;
		Secure annotation = tClass.getAnnotation(Secure.class);
		if(roles == null) throw new AccessSecurityException(annotation, "No roles supplied. To be safe, access is denied.");
		String[] allowedRoles = annotation.roles();
		boolean requireAll = annotation.requireAll();
		boolean isSecure = requireAll;
		for(String role : allowedRoles) {
			if(role.equals("all")) {
				return;
			}
			if(roles.hasRole(role)) {
				if(!requireAll) return;
			} else {
				isSecure = false;
			}
		}
		if(!isSecure) throw new AccessSecurityException(annotation);
	}
	
}
