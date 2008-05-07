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
 * A default implementation of the FrameworkConfig interface.
 *
 * This implementation works directly against the Config Service
 *
 * @author muzquiano
 */
public class DefaultFrameworkConfig implements FrameworkConfig
{
    /**
     * Statics of keys that comprise XML elements in the
     * configuration file
     */
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

    /** The config object. */
    protected Config config = null;

    /**
     * Storage variables to prevent re-fetch into XML
     */
    protected boolean isInitialized = false;
    protected Boolean isTimerEnabled = null;
    protected String defaultFormatId = null;
    protected String defaultPageMapperId = null;
    protected String defaultLinkBuilderId = null;
    protected String defaultRequestContextId = null;
    protected String defaultUserFactoryId = null;


    /**
     * Instantiates a new default framework config.
     *
     * @param config the config
     */
    public DefaultFrameworkConfig(Config config)
    {
        this.config = config;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#isInitialized()
     */
    public boolean isInitialized()
    {
        return isInitialized;
    }


    //////////////////////////////////////////////////////////////////
    // Helpers
    //////////////////////////////////////////////////////////////////

    /**
     * Gets the element child value by id.
     *
     * @param elements the elements
     * @param id the id
     * @param childId the child id
     *
     * @return the element child value by id
     */
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

    /**
     * Gets the element ids.
     *
     * @param elements the elements
     *
     * @return the element ids
     */
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

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getFormatIds()
     */
    public String[] getFormatIds()
    {
        List elements = config.getConfigElement(CONFIG_FORMATS).getChildren(CONFIG_FORMAT);
        return getElementIds(elements);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getFormatName(java.lang.String)
     */
    public String getFormatName(String id)
    {
        return getFormatChildElement(id, CONFIG_NAME);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getFormatDescription(java.lang.String)
     */
    public String getFormatDescription(String id)
    {
        return getFormatChildElement(id, CONFIG_DESCRIPTION);
    }

    /**
     * Gets the format child element.
     *
     * @param id the id
     * @param childId the child id
     *
     * @return the format child element
     */
    private String getFormatChildElement(String id, String childId)
    {
        List<ConfigElement> elements = config.getConfigElement(CONFIG_FORMATS).getChildren(CONFIG_FORMAT);
        return getElementChildValueById(elements, id, childId);
    }


    //////////////////////////////////////////////////////////////////
    // Page Mapper
    //////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getPageMapperIds()
     */
    public String[] getPageMapperIds()
    {
        List elements = config.getConfigElement(CONFIG_PAGE_MAPPER).getChild(
                CONFIG_DEFINITIONS).getChildren(CONFIG_DEFINITION);
        return getElementIds(elements);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getPageMapperName(java.lang.String)
     */
    public String getPageMapperName(String id)
    {
        return getPageMapperChildElement(id, CONFIG_NAME);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getPageMapperDescription(java.lang.String)
     */
    public String getPageMapperDescription(String id)
    {
        return getPageMapperChildElement(id, CONFIG_DESCRIPTION);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getPageMapperClass(java.lang.String)
     */
    public String getPageMapperClass(String id)
    {
        return getPageMapperChildElement(id, CONFIG_CLASS);
    }

    /**
     * Gets the page mapper child element.
     *
     * @param id the id
     * @param childId the child id
     *
     * @return the page mapper child element
     */
    private String getPageMapperChildElement(String id, String childId)
    {
        List elements = config.getConfigElement(CONFIG_PAGE_MAPPER).getChild(
                CONFIG_DEFINITIONS).getChildren(CONFIG_DEFINITION);
        return getElementChildValueById(elements, id, childId);
    }


    //////////////////////////////////////////////////////////////////
    // Link Builder
    //////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getLinkBuilderIds()
     */
    public String[] getLinkBuilderIds()
    {
        List elements = config.getConfigElement(CONFIG_LINK_BUILDER).getChild(
                CONFIG_DEFINITIONS).getChildren(CONFIG_DEFINITION);
        return getElementIds(elements);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getLinkBuilderName(java.lang.String)
     */
    public String getLinkBuilderName(String id)
    {
        return getLinkBuilderChildElement(id, CONFIG_NAME);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getLinkBuilderDescription(java.lang.String)
     */
    public String getLinkBuilderDescription(String id)
    {
        return getLinkBuilderChildElement(id, CONFIG_DESCRIPTION);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getLinkBuilderClass(java.lang.String)
     */
    public String getLinkBuilderClass(String id)
    {
        return getLinkBuilderChildElement(id, CONFIG_CLASS);
    }

    /**
     * Gets the link builder child element.
     *
     * @param id the id
     * @param childId the child id
     *
     * @return the link builder child element
     */
    private String getLinkBuilderChildElement(String id, String childId)
    {
        List elements = config.getConfigElement(CONFIG_LINK_BUILDER).getChild(
                CONFIG_DEFINITIONS).getChildren(CONFIG_DEFINITION);
        return getElementChildValueById(elements, id, childId);
    }



    //////////////////////////////////////////////////////////////////
    // Request Context
    //////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getRequestContextIds()
     */
    public String[] getRequestContextIds()
    {
        List elements = config.getConfigElement(CONFIG_REQUEST_CONTEXT).getChild(
                CONFIG_DEFINITIONS).getChildren(CONFIG_DEFINITION);
        return getElementIds(elements);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getRequestContextName(java.lang.String)
     */
    public String getRequestContextName(String id)
    {
        return getRequestContextElement(id, CONFIG_NAME);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getRequestContextDescription(java.lang.String)
     */
    public String getRequestContextDescription(String id)
    {
        return getRequestContextElement(id, CONFIG_DESCRIPTION);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getRequestContextClass(java.lang.String)
     */
    public String getRequestContextClass(String id)
    {
        return getRequestContextElement(id, CONFIG_CLASS);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getRequestContextFactoryClass()
     */
    public String getRequestContextFactoryClass()
    {
        String defId = getDefaultRequestContextId();
        if (defId != null)
        {
            return getRequestContextClass(defId);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getRequestContextSetting(java.lang.String, java.lang.String)
     */
    public String getRequestContextSetting(String id, String settingKey)
    {
        return getRequestContextElement(id, settingKey);
    }

    /**
     * Gets the request context element.
     *
     * @param id the id
     * @param childId the child id
     *
     * @return the request context element
     */
    private String getRequestContextElement(String id, String childId)
    {
        List elements = config.getConfigElement(CONFIG_REQUEST_CONTEXT).getChild(
                CONFIG_DEFINITIONS).getChildren(CONFIG_DEFINITION);
        return getElementChildValueById(elements, id, childId);
    }


    //////////////////////////////////////////////////////////////////
    // Servlet and Dispatcher Information
    //////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getDefaultServletUri()
     */
    public String getDefaultServletUri()
    {
        return config.getConfigElement(CONFIG_SERVLET).getChild(CONFIG_DEFAULT).getChildValue(
                CONFIG_CONTEXT);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getDynamicWebsiteServletUri()
     */
    public String getDynamicWebsiteServletUri()
    {
        return config.getConfigElement(CONFIG_SERVLET).getChild(CONFIG_DISPATCHER).getChildValue(
                CONFIG_CONTEXT);
    }


    //////////////////////////////////////////////////////////////////
    // Dispatcher
    //////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getDispatcherErrorHandlerIds()
     */
    public String[] getDispatcherErrorHandlerIds()
    {
        List elements = config.getConfigElement(CONFIG_DISPATCHER).getChild(
                CONFIG_ERROR_HANDLERS).getChildren(CONFIG_HANDLER);
        return getElementIds(elements);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getDispatcherErrorHandlerRenderer(java.lang.String)
     */
    public String getDispatcherErrorHandlerRenderer(String id)
    {
        return getDispatcherErrorHandlerProperty(id, CONFIG_RENDERER);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getDispatcherErrorHandlerRendererType(java.lang.String)
     */
    public String getDispatcherErrorHandlerRendererType(String id)
    {
        return getDispatcherErrorHandlerProperty(id, CONFIG_RENDERER_TYPE);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getDispatcherErrorHandlerProperty(java.lang.String, java.lang.String)
     */
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

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getDispatcherSystemPageIds()
     */
    public String[] getDispatcherSystemPageIds()
    {
        List elements = config.getConfigElement(CONFIG_DISPATCHER).getChild(
                CONFIG_SYSTEM_PAGES).getChildren(CONFIG_PAGE);
        return getElementIds(elements);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getDispatcherSystemPageRenderer(java.lang.String)
     */
    public String getDispatcherSystemPageRenderer(String id)
    {
        return getDispatcherSystemPageProperty(id, CONFIG_RENDERER);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getDispatcherSystemPageRendererType(java.lang.String)
     */
    public String getDispatcherSystemPageRendererType(String id)
    {
        return getDispatcherSystemPageProperty(id, CONFIG_RENDERER_TYPE);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getDispatcherSystemPageProperty(java.lang.String, java.lang.String)
     */
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

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getFileSystemIds()
     */
    public String[] getFileSystemIds()
    {
        List elements = config.getConfigElement(CONFIG_FILE_SYSTEM).getChild(
                CONFIG_DEFINITIONS).getChildren(CONFIG_DEFINITION);
        return getElementIds(elements);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getFileSystemName(java.lang.String)
     */
    public String getFileSystemName(String id)
    {
        return getFileSystemElement(id, CONFIG_NAME);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getFileSystemClass(java.lang.String)
     */
    public String getFileSystemClass(String id)
    {
        return getFileSystemElement(id, CONFIG_CLASS);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getFileSystemUseCache(java.lang.String)
     */
    public String getFileSystemUseCache(String id)
    {
        return getFileSystemElement(id, CONFIG_USECACHE);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getFileSystemRootPath(java.lang.String)
     */
    public String getFileSystemRootPath(String id)
    {
        return getFileSystemElement(id, CONFIG_ROOTPATH);
    }

    /**
     * Gets the file system element.
     *
     * @param id the id
     * @param childId the child id
     *
     * @return the file system element
     */
    private String getFileSystemElement(String id, String childId)
    {
        List elements = config.getConfigElement(CONFIG_FILE_SYSTEM).getChild(
                CONFIG_DEFINITIONS).getChildren(CONFIG_DEFINITION);
        return getElementChildValueById(elements, id, childId);
    }


    //////////////////////////////////////////////////////////////////
    // In-Context
    //////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#isInContextEnabled()
     */
    public boolean isInContextEnabled()
    {
        String v = config.getConfigElement(CONFIG_INCONTEXT).getChildValue(CONFIG_ENABLED);
        return Boolean.parseBoolean(v);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getInContextElementIds()
     */
    public String[] getInContextElementIds()
    {
        List elements = config.getConfigElement(CONFIG_INCONTEXT).getChild(
                CONFIG_ELEMENTS).getChildren(CONFIG_ELEMENT);
        return getElementIds(elements);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getInContextElementName(java.lang.String)
     */
    public String getInContextElementName(String id)
    {
        return getInContextElement(id, CONFIG_NAME);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getInContextElementType(java.lang.String)
     */
    public String getInContextElementType(String id)
    {
        return getInContextElement(id, CONFIG_TYPE);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getInContextElementDefaultEnabled(java.lang.String)
     */
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

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getInContextElementDefaultState(java.lang.String)
     */
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

    /**
     * Gets the in context element.
     *
     * @param id the id
     * @param childId the child id
     *
     * @return the in context element
     */
    private String getInContextElement(String id, String childId)
    {
        List elements = config.getConfigElement(CONFIG_INCONTEXT).getChild(
                CONFIG_ELEMENTS).getChildren(CONFIG_ELEMENT);
        return getElementChildValueById(elements, id, childId);
    }


    //////////////////////////////////////////////////////////////////
    // Model Types
    //////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getModelTypeIds()
     */
    public String[] getModelTypeIds()
    {
        List elements = config.getConfigElement(CONFIG_MODEL).getChild(CONFIG_TYPES).getChildren(
                CONFIG_TYPE);
        return getElementIds(elements);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getModelTypeName(java.lang.String)
     */
    public String getModelTypeName(String id)
    {
        return getModelTypeProperty(id, CONFIG_NAME);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getModelTypeDescription(java.lang.String)
     */
    public String getModelTypeDescription(String id)
    {
        return getModelTypeProperty(id, CONFIG_DESCRIPTION);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getModelTypeNamespace(java.lang.String)
     */
    public String getModelTypeNamespace(String id)
    {
        return getModelTypeProperty(id, CONFIG_NAMESPACE);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getModelTypeTagName(java.lang.String)
     */
    public String getModelTypeTagName(String id)
    {
        return getModelTypeProperty(id, CONFIG_TAGNAME);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getModelTypeClass(java.lang.String)
     */
    public String getModelTypeClass(String id)
    {
        return getModelTypeProperty(id, CONFIG_CLASS);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getModelTypePath(java.lang.String)
     */
    public String getModelTypePath(String id)
    {
        return getModelTypeProperty(id, CONFIG_PATH);    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getModelTypePrefix(java.lang.String)
     */
    public String getModelTypePrefix(String id)
    {
        return getModelTypeProperty(id, CONFIG_PREFIX);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getModelTypeVersion(java.lang.String)
     */
    public String getModelTypeVersion(String id)
    {
        return getModelTypeProperty(id, CONFIG_VERSION);    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getModelTypeProperty(java.lang.String, java.lang.String)
     */
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

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getModelRootPath()
     */
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

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getTagLibraryIds()
     */
    public String[] getTagLibraryIds()
    {
        List elements = config.getConfigElement(CONFIG_TAGS).getChild(CONFIG_LIBRARIES).getChildren(
                CONFIG_LIBRARY);
        return getElementIds(elements);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getTagLibraryUri(java.lang.String)
     */
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

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getTagLibraryNamespace(java.lang.String)
     */
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

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getUserFactoryIds()
     */
    public String[] getUserFactoryIds()
    {
        List elements = config.getConfigElement(CONFIG_USER).getChild(CONFIG_FACTORIES).getChildren(
                CONFIG_FACTORY);
        return getElementIds(elements);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getUserFactoryName(java.lang.String)
     */
    public String getUserFactoryName(String id)
    {
        return getUserFactoryProperty(id, CONFIG_NAME);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getUserFactoryDescription(java.lang.String)
     */
    public String getUserFactoryDescription(String id)
    {
        return getUserFactoryProperty(id, CONFIG_DESCRIPTION);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getUserFactoryClass(java.lang.String)
     */
    public String getUserFactoryClass(String id)
    {
        return getUserFactoryProperty(id, CONFIG_CLASS);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getUserFactoryProperty(java.lang.String, java.lang.String)
     */
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

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getRemoteConnectorIds()
     */
    public String[] getRemoteConnectorIds()
    {
        List elements = config.getConfigElement(CONFIG_REMOTE).getChild(CONFIG_CONNECTORS).getChildren(
                CONFIG_CONNECTOR);
        return getElementIds(elements);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getRemoteConnectorName(java.lang.String)
     */
    public String getRemoteConnectorName(String id)
    {
        return getRemoteConnectorProperty(id, CONFIG_NAME);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getRemoteConnectorDescription(java.lang.String)
     */
    public String getRemoteConnectorDescription(String id)
    {
        return getRemoteConnectorProperty(id, CONFIG_DESCRIPTION);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getRemoteConnectorClass(java.lang.String)
     */
    public String getRemoteConnectorClass(String id)
    {
        return getRemoteConnectorProperty(id, CONFIG_CLASS);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getRemoteConnectorProperty(java.lang.String, java.lang.String)
     */
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

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getRemoteAuthenticatorIds()
     */
    public String[] getRemoteAuthenticatorIds()
    {
        List elements = config.getConfigElement(CONFIG_REMOTE).getChild(
                CONFIG_AUTHENTICATORS).getChildren(CONFIG_AUTHENTICATOR);
        return getElementIds(elements);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getRemoteAuthenticatorName(java.lang.String)
     */
    public String getRemoteAuthenticatorName(String id)
    {
        return getRemoteAuthenticatorProperty(id, CONFIG_NAME);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getRemoteAuthenticatorDescription(java.lang.String)
     */
    public String getRemoteAuthenticatorDescription(String id)
    {
        return getRemoteAuthenticatorProperty(id, CONFIG_DESCRIPTION);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getRemoteAuthenticatorClass(java.lang.String)
     */
    public String getRemoteAuthenticatorClass(String id)
    {
        return getRemoteAuthenticatorProperty(id, CONFIG_CLASS);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getRemoteAuthenticatorProperty(java.lang.String, java.lang.String)
     */
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

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getRendererIds()
     */
    public String[] getRendererIds()
    {
        List elements = config.getConfigElement(CONFIG_RENDERERS).getChildren(CONFIG_RENDERER);
        return getElementIds(elements);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getRendererName(java.lang.String)
     */
    public String getRendererName(String id)
    {
        return getRendererProperty(id, CONFIG_NAME);

    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getRendererDescription(java.lang.String)
     */
    public String getRendererDescription(String id)
    {
        return getRendererProperty(id, CONFIG_DESCRIPTION);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getRendererClass(java.lang.String)
     */
    public String getRendererClass(String id)
    {
        return getRendererProperty(id, CONFIG_CLASS);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getRendererProperty(java.lang.String, java.lang.String)
     */
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

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getDebugTimerEnabled()
     */
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

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getDefaultRegionChrome()
     */
    public String getDefaultRegionChrome()
    {
        return config.getConfigElement(CONFIG_APPLICATION).getChild(CONFIG_DEFAULTS).getChildValue("region-chrome");
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getDefaultComponentChrome()
     */
    public String getDefaultComponentChrome()
    {
        return config.getConfigElement(CONFIG_APPLICATION).getChild(CONFIG_DEFAULTS).getChildValue("component-chrome");
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getDefaultPageTypeIds()
     */
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

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getDefaultPageTypeInstanceId(java.lang.String)
     */
    public String getDefaultPageTypeInstanceId(String id)
    {
        ConfigElement pageTypes = config.getConfigElement(CONFIG_APPLICATION).getChild(
                CONFIG_PAGE_TYPES);

        if(pageTypes != null)
        {
        	List elements = pageTypes.getChildren(CONFIG_PAGE_TYPE);
	        for (int i = 0; i < elements.size(); i++)
	        {
	            ConfigElement configElement = (ConfigElement) elements.get(i);
	            String _id = configElement.getChildValue(CONFIG_ID);
	            if (_id.equals(id))
	            {
	                return configElement.getChildValue(CONFIG_PAGE_INSTANCE_ID);
				}
	        }
        }

	    return null;
    }



    //////////////////////////////////////////////////////////////////
    // Defaults
    //////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getDefaultFormatId()
     */
    public String getDefaultFormatId()
    {
        if(defaultFormatId == null)
        {
            defaultFormatId = getDefaultProperty(CONFIG_FORMAT);
        }
        return defaultFormatId;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getDefaultPageMapperId()
     */
    public String getDefaultPageMapperId()
    {
        if(defaultPageMapperId == null)
        {
            defaultPageMapperId = getDefaultProperty(CONFIG_PAGE_MAPPER);
        }
        return defaultPageMapperId;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getDefaultLinkBuilderId()
     */
    public String getDefaultLinkBuilderId()
    {
        if(defaultLinkBuilderId == null)
        {
            defaultLinkBuilderId = getDefaultProperty(CONFIG_LINK_BUILDER);
        }
        return defaultLinkBuilderId;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getDefaultRequestContextId()
     */
    public String getDefaultRequestContextId()
    {
        if(defaultRequestContextId == null)
        {
            defaultRequestContextId = getDefaultProperty(CONFIG_REQUEST_CONTEXT);
        }
        return defaultRequestContextId;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getDefaultUserFactoryId()
     */
    public String getDefaultUserFactoryId()
    {
        if(defaultUserFactoryId == null)
        {
            defaultUserFactoryId = getDefaultProperty("user-factory");
        }
        return defaultUserFactoryId;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getDefaultThemeId(java.lang.String)
     */
    public String getDefaultThemeId()
    {
    	return config.getConfigElement(CONFIG_APPLICATION).getChild(CONFIG_DEFAULTS).getChildValue("theme");
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.FrameworkConfig#getDefaultProperty(java.lang.String)
     */
    public String getDefaultProperty(String id)
    {
        ConfigElement element = config.getConfigElement(CONFIG_FRAMEWORK).getChild(CONFIG_DEFAULTS).getChild(id);
        return element.getValue();
    }
}
