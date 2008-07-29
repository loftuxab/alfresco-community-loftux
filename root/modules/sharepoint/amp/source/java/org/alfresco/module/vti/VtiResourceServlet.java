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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.vti.httpconnector.VtiServletContainer;

/**
 * Servlet implementation class for Servlet: VtiResourceServlet
 *
 */
 public class VtiResourceServlet extends HttpServlet implements Servlet {
   
    private static final long serialVersionUID = 9073113240345164795L;
    
    private static final String CSS_EXTENSION = ".css";
    private static final String ALFRESCO_CONTEXT_VALUE = "ALFRESCO_CONTEXT_VALUE";
    
    private static Map<String, byte[]> resourcesMap = new HashMap<String, byte[]>();

    /**
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public VtiResourceServlet() {
		super();
	}   	
	
	/**
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String alfrescoContext = (String)request.getAttribute(VtiServletContainer.VTI_ALFRESCO_CONTEXT);
	    
	    String resourceLocation = request.getRequestURI().replaceAll(alfrescoContext + "/resources", "");
	    
	    if (isResourceCSS(resourceLocation)) {
	    	writeResponse(resourceLocation, response, alfrescoContext);
	    } else {
	    	InputStream is = getServletContext().getResourceAsStream(resourceLocation);
	    	//1K buffer 
	    	byte[] bytearray = new byte[1024];
	    	int bytesread = 0; 
	    	while( (bytesread = is.read(bytearray) ) != -1 ) 
	    	{ 
	    		response.getOutputStream().write(bytearray, 0, bytesread); 
	    	}
	    	is.close();
	    }
	}  	
	
	/**
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	/**
	 *  Check resource on css file
	 * 
	 * @param resourceLocation Resource location
	 * @return True if resource is css file
	 */
	private boolean isResourceCSS(String resourceLocation) {
		return resourceLocation.toLowerCase().endsWith(CSS_EXTENSION);
	}
	
	/**
	 *  Write response
	 * 
	 * @param resourceLocation Resource location
	 * @param response Http response
	 * @throws IOException
	 */
	private void writeResponse(String resourceLocation, HttpServletResponse response, String alfrescoContext) throws IOException {
		
		byte[] resource = null;
		
		if ((resource = resourcesMap.get(resourceLocation)) == null) {
			resource = cacheResource(resourceLocation, alfrescoContext);
		}	
		
		response.getOutputStream().write(resource);
	}
	
	/**
	 *  Cache resource
	 * 
	 * @param resourceLocation Resource location
	 * @param alfrescoContext Alfresco context
	 * @return Byte array resource
	 * @throws IOException
	 */
	private byte[] cacheResource(String resourceLocation, String alfrescoContext) throws IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(getServletContext().getResourceAsStream(resourceLocation)));
		
		String temp = null;
		String endString = "";
		
		while((temp = br.readLine()) != null) {
			endString += temp.replaceAll(ALFRESCO_CONTEXT_VALUE, alfrescoContext);
		}
		
		byte[] result = endString.getBytes();
		resourcesMap.put(resourceLocation, result);
		
		return result;
	}
}