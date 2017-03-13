package com.amg.mvc.velocity;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.amg.mvc.bean.view.View;
import com.amg.mvc.bean.view.ViewData;
import com.amg.mvc.config.SettingsManager;
import com.amg.mvc.context.ServletContextManager;

public class VelocityTemplateBuilder {
	
	public static String generateHTML(ServletContextManager scm, View view, String template) {
		ServletContext sc = scm.getServletContext();
		SettingsManager sm = scm.getSettingsManager();
		Properties props = new Properties();
		props.setProperty("file.resource.loader.path", sc.getRealPath(sm.getSetting("resource.path.templates")));
		return generateHTML(props, view, template);
	}
	
	public static String generateHTML(View view, String template) {
		Properties props = new Properties();
		props.setProperty("file.resource.loader.path", "templates/");
		return generateHTML(props, view, template);
	}
	
	public static String generateHTML(Properties props, View view, String template) {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.init(props);
		Template t = velocityEngine.getTemplate(template + ".vm");
		VelocityContext context = new VelocityContext();

		context.put("viewData", new LinkedList<ViewData>()); //Default, empty.. in case.
		if(view != null) {
			if(view.getViewData() != null) {
				LinkedList<ViewData> ll = view.getViewData();
				context.put("viewData", ll);
			}
			
			if(view.getKeys() != null) for(String parameter : view.getKeys()) {
				context.put(parameter, view.getParam(parameter));
			}
		}
		
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		return writer.toString();
	}
	
}