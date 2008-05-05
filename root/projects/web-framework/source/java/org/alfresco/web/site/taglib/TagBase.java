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

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.RequestUtil;
import org.alfresco.web.site.exception.RequestContextException;

/**
 * @author muzquiano
 */
public abstract class TagBase extends BodyTagSupport implements Serializable
{
    private PageContext pageContext = null;

    public void setPageContext(PageContext pageContext)
    {
        this.pageContext = pageContext;
        TagSupport a;
    }

    public int doEndTag() throws JspException
    {
        return EVAL_PAGE;
    }

    protected PageContext getPageContext()
    {
        return this.pageContext;
    }

    protected RequestContext getRequestContext()
        throws JspException
    {
        RequestContext context = null;
    
        HttpServletRequest request = (HttpServletRequest) getPageContext().getRequest();
        try 
        {
            context = RequestUtil.getRequestContext(request);
        }
        catch(RequestContextException rce)
        {
            throw new JspException("Unable to retrieve request context from request", rce);
        }
        
        return context;
    }

    protected JspWriter getOut()
    {
        return getPageContext().getOut();
    }

    protected void print(String str)
        throws JspException
    {
        try
        {
            getOut().print(str);
        }
        catch (Exception ex)
        {
            throw new JspException(ex);
        }
    }
    
    public void release()
    {
        this.pageContext = null;
        super.release();
    }
    
}
