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

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.connector.CredentialVault;
import org.alfresco.connector.User;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.surf.util.InputStreamContent;
import org.alfresco.web.framework.model.Component;
import org.alfresco.web.framework.model.Configuration;
import org.alfresco.web.framework.model.Page;
import org.alfresco.web.framework.model.TemplateInstance;
import org.alfresco.web.framework.model.Theme;
import org.alfresco.web.framework.resource.ResourceContent;
import org.alfresco.web.scripts.WebScriptException;

/**
 * Represents the context of the original request to the web page.
 * 
 * This context object is manufactured at the top of the request chain
 * and is then made available to all templates, regions, components,
 * chromes and anything else downstream.
 * 
 * This object provides a single point of reference for information
 * about the user, the current rendering page and other context.  It
 * provides this information so that individual rendering pieces do
 * not need to load it themselves.
 * 
 * @author muzquiano
 */
public abstract class AbstractRequestContext implements RequestContext
{
    public static final String VALUE_HEAD_TAGS = "headTags";

    protected static final String SESSION_CURRENT_THEME    = "alfTheme";
    protected static final String SESSION_CURRENT_THEME_ID = "alfThemeId";
        
    /*
     * Increments every time a request ID is required (debug)
     */
    protected static int idCounter = 0;
    
    protected Map<String, Serializable> valuesMap;
    protected Map<String, Serializable> parametersMap;
    
    protected Page rootPage;
    protected Configuration siteConfiguration;
    
    protected Page currentPage;
    protected TemplateInstance currentTemplate;
    protected ResourceContent currentObject;
    protected String currentFormatId;
    protected String storeId;
    protected User user;
    protected String id;
    protected String uri;
    protected Model model;
    
    protected Map<String, Component> components = null;
    
    /** The request encapsulated by this context object */
    protected HttpServletRequest request;
    
    /** The content in the request body **/
    private Content content;        
    
    /**
     * Constructs a new Request Context.  In general, you should not
     * have to construct these by hand.  They are constructed by
     * the framework via a RequestContextFactory.
     */
    protected AbstractRequestContext(HttpServletRequest request)
    {
        // initialize maps
        this.valuesMap = new HashMap<String, Serializable>(16, 1.0f);
        this.parametersMap = new HashMap<String, Serializable>(16, 1.0f);
        this.components = new LinkedHashMap<String, Component>(16, 1.0f);
        
        // set request
        this.request = request;
    }
    
    /**
     * Each request context instance is stamped with a unique id - generally only used for debugging
     * 
     * @return The id of the request context
     */
    public String getId()
    {
        synchronized (AbstractRequestContext.class)
        {
            if (this.id == null)
            {
                idCounter++;
                this.id = Integer.toString(idCounter);
            }
        }
        return this.id;
    }
    
    /**
     * Returns the site's configuration object
     * 
     * @return Configuration instance for the site
     */
    public Configuration getSiteConfiguration()
    {
        if(this.siteConfiguration == null)
        {
            this.siteConfiguration = SiteUtil.getSiteConfiguration(this);
        }
        
        return this.siteConfiguration;
    }
    
    /**
     * Returns the title of the web site.  This is drawn from the
     * site configuration XML if available.
     * 
     * @return
     */
    public String getWebsiteTitle()
    {
        String title = "Web Application";
        
        if (getSiteConfiguration() != null)
        {
            title = getSiteConfiguration().getTitle();
        }
        
        return title;
    }

    /**
     * Returns the title of the current page.  This is drawn from
     * the current page instance, if set.
     * 
     * @return The title of the current page.
     */
    public String getPageTitle()
    {
        String title = "Default Page";
        
        if (getPage() != null)
        {
            title = getPage().getTitle();
        }
        
        return title;
    }

    /**
     * @return the currently executing uri.
     */
    public String getUri()
    {
        return this.uri;
    }

