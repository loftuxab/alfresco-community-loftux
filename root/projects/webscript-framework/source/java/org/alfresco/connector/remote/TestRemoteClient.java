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
package org.alfresco.connector.remote;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;

import junit.framework.TestCase;

/**
 * JUnit test for Remote HTTP client and Remote Store API.
 * 
 * Requires that an Alfresco repo is running on http://localhost:8080/alfresco
 * and that the default remote store (an AVM store called 'sitestore') exists.
 * 
 * @author Kevin Roast
 */
public class TestRemoteClient extends TestCase
{
   String TEST_CONTENT1 = "some test text";
   String TEST_CONTENT2 = "some text text - updated";
   
   public void testRemoteClient()
   {
      RemoteClient remote = new RemoteClient("http://localhost:8080/alfresco/s");
      
      Response res = remote.call("/index");
      assertEquals(200, res.getStatus().getCode());
      assertTrue(res.getResponse().length()!= 0);
   }
   
   public void testRemoteStore()
   {
      RemoteClient remote = new RemoteClient("http://localhost:8080/alfresco/s");
      
      // POST a new file
      String filename = Long.toString((new Random().nextLong())) + ".txt";
      Response res = remote.call("/remotestore/create/" + filename, new ByteArrayInputStream(TEST_CONTENT1.getBytes()));
      assertEquals(200, res.getStatus().getCode());
      
      // get it back again into a response string
      res = remote.call("/remotestore/get/" + filename);
      assertEquals(200, res.getStatus().getCode());
      assertEquals(TEST_CONTENT1, res.getResponse());
      
      // get it back again into an output stream
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      res = remote.call("/remotestore/get/" + filename, out);
      assertEquals(200, res.getStatus().getCode());
      assertEquals(TEST_CONTENT1, out.toString());
      
      // test simple 'has' method
      res = remote.call("/remotestore/has/" + filename);
      assertEquals(200, res.getStatus().getCode());
      assertEquals(res.getResponse(), "true");
      
      // POST to update the file
      res = remote.call("/remotestore/update/" + filename, new ByteArrayInputStream(TEST_CONTENT2.getBytes()));
      assertEquals(200, res.getStatus().getCode());
      
      // get it back again to confirm update
      out = new ByteArrayOutputStream();
      res = remote.call("/remotestore/get/" + filename, out);
      assertEquals(200, res.getStatus().getCode());
      assertEquals(TEST_CONTENT2, out.toString());
   }
}
