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
package org.alfresco.web.site.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigService;
import org.alfresco.connector.remote.RemoteClient;
import org.alfresco.connector.remote.Response;
import org.alfresco.util.URLEncoder;
import org.alfresco.web.scripts.Description.RequiredAuthentication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author Kevin Roast
 */
public class WebScriptAuthenticationServlet extends HttpServlet
{
   private String loginPage = null;
   private ConfigService configService = null;
   
   
   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException
   {
      String username = req.getParameter("username");
      String password = req.getParameter("password");
      
      Config config = getConfigService().getConfig("Remote");
      if (config != null)
      {
         ConfigElement remoteConfig = (ConfigElement)config.getConfigElement("remote");
         String endpoint = remoteConfig.getChildValue("endpoint");
         if (endpoint == null || endpoint.length() == 0)
         {
            throw new IllegalStateException("No endpoint configured for remote authentication.");
         }
         else
         {
            // make a direct call to login api to retrieve a ticket for the user credentials
            RemoteClient remote = new RemoteClient(endpoint);
            Response response = remote.call("/s/api/login?u=" + username + "&pw=" + password);
            if (response.getStatus().getCode() == HttpServletResponse.SC_OK)
            {
               boolean gotTicket = false;
               String ticketXML = response.getResponse();
               if (ticketXML != null && ticketXML.length() != 0)
               {
                  int start = ticketXML.indexOf("<ticket>");
                  if (start != -1)
                  {
                     int end = ticketXML.indexOf("</ticket>", start + 8);
                     if (end != -1)
                     {
                        String ticket = ticketXML.substring(start + 8, end);
                        req.getSession().setAttribute("_alfticket", ticket);
                        String returnUrl = req.getParameter("returl");
                        res.sendRedirect(returnUrl);
                        gotTicket = true;
                     }
                  }
               }
               if (gotTicket == false)
               {
                  throw new IllegalStateException("Failed to extract ticket from server response: " + ticketXML);
               }
            }
            else if (response.getStatus().getCode() == HttpServletResponse.SC_FORBIDDEN)
            {
               throw new IllegalStateException("Error - username/password not accepted.",
                     response.getStatus().getException());
            }
            else if (response.getStatus().getCode() == HttpServletResponse.SC_GATEWAY_TIMEOUT)
            {
               throw new IllegalStateException("Error - server did not respond and timed out.",
                     response.getStatus().getException());
            }
            else
            {
               throw new IllegalStateException("Error - failure to login: " + response.getStatus().getMessage(),
                     response.getStatus().getException());
            }
         }
      }
   }
   
   /**
    * Authenticate against the repository using the specified Authentication.
    * 
    * @return valid ticket on success, null on failure
    */
   public static AuthenticationResult authenticate(HttpServletRequest req, RequiredAuthentication auth)
   {
      String ticket = req.getParameter("ticket");
      if (ticket == null)
      {
         ticket = (String)req.getSession().getAttribute("_alfticket");
      }
      
      AuthenticationResult result = null;
      switch (auth)
      {
         case guest:
            if (ticket == null || ticket.length() == 0)
            {
               // TODO: authenticate directly as guest and retrieve Guest ticket
            }
            throw new IllegalArgumentException("Guest authentication unsupported.");
            //break;
            
         case user:
            if (ticket == null || ticket.length() == 0)
            {
               // redirect to login page as there is no ticket
               result = new AuthenticationResult(false, null);
            }
            else
            {
               result = new AuthenticationResult(true, ticket);
            }
            break;
            
         case admin:
            // TODO: how to differentiate admin - or not support as an auth type?
            if (ticket == null || ticket.length() == 0)
            {
               // redirect to login page as there is no ticket
               result = new AuthenticationResult(false, null);
            }
            else
            {
               result = new AuthenticationResult(true, ticket);
            }
            break;
            
         case none:
            result = new AuthenticationResult(true, null);
            break;
      }
      
      return result;
   }
   
   public static class AuthenticationResult
   {
      public boolean Success;
      public String Ticket;
      
      AuthenticationResult(boolean success, String ticket)
      {
         Success = success;
         Ticket = ticket;
      }
   }
   
   public static void redirectToLoginPage(HttpServletRequest req, HttpServletResponse res, ConfigService cs)
      throws IOException
   {
      String returl = req.getRequestURI();
      if (req.getQueryString() != null && req.getQueryString().length() != 0)
      {
         returl += "?" + req.getQueryString();
      }
      res.sendRedirect(
            req.getContextPath() + getLoginPage(cs) +
            "?returl=" + URLEncoder.encode(returl));
   }
   
   /**
    * @return The login page url
    */
   private String getLoginPage()
   {
      if (this.loginPage == null)
      {
         this.loginPage = getLoginPage(configService);
      }
      return this.loginPage;
   }
   
   public static String getLoginPage(ConfigService configService)
   {
      Config config = configService.getConfig("Authentication");
      if (config == null)
      {
         throw new IllegalStateException("Authentication config section cannot be found.");
      }
      ConfigElement loginElement = config.getConfigElement("login-page");
      if (loginElement == null)
      {
         throw new IllegalStateException("Authentication login-page config element cannot be found.");
      }
      return loginElement.getValue().trim();
   }
   
   private ConfigService getConfigService()
   {
      if (this.configService == null)
      {
         ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
         this.configService = (ConfigService)context.getBean("pagerenderer.config");
      }
      return this.configService;
   }
}
