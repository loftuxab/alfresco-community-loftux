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
package org.alfresco.web.studio.bean;

import java.io.IOException;
import java.io.PrintWriter;

import org.alfresco.web.framework.model.Component;
import org.alfresco.web.framework.model.ComponentType;
import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.framework.render.bean.ComponentRenderer;
import org.alfresco.web.scripts.Registry;
import org.alfresco.web.scripts.WebScript;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.WebFrameworkConstants;

/**
 * Provides Web-Studio extensions to component rendering
 * 
 * Primarily, this enables components to output additional
 * Web Studio specific JavaScript to bind client-side DOM elements
 * together.
 * 
 * @author muzquiano
 */
public class WebStudioComponentRenderer extends ComponentRenderer
{
	private static final String WEBSCRIPTS_REGISTRY = "webframework.webscripts.registry";
	
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.bean.RegionRenderer#postProcess(org.alfresco.web.framework.render.RenderContext)
     */
    public void postProcess(RenderContext context)
    	throws IOException
    {
        // if web studio is enabled (and not passive mode)
        if(FrameworkHelper.getConfig().isWebStudioEnabled() && !context.isPassiveMode())
        {
			// html binding id
			String htmlId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_HTMLID);

	        // component and component type properties
	        String componentId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_COMPONENT_ID);
	        String componentTypeId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_COMPONENT_TYPE_ID);
	        	
	        // commit to output
			PrintWriter writer = context.getResponse().getWriter();
			writer.println("<script language='Javascript'>");
	        
			// if there is a component, bind that in too
			if(componentId != null && componentTypeId != null)
			{
				String componentTitle = "";
				String componentTypeTitle = "";
				String editorUrl = "";
				Component c = (Component) context.getModel().getComponent(componentId);
				if(c != null)
				{
					componentTitle = getComponentTitle(context, c);
					componentTypeTitle = getComponentTypeTitle(context, c);				
					editorUrl = getComponentEditorUrl(context, c);
				}
				writer.println("WebStudio.configureComponent('" + htmlId + "', '" + componentId + "', '" + componentTypeId + "', '" + componentTitle + "', '" + componentTypeTitle + "', '" + editorUrl + "');");
			}

			writer.println("</script>");		
			writer.flush();
        }
    }
	
	public static String getComponentTitle(RequestContext context, Component c)
	{
		String title = c.getTitle();
		if(title == null)
		{
			title = c.getId();
		}
		
		return title;
	}
		
	public static String getComponentTypeTitle(RequestContext context, Component c)
	{
		String title = null;
		
		ComponentType ct = c.getComponentType(context);
		if(ct != null)
		{
			title = ct.getTitle();
			if(title == null)
			{
				title = ct.getId();
			}
			
			if("webscript".equals(ct.getId()))
			{
				String url = c.getURL();
				
				Registry registry = (Registry)FrameworkHelper.getApplicationContext().getBean(WEBSCRIPTS_REGISTRY);
				WebScript webScript = registry.getWebScript(url);
				if(webScript != null)
				{
					title = webScript.getDescription().getShortName();
				}
			}
		}
		else
		{
			// assume the component type field is a web script id
			String url = c.getComponentTypeId();
			
			Registry registry = (Registry)FrameworkHelper.getApplicationContext().getBean(WEBSCRIPTS_REGISTRY);
			
            for (WebScript webscript : registry.getWebScripts())
            {
            	String[] uris = webscript.getDescription().getURIs();
            	for(int z = 0; z < uris.length; z++)
            	{
            		String uri = uris[z];
            		if(uri.equals(url))
            		{
            			title = webscript.getDescription().getShortName();
            		}
            	}
            }
		}
		
		return title;			
	}
	
	public static String getComponentEditorUrl(RequestContext context, Component c)
	{
		String url = null;
		
		ComponentType ct = c.getComponentType(context);
		if(ct != null)
		{
			if("webscript".equals(ct.getId()))
			{
				url = "/c/edit/" + c.getId();
			}
		}
		else
		{
			// assume it is a web script
			url = "/c/edit/" + c.getId();
		}
		
		return url;		
	}
    
    
}
