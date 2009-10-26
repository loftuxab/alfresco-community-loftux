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

/**
 * @author muzquiano
 */
public class WebFrameworkConstants
{
    // System Pages
    public static final String SYSTEM_PAGE_GETTING_STARTED = "page-getting-started";
    public static final String SYSTEM_PAGE_UNCONFIGURED = "page-unconfigured";
    public static final String SYSTEM_PAGE_CONTENT_NOT_LOADED = "page-content-not-loaded";
    public static final String SYSTEM_PAGE_CONTENT_ASSOCIATION_MISSING = "page-content-association-missing";
    public static final String DEFAULT_SYSTEM_PAGE_GETTING_STARTED = "/core/page-gettingstarted.jsp";
    public static final String DEFAULT_SYSTEM_PAGE_UNCONFIGURED = "/core/page-unconfigured.jsp";
    public static final String DEFAULT_SYSTEM_PAGE_CONTENT_NOT_LOADED = "/core/content-not-loaded.jsp";
    public static final String DEFAULT_SYSTEM_PAGE_CONTENT_ASSOCIATION_MISSING = "/core/content-association-missing.jsp";

    // Dispatcher Handlers
    public static final String DISPATCHER_HANDLER_PAGE_ERROR = "page-error";
    public static final String DISPATCHER_HANDLER_COMPONENT_ERROR = "component-error";
    public static final String DISPATCHER_HANDLER_TEMPLATE_ERROR = "template-error";
    public static final String DISPATCHER_HANDLER_REGION_ERROR = "region-error";
    public static final String DISPATCHER_HANDLER_REGION_NO_COMPONENT = "region-nocomponent";
    public static final String DEFAULT_DISPATCHER_HANDLER_PAGE_ERROR = "/core/page-error.jsp";
    public static final String DEFAULT_DISPATCHER_HANDLER_COMPONENT_ERROR = "/core/component-error.jsp";
    public static final String DEFAULT_DISPATCHER_HANDLER_TEMPLATE_ERROR = "/core/template-error.jsp";
    public static final String DEFAULT_DISPATCHER_HANDLER_REGION_ERROR = "/core/region-error.jsp";
    public static final String DEFAULT_DISPATCHER_HANDLER_REGION_NO_COMPONENT = "/core/region-nocomponent.jsp";
    
    // Region Scopes
    public static final String REGION_SCOPE_GLOBAL   = "global";
    public static final String REGION_SCOPE_TEMPLATE = "template";
    public static final String REGION_SCOPE_PAGE     = "page";
    public static final String REGION_SCOPE_URI      = "uri";
    public static final String REGION_SCOPE_THEME    = "theme";
        
    // Chromes
    public static final String DEFAULT_REGION_CHROME_ID = "default-region-chrome";
    public static final String CHROMELESS_REGION_CHROME_ID = "chromeless-region-chrome";
    public static final String DEFAULT_COMPONENT_CHROME_ID = "default-component-chrome";
    public static final String DEBUG_COMPONENT_CHROME_ID = "debug-component-chrome";
    
    // Misc
    public static final String DEFAULT_ALFRESCO_ENDPOINT_ID = "alfresco";

    // Renderer Context Parameters (Page)
    public static final String RENDER_DATA_PAGE_ID = "page-id";
    public static final String RENDER_DATA_PAGE_TYPE_ID = "page-type-id";
    public static final String RENDER_DATA_TEMPLATE_ID = "template-id";
    public static final String RENDER_DATA_TEMPLATE_TYPE_ID = "template-type-id";
    public static final String RENDER_DATA_REGION_ID = "region-id";
    public static final String RENDER_DATA_REGION_SCOPE_ID = "region-scope-id";
    public static final String RENDER_DATA_REGION_SOURCE_ID = "region-source-id";
    public static final String RENDER_DATA_REGION_CHROME_ID = "region-chrome-id";
    public static final String RENDER_DATA_COMPONENT_ID = "component-id";
    public static final String RENDER_DATA_COMPONENT_TYPE_ID = "component-type-id";
    public static final String RENDER_DATA_COMPONENT_REGION_ID = "component-region-id";
    public static final String RENDER_DATA_COMPONENT_SOURCE_ID = "component-source-id";
    public static final String RENDER_DATA_COMPONENT_SCOPE_ID = "component-scope-id";
    public static final String RENDER_DATA_COMPONENT_CHROME_ID = "component-chrome-id";
    
    public static final String RENDER_DATA_HTMLID = "htmlid";
    public static final String RENDER_DATA_REQUEST_CONTEXT_STACK_KEY = "configuration-stack";
    
    // Model Persistence Store ID key name
    public static final String WEBAPP_ID_REQUEST_PARAM_NAME = "alfWebappId";
    public static final String WEBAPP_ID_SESSION_ATTRIBUTE_NAME = "alfWebappId";
    
    public static final String STORE_ID_REQUEST_PARAM_NAME = "alfStoreId";    
    public static final String STORE_ID_SESSION_ATTRIBUTE_NAME = "alfStoreId";
    
    // Request Context Environment
    public static final String STORE_ID_REQUEST_CONTEXT_NAME = "alfStoreId";
    public static final String WEBAPP_ID_REQUEST_CONTEXT_NAME = "alfWebappId";
    
    // Web Studio Signature
    public static final String WEB_STUDIO_SIGNATURE = "<!-- Include Alfresco Web Studio -->";
    
    // Theme
    public static final String DEFAULT_THEME_ID = "default";
    
    // Page Types
    public static final String GENERIC_PAGE_TYPE_DEFAULT_PAGE_ID = "generic";
    
    // Processor Types
    public static final String PROCESSOR_JSP = "jsp";
    public static final String PROCESSOR_FREEMARKER = "freemarker";
    public static final String PROCESSOR_WEBSCRIPT = "webscript";
    public static final String DEFAULT_PROCESSOR = "jsp";
    
    // Render context values
    public static final String STYLESHEET_RENDER_CONTEXT_NAME = "alfStylesheet"; 
}