package org.alfresco.web.app.servlet;

import java.io.IOException;

import javax.faces.webapp.FacesServlet;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.alfresco.web.app.Application;
import org.alfresco.web.bean.ErrorBean;
import org.apache.log4j.Logger;

/**
 * Wrapper around standard faces servlet to provide error handling
 * 
 * @author gavinc
 */
public class AlfrescoFacesServlet extends FacesServlet
{
   private static Logger logger = Logger.getLogger(AlfrescoFacesServlet.class);
   
   /**
    * @see javax.servlet.Servlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
    */
   public void service(ServletRequest request, ServletResponse response) 
      throws IOException, ServletException
   {
      try
      {
         super.service(request, response);
      }
      catch (Throwable error)
      {
         // TODO: determine the return page
         
         Application.handleServletError(getServletConfig().getServletContext(), (HttpServletRequest)request,
               (HttpServletResponse)response, error, logger, null);
      }
   }
   
}
