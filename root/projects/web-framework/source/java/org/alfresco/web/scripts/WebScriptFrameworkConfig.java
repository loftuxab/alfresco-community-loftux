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

import java.util.List;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigElement;
import org.alfresco.web.site.AbstractConfig;
import org.alfresco.web.site.RequestContext;

/**
 * @author muzquiano
 */
public class WebScriptFrameworkConfig extends AbstractConfig
{
    protected Config config = null;
    protected boolean isInitialized = false;

    public WebScriptFrameworkConfig(Config config)
    {
        reset(config);
    }

    public void reset(Config config)
    {
        this.config = config;
        this.isInitialized = true;
    }

    public void reset(RequestContext context)
    {
        // not much that can really be done here...
        this.isInitialized = false;
        this.config = null;
    }

    public boolean isInitialized()
    {
        return isInitialized;
    }

    //////////////////////////////////////////////////////////////////
    // Formats
    //////////////////////////////////////////////////////////////////

    public String getDefaultFormatId()
    {
        ConfigElement element = config.getConfigElement("defaults").getChild(
                "format");
        return element.getValue();
    }

    public String[] getFormatIds()
    {
        List elements = config.getConfigElement("formats").getChildren("format");
        String[] ids = new String[elements.size()];
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            ids[i] = configElement.getChildValue("id");
        }
        return ids;
    }

    public String getFormatName(String id)
    {
        List elements = config.getConfigElement("formats").getChildren("format");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("name");
        }
        return null;
    }

    public String getFormatDescription(String id)
    {
        List elements = config.getConfigElement("formats").getChildren("format");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("description");
        }
        return null;
    }

    //////////////////////////////////////////////////////////////////
    // Page Mapper
    //////////////////////////////////////////////////////////////////

    public String getDefaultPageMapperId()
    {
        ConfigElement element = config.getConfigElement("defaults").getChild(
                "page-mapper");
        return element.getValue();
    }

    public String[] getPageMapperIds()
    {
        List elements = config.getConfigElement("page-mapper").getChild(
                "definitions").getChildren("definition");
        String[] ids = new String[elements.size()];
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            ids[i] = configElement.getChildValue("id");
        }
        return ids;
    }

    public String getPageMapperName(String id)
    {
        List elements = config.getConfigElement("page-mapper").getChild(
                "definitions").getChildren("definition");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("name");
        }
        return null;
    }

    public String getPageMapperDescription(String id)
    {
        List elements = config.getConfigElement("page-mapper").getChild(
                "definitions").getChildren("definition");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("description");
        }
        return null;
    }

    public String getPageMapperClass(String id)
    {
        List elements = config.getConfigElement("page-mapper").getChild(
                "definitions").getChildren("definition");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("class");
        }
        return null;
    }

    //////////////////////////////////////////////////////////////////
    // Link Builder
    //////////////////////////////////////////////////////////////////

    public String getDefaultLinkBuilderId()
    {
        ConfigElement element = config.getConfigElement("defaults").getChild(
                "link-builder");
        return element.getValue();
    }

    public String[] getLinkBuilderIds()
    {
        List elements = config.getConfigElement("link-builder").getChild(
                "definitions").getChildren("definition");
        String[] ids = new String[elements.size()];
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            ids[i] = configElement.getChildValue("id");
        }
        return ids;
    }

    public String getLinkBuilderName(String id)
    {
        List elements = config.getConfigElement("link-builder").getChild(
                "definitions").getChildren("definition");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("name");
        }
        return null;
    }

    public String getLinkBuilderDescription(String id)
    {
        List elements = config.getConfigElement("link-builder").getChild(
                "definitions").getChildren("definition");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("description");
        }
        return null;
    }

    public String getLinkBuilderClass(String id)
    {
        List elements = config.getConfigElement("link-builder").getChild(
                "definitions").getChildren("definition");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("class");
        }
        return null;
    }

    //////////////////////////////////////////////////////////////////
    // Request Context
    //////////////////////////////////////////////////////////////////

    public String getDefaultRequestContextId()
    {
        ConfigElement element = config.getConfigElement("defaults").getChild(
                "request-context");
        return element.getValue();
    }

    public String[] getRequestContextIds()
    {
        List elements = config.getConfigElement("request-context").getChild(
                "definitions").getChildren("definition");
        String[] ids = new String[elements.size()];
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            ids[i] = configElement.getChildValue("id");
        }
        return ids;
    }

    public String getRequestContextName(String id)
    {
        List elements = config.getConfigElement("request-context").getChild(
                "definitions").getChildren("definition");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("name");
        }
        return null;
    }

    public String getRequestContextDescription(String id)
    {
        List elements = config.getConfigElement("request-context").getChild(
                "definitions").getChildren("definition");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("description");
        }
        return null;
    }

    public String getRequestContextClass(String id)
    {
        List elements = config.getConfigElement("request-context").getChild(
                "definitions").getChildren("definition");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("class");
        }
        return null;
    }

    public String getRequestContextFactoryClass()
    {
        String defId = getDefaultRequestContextId();
        if (defId != null)
            return getRequestContextClass(defId);
        return null;
    }

    public String getRequestContextSetting(String id, String settingKey)
    {
        List elements = config.getConfigElement("request-context").getChild(
                "definitions").getChildren("definition");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue(settingKey);
        }
        return null;
    }

    //////////////////////////////////////////////////////////////////
    // Alfresco Authoring Server
    //////////////////////////////////////////////////////////////////

    public String getAlfrescoAuthoringHost()
    {
        return config.getConfigElement("alfresco").getChild("authoring").getChildValue(
                "host");
    }

    public String getAlfrescoAuthoringPort()
    {
        return config.getConfigElement("alfresco").getChild("authoring").getChildValue(
                "port");
    }

    public String getAlfrescoAuthoringWebappUri()
    {
        return config.getConfigElement("alfresco").getChild("authoring").getChildValue(
                "webapp-uri");
    }

    public String getAlfrescoAuthoringWebscriptServiceUri()
    {
        return config.getConfigElement("alfresco").getChild("authoring").getChildValue(
                "webscript-service-uri");
    }

    //////////////////////////////////////////////////////////////////
    // Servlet and Dispatcher Information
    //////////////////////////////////////////////////////////////////

    public String getDefaultServletUri()
    {
        return config.getConfigElement("servlet").getChild("default").getChildValue(
                "context");
    }

    public String getDynamicWebsiteServletUri()
    {
        return config.getConfigElement("servlet").getChild("dispatcher").getChildValue(
                "context");
    }

    //////////////////////////////////////////////////////////////////
    // File Systems
    //////////////////////////////////////////////////////////////////

    public String[] getFileSystemIds()
    {
        List elements = config.getConfigElement("file-system").getChild(
                "definitions").getChildren("definition");
        String[] ids = new String[elements.size()];
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            ids[i] = configElement.getChildValue("id");
        }
        return ids;
    }

    public String getFileSystemName(String id)
    {
        List elements = config.getConfigElement("file-system").getChild(
                "definitions").getChildren("definition");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("name");
        }
        return null;
    }

    public String getFileSystemClass(String id)
    {
        List elements = config.getConfigElement("file-system").getChild(
                "definitions").getChildren("definition");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("class");
        }
        return null;
    }

    public String getFileSystemUseCache(String id)
    {
        List elements = config.getConfigElement("file-system").getChild(
                "definitions").getChildren("definition");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("usecache");
        }
        return null;
    }

    public String getFileSystemRootPath(String id)
    {
        List elements = config.getConfigElement("file-system").getChild(
                "definitions").getChildren("definition");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("rootpath");
        }
        return null;
    }

    //////////////////////////////////////////////////////////////////
    // In-Context
    //////////////////////////////////////////////////////////////////

    public boolean isInContextEnabled()
    {
        String v = config.getConfigElement("incontext").getChildValue("enabled");
        return Boolean.parseBoolean(v);
    }

    public String[] getInContextElementIds()
    {
        List elements = config.getConfigElement("incontext").getChild(
                "elements").getChildren("element");
        String[] ids = new String[elements.size()];
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            ids[i] = configElement.getChildValue("id");
        }
        return ids;
    }

    public String getInContextElementName(String id)
    {
        List elements = config.getConfigElement("incontext").getChild(
                "elements").getChildren("element");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("name");
        }
        return null;
    }

    public String getInContextElementType(String id)
    {
        List elements = config.getConfigElement("incontext").getChild(
                "elements").getChildren("element");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("type");
        }
        return null;
    }

    public String getInContextElementDefaultEnabled(String id)
    {
        List elements = config.getConfigElement("incontext").getChild(
                "elements").getChildren("element");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChild("defaults").getChildValue(
                        "enabled");
        }
        return null;
    }

    public String getInContextElementDefaultState(String id)
    {
        List elements = config.getConfigElement("incontext").getChild(
                "elements").getChildren("element");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChild("defaults").getChildValue("state");
        }
        return null;
    }

    //////////////////////////////////////////////////////////////////
    // Model Types
    //////////////////////////////////////////////////////////////////

    public String[] getModelTypeIds()
    {
        List elements = config.getConfigElement("model").getChild("types").getChildren(
                "type");
        String[] ids = new String[elements.size()];
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            ids[i] = configElement.getChildValue("id");
        }
        return ids;
    }

    public String getModelTypeName(String id)
    {
        List elements = config.getConfigElement("model").getChild("types").getChildren(
                "type");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("name");
        }
        return null;
    }

    public String getModelTypeDescription(String id)
    {
        List elements = config.getConfigElement("model").getChild("types").getChildren(
                "type");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("description");
        }
        return null;
    }

    public String getModelTypeTagName(String id)
    {
        List elements = config.getConfigElement("model").getChild("types").getChildren(
                "type");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("tagname");
        }
        return null;
    }

    public String getModelTypeClass(String id)
    {
        List elements = config.getConfigElement("model").getChild("types").getChildren(
                "type");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("class");
        }
        return null;
    }

    public String getModelTypePath(String id)
    {
        List elements = config.getConfigElement("model").getChild("types").getChildren(
                "type");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("path");
        }
        return null;
    }

    public String getModelTypePrefix(String id)
    {
        List elements = config.getConfigElement("model").getChild("types").getChildren(
                "type");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("prefix");
        }
        return null;
    }
    
    public String getModelRootPath()
    {
        String rootPath = config.getConfigElement("model").getChildValue("rootpath");
        if(rootPath == null)
            rootPath = "/";
        return rootPath;
    }
    
    //////////////////////////////////////////////////////////////////
    // Tag Libraries
    //////////////////////////////////////////////////////////////////
    
    public String[] getTagLibraryIds()
    {
        List elements = config.getConfigElement("tags").getChild("libraries").getChildren("library");        
        String[] ids = new String[elements.size()];
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            ids[i] = configElement.getChildValue("id");
        }
        return ids;        
    }
    
    public String getTagLibraryUri(String id)
    {
        List elements = config.getConfigElement("tags").getChild("libraries").getChildren("library");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("uri");
        }
        return null;        
    }

    public String getTagLibraryNamespace(String id)
    {
        List elements = config.getConfigElement("tags").getChild("libraries").getChildren("library");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue("namespace");
        }
        return null;        
    }    
    
    //////////////////////////////////////////////////////////////////
    // User Factories
    //////////////////////////////////////////////////////////////////
    
    public String getDefaultUserFactoryId()
    {
        ConfigElement element = config.getConfigElement("defaults").getChild("user-factory");
        return element.getValue();        
    }
    
    public String[] getUserFactoryIds()
    {
        List elements = config.getConfigElement("user").getChild("factories").getChildren("factory");        
        String[] ids = new String[elements.size()];
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            ids[i] = configElement.getChildValue("id");
        }
        return ids;                
    }

    public String getUserFactoryName(String id)
    {
        return getUserFactoryProperty(id, "name");
    }

    public String getUserFactoryDescription(String id)
    {
        return getUserFactoryProperty(id, "description");
    }

    public String getUserFactoryClass(String id)
    {
        return getUserFactoryProperty(id, "class");
    }

    public String getUserFactoryProperty(String id, String property)
    {
        List elements = config.getConfigElement("user").getChild("factories").getChildren("factory");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue(property);
        }
        return null;                
    }
    
    //////////////////////////////////////////////////////////////////
    // Remote
    //////////////////////////////////////////////////////////////////
    
    public String[] getRemoteConnectorIds()
    {
        List elements = config.getConfigElement("remote").getChild("connectors").getChildren("connector");        
        String[] ids = new String[elements.size()];
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            ids[i] = configElement.getChildValue("id");
        }
        return ids;                
    }

    public String getRemoteConnectorName(String id)
    {
        return getRemoteConnectorProperty(id, "name");        
    }

    public String getRemoteConnectorDescription(String id)
    {
        return getRemoteConnectorProperty(id, "description");
    }

    public String getRemoteConnectorClass(String id)
    {
        return getRemoteConnectorProperty(id, "class");
    }
    
    public String getRemoteConnectorProperty(String id, String property)
    {
        List elements = config.getConfigElement("remote").getChild("connectors").getChildren("connector");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue(property);
        }
        return null;                                
    }

    public String[] getRemoteAuthenticatorIds()
    {
        List elements = config.getConfigElement("remote").getChild("authenticators").getChildren("authenticator");        
        String[] ids = new String[elements.size()];
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            ids[i] = configElement.getChildValue("id");
        }
        return ids;                        
    }

    public String getRemoteAuthenticatorName(String id)
    {
        return getRemoteAuthenticatorProperty(id, "name");
    }

    public String getRemoteAuthenticatorDescription(String id)
    {
        return getRemoteAuthenticatorProperty(id, "description");
    }

    public String getRemoteAuthenticatorClass(String id)
    {
        return getRemoteAuthenticatorProperty(id, "class");
    }
    
    public String getRemoteAuthenticatorProperty(String id, String property)
    {
        List elements = config.getConfigElement("remote").getChild("authenticators").getChildren("authenticator");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue(property);
        }
        return null;                        
    }
    
    
    //////////////////////////////////////////////////////////////////
    // Renderers
    //////////////////////////////////////////////////////////////////
    
    public String[] getRendererIds()
    {
        List elements = config.getConfigElement("renderers").getChildren("renderer");        
        String[] ids = new String[elements.size()];
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            ids[i] = configElement.getChildValue("id");
        }
        return ids;                
        
    }

    public String getRendererName(String id)
    {
        return getRendererProperty(id, "name");
        
    }

    public String getRendererDescription(String id)
    {
        return getRendererProperty(id, "description");
    }

    public String getRendererClass(String id)
    {
        return getRendererProperty(id, "class");
    }
    
    public String getRendererProperty(String id, String property)
    {
        List elements = config.getConfigElement("renderers").getChildren("renderer");
        for (int i = 0; i < elements.size(); i++)
        {
            ConfigElement configElement = (ConfigElement) elements.get(i);
            String _id = configElement.getChildValue("id");
            if (_id.equals(id))
                return configElement.getChildValue(property);
        }
        return null;                        
    }
    
    
}
