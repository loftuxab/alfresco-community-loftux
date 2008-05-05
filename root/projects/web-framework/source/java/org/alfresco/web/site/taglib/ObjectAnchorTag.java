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

import org.alfresco.web.site.RequestContext;

/**
 * @author muzquiano
 */
public class ObjectAnchorTag extends AbstractObjectTag
{
    private String formatId = null;
    private String target = null;

    public void setFormat(String formatId)
    {
        this.formatId = formatId;
    }

    public String getFormat()
    {
        return this.formatId;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public String getTarget()
    {
        return this.target;
    }

    public int doStartTag() throws JspException
    {
        RequestContext context = getRequestContext();

        // generate the URL
        String url = link(context, getPageType(), getPage(), getObject(), getFormat());

        try
        {
            this.getOut().write("<A href=\"" + url + "\"");
            if (getTarget() != null)
                this.getOut().write(" target=\"" + getTarget() + "\"");
            this.getOut().write(">");
        }
        catch (Exception ex)
        {
            throw new JspException(ex);
        }
        return this.EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException
    {
        try
        {
            this.getOut().write("</A>");
        }
        catch (Exception ex)
        {
            throw new JspException(ex);
        }
        return EVAL_PAGE;
    }
    
    public void release()
    {
        this.formatId = null;
        this.target = null;
        
        super.release();
    }
    
}
