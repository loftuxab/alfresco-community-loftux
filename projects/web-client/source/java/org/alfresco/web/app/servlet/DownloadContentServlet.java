/*
 * Created on 22-May-2005
 */
package org.alfresco.web.app.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Servlet responsible for streaming node content from the repo directly to the reponse stream.
 * The appropriate mimetype is calculated based on filename extension.
 * <p>
 * The URL to the servlet should be generated thus:
 * <pre>/web-client/download/workspace/SpacesStore/0000-0000-0000-0000/myfile.pdf</pre>
 * the store protocol, followed by the store ID, followed by the content Node Id
 * the last part is used for mimetype calculation and browser default filename
 * 
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
         //       only really needed if we don't use the built in compression of the servlet container
         
         // The URL contains multiple parts
         // /web-client/download/attach/workspace/SpacesStore/0000-0000-0000-0000/myfile.pdf
         // the protocol, followed by the store, followed by the Id
         // the last part is only used for mimetype and browser use
         String uri = req.getRequestURI();
         
         if (logger.isDebugEnabled())
            logger.debug("Processing URL: " + uri);
         
         StringTokenizer t = new StringTokenizer(uri, "/");
         if (t.countTokens() < 7)
         {
            throw new IllegalArgumentException("Download URL did not contain all required args: " + uri); 
         }
         
         t.nextToken();    // skip web app name
         t.nextToken();    // skip servlet name
         boolean attachment = t.nextToken().equals("attach");
         StoreRef storeRef = new StoreRef(t.nextToken(), t.nextToken());
         String id = t.nextToken();
         String filename = t.nextToken();
         NodeRef nodeRef = new NodeRef(storeRef, id);
         if (logger.isDebugEnabled())
         {
            logger.debug("Found NodeRef: " + nodeRef.toString());
            logger.debug("Will use filename: " + filename);
            logger.debug("With attachment mode: " + attachment);
         }
         
         if (attachment == true)
         {
            // set header based on filename - will force a Save As from the browse if it doesn't recognise it
            // this is better than the default response of the browse trying to display the contents!
            // TODO: make this configurable - and check it does not prevent streaming of large files
            res.setHeader("Content-Disposition", "attachment;filename=\"" + URLDecoder.decode(filename) + '"');
         }
         
         // get the content mimetype from the node properties
         NodeService nodeService = (NodeService)context.getBean("nodeService");
         String mimetype = (String)nodeService.getProperty(nodeRef, ContentModel.PROP_MIME_TYPE);
         
         // fall back if unable to resolve mimetype property
         if (mimetype == null || mimetype.length() == 0)
         {
            MimetypeMap mimetypeMap = (MimetypeMap)context.getBean("mimetypeMap");
            mimetype = "text/plain";
            int extIndex = filename.lastIndexOf('.');
            if (extIndex != -1)
            {
               String ext = filename.substring(extIndex + 1);
               String mt = mimetypeMap.getMimetypesByExtension().get(ext);
               if (mt != null)
               {
                  mimetype = mt;
               }
            }
         }
         res.setContentType(mimetype);
         
         // get the content and stream directly to the response output stream
         // assuming the repo is capable of streaming in chunks, this should allow large files
         // to be streamed directly to the browser response stream.
         ContentService contentService = (ContentService)context.getBean("contentService");
         ContentReader reader = contentService.getReader(nodeRef);
         reader.getContent( res.getOutputStream() );
      }
      catch (Throwable err)
      {
         throw new AlfrescoRuntimeException("Error during download content servlet processing: " + err.getMessage(), err);
      }
      finally
      {
         out.close();
      }
   }
   
   /**
    * Helper to generate a URL to a content node for downloading content from the server.
    * The content is supplied as an HTTP1.1 attachment to the response. This generally means
    * a browser should prompt the user to save the content to specified location.
    * 
    * @param ref     NodeRef of the content node to generate URL for (cannot be null)
    * @param name    File name to return in the URL (cannot be null)
    * 
    * @return URL to download the content from the specified node
    */
   public final static String generateDownloadURL(NodeRef ref, String name)
   {
      return MessageFormat.format(DOWNLOAD_URL, new Object[] {
            ref.getStoreRef().getProtocol(),
            ref.getStoreRef().getIdentifier(),
            ref.getId(),
            URLEncoder.encode(name) } );
   }
   
   /**
    * Helper to generate a URL to a content node for downloading content from the server.
    * The content is supplied directly in the reponse. This generally means a browser will
    * attempt to open the content directly if possible, else it will prompt to save the file.
    * 
    * @param ref     NodeRef of the content node to generate URL for (cannot be null)
    * @param name    File name to return in the URL (cannot be null)
    * 
    * @return URL to download the content from the specified node
    */
   public final static String generateBrowserURL(NodeRef ref, String name)
   {
      return MessageFormat.format(BROWSER_URL, new Object[] {
            ref.getStoreRef().getProtocol(),
            ref.getStoreRef().getIdentifier(),
            ref.getId(),
            URLEncoder.encode(name)} );
   }
   
   
   private static Logger logger = Logger.getLogger(DownloadContentServlet.class);
   
   private static final String DOWNLOAD_URL  = "/download/attach/{0}/{1}/{2}/{3}";
   private static final String BROWSER_URL   = "/download/direct/{0}/{1}/{2}/{3}";
}
