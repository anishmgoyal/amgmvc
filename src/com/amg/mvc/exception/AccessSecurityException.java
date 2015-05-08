package com.amg.mvc.exception;

import com.amg.mvc.annotations.Secure;

public class AccessSecurityException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 834330195092663161L;
	
	private Secure securityInfo;
	
	/**
	 * Basic constructor for this exception.
	 */
	public AccessSecurityException() {
		super("A security exception occurred.");
	}
	
	/**
	 * This constructor takes the secure annotation so that it can be used later for redirection.
	 * @param annotation This is the Secure annotation from the class object of the requested
	 * 					 controller/model/view.
	 */
	public AccessSecurityException(Secure annotation) {
		super("A security exception occurred. Redirecting to: " + annotation.redirectAction());
		this.securityInfo = annotation;
	}
	
	/**
	 * This constructor takes the secure annotation so that it can be used later for redirection,
	 * and also takes in an optional message.
	 * @param annotation This is the secure annotation from the class object of the requested
	 * 					 controller/model/view.
	 * @param message This is a message to be used in the exception.
	 */
	public AccessSecurityException(Secure annotation, String message) {
		super(message);
		this.securityInfo = annotation;
	}
	
	/**
	 * This gets the "secure" annotation from the reporter of this exception.
	 * @return A "Secure" annotation containing required role info and redirection info.
	 */
	public Secure getSecurityInfo() {
		return securityInfo;
	}
	
}
