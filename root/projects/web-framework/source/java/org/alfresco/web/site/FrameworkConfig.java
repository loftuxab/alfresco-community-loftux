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
 * Web Framework Configuratation
 * 
 * This interface defines all of the methods that must be available 
 * to the web framework in order to work.  This interface exists so that
 * configuration implementations can be swapped in and out.
 * 
 * @author muzquiano
 */
public interface FrameworkConfig
{
    public boolean isInitialized();

    
    //////////////////////////////////////////////////////////////////
    // Formats
    //////////////////////////////////////////////////////////////////	

    public String[] getFormatIds();

    public String getFormatName(String id);

    public String getFormatDescription(String id);

    
    //////////////////////////////////////////////////////////////////
    // Page Mapper
    //////////////////////////////////////////////////////////////////

    public String[] getPageMapperIds();

    public abstract String getPageMapperName(String id);

    public abstract String getPageMapperDescription(String id);

    public abstract String getPageMapperClass(String id);

    
    //////////////////////////////////////////////////////////////////
    // Link Builder
    //////////////////////////////////////////////////////////////////

    public String[] getLinkBuilderIds();

    public String getLinkBuilderName(String id);

    public String getLinkBuilderDescription(String id);

    public String getLinkBuilderClass(String id);

    
    //////////////////////////////////////////////////////////////////
    // Request Context
    //////////////////////////////////////////////////////////////////

    public String[] getRequestContextIds();

    public String getRequestContextName(String id);

    public String getRequestContextDescription(String id);

    public String getRequestContextClass(String id);

    public String getRequestContextFactoryClass();

    public String getRequestContextSetting(String id, String settingKey);

    
    //////////////////////////////////////////////////////////////////
    // Servlet and Dispatcher Information
    //////////////////////////////////////////////////////////////////

    public String getDefaultServletUri();

    public String getDynamicWebsiteServletUri();


    //////////////////////////////////////////////////////////////////
    // Dispatcher
    //////////////////////////////////////////////////////////////////

    public String[] getDispatcherErrorHandlerIds();
    
    public String getDispatcherErrorHandlerRenderer(String id);

    public String getDispatcherErrorHandlerRendererType(String id);
    
    public String getDispatcherErrorHandlerProperty(String id, String propertyId);
    
    public String[] getDispatcherSystemPageIds();
    
    public String getDispatcherSystemPageRenderer(String id);

    public String getDispatcherSystemPageRendererType(String id);
    
    public String getDispatcherSystemPageProperty(String id, String propertyId);

    
    //////////////////////////////////////////////////////////////////
    // File Systems
    //////////////////////////////////////////////////////////////////

    public String[] getFileSystemIds();

    public String getFileSystemName(String id);

    public String getFileSystemClass(String id);

    public String getFileSystemUseCache(String id);

    public String getFileSystemRootPath(String id);

    
    //////////////////////////////////////////////////////////////////
    // In-Context
    //////////////////////////////////////////////////////////////////

    public boolean isInContextEnabled();

    public String[] getInContextElementIds();

    public String getInContextElementName(String id);

    public String getInContextElementType(String id);

    public String getInContextElementDefaultEnabled(String id);

    public String getInContextElementDefaultState(String id);

    
    //////////////////////////////////////////////////////////////////
    // Model Types
    //////////////////////////////////////////////////////////////////

    public String[] getModelTypeIds();

    public String getModelTypeName(String id);

    public String getModelTypeDescription(String id);

    public String getModelTypeTagName(String id);
    
    public String getModelTypeNamespace(String id);

    public String getModelTypeClass(String id);

    public String getModelTypePath(String id);

    public String getModelTypePrefix(String id);
    
    public String getModelTypeProperty(String id, String propertyName);
    
    public String getModelTypeVersion(String id);

    public String getModelRootPath();    
    

    //////////////////////////////////////////////////////////////////
    // Tag Libraries
    //////////////////////////////////////////////////////////////////

    public String[] getTagLibraryIds();

    public String getTagLibraryUri(String id);

    public String getTagLibraryNamespace(String id);

    
    //////////////////////////////////////////////////////////////////
    // User Identity 
    //////////////////////////////////////////////////////////////////

    public String[] getUserFactoryIds();

    public String getUserFactoryName(String id);

    public String getUserFactoryDescription(String id);

    public String getUserFactoryClass(String id);

    public String getUserFactoryProperty(String id, String property);

    
    //////////////////////////////////////////////////////////////////
    // Remote
    //////////////////////////////////////////////////////////////////

    public String[] getRemoteConnectorIds();

    public String getRemoteConnectorName(String id);

    public String getRemoteConnectorDescription(String id);

    public String getRemoteConnectorClass(String id);

    public String getRemoteConnectorProperty(String id, String property);

    public String[] getRemoteAuthenticatorIds();

    public String getRemoteAuthenticatorName(String id);

    public String getRemoteAuthenticatorDescription(String id);

    public String getRemoteAuthenticatorClass(String id);

    public String getRemoteAuthenticatorProperty(String id, String property);

    
    //////////////////////////////////////////////////////////////////
    // Renderers
    //////////////////////////////////////////////////////////////////

    public String[] getRendererIds();

    public String getRendererName(String id);

    public String getRendererDescription(String id);

    public String getRendererClass(String id);

    public String getRendererProperty(String id, String property);

    
    //////////////////////////////////////////////////////////////////
    // Application Default Settings
    //////////////////////////////////////////////////////////////////
        
    public String getDefaultRegionChrome();

    public String getDefaultComponentChrome();

    public String[] getDefaultPageTypeIds();

    public String getDefaultPageTypeInstanceId(String id);

    
    //////////////////////////////////////////////////////////////////
    // Defaults
    //////////////////////////////////////////////////////////////////
    
    public String getDefaultFormatId();
    
    public String getDefaultPageMapperId();

    public String getDefaultLinkBuilderId();
    
    public String getDefaultRequestContextId();

    public String getDefaultUserFactoryId();
    
    public String getDefaultThemeId();
    
    public String getDefaultProperty(String id);
    
    
    //////////////////////////////////////////////////////////////////
    // Debug
    //////////////////////////////////////////////////////////////////
    
    public boolean getDebugTimerEnabled();
}
