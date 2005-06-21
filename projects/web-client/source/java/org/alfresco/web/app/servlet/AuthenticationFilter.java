/*
 * Created on 20-Jun-2005
 */
package org.alfresco.web.app.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.app.Application;
import org.alfresco.web.bean.repository.User;

/**
 * @author Kevin Roast
 */
public class AuthenticationFilter implements Filter
{
   public final static String AUTHENTICATION_USER = "_alfAuthTicket";
   
   /**
    * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
    */
   public void init(FilterConfig config) throws ServletException
   {
      this.context = config.getServletContext();
   }

   /**
    * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
    */
   public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
         throws IOException, ServletException
   {
      HttpServletRequest httpRequest = (HttpServletRequest)req;
      // allow the login page to proceed
      if (httpRequest.getRequestURI().endsWith(getLoginPage()) == false)
      {
         // examine the session for our User object
         User user = (User)httpRequest.getSession().getAttribute(AUTHENTICATION_USER);
         if (user == null)
         {
            // no user/ticket - redirect to login page
            HttpServletResponse httpResponse = (HttpServletResponse)res; 
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/faces" + getLoginPage());
         }
         else
         {
            // continue filter chaining
            chain.doFilter(req, res);
         }
      }
      else
      {
         // continue filter chaining
         chain.doFilter(req, res);
      }
   }

   /**
    * @see javax.servlet.Filter#destroy()
    */
   public void destroy()
   {
      // nothing to do
   }
   
   private String getLoginPage()
   {
      if (this.loginPage == null)
      {
         this.loginPage = Application.getLoginPage(this.context);
      }
      
      return this.loginPage;
   }
   
   
   private String loginPage = null;
   private ServletContext context;
}
