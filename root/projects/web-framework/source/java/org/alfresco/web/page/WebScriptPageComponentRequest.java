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
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.web.page;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.util.Content;
import org.alfresco.web.config.ServerProperties;
import org.alfresco.web.scripts.Match;
import org.alfresco.web.scripts.Runtime;
import org.alfresco.web.scripts.WebScriptRequestURLImpl;
import org.alfresco.web.scripts.servlet.WebScriptServletRequest;

/**
 * Simple implementation of a WebScript URL Request for a webscript component on the page.
 * Mostly based on the existing WebScriptRequestURLImpl - just adds support for additional
 * page level context parameters available to the component as args.
 * 
 * @author Kevin Roast
 */
class WebScriptPageComponentRequest extends WebScriptRequestURLImpl
{
   /** request parameters */
   private Map<String, String> parameters;
   
   /** properties of the host server */
   private ServerProperties serverProperties;
   
   /** servlet request for the containing page */
   private HttpServletRequest servletRequest;
   
   
   /**
    * Construction
    * 
    * @param runtime        Page Component WebScript Runtime
    * @param scriptUrl      Url to the script request to execute
    * @param match          WebScript match for the request
    * @param parameters     Request parameters
    */
   WebScriptPageComponentRequest(
           PageComponentWebScriptRuntime runtime, String scriptUrl, Match match, Map<String, String> parameters)
   {
      super(runtime, scriptUrl, match);
      this.parameters = parameters;
      this.serverProperties = runtime.getPageRendererContext().ServerProperties;
      this.servletRequest = runtime.getPageRendererContext().ServletRequest;
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptRequest#getParameterNames()
    */
   public String[] getParameterNames()
   {
      return this.parameters.keySet().toArray(new String[this.parameters.size()]);
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptRequest#getParameter(java.lang.String)
    */
   public String getParameter(String name)
   {
      return this.parameters.get(name);
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptRequest#getParameterValues(java.lang.String)
    */
   public String[] getParameterValues(String name)
   {
      return this.parameters.values().toArray(new String[this.parameters.size()]);
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptRequest#getAgent()
    */
   public String getAgent()
   {
      return WebScriptServletRequest.resolveUserAgent(servletRequest.getHeader("user-agent"));
   }

   /**
    * @return the absolute server path
    *         based on configured values or built from request if not configured 
    */
   public String getServerPath()
   {
      return getServerScheme() + "://" + getServerName() + ":" + getServerPort();
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptRequest#getHeaderNames()
    */
   @SuppressWarnings("unchecked")
   public String[] getHeaderNames()
   {
      List<String> headersList = new ArrayList<String>();
      Enumeration<String> enumNames = servletRequest.getHeaderNames();
      while(enumNames.hasMoreElements())
      {
         headersList.add(enumNames.nextElement());
      }
      String[] headers = new String[headersList.size()];
      headersList.toArray(headers);
      return headers;
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptRequest#getHeader(java.lang.String)
    */
   public String getHeader(String name)
   {
      return servletRequest.getHeader(name);
   }

   /* (non-Javadoc)
    * @see org.alfresco.web.scripts.WebScriptRequest#getHeaderValues(java.lang.String)
    */
   @SuppressWarnings("unchecked")
   public String[] getHeaderValues(String name)
   {
      String[] values = null;
      Enumeration<String> enumValues = servletRequest.getHeaders(name);
      if (enumValues.hasMoreElements())
      {
         List<String> valuesList = new ArrayList<String>(2);
         do
         {
            valuesList.add(enumValues.nextElement());
         } 
         while (enumValues.hasMoreElements());
         values = new String[valuesList.size()];
         valuesList.toArray(values);
      }
      return values;
   }

   public Content getContent()
   {
      return null;
   }

   /**
    * Get Server Scheme
    * 
    * @return  server scheme
    */
   private String getServerScheme()
   {
      String scheme = null;
      if (serverProperties != null)
      {
         scheme = serverProperties.getScheme();
      }
      if (scheme == null)
      {
         scheme = servletRequest.getScheme();
      }
      return scheme;
   }

   /**
    * Get Server Name
    * 
    * @return  server name
    */
   private String getServerName()
   {
      String name = null;
      if (serverProperties != null)
      {
         name = serverProperties.getHostName();
      }
      if (name == null)
      {
         name = servletRequest.getServerName();
      }
      return name;
   }

   /**
    * Get Server Port
    * 
    * @return  server name
    */
   private int getServerPort()
   {
      Integer port = null;
      if (serverProperties != null)
      {
         port = serverProperties.getPort();
      }
      if (port == null)
      {
         port = servletRequest.getServerPort();
      }
      return port;
   }
}
