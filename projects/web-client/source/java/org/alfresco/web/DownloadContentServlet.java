/*
 * Created on 22-May-2005
 */
package org.alfresco.web;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigService;
import org.alfresco.repo.content.ContentReader;
import org.alfresco.repo.content.ContentService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.web.config.MimeTypeConfigElement;
import org.alfresco.web.config.MimeTypesElementReader;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author Kevin Roast
 */
public class DownloadContentServlet extends HttpServlet
{
   /**
    * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException
   {
      ServletOutputStream out = res.getOutputStream();
      
      try
      {
         WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
         // TODO: add compression here?
         //       see http://servlets.com/jservlet2/examples/ch06/ViewResourceCompress.java for example
         
         // /web-client/download/workspace/SpacesStore/0000-0000-0000-0000/myfile.pdf
         // The URL contains multiple parts
         // the protocol, followed by the store, followed by the Id
         // the last part is only used for mimetype and for browser use
         String uri = req.getRequestURI();
         if (logger.isDebugEnabled())
            logger.debug("Processing URL: " + uri);
         StringTokenizer t = new StringTokenizer(uri, "/");
         if (t.countTokens() < 6)
         {
            throw new IllegalArgumentException("Download URL did not contain all required args: " + uri); 
         }
         t.nextToken();    // skip
         t.nextToken();    // skip
         StoreRef storeRef = new StoreRef(t.nextToken(), t.nextToken());
         String id = t.nextToken();
         String filename = t.nextToken();
         NodeRef nodeRef = new NodeRef(storeRef, id);
         if (logger.isDebugEnabled())
            logger.debug("Found NodeRef: " + nodeRef.toString());
         
         // base the mimetype from the file extension
         ConfigService configService = (ConfigService)context.getBean("configService");
         MimeTypeConfigElement config = (MimeTypeConfigElement)configService.getGlobalConfig()
               .getConfigElement(MimeTypesElementReader.ELEMENT_MIMETYPES);
         
         String mimetype = "text/plain";
         int extIndex = filename.lastIndexOf('.');
         if (extIndex != -1)
         {
            String mt = config.getMimeType(filename.substring(extIndex + 1));
            if (mt != null)
            {
               mimetype = mt;
            }
         }
         res.setContentType(mimetype);
         
         // get the content and stream directly to the response output stream
         ContentService contentService = (ContentService)context.getBean("contentService");
         ContentReader reader = contentService.getReader(nodeRef);
         reader.getContent( res.getOutputStream() );
         
         // TODO: redirect (form parameter? could use form params instead of URL!)
      }
      catch (Throwable err)
      {
         throw new RuntimeException("Error during download content servlet processing: " + err.getMessage(), err);
      }
      finally
      {
         out.close();
      }
   }
      
   private static Logger logger = Logger.getLogger(DownloadContentServlet.class);
}
