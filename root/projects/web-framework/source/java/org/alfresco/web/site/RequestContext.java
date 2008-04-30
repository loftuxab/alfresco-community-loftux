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
import java.util.Map;

import org.alfresco.connector.CredentialsVault;
import org.alfresco.connector.IdentityVault;
import org.alfresco.web.site.filesystem.IFileSystem;
import org.alfresco.web.site.model.Configuration;
import org.alfresco.web.site.model.Page;
import org.alfresco.web.site.model.TemplateInstance;
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
public abstract class RequestContext
{
    /*
     * Increments every time a request context is manufactured
     */
    protected static int requestCount = 0;
    
    /**
     * Constructs a new Request Context.  In general, you should not
     * have to construct these by hand.  They are constructed by
     * the framework via a RequestContextFactory.
     */
    protected RequestContext()
    {
        this.map = new HashMap();
        
        synchronized(RequestContext.class)
        {
            requestCount++;
            this.id = "" + requestCount;            
        }
    }

    /**
     * Each request context instance is stamped with a unique id
     * @return The id of the request context
     */
    public String getId()
    {
        return this.id;
    }
    
    /**
     * If the site has a configuration XML, then this will return it
     * @return Configuration instance for the site
     */
    public Configuration getSiteConfiguration()
    {
        return ModelUtil.getSiteConfiguration(this);
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
            title = getSiteConfiguration().getName();
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
        
        if (getCurrentPage() != null)
        {
            title = getCurrentPage().getName();
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
        if (key != null && value != null)
        {
            map.put(key, value);
        }
    }

    /**
     * Retrieves a custom value from the request context
     * 
     * @param key
     * @return
     */
    public Object getValue(String key)
    {
        return map.get(key);
    }

    /**
     * Removes a custom value from the request context
     * 
     * @param key
     */
    public void removeValue(String key)
    {
        if (map.containsKey(key))
        {
            map.remove(key);
        }
    }


    /**
     * If a page instance is currently executing, it can be retrieved
     * from the request context.
     * 
     * @return The current page
     */
    public Page getCurrentPage()
    {
        return this.currentPage;
    }

    /**
     * Sets the currently executing page.
     * 
     * @param page
     */
    public void setCurrentPage(Page page)
    {
        this.currentPage = page;
    }
    
    /**
     * Returns the id of the currently executing page.  If a currently
     * executing page is not set, this will return null.
     * 
     * @return The current page id (or null)
     */
    public String getCurrentPageId()
    {
        if(getCurrentPage() != null)
        {
            return getCurrentPage().getId();
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
        return ModelUtil.getRootPage(this);
    }

    /**
     * Returns the current executing template.
     * 
     * @return
     */
    public TemplateInstance getCurrentTemplate()
    {
        if(getCurrentPage() != null)
        {
            return getCurrentPage().getTemplate(this);
        }
        return null;
    }
    
    /**
     * Returns the id of the currently executing template.
     * If no template is set, this will return null.
     * 
     * @return The current template id or null
     */
    public String getCurrentTemplateId()
    {
        if(getCurrentTemplate() != null)
        {
            return getCurrentTemplate().getId();
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
        return this.currentObjectId;
    }

    /**
     * Sets the id of the current object
     * 
     * @param objectId
     */
    public void setCurrentObjectId(String objectId)
    {
        this.currentObjectId = objectId;
    }

    /**
     * Returns the current format id
     * 
     * @return
     */
    public String getCurrentFormatId()
    {
        return this.currentFormatId;
    }

    /**
     * Sets the current format id
     * 
     * @param formatId
     */
    public void setCurrentFormatId(String formatId)
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
     * Returns the current AVM store ID.
     * 
     * This is an Alfresco specific property which can either be set
     * by hand or picked up automatically from the virtual server.
     * 
     * This property is inspected downstream by the AVM remote store.
     * 
     * @return
     */
    public String getStoreId()
    {
        if (this.storeId == null)
        {
            this.storeId = "";
        }
        return this.storeId;
    }

    /**
     * Sets the current AVM store ID.
     * 
     * @param storeId
     */
    public void setStoreId(String storeId)
    {
        this.storeId = storeId;
    }


    /**
     * Returns the model.  The model allows object model manipulation
     * and persistence.  Models are intended to be pluggable so that
     * multiple implementations could be supported.
     * 
     * @return
     */
    public IModel getModel()
    {
        return Framework.getModel();
    }

    /**
     * Returns the configuration for the framework.
     * 
     * @return
     */
    public FrameworkConfig getConfig()
    {
        return Framework.getConfig();
    }

    /**
     * Returns the logger for the framework
     * 
     * @return
     */
    public Log getLogger()
    { 
        return Framework.getLogger();
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
     * Returns the credential vault for the current user
     * 
     * @return
     */
    public CredentialsVault getUserCredentialVault()
    {
        return (CredentialsVault) getValue(VALUE_CREDENTIAL_VAULT);
    }

    /**
     * Returns the identity vault for the current user
     * 
     * @return
     */
    public IdentityVault getUserIdentityVault()
    {
        return (IdentityVault) getValue(VALUE_IDENTITY_VAULT);
    }

    /**
     * Returns the render data context for the currently rendering
     * object.  The render data context is scoped to the currently
     * rendering object.
     * 
     * @return The Render Data instance
     */
    public RenderData getRenderData()
    {
        return RenderDataHelper.current(this);
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
    
    // variables
    
    protected Map map;
    protected Page currentPage;
    protected String currentObjectId;
    protected String currentFormatId;
    protected IFileSystem fileSystem;
    protected String storeId;
    protected User user;
    protected String id;
    protected String themeId;

    // constants
    
    public static final String VALUE_HEAD_TAGS = "headTags";
    public static final String VALUE_CREDENTIAL_VAULT = "credential_vault";
    public static final String VALUE_IDENTITY_VAULT = "identity_vault";
    
    public static final String DEBUG_MODE_VALUE_COMPONENTS = "components";
    

}
