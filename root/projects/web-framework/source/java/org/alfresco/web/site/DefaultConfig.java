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

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.alfresco.web.site.filesystem.IFile;

/**
 * @author muzquiano
 */
public class DefaultConfig extends AbstractConfig
{
    public static String PROPERTIES_FILE_NAME = "/default.properties";

    protected Properties properties = null;

    public void reset()
    {
        System.out.println("DefaultSiteConfiguration initializing from web application");
        InputStream stream = DefaultConfig.class.getClassLoader().getResourceAsStream(
                PROPERTIES_FILE_NAME);
        reset(stream);
    }

    // method to have the configuration bootstrap from the file system
    public void reset(RequestContext context)
    {
        System.out.println("DefaultSiteConfiguration initializing from request context");
        IFile file = context.getFileSystem().getFile(
                "/WEB-INF/classes/default.properties");
        if (file != null)
        {
            try
            {
                InputStream stream = (InputStream) context.getFileSystem().getInputStream(
                        file);
                reset(stream);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public void reset(InputStream stream)
    {
        try
        {
            Properties _properties = new Properties();
            _properties.load(stream);
            properties = _properties;
            System.out.println("Successfully loaded: " + PROPERTIES_FILE_NAME);
        }
        catch (Exception ex)
        {
            properties = null;
            ex.printStackTrace();
        }
    }

    public boolean isInitialized()
    {
        return (properties != null);
    }

    public Properties getProperties()
    {
        if (properties == null)
            reset();
        return properties;
    }

    public String get(String key)
    {
        if (getProperties() != null)
            return getProperties().getProperty(key);
        return null;
    }

    //////////////////////////////////////////////////////////////////
    // Formats
    //////////////////////////////////////////////////////////////////

    public String getDefaultFormatId()
    {
        return get("format.default.id");
    }

    public String[] getFormatIds()
    {
        String _car = "format.definition.";
        String _cdr = ".name";

        return spliceIds(_car, _cdr);
    }

    public String getFormatName(String id)
    {
        String key = "format.definition." + id + ".name";
        return get(key);
    }

    public String getFormatDescription(String id)
    {
        String key = "format.definition." + id + ".description";
        return get(key);
    }

    //////////////////////////////////////////////////////////////////
    // Page Mapper
    //////////////////////////////////////////////////////////////////

    public String getDefaultPageMapperId()
    {
        return get("pagemapper.default.id");
    }

    public String[] getPageMapperIds()
    {
        String _car = "pagemapper.definition.";
        String _cdr = ".name";

        return spliceIds(_car, _cdr);
    }

    public String getPageMapperName(String id)
    {
        String key = "pagemapper.definition." + id + ".name";
        return get(key);
    }

    public String getPageMapperDescription(String id)
    {
        String key = "pagemapper.definition." + id + ".description";
        return get(key);
    }

    public String getPageMapperClass(String id)
    {
        String key = "pagemapper.definition." + id + ".class";
        return get(key);
    }

    //////////////////////////////////////////////////////////////////
    // Link Builder
    //////////////////////////////////////////////////////////////////

    public String getDefaultLinkBuilderId()
    {
        return get("linkbuilder.default.id");
    }

    public String[] getLinkBuilderIds()
    {
        String _car = "linkbuilder.definition.";
        String _cdr = ".name";

        return spliceIds(_car, _cdr);
    }

    public String getLinkBuilderName(String id)
    {
        String key = "linkbuilder.definition." + id + ".name";
        return get(key);
    }

    public String getLinkBuilderDescription(String id)
    {
        String key = "linkbuilder.definition." + id + ".description";
        return get(key);
    }

    public String getLinkBuilderClass(String id)
    {
        String key = "linkbuilder.definition." + id + ".class";
        return get(key);
    }

    //////////////////////////////////////////////////////////////////
    // Request Context
    //////////////////////////////////////////////////////////////////

    public String getDefaultRequestContextId()
    {
        return get("requestcontext.default.id");
    }

    public String[] getRequestContextIds()
    {
        String _car = "requestcontext.definition.";
        String _cdr = ".name";

        return spliceIds(_car, _cdr);
    }

    public String getRequestContextName(String id)
    {
        String key = "requestcontext.definition." + id + ".name";
        return get(key);
    }

    public String getRequestContextDescription(String id)
    {
        String key = "requestcontext.definition." + id + ".description";
        return get(key);
    }

    public String getRequestContextClass(String id)
    {
        String key = "requestcontext.definition." + id + ".class";
        return get(key);
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
        String key = "requestcontext.definition." + id + "." + settingKey;
        return get(key);
    }

    //////////////////////////////////////////////////////////////////
    // Alfresco Authoring Server
    //////////////////////////////////////////////////////////////////

    public String getAlfrescoAuthoringHost()
    {
        String value = get("alfresco.authoring.host");
        if (value == null)
            value = "localhost";
        return value;
    }

    public String getAlfrescoAuthoringPort()
    {
        String value = get("alfresco.authoring.port");
        if (value == null)
            value = "8080";
        return value;
    }

    public String getAlfrescoAuthoringWebappUri()
    {
        String value = get("alfresco.authoring.webapp.uri");
        if (value == null)
            value = "/alfresco";
        return value;
    }

    public String getAlfrescoAuthoringWebscriptServiceUri()
    {
        String value = get("alfresco.authoring.webscript.service.uri");
        if (value == null)
            value = "/alfresco/service";
        return value;
    }

    //////////////////////////////////////////////////////////////////
    // Servlet and Dispatcher Information
    //////////////////////////////////////////////////////////////////

    public String getDefaultServletUri()
    {
        String value = get("servlet.default.context");
        if (value == null)
            value = "/";
        if (!value.endsWith("/"))
            value = value + "/";
        return value;
    }

    public String getDynamicWebsiteServletUri()
    {
        String value = get("servlet.dispatcher.context");
        if (value == null)
            value = "/";
        return value;
    }

    public String getDefaultPageUri()
    {
        String value = get("page.default.uri");
        if (value == null)
            value = "/ui/core/page-default.jsp";
        return value;
    }

    //////////////////////////////////////////////////////////////////
    // File Systems
    //////////////////////////////////////////////////////////////////

    public String[] getFileSystemIds()
    {
        String _car = "filesystem.";
        String _cdr = ".name";

        return spliceIds(_car, _cdr);
    }

    public String getFileSystemName(String id)
    {
        String key = "filesystem." + id + ".name";
        return get(key);
    }

    public String getFileSystemClass(String id)
    {
        String key = "filesystem." + id + ".class";
        return get(key);
    }

    public String getFileSystemUseCache(String id)
    {
        String key = "filesystem." + id + ".usecache";
        return get(key);
    }

    public String getFileSystemRootPath(String id)
    {
        String key = "filesystem." + id + ".rootpath";
        String value = get(key);
        if (value == null)
            value = "/";
        return value;
    }

    //////////////////////////////////////////////////////////////////
    // In-Context
    //////////////////////////////////////////////////////////////////

    public boolean isInContextEnabled()
    {
        String value = get("incontext.enabled");
        if ("true".equalsIgnoreCase(value))
            return true;
        return false;
    }

    public String[] getInContextElementIds()
    {
        String _car = "incontext.element.";
        String _cdr = ".name";

        return spliceIds(_car, _cdr);
    }

    public String getInContextElementName(String id)
    {
        String key = "incontext.element." + id + ".name";
        return get(key);
    }

    public String getInContextElementType(String id)
    {
        String key = "incontext.element." + id + ".type";
        return get(key);
    }

    public String getInContextElementDefaultEnabled(String id)
    {
        String key = "incontext.element." + id + ".default.enabled";
        return get(key);
    }

    public String getInContextElementDefaultState(String id)
    {
        String key = "incontext.element." + id + ".default.state";
        return get(key);
    }

    //////////////////////////////////////////////////////////////////
    // Model Types
    //////////////////////////////////////////////////////////////////

    public String[] getModelTypeIds()
    {
        String _car = "model.type.";
        String _cdr = ".name";

        return spliceIds(_car, _cdr);
    }

    public String getModelTypeName(String id)
    {
        String key = "model.type." + id + ".name";
        return get(key);
    }

    public String getModelTypeDescription(String id)
    {
        String key = "model.type." + id + ".class";
        return get(key);
    }
    
    public String getModelTypeNamespace(String id)
    {
        String key = "model.type." + id + ".namespace";
        return get(key);
    }

    public String getModelTypeTagName(String id)
    {
        String key = "model.type." + id + ".tagname";
        return get(key);
    }

    public String getModelTypeClass(String id)
    {
        String key = "model.type." + id + ".class";
        return get(key);
    }

    public String getModelTypePath(String id)
    {
        String key = "model.type." + id + ".path";
        return get(key);
    }

    public String getModelTypePrefix(String id)
    {
        String key = "model.type." + id + ".prefix";
        return get(key);
    }

    public String getModelRootPath()
    {
        String key = "model.rootpath";
        String value = get(key);
        if (value == null)
            value = "/";
        return value;
    }

    //////////////////////////////////////////////////////////////////
    // Tag Libraries
    //////////////////////////////////////////////////////////////////

    public String[] getTagLibraryIds()
    {
        String _car = "tags.libraries.library.";
        String _cdr = ".id";

        return spliceIds(_car, _cdr);
    }

    public String getTagLibraryUri(String id)
    {
        String key = "tags.libraries.library." + id + ".uri";
        return get(key);
    }

    public String getTagLibraryNamespace(String id)
    {
        String key = "tags.libraries.library." + id + ".namespace";
        return get(key);
    }

    //////////////////////////////////////////////////////////////////
    // User Factories
    //////////////////////////////////////////////////////////////////

    public String getDefaultUserFactoryId()
    {
        return get("user.factory.default.id");
    }

    public String[] getUserFactoryIds()
    {
        String _car = "user.factory.definition.";
        String _cdr = ".name";

        return spliceIds(_car, _cdr);
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
        String key = "user.factory.definition." + id + "." + property;
        return get(key);
    }

    //////////////////////////////////////////////////////////////////
    // Remote
    //////////////////////////////////////////////////////////////////

    public String[] getRemoteConnectorIds()
    {
        String _car = "remote.connector.";
        String _cdr = ".name";

        return spliceIds(_car, _cdr);

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
        String key = "remote.connector." + id + "." + property;
        return get(key);
    }

    public String[] getRemoteAuthenticatorIds()
    {
        String _car = "remote.authenticator.";
        String _cdr = ".name";

        return spliceIds(_car, _cdr);
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
        String key = "remote.authenticator." + id + "." + property;
        return get(key);
    }

    //////////////////////////////////////////////////////////////////
    // Renderers
    //////////////////////////////////////////////////////////////////

    public String[] getRendererIds()
    {
        String _car = "renderers.renderer.";
        String _cdr = ".name";

        return spliceIds(_car, _cdr);
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
        String key = "renderers.renderer." + id + "." + property;
        return get(key);
    }

    //////////////////////////////////////////////////////////////////
    // Utils
    //////////////////////////////////////////////////////////////////

    protected String[] spliceIds(String _car, String _cdr)
    {
        if (getProperties() == null)
            return null;

        Map map = new HashMap();

        Enumeration keys = getProperties().keys();
        while (keys.hasMoreElements())
        {
            String key = (String) keys.nextElement();
            if (key != null && key.startsWith(_car))
            {
                int x = key.indexOf(_cdr);
                if (x > -1)
                {
                    String spliceId = key.substring(_car.length(), x);
                    map.put(spliceId, spliceId);
                }
            }
        }

        String[] spliceIds = new String[map.size()];
        Object[] values = map.keySet().toArray();
        for (int i = 0; i < values.length; i++)
            spliceIds[i] = (String) values[i];

        return spliceIds;
    }

}
