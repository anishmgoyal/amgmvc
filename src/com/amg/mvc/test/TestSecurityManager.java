package com.amg.mvc.test;

import com.amg.mvc.security.SecurityManager;
import com.amg.mvc.user.UserRoles;

public class TestSecurityManager implements SecurityManager {

	@Override
	public boolean verifySecurity(UserRoles user, String[] requiredRoles, boolean requireAll) {
		// TODO Auto-generated method stub
		boolean isAuth = requireAll;
		for(String role : requiredRoles) {
			boolean hasRole = user.hasRole(role);
			if(hasRole && !requireAll) {
				isAuth = true;
				break;
			} else if(!hasRole && requireAll) {
				isAuth = false;
				break;
			}
		}
		return isAuth;
	}
	
}
