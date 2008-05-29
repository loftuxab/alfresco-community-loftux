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
package org.alfresco.web.config;

import java.util.HashMap;
import java.util.List;

import org.alfresco.config.ConfigElement;
import org.alfresco.config.element.ConfigElementAdapter;
import org.dom4j.Element;

/**
 * @author muzquiano
 */
public class WebFrameworkConfigElement extends ConfigElementAdapter implements WebFrameworkConfigProperties
{
    public static final String CONFIG_ELEMENT_ID = "web-framework";

    protected HashMap<String, FormatDescriptor> formats = null;
    protected HashMap<String, PageMapperDescriptor> pageMappers = null;
    protected HashMap<String, LinkBuilderDescriptor> linkBuilders = null;
    protected HashMap<String, RequestContextDescriptor> requestContexts = null;
    protected HashMap<String, ErrorHandlerDescriptor> errorHandlers = null;
    protected HashMap<String, SystemPageDescriptor> systemPages = null;
    protected HashMap<String, FileSystemDescriptor> fileSystems = null;
    protected HashMap<String, TagLibraryDescriptor> tagLibraries = null;
    protected HashMap<String, UserFactoryDescriptor> userFactories = null;
    protected HashMap<String, RendererDescriptor> renderers = null;
    protected HashMap<String, String> pageTypes = null;
    protected HashMap<String, TypeDescriptor> types = null;
    protected HashMap<String, ContentLoaderDescriptor> contentLoaders = null;

    protected boolean isTimerEnabled = false;
    protected String defaultFormatId = null;
    protected String defaultFileSystemId = null;
    protected String defaultPageMapperId = null;
    protected String defaultLinkBuilderId = null;
    protected String defaultRequestContextId = null;
    protected String defaultUserFactoryId = null;

    protected String defaultRegionChrome = null;
    protected String defaultComponentChrome = null;
    protected String defaultTheme = null;
    protected String defaultSiteConfiguration = null;

    protected String rootPath;

    /**
     * Default Constructor
     */
    public WebFrameworkConfigElement()
    {
        super(CONFIG_ELEMENT_ID);

        formats = new HashMap<String, FormatDescriptor>();
        pageMappers = new HashMap<String, PageMapperDescriptor>();
        linkBuilders = new HashMap<String, LinkBuilderDescriptor>();
        requestContexts = new HashMap<String, RequestContextDescriptor>();
        errorHandlers = new HashMap<String, ErrorHandlerDescriptor>();
        systemPages = new HashMap<String, SystemPageDescriptor>();
        fileSystems = new HashMap<String, FileSystemDescriptor>();
        tagLibraries = new HashMap<String, TagLibraryDescriptor>();
        userFactories = new HashMap<String, UserFactoryDescriptor>();
        renderers = new HashMap<String, RendererDescriptor>();
        pageTypes = new HashMap<String, String>();
        types = new HashMap<String, TypeDescriptor>();
        contentLoaders = new HashMap<String, ContentLoaderDescriptor>();

        isTimerEnabled = false;
    }

