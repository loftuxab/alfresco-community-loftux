package org.alfresco.repo.transfer.fsr;

import org.springframework.extensions.webscripts.AbstractBasicHttpAuthenticatorFactory;
import org.springframework.extensions.webscripts.Description.RequiredAuthentication;

public class WebscriptAuthenticatorFactoryImpl extends AbstractBasicHttpAuthenticatorFactory
{
    private String permittedUsername;
    private String permittedPassword;

    public void setPermittedUsername(String permittedUsername)
    {
        this.permittedUsername = permittedUsername;
    }

    public void setPermittedPassword(String permittedPassword)
    {
        this.permittedPassword = permittedPassword;
    }

    @Override
    public boolean doAuthenticate(String username, String password)
    {
        return (username != null && username.equals(permittedUsername) && password != null && 
                password.equals(permittedPassword));
    }

    @Override
    public boolean doAuthorize(String username, RequiredAuthentication role)
    {
        return username != null && username.equals(permittedUsername);
    }

}
