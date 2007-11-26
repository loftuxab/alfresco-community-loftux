package org.alfresco.web.scripts.portlet;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.alfresco.web.scripts.Authenticator;

public interface PortletAuthenticatorFactory
{
    public Authenticator create(RenderRequest req, RenderResponse res);
}
