/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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

import java.util.LinkedList;
import javax.servlet.jsp.JspException;

import org.alfresco.web.framework.render.RenderContext;
import org.alfresco.web.site.WebFrameworkConstants;

/**
 * The "link" tag is provided so that multiple CSS resources requested by a component
 * can be batched up into a single "style" tag with multiple @import statements.
 * This mechanism is to workaround the MSIE bug described in KB262161 whereby IE browsers
 * will not parse more than 30 separate CSS resource tags.
 * 
 * @author mikeh
 */
public class StylesheetTag extends TagBase
{
    private String rel = null;
    private String type = null;
    private String href = null;

    private static final long serialVersionUID = -2372542871999800148L;

    public void setRel(String rel)
    {
        this.rel = rel;
    }

    public String getRel()
    {
        return this.rel;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    public void setHref(String href)
    {
        this.href = href;
    }

    public String getHref()
    {
        return href;
    }

    @SuppressWarnings("unchecked")
    public int doStartTag() throws JspException
    {
        RenderContext context = getRenderContext();

        try
        {
            LinkedList<String> css = null;
            if (context.hasValue(WebFrameworkConstants.STYLESHEET_RENDER_CONTEXT_NAME))
            {
                css = (LinkedList<String>)context.getValue(WebFrameworkConstants.STYLESHEET_RENDER_CONTEXT_NAME);
            }
            else
            {
                css = new LinkedList<String>();
                context.setValue(WebFrameworkConstants.STYLESHEET_RENDER_CONTEXT_NAME, css);
            }
            css.add(this.href);
        }
        catch (Throwable t)
        {
            throw new JspException(t);
        }
        return SKIP_BODY;
    }
    
    public void release()
    {
        this.rel = null;
        this.setType(null);
        this.setHref(null);
        
        super.release();
    }
}
