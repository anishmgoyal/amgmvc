package com.amg.mvc.user;

public interface UserRoles {
		
	public boolean hasRole(String role);
	
	public void addRole(String role);
	
	public void removeRole(String role);
	
}
