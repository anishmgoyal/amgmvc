package com.amg.mvc.test;

import java.util.LinkedList;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amg.mvc.factory.view.ContentType;
import com.amg.mvc.factory.view.View;
import com.amg.mvc.factory.view.ViewData;

public class TestUnauthView extends View {

	LinkedList<ViewData> data;
	
	@Override
	public LinkedList<ViewData> getViewData() {
		// TODO Auto-generated method stub
		return data;
	}

	@Override
	public void generateView(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		data = new LinkedList<ViewData>();
		data.add(new ViewData(ContentType.PLAINTEXT, "Unauthorized", "You do not have permission to access the given page."));

	}

	@Override
	public void bindParam(String key, Object param) {
		// TODO Auto-generated method stub

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
