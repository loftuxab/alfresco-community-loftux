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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.alfresco.connector.CredentialVault;
import org.alfresco.connector.User;
import org.alfresco.web.config.RemoteConfigElement;
import org.alfresco.web.config.WebFrameworkConfigElement;
import org.alfresco.web.framework.model.Configuration;
import org.alfresco.web.framework.model.Page;
import org.alfresco.web.framework.model.TemplateInstance;
import org.alfresco.web.site.filesystem.IFileSystem;
import org.alfresco.web.site.renderer.RendererContext;
import org.alfresco.web.site.renderer.RendererContextHelper;
import org.apache.commons.logging.Log;

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
    /*
     * Increments every time a request ID is required
     */
    protected static int idCounter = 0;
    
    /**
     * Constructs a new Request Context.  In general, you should not
     * have to construct these by hand.  They are constructed by
     * the framework via a RequestContextFactory.
     */
    protected AbstractRequestContext()
    {
        this.map = new HashMap(16, 1.0f);
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
     * If the site has a configuration XML, then this will return it
     * @return Configuration instance for the site
     */
    public Configuration getSiteConfiguration()
    {
        if(this.siteConfiguration == null)
        {
            // get the default site configuration
            String defaultSiteConfigurationId = getConfig().getDefaultSiteConfigurationId();
            this. siteConfiguration = getModel().getConfiguration(defaultSiteConfigurationId);
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
        
        if(getSiteConfiguration() != null)
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
     * Sets a custom value onto the request context
     * 
     * @param key
     * @param value
     */
    public void setValue(String key, Object value)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("Key is mandatory.");
        }
        map.put(key, value);
    }

    /**
     * Retrieves a custom value from the request context
     * 
     * @param key
     * @return
     */
    public Object getValue(String key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("Key is mandatory.");
        }
        return map.get(key);
    }

    /**
     * Removes a custom value from the request context
     * 
     * @param key
     */
    public void removeValue(String key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("Key is mandatory.");
        }
        map.remove(key);
    }
    
    /**
     * Returns true if a custom value exists in the request context for the given key
     * 
     * @param key
     * 
     * @return true if a custom value exists in the request context for the given key
     */
    public boolean hasValue(String key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("Key is mandatory.");
        }
        return map.containsKey(key);
    }

    /**
     * Returns an Iterator over the keys of the custom key/value pairs
     * stored on this RequestContext instance
     * 
     * @return An iterator of String keys
     */
    public Iterator keys()
    {
        return map.keySet().iterator();
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
        return SiteUtil.getRootPage(this, getSiteConfiguration());
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
    public void setCurrentObject(Content content)
    {
        this.currentObject = content;
    }
    
    /**
     * Returns the current object
     */
    public Content getCurrentObject()
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
     * Returns the File System implementation which points to the
     * "root" of the current web application.  This allows the framework
     * to inspect the contents of the current web application and
     * provision them as useful elements to the end user.
     *  
     * @return The file system implementation
     */
    public IFileSystem getFileSystem()
    {
        return this.fileSystem;
    }

    /**
     * Sets the File System implementation to serve as the "root" of
     * the current web application.
     * 
     * @param fileSystem
     */
    public void setFileSystem(IFileSystem fileSystem)
    {
        this.fileSystem = fileSystem;
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
    
    public void setModel(Model model)
    {
        this.model = model;
    }
    
    /**
     * Sets the model
     */

    /**
     * Returns the configuration for the framework.
     * 
     * @return
     */    
    public WebFrameworkConfigElement getConfig()
    {
    	return FrameworkHelper.getConfig();
    }

    /**
     * Returns the configuration for the remote.
     * 
     * @return
     */
    public RemoteConfigElement getRemoteConfig()
    {
    	return FrameworkHelper.getRemoteConfig();
    }

    /**
     * Returns the logger for the framework
     * 
     * @return
     */
    public Log getLogger()
    { 
        return FrameworkHelper.getLogger();
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
     * Returns the user id
     * 
     * @return
     */
    public String getUserId()
    {
    	String userId = null;
    	if(getUser() != null)
    	{
    		userId = getUser().getId();
    	}
    	return userId;
    }

    /**
     * Returns the credential vault for the current user
     * 
     * @return
     */
    public CredentialVault getCredentialVault()
    {
        return FrameworkHelper.getCredentialVault(this, this.getUserId());
    }

    /**
     * Returns the render context for the currently rendering
     * object.  The render context is scoped to the currently
     * rendering object.
     * 
     * @return The Render Data instance
     */
    public RendererContext getRenderContext()
    {
        return RendererContextHelper.current(this);
    }
    
    /**
     * Returns the debug mode of the current request
     * If not in debug mode, this will return null
     */
    public String getDebugMode()
    {
        return null;
    }
    
    /**
     * Returns the current theme id
     */
    public String getThemeId()
    {
        return themeId;
    }
    
    /**
     * Sets the current theme id
     */
    public void setThemeId(String themeId)
    {
        this.themeId = themeId;
    }
    
    @Override
    public String toString()
    {
        return map.toString();
    }


    protected Map map;
    protected Page currentPage;
    protected TemplateInstance currentTemplate;
    protected Content currentObject;
    protected String currentFormatId;
    protected IFileSystem fileSystem;
    protected String storeId;
    protected User user;
    protected String id;
    protected String themeId;
    protected Configuration siteConfiguration;
    protected Model model;

    // constants
    
    public static final String VALUE_HEAD_TAGS = "headTags";    
    public static final String DEBUG_MODE_VALUE_COMPONENTS = "components";
}