    /* (non-Javadoc)
     * @see org.alfresco.config.element.GenericConfigElement#combine(org.alfresco.config.ConfigElement)
     */
    public ConfigElement combine(ConfigElement element)
    {
        WebFrameworkConfigElement configElement = (WebFrameworkConfigElement) element;

        // new combined element    	
        WebFrameworkConfigElement combinedElement = new WebFrameworkConfigElement();

        // copy in our things
        combinedElement.formats.putAll(this.formats);
        combinedElement.pageMappers.putAll(this.pageMappers);
        combinedElement.linkBuilders.putAll(this.linkBuilders);
        combinedElement.requestContexts.putAll(this.requestContexts);
        combinedElement.errorHandlers.putAll(this.errorHandlers);
        combinedElement.systemPages.putAll(this.systemPages);
        combinedElement.fileSystems.putAll(this.fileSystems);
        combinedElement.tagLibraries.putAll(this.tagLibraries);
        combinedElement.userFactories.putAll(this.userFactories);
        combinedElement.renderers.putAll(this.renderers);
        combinedElement.types.putAll(this.types);
        combinedElement.contentLoaders.putAll(this.contentLoaders);
        combinedElement.pageTypes.putAll(this.pageTypes);

        // override with things from the merging object
        combinedElement.formats.putAll(configElement.formats);
        combinedElement.pageMappers.putAll(configElement.pageMappers);
        combinedElement.linkBuilders.putAll(configElement.linkBuilders);        
        combinedElement.requestContexts.putAll(configElement.requestContexts);
        combinedElement.errorHandlers.putAll(configElement.errorHandlers);
        combinedElement.systemPages.putAll(configElement.systemPages);
        combinedElement.fileSystems.putAll(configElement.fileSystems);
        combinedElement.tagLibraries.putAll(configElement.tagLibraries);
        combinedElement.userFactories.putAll(configElement.userFactories);
        combinedElement.renderers.putAll(configElement.renderers);
        combinedElement.types.putAll(configElement.types);
        combinedElement.contentLoaders.putAll(configElement.contentLoaders);
        combinedElement.pageTypes.putAll(configElement.pageTypes);

        // other properties
        combinedElement.isTimerEnabled = this.isTimerEnabled;
        if(configElement.isTimerEnabled)
        {
            combinedElement.isTimerEnabled = configElement.isTimerEnabled;
        }
        combinedElement.defaultFormatId = this.defaultFormatId;
        if(configElement.defaultFormatId != null)
        {
            combinedElement.defaultFormatId = configElement.defaultFormatId;
        }
        combinedElement.defaultFileSystemId = this.defaultFileSystemId;
        if(configElement.defaultFileSystemId != null)
        {
            combinedElement.defaultFileSystemId = configElement.defaultFileSystemId;
        }
        combinedElement.defaultPageMapperId = this.defaultPageMapperId;
        if(configElement.defaultPageMapperId != null)
        {
            combinedElement.defaultPageMapperId = configElement.defaultPageMapperId;
        }
        combinedElement.defaultLinkBuilderId = this.defaultLinkBuilderId;
        if(configElement.defaultLinkBuilderId != null)
        {
            combinedElement.defaultLinkBuilderId = configElement.defaultLinkBuilderId;
        }
        combinedElement.defaultRequestContextId = this.defaultRequestContextId;
        if(configElement.defaultRequestContextId != null)
        {
            combinedElement.defaultRequestContextId = configElement.defaultRequestContextId;
        }
        combinedElement.defaultUserFactoryId = this.defaultUserFactoryId;
        if(configElement.defaultUserFactoryId != null)
        {
            combinedElement.defaultUserFactoryId = configElement.defaultUserFactoryId;
        }

        combinedElement.defaultRegionChrome = this.defaultRegionChrome;
        if(configElement.defaultRegionChrome != null)
        {
            combinedElement.defaultRegionChrome = configElement.defaultRegionChrome;
        }
        combinedElement.defaultComponentChrome = this.defaultComponentChrome;
        if(configElement.defaultComponentChrome != null)
        {
            combinedElement.defaultComponentChrome = configElement.defaultComponentChrome;
        }
        combinedElement.defaultTheme = this.defaultTheme;
        if(configElement.defaultTheme != null)
        {
            combinedElement.defaultTheme = configElement.defaultTheme;
        }

        combinedElement.rootPath = this.rootPath;
        if(configElement.rootPath != null)
        {
            combinedElement.rootPath = configElement.rootPath;
        }
        
        combinedElement.defaultSiteConfiguration = this.defaultSiteConfiguration;
        if(configElement.defaultSiteConfiguration != null)
        {
            combinedElement.defaultSiteConfiguration = configElement.defaultSiteConfiguration;
        }

        return combinedElement;
    }


    public String[] getFormatIds()
    {
        return this.formats.keySet().toArray(new String[this.formats.size()]);
    }
    public FormatDescriptor getFormatDescriptor(String id)
    {
        return (FormatDescriptor) this.formats.get(id);
    }

    // page mappers
    public String[] getPageMapperIds()
    {
        return this.pageMappers.keySet().toArray(new String[this.pageMappers.size()]);
    }
    public PageMapperDescriptor getPageMapperDescriptor(String id)
    {
        return (PageMapperDescriptor) this.pageMappers.get(id);
    }

