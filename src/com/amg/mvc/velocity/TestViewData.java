package com.amg.mvc.velocity;

public class TestViewData {

	public String header;
	public String body;
	public TestViewData(String header, String body) {
		this.header = header;this.body = body;
	}
	
	public String getHeader() {return header;}
	public String getBody() {return body;}
}
