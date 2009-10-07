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

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.i18n.I18NUtil;
import org.alfresco.util.StringBuilderWriter;
import org.alfresco.web.scripts.json.JSONWriter;

/**
 * WebScript responsible for returning a JavaScript response containing a JavaScript
 * associative array of all I18N messages name/key pairs installed on the web-tier.
 * <p>
 * The JavaScript object is created as 'Alfresco.messages' - example usage:
 * <code>
 * var msg = Alfresco.messages["messageid"];
 * </code>
 * 
 * @author Kevin Roast
 */
public class MessagesWebScript extends AbstractWebScript
{
    /**
     * As WebScript beans as singletons, we can create a new Date() once when runtime
     * instantiates bean and use this as the "last modified" for the global messages
     * WebScript response Cache value.
     */
    private Cache cache;
    
    /**
     * The response is built once per locale and cached - they do not change for the life
     * of the server instance.
     */
    private Map<String, String> messages = new HashMap<String, String>(8);
    
    
    /**
     * Construction
     */
    public MessagesWebScript()
    {
        cache = new Cache();
        cache.setNeverCache(false);
        cache.setMustRevalidate(true);
        cache.setLastModified(new Date());
        cache.setMaxAge(6000L);
    }
    
    /**
     * Execute the webscript and return the cached JavaScript response
     */
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        res.setContentType(Format.JAVASCRIPT.mimetype() + ";charset=UTF-8");
        res.setCache(cache);
        
        String locale = req.getParameter("locale");
        if (locale == null || locale.length() == 0)
        {
            throw new WebScriptException("Locale parameter is mandatory.");
        }
        
        String result;
        synchronized (messages)
        {
            result = messages.get(locale);
            if (result == null)
            {
                Writer writer = new StringBuilderWriter(8192);
                writer.write("if (typeof Alfresco == \"undefined\" || !Alfresco) {var Alfresco = {};}\r\n");
                writer.write("Alfresco.messages = Alfresco.messages || {global: null, scope: {}}\r\n");
                writer.write("Alfresco.messages.global = ");
                JSONWriter out = new JSONWriter(writer);
                try
                {
                    out.startObject();
                    Map<String, String> messages = I18NUtil.getAllMessages(I18NUtil.parseLocale(locale));
                    for (Map.Entry<String, String> entry : messages.entrySet())
                    {
                        out.writeValue(entry.getKey(), entry.getValue());
                    }
                    out.endObject();
                }
                catch (IOException jsonErr)
                {
                    throw new WebScriptException("Error building messages response.", jsonErr);
                }
                writer.write(";\r\n");
                
                // community tracking logo
                final String serverPath = req.getServerPath();
                final int schemaIndex = serverPath.indexOf(':');
                writer.write("window.setTimeout(function(){(document.getElementById('alfresco-yuiloader')||document.createElement('div')).innerHTML = '<img src=\"");
                writer.write(serverPath.substring(0, schemaIndex));
                writer.write("://www.alfresco.com/assets/images/logos/community-edition-3.2.png\" alt=\"*\" style=\"display:none\"/>\'}, 100);\r\n");
                
                // retrieve result from the writer and cache for this locale
                result = writer.toString();
                messages.put(locale, result);
            }
        }
        res.getWriter().write(result);
        res.getWriter().flush();
        res.getWriter().close();
    }
}
