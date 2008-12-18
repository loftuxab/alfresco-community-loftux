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

import org.alfresco.connector.User;
import org.alfresco.web.framework.model.Page;
import org.alfresco.web.framework.model.TemplateInstance;
import org.alfresco.web.framework.model.Theme;
import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.framework.resource.ResourceContent;
import org.alfresco.web.site.AuthenticationUtil;
import org.alfresco.web.site.ThemeUtil;

/**
 * A read-only root-scoped Java object that wraps the Render Context 
 * and provides lightweight methods for working with the render context 
 * object.
 * 
 * @author muzquiano
 */
public final class ScriptRenderContext extends ScriptBase
{
    private ScriptContentObject scriptContentObject = null;
    private ScriptModelObject scriptPageObject = null;
    private ScriptModelObject scriptTemplateObject = null;
    private ScriptModelObject scriptThemeObject = null;
    private ScriptUser scriptUser = null;
    private ScriptLinkBuilder scriptLinkBuilder = null;
    
    final private RenderContext renderContext;
    
    
    /**
     * Constructs a new ScriptRequestContext object.
     * 
     * @param context   The RenderContext instance for the current request
     */
    public ScriptRenderContext(RenderContext context)
    {
        super(context);
        
        this.renderContext = context;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebFrameworkScriptBase#buildProperties()
     */
    protected ScriptableMap buildProperties()
    {
        if (this.properties == null)
        {
            this.properties = new ScriptableWrappedMap(context.getValuesMap());
        }
        
        return this.properties;
    }

    
    // --------------------------------------------------------------
    // JavaScript Properties
    
    /**
     * Gets the id of the content instance
     * 
     * If a content instance is not bound, null is returned.
     */
    public String getContentId()
    {
        return context.getCurrentObjectId();
    }
    
    /**
     * Gets the content instance.
     * 
     * @return the content instance
     */
    public ScriptContentObject getContent()
    {
        if (scriptContentObject == null)
        {
            ResourceContent content = context.getCurrentObject();
            if (content != null)
            {
                scriptContentObject = new ScriptContentObject(context, content);
            }
        }
        
        return scriptContentObject;
    }
    
    /**
     * Gets the format id.
     * 
     * @return the format id
     */
    public String getFormatId()
    {
        return context.getFormatId();
    }
    
    /**
     * Gets the id.
     * 
     * @return the id
     */
    public String getId()
    {
        return context.getId();
    }
    
    /**
     * Gets the id of the page
     * 
     * @return the id
     */
    public String getPageId()
    {
        return context.getPageId();
    }    
    
    /**
     * Gets the page.
     * 
     * @return the page
     */
    public ScriptModelObject getPage()
    {
        if (scriptPageObject == null)
        {
            Page page = context.getPage();
            if (page != null)
            {
                scriptPageObject = new ScriptModelObject(this.context, page);
            }
        }
        
        return scriptPageObject;
    }
    
    /**
     * Gets the id of the template
     * 
     * @return the id
     */
    public String getTemplateId()
    {
        return context.getTemplateId();
    }    
    
    /**
     * Gets the template.
     * 
     * @return the template
     */
    public ScriptModelObject getTemplate()
    {
        if (scriptTemplateObject == null)
        {
            TemplateInstance template = context.getTemplate();
            if (template != null)
            {
                scriptTemplateObject = new ScriptModelObject(this.context, template);
            }
        }
        
        return scriptTemplateObject;
    }
    
    /**
     * Gets the theme id.
     * 
     * @return the theme id
     */
    public String getThemeId()
    {
        return context.getThemeId();
    }
    
    /**
     * Gets the theme.
     * 
     * @return the theme
     */
    public ScriptModelObject getTheme()
    {
        if (scriptThemeObject == null)
        {
            Theme theme = ThemeUtil.getCurrentTheme(context);
            if (theme != null)
            {
                scriptThemeObject = new ScriptModelObject(this.context, theme);
            }
        }
        
        return scriptThemeObject;
    }
    
    /**
     * Gets the user.
     * 
     * @return the user
     */
    public ScriptUser getUser()
    {
        if (scriptUser == null)
        {
            User user = context.getUser();
            if (user != null)
            {
                scriptUser = new ScriptUser(context, user);
            }
        }
        
        return scriptUser;
    } 
    
    /**
     * Returns whether the current user is authenticated
     * If there is no curren tuesr, this will return false.
     * 
     * @return whether the user is authenticated
     */
    public boolean getAuthenticated()
    {
        return AuthenticationUtil.isAuthenticated(this.renderContext.getRequest());           
    }
    
    /**
     * @return true if external authentication is being used to manage the user
     */
    public boolean getExternalAuthentication()
    {
        return AuthenticationUtil.isExternalAuthentication(this.renderContext.getRequest());
    }
    
    /**
     * Gets the link builder helper object
     * 
     * @return the link builder
     */
    public ScriptLinkBuilder getLinkBuilder()
    {
        if (scriptLinkBuilder == null)
        {
            scriptLinkBuilder = new ScriptLinkBuilder(context);         
        }
        return scriptLinkBuilder;
    }    
}
