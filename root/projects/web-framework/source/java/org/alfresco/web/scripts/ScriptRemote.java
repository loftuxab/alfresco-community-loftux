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
package org.alfresco.web.scripts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.util.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Script root object for accessing data from remote webscripts.
 * 
 * Generally remote webscripts will be "data" webscripts (i.e. returning XML/JSON) and
 * will be housed within an Alfresco Repository server.
 * 
 * @author Kevin Roast
 */
public class ScriptRemote
{
   private static Log logger = LogFactory.getLog(ScriptRemote.class);
   
   private static final int BUFFERSIZE = 4096;

   private String endpoint;
   private String defaultEncoding;
   
   // TODO: remove this - for testing only!
   private String username;
   private String password;
   
   
   /**
    * Construction
    * 
    * @param endpoint         HTTP API endpoint of remote Alfresco server webapp
    *                         For example http://servername:8080/alfresco
    * @param defaultEncoding  Encoding to use when converting responses that do not specify one
    */
   public ScriptRemote(String endpoint, String defaultEncoding)
   {
      this.endpoint = endpoint;
      this.defaultEncoding = defaultEncoding;
   }
   
   // TODO: remove this - for testing only!
   public void setUsernamePassword(String user, String pass)
   {
      this.username = user;
      this.password = pass;
   }
   
   /**
    * Call a remote WebScript uri. The endpoint as supplied in the constructor will be used
    * as the prefix for the full WebScript url.
    * 
    * @param uri     WebScript URI - for example /test/myscript?arg=value
    * 
    * @return Response object from the call {@link Response}
    */
   public Response call(String uri)
   {
      String result = null;
      Status status = new Status();
      try
      {
         //
         // TODO: Authentication ticket! Either pass in to constructor (must be authenticated
         //       before main page is rendered) or assume uri already has ticket argument.
         //
         URL url = new URL(endpoint + uri);
         ByteArrayOutputStream bOut = new ByteArrayOutputStream(BUFFERSIZE);
         String encoding = service(url, bOut, status);
         if (encoding != null)
         {
            result = bOut.toString(encoding);
         }
         else
         {
            result = (defaultEncoding != null ? bOut.toString(defaultEncoding) : bOut.toString());
         }
      }
      catch (IOException ioErr)
      {
         // error information already applied to Status object during service() call
      }
      
      return new Response(result, status);
   }

   /**
    * Service a remote URL and write the the result into an output stream.
    * 
    * @param url     The URL to open and retrieve data from
    * @param out     The outputstream to write result to
    * @param status  The status object to apply the response code too
    * 
    * @return encoding specified by the source URL - may be null
    * 
    * @throws IOException
    */
   private String service(URL url, OutputStream out, Status status)
      throws IOException
   {
      HttpURLConnection connection = null;
      try
      {
         connection = (HttpURLConnection)url.openConnection();
         
         // TODO: remove this once authentication has been added!
         if (this.username != null && this.password != null)
         {
            String auth = this.username + ':' + this.password;
            connection.addRequestProperty("Authorization", "Basic " + Base64.encodeBytes(auth.getBytes()));
         }
         
         // locate encoding from the response headers
         String encoding = null;
         String ct = connection.getContentType();
         if (ct != null)
         {
            int csi = ct.indexOf("charset=");
            if (csi != -1)
            {
               encoding = ct.substring(csi + 8);
            }
         }
         
         // write the service result to the output stream
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
            }
            catch (IOException e)
            {
               // TODO: log IO exceptions from close()? - probably not a fatal error...
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
   
   
   /**
    * Representation of the response from a remote HTTP API call.
    * 
    * @author Kevin Roast
    */
   public static class Response
   {
      private String data;
      private Status status;
      
      Response(String data, Status status)
      {
         this.data = data;
         this.status = status;
      }
      
      /**
       * @return the data stream from the response object - will be null on error
       */
      public String getResponse()
      {
         return this.data;
      }
      
      /**
       * @return Status object representing the response status and any error information {@link Status}
       */
      public Status getStatus()
      {
         return this.status;
      }

      @Override
      public String toString()
      {
         return this.data;
      }
   }
}