    // link builders
    public String[] getLinkBuilderIds()
    {
        return this.linkBuilders.keySet().toArray(new String[this.linkBuilders.size()]);
    }
    public LinkBuilderDescriptor getLinkBuilderDescriptor(String id)
    {
        return (LinkBuilderDescriptor) this.linkBuilders.get(id);
    }

    // request contexts
    public String[] getRequestContextIds()
    {
        return this.requestContexts.keySet().toArray(new String[this.requestContexts.size()]);
    }
    public RequestContextDescriptor getRequestContextDescriptor(String id)
    {
        return (RequestContextDescriptor) this.requestContexts.get(id);
    }

    // error handlers
    public String[] getErrorHandlerIds()
    {
        return this.errorHandlers.keySet().toArray(new String[this.errorHandlers.size()]);
    }
    public ErrorHandlerDescriptor getErrorHandlerDescriptor(String id)
    {
        return (ErrorHandlerDescriptor) this.errorHandlers.get(id);
    }

    // system pages
    public String[] getSystemPageIds()
    {
        return this.systemPages.keySet().toArray(new String[this.systemPages.size()]);
    }
    public SystemPageDescriptor getSystemPageDescriptor(String id)
    {
        return (SystemPageDescriptor) this.systemPages.get(id);
    }

    // file systems
    public String[] getFileSystemIds()
    {
        return this.fileSystems.keySet().toArray(new String[this.fileSystems.size()]);
    }
    public FileSystemDescriptor getFileSystemDescriptor(String id)
    {
        return (FileSystemDescriptor) this.fileSystems.get(id);
    }

    // tag libraries
    public String[] getTagLibraryIds()
    {
        return this.tagLibraries.keySet().toArray(new String[this.tagLibraries.size()]);
    }
    public TagLibraryDescriptor getTagLibraryDescriptor(String id)
    {
        return (TagLibraryDescriptor) this.tagLibraries.get(id);
    }

    // user factories
    public String[] getUserFactoryIds()
    {
        return this.userFactories.keySet().toArray(new String[this.userFactories.size()]);
    }
    public UserFactoryDescriptor getUserFactoryDescriptor(String id)
    {
        return (UserFactoryDescriptor) this.userFactories.get(id);
    }

    // renderers
    public String[] getRendererIds()
    {
        return this.renderers.keySet().toArray(new String[this.renderers.size()]);
    }
    public RendererDescriptor getRendererDescriptor(String id)
    {
        return (RendererDescriptor) this.renderers.get(id);
    }

    // types (model files)
    public String[] getTypeIds()
    {
        return this.types.keySet().toArray(new String[this.types.size()]);
    }

    public TypeDescriptor getTypeDescriptor(String id)
    {
        return (TypeDescriptor) this.types.get(id);
    }

    public String getRootPath()
    {
        if(rootPath == null)
        {
            return "/WEB-INF/classes/alfresco";
        }
        return rootPath;
    }
    
    // content loaders
    public String[] getContentLoaderIds()
    {
    	return this.contentLoaders.keySet().toArray(new String[this.contentLoaders.size()]);
    }
    
    public ContentLoaderDescriptor getContentLoaderDescriptor(String id)
    {
    	return (ContentLoaderDescriptor) this.contentLoaders.get(id);    	
    }
    
    // debug
    public boolean isTimerEnabled()
    {
        return this.isTimerEnabled;
    }

    // application defaults
    public String getDefaultRegionChrome()
    {
        return this.defaultRegionChrome;
    }
    
    public String getDefaultComponentChrome()
    {
        return this.defaultComponentChrome;
    }
    
    public String[] getDefaultPageTypeIds()
    {
    	return this.pageTypes.keySet().toArray(new String[this.pageTypes.size()]);
    }
    
    public String getDefaultPageTypeInstanceId(String id)
    {
        return (String) this.pageTypes.get(id);
    }
    
    public String getDefaultThemeId()
    {
        if(this.defaultTheme == null)
        {
            return "default";
        }
        return this.defaultTheme;
    }
    
