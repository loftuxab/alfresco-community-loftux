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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.scripts.atom;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.util.Content;
import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.Format;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.servlet.WebScriptServletRequest;
import org.apache.abdera.model.Entry;


/**
 * ATOM Entry Web Script
 *
 * @author davidc
 */
public class AtomEntryWebScript extends AtomWebScript 
{

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.Status, org.alfresco.web.scripts.Cache)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        // create generic atom root objects
        Map<String, Object> model = super.executeImpl(req, status, cache);

        // add atom entry (Entry Resource or Media Resource)
        Entry entry = null;
        Content media = null;
        String contentType = req.getHeader("Content-Type");
        if (contentType != null && contentType.startsWith(Format.ATOM.mimetype()))
        {
            try
            {
                String base = req.getServerPath() + req.getServicePath();
                entry = abderaService.parseEntry(((WebScriptServletRequest)req).getHttpServletRequest().getInputStream(), base);
            }
            catch(IOException e)
            {
                throw new WebScriptException(HttpServletResponse.SC_BAD_REQUEST, e.toString());
            }
        }
        else
        {
            entry = abderaService.newEntry();
            media = req.getContent();
        }
        
        model.put("entry", entry);
        model.put("media", media);
        return model;
    }
    
}
