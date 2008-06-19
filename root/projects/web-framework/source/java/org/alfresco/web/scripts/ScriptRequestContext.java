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

import java.io.Serializable;
import java.util.Iterator;

import org.alfresco.connector.User;
import org.alfresco.web.framework.model.Page;
import org.alfresco.web.framework.model.TemplateInstance;
import org.alfresco.web.framework.model.Theme;
import org.alfresco.web.site.Content;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.ThemeUtil;

/**
 * A read-only root-scoped Java object that wraps the Request Context 
 * and provides lightweight methods for working with the request context 
 * object.
 * 
 * @author muzquiano
 */
public final class ScriptRequestContext extends ScriptBase
{
    protected ScriptContentObject scriptContentObject = null;
    protected ScriptModelObject scriptPageObject = null;
    protected ScriptModelObject scriptTemplateObject = null;
    protected ScriptModelObject scriptThemeObject = null;
    protected ScriptUser scriptUser = null;
    
    /**
     * Constructs a new ScriptRequestContext object.
     * 
     * @param context   The RequestContext instance for the current request
     */
    public ScriptRequestContext(RequestContext context)
    {
        super(context);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebFrameworkScriptBase#buildProperties()
     */
    protected ScriptableMap buildProperties()
    {
        if (this.properties == null)
        {
            this.properties = new ScriptableMap<String, Serializable>();
            
            // copy in any custom values that have been applied to the RequestContext
            Iterator<String> it = context.keys();
            while (it.hasNext())
            {
                String key = it.next();
                Serializable value = (Serializable)context.getValue(key);
                properties.put(key, value);
            }
        }
        
        return this.properties;
    }

    
    // --------------------------------------------------------------
    // JavaScript Properties
    //
    
    public ScriptContentObject getContent()
    {
        if(scriptContentObject == null)
        {
            Content content = context.getCurrentObject();
            if(content != null)
            {
                scriptContentObject = new ScriptContentObject(context, content);
            }
        }
        
        return scriptContentObject;
    }
    
    public String getFormatId()
    {
        return context.getFormatId();
    }
    
    public String getId()
    {
        return context.getId();
    }
    
    public ScriptModelObject getPage()
    {
        if(scriptPageObject == null)
        {
            Page page = context.getPage();
            if(page != null)
            {
                scriptPageObject = new ScriptModelObject(getRequestContext(), page);
            }
        }
        
        return scriptPageObject;
    }
    
    public ScriptModelObject getTemplate()
    {
        if(scriptTemplateObject == null)
        {
            TemplateInstance template = context.getTemplate();
            if(template != null)
            {
                scriptTemplateObject = new ScriptModelObject(getRequestContext(), template);
            }
        }
        
        return scriptTemplateObject;
    }
    
    public String getThemeId()
    {
        return context.getThemeId();
    }
    
    public ScriptModelObject getTheme()
    {
        if(scriptThemeObject == null)
        {
            Theme theme = ThemeUtil.getCurrentTheme(context);
            if(theme != null)
            {
                scriptThemeObject = new ScriptModelObject(getRequestContext(), theme);
            }
        }
        
        return scriptThemeObject;
    }
    
    public ScriptUser getUser()
    {
        if(scriptUser == null)
        {
            User user = context.getUser();
            if(user != null)
            {
                scriptUser = new ScriptUser(context, user);
            }
        }
        
        return scriptUser;
    }   
}