    /**
     * Sets the currently executing uri.
     */
    public void setUri(String uri)
    {
        this.uri = uri;
    }

    /**
     * If a page instance is currently executing, it can be retrieved
     * from the request context.
     * 
     * @return The current page
     */
    public Page getPage()
    {
        return this.currentPage;
    }

    /**
     * Sets the currently executing page.
     * 
     * @param page
     */
    public void setPage(Page page)
    {
        this.currentPage = page;
        // clear cached variable
        this.currentTemplate = null;
    }
    
    /**
     * Returns the id of the currently executing page.  If a currently
     * executing page is not set, this will return null.
     * 
     * @return The current page id (or null)
     */
    public String getPageId()
    {
        if (getPage() != null)
        {
            return getPage().getId();
        }
        return null;
    }

    /**
     * Returns the LinkBuilder to be used for the currently executing
     * page.  In general, you will have one link builder per site but
     * this hook allows for the possibility of multiple.
     * 
     * @return
     */
    public LinkBuilder getLinkBuilder()
    {
        return LinkBuilderFactory.newInstance(this);
    }
    
    /**
     * Returns the root page for a site.  A root page is designated
     * if it either has a root-page property in its XML or the site
     * configuration has specifically designated a root page.
     * 
     * @return The root page of the application
     */
    public Page getRootPage()
    {
        if(this.rootPage == null)
        {
            this.rootPage = SiteUtil.getRootPage(this);
        }
        
        return this.rootPage;
    }

    /**
     * Returns the current executing template.
     * 
     * @return
     */
    public TemplateInstance getTemplate()
    {
        if (this.currentTemplate == null)
        {
            if (getPage() != null)
            {
                this.currentTemplate = getPage().getTemplate(this);
            }
        }
        return this.currentTemplate;
    }
    
    /**
     * Sets the current executing template.
     * 
     * @return
     */
    public void setTemplate(TemplateInstance currentTemplate)
    {
        this.currentTemplate = currentTemplate;        
    }
    
    
    /**
     * Returns the id of the currently executing template.
     * If no template is set, this will return null.
     * 
     * @return The current template id or null
     */
    public String getTemplateId()
    {
        if (getTemplate() != null)
        {
            return getTemplate().getId();
        }
        return null;
    }
    
    /**
     * Returns the id of the current object
     * If no object has been set, then the id will be null.
     * 
     * @return The id of the current object
     */
    public String getCurrentObjectId()
    {
        String id = null;        
        if (getCurrentObject() != null)
        {
            id = getCurrentObject().getId();
        }
        return id;
    }

    /**
     * Sets the current object
     * 
     * @param content
     */
    public void setCurrentObject(ResourceContent content)
    {
        this.currentObject = content;
    }
    
    /**
     * Returns the current object
     */
    public ResourceContent getCurrentObject()
    {
        return this.currentObject;
    }

    /**
     * Returns the current format id
     * 
     * @return
     */
    public String getFormatId()
    {
        return this.currentFormatId;
    }

    /**
     * Sets the current format id
     * 
     * @param formatId
     */
    public void setFormatId(String formatId)
    {
        this.currentFormatId = formatId;
    }

    /**
     * Returns the model.  The model allows object model manipulation
     * and persistence.  Models are intended to be pluggable so that
     * multiple implementations could be supported.
     * 
     * @return
     */
    public Model getModel()
    {
        return this.model;
    }
    
    /**
     * Sets the model
     */
    public void setModel(Model model)
    {
        this.model = model;
    }

    /**
     * Sets the current user for this request
     * @param user
     */
    public void setUser(User user)
    {
        this.user = user;
    }

    /**
     * Returns the current user
     * 
     * @return
     */
    public User getUser()
    {
        return user;
    }
    
    /**
     * @return the user id
     */
    public String getUserId()
    {
    	return (getUser() != null ? getUser().getId() : null);
    }

