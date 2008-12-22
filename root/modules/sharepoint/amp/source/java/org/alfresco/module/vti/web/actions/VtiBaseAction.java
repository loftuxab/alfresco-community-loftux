/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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

package org.alfresco.module.vti.web.actions;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.vti.web.VtiAction;
import org.alfresco.module.vti.web.fp.VtiFpRequest;
import org.alfresco.module.vti.web.fp.VtiFpResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
* <p>VtiBaseAction is base class for all actions that process "Frontpage extension" 
* protocol. It is an adapter for wrapping HttpServletRequest and HttpServletResponse
* in {@link VtiFpRequest} and {@link VtiFpResponse}.  </p>
*
* @author Michael Shavnev
*
*/
public class VtiBaseAction extends HttpServlet implements VtiAction
{
    private static final long serialVersionUID = -9085006990256046378L;

    private final static Log logger = LogFactory.getLog(VtiBaseAction.class);
   
    protected final void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doDelete(new VtiFpRequest(request), new VtiFpResponse(response));
    }

    /**
     * <p>Wrapped doDelete method.</p> 
     *
     * @param request Vti Frontpage request
     * @param response Vti Frontpage response
     */
    protected void doDelete(VtiFpRequest request, VtiFpResponse response) throws ServletException, IOException
    {
        
    }
    
    /* 
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(new VtiFpRequest(request), new VtiFpResponse(response));
    }
    
    /**
     * <p>Wrapped doGet method.</p> 
     *
     * @param request Vti Frontpage request
     * @param response Vti Frontpage response
     */
    protected void doGet(VtiFpRequest request, VtiFpResponse response) throws ServletException, IOException
    {
        
    }
    
    /** 
     * @see javax.servlet.http.HttpServlet#doHead(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected final void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doHead(new VtiFpRequest(request), new VtiFpResponse(response));
    }

    /**
     * <p>Wrapped doHead method.</p> 
     *
     * @param request Vti Frontpage request
     * @param response Vti Frontpage response
     */
    protected void doHead(VtiFpRequest request, VtiFpResponse response) throws ServletException, IOException
    {
        
    }
    
    /** 
     * @see javax.servlet.http.HttpServlet#doOptions(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected final void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doOptions(new VtiFpRequest(request), new VtiFpResponse(response));
    }

    /**
     * <p>Wrapped doOptions method.</p> 
     *
     * @param request Vti Frontpage request
     * @param response Vti Frontpage response
     */
    protected void doOptions(VtiFpRequest request, VtiFpResponse response) throws ServletException, IOException
    {
        
    }

    /** 
     * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected final void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPut(new VtiFpRequest(request), new VtiFpResponse(response));
    }

    /**
     * <p>Wrapped doPut method.</p> 
     *
     * @param request Vti Frontpage request
     * @param response Vti Frontpage response
     */
    protected void doPut(VtiFpRequest request, VtiFpResponse response) throws ServletException, IOException
    {
        
    }
   
    /** 
     * @see javax.servlet.http.HttpServlet#doTrace(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */   
    protected final void doTrace(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doTrace(new VtiFpRequest(request), new VtiFpResponse(response));
    }
    
    /**
     * <p>Wrapped doTrace method.</p> 
     *
     * @param request Vti Frontpage request
     * @param response Vti Frontpage response
     */
    protected void doTrace(VtiFpRequest request, VtiFpResponse response) throws ServletException, IOException
    {
        
    }
    
    /** 
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */   
    protected final void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        VtiFpRequest vtiRequest = new VtiFpRequest(request);
        VtiFpResponse vtiResponse = new VtiFpResponse(response);
        
        if ("application/x-vermeer-urlencoded".equals(request.getContentType())) 
        {
            StringBuffer formString = new StringBuffer();
            InputStream inputStream = request.getInputStream();
            char c = '\n'; 
            do
            {
                c = (char) inputStream.read();
                formString.append(c);
            } 
            while (c != '\n');
            
            String encoding = request.getCharacterEncoding();
            if (encoding == null || encoding.trim().length() == 0) 
            {
                encoding = "UTF-8"; 
            }
            
            String[] paramValues = formString.toString().split("&");
            for (String string : paramValues)
            {
                String[] paramValue = string.split("=");
                String decodedParamName = URLDecoder.decode(paramValue[0], encoding);
                String decodedParamValue = null;
                
                if (paramValue.length > 1) 
                {
                    decodedParamValue = URLDecoder.decode(paramValue[1], encoding);
                }
                else
                {
                    decodedParamValue = new String();
                }
                
                vtiRequest.setParameter(decodedParamName, decodedParamValue);
            }
        }
        
        doPost(vtiRequest, vtiResponse);
    }
    
    /**
     * <p>Wrapped doPost method.</p> 
     *
     * @param request Vti Frontpage request
     * @param response Vti Frontpage response
     */
    protected void doPost(VtiFpRequest request, VtiFpResponse response) throws ServletException, IOException
    {
        
    }

    /** 
     * @see org.alfresco.module.vti.web.VtiAction#execute(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void execute(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            service(request, response);
        }
        catch (IOException e)
        {
            if (logger.isDebugEnabled()) {
                logger.debug("Action IO exception", e);
            }
        }
        catch (ServletException e)
        {
            if (logger.isDebugEnabled()) {
                logger.debug("Action execution exception", e);
            }
        }
    }
    
}
