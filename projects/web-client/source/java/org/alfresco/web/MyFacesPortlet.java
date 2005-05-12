package org.alfresco.web;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.FactoryFinder;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.UnavailableException;

import org.alfresco.web.bean.ErrorBean;
import org.alfresco.web.bean.wizard.AddContentWizard;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.log4j.Logger;
import org.apache.myfaces.portlet.MyFacesGenericPortlet;

public class MyFacesPortlet extends MyFacesGenericPortlet
{
   public static final String ERROR_BEAN_NAME = "ErrorBean";
   public static final String INSTANCE_NAME = "AlfrescoClientInstance";
   public static final String WINDOW_NAME = "AlfrescoClientWindow";
   public static final String MANAGED_BEAN_PREFIX = "javax.portlet.p." + INSTANCE_NAME + 
                                                    "." + WINDOW_NAME + "?";
   
   private static final String ERROR_PAGE_PARAM = "error-page";
   private static final String ERROR_OCCURRED = "error-occurred";
   
   private static Logger logger = Logger.getLogger(MyFacesPortlet.class);
   
   private String errorPage;

   /**
    * @see org.apache.myfaces.portlet.MyFacesGenericPortlet#init()
    */
   public void init() throws PortletException, UnavailableException
   {
      this.errorPage = getPortletConfig().getInitParameter(ERROR_PAGE_PARAM);
      
      super.init();
   }

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
         //       solution. As there are only a couple of places in the app we handle
         //       file uploads they are dealt with specifically in here.
         
         if (isMultipart)
         {
            if (logger.isDebugEnabled())
               logger.debug("Handling multipart request...");
            
            PortletSession session = request.getPortletSession();
            AddContentWizard wizard = (AddContentWizard)session.
               getAttribute(MANAGED_BEAN_PREFIX + "AddContentWizard", PortletSession.APPLICATION_SCOPE);
            if (wizard == null)
            {
               throw new IllegalStateException("The AddContentWizard bean needs to be in the session");
            }
            
            // get the file from the request and put it in the session
            DiskFileItemFactory factory = new DiskFileItemFactory();
            PortletFileUpload upload = new PortletFileUpload(factory);
            String sPath = "";
            List fileItems = upload.parseRequest(request);
            Iterator iter = fileItems.iterator();
            while(iter.hasNext())
            {
               FileItem item = (FileItem)iter.next();
               String filename = item.getName();
               if(item.isFormField() == false)
               {
                  File tempFile = File.createTempFile("alfresco", ".upload");
                  tempFile.deleteOnExit();
                  item.write(tempFile);
                  wizard.setFile(tempFile);
                  wizard.setName(filename);
               }
            }
            
            // redirect back to the add content wizard for now
            response.setRenderParameter(VIEW_ID, "/jsp/wizard/add-content/details.jsp");
         }
         else
         {
            // do the normal JSF processing
            super.processAction(request, response);
         }
      }
      catch (Throwable e)
      {
         if (this.errorPage != null)
         {
            handleError(request, response, e);
         }
         else
         {
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
   
   private void handleError(ActionRequest request, ActionResponse response, Throwable error)
      throws PortletException, IOException
   {
      // get the error bean from the session and set the error that occurred.
      PortletSession session = request.getPortletSession();
      ErrorBean errorBean = (ErrorBean)session.getAttribute(ERROR_BEAN_NAME, 
                             PortletSession.PORTLET_SCOPE);
      if (errorBean == null)
      {
         errorBean = new ErrorBean();
         session.setAttribute(ERROR_BEAN_NAME, errorBean, PortletSession.PORTLET_SCOPE);
      }
      errorBean.setLastError(error);
      
      response.setRenderParameter(ERROR_OCCURRED, "true");
   }

   /**
    * @see org.apache.myfaces.portlet.MyFacesGenericPortlet#facesRender(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
    */
   protected void facesRender(RenderRequest request, RenderResponse response) 
      throws PortletException, IOException
   {
      if (request.getParameter(ERROR_OCCURRED) != null)
      {
         if (logger.isDebugEnabled())
            logger.debug("An error has occurred, redirecting to error page: " + this.errorPage);
         
         response.setContentType("text/html");
         PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(this.errorPage);
         dispatcher.include(request, response);
      }
      else
      {
         super.facesRender(request, response);
      }
   }
}
