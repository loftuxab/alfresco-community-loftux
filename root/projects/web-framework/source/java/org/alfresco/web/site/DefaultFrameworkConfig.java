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

import java.util.List;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigElement;

/**
 * @author muzquiano
 */
public class DefaultFrameworkConfig implements FrameworkConfig
{
    private static final String CONFIG_URI = "uri";
    private static final String CONFIG_USECACHE = "usecache";
    private static final String CONFIG_FORMAT = "format";
    private static final String CONFIG_FORMATS = "formats";
    private static final String CONFIG_PAGE_MAPPER = "page-mapper";
    private static final String CONFIG_LINK_BUILDER = "link-builder";
    private static final String CONFIG_REQUEST_CONTEXT = "request-context";
    private static final String CONFIG_CONTEXT = "context";
    private static final String CONFIG_DEFAULT = "default";
    private static final String CONFIG_SERVLET = "servlet";
    private static final String CONFIG_HANDLER = "handler";
    private static final String CONFIG_ERROR_HANDLERS = "error-handlers";
    private static final String CONFIG_RENDERER_TYPE = "renderer-type";
    private static final String CONFIG_PAGE = "page";
    private static final String CONFIG_SYSTEM_PAGES = "system-pages";
    private static final String CONFIG_DISPATCHER = "dispatcher";
    private static final String CONFIG_DEFINITION = "definition";
    private static final String CONFIG_DEFINITIONS = "definitions";
    private static final String CONFIG_FILE_SYSTEM = "file-system";
    private static final String CONFIG_STATE = "state";
    private static final String CONFIG_ELEMENT = "element";
    private static final String CONFIG_ELEMENTS = "elements";
    private static final String CONFIG_INCONTEXT = "incontext";
    private static final String CONFIG_TAGNAME = "tagname";
    private static final String CONFIG_PATH = "path";
    private static final String CONFIG_PREFIX = "prefix";
    private static final String CONFIG_VERSION = "version";
    private static final String CONFIG_TYPE = "type";
    private static final String CONFIG_TYPES = "types";
    private static final String CONFIG_ROOTPATH = "rootpath";
    private static final String CONFIG_MODEL = "model";
    private static final String CONFIG_NAMESPACE = "namespace";
    private static final String CONFIG_LIBRARY = "library";
    private static final String CONFIG_LIBRARIES = "libraries";
    private static final String CONFIG_TAGS = "tags";
    private static final String CONFIG_FACTORY = "factory";
    private static final String CONFIG_FACTORIES = "factories";
    private static final String CONFIG_USER = "user";
    private static final String CONFIG_CONNECTOR = "connector";
    private static final String CONFIG_CONNECTORS = "connectors";
    private static final String CONFIG_CLASS = "class";
    private static final String CONFIG_DESCRIPTION = "description";
    private static final String CONFIG_NAME = "name";
    private static final String CONFIG_AUTHENTICATOR = "authenticator";
    private static final String CONFIG_AUTHENTICATORS = "authenticators";
    private static final String CONFIG_REMOTE = "remote";
    private static final String CONFIG_RENDERER = "renderer";
    private static final String CONFIG_RENDERERS = "renderers";
    private static final String CONFIG_PAGE_INSTANCE_ID = "page-instance-id";
    private static final String CONFIG_FRAMEWORK = "framework";
    private static final String CONFIG_PAGE_TYPES = "page-types";
    private static final String CONFIG_PAGE_TYPE = "page-type";
    private static final String CONFIG_ID = "id";
    private static final String CONFIG_DEFAULTS = "defaults";
    private static final String CONFIG_APPLICATION = "application";
    private static final String CONFIG_ENABLED = "enabled";
    private static final String CONFIG_TIMER = "timer";
    private static final String CONFIG_DEBUG = "debug";
    
    protected Config config = null;
    protected boolean isInitialized = false;
    protected Boolean isTimerEnabled = null;

    
    public DefaultFrameworkConfig(Config config)
    {
        reset(config);
    }

    public void reset(Config config)
    {
        this.config = config;
        this.isInitialized = true;
        this.isTimerEnabled = null;
    }

    public void reset(RequestContext context)
    {
        // not much that can really be done here...
        this.isInitialized = false;
        this.config = null;
        this.isTimerEnabled = null;
    }

