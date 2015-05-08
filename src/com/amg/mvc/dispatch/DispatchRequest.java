package com.amg.mvc.dispatch;

public class DispatchRequest {
	public DispatchType dispatchType;
	public String dispatchObject;
	public String dispatchMethod;
	public DispatchRequest(DispatchType dispatchType, String dispatchObject, String dispatchMethod) {
		this.dispatchType = dispatchType;
		this.dispatchObject = dispatchObject;
		this.dispatchMethod = dispatchMethod;
	}
}