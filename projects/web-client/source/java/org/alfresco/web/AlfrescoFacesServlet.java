package org.alfresco.web;

import java.io.IOException;

import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.alfresco.web.bean.ErrorBean;
import org.apache.log4j.Logger;

/**
 * Wrapper around standard faces servlet to handle errors
 * 
 * @author gavinc
 */
public class AlfrescoFacesServlet extends FacesServlet
{
   private static Logger logger = Logger.getLogger(AlfrescoFacesServlet.class);
   
   /**
    * @see javax.servlet.Servlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
    */
   public void service(ServletRequest request, ServletResponse response) throws IOException, ServletException
   {
      try
      {
         super.service(request, response);
      }
      catch (Throwable error)
      {
         // TODO: configure the error page and re-throw error if not specified
         
         // get the error bean from the session and set the error that occurred.
         HttpSession session = ((HttpServletRequest)request).getSession();
         ErrorBean errorBean = (ErrorBean)session.getAttribute(ErrorBean.ERROR_BEAN_NAME);
         if (errorBean == null)
         {
            errorBean = new ErrorBean();
            session.setAttribute(ErrorBean.ERROR_BEAN_NAME, errorBean);
         }
         errorBean.setLastError(error);

         if (logger.isDebugEnabled())
            logger.debug("An error has occurred, redirecting to error page: /jsp/error.jsp");
         
         ((HttpServletResponse)response).sendRedirect(((HttpServletRequest)request).getContextPath() + 
                                                         "/jsp/error.jsp");
      }
   }
   
}
