package org.alfresco.web.scripts.servlet;

import org.alfresco.web.scripts.Authenticator;

public interface ServletAuthenticatorFactory
{
    public Authenticator create(WebScriptServletRequest req, WebScriptServletResponse res);
}
