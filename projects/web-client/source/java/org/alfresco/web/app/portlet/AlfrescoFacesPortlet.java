/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.web.app.portlet;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortalContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletContext;

import org.alfresco.repo.security.authentication.AuthenticationService;
import org.alfresco.repo.security.authentication.StoreContextHolder;
import org.alfresco.web.app.Application;
import org.alfresco.web.app.servlet.AuthenticationFilter;
import org.alfresco.web.bean.ErrorBean;
import org.alfresco.web.bean.FileUploadBean;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.repository.User;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.log4j.Logger;
import org.apache.myfaces.context.servlet.ServletFacesContextImpl;
import org.apache.myfaces.portlet.MyFacesGenericPortlet;
import org.apache.myfaces.portlet.PortletUtil;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Class to extend the MyFacesGenericPortlet to provide behaviour specific to Alfresco web client.
 * Handles upload of multi-part forms through a JSR-168 Portlet, generic error handling and session
 * login authentication.
 * 
 * @author Gavin Cornwell, Kevin Roast
 */
public class AlfrescoFacesPortlet extends MyFacesGenericPortlet
{
   public static final String INSTANCE_NAME = "AlfrescoClientInstance";
   public static final String WINDOW_NAME = "AlfrescoClientWindow";
   public static final String MANAGED_BEAN_PREFIX = "javax.portlet.p." + INSTANCE_NAME + 
                                                    "." + WINDOW_NAME + "?";
   
   private static final String ERROR_PAGE_PARAM = "error-page";
   private static final String ERROR_OCCURRED = "error-occurred";
   
   private static Logger logger = Logger.getLogger(AlfrescoFacesPortlet.class);

   private String loginPage = null;
   private String errorPage = null;
   
   
   /**
    * Called by the portlet container to allow the portlet to process an action request.
    */
   public void processAction(ActionRequest request, ActionResponse response) 
      throws PortletException, IOException 
   {      
      boolean isMultipart = PortletFileUpload.isMultipartContent(request);
      
      try
      {
         // NOTE: Due to filters not being called within portlets we can not make use
         //       of the MyFaces file upload support, therefore we are using a pure
         //       portlet request/action to handle file uploads until there is a 
         //       solution.
         
         if (isMultipart)
         {
            if (logger.isDebugEnabled())
               logger.debug("Handling multipart request...");
            
            PortletSession session = request.getPortletSession();
            
            // get the file from the request and put it in the session
            DiskFileItemFactory factory = new DiskFileItemFactory();
            PortletFileUpload upload = new PortletFileUpload(factory);
            List<FileItem> fileItems = upload.parseRequest(request);
            Iterator<FileItem> iter = fileItems.iterator();
            FileUploadBean bean = new FileUploadBean();
            while(iter.hasNext())
            {
               FileItem item = iter.next();
               String filename = item.getName();
               if(item.isFormField() == false)
               {
                  // workaround a bug in IE where the full path is returned
                  int idx = filename.lastIndexOf(File.separator);
                  if (idx != -1)
                  {
                     filename = filename.substring(idx + File.separator.length());
                  }
                  
                  File tempFile = File.createTempFile("alfresco", ".upload");
                  item.write(tempFile);
                  bean.setFile(tempFile);
                  bean.setFileName(filename);
                  bean.setFilePath(tempFile.getAbsolutePath());
                  session.setAttribute(FileUploadBean.FILE_UPLOAD_BEAN_NAME, bean, 
                                       PortletSession.PORTLET_SCOPE);
               }
            }
            
            // it doesn't matter what the value is we just need the VIEW_ID parameter
            // to tell the faces portlet bridge to treat the request as a JSF request,
            // this will send us back to the same page we came from, which is fine for
            // most scenarios.
            response.setRenderParameter(VIEW_ID, "a-jsf-page");
         }
         else
         {
            // do the normal JSF processing
            super.processAction(request, response);
         }
      }
      catch (Throwable e)
      {
         if (getErrorPage() != null)
         {
            handleError(request, response, e);
         }
         else
         {
            logger.warn("No error page configured, re-throwing exception");
            
            if (e instanceof PortletException)
            {
               throw (PortletException)e;
            }
            else if (e instanceof IOException)
            {
               throw (IOException)e;
            }
            else
            {
               throw new PortletException(e);
            }
         }
      }
   }

