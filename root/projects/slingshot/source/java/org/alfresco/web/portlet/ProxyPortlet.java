/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.portlet;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletSecurityException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.URLEncoder;

/**
 * Generic JSR-168 Portlet for exposing an Alfresco Web Script as a Portlet.
 *
 * Accepts the following init-config:
 * 
 * scriptUrl => the initial URL to expose e.g. /share/service/sample/cmis/repo
 *  
 * @author davidc
 * @author dward
 * @author kevinr
 */
public class ProxyPortlet implements Portlet
{
    private static Log logger = LogFactory.getLog(ProxyPortlet.class);
    
    private static final String SCRIPT_URL   = "scriptUrl";
    private static final String PORTLET_URL  = "portletUrl";
    private static final String PORTLET_HOST = "portletHost";
    
    // Portlet initialisation
    protected PortletConfig config;
    protected String initScriptUrl;


    /*
     * (non-Javadoc)
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException
    {
        this.config = config;
        this.initScriptUrl = config.getInitParameter(SCRIPT_URL);
    }

    /*
     * (non-Javadoc)
     * @see javax.portlet.Portlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest req, ActionResponse res)
        throws PortletException, PortletSecurityException, IOException
    {
        String scriptUrl = req.getParameter(SCRIPT_URL);
        if (scriptUrl != null)
        {
            res.setRenderParameter(SCRIPT_URL, scriptUrl);
        }
    }

    /*
     * (non-Javadoc)
     * @see javax.portlet.Portlet#render(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void render(RenderRequest req, RenderResponse res)
        throws PortletException, PortletSecurityException, IOException
    {
        PortletMode portletMode = req.getPortletMode();
        if (PortletMode.VIEW.equals(portletMode))
        {
            doView(req, res);
        }
    }

    /*
     * (non-Javadoc)
     * @see javax.portlet.Portlet#destroy()
     */
    public void destroy()
    {
    }

    /**
     * Render Surf view
     * 
     * @param req
     * @param res
     * @throws PortletException
     * @throws PortletSecurityException
     * @throws IOException
     */
    protected void doView(RenderRequest req, RenderResponse res)
        throws PortletException, PortletSecurityException, IOException
    {
        //
        // Establish View URL
        //
        
        String scriptUrl = req.getParameter(SCRIPT_URL);
        if (scriptUrl == null)
        {
            // retrieve initial scriptUrl as configured by Portlet
            scriptUrl = this.initScriptUrl;
            if (scriptUrl == null)
            {
                // If the path parameter has not been provided, forward to the user specific dashboard page
                String userId = req.getRemoteUser();
                if (userId != null)
                {
                    scriptUrl = "/page/user/" + URLEncoder.encode(userId) + "/dashboard";
                }
                else
                {
                    throw new PortletException("Initial 'scriptUrl' parameter has not been specified.");
                }
            }
        }
        
        if (logger.isDebugEnabled())
            logger.debug("Processing portal render request " + req.getScheme() + "://" + req.getServerName() + ":"
                    + req.getServerPort() + "/" + req.getContextPath() + " (scriptUrl=" + scriptUrl + ")");
        
        // apply request attribute to indicate portal mode to Share application
        req.setAttribute(PORTLET_HOST, Boolean.TRUE);
        
        // apply request attribute to enable client-side construction of portlet action URLs
        PortletURL actionUrl = res.createActionURL();
        actionUrl.setParameter(SCRIPT_URL, "$$" + SCRIPT_URL + "$$");
        req.setAttribute(PORTLET_URL, actionUrl.toString());
        
        // render view url
        this.config.getPortletContext().getRequestDispatcher(scriptUrl).include(req, res);
    }
}
