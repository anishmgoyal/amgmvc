package com.amg.mvc.scan;

import java.util.LinkedList;


public class ItemScanner {
	
	public static void main(String[] args) {
		
		prn(getClasses(Object.class, "javax", true, ItemScanner.class.getClassLoader()));
	}
	
	public static void prn(LinkedList<Class<?>> ll) {
		for(Class<?> classObj : ll) {
			System.out.println(classObj);
		}
	}
	
	//Generic redirects
	public static LinkedList<Class<?>> getClasses(String type, String packageName) {
		return getClasses(type, packageName, false, ItemScanner.class.getClassLoader());
	}
	public static LinkedList<Class<?>> getClasses(String type, String packageName, boolean recursive) {
		return getClasses(type, packageName, recursive, ItemScanner.class.getClassLoader());
	}
	public static LinkedList<Class<?>> getClasses(String type, String packageName, ClassLoader classLoader) {
		return getClasses(type, packageName, false, classLoader);
	}

	//Are we using a configuration where the type is DEFINITELY appended to the className?
	public static LinkedList<Class<?>> getClasses(String type, String packageName, boolean recursive, ClassLoader classLoader) {
		LinkedList<Class<?>> retVal = new LinkedList<Class<?>>();
		for(Class<?> classObj : PackageScanner.getClasses(packageName, recursive, classLoader)) {
			if(classObj.getSimpleName().endsWith(type)) {
				System.out.println("Scanned in a class, " + classObj.getName());
				retVal.add(classObj);
			}
		}
		return retVal;
	}
	
	//Generic redirects
	public static LinkedList<Class<?>> getClasses(Class<?> type, String packageName) {
		return getClasses(type, packageName, false, ItemScanner.class.getClassLoader());
	}
	public static LinkedList<Class<?>> getClasses(Class<?> type, String packageName, boolean recursive) {
		return getClasses(type, packageName, recursive, ItemScanner.class.getClassLoader());
	}
	public static LinkedList<Class<?>> getClasses(Class<?> type, String packageName, ClassLoader classLoader) {
		return getClasses(type, packageName, false, classLoader);
	}
	
	//Are we using a configuration where the only real indicator of validity is where the class implements or extends an interface or class?
	public static LinkedList<Class<?>> getClasses(Class<?> type, String packageName, boolean recursive, ClassLoader classLoader) {
		LinkedList<Class<?>> retVal = new LinkedList<Class<?>>();
		for(Class<?> classObj : PackageScanner.getClasses(packageName, recursive, classLoader)) {
			if(type.isAssignableFrom(classObj)) retVal.add(classObj);
		}
		return retVal;
	}
	
}