    /**
     * @return the credential vault for the current user
     */
    public CredentialVault getCredentialVault()
    {
        return FrameworkHelper.getCredentialVault(this, this.getUserId());
    }
            
    /* (non-Javadoc)
     * @see org.alfresco.web.site.RequestContext#setValue(java.lang.String, java.io.Serializable)
     */
    public void setValue(String key, Serializable value)
    {
        this.valuesMap.put(key, value);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.RequestContext#getValue(java.lang.String)
     */
    public Serializable getValue(String key)
    {
        return this.valuesMap.get(key);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.RequestContext#removeValue(java.lang.String)
     */
    public void removeValue(String key)
    {
        this.valuesMap.remove(key);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.RequestContext#hasValue(java.lang.String)
     */
    public boolean hasValue(String key)
    {
        return (this.valuesMap.get(key) != null);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.RequestContext#getValuesMap()
     */
    public Map<String, Serializable> getValuesMap()
    {
        return this.valuesMap;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.RequestContext#getParameter(java.lang.String)
     */
    public Serializable getParameter(String key)
    {
        return this.parametersMap.get(key);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.RequestContext#hasParameter(java.lang.String)
     */
    public boolean hasParameter(String key)
    {
        return (this.parametersMap.get(key) != null);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.RequestContext#getParameters()
     */
    public Map<String, Serializable> getParameters()
    {
        return this.parametersMap;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.RequestContext#getRenderingComponents()
     */
    public Component[] getRenderingComponents()
    {
        if (this.components.size() == 0)
        {
            return null;
        }
        else
        {
            return this.components.values().toArray(new Component[this.components.size()]);
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.RequestContext#setRenderingComponent(org.alfresco.web.framework.model.Component)
     */
    public void setRenderingComponent(Component component)
    {
        this.components.put(component.getId(), component);        
    }
    
    /**
     * Returns the HTTP Servlet Request bound to this request
     * 
     * @return
     */
    public HttpServletRequest getRequest()
    {
        return request;
    }
    
    /**
     * Returns the current Theme Id for the current user
     */
    public String getThemeId()
    {
        return (String)request.getSession().getAttribute(SESSION_CURRENT_THEME_ID);
    }
    
    /**
     * Sets the current theme id
     */
    public void setThemeId(String themeId)
    {
        if (themeId != null)
        {
            request.getSession().setAttribute(SESSION_CURRENT_THEME_ID, themeId);
        }
    }
    
    /**
     * Gets the current Theme object, or null if not set
     */
    public Theme getTheme()
    {
        Theme theme = (Theme)getValue(SESSION_CURRENT_THEME);
        if (theme == null)
        {
            String themeId = getThemeId();
            if (themeId != null)
            {
                theme = getModel().getTheme(themeId);
                if (theme != null)
                {
                    setValue(SESSION_CURRENT_THEME, theme);
                }
            }
        }
        return theme;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.RequestContext#getRequestContentType()
     */
    public String getRequestContentType()
    {
        String contentType = this.getRequest().getContentType();
        if (contentType != null && contentType.startsWith("multipart/form-data"))
        {
            contentType = "multipart/form-data";
        }
        return contentType;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.RequestContext#getRequestMethod()
     */
    public String getRequestMethod()
    {
        return this.getRequest().getMethod();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.RequestContext#getRequestContent()
     */
    public synchronized org.springframework.extensions.surf.util.Content getRequestContent()
    {
        // ensure we only try to read the content once - as this method may be called several times
        // but the underlying inputstream itself can only be processed a single time
        if (content == null)
        {
            try
            {
                content = new InputStreamContent(getRequest().getInputStream(), getRequest().getContentType(), getRequest().getCharacterEncoding());
            }
            catch (IOException e)
            {
                throw new WebScriptException("Failed to retrieve request content", e);
            }
        }
        return content;
    }
    

    @Override
    public String toString()
    {
        return "RequestContext-" + getId();
    }    
}
