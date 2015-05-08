package com.amg.mvc.security;

import com.amg.mvc.user.UserRoles;

public interface SecurityManager {

	public boolean verifySecurity(UserRoles user, String[] requiredRoles, boolean requireAll);
	
}
