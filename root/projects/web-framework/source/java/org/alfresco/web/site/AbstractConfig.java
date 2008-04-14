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
public abstract class AbstractConfig
{
    public abstract void reset(RequestContext context);

    public abstract boolean isInitialized();

    //////////////////////////////////////////////////////////////////
    // Formats
    //////////////////////////////////////////////////////////////////	

    public abstract String getDefaultFormatId();

    public abstract String[] getFormatIds();

    public abstract String getFormatName(String id);

    public abstract String getFormatDescription(String id);

    //////////////////////////////////////////////////////////////////
    // Page Mapper
    //////////////////////////////////////////////////////////////////

    public abstract String getDefaultPageMapperId();

    public abstract String[] getPageMapperIds();

    public abstract String getPageMapperName(String id);

    public abstract String getPageMapperDescription(String id);

    public abstract String getPageMapperClass(String id);

    //////////////////////////////////////////////////////////////////
    // Link Builder
    //////////////////////////////////////////////////////////////////

    public abstract String getDefaultLinkBuilderId();

    public abstract String[] getLinkBuilderIds();

    public abstract String getLinkBuilderName(String id);

    public abstract String getLinkBuilderDescription(String id);

    public abstract String getLinkBuilderClass(String id);

    //////////////////////////////////////////////////////////////////
    // Request Context
    //////////////////////////////////////////////////////////////////

    public abstract String getDefaultRequestContextId();

    public abstract String[] getRequestContextIds();

    public abstract String getRequestContextName(String id);

    public abstract String getRequestContextDescription(String id);

    public abstract String getRequestContextClass(String id);

    public abstract String getRequestContextFactoryClass();

    public abstract String getRequestContextSetting(String id, String settingKey);

    //////////////////////////////////////////////////////////////////
    // Alfresco Authoring Server
    //////////////////////////////////////////////////////////////////

    public abstract String getAlfrescoAuthoringHost();

    public abstract String getAlfrescoAuthoringPort();

    public abstract String getAlfrescoAuthoringWebappUri();

    public abstract String getAlfrescoAuthoringWebscriptServiceUri();

    //////////////////////////////////////////////////////////////////
    // Servlet and Dispatcher Information
    //////////////////////////////////////////////////////////////////

    public abstract String getDefaultServletUri();

    public abstract String getDynamicWebsiteServletUri();
    
    public abstract String getDefaultPageUri();

    //////////////////////////////////////////////////////////////////
    // File Systems
    //////////////////////////////////////////////////////////////////

    public abstract String[] getFileSystemIds();

    public abstract String getFileSystemName(String id);

    public abstract String getFileSystemClass(String id);

    public abstract String getFileSystemUseCache(String id);

    public abstract String getFileSystemRootPath(String id);

    //////////////////////////////////////////////////////////////////
    // In-Context
    //////////////////////////////////////////////////////////////////

    public abstract boolean isInContextEnabled();

    public abstract String[] getInContextElementIds();

    public abstract String getInContextElementName(String id);

    public abstract String getInContextElementType(String id);

    public abstract String getInContextElementDefaultEnabled(String id);

    public abstract String getInContextElementDefaultState(String id);

    //////////////////////////////////////////////////////////////////
    // Model Types
    //////////////////////////////////////////////////////////////////

    public abstract String[] getModelTypeIds();

    public abstract String getModelTypeName(String id);

    public abstract String getModelTypeDescription(String id);

    public abstract String getModelTypeTagName(String id);

    public abstract String getModelTypeClass(String id);

    public abstract String getModelTypePath(String id);

    public abstract String getModelTypePrefix(String id);
    
    public abstract String getModelRootPath();

    //////////////////////////////////////////////////////////////////
    // Tag Libraries
    //////////////////////////////////////////////////////////////////

    public abstract String[] getTagLibraryIds();
    
    public abstract String getTagLibraryUri(String id);

    public abstract String getTagLibraryNamespace(String id);
    
    //////////////////////////////////////////////////////////////////
    // User Identity 
    //////////////////////////////////////////////////////////////////
    
    public abstract String getDefaultUserFactoryId();
    
    public abstract String[] getUserFactoryIds();

    public abstract String getUserFactoryName(String id);

    public abstract String getUserFactoryDescription(String id);

    public abstract String getUserFactoryClass(String id);
    
    public abstract String getUserFactoryProperty(String id, String property);

    
    //////////////////////////////////////////////////////////////////
    // Remote
    //////////////////////////////////////////////////////////////////
    
    public abstract String[] getRemoteConnectorIds();

    public abstract String getRemoteConnectorName(String id);

    public abstract String getRemoteConnectorDescription(String id);

    public abstract String getRemoteConnectorClass(String id);
    
    public abstract String getRemoteConnectorProperty(String id, String property);

    public abstract String[] getRemoteAuthenticatorIds();

    public abstract String getRemoteAuthenticatorName(String id);

    public abstract String getRemoteAuthenticatorDescription(String id);

    public abstract String getRemoteAuthenticatorClass(String id);
    
    public abstract String getRemoteAuthenticatorProperty(String id, String property);
    
    
    //////////////////////////////////////////////////////////////////
    // Renderers
    //////////////////////////////////////////////////////////////////
    
    public abstract String[] getRendererIds();

    public abstract String getRendererName(String id);

    public abstract String getRendererDescription(String id);

    public abstract String getRendererClass(String id);
    
    public abstract String getRendererProperty(String id, String property);
    
    
}