    public String getDefaultSiteConfigurationId()
    {
        if(this.defaultSiteConfiguration == null)
        {
            return "default.site.configuration";
        }
        return this.defaultSiteConfiguration;
    }

    // framework defaults
    public String getDefaultFormatId()
    {
        if(this.defaultFormatId == null)
        {
            return "default";
        }
        return this.defaultFormatId;
    }
    
    public String getDefaultPageMapperId()
    {
        if(this.defaultPageMapperId == null)
        {
            return "default";
        }
        return this.defaultPageMapperId;
    }
    
    public String getDefaultLinkBuilderId()
    {
        if(this.defaultLinkBuilderId == null)
        {
            return "default";
        }
        return this.defaultLinkBuilderId;
    }
    
    public String getDefaultRequestContextId()
    {
        if(this.defaultRequestContextId == null)
        {
            return "http";
        }
        return this.defaultRequestContextId;
    }
    
    public String getDefaultUserFactoryId()
    {
        if(this.defaultUserFactoryId == null)
        {
            return "default";
        }
        return this.defaultUserFactoryId;
    }
    
    public String getDefaultFileSystemId()
    {
        if(this.defaultFileSystemId == null)
        {
            return "local";
        }
        return this.defaultFileSystemId;
    }


    /**
     * Base for all Descriptor classes. Defines a basic get/put property bag
     * of descriptor info. Sub classes should provide typed and named getter/setters.
     */
    public static class Descriptor
    {
        private static final String ID = "id";

        HashMap<String, Object> map;

        Descriptor(Element el)
        {
            List elements = el.elements();
            for(int i = 0; i < elements.size(); i++)
            {
                Element element = (Element) elements.get(i);
                put(element);
            }
        }

        public void put(Element el)
        {
            if(this.map == null)
            {
                this.map = new HashMap<String, Object>();
            }

            String key = el.getName();
            Object value = (Object) el.getTextTrim();
            if(value != null)
            {
                this.map.put(key, value);
            }
        }

        public Object get(String key)
        {
            if(this.map == null)
            {
                this.map = new HashMap<String, Object>();
            }

            return (Object) this.map.get(key);
        }	

        public String getId() 
        {
            return (String) get(ID);
        }		

        public Object getProperty(String key)
        {
            return get(key);
        }

        public String getStringProperty(String key)
        {
            return (String) get(key);
        }
    }

    public static class FormatDescriptor extends Descriptor
    {
        private static final String DESCRIPTION = "description";
        private static final String NAME = "name";

        FormatDescriptor(Element el)
        {
            super(el);
        }

        public String getName() 
        {
            return getStringProperty(NAME);
        }
        public String getDescription() 
        {
            return getStringProperty(DESCRIPTION);
        }
    }

    public static class PageMapperDescriptor extends Descriptor
    {
        private static final String CLAZZ = "class";
        private static final String DESCRIPTION = "description";
        private static final String NAME = "name";

        PageMapperDescriptor(Element el)
        {
            super(el);
        }

        public String getImplementationClass() 
        {
            return getStringProperty(CLAZZ);
        }
        public String getDescription() 
        {
            return getStringProperty(DESCRIPTION);
        }
        public String getName() 
        {
            return getStringProperty(NAME);
        }
    }

    public static class LinkBuilderDescriptor extends Descriptor
    {
        private static final String CLAZZ = "class";
        private static final String DESCRIPTION = "description";
        private static final String NAME = "name";

        LinkBuilderDescriptor(Element el)
        {
            super(el);
        }

        public String getImplementationClass() 
        {
            return getStringProperty(CLAZZ);
        }
        public String getDescription() 
        {
            return getStringProperty(DESCRIPTION);
        }
        public String getName() 
        {
            return getStringProperty(NAME);
        }
    }

    public static class RequestContextDescriptor extends Descriptor
    {
        private static final String CLAZZ = "class";
        private static final String DESCRIPTION = "description";
        private static final String NAME = "name";

        RequestContextDescriptor(Element el)
        {
            super(el);
        }

        public String getImplementationClass() 
        {
            return getStringProperty(CLAZZ);
        }
        public String getDescription() 
        {
            return getStringProperty(DESCRIPTION);
        }
        public String getName() 
        {
            return getStringProperty(NAME);
        }
    }

