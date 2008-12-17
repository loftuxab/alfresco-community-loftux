/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.scripts;

import java.io.IOException;

import junit.framework.TestCase;

import org.alfresco.web.scripts.TestWebScriptServer.GetRequest;
import org.alfresco.web.scripts.TestWebScriptServer.Request;
import org.alfresco.web.scripts.TestWebScriptServer.Response;

/**
 * Unit test to test Web Script API
 * 
 * @author davidc
 */
public class WebScriptExceptionTest extends TestCase
{
    private static final String URL_EXCEPTION = "/test/exception?a=1";

    private static final TestWebScriptServer TEST_SERVER = TestWebScriptServer.getTestServer();

    /**
     * Ensure that, for a non request type specific .js script, the request body
     * is available as requestbody.
     * 
     * @throws Exception
     */
    public void testScriptStatusTemplate() throws Exception
    {
        String res = "Failed /alfresco/service/test/exception - args 1";
        sendRequest(new GetRequest(URL_EXCEPTION), 500, res);
    }

    /**
     * @param req
     * @param expectedStatus
     * @param expectedResponse
     * @return
     * @throws IOException
     */
    private Response sendRequest(Request req, int expectedStatus, String expectedResponse) throws IOException
    {
        System.out.println();
        System.out.println("* Request: " + req.getMethod() + " " + req.getFullUri() + (req.getBody() == null ? "" : "\n" + req.getBody()));

        Response res = TEST_SERVER.submitRequest(req);

        System.out.println();
        System.out.println("* Response: " + res.getStatus() + " " + req.getMethod() + " " + req.getFullUri() + "\n" + res.getContentAsString());
        if (expectedStatus > 0)
        {
            assertEquals("Unexpected status code", expectedStatus, res.getStatus());
        }
        if (expectedResponse != null)
        {
            assertEquals("Unexpected response", expectedResponse, res.getContentAsString());
        }
        return res;
    }
}
