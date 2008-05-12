/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing
 */
package org.alfresco.connector;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.util.Base64;
import org.alfresco.web.scripts.Status;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;

/**
 * Remote client for for accessing data from remote URLs.
 * 
 * Can be used as a Script root object for simple HTTP requests.
 * 
 * Generally remote URLs will be "data" webscripts (i.e. returning XML/JSON) called from
 * web-tier script objects and will be housed within an Alfresco Repository server.
 * 
 * Also supports POST of content data 
 * 
 * A 'Response' is returned containing the response data stream as a String and the Status
 * object representing the status code and error information if any.
 * 
 * @author Kevin Roast
 */
public class RemoteClient extends AbstractClient
{
   private static Log logger = LogFactory.getLog(RemoteClient.class);
   
   private static final String CHARSETEQUALS = "charset=";
   private static final int BUFFERSIZE = 4096;

   private String defaultEncoding;
   private String ticket;
   private String requestContentType;
   
   private String username;
   private String password;
   
   
   /**
    * Construction
    * 
    * @param endpoint         HTTP API endpoint of remote Alfresco server webapp
    *                         For example http://servername:8080/alfresco
    */
   public RemoteClient(String endpoint)
   {
      this(endpoint, null);
   }
   
   /**
    * Construction
    * 
    * @param endpoint         HTTP API endpoint of remote Alfresco server webapp
    *                         For example http://servername:8080/alfresco
    * @param defaultEncoding  Encoding to use when converting responses that do not specify one
    */
   public RemoteClient(String endpoint, String defaultEncoding)
   {
      super(endpoint);
      this.defaultEncoding = defaultEncoding;
   }
   
   /**
    * Authentication ticket to use
    * 
    * @param ticket
    */
   public void setTicket(String ticket)
   {
      this.ticket = ticket;
   }
   
   /**
    * Basic HTTP auth
    * 
    * @param user
    * @param pass
    */
   public void setUsernamePassword(String user, String pass)
   {
      this.username = user;
      this.password = pass;
   }

   /**
    * @param requestContentType     the POST request "Content-Type" header value to set
    */
   public void setRequestContentType(String contentType)
   {
       this.requestContentType = contentType;
   }

   /**
    * Call a remote WebScript uri. The endpoint as supplied in the constructor will be used
    * as the prefix for the full WebScript url.
    * 
    * This API is generally called from a script host.
    * 
    * @param uri     WebScript URI - for example /test/myscript?arg=value
    * 
    * @return Response object from the call {@link Response}
    */
   public Response call(String uri)
   {
      return call(uri, true, null);
   }
   
   /**
    * Call a remote WebScript uri. The endpoint as supplied in the constructor will be used
    * as the prefix for the full WebScript url.
    * 
    * @param uri    WebScript URI - for example /test/myscript?arg=value
    * @param in     The optional InputStream to the call - if supplied a POST will be performed
    * 
    * @return Response object from the call {@link Response}
    */
   public Response call(String uri, InputStream in)
   {
      return call(uri, true, in);
   }
   
   /**
    * Call a remote WebScript uri. The endpoint as supplied in the constructor will be used
    * as the prefix for the full WebScript url.
    * 
    * @param uri    WebScript URI - for example /test/myscript?arg=value
    * @param buildResponseString   True to build a String result automatically based on the response
    *                              encoding, false to instead return the InputStream in the Response.
    * @param in     The optional InputStream to the call - if supplied a POST will be performed
    * 
    * @return Response object from the call {@link Response}
    */
   public Response call(String uri, boolean buildResponseString, InputStream in)
   {
      Response result;
      Status status = new Status();
      try
      {
         ByteArrayOutputStream bOut = new ByteArrayOutputStream(BUFFERSIZE);
         String encoding = service(buildURL(uri), in, bOut, status);
         if (buildResponseString)
         {
            String data;
            if (encoding != null)
            {
               data = bOut.toString(encoding);
            }
            else
            {
               data = (defaultEncoding != null ? bOut.toString(defaultEncoding) : bOut.toString());
            }
            result = new Response(data, status);
         }
         else
         {
            result = new Response(new ByteArrayInputStream(bOut.toByteArray()), status);
         }
         result.setEncoding(encoding);
      }
      catch (IOException ioErr)
      {
         if (logger.isDebugEnabled())
            logger.debug("Error status " + status.getCode() + " " + status.getMessage());
         
         // error information already applied to Status object during service() call
         result = new Response(status);
      }
      
      return result;
   }
   
   /**
    * Call a remote WebScript uri. The endpoint as supplied in the constructor will be used
    * as the prefix for the full WebScript url.
    * 
    * @param uri    WebScript URI - for example /test/myscript?arg=value
    * @param out    OutputStream to stream successful response to - will be closed automatically.
    *               A response data string will not therefore be available in the Response object.
    *               If remote call fails the OutputStream will not be modified or closed.
    * 
    * @return Response object from the call {@link Response}
    */
   public Response call(String uri, OutputStream out)
   {
      return call(uri, null, out);
   }
   
