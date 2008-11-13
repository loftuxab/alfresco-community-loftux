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
package org.alfresco.web.site.taglib;

import javax.servlet.jsp.JspException;

import org.alfresco.web.framework.render.PresentationUtil;
import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.framework.render.RenderFocus;
import org.alfresco.web.site.WebFrameworkConstants;

/**
 * @author muzquiano
 */
public class ComponentTag extends TagBase
{
    private String component = null;
    private String chrome = null;
    private boolean chromeless = false;

    public void setComponent(String componentId)
    {
        this.component = componentId;
    }
    
    public String getComponent()
    {
        return this.component;
    }
    
    public void setChrome(String chrome)
    {
        this.chrome = chrome;
    }

    public String getChrome()
    {
        return this.chrome;
    }
    
    public boolean isChromeless()
    {
        return chromeless;
    }

    public void setChromeless(boolean chromeless)
    {
        this.chromeless = chromeless;
    }
    
    

    public int doStartTag() throws JspException
    {
        RenderContext context = getRenderContext();
        
        String componentId = this.component;
        
        // If we were not supplied with a component id, try to infer it
        // this will apply if we're rendering in a region
        if (componentId == null)
        {
            componentId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_COMPONENT_ID);
        }
        
        // now render the component
        try
        {
            // get overridden chrome id
            String chromeId = getChrome();
            boolean isChromeless = isChromeless();

            if (!isChromeless)
            {
                PresentationUtil.renderComponent(context, RenderFocus.BODY, componentId, chromeId);
            }
            else
            {
                PresentationUtil.renderChromelessComponent(context, RenderFocus.BODY, componentId);
            }
        }
        catch (Throwable t)
        {
            throw new JspException(t);
        }
        return SKIP_BODY;
    }
    
    public void release()
    {
        this.component = null;
        this.chrome = null;
        this.chromeless = false;
        
        super.release();
    }
    
}
