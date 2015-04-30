package org.alfresco.enterprise.repo.officeservices.service;

import org.alfresco.service.cmr.security.AuthenticationService;

import com.xaldon.officeservices.UserData;

public class AuthenticationServiceUserData implements UserData
{

	protected AuthenticationService authenticationService;
	
	public AuthenticationServiceUserData(AuthenticationService authService)
	{
		authenticationService = authService;
	}
	
    @Override
	public String getUsername()
	{
		return authenticationService.getCurrentUserName();
	}

}
