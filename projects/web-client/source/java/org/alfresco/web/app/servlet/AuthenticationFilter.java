/*
 * Created on 20-Jun-2005
 */
package org.alfresco.web.app.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.bean.repository.User;

/**
 * @author Kevin Roast
 */
public class AuthenticationFilter implements Filter
{
   public final static String AUTHENTICATION_USER = "_alfAuthTicket";
   private final static String LOGIN_PAGE = "/faces/jsp/login.jsp";
   
   /**
    * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
    */
   public void init(FilterConfig config) throws ServletException
   {
      // nothing to do
   }

   /**
    * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
    */
   public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
         throws IOException, ServletException
   {
      HttpServletRequest httpRequest = (HttpServletRequest)req;
      // allow the login page to proceed
      if (httpRequest.getRequestURI().endsWith(LOGIN_PAGE) == false)
      {
         // examine the session for our User object
         User user = (User)httpRequest.getSession().getAttribute(AUTHENTICATION_USER);
         if (user == null)
         {
            // no user/ticket - redirect to login page
            HttpServletResponse httpResponse = (HttpServletResponse)res; 
            httpResponse.sendRedirect(httpRequest.getContextPath() + LOGIN_PAGE);
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
}
