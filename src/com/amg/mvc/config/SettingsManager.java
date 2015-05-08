package com.amg.mvc.config;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;

import org.w3c.dom.Node;

import com.amg.mvc.file.xml.XMLClient;

public class SettingsManager {

	private XMLClient client;
	private HashMap<String, String> settings;
	private ServletContext sc;
	
	public SettingsManager() {
		this.settings = new HashMap<String, String>();
		loadFromProperties(true);
		XMLClient client = new XMLClient(getConfigFilePath("resource.path.config", "config.file.beans"), "beans");
		this.client = client;
	}
	
	public SettingsManager(ServletContext sc) {
		this.sc = sc;
		this.settings = new HashMap<String, String>();
		loadFromProperties(true);
		XMLClient client = new XMLClient(sc, getConfigFilePath("resource.path.config", "config.file.beans"), "beans");
		this.client = client;
	}
	
	//These methods load .properties settings
	public void loadFromProperties(boolean defaultPropFile) {
		ResourceBundle bundle;
		if(defaultPropFile) {
			bundle = ResourceBundle.getBundle("com.amg.mvc.constants.constants");
		} else {
			String bundleName = getSetting("config.file.settings").replace(".properties", "");
			String bundlePath = getSetting("resource.path.config");
			if(!bundlePath.endsWith("/")) bundlePath += "/";
			bundlePath += getSetting("config.file.settings");
			if(sc == null) {
				bundle = BundleLoader.getBundle(bundleName, getSetting("resource.path.config"));
			} else {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(sc.getRealPath("/META-INF/" + bundlePath));
					bundle = new PropertyResourceBundle(fis);
				} catch(Exception e) {
					//The file wasn't found, so we don't want to configure anything...
					System.out.println("No settings property file provided. Assuming none exists.");
					//Silently fail.
					bundle = null;
				} finally {
					if(fis != null) try {fis.close();} catch(Exception e) {/*Tried*/}
				}
			}
			if(bundle == null) return;
		}
		for(String key : bundle.keySet()) {
			settings.put(key, bundle.getString(key));
		}
		if(defaultPropFile) loadFromProperties(false);
	}
	
	
	public String getSetting(String key) {return settings.get(key);}
	
	
	public String getPackage(String packageType) {
		String subPackage = settings.get(packageType);
		String basePackage = settings.get("package.project.base");
		if(basePackage.length() > 0 && !basePackage.endsWith(".")
				&& subPackage != null && !subPackage.matches("|")) basePackage += ".";
		return basePackage + subPackage;
	}
	
	
	private String getConfigFilePath(String directoryType, String fileType) {
		String dirName = settings.get(directoryType);
		if(!dirName.endsWith("/") && !dirName.endsWith("\\")) dirName += "/";
		String fileName = settings.get(fileType);
		return dirName + fileName;
	}
	
	public String getResource(String directory, String fileName) {
		String dirName = settings.get("resource.path." + directory);
		if(!dirName.endsWith("/") && !dirName.endsWith("\\")) dirName += "/";
		return dirName + fileName;
	}
	
	public void setSetting(String key, String value) {settings.put(key, value);}
	
	//This adds tags that aren't already in the config file
	public Node addNodeToConfig(String nodeName) {
		client.addNodeToDoc(nodeName);
		client.commit();
		return client.findChildNode(client.getNodes(), nodeName);
	}
	
	//This gets a node, and adds it to file if necessary.
	public Node getFileNode(String nodeName) {
		Node retVal = client.findChildNode(client.getNodes(), nodeName);
		if(retVal == null) {
			retVal = addNodeToConfig(nodeName);
			retVal.setTextContent("");
		}
		return retVal;
	}
	
	//OBJECT REGISTRY
	
	//These are getters for the object registry
	
	//This is a generic version of this method
	
	public LinkedList<String> getObjectList(String type) {
		Node baseNode = getFileNode(type + "s");
		return client.getChildValues(baseNode, type);
	}
	
	//These are the setters for the object registry
	
	public void registerObject(String type, String name) {
		Node baseElem = getFileNode(type + "s");
		//No duplicates
		if(!client.hasChildWithValue(baseElem, type, name))
				client.appendElem(baseElem, type, name);
		client.commit();
	}
	
	public void removeObject(String type, String name) {
		Node baseElem = getFileNode(type + "s");
		client.removeElem(baseElem, type, name);
		client.commit();
	}
	
	
}
