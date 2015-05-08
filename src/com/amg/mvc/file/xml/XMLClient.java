package com.amg.mvc.file.xml;

import java.io.File;
import java.io.PrintWriter;
import java.util.LinkedList;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XMLClient {
	
	private NodeList nodes;
	private Document document;
	private String fileName;
	private String defaultDocumentElement;
	private ServletContext sc;
	
	//Constructors
	
	public XMLClient(String fileName) {
		this(fileName, "document");
	}
	
	public XMLClient(String fileName, String defaultDocumentElement) {
		this.fileName = fileName;
		this.defaultDocumentElement = defaultDocumentElement;
		init(false);
	}
	
	public XMLClient(ServletContext sc, String fileName, String defaultDocumentElement) {
		this.sc = sc;
		this.fileName = fileName;
		this.defaultDocumentElement = defaultDocumentElement;
		init(true);
	}
	
	//Initiates the object, and provides support for reloading of the object
	public void init(boolean contextProvided) {
		File file = null;
		try {
			if(!contextProvided) file = new File(fileName);
			else {
				String path = sc.getRealPath("/META-INF/" + fileName);
				if(path != null)
					file = new File(path);
				else {
					fileName = fileName.replace("\\", "/");
					path = sc.getRealPath("/META-INF/" + fileName.substring(0, fileName.indexOf("/"))) + fileName.substring(fileName.indexOf("/"));
					file = new File(path);
				}
			}
			System.out.println("Using bean config file at: " + file.getAbsolutePath());
			if(!file.exists()){
				file.createNewFile();
				setUpNewXMLFile(file);
			}
			
			//Get the XML Document.
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			document = documentBuilder.parse(file);
			
			//Get the nodes of the file.
			nodes = document.getDocumentElement().getChildNodes();
		} catch(Exception e) {
			System.out.println("Error while instantiating XMLClient for " + fileName);
			e.printStackTrace();
		}
	}
	
	//This method creates an xml header in a new XML File
	public void setUpNewXMLFile(File file) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(file, "UTF-8");
			pw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n");
			pw.write("<" + defaultDocumentElement + ">\n");
			pw.write("</" + defaultDocumentElement + ">\n");
			pw.close();
		} catch(Exception e) {
			System.out.println("Error creating new XML File.");
		} finally {
			try {
				pw.close();
			} catch(Exception e) {
				//Seems like it was already closed.
			}
		}
	}
	
	//The following saves changes made to an XML File.
	public void commit() {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(new File(fileName));
			transformer.transform(domSource, streamResult);
			//Reload nodes list
			nodes = document.getDocumentElement().getChildNodes();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//The following methods make changes to an XML File.
	
	public void addNodeToDoc(String nodeName) {
		document.getDocumentElement().appendChild(document.createElement(nodeName));
	}
	
	public void appendElem(Node baseNode, String newElemType, String newElemValue) {
		Element newElem = document.createElement(newElemType);
		newElem.appendChild(document.createTextNode(newElemValue));
		baseNode.appendChild(newElem);
	}
	
	public void removeElem(Node baseNode, String elemType, String elemValue) {
		NodeList nodeList = baseNode.getChildNodes();
		for(int i = 0; i < nodeList.getLength(); i++) {
			Node current = nodeList.item(i);
			if(current.getNodeName().equals(elemType)) {
				String nodeContents = current.getTextContent();
				if(nodeContents.equals(elemValue)) {
					baseNode.removeChild(current);
					break;
				}
			}
		}
	}
	
	//The following methods are related to polling an XML File.
	
	public Node findChildNode(NodeList list, String targetNodeName) {
		for(int i = 0; i < list.getLength(); i++) {
			if(list.item(i).getNodeName().equals(targetNodeName))
				return list.item(i);
		}
		return null;
	}
	
	public LinkedList<String> getChildValues(Node baseNode, String childNodeName) {
		if(baseNode == null) return null;
		LinkedList<String> retVal = new LinkedList<String>();
		NodeList nodeList = baseNode.getChildNodes();
		for(int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = nodeList.item(i);
			if(currentNode.getNodeName().equals(childNodeName)) {
				retVal.add(currentNode.getTextContent());
			}
		}
		return retVal;
	}
	
	public boolean hasChildWithValue(Node baseNode, String elemType, String elemValue) {
		NodeList nodeList = baseNode.getChildNodes();
		for(int i = 0; i < nodeList.getLength(); i++) {
			Node current = nodeList.item(i);
			if(current.getNodeName().equals(elemType)) {
				if(current.getTextContent().equals(elemValue)) return true;
			}
		}
		return false;
	}
	
	//Getters
	public NodeList getNodes() {
		return nodes;
	}
	
	public Document getDocument() {
		return document;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	//Setters
	public void setFileName(String fileName) {
		this.fileName = fileName;
		if(sc == null) init(false);
		else init(true);
	}
	
	//Reloader
	public void reload() {
		if(sc == null) init(false);
		else init(true);
	}
	
	
}
