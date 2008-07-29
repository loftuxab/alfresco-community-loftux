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

package org.alfresco.module.vti.endpoints;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.module.vti.httpconnector.VtiServletContainer;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpServletConnection;

/**
 * Class that provides utils for endpoint package
 *
 * @author AndreyAk
 */
public class EndpointUtils
{
    public static String DWS = "dws";

    public static String getResponseTagName(String name)
    {
        return name + "Response";
    }

    public static String getResultTagName(String name)
    {
        return name + "Result";
    }

    /**
     * @param prefix namespace prefix
     * @param searchPath path to the element
     * @return
     */
    public static String buildXPath(String prefix, String searchPath)
    {
        return searchPath.replaceAll("/", "/" + prefix + ":");
    }    
    
    public static String getContext()
    {
        TransportContext context = TransportContextHolder.getTransportContext();
        HttpServletConnection connection = (HttpServletConnection )context.getConnection();
        HttpServletRequest request = connection.getHttpServletRequest();
        Object alfrescoContext = request.getAttribute(VtiServletContainer.VTI_ALFRESCO_CONTEXT);
        
        if (alfrescoContext != null)
        {
            return alfrescoContext.toString();
        }
        else
        {
            return "";
        }
    }

    public static String getHost()
    {
        TransportContext context = TransportContextHolder.getTransportContext();
        HttpServletConnection connection = (HttpServletConnection )context.getConnection();
        HttpServletRequest request = connection.getHttpServletRequest();
        return request.getHeader("Host");
    }    
    
    public static String getDwsFromUri()
    {
        String dws;
        TransportContext context = TransportContextHolder.getTransportContext();
        HttpServletConnection connection = (HttpServletConnection )context.getConnection();
        HttpServletRequest request = connection.getHttpServletRequest();        
        String uri = request.getRequestURI();
        if (uri.startsWith(getContext() + "/_vti_bin"))
            return "";
        dws = uri.substring(getContext().length() + 1, uri.indexOf("/_vti_bin"));
        try
        {
            dws = URLDecoder.decode(dws, "UTF-8");                
        }catch (UnsupportedEncodingException e) {
            // TODO: handle exception
        }
        return dws;         
    }   
    
    public static HttpServletRequest getRequest()
    {
        TransportContext context = TransportContextHolder.getTransportContext();
        HttpServletConnection connection = (HttpServletConnection )context.getConnection();
        return connection.getHttpServletRequest();        
    }
    
}
