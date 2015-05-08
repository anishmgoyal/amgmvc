package com.amg.mvc.config;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;

public class BundleLoader {
	
	public static ResourceBundle getBundle(String name, String directory) {
		return getBundle(name, Locale.getDefault(), directory);
	}
	
	public static ResourceBundle getBundle(String name, Locale targetLocale, String directory) {
		File file = new File(directory);
		try {
			URL[] urls = new URL[]{file.toURI().toURL()};
			ClassLoader loader = new URLClassLoader(urls);
			return ResourceBundle.getBundle(name, targetLocale, loader);
		} catch(Exception e) {
			System.out.println("Error getting bundle " + name + " from " + directory);
			return null;
		}
		
	}
	
}
