package com.amg.mvc.bean.view;

public class ViewData {

	private ContentType contentType;
	private String title;
	private Object data;
	
	public ViewData(ContentType contentType, String title, Object data) {
		this.contentType = contentType;
		this.data = data;
		this.title = title;
	}
	
	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void bindData(Object data) {
		this.data = data;
	}
	
	public ContentType getContentType() {return contentType;}
	
	public String getTitle() {return title;}
	
	public Object getData() {return data;}
	
}
