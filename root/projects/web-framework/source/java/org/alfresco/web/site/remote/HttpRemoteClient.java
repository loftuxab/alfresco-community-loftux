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
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.site.remote;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * @author muzquiano
 */
public class HttpRemoteClient extends Client
{
    protected HttpRemoteClient(String host, int port, String uri,
            String protocol)
    {
        super(host, port, protocol);
        this.uri = uri;
    }

    /////////////////

    protected void stampIdentity(Identity identity)
    {
        if (getAuthenticationMode() == HttpRemoteClient.AUTHENTICATION_BASIC)
        {
            if (identity instanceof UserPasswordIdentity)
            {
                String username = ((UserPasswordIdentity) identity).getUsername();
                String password = ((UserPasswordIdentity) identity).getPassword();

                Credentials defaultcreds = new UsernamePasswordCredentials(
                        username, password);
                this.client.getState().setCredentials(
                        new AuthScope(getHost(), getPort(), AuthScope.ANY_REALM),
                        defaultcreds);
            }
        }
    }

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

    /////////////////

    public void init(Map params, Map headers)
    {
        this.client = new HttpClient();
        this.method = new GetMethod(getURL());

        // stamp request parameters and headers onto the method
        if (params != null)
            stampParameters(method, params);
        if (headers != null)
            stampHeaders(method, headers);
    }

    public String execute() throws IOException
    {
        return execute(null);
    }

    public String execute(Identity identity) throws IOException
    {
        if (identity != null)
            stampIdentity(identity);

        // execute
        this.client.executeMethod(method);

        String responseBody = method.getResponseBodyAsString();
        return responseBody;
    }

    public String getURL()
    {
        return getProtocol() + "://" + getHost() + ":" + getPort() + getUri();
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public void setAuthenticationMode(int authenticationMode)
    {
        this.authenticationMode = authenticationMode;
    }

    public int getAuthenticationMode()
    {
        return this.authenticationMode;
    }

    public void setAuthenticationMode(String authenticationString)
    {
        if ("basic".equalsIgnoreCase(authenticationString))
            setAuthenticationMode(HttpRemoteClient.AUTHENTICATION_BASIC);
    }

    protected int authenticationMode = 0;
    protected String uri;
    protected HttpClient client;
    protected GetMethod method;

    public static int AUTHENTICATION_NONE = 0;
    public static int AUTHENTICATION_BASIC = 1;

}