    public static class ErrorHandlerDescriptor extends Descriptor
    {
        private static final String RENDERER_TYPE = "renderer-type";
        private static final String RENDERER = "renderer";

        ErrorHandlerDescriptor(Element el)
        {
            super(el);
        }

        public String getRendererType() 
        {
            return getStringProperty(RENDERER_TYPE);
        }
        public String getRenderer() 
        {
            return getStringProperty(RENDERER);
        }
    }

    public static class SystemPageDescriptor extends Descriptor
    {
        private static final String RENDERER_TYPE = "renderer-type";
        private static final String RENDERER = "renderer";

        SystemPageDescriptor(Element el)
        {
            super(el);
        }

        public String getRendererType() 
        {
            return getStringProperty(RENDERER_TYPE);
        }
        public String getRenderer() 
        {
            return getStringProperty(RENDERER);
        }
    }

    public static class FileSystemDescriptor extends Descriptor
    {
        private static final String ROOT_PATH = "root-path";
        private static final String USE_CACHE = "use-cache";
        private static final String CLAZZ = "class";
        private static final String DESCRIPTION = "description";
        private static final String NAME = "name";
        private static final String STORE = "store";

        FileSystemDescriptor(Element el)
        {
            super(el);
        }

        public String getImplementationClass() 
        {
            return getStringProperty(CLAZZ);
        }
        public String getDescription() 
        {
            return getStringProperty(DESCRIPTION);
        }
        public String getName() 
        {
            return getStringProperty(NAME);
        }
        public String getRootPath() 
        {
            return getStringProperty(ROOT_PATH);
        }
        public String getUseCache() 
        {
            return getStringProperty(USE_CACHE);
        }
        public String getStore()
        {
            return getStringProperty(STORE);
        }
    }

    public static class TagLibraryDescriptor extends Descriptor
    {
        private static final String NAMESPACE = "namespace";
        private static final String URI = "uri";
        TagLibraryDescriptor(Element el)
        {
            super(el);
        }

        public String getUri()
        {
            return getStringProperty(URI);
        }

        public String getNamespace()
        {
            return getStringProperty(NAMESPACE);
        }
    }

    public static class UserFactoryDescriptor extends Descriptor
    {
        private static final String CLAZZ = "class";
        private static final String DESCRIPTION = "description";
        private static final String NAME = "name";

        UserFactoryDescriptor(Element el)
        {
            super(el);
        }

        public String getImplementationClass() 
        {
            return getStringProperty(CLAZZ);
        }
        public String getDescription() 
        {
            return getStringProperty(DESCRIPTION);
        }
        public String getName() 
        {
            return getStringProperty(NAME);
        }    	    	
    }

    public static class RendererDescriptor extends Descriptor
    {
        private static final String CLAZZ = "class";
        private static final String DESCRIPTION = "description";
        private static final String NAME = "name";

        RendererDescriptor(Element el)
        {
            super(el);
        }

        public String getImplementationClass() 
        {
            return getStringProperty(CLAZZ);
        }
        public String getDescription() 
        {
            return getStringProperty(DESCRIPTION);
        }
        public String getName() 
        {
            return getStringProperty(NAME);
        }    	    	
    }

    public static class TypeDescriptor extends Descriptor
    {
        private static final String PREFIX = "prefix";
        private static final String PATH = "path";
        private static final String CLAZZ = "class";
        private static final String TAGNAME = "tagname";
        private static final String NAMESPACE = "namespace";
        private static final String DESCRIPTION = "description";
        private static final String NAME = "name";
        private static final String VERSION = "version";

        TypeDescriptor(Element el)
        {
            super(el);
        }

        public String getImplementationClass() 
        {
            return getStringProperty(CLAZZ);
        }
        public String getDescription() 
        {
            return getStringProperty(DESCRIPTION);
        }
        public String getName() 
        {
            return getStringProperty(NAME);
        }
        public String getNamespace() 
        {
            return getStringProperty(NAMESPACE);
        }
        public String getPath() 
        {
            return getStringProperty(PATH);
        }
        public String getPrefix() 
        {
            return getStringProperty(PREFIX);
        }
        public String getTagname() 
        {
            return getStringProperty(TAGNAME);
        }
        public String getVersion() 
        {
            return getStringProperty(VERSION);
        }
    }
    
