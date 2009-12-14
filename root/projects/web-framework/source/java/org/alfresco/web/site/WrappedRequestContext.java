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
package org.alfresco.web.site;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.connector.CredentialVault;
import org.alfresco.connector.User;
import org.alfresco.web.framework.model.Component;
import org.alfresco.web.framework.model.Configuration;
import org.alfresco.web.framework.model.Page;
import org.alfresco.web.framework.model.TemplateInstance;
import org.alfresco.web.framework.model.Theme;

/**
 * @author muzquiano
 */
public class WrappedRequestContext implements RequestContext
{
    final private RequestContext context;
    
    public WrappedRequestContext(RequestContext context)
    {
        this.context = context;
    }
    
    final public RequestContext getOriginalContext()
    {
        return this.context;
    }

    public String getId()
    {
        return this.context.getId();
    }
    
    public Configuration getSiteConfiguration()
    {
        return this.context.getSiteConfiguration();
    }
    
    public String getWebsiteTitle()
    {
        return this.context.getWebsiteTitle();
    }

    public String getPageTitle()
    {
        return this.context.getPageTitle();
    }

    public String getUri()
    {
        return this.context.getUri();
    }

    public void setUri(String uri)
    {
        this.context.setUri(uri);
    }

    public Page getPage()
    {
        return this.context.getPage();
    }

    public void setPage(Page page)
    {
        this.context.setPage(page);
    }
    
    public String getPageId()
    {
        return this.context.getPageId();
    }

    public LinkBuilder getLinkBuilder()
    {
        return this.context.getLinkBuilder();
    }
    
    public Page getRootPage()
    {
        return this.context.getRootPage();
    }

    public TemplateInstance getTemplate()
    {
        return this.context.getTemplate();
    }
    
    public void setTemplate(TemplateInstance currentTemplate)
    {
        this.context.setTemplate(currentTemplate);
    }
    
    public String getTemplateId()
    {
        return this.context.getTemplateId();
    }
    
    public String getCurrentObjectId()
    {
        return this.context.getCurrentObjectId();
    }

    public void setCurrentObject(org.alfresco.web.framework.resource.ResourceContent content)
    {
        this.context.setCurrentObject(content);
    }
    
    public org.alfresco.web.framework.resource.ResourceContent getCurrentObject()
    {
        return this.context.getCurrentObject();
    }

    public String getFormatId()
    {
        return this.context.getFormatId();
    }

    public void setFormatId(String formatId)
    {
        this.context.setFormatId(formatId);
    }

    public Model getModel()
    {
        return this.context.getModel();
    }
    
    public void setModel(Model model)
    {
        this.context.setModel(model);
    }

    public void setUser(User user)
    {
        this.context.setUser(user);
    }

    public User getUser()
    {
        return this.context.getUser();
    }
    
    public String getUserId()
    {
        return this.context.getUserId();
    }

    public CredentialVault getCredentialVault()
    {
        return this.context.getCredentialVault();
    }
    
    public String getThemeId()
    {
        return this.context.getThemeId();
    }
    
    public void setThemeId(String themeId)
    {
        this.context.setThemeId(themeId);
    }
    
    public Theme getTheme()
    {
        return this.context.getTheme();
    }
    
    public String getRequestContentType()
    {
        return this.context.getRequestContentType();
    }
    
    public String getRequestMethod()
    {
        return this.context.getRequestMethod();
    }
    
    public org.springframework.extensions.surf.util.Content getRequestContent()
    {
        return this.context.getRequestContent();
    }   
    
    public void setValue(String key, Serializable value)
    {
        this.context.setValue(key, value);
    }

    public Serializable getValue(String key)
    {
        return this.context.getValue(key);
    }

    public void removeValue(String key)
    {
        this.context.removeValue(key);
    }
    
    public boolean hasValue(String key)
    {
        return this.context.hasValue(key);
    }
    
    public Map<String, Serializable> getValuesMap()
    {
        return this.context.getValuesMap();
    }
    
    public Serializable getParameter(String key)
    {
        return this.context.getParameter(key);
    }

    public boolean hasParameter(String key)
    {
        return this.context.hasParameter(key);
    }
    
    public Map<String, Serializable> getParameters()
    {
        return this.context.getParameters();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.RequestContext#getRenderingComponents()
     */
    public Component[] getRenderingComponents()
    {
        return this.context.getRenderingComponents();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.RequestContext#setRenderingComponent(org.alfresco.web.framework.model.Component)
     */
    public void setRenderingComponent(Component component)
    {
        this.context.setRenderingComponent(component);        
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.RequestContext#getRequest()
     */
    public HttpServletRequest getRequest()
    {
        return this.context.getRequest();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.RequestContext#release()
     */
    public void release()
    {
    }
    
    @Override
    public String toString()
    {
        return "Wrapped: " + this.context.toString();
    }    
}
