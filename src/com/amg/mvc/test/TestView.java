package com.amg.mvc.test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amg.mvc.factory.view.ContentType;
import com.amg.mvc.factory.view.View;
import com.amg.mvc.factory.view.ViewData;

public class TestView extends View {

	private LinkedList<ViewData> data;
	private HashMap<String, Object> parameters = null;
	
	@Override
	public LinkedList<ViewData> getViewData() {
		return data;
	}

	@Override
	public void generateView(HttpServletRequest request,
			HttpServletResponse response) {
		data = new LinkedList<ViewData>();
		String message = (String) parameters.get("message");
		String recepient = (String) parameters.get("recepient");
		data.add(new ViewData(ContentType.PLAINTEXT, "Welcome!",
				"The message sent was: " + message + ".\nIt was sent to: " + recepient));
		data.add(new ViewData(ContentType.PLAINTEXT, "Is this correct?",
				"If the message has been sent to the wrong person,\n ensure that " +
				"you report the issue to anishgoyal@yahoo.com."));
	}

	@Override
	public void bindParam(String key, Object param) {
		if(parameters == null) parameters = new HashMap<String, Object>();
		parameters.put(key, param);
	}

	@Override
	public Object getParam(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getKeys() {
		// TODO Auto-generated method stub
		return null;
	}

}
