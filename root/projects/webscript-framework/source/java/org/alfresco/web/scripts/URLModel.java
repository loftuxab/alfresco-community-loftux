/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.scripts;

import java.util.Collections;
import java.util.Map;


/**
 * Script / Template Model representing Web Script URLs
 * 
 * @author davidc
 */
public class URLModel
{
    private static Map<String, String> emptyArgs = Collections.emptyMap();
    private WebScriptRequest req;
    
    /**
     * Construct
     * 
     * @param req
     * @param res
     */
    URLModel(WebScriptRequest req)
    {
        this.req = req;
    }

    /**
     * Gets the Server Path
     * 
     * e.g.  http://host:port
     * 
     * @return  server path
     */
    public String getServer()
    {
        return req.getServerPath();
    }
    
    /**
     * Gets the Context Path
     * 
     * e.g. /alfresco
     * 
     * @return  context path
     */
    public String getContext()
    {
        return req.getContextPath();
    }

    /**
     * Gets the Service Context Path
     * 
     * e.g. /alfresco/service
     * 
     * @return  service context path
     */
    public String getServiceContext()
    {
        return req.getServiceContextPath();
    }

    /**
     * Gets the Service Path
     * 
     * e.g. /alfresco/service/search/keyword
     * 
     * @return  service path
     */
    public String getService()
    {
        return req.getServicePath();
    }

    /**
     * Gets the full path
     * 
     * e.g. /alfresco/service/search/keyword?q=term
     * 
     * @return  service path
     */
    public String getFull()
    {
        return req.getURL();
    }
    
    /**
     * Gets the URL arguments (query string)
     * 
     * @return  args (query string)
     */
    public String getArgs()
    {
        String args = req.getQueryString();
        return (args == null) ? "" : args;
    }
    
    /**
     * Gets the matching service path
     * 
     * e.g.
     * a) service registered path = /search/engine
     * b) request path = /search/engine/external
     * 
     * => /search/engine
     * 
     * @return  matching path
     */
    public String getMatch()
    {
        return req.getServiceMatch().getPath();
    }
    
    /**
     * Gets the Service Extension Path
     * 
     * e.g.
     * a) service registered path = /search/engine
     * b) request path = /search/engine/external
     * 
     * => /external
     * 
     * @return  extension path
     */
    public String getExtension()
    {
        return req.getExtensionPath();
    }
    
    /**
     * Gets the template form of this path
     * 
     * @return  template form of path
     */
    public String getTemplate()
    {
        return req.getServiceMatch().getTemplate();
    }
    
    /**
     * Gets the values of template variables
     * 
     * @return  map of value indexed by variable name (or the empty map)
     */
    public Map<String, String> getTemplateArgs()
    {
        Map<String, String> args = req.getServiceMatch().getTemplateVars();
        return (args == null) ? emptyArgs : args;
    }
    
}
