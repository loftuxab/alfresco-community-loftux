/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.connector.remote;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.auth.AuthScope;

/**
 * @author muzquiano
 */
public class HttpClient extends AbstractClient
{
    public HttpClient(String endpoint)
    {
        super(endpoint);
    }

    // ///////////////

    protected void stampParameters(HttpMethod method, Map params)
    {
        int count = 0;
        NameValuePair[] nvpArray = new NameValuePair[params.size()];
        Iterator it = params.keySet().iterator();
        while (it.hasNext())
        {
            String key = (String) it.next();
            String value = (String) params.get(key);
            if (value != null)
            {
                NameValuePair nvp = new NameValuePair(key, value);
                nvpArray[count] = nvp;
                count++;
            }
        }
        method.setQueryString(nvpArray);
    }

    protected void stampHeaders(HttpMethod method, Map headers)
    {
        Iterator it = headers.keySet().iterator();
        while (it.hasNext())
        {
            String key = (String) it.next();
            String value = (String) headers.get(key);
            if (value != null)
                method.addRequestHeader(key, value);
        }
    }

    // ///////////////

    public void init(Map params, Map headers, String uri)
    {
        String url = this.getEndpoint();
        if (uri != null)
            url = url + uri;

        System.out.println("Calling init: " + url);
        this.client = new org.apache.commons.httpclient.HttpClient();
        this.method = new org.apache.commons.httpclient.methods.GetMethod(url);

        // stamp request parameters and headers onto the method
        if (params != null)
            stampParameters(method, params);
        if (headers != null)
            stampHeaders(method, headers);
    }

    public String execute() throws IOException
    {
        this.client.executeMethod(method);

        return method.getResponseBodyAsString();
    }

    public void setAuthenticationMode(int authenticationMode)
    {
        this.authenticationMode = authenticationMode;
    }

    public int getAuthenticationMode()
    {
        return this.authenticationMode;
    }

    protected int authenticationMode = 0;
    protected org.apache.commons.httpclient.HttpClient client;
    protected org.apache.commons.httpclient.methods.GetMethod method;

    protected void applyCredentials(Credentials credentials)
    {
        this.client.getState().setCredentials(
                new AuthScope(getHost(), getPort(), AuthScope.ANY_REALM),
                credentials);
    }

    public static int AUTHENTICATION_NONE = 0;
    public static int AUTHENTICATION_BASIC = 1;

}
