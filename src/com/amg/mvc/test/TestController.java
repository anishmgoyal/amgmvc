package com.amg.mvc.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amg.mvc.annotations.Secure;
import com.amg.mvc.annotations.Wire;
import com.amg.mvc.bean.controller.Controller;
import com.amg.mvc.bean.view.View;

@Wire
@Secure(roles={"USER_READ", "USER_WRITE"}, redirectAction="/view/testunauthview")
public class TestController extends Controller {

	private TestService testService;
	
	@Override
	public View handleRequest(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		testService.sendMessage();
		View view = new TestView();
		view.bindParam("message", testService.getMessage());
		view.bindParam("recepient", "Anish Manu Goyal");
		return view;
	}
	
	public void setTestService(TestService testService) {
		this.testService = testService;
	}
	
	public TestService getTestService() {
		return this.testService;
	}

}