   /**
    * Call a remote WebScript uri. The endpoint as supplied in the constructor will be used
    * as the prefix for the full WebScript url.
    * 
    * @param uri    WebScript URI - for example /test/myscript?arg=value
    * @param in     The optional InputStream to the call - if supplied a POST will be performed
    * @param out    OutputStream to stream successful response to - will be closed automatically.
    *               A response data string will not therefore be available in the Response object.
    *               If remote call fails the OutputStream will not be modified or closed.
    * 
    * @return Response object from the call {@link Response}
    */
   public Response call(String uri, InputStream in, OutputStream out)
   {
      Response result;
      Status status = new Status();
      try
      {
         String encoding = service(buildURL(uri), in, out, status);
         result = new Response(status);
         result.setEncoding(encoding);
      }
      catch (IOException ioErr)
      {
         if (logger.isDebugEnabled())
            logger.debug("Error status " + status.getCode() + " " + status.getMessage());
         
         // error information already applied to Status object during service() call
         result = new Response(status);
      }
      
      return result;
   }

   /**
    * Build the URL object based on the supplied uri and configured endpoint. Ticket
    * will be appiled as an argument if available.
    * 
    * @param uri     URI to build URL against
    * 
    * @return the URL object representing the call.
    * 
    * @throws MalformedURLException
    */
   private URL buildURL(String uri) throws MalformedURLException
   {
      URL url;
      if (this.ticket == null)
      {
         url = new URL(endpoint + uri);
      }
      else
      {
         url = new URL(endpoint + uri +
                       (uri.lastIndexOf('?') == -1 ? ("?alf_ticket="+ticket) : ("&alf_ticket="+ticket)));
      }
      return url;
   }

   /**
    * Service a remote URL and write the the result into an output stream.
    * If an InputStream is provided then a POST will be performed with the content
    * pushed to the url. Otherwise a standard GET will be performed.
    * 
    * @param url    The URL to open and retrieve data from
    * @param in     The optional InputStream - if set a POST will be performed
    * @param out    The OutputStream to write result to
    * @param status The status object to apply the response code too
    * 
    * @return encoding specified by the source URL - may be null
    * 
    * @throws IOException
    */
   private String service(URL url, InputStream in, OutputStream out, Status status)
      throws IOException
   {
      if (logger.isDebugEnabled())
         logger.debug("Executing " + (in == null ? "(get)" : "(post)") + ' ' + url.toString());
      
      HttpURLConnection connection = null;
      try
      {
         connection = (HttpURLConnection)url.openConnection();
         
         // HTTP basic auth support
         if (this.username != null && this.password != null)
         {
            String auth = this.username + ':' + this.password;
            connection.addRequestProperty("Authorization", "Basic " + Base64.encodeBytes(auth.getBytes()));
         }
         
         // POST to the connection if input supplied
         if (in != null)
         {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty ("Content-Type",
                    (this.requestContentType != null ? this.requestContentType : "application/octet-stream"));
            FileCopyUtils.copy(in, new BufferedOutputStream(connection.getOutputStream()));
         }
         
         // write the connection result to the output stream
         InputStream input = connection.getInputStream();
         try
         {
            byte[] buffer = new byte[BUFFERSIZE];
            int read = input.read(buffer);
            while (read != -1)
            {
               out.write(buffer, 0, read);
               read = input.read(buffer);
            }
         }
         finally
         {
            try
            {
               if (input != null)
               {
                  input.close();
               }
               if (out != null)
               {
                  out.flush();
                  out.close();
               }
               // TODO: required?
               connection.disconnect();
            }
            catch (IOException e)
            {
               if (logger.isWarnEnabled())
                  logger.warn("Exception during close() of HTTP API connection", e);
            }
         }
         
         // locate response encoding from the headers
         String encoding = null;
         String ct = connection.getContentType();
         if (ct != null)
         {
            int csi = ct.indexOf(CHARSETEQUALS);
            if (csi != -1)
            {
               encoding = ct.substring(csi + CHARSETEQUALS.length());
            }
         }
         
         // if we get here call was successful
         status.setCode(HttpServletResponse.SC_OK);
         
         return encoding;
      }
      catch (ConnectException conErr)
      {
         // caught a connection exception - generic error code as won't get one returned
         status.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         status.setException(conErr);
         status.setMessage(conErr.getMessage());
         
         throw conErr;
      }
      catch (IOException ioErr)
      {
         // caught an IO exception - record the status code and message
         status.setCode(connection.getResponseCode());
         status.setException(ioErr);
         status.setMessage(ioErr.getMessage());
         
         throw ioErr;
      }
   }
}
