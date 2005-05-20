package org.alfresco.web;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.bean.ErrorBean;
import org.alfresco.web.bean.FileUploadBean;
import org.alfresco.web.util.Utils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

/**
 * Servlet that takes a file uploaded via a browser and represents it as an
 * UploadFileBean in the session
 * 
 * @author gavinc
 */
public class UploadFileServlet extends HttpServlet
{
   private static Logger logger = Logger.getLogger(UploadFileServlet.class); 
   
   /**
    * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   protected void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
   {
      String returnPage = null;
      boolean isMultipart = ServletFileUpload.isMultipartContent(request);
      
      try
      {  
         if (isMultipart == false)
         {
            throw new AlfrescoRuntimeException("This servlet can only be used to handle file upload requests, make" +
                                        "sure you have set the enctype attribute on your form to multipart/form-data");
         }

         if (logger.isDebugEnabled())
            logger.debug("Uploading servlet servicing...");
         
         HttpSession session = request.getSession();
         ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
         List<FileItem> fileItems = upload.parseRequest(request);
         
         Iterator<FileItem> iter = fileItems.iterator();
         FileUploadBean bean = new FileUploadBean();
         while(iter.hasNext())
         {
            FileItem item = iter.next();
            if(item.isFormField())
            {
               if (item.getFieldName().equalsIgnoreCase("return-page"))
               {
                  returnPage = item.getString();
               }
            }
            else
            {
               String filename = item.getName();
               if (filename != null && filename.length() != 0)
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
                  session.setAttribute(FileUploadBean.FILE_UPLOAD_BEAN_NAME, bean);
                  if (logger.isDebugEnabled())
                     logger.debug("Temp file: " + tempFile.getAbsolutePath() + " created from upload filename: " + filename);
               }
            }
         }
         
         if (returnPage == null || returnPage.length() == 0)
         {
            throw new AlfrescoRuntimeException("return-page parameter has not been supplied");
         }

         // finally redirect
         if (logger.isDebugEnabled())
            logger.debug("Upload servicing complete, redirecting to: " + returnPage);

         response.sendRedirect(returnPage);
      }
      catch (Throwable error)
      {
         Utils.handleServletError(getServletContext(), (HttpServletRequest)request,
               (HttpServletResponse)response, error, logger, returnPage);
      }
   }
}
