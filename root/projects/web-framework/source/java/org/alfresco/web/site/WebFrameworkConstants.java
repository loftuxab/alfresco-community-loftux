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
    public final static String PRESENTATION_PAGE_GETTING_STARTED = "getting-started";
    public final static String PRESENTATION_PAGE_UNCONFIGURED = "page-unconfigured";
    public final static String PRESENTATION_PAGE_RENDER_ERROR = "page-error";

    public final static String PRESENTATION_CONTAINER_REGION = "region-container";
    public final static String PRESENTATION_CONTAINER_REGION_NO_COMPONENT = "region-nocomponent";
    public final static String PRESENTATION_CONTAINER_COMPONENT_RENDER_ERROR = "component-error";
    public final static String PRESENTATION_CONTAINER_TEMPLATE_RENDER_ERROR = "template-error";
    public final static String PRESENTATION_CONTAINER_REGION_RENDER_ERROR = "region-error";
    
    // defaults
    
    public final static String DEFAULT_PAGE_URI_GETTING_STARTED = "/core/page-gettingstarted.jsp";
    public final static String DEFAULT_PAGE_URI_UNCONFIGURED = "/core/page-unconfigured.jsp";
    public final static String DEFAULT_PAGE_URI_RENDER_ERROR = "/core/page-error";
    public final static String DEFAULT_CONTAINER_URI_REGION = "/core/region.jsp";
    public final static String DEFAULT_CONTAINER_URI_REGION_NO_COMPONENT = "/core/region-nocomponent.jsp";
    public final static String DEFAULT_CONTAINER_URI_REGION_RENDER_ERROR = "/core/region-error.jsp";
    public final static String DEFAULT_CONTAINER_URI_COMPONENT_RENDER_ERROR = "/core/component-error.jsp";
    public final static String DEFAULT_CONTAINER_URI_TEMPLATE_RENDER_ERROR = "/core/template-error.jsp";
    

}
