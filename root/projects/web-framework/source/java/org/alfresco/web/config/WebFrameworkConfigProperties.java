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

import org.alfresco.web.config.WebFrameworkConfigElement.ErrorHandlerDescriptor;
import org.alfresco.web.config.WebFrameworkConfigElement.FileSystemDescriptor;
import org.alfresco.web.config.WebFrameworkConfigElement.FormatDescriptor;
import org.alfresco.web.config.WebFrameworkConfigElement.LinkBuilderDescriptor;
import org.alfresco.web.config.WebFrameworkConfigElement.PageMapperDescriptor;
import org.alfresco.web.config.WebFrameworkConfigElement.RendererDescriptor;
import org.alfresco.web.config.WebFrameworkConfigElement.RequestContextDescriptor;
import org.alfresco.web.config.WebFrameworkConfigElement.SystemPageDescriptor;
import org.alfresco.web.config.WebFrameworkConfigElement.TagLibraryDescriptor;
import org.alfresco.web.config.WebFrameworkConfigElement.TypeDescriptor;
import org.alfresco.web.config.WebFrameworkConfigElement.UserFactoryDescriptor;

/**
 * @author muzquiano
 */
public interface WebFrameworkConfigProperties
{
	// formats
    public String[] getFormatIds();
    public FormatDescriptor getFormatDescriptor(String id);
    
    // page mappers
    public String[] getPageMapperIds();
    public PageMapperDescriptor getPageMapperDescriptor(String id);

    // link builders
    public String[] getLinkBuilderIds();
    public LinkBuilderDescriptor getLinkBuilderDescriptor(String id);

    // request contexts
    public String[] getRequestContextIds();
    public RequestContextDescriptor getRequestContextDescriptor(String id);

    // error handlers
    public String[] getErrorHandlerIds();
    public ErrorHandlerDescriptor getErrorHandlerDescriptor(String id);
    
    // system pages
    public String[] getSystemPageIds();
    public SystemPageDescriptor getSystemPageDescriptor(String id);

    // file systems
    public String[] getFileSystemIds();
    public FileSystemDescriptor getFileSystemDescriptor(String id);
    
    // tag libraries
    public String[] getTagLibraryIds();
    public TagLibraryDescriptor getTagLibraryDescriptor(String id);

    // user factories
    public String[] getUserFactoryIds();
    public UserFactoryDescriptor getUserFactoryDescriptor(String id);
    
    // renderers
    public String[] getRendererIds();
    public RendererDescriptor getRendererDescriptor(String id);

    // debug
    public boolean isTimerEnabled();

    // application defaults
    public String getDefaultRegionChrome();
    public String getDefaultComponentChrome();
    public String[] getDefaultPageTypeIds();
    public String getDefaultPageTypeInstanceId(String id);
    public String getDefaultThemeId();
    
    // framework defaults
    public String getDefaultFormatId();
    public String getDefaultPageMapperId();
    public String getDefaultLinkBuilderId();
    public String getDefaultRequestContextId();
    public String getDefaultUserFactoryId();
    public String getDefaultFileSystemId();
    
    // types (model files)
    public String[] getTypeIds();
    public TypeDescriptor getTypeDescriptor(String id);
    public String getRootPath();
    

/*
    public String getDefaultServletUri();
    public String getDynamicWebsiteServletUri();
*/
}
