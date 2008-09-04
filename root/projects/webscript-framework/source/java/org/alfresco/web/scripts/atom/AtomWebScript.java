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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.alfresco.web.scripts.Cache;
import org.alfresco.web.scripts.DeclarativeWebScript;
import org.alfresco.web.scripts.Status;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.servlet.WebScriptServletRequest;


/**
 * Abstract ATOM Web Script
 *
 * @author davidc
 */
public abstract class AtomWebScript extends DeclarativeWebScript 
{
    // dependencies
    protected AbderaService abderaService;
    
    /**
     * Sets the Abdera Service
     * 
     * @param abderaService
     */
    public void setAbderaService(AbderaService abderaService)
    {
       this.abderaService = abderaService; 
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.Status, org.alfresco.web.scripts.Cache)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        if (!(req instanceof WebScriptServletRequest))
        {
            throw new WebScriptException("ATOM Web Scripts require servlet runtime");
        }

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("slug", req.getHeader("Slug"));
        model.put("type", req.getHeader("Content-Type"));
        Map<String,QName> qnames = new HashMap<String,QName>();
        qnames.putAll(abderaService.getNames());
        Map<String,QName> scriptQnames = (Map<String,QName>)req.getServiceMatch().getWebScript().getDescription().getExtensions().get("qnames");
        if (scriptQnames != null)
        {
            qnames.putAll(scriptQnames);
        }
        model.put("qnames", qnames);
        return model;
    }

}
