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
            logger.debug("Uploading file...");
         
         HttpSession session = request.getSession();
         ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
         List<FileItem> fileItems = upload.parseRequest(request);
         
         Iterator<FileItem> iter = fileItems.iterator();
         FileUploadBean bean = new FileUploadBean();
         while(iter.hasNext())
         {
            FileItem item = iter.next();
            String filename = item.getName();
            if(item.isFormField())
            {
               if (item.getFieldName().equalsIgnoreCase("return-page"))
               {
                  returnPage = item.getString();
               }
            }
            else
            {
               File tempFile = File.createTempFile("alfresco", ".upload");
               tempFile.deleteOnExit();
               item.write(tempFile);
               bean.setFile(tempFile);
               bean.setFileName(filename);
               bean.setFilePath(tempFile.getAbsolutePath());
               session.setAttribute(FileUploadBean.FILE_UPLOAD_BEAN_NAME, bean);
            }
         }
         
         if (returnPage == null || returnPage.length() == 0)
         {
            throw new AlfrescoRuntimeException("return-page parameter has not been supplied");
         }

         // finally redirect
         if (logger.isDebugEnabled())
            logger.debug("Upload complete, redirecting to: " + returnPage);

         response.sendRedirect(returnPage);
      }
      catch (Throwable error)
      {
         // ******************************************************************
         // TODO: configure the error page and re-throw error if not specified
         //       we also need to calculate the return page so we don't rely
         //       on the browser history - we may also want to do a forward
         //       instead of a redirect!!
         // ******************************************************************
         
         // get the error bean from the session and set the error that occurred.
         HttpSession session = request.getSession();
         ErrorBean errorBean = (ErrorBean)session.getAttribute(ErrorBean.ERROR_BEAN_NAME);
         if (errorBean == null)
         {
            errorBean = new ErrorBean();
            session.setAttribute(ErrorBean.ERROR_BEAN_NAME, errorBean);
         }
         errorBean.setLastError(error);
         errorBean.setReturnPage(returnPage);

         if (logger.isDebugEnabled())
            logger.debug("An error has occurred, redirecting to error page: /jsp/error.jsp");
         
         response.sendRedirect(request.getContextPath() + "/jsp/error.jsp");
      }
   }
}
