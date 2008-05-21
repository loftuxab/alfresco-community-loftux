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
package org.alfresco.web.scripts;

import java.io.Writer;
import java.util.Map;

import org.alfresco.web.site.WebFrameworkConstants;
import org.alfresco.web.uri.UriUtils;

/**
 * @author kevinr
 * @author muzquiano
 */
public class LocalWebScriptRuntime extends AbstractRuntime
{
    private LocalWebScriptContext context;
    private Writer out;

    public LocalWebScriptRuntime(
            Writer out, RuntimeContainer container, LocalWebScriptContext context) 
    {
        super(container);
        
        this.out = out;
        this.context = context;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Runtime#getName()
     */
    public String getName()
    {
        return "Web Framework Runtime";
    }

    @Override
    protected String getScriptUrl()
    {
        return context.ScriptUrl;
    }

    @Override
    protected WebScriptRequest createRequest(Match match)
    {
        // this includes all elements of the xml
        Map properties = context.Object.getProperties();
        String scriptUrl = context.ExecuteUrl;

        // component ID is always available to the component
        properties.put("id", context.Object.getId());

        // add/replace the "well known" context tokens in component properties
        for (String arg : context.Object.getCustomProperties().keySet())
        {
            properties.put(arg, UriUtils.replaceUriTokens((String)context.Object.getCustomProperties().get(arg), context.Tokens));
        }

        // add the html binding id
        String htmlBindingId = (String)context.RendererContext.get(WebFrameworkConstants.RENDER_DATA_HTML_BINDING_ID);
        if (htmlBindingId != null)
        {
            properties.put(ProcessorModelHelper.PROP_HTMLID, htmlBindingId);
        }

        return new LocalWebScriptRequest(this, scriptUrl, match, properties);
    }

    @Override
    protected LocalWebScriptResponse createResponse()
    {
        return new LocalWebScriptResponse(this, context, out);
    }

    @Override
    protected String getScriptMethod()
    {
        return "GET";
    }

    @Override
    protected Authenticator createAuthenticator()
    {
        return null;
    }
}