   /**
    * @see org.apache.myfaces.portlet.MyFacesGenericPortlet#facesRender(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
    */
   protected void facesRender(RenderRequest request, RenderResponse response) 
      throws PortletException, IOException
   {
      if (request.getParameter(ERROR_OCCURRED) != null)
      {
         String errorPage = Application.getErrorPage(getPortletContext());
         
         if (logger.isDebugEnabled())
            logger.debug("An error has occurred, redirecting to error page: " + errorPage);
         
         response.setContentType("text/html");
         PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(errorPage);
         dispatcher.include(request, response);
      }
      else
      {
         // if we have no User object in the session then a timeout must have occured
         // use the viewId to check that we are not already on the login page
         String viewId = request.getParameter(VIEW_ID);
         User user = (User)request.getPortletSession().getAttribute(AuthenticationFilter.AUTHENTICATION_USER);
         if (user == null && (viewId == null || viewId.equals(getLoginPage()) == false))
         {
            if (logger.isDebugEnabled())
               logger.debug("No valid login, requesting login page. ViewId: " + viewId);
            
            // login page redirect
            response.setContentType("text/html");
            request.getPortletSession().setAttribute(PortletUtil.PORTLET_REQUEST_FLAG, "true");
            nonFacesRequest(request, response);
         }
         else
         {
            try
            {
               // setup the authentication context
               WebApplicationContext ctx = (WebApplicationContext)getPortletContext().getAttribute(
                     WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
               AuthenticationService auth = (AuthenticationService)ctx.getBean("authenticationService");
               StoreContextHolder.setContext(Repository.getStoreRef());
               auth.validate(user.getTicket());
               
               // do the normal JSF processing
               super.facesRender(request, response);
            }
            catch (Throwable e)
            {
               if (getErrorPage() != null)
               {
                  handleError(request, response, e);
               }
               else
               {
                  logger.warn("No error page configured, re-throwing exception");
                  
                  if (e instanceof PortletException)
                  {
                     throw (PortletException)e;
                  }
                  else if (e instanceof IOException)
                  {
                     throw (IOException)e;
                  }
                  else
                  {
                     throw new PortletException(e);
                  }
               }
            }
         }
      }
   }
   
   /**
    * Handles errors that occur during a process action request
    */
   private void handleError(ActionRequest request, ActionResponse response, Throwable error)
      throws PortletException, IOException
   {
      // get the error bean from the session and set the error that occurred.
      PortletSession session = request.getPortletSession();
      ErrorBean errorBean = (ErrorBean)session.getAttribute(ErrorBean.ERROR_BEAN_NAME, 
                             PortletSession.PORTLET_SCOPE);
      if (errorBean == null)
      {
         errorBean = new ErrorBean();
         session.setAttribute(ErrorBean.ERROR_BEAN_NAME, errorBean, PortletSession.PORTLET_SCOPE);
      }
      errorBean.setLastError(error);
      
      response.setRenderParameter(ERROR_OCCURRED, "true");
   }
   
   /**
    * Handles errors that occur during a render request
    */
   private void handleError(RenderRequest request, RenderResponse response, Throwable error)
      throws PortletException, IOException
   {
      // get the error bean from the session and set the error that occurred.
      PortletSession session = request.getPortletSession();
      ErrorBean errorBean = (ErrorBean)session.getAttribute(ErrorBean.ERROR_BEAN_NAME, 
                             PortletSession.PORTLET_SCOPE);
      if (errorBean == null)
      {
         errorBean = new ErrorBean();
         session.setAttribute(ErrorBean.ERROR_BEAN_NAME, errorBean, PortletSession.PORTLET_SCOPE);
      }
      errorBean.setLastError(error);

      // if the faces context is available set the current view to the browse page
      // so that the error page goes back to the application (rather than going back
      // to the same page which just throws the error again meaning we can never leave
      // the error page)
      FacesContext context = FacesContext.getCurrentInstance();
      if (context != null)
      {
         ViewHandler viewHandler = context.getApplication().getViewHandler();
         // TODO: configure the portlet error return page
         UIViewRoot view = viewHandler.createView(context, "/jsp/browse/browse.jsp");
         context.setViewRoot(view);
      }

      // get the error page and include that instead
      String errorPage = getErrorPage();
      
      if (logger.isDebugEnabled())
         logger.debug("An error has occurred, redirecting to error page: " + errorPage);
      
      response.setContentType("text/html");
      PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(errorPage);
      dispatcher.include(request, response);
   }
   
   /**
    * @return Retrieves the configured login page
    */
   private String getLoginPage()
   {
      if (this.loginPage == null)
      {
         this.loginPage = Application.getLoginPage(getPortletContext());
      }
      
      return this.loginPage;
   }
   
   /**
    * @return Retrieves the configured error page
    */
   private String getErrorPage()
   {
      if (this.errorPage == null)
      {
         this.errorPage = Application.getErrorPage(getPortletContext());
      }
      
      return this.errorPage;
   }
}
