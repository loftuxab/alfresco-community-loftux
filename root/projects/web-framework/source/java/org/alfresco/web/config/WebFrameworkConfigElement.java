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
import java.util.Map;

import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.element.ConfigElementAdapter;
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
    protected HashMap<String, TagLibraryDescriptor> tagLibraries = null;
    protected HashMap<String, UserFactoryDescriptor> userFactories = null;
    protected HashMap<String, String> pageTypes = null;
    protected HashMap<String, TypeDescriptor> types = null;
    protected HashMap<String, ResourceLoaderDescriptor> resourceLoaders = null;
    protected HashMap<String, ResourceResolverDescriptor> resourceResolvers = null;

    protected boolean isTimerEnabled = false;
    
    protected String defaultFormatId = null;
    protected String defaultPageMapperId = null;
    protected String defaultLinkBuilderId = null;
    protected String defaultRequestContextId = null;
    protected String defaultUserFactoryId = null;

    protected String defaultRegionChrome = null;
    protected String defaultComponentChrome = null;
    protected String defaultTheme = null;
    protected String defaultSiteConfiguration = null;
    
    protected Integer persisterCacheCheckDelay = null;
    protected Boolean persisterCacheEnabled = null;

    protected String rootPath;
    
    protected String webStudioMode = null;
    protected String webStudioLocation = null;
    protected boolean webStudioEnabled = false;
    
    protected String previewMode;
    protected String previewDefaultStoreId = null;
    protected String previewDefaultWebappId = null;
    protected boolean previewEnabled = false;
    

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
        tagLibraries = new HashMap<String, TagLibraryDescriptor>();
        userFactories = new HashMap<String, UserFactoryDescriptor>();
        pageTypes = new HashMap<String, String>();
        types = new HashMap<String, TypeDescriptor>();
        resourceLoaders = new HashMap<String, ResourceLoaderDescriptor>();
        resourceResolvers = new HashMap<String, ResourceResolverDescriptor>();

        isTimerEnabled = false;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.config.element.GenericConfigElement#combine(org.alfresco.config.ConfigElement)
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
        combinedElement.tagLibraries.putAll(this.tagLibraries);
        combinedElement.userFactories.putAll(this.userFactories);
        combinedElement.types.putAll(this.types);
        combinedElement.resourceLoaders.putAll(this.resourceLoaders);
        combinedElement.resourceResolvers.putAll(this.resourceResolvers);
        combinedElement.pageTypes.putAll(this.pageTypes);

        // override with things from the merging object
        combinedElement.formats.putAll(configElement.formats);
        combinedElement.pageMappers.putAll(configElement.pageMappers);
        combinedElement.linkBuilders.putAll(configElement.linkBuilders);        
        combinedElement.requestContexts.putAll(configElement.requestContexts);
        combinedElement.errorHandlers.putAll(configElement.errorHandlers);
        combinedElement.systemPages.putAll(configElement.systemPages);
        combinedElement.tagLibraries.putAll(configElement.tagLibraries);
        combinedElement.userFactories.putAll(configElement.userFactories);
        combinedElement.types.putAll(configElement.types);
        combinedElement.resourceLoaders.putAll(configElement.resourceLoaders);
        combinedElement.resourceResolvers.putAll(configElement.resourceResolvers);
        combinedElement.pageTypes.putAll(configElement.pageTypes);

        // other properties
        combinedElement.isTimerEnabled = this.isTimerEnabled;
        if (configElement.isTimerEnabled)
        {
            combinedElement.isTimerEnabled = configElement.isTimerEnabled;
        }
        combinedElement.defaultFormatId = this.defaultFormatId;
        if (configElement.defaultFormatId != null)
        {
            combinedElement.defaultFormatId = configElement.defaultFormatId;
        }
        combinedElement.defaultPageMapperId = this.defaultPageMapperId;
        if (configElement.defaultPageMapperId != null)
        {
            combinedElement.defaultPageMapperId = configElement.defaultPageMapperId;
        }
        combinedElement.defaultLinkBuilderId = this.defaultLinkBuilderId;
        if (configElement.defaultLinkBuilderId != null)
        {
            combinedElement.defaultLinkBuilderId = configElement.defaultLinkBuilderId;
        }
        combinedElement.defaultRequestContextId = this.defaultRequestContextId;
        if (configElement.defaultRequestContextId != null)
        {
            combinedElement.defaultRequestContextId = configElement.defaultRequestContextId;
        }
        combinedElement.defaultUserFactoryId = this.defaultUserFactoryId;
        if (configElement.defaultUserFactoryId != null)
        {
            combinedElement.defaultUserFactoryId = configElement.defaultUserFactoryId;
        }

        combinedElement.defaultRegionChrome = this.defaultRegionChrome;
        if (configElement.defaultRegionChrome != null)
        {
            combinedElement.defaultRegionChrome = configElement.defaultRegionChrome;
        }
        combinedElement.defaultComponentChrome = this.defaultComponentChrome;
        if (configElement.defaultComponentChrome != null)
        {
            combinedElement.defaultComponentChrome = configElement.defaultComponentChrome;
        }
        combinedElement.defaultTheme = this.defaultTheme;
        if (configElement.defaultTheme != null)
        {
            combinedElement.defaultTheme = configElement.defaultTheme;
        }

        combinedElement.rootPath = this.rootPath;
        if (configElement.rootPath != null)
        {
            combinedElement.rootPath = configElement.rootPath;
        }
        
        combinedElement.defaultSiteConfiguration = this.defaultSiteConfiguration;
        if (configElement.defaultSiteConfiguration != null)
        {
            combinedElement.defaultSiteConfiguration = configElement.defaultSiteConfiguration;
        }
        
        combinedElement.persisterCacheCheckDelay = this.persisterCacheCheckDelay;
        if (configElement.persisterCacheCheckDelay != null)
        {
            combinedElement.persisterCacheCheckDelay = configElement.persisterCacheCheckDelay;
        }
        
        combinedElement.persisterCacheEnabled = this.persisterCacheEnabled;
        if (configElement.persisterCacheEnabled != null)
        {
            combinedElement.persisterCacheEnabled = configElement.persisterCacheEnabled;
        }
        
        
        combinedElement.webStudioMode = this.webStudioMode;
        if (configElement.webStudioMode != null)
        {
            combinedElement.webStudioMode = configElement.webStudioMode;
        }
        combinedElement.webStudioLocation = this.webStudioLocation;
        if (configElement.webStudioLocation != null)
        {
            combinedElement.webStudioLocation = configElement.webStudioLocation;
        }
        combinedElement.webStudioEnabled = this.webStudioEnabled;
        if (configElement.webStudioEnabled)
        {
            combinedElement.webStudioEnabled = configElement.webStudioEnabled;
        }


        combinedElement.previewMode = this.previewMode;
        if (configElement.previewMode != null)
        {
            combinedElement.previewMode = configElement.previewMode;
        }
        combinedElement.previewEnabled = this.previewEnabled;
        if (configElement.previewEnabled)
        {
            combinedElement.previewEnabled = configElement.previewEnabled;
        }
        combinedElement.previewDefaultStoreId = this.previewDefaultStoreId;
        if (configElement.previewDefaultStoreId != null)
        {
            combinedElement.previewDefaultStoreId = configElement.previewDefaultStoreId;
        }
        combinedElement.previewDefaultWebappId = this.previewDefaultWebappId;
        if (configElement.previewDefaultWebappId != null)
        {
            combinedElement.previewDefaultWebappId = configElement.previewDefaultWebappId;
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
        if (rootPath == null)
        {
            return "/WEB-INF/classes/alfresco";
        }
        return rootPath;
    }
    
    // resource loaders
    public String[] getResourceLoaderIds()
    {
        return this.resourceLoaders.keySet().toArray(new String[this.resourceLoaders.size()]);
    }
    
    public ResourceLoaderDescriptor getResourceLoaderDescriptor(String id)
    {
        return (ResourceLoaderDescriptor) this.resourceLoaders.get(id);        
    }

    // resource resolvers
    public String[] getResourceResolverIds()
    {
        return this.resourceResolvers.keySet().toArray(new String[this.resourceResolvers.size()]);
    }
    
    public ResourceResolverDescriptor getResourceResolverDescriptor(String id)
    {
        return (ResourceResolverDescriptor) this.resourceResolvers.get(id);        
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
        if (this.defaultTheme == null)
        {
            return "default";
        }
        return this.defaultTheme;
    }
    
    public String getDefaultSiteConfigurationId()
    {
        if (this.defaultSiteConfiguration == null)
        {
            return "default.site.configuration";
        }
        return this.defaultSiteConfiguration;
    }

    // framework defaults
    public String getDefaultFormatId()
    {
        if (this.defaultFormatId == null)
        {
            return "default";
        }
        return this.defaultFormatId;
    }
    
    public String getDefaultPageMapperId()
    {
        if (this.defaultPageMapperId == null)
        {
            return "default";
        }
        return this.defaultPageMapperId;
    }
    
    public String getDefaultLinkBuilderId()
    {
        if (this.defaultLinkBuilderId == null)
        {
            return "default";
        }
        return this.defaultLinkBuilderId;
    }
    
    public String getDefaultRequestContextId()
    {
        if (this.defaultRequestContextId == null)
        {
            return "http";
        }
        return this.defaultRequestContextId;
    }
    
    public String getDefaultUserFactoryId()
    {
        if (this.defaultUserFactoryId == null)
        {
            return "default";
        }
        return this.defaultUserFactoryId;
    }
        
    public int getPersisterCacheCheckDelay()
    {
        if (persisterCacheCheckDelay != null)
        {
            return persisterCacheCheckDelay.intValue();
        }
        else
        {
            return 0;
        }
    }
    
    public boolean getPersisterCacheEnabled()
    {
        if (persisterCacheEnabled != null)
        {
            return persisterCacheEnabled.booleanValue();
        }
        else
        {
            return true;
        }
    }
    
    public String getWebStudioMode()
    {
        return this.webStudioMode;
    }
    
    public String getWebStudioLocation()
    {
        return this.webStudioLocation;
    }
    
    public boolean isWebStudioEnabled()
    {
        return this.webStudioEnabled;        
    }
    

    public String getPreviewMode()
    {
        return this.previewMode;
    }
    
    public boolean isPreviewEnabled()
    {
        return this.previewEnabled;
    }
    
    public String getPreviewDefaultStoreId()
    {
        return this.previewDefaultStoreId;        
    }
    
    public String getPreviewDefaultWebappId()
    {
        return this.previewDefaultWebappId;
    }
    

    /**
     * Base for all Descriptor classes. Defines a basic get/put property bag
     * of descriptor info. Sub classes should provide typed and named getter/setters.
     */
    public static class Descriptor
    {
        private static final String ID = "id";

        HashMap<String, String> map;

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
            if (this.map == null)
            {
                this.map = new HashMap<String, String>();
            }

            String key = el.getName();
            String value = (String) el.getTextTrim();
            if (value != null)
            {
                this.map.put(key, value);
            }
        }

        public Object get(String key)
        {
            if (this.map == null)
            {
                this.map = new HashMap<String, String>();
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
        
        public Map<String, String> map()
        {
            return this.map;
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
        private static final String PROCESSOR_ID = "processor-id";
        private static final String JSP_PATH = "jsp-path";

        ErrorHandlerDescriptor(Element el)
        {
            super(el);
        }

        public String getJspPath() 
        {
            return getStringProperty(JSP_PATH);
        }
        public String getProcessorId() 
        {
            return getStringProperty(PROCESSOR_ID);
        }
    }

    public static class SystemPageDescriptor extends Descriptor
    {
        private static final String PROCESSOR_ID = "processor-id";
        private static final String JSP_PATH = "jsp-path";

        SystemPageDescriptor(Element el)
        {
            super(el);
        }

        public String getJspPath() 
        {
            return getStringProperty(JSP_PATH);
        }
        public String getProcessorId() 
        {
            return getStringProperty(PROCESSOR_ID);
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
    
    public static class TypeDescriptor extends Descriptor
    {
        private static final String CLAZZ = "class";
        private static final String NAMESPACE = "namespace";
        private static final String DESCRIPTION = "description";
        private static final String NAME = "name";
        private static final String VERSION = "version";
        private static final String SEARCH_PATH_ID = "search-path-id";
        private static final String DEFAULT_STORE_ID = "default-store-id";
        private static final String CACHE_ENABLED = "cache-enabled";
        private static final String CACHE_CHECK_DELAY = "cache-check-delay";

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
        public String getVersion() 
        {
            return getStringProperty(VERSION);
        }
        public String getSearchPathId() 
        {
            return getStringProperty(SEARCH_PATH_ID);
        }
        public String getDefaultStoreId() 
        {
            return getStringProperty(DEFAULT_STORE_ID);
        }
        public Boolean getCacheEnabled()
        {
            Boolean enabled = null;
            String value = getStringProperty(CACHE_ENABLED);
            if (value != null && value.length() != 0)
            {
                enabled = Boolean.parseBoolean(value);
            }
            return enabled;
        }
        public Integer getCacheCheckDelay()
        {
            Integer value = null;
            String v =  getStringProperty(CACHE_CHECK_DELAY);
            if (v != null && v.length() != 0)
            {
                value = Integer.valueOf(v);
            }
            return value;
        }
    }
    
    public static class ResourceLoaderDescriptor extends Descriptor
    {
        private static final String CLAZZ = "class";
        private static final String DESCRIPTION = "description";
        private static final String NAME = "name";
        private static final String ENDPOINT = "endpoint";

        ResourceLoaderDescriptor(Element el)
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

    public static class ResourceResolverDescriptor extends Descriptor
    {
        private static final String CLAZZ = "class";
        private static final String DESCRIPTION = "description";
        private static final String NAME = "name";
        private static final String ENDPOINT = "endpoint";

        ResourceResolverDescriptor(Element el)
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

        // framework defaults
        Element frameworkDefaults = elem.element("framework-defaults");
        if (frameworkDefaults != null)
        {
            String _requestContext = frameworkDefaults.elementTextTrim("request-context");
            if (_requestContext != null)
            {
                configElement.defaultRequestContextId = _requestContext;
            }
            String _format = frameworkDefaults.elementTextTrim("format");
            if (_format != null)
            {
                configElement.defaultFormatId = _format;
            }
            String _pageMapper = frameworkDefaults.elementTextTrim("page-mapper");
            if (_pageMapper != null)
            {
                configElement.defaultPageMapperId = _pageMapper;
            }
            String _linkBuilder = frameworkDefaults.elementTextTrim("link-builder");
            if (_linkBuilder != null)
            {
                configElement.defaultLinkBuilderId = _linkBuilder;
            }
            String _userFactory = frameworkDefaults.elementTextTrim("user-factory");
            if (_userFactory != null)
            {
                configElement.defaultUserFactoryId = _userFactory;
            }
        }

        // application defaults
        Element applicationDefaults = elem.element("application-defaults");
        if (applicationDefaults != null)
        {
            String _regionChrome = applicationDefaults.elementTextTrim("region-chrome");
            if (_regionChrome != null)
            {
                configElement.defaultRegionChrome = _regionChrome;
            }
            String _componentChrome = applicationDefaults.elementTextTrim("component-chrome");
            if (_componentChrome != null)
            {
                configElement.defaultComponentChrome = _componentChrome;
            }
            String _theme = applicationDefaults.elementTextTrim("theme");
            if (_theme != null && _theme.length() != 0)
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
            if (_siteConfiguration != null)
            {
                configElement.defaultSiteConfiguration = _siteConfiguration;
            }
        }

        //////////////////////////////////////////////////////
        // Debug Timer
        //////////////////////////////////////////////////////

        Element debugElement = elem.element("debug");
        if (debugElement != null)
        {
            String _isTimerEnabled = debugElement.elementTextTrim("timer");
            if (_isTimerEnabled != null)
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
        if (_rootPath != null)
        {
            configElement.rootPath = _rootPath;
        }

        
        //////////////////////////////////////////////////////
        // Resource Loaders
        //////////////////////////////////////////////////////

        List loaders = elem.elements("resource-loader");
        for(int i = 0; i < loaders.size(); i++)
        {
            Element el = (Element) loaders.get(i);
            ResourceLoaderDescriptor descriptor = new ResourceLoaderDescriptor(el);
            configElement.resourceLoaders.put(descriptor.getId(), descriptor);
        }

        
        //////////////////////////////////////////////////////
        // Resource Resolvers
        //////////////////////////////////////////////////////

        List resolvers = elem.elements("resource-resolver");
        for(int i = 0; i < resolvers.size(); i++)
        {
            Element el = (Element) resolvers.get(i);
            ResourceResolverDescriptor descriptor = new ResourceResolverDescriptor(el);
            configElement.resourceResolvers.put(descriptor.getId(), descriptor);
        }

        
        //////////////////////////////////////////////////////
        // Persisters
        //////////////////////////////////////////////////////
        
        Element persisterElement = elem.element("persisters");
        if (persisterElement != null)
        {
            if (persisterElement.element("cache-enabled") != null)
            {
                configElement.persisterCacheEnabled = Boolean.valueOf(
                        persisterElement.elementTextTrim("cache-enabled"));
            }
            if (persisterElement.element("cache-check-delay") != null)
            {
                configElement.persisterCacheCheckDelay = Integer.valueOf(
                        persisterElement.elementTextTrim("cache-check-delay"));
            }
        }
        
        
        //////////////////////////////////////////////////////
        // Web Studio
        //////////////////////////////////////////////////////

        Element webStudio = elem.element("web-studio");
        if (webStudio != null)
        {
            String _webStudioMode = webStudio.elementTextTrim("mode");
            if (_webStudioMode != null)
            {
                configElement.webStudioMode = _webStudioMode;
                if ("enabled".equalsIgnoreCase(_webStudioMode))
                {
                    configElement.webStudioEnabled = true;
                    
                    // turn on deployment preview mode as well
                    configElement.previewEnabled = true;
                }
            }
            String _webStudioLocation = webStudio.elementTextTrim("location");
            if (_webStudioLocation != null)
            {
                configElement.webStudioLocation = _webStudioLocation;
            }
        }

        
        
        //////////////////////////////////////////////////////
        // Preview Mode Configuration
        //////////////////////////////////////////////////////
        Element previewConfig = elem.element("preview");
        if (previewConfig != null)
        {
            String _previewMode = previewConfig.elementTextTrim("mode");
            if ("enabled".equalsIgnoreCase(_previewMode))
            {
                configElement.previewEnabled = true;
            }
            
            String _defaultStoreId = previewConfig.elementTextTrim("default-store-id");
            if (_defaultStoreId != null)
            {
                configElement.previewDefaultStoreId = _defaultStoreId;
            }
            String _defaultWebappId = previewConfig.elementTextTrim("default-webapp-id");
            if (_defaultWebappId != null)
            {
                configElement.previewDefaultWebappId = _defaultWebappId;
            }
        }
                
        return configElement;
    }    
}
