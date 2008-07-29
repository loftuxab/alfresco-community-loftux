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

package org.alfresco.module.vti;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Michael Shavnev
 *
 */
public class VtiServlet extends HttpServlet
{
    private static final long serialVersionUID = -9085006990256046378L;

    protected final void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doDelete(new VtiRequest(request), new VtiResponse(response));
    }

    protected void doDelete(VtiRequest request, VtiResponse response) throws ServletException, IOException
    {
        
    }
    
    protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(new VtiRequest(request), new VtiResponse(response));
    }
    
    protected void doGet(VtiRequest request, VtiResponse response) throws ServletException, IOException
    {
        
    }
    
    protected final void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doHead(new VtiRequest(request), new VtiResponse(response));
    }

    protected void doHead(VtiRequest request, VtiResponse response) throws ServletException, IOException
    {
        
    }
    
    protected final void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doOptions(new VtiRequest(request), new VtiResponse(response));
    }
    
    protected void doOptions(VtiRequest request, VtiResponse response) throws ServletException, IOException
    {
        
    }
    
    protected final void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPut(new VtiRequest(request), new VtiResponse(response));
    }

    protected void doPut(VtiRequest request, VtiResponse response) throws ServletException, IOException
    {
        
    }
    
    protected final void doTrace(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doTrace(new VtiRequest(request), new VtiResponse(response));
    }
    
    protected void doTrace(VtiRequest request, VtiResponse response) throws ServletException, IOException
    {
        
    }
    
    
    protected final void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        VtiRequest vtiRequest = new VtiRequest(request);
        VtiResponse vtiResponse = new VtiResponse(response);
        
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
    
    protected void doPost(VtiRequest request, VtiResponse response) throws ServletException, IOException
    {
        
    }
    
}
