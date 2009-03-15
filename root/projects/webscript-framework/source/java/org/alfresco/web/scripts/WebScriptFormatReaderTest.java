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

import org.alfresco.web.scripts.TestWebScriptServer.PostRequest;
import org.alfresco.web.scripts.TestWebScriptServer.PutRequest;
import org.alfresco.web.scripts.TestWebScriptServer.Request;
import org.alfresco.web.scripts.TestWebScriptServer.Response;
import org.json.JSONObject;

/**
 * Unit test to test Web Script API
 * 
 * @author David Ward
 */

public class WebScriptFormatReaderTest extends TestCase
{
    private static final String URL_REQUESTBODY = "/test/requestbody";
    private static final String URL_JSONECHO = "/test/jsonecho";
    private static final String URL_ENCODEDPOST = "/test/encodedpost";
    private static final String URL_ATOMENTRY = "/test/atomentry";
    private static final String URL_BOGUS = "/test/bogus";
    private static final TestWebScriptServer TEST_SERVER = TestWebScriptServer.getTestServer();

    /**
     * Ensure that, for a non request type specific .js script, the request body
     * is available as requestbody.
     * 
     * @throws Exception
     */
    public void testRequestBody() throws Exception
    {
        String requestBody = "<html><head>Expected Result</head><body>Hello World</body></html>";
        sendRequest(new PutRequest(URL_REQUESTBODY, requestBody, "text/html"), 200, requestBody);
    }

    /**
     * Ensure that for a .json.js script and an application/json request, the
     * json string is available as "json".
     * 
     * @throws Exception
     */
    public void testJson() throws Exception
    {
        JSONObject json = new JSONObject();
        json.put("company", "Alfresco Software Inc.");
        json.put("building", "Park House");
        json.put("street", "Park Street");
        json.put("town", "Maidenhead");

        String postCode = "SL6 1SL";
        json.put("postCode", postCode);
        json.put("country", "United Kingdom");
        json.put("year", 2008);
        json.put("valid", true);

        String requestBody = json.toString();
        sendRequest(new PostRequest(URL_JSONECHO, requestBody, "application/json; charset=UTF-8"), 200, postCode);
    }

    /**
     * 
     * @throws Exception
     */
    public void testXWwwFormUrlEncoded() throws Exception
    {
        //FIXME URL-encoded post of forms data is not yet working.
        String requestBody = "param1=a&param2=Hello+World";
        String expectedResponse = "<html><body>a<br/>Hello World</body></html>";
        sendRequest(new PostRequest(URL_ENCODEDPOST, requestBody,
                "application/x-www-form-urlencoded; charset=UTF-8"), 200, expectedResponse);
    }

    /**
     * Ensure that for a .atom.js script and an application/atom+xml;type=entry
     * request (for which a less generalized atomentry format is registered) the
     * entry variable is available as "entry".
     * 
     * @throws Exception
     */
    public void testAtomEntry() throws Exception
    {
        String entryTitle = "Test Atom Entry";
        String requestBody = "<entry xmlns=\"http://www.w3.org/2005/Atom\">" + "<title>" + entryTitle + "</title>"
                + "</entry>";
        sendRequest(new PostRequest(URL_ATOMENTRY, requestBody, "application/atom+xml;type=entry"), 200, entryTitle);
    }

    /**
     * Ensure that for a .bogus.js script and an application/bogus request, an
     * error is returned because the bogus format is registered, but no
     * FormatReader is registered.
     * 
     * @throws Exception
     */
    public void testBogus() throws Exception
    {
        String requestBody = "I've got a lovely bunch of coconuts";
        sendRequest(new PostRequest(URL_BOGUS, requestBody, "application/bogus"), 500, null);
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
        System.out.println("* Request: " + req.getMethod() + " " + req.getFullUri()
                + (req.getBody() == null ? "" : "\n" + req.getBody()));

        Response res = TEST_SERVER.submitRequest(req);

        System.out.println();
        System.out.println("* Response: " + res.getStatus() + " " + req.getMethod() + " " + req.getFullUri() + "\n"
                + res.getContentAsString());
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
