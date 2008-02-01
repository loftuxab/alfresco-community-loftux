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
import java.net.URL;
import java.net.URLConnection;

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
   private String encoding;
   
   
   public ScriptRemote(String endpoint, String encoding)
   {
      this.endpoint = endpoint;
      this.encoding = encoding;
   }
   
   public String call(String uri)
   {
      String result = null;
      
      try
      {
         // TODO: Authentication ticket! Either pass in to constructor (must be authenticated
         //       before main page is rendered) or assume uri already has ticket argument. 
         URL url = new URL(this.endpoint + uri);
         ByteArrayOutputStream bOut = new ByteArrayOutputStream(BUFFERSIZE);
         service(url, bOut);
         result = (encoding != null ? bOut.toString(encoding) : bOut.toString());
      }
      catch (IOException ioErr)
      {
         // TODO: how to handle and report errors?
      }
      
      return result;
   }

   private void service(URL url, OutputStream out)
      throws IOException
   {
      URLConnection connection = url.openConnection();
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
         catch(IOException e)
         {
            // TODO: log io exceptions?
         }
      }
   }
}
