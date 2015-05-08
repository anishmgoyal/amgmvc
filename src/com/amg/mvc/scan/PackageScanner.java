package com.amg.mvc.scan;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PackageScanner {
	
	public static void main(String[] args) {
		System.out.println(getClasses("javax.servlet", false));
	}

	//The following three methods are redirects for flexible use that eventually go to
	//the getClasses method.
	public static LinkedList<Class<?>> getClasses(String packageName) {
		return getClasses(packageName, false);
	}
	public static LinkedList<Class<?>> getClasses(String packageName, ClassLoader classLoader) {
		return getClasses(packageName, false, classLoader);
	}
	public static LinkedList<Class<?>> getClasses(String packageName, boolean recursive) {
		ClassLoader classLoader = PackageScanner.class.getClassLoader();
		return getClasses(packageName, recursive, classLoader);
	}
	
	//This initializes a scan for a given class loader.
	public static LinkedList<Class<?>> getClasses(String packageName, boolean recursive, ClassLoader classLoader) {
		LinkedList<Class<?>> retVal = new LinkedList<Class<?>>();
		try {
			Enumeration<URL> resources = classLoader.getResources(packageName.replace(".", "/"));
			
			while(resources.hasMoreElements()) {
				URL url = resources.nextElement();
				String path = URLDecoder.decode(url.getFile(), "UTF-8");
				File file = new File(path);
				if(file.exists() && file.isDirectory()) {
					retVal.addAll(getClassesFromDir(packageName, file, recursive));
				} else {
					URLConnection connection = url.openConnection();
					if(connection instanceof JarURLConnection) {
						retVal.addAll(getClassesFromJar(packageName, (JarURLConnection) connection, recursive));
					}
				}
			}
			
		} catch(Exception e) {
			System.out.println("Error while scanning classes from package " + packageName);
			e.printStackTrace();
		}
		return retVal;
	}
	
	public static LinkedList<Class<?>> getClassesFromDir(String packageName, File directory, boolean recursive) {
		LinkedList<Class<?>> retVal = new LinkedList<Class<?>>();
		for(String fileName : directory.list()) {
			//Check if it is a class file.
			if(fileName.endsWith(".class")) {
				//If it is, get rid of the extension. We don't need it.
				String className = fileName.substring(0, fileName.length()-6);
				//Try to load the class.
				try {
					retVal.add(Class.forName(packageName + "." + className));
				} catch(Exception e) {
					System.out.println("Error loading class " + packageName + "." + className);
					e.printStackTrace();
				}
			} else if(recursive) {
				//This isn't a class file, and we are searching deeper
				//levels of the packages... so check if it's a directory.
				File currFile = new File(directory, fileName);
				if(currFile.isDirectory()) {
					retVal.addAll(getClassesFromDir(packageName + "." + fileName, currFile, recursive));
				}
			}
		}
		return retVal;
	}
	
	public static LinkedList<Class<?>> getClassesFromJar(String packageName, JarURLConnection connection, boolean recursive) {
		LinkedList<Class<?>> retVal = new LinkedList<Class<?>>();
		try {
			JarFile jarFile = connection.getJarFile();
			Enumeration<JarEntry> entries = jarFile.entries();
			while(entries.hasMoreElements()) {
				JarEntry file = entries.nextElement();
				//Get the fileName and format as a package.
				String fileName = file.getName().replace("/", ".");
				//Class files only.
				if(fileName.endsWith(".class")) {
					//If this is a class, get rid of the extension. We don't need it.
					fileName = fileName.substring(0, fileName.length()-6);
					boolean isValid = false;
					//If we are taking recursive searches, and the
					//class is part of a level of the package, include it.
					if(recursive && fileName.startsWith(packageName))
						isValid = true;
					else if(!recursive) {
						//Remove package name from package to see if there
						//is a deeper level of package in the jar
						int length = packageName.length() + 1;
						//To avoid an indexOutOfBoundsException, use an if statement.
						if(length < fileName.length()) {
							String simpleName = fileName.substring(packageName.length() + 1);
							//If this class is directly in this package, there should be
							//no periods left.
							if(!simpleName.contains(".")) isValid = true;
						}
					}
					//Load the class, if possible.
					if(isValid) {
						try {
							retVal.add(Class.forName(fileName));
						} catch(Exception e) {
							System.out.println("Error loading " + fileName);
							e.printStackTrace();
						}
					}
				}
			}
		} catch(Exception e) {
			System.out.println("Error while reading jar file for package " + packageName);
		}
		return retVal;
	}
	
}
