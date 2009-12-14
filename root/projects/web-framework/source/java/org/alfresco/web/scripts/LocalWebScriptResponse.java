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
package org.alfresco.web.scripts;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;

import org.springframework.extensions.surf.util.URLEncoder;

/**
 * Implementation of a WebScript Response object for WebScript Component type.
 * Mostly based on the existing WebScriptResponseImpl - just adds support for
 * encoding URLs to manage user click requests to any component on the page.
 * 
 * @author kevinr
 */
public class LocalWebScriptResponse extends WebScriptResponseImpl
{
    private Writer out;
    private Runtime runtime;
    private LocalWebScriptContext context;
    private String scriptUrlPrefix = null;

    public LocalWebScriptResponse(Runtime runtime, LocalWebScriptContext context, Writer out)
    {
        super(runtime);
        this.context = context;
        this.runtime = runtime;
        this.out = out;
    }

    public String encodeScriptUrl(String url)
    {
        // encode to allow presentation tier webscripts to call back to themselves on a page
        // needs the servlet URL plus args to identify the webscript id and the new url
        if (scriptUrlPrefix == null)
        {
            // And build up the request URL - may be used by webscript responses to build further urls
            StringBuffer buf = new StringBuffer(128);
            buf.append(context.RenderContext.getRequest().getRequestURI());
            boolean first = true;
            for (Map.Entry<String, String> entry : context.Tokens.entrySet())
            {
                String key = entry.getKey();
                if (!WebScriptProcessor.PARAM_WEBSCRIPT_URL.equals(key) &&
                    !WebScriptProcessor.PARAM_WEBSCRIPT_ID.equals(key))
                {
                    String value = entry.getValue();                
                    buf.append(first ? '?' : '&').append(key).append('=').append(URLEncoder.encode(value));
                    first = false;
                }
            }
            scriptUrlPrefix = buf.toString();
        }
        return scriptUrlPrefix + (context.Tokens.size() != 0 ? '&' : '?') +
               WebScriptProcessor.PARAM_WEBSCRIPT_URL + "=" +
               URLEncoder.encode(url) + "&" +
               WebScriptProcessor.PARAM_WEBSCRIPT_ID + "=" + context.Object.getId();
    }

    public String getEncodeScriptUrlFunction(String name)
    {
        // TODO: may be required?
        return null;
    }

    public OutputStream getOutputStream() throws IOException
    {
        // NOTE: not support by local WebScript runtime 
        return null;
    }

    public Writer getWriter() throws IOException
    {
        return this.out;
    }

    public void reset()
    {
        // not supported
    }

    public void setCache(Cache cache)
    {
        // not supported
    }

    public void setHeader(String name, String value)
    {
        // not supported
    }

    public void addHeader(String name, String value)
    {
        // not supported
    }

    public void setContentType(String contentType)
    {
        // not supported
    }

    public void setContentEncoding(String contentEncoding)
    {
        // not supported
    }

    public void setStatus(int status)
    {
        // not supported
    }
}
