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
import java.net.URLEncoder;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletSecurityException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Generic JSR-168 Portlet for exposing an Alfresco Web Script as a Portlet.
 *
 * Accepts the following init-config:
 * 
 * scriptUrl => the initial URL to expose e.g. /share/service/sample/cmis/repo
 *  
 * @author davidc
 * @author dward
 */
public class ProxyPortlet implements Portlet
{
    private static Log logger = LogFactory.getLog(ProxyPortlet.class);

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
        initScriptUrl = config.getInitParameter("scriptUrl");
    }

    /*
     * (non-Javadoc)
     * @see javax.portlet.Portlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest req, ActionResponse res) throws PortletException, PortletSecurityException,
            IOException
    {
        String scriptUrl = req.getParameter("scriptUrl");
        if (scriptUrl != null)
        {
            res.setRenderParameter("scriptUrl", scriptUrl);
        }
    }

    /*
     * (non-Javadoc)
     * @see javax.portlet.Portlet#render(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void render(RenderRequest req, RenderResponse res) throws PortletException, PortletSecurityException,
            IOException
    {
        PortletMode portletMode = req.getPortletMode();
        if (PortletMode.VIEW.equals(portletMode))
        {
            doView(req, res);
        }
        // else if (PortletMode.HELP.equals(portletMode))
        // {
        // doHelp(request, response);
        // }
        // else if (PortletMode.EDIT.equals(portletMode))
        // {
        // doEdit(request, response);
        // }
    }

    /*
     * (non-Javadoc)
     * @see javax.portlet.Portlet#destroy()
     */
    public void destroy()
    {
    }

    /**
     * Render Web Script view
     * 
     * @param req
     * @param res
     * @throws PortletException
     * @throws PortletSecurityException
     * @throws IOException
     */
    protected void doView(RenderRequest req, RenderResponse res) throws PortletException, PortletSecurityException,
            IOException
    {
        //
        // Establish Web Script URL
        //

        String contextPath = req.getContextPath();
        String scriptUrl = req.getParameter("scriptUrl");
        if (scriptUrl == null)
        {
            // retrieve initial scriptUrl as configured by Portlet
            scriptUrl = initScriptUrl;
            if (scriptUrl == null)
            {
                // If the path parameter has not been provided, forward to the user specific dashboard page
                String userId = req.getRemoteUser();
                if (userId != null)
                {
                    scriptUrl = contextPath + "/page/user/" + URLEncoder.encode(userId, "UTF-8") + "/dashboard";
                }
                else
                {
                    throw new PortletException("Initial Web script URL has not been specified.");
                }
            }
        }

        //
        // Execute Web Script
        //

        if (logger.isDebugEnabled())
            logger.debug("Processing portal render request " + req.getScheme() + "://" + req.getServerName() + ":"
                    + req.getServerPort() + "/" + req.getContextPath() + " (scriptUrl=" + scriptUrl + ")");

        // Work out the script URL relative to the context path
        int contextPathLength = contextPath.length();
        String relScriptUrl = contextPathLength > 0 && scriptUrl.startsWith(contextPath) ? scriptUrl
                .substring(contextPathLength) : scriptUrl;

        this.config.getPortletContext().getRequestDispatcher(relScriptUrl).include(req, res);
    }
}
