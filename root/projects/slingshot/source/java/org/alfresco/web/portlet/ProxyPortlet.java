/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.portlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

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
        Map<String, String[]> params = req.getParameterMap();
        for (Map.Entry<String, String[]> param : params.entrySet())
        {
            String name = param.getKey();
            if (name.equals("scriptUrl") || name.startsWith("arg."))
            {
                res.setRenderParameter(name, param.getValue());
            }
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
        if (scriptUrl != null)
        {
            // build web script url from render request
            StringBuilder scriptUrlArgs = null;
            Map<String, String[]> params = req.getParameterMap();
            for (Map.Entry<String, String[]> param : params.entrySet())
            {
                String name = param.getKey();
                if (name.startsWith("arg."))
                {
                    String argName = name.substring("arg.".length());
                    for (String argValue : param.getValue())
                    {
                        if (scriptUrlArgs == null)
                        {
                            scriptUrlArgs = new StringBuilder(128).append(scriptUrl).append('?');
                        }
                        else
                        {
                            scriptUrlArgs.append("&");
                        }

                        // Append encoded argument
                        String encoding = res.getCharacterEncoding();
                        scriptUrlArgs.append(URLEncoder.encode(argName, encoding)).append("=").append(
                                URLEncoder.encode(argValue, encoding));
                    }
                }
            }
            if (scriptUrlArgs != null)
            {
                scriptUrl = scriptUrlArgs.toString();
            }
        }
        else
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
            }

            if (scriptUrl == null)
            {
                throw new PortletException("Initial Web script URL has not been specified.");
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