    public static class ContentLoaderDescriptor extends Descriptor
    {
        private static final String CLAZZ = "class";
        private static final String DESCRIPTION = "description";
        private static final String NAME = "name";
        private static final String ENDPOINT = "endpoint";

        ContentLoaderDescriptor(Element el)
        {
            super(el);
        }

        public String getImplementationClass() 
        {
            return getStringProperty(CLAZZ);
        }
        public String getDescription() 
        {
            return getStringProperty(DESCRIPTION);
        }
        public String getName() 
        {
            return getStringProperty(NAME);
        }
        public String getEndpoint() 
        {
            return getStringProperty(ENDPOINT);
        }
    }
    


    protected static WebFrameworkConfigElement newInstance(Element elem)
    {
        WebFrameworkConfigElement configElement = new WebFrameworkConfigElement();

        // formats
        List formats = elem.elements("format");
        for(int i = 0; i < formats.size(); i++)
        {
            Element el = (Element) formats.get(i);
            FormatDescriptor descriptor = new FormatDescriptor(el);
            configElement.formats.put(descriptor.getId(), descriptor);
        }

        // page mappers
        List pageMappers = elem.elements("page-mapper");
        for(int i = 0; i < pageMappers.size(); i++)
        {
            Element el = (Element) pageMappers.get(i);
            PageMapperDescriptor descriptor = new PageMapperDescriptor(el);
            configElement.pageMappers.put(descriptor.getId(), descriptor);
        }

        // link builders
        List linkBuilders = elem.elements("link-builder");
        for(int i = 0; i < linkBuilders.size(); i++)
        {
            Element el = (Element) linkBuilders.get(i);
            LinkBuilderDescriptor descriptor = new LinkBuilderDescriptor(el);
            configElement.linkBuilders.put(descriptor.getId(), descriptor);
        }

        // request contexts
        List requestContexts = elem.elements("request-context");
        for(int i = 0; i < requestContexts.size(); i++)
        {
            Element el = (Element) requestContexts.get(i);
            RequestContextDescriptor descriptor = new RequestContextDescriptor(el);
            configElement.requestContexts.put(descriptor.getId(), descriptor);
        }


        // error handlers
        List errorHandlers = elem.elements("error-handler");
        for(int i = 0; i < errorHandlers.size(); i++)
        {
            Element el = (Element) errorHandlers.get(i);
            ErrorHandlerDescriptor descriptor = new ErrorHandlerDescriptor(el);
            configElement.errorHandlers.put(descriptor.getId(), descriptor);
        }

        // system pages
        List systemPages = elem.elements("system-page");
        for(int i = 0; i < systemPages.size(); i++)
        {
            Element el = (Element) systemPages.get(i);
            SystemPageDescriptor descriptor = new SystemPageDescriptor(el);
            configElement.systemPages.put(descriptor.getId(), descriptor);
        }

        // file systems
        List fileSystems= elem.elements("file-system");
        for(int i = 0; i < fileSystems.size(); i++)
        {
            Element el = (Element) fileSystems.get(i);
            FileSystemDescriptor descriptor = new FileSystemDescriptor(el);
            configElement.fileSystems.put(descriptor.getId(), descriptor);
        }

        // tag libraries
        List tagLibraries= elem.elements("tag-library");
        for(int i = 0; i < tagLibraries.size(); i++)
        {
            Element el = (Element) tagLibraries.get(i);
            TagLibraryDescriptor descriptor = new TagLibraryDescriptor(el);
            configElement.tagLibraries.put(descriptor.getId(), descriptor);
        }

        // user factories
        List userFactories = elem.elements("user-factory");
        for(int i = 0; i < userFactories.size(); i++)
        {
            Element el = (Element) userFactories.get(i);
            UserFactoryDescriptor descriptor = new UserFactoryDescriptor(el);
            configElement.userFactories.put(descriptor.getId(), descriptor);
        }

        // renderers
        List renderers = elem.elements("renderer");
        for(int i = 0; i < renderers.size(); i++)
        {
            Element el = (Element) renderers.get(i);
            RendererDescriptor descriptor = new RendererDescriptor(el);
            configElement.renderers.put(descriptor.getId(), descriptor);
        }

        // framework defaults
        Element frameworkDefaults = elem.element("framework-defaults");
        if(frameworkDefaults != null)
        {
            String _requestContext = frameworkDefaults.elementTextTrim("request-context");
            if(_requestContext != null)
            {
                configElement.defaultRequestContextId = _requestContext;
            }
            String _fileSystem = frameworkDefaults.elementTextTrim("file-system");
            if(_fileSystem != null)
            {
                configElement.defaultFileSystemId = _fileSystem;
            }
            String _format = frameworkDefaults.elementTextTrim("format");
            if(_format != null)
            {
                configElement.defaultFormatId = _format;
            }
            String _pageMapper = frameworkDefaults.elementTextTrim("page-mapper");
            if(_pageMapper != null)
            {
                configElement.defaultPageMapperId = _pageMapper;
            }
            String _linkBuilder = frameworkDefaults.elementTextTrim("link-builder");
            if(_linkBuilder != null)
            {
                configElement.defaultLinkBuilderId = _linkBuilder;
            }
            String _userFactory = frameworkDefaults.elementTextTrim("user-factory");
            if(_userFactory != null)
            {
                configElement.defaultUserFactoryId = _userFactory;
            }
        }

        // application defaults
        Element applicationDefaults = elem.element("application-defaults");
        if(applicationDefaults != null)
        {
            String _regionChrome = applicationDefaults.elementTextTrim("region-chrome");
            if(_regionChrome != null)
            {
                configElement.defaultRegionChrome = _regionChrome;
            }
            String _componentChrome = applicationDefaults.elementTextTrim("component-chrome");
            if(_componentChrome != null)
            {
                configElement.defaultComponentChrome = _componentChrome;
            }
            String _theme = applicationDefaults.elementTextTrim("theme");
            if(_theme != null && _theme.length() != 0)
            {
                configElement.defaultTheme = _theme;
            }

            List pageTypes = applicationDefaults.elements("page-type");
            for(int i = 0; i < pageTypes.size(); i++)
            {
                Element pageType = (Element) pageTypes.get(i);
                String pageTypeId = pageType.elementTextTrim("id");
                String pageTypeInstanceId = pageType.elementTextTrim("page-instance-id");
                configElement.pageTypes.put(pageTypeId, pageTypeInstanceId);
            }
            
            String _siteConfiguration = applicationDefaults.elementTextTrim("site-configuration");
            if(_siteConfiguration != null)
            {
                configElement.defaultSiteConfiguration = _siteConfiguration;
            }
        }

        //////////////////////////////////////////////////////
        // Debug Timer
        //////////////////////////////////////////////////////

        Element debugElement = elem.element("debug");
        if(debugElement != null)
        {
            String _isTimerEnabled = debugElement.elementTextTrim("timer");
            if(_isTimerEnabled != null)
            {
                configElement.isTimerEnabled = Boolean.parseBoolean(_isTimerEnabled);
            }
        }    	

        //////////////////////////////////////////////////////
        // Type Specific Things
        //////////////////////////////////////////////////////

        List modelTypes = elem.elements("model-type");
        for(int i = 0; i < modelTypes.size(); i++)
        {
            Element el = (Element) modelTypes.get(i);
            TypeDescriptor descriptor = new TypeDescriptor(el);
            configElement.types.put(descriptor.getId(), descriptor);
        }

        String _rootPath = elem.elementTextTrim("model-root-path");
        if(_rootPath != null)
        {
            configElement.rootPath = _rootPath;
        }

        
        //////////////////////////////////////////////////////
        // Content Loaders
        //////////////////////////////////////////////////////

        List loaders = elem.elements("content-loader");
        for(int i = 0; i < loaders.size(); i++)
        {
            Element el = (Element) loaders.get(i);
            ContentLoaderDescriptor descriptor = new ContentLoaderDescriptor(el);
            configElement.contentLoaders.put(descriptor.getId(), descriptor);
        }
        
        return configElement;
    }
}
