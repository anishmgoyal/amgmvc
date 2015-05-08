package com.amg.mvc.test;

import com.amg.mvc.factory.service.Service;

public class TestService extends Service {

	private String serviceVariable;
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		serviceVariable = "Test";
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	public void sendMessage() {
		System.out.println("Message dispatch: " + serviceVariable);
	}
	
	public String getMessage() {
		return serviceVariable;
	}

}
