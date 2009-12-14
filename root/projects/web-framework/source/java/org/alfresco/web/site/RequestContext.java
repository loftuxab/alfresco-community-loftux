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
import org.alfresco.web.framework.resource.ResourceContent;

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
public interface RequestContext extends Serializable
{
    public static final String VALUE_HEAD_TAGS = "headTags";
    public static final String VALUE_CREDENTIAL_VAULT = "credential_vault";
    public static final String VALUE_IDENTITY_VAULT = "identity_vault";
    
    public static final String DEBUG_MODE_VALUE_COMPONENTS = "components";
    
    /**
     * Each request context instance is stamped with a unique id
     * @return The id of the request context
     */
    public String getId();
    
    /**
     * If the site has a configuration XML, then this will return it
     * @return Configuration instance for the site
     */
    public Configuration getSiteConfiguration();
    
    /**
     * Returns the title of the web site.  This is drawn from the
     * site configuration XML if available.
     * 
     * @return
     */
    public String getWebsiteTitle();

    /**
     * Returns the title of the current page.  This is drawn from
     * the current page instance, if set.
     * 
     * @return The title of the current page.
     */
    public String getPageTitle();

    /**
     * Sets a custom attribute onto the request context
     * 
     * @param key
     * @param value
     */
    public void setValue(String key, Serializable value);

    /**
     * Retrieves a custom value from the request context
     * 
     * @param key
     * @return
     */
    public Serializable getValue(String key);

    /**
     * Removes a custom value from the request context
     * 
     * @param key
     */
    public void removeValue(String key);
    
    /**
     * Returns true if a custom value exists in the request context
     * 
     * @param key
     * @return true if a custom value exists in the request context
     */
    public boolean hasValue(String key);
    
    /**
     * Returns the underlying map of the custom key/values pairs
     * stored on this RequestContext instance. Use with caution!
     * 
     * @return the underlying map of custom key/value pairs.
     */
    public Map<String, Serializable> getValuesMap();
    
    /**
     * Retrieves a parameter from the request context
     * 
     * @param key
     * @return
     */
    public Serializable getParameter(String key);

    /**
     * Returns true if a parameter exists in the request context
     * 
     * @param key
     * @return true if a custom value exists in the request context
     */
    public boolean hasParameter(String key);
    
    /**
     * Returns a map of parameters
     * 
     * @return the underlying map of parameters
     */
    public Map<String, Serializable> getParameters();
    
    /**
     * Sets the currently executing uri.
     */
    public void setUri(String uri);
    
    /**
     * @return the currently executing uri.
     */
    public String getUri();
    
    /**
     * If a page instance is currently executing, it can be retrieved
     * from the request context.
     * 
     * @return The current page
     */
    public Page getPage();

    /**
     * Sets the currently executing page.
     * 
     * @param page
     */
    public void setPage(Page page);
    
    /**
     * Returns the id of the currently executing page.  If a currently
     * executing page is not set, this will return null.
     * 
     * @return The current page id (or null)
     */
    public String getPageId();

    /**
     * Returns the LinkBuilder to be used for the currently executing
     * page.  In general, you will have one link builder per site but
     * this hook allows for the possibility of multiple.
     * 
     * @return
     */
    public LinkBuilder getLinkBuilder();
    
    /**
     * Returns the root page for a site.  A root page is designated
     * if it either has a root-page property in its XML or the site
     * configuration has specifically designated a root page.
     * 
     * @return The root page of the application
     */
    public Page getRootPage();

    /**
     * Returns the current executing template.
     * 
     * @return
     */
    public TemplateInstance getTemplate();

    /**
     * Sets the current executing template.
     * 
     * @return
     */
    public void setTemplate(TemplateInstance currentTemplate);
    
    /**
     * Returns the id of the currently executing template.
     * If no template is set, this will return null.
     * 
     * @return The current template id or null
     */
    public String getTemplateId();
    
    /**
     * Returns the id of the current object
     * If no object has been set, then the id will be null.
     * 
     * @return The id of the current object
     */
    public String getCurrentObjectId();

    /**
     * Returns the current object
     * If no object has been set, then null is returned
     * 
     * @return The current object
     */
    public ResourceContent getCurrentObject();
    
    /**
     * Sets the current object
     * 
     * @param object
     */
    public void setCurrentObject(ResourceContent object);

    /**
     * Returns the current format id
     * 
     * @return
     */
    public String getFormatId();

    /**
     * Sets the current format id
     * 
     * @param formatId
     */
    public void setFormatId(String formatId);

    /**
     * Returns the model.  The model allows object model manipulation
     * and persistence.
     * 
     * @return
     */
    public Model getModel();

    /**
     * Sets the current model
     * 
     * @param model
     */
    public void setModel(Model model);

    /**
     * Sets the current user for this request
     * @param user
     */
    public void setUser(User user);

    /**
     * Returns the current user
     * 
     * @return
     */
    public User getUser();

    /**
     * Returns the current user id
     * 
     * @return
     */
    public String getUserId();
    
    /**
     * Returns the credential vault
     * 
     * @return
     */
    public CredentialVault getCredentialVault();
        
    /**
     * Returns the current theme id
     */
    public String getThemeId();
    
    /**
     * Sets the current theme id
     */
    public void setThemeId(String themeId);
    
    /**
     * @return the current Theme object or null if not set
     */
    public Theme getTheme();

    /**
     * Returns the components that were bound to this and any of its parent context
     * during the rendering.  This is useful to determine what other components
     * are configured on the current page.
     * 
     * If no rendering components are set, null will be returned
     * 
     * @return  An array of Component objects
     */
    public Component[] getRenderingComponents();
    
    /**
     * Indicates that the given component is being rendered as part of
     * the rendering execution for this and any parent rendering context.
     *  
     * @param component The component that is being rendered
     */
    public void setRenderingComponent(Component component);
    
    /**
     * Returns the content type of the incoming request
     * 
     * @return content type
     */
    public String getRequestContentType();
    
    /**
     * Returns the method of the incoming request
     * 
     * @return request method
     */
    public String getRequestMethod();
    
    /**
     * Returns the body of the incoming POST content
     * This is applicable for multipart form requests
     * 
     * @return content
     */
    public org.springframework.extensions.surf.util.Content getRequestContent();
    
    /**
     * Returns the HTTP Servlet Request bound to this request
     * 
     * @return
     */
    public HttpServletRequest getRequest();
    
    /**
     * Release any resources held by the request context
     * 
     * As part of the contract for a RequestContext object, this will only ever be called once
     * and no further method calls will be made to the RequestContext object.
     */
    public void release();
}