    public boolean isInitialized()
    {
        return isInitialized;
    }

    
    //////////////////////////////////////////////////////////////////
    // Helpers
    //////////////////////////////////////////////////////////////////

    private static String getElementChildValueById(List<ConfigElement> elements, String id, String childId)
    {
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            if (configElement.getChildValue(CONFIG_ID).equals(id))
            {
                return configElement.getChildValue(childId);
            }
        }
        return null;
    }
    
    private static String[] getElementIds(List<ConfigElement> elements)
    {
        String[] ids = new String[elements.size()];
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            ids[i] = configElement.getChildValue(CONFIG_ID);
        }
        return ids;
    }
    
    
    //////////////////////////////////////////////////////////////////
    // Formats
    //////////////////////////////////////////////////////////////////

    public String[] getFormatIds()
    {
        List elements = config.getConfigElement(CONFIG_FORMATS).getChildren(CONFIG_FORMAT);
        return getElementIds(elements);
    }

    public String getFormatName(String id)
    {
        return getFormatChildElement(id, CONFIG_NAME);
    }

    public String getFormatDescription(String id)
    {
        return getFormatChildElement(id, CONFIG_DESCRIPTION);
    }
    
    private String getFormatChildElement(String id, String childId)
    {
        List<ConfigElement> elements = config.getConfigElement(CONFIG_FORMATS).getChildren(CONFIG_FORMAT);
        return getElementChildValueById(elements, id, childId);
    }


    //////////////////////////////////////////////////////////////////
    // Page Mapper
    //////////////////////////////////////////////////////////////////

    public String[] getPageMapperIds()
    {
        List elements = config.getConfigElement(CONFIG_PAGE_MAPPER).getChild(
                CONFIG_DEFINITIONS).getChildren(CONFIG_DEFINITION);
        return getElementIds(elements);
    }

    public String getPageMapperName(String id)
    {
        return getPageMapperChildElement(id, CONFIG_NAME);
    }

    public String getPageMapperDescription(String id)
    {
        return getPageMapperChildElement(id, CONFIG_DESCRIPTION);
    }

    public String getPageMapperClass(String id)
    {
        return getPageMapperChildElement(id, CONFIG_CLASS);
    }
    
    private String getPageMapperChildElement(String id, String childId)
    {
        List elements = config.getConfigElement(CONFIG_PAGE_MAPPER).getChild(
                CONFIG_DEFINITIONS).getChildren(CONFIG_DEFINITION);
        return getElementChildValueById(elements, id, childId);
    }

    
    //////////////////////////////////////////////////////////////////
    // Link Builder
    //////////////////////////////////////////////////////////////////

    public String[] getLinkBuilderIds()
    {
        List elements = config.getConfigElement(CONFIG_LINK_BUILDER).getChild(
                CONFIG_DEFINITIONS).getChildren(CONFIG_DEFINITION);
        return getElementIds(elements);
    }

    public String getLinkBuilderName(String id)
    {
        return getLinkBuilderChildElement(id, CONFIG_NAME);
    }

    public String getLinkBuilderDescription(String id)
    {
        return getLinkBuilderChildElement(id, CONFIG_DESCRIPTION);
    }

    public String getLinkBuilderClass(String id)
    {
        return getLinkBuilderChildElement(id, CONFIG_CLASS);
    }
    
    private String getLinkBuilderChildElement(String id, String childId)
    {
        List elements = config.getConfigElement(CONFIG_LINK_BUILDER).getChild(
                CONFIG_DEFINITIONS).getChildren(CONFIG_DEFINITION);
        return getElementChildValueById(elements, id, childId);
    }
    
    

    //////////////////////////////////////////////////////////////////
    // Request Context
    //////////////////////////////////////////////////////////////////

    public String[] getRequestContextIds()
    {
        List elements = config.getConfigElement(CONFIG_REQUEST_CONTEXT).getChild(
                CONFIG_DEFINITIONS).getChildren(CONFIG_DEFINITION);
        return getElementIds(elements);
    }

    public String getRequestContextName(String id)
    {
        return getRequestContextElement(id, CONFIG_NAME);
    }

    public String getRequestContextDescription(String id)
    {
        return getRequestContextElement(id, CONFIG_DESCRIPTION);
    }

    public String getRequestContextClass(String id)
    {
        return getRequestContextElement(id, CONFIG_CLASS);
    }

    public String getRequestContextFactoryClass()
    {
        String defId = getDefaultRequestContextId();
        if (defId != null)
        {
            return getRequestContextClass(defId);
        }
        return null;
    }

    public String getRequestContextSetting(String id, String settingKey)
    {
        return getRequestContextElement(id, settingKey);
    }
    
    private String getRequestContextElement(String id, String childId)
    {
        List elements = config.getConfigElement(CONFIG_REQUEST_CONTEXT).getChild(
                CONFIG_DEFINITIONS).getChildren(CONFIG_DEFINITION);
        return getElementChildValueById(elements, id, childId);
    }

    
    //////////////////////////////////////////////////////////////////
    // Servlet and Dispatcher Information
    //////////////////////////////////////////////////////////////////
    
    public String getDefaultServletUri()
    {
        return config.getConfigElement(CONFIG_SERVLET).getChild(CONFIG_DEFAULT).getChildValue(
                CONFIG_CONTEXT);
    }

    public String getDynamicWebsiteServletUri()
    {
        return config.getConfigElement(CONFIG_SERVLET).getChild(CONFIG_DISPATCHER).getChildValue(
                CONFIG_CONTEXT);
    }


    //////////////////////////////////////////////////////////////////
    // Dispatcher
    //////////////////////////////////////////////////////////////////

    public String[] getDispatcherErrorHandlerIds()
    {
        List elements = config.getConfigElement(CONFIG_DISPATCHER).getChild(
                CONFIG_ERROR_HANDLERS).getChildren(CONFIG_HANDLER);
        return getElementIds(elements);
    }
    
    public String getDispatcherErrorHandlerRenderer(String id)
    {
        return getDispatcherErrorHandlerProperty(id, CONFIG_RENDERER);
    }

    public String getDispatcherErrorHandlerRendererType(String id)
    {
        return getDispatcherErrorHandlerProperty(id, CONFIG_RENDERER_TYPE);
    }
    
    public String getDispatcherErrorHandlerProperty(String id, String propertyId)
    {
        try
        {
            List elements = config.getConfigElement(CONFIG_DISPATCHER).getChild(
                    CONFIG_ERROR_HANDLERS).getChildren(CONFIG_HANDLER);
            for (int i = 0; i < elements.size(); i++)
            {
                ConfigElement configElement = (ConfigElement) elements.get(i);
                if (configElement.getChildValue(CONFIG_ID).equals(id))
                {
                    return configElement.getChildValue(propertyId);
                }
            }
        }
        catch(Exception ex) { }
        return null;
    }
    
    public String[] getDispatcherSystemPageIds()
    {
        List elements = config.getConfigElement(CONFIG_DISPATCHER).getChild(
                CONFIG_SYSTEM_PAGES).getChildren(CONFIG_PAGE);
        return getElementIds(elements);
    }
    
    public String getDispatcherSystemPageRenderer(String id)
    {
        return getDispatcherSystemPageProperty(id, CONFIG_RENDERER);
    }

    public String getDispatcherSystemPageRendererType(String id)
    {
        return getDispatcherSystemPageProperty(id, CONFIG_RENDERER_TYPE);
    }
    
    public String getDispatcherSystemPageProperty(String id, String propertyId)
    {
        try
        {
            List elements = config.getConfigElement(CONFIG_DISPATCHER).getChild(
                    CONFIG_SYSTEM_PAGES).getChildren(CONFIG_PAGE);
            for (int i = 0; i < elements.size(); i++)
            {
                ConfigElement configElement = (ConfigElement) elements.get(i);
                String _id = configElement.getChildValue(CONFIG_ID);
                if (_id.equals(id))
                    return configElement.getChildValue(propertyId);
            }
        }
        catch(Exception ex) { }
        return null;
    }
    
    

    //////////////////////////////////////////////////////////////////
    // File Systems
    //////////////////////////////////////////////////////////////////

    public String[] getFileSystemIds()
    {
        List elements = config.getConfigElement(CONFIG_FILE_SYSTEM).getChild(
                CONFIG_DEFINITIONS).getChildren(CONFIG_DEFINITION);
        return getElementIds(elements);
    }

    public String getFileSystemName(String id)
    {
        return getFileSystemElement(id, CONFIG_NAME);
    }

    public String getFileSystemClass(String id)
    {
        return getFileSystemElement(id, CONFIG_CLASS);
    }

    public String getFileSystemUseCache(String id)
    {
        return getFileSystemElement(id, CONFIG_USECACHE);
    }

    public String getFileSystemRootPath(String id)
    {
        return getFileSystemElement(id, CONFIG_ROOTPATH);
    }

    private String getFileSystemElement(String id, String childId)
    {
        List elements = config.getConfigElement(CONFIG_FILE_SYSTEM).getChild(
                CONFIG_DEFINITIONS).getChildren(CONFIG_DEFINITION);
        return getElementChildValueById(elements, id, childId);
    }


    //////////////////////////////////////////////////////////////////
    // In-Context
    //////////////////////////////////////////////////////////////////

    public boolean isInContextEnabled()
    {
        String v = config.getConfigElement(CONFIG_INCONTEXT).getChildValue(CONFIG_ENABLED);
        return Boolean.parseBoolean(v);
    }

    public String[] getInContextElementIds()
    {
        List elements = config.getConfigElement(CONFIG_INCONTEXT).getChild(
                CONFIG_ELEMENTS).getChildren(CONFIG_ELEMENT);
        return getElementIds(elements);
    }

    public String getInContextElementName(String id)
    {
        return getInContextElement(id, CONFIG_NAME);
    }

    public String getInContextElementType(String id)
    {
        return getInContextElement(id, CONFIG_TYPE);
    }

    public String getInContextElementDefaultEnabled(String id)
    {
        List elements = config.getConfigElement(CONFIG_INCONTEXT).getChild(
                CONFIG_ELEMENTS).getChildren(CONFIG_ELEMENT);
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            if (configElement.getChildValue(CONFIG_ID).equals(id))
            {
                return configElement.getChild(CONFIG_DEFAULTS).getChildValue(CONFIG_ENABLED);
            }
        }
        return null;
    }

    public String getInContextElementDefaultState(String id)
    {
        List elements = config.getConfigElement(CONFIG_INCONTEXT).getChild(
                CONFIG_ELEMENTS).getChildren(CONFIG_ELEMENT);
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            if (configElement.getChildValue(CONFIG_ID).equals(id))
            {
                return configElement.getChild(CONFIG_DEFAULTS).getChildValue(CONFIG_STATE);
            }
        }
        return null;
    }
    
    private String getInContextElement(String id, String childId)
    {
        List elements = config.getConfigElement(CONFIG_INCONTEXT).getChild(
                CONFIG_ELEMENTS).getChildren(CONFIG_ELEMENT);
        return getElementChildValueById(elements, id, childId);
    }
    

    //////////////////////////////////////////////////////////////////
    // Model Types
    //////////////////////////////////////////////////////////////////

    public String[] getModelTypeIds()
    {
        List elements = config.getConfigElement(CONFIG_MODEL).getChild(CONFIG_TYPES).getChildren(
                CONFIG_TYPE);
        return getElementIds(elements);
    }

    public String getModelTypeName(String id)
    {
        return getModelTypeProperty(id, CONFIG_NAME);
    }

    public String getModelTypeDescription(String id)
    {
        return getModelTypeProperty(id, CONFIG_DESCRIPTION);
    }

    public String getModelTypeNamespace(String id)
    {
        return getModelTypeProperty(id, CONFIG_NAMESPACE);
    }
    
    public String getModelTypeTagName(String id)
    {
        return getModelTypeProperty(id, CONFIG_TAGNAME);
    }

    public String getModelTypeClass(String id)
    {
        return getModelTypeProperty(id, CONFIG_CLASS);
    }

    public String getModelTypePath(String id)
    {
        return getModelTypeProperty(id, CONFIG_PATH);    }

    public String getModelTypePrefix(String id)
    {
        return getModelTypeProperty(id, CONFIG_PREFIX);        
    }
    
    public String getModelTypeVersion(String id)
    {
        return getModelTypeProperty(id, CONFIG_VERSION);    }

    public String getModelTypeProperty(String id, String propertyName)
    {
        List elements = config.getConfigElement(CONFIG_MODEL).getChild(CONFIG_TYPES).getChildren(
            CONFIG_TYPE);
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            if (configElement.getChildValue(CONFIG_ID).equals(id))
            {
                return configElement.getChildValue(propertyName);
            }
        }
        return null;        
    }
    
    public String getModelRootPath()
    {
        String rootPath = config.getConfigElement(CONFIG_MODEL).getChildValue(CONFIG_ROOTPATH);
        if (rootPath == null)
        {
            rootPath = "/";
        }
        return rootPath;
    }
    
    

    //////////////////////////////////////////////////////////////////
    // Tag Libraries
    //////////////////////////////////////////////////////////////////

    public String[] getTagLibraryIds()
    {
        List elements = config.getConfigElement(CONFIG_TAGS).getChild(CONFIG_LIBRARIES).getChildren(
                CONFIG_LIBRARY);
        return getElementIds(elements);
    }

    public String getTagLibraryUri(String id)
    {
        List elements = config.getConfigElement(CONFIG_TAGS).getChild(CONFIG_LIBRARIES).getChildren(
                CONFIG_LIBRARY);
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue(CONFIG_ID);
            if (_id.equals(id))
                return configElement.getChildValue(CONFIG_URI);
        }
        return null;
    }

    public String getTagLibraryNamespace(String id)
    {
        List elements = config.getConfigElement(CONFIG_TAGS).getChild(CONFIG_LIBRARIES).getChildren(
                CONFIG_LIBRARY);
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue(CONFIG_ID);
            if (_id.equals(id))
                return configElement.getChildValue(CONFIG_NAMESPACE);
        }
        return null;
    }

    
    
    //////////////////////////////////////////////////////////////////
    // User Factories
    //////////////////////////////////////////////////////////////////

    public String[] getUserFactoryIds()
    {
        List elements = config.getConfigElement(CONFIG_USER).getChild(CONFIG_FACTORIES).getChildren(
                CONFIG_FACTORY);
        return getElementIds(elements);
    }

    public String getUserFactoryName(String id)
    {
        return getUserFactoryProperty(id, CONFIG_NAME);
    }

    public String getUserFactoryDescription(String id)
    {
        return getUserFactoryProperty(id, CONFIG_DESCRIPTION);
    }

    public String getUserFactoryClass(String id)
    {
        return getUserFactoryProperty(id, CONFIG_CLASS);
    }

    public String getUserFactoryProperty(String id, String property)
    {
        List elements = config.getConfigElement(CONFIG_USER).getChild(CONFIG_FACTORIES).getChildren(
                CONFIG_FACTORY);
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            if (configElement.getChildValue(CONFIG_ID).equals(id))
            {
                return configElement.getChildValue(property);
            }
        }
        return null;
    }

 
    //////////////////////////////////////////////////////////////////
    // Remote
    //////////////////////////////////////////////////////////////////

    public String[] getRemoteConnectorIds()
    {
        List elements = config.getConfigElement(CONFIG_REMOTE).getChild(CONFIG_CONNECTORS).getChildren(
                CONFIG_CONNECTOR);
        return getElementIds(elements);
    }

    public String getRemoteConnectorName(String id)
    {
        return getRemoteConnectorProperty(id, CONFIG_NAME);
    }

    public String getRemoteConnectorDescription(String id)
    {
        return getRemoteConnectorProperty(id, CONFIG_DESCRIPTION);
    }

    public String getRemoteConnectorClass(String id)
    {
        return getRemoteConnectorProperty(id, CONFIG_CLASS);
    }

    public String getRemoteConnectorProperty(String id, String property)
    {
        List elements = config.getConfigElement(CONFIG_REMOTE).getChild(CONFIG_CONNECTORS).getChildren(
                CONFIG_CONNECTOR);
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue(CONFIG_ID);
            if (_id.equals(id))
                return configElement.getChildValue(property);
        }
        return null;
    }

    public String[] getRemoteAuthenticatorIds()
    {
        List elements = config.getConfigElement(CONFIG_REMOTE).getChild(
                CONFIG_AUTHENTICATORS).getChildren(CONFIG_AUTHENTICATOR);
        return getElementIds(elements);
    }

    public String getRemoteAuthenticatorName(String id)
    {
        return getRemoteAuthenticatorProperty(id, CONFIG_NAME);
    }

    public String getRemoteAuthenticatorDescription(String id)
    {
        return getRemoteAuthenticatorProperty(id, CONFIG_DESCRIPTION);
    }

    public String getRemoteAuthenticatorClass(String id)
    {
        return getRemoteAuthenticatorProperty(id, CONFIG_CLASS);
    }

    public String getRemoteAuthenticatorProperty(String id, String property)
    {
        List elements = config.getConfigElement(CONFIG_REMOTE).getChild(
                CONFIG_AUTHENTICATORS).getChildren(CONFIG_AUTHENTICATOR);
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            if (configElement.getChildValue(CONFIG_ID).equals(id))
            {
                return configElement.getChildValue(property);
            }
        }
        return null;
    }


    //////////////////////////////////////////////////////////////////
    // Renderers
    //////////////////////////////////////////////////////////////////

    public String[] getRendererIds()
    {
        List elements = config.getConfigElement(CONFIG_RENDERERS).getChildren(CONFIG_RENDERER);
        return getElementIds(elements);
    }

    public String getRendererName(String id)
    {
        return getRendererProperty(id, CONFIG_NAME);

    }

    public String getRendererDescription(String id)
    {
        return getRendererProperty(id, CONFIG_DESCRIPTION);
    }

    public String getRendererClass(String id)
    {
        return getRendererProperty(id, CONFIG_CLASS);
    }

    public String getRendererProperty(String id, String property)
    {
        List elements = config.getConfigElement(CONFIG_RENDERERS).getChildren(CONFIG_RENDERER);
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            if (configElement.getChildValue(CONFIG_ID).equals(id))
            {
                return configElement.getChildValue(property);
            }
        }
        return null;
    }

    
    
    //////////////////////////////////////////////////////////////////
    // Debug
    //////////////////////////////////////////////////////////////////
    
    public boolean getDebugTimerEnabled()
    {
        if (isTimerEnabled == null)
        {   
            String val = config.getConfigElement(CONFIG_DEBUG).getChild(CONFIG_TIMER).getChildValue(CONFIG_ENABLED);
            isTimerEnabled = Boolean.valueOf(val);
        }
        return isTimerEnabled.booleanValue();
    }

    
    
    //////////////////////////////////////////////////////////////////
    // Application Default Settings
    //////////////////////////////////////////////////////////////////
        
    public String getDefaultRegionChrome()
    {
        return config.getConfigElement(CONFIG_APPLICATION).getChild(CONFIG_DEFAULTS).getChildValue("region-chrome");
    }

    public String getDefaultComponentChrome()
    {
        return config.getConfigElement(CONFIG_APPLICATION).getChild(CONFIG_DEFAULTS).getChildValue("component-chrome");
    }

    public String[] getDefaultPageTypeIds()
    {
        List elements = config.getConfigElement(CONFIG_APPLICATION).getChild(
                CONFIG_DEFAULTS).getChild(CONFIG_PAGE_TYPES).getChildren(CONFIG_PAGE_TYPE);
        String[] ids = new String[elements.size()];
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            ids[i] = configElement.getChildValue(CONFIG_ID);
        }
        return ids;
    }

    public String getDefaultPageTypeInstanceId(String id)
    {
        List elements = config.getConfigElement(CONFIG_APPLICATION).getChild(
                CONFIG_PAGE_TYPES).getChildren(CONFIG_PAGE_TYPE);
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue(CONFIG_ID);
            if (_id.equals(id))
                return configElement.getChildValue(CONFIG_PAGE_INSTANCE_ID);
        }
        return null;
    }
    
    
    
    //////////////////////////////////////////////////////////////////
    // Defaults
    //////////////////////////////////////////////////////////////////
    
    public String getDefaultFormatId()
    {
        return getDefaultProperty(CONFIG_FORMAT);
    }
    
    public String getDefaultPageMapperId()
    {
        return getDefaultProperty(CONFIG_PAGE_MAPPER);
    }

    public String getDefaultLinkBuilderId()
    {
        return getDefaultProperty(CONFIG_LINK_BUILDER);
    }
    
    public String getDefaultRequestContextId()
    {
        return getDefaultProperty(CONFIG_REQUEST_CONTEXT);        
    }

    public String getDefaultUserFactoryId()
    {
        return getDefaultProperty("user-factory");        
    }
    
    public String getDefaultTheme(String id)
    {
        return getDefaultProperty("theme");
    }
    
    public String getDefaultProperty(String id)
    {
        ConfigElement element = config.getConfigElement(CONFIG_FRAMEWORK).getChild(CONFIG_DEFAULTS).getChild(id);
        return element.getValue();
    }
    
    
}
