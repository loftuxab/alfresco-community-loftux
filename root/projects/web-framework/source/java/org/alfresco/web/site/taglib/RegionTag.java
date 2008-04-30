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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.alfresco.web.site.PresentationUtil;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.WebFrameworkConstants;
import org.alfresco.web.site.model.Chrome;

/**
 * @author muzquiano
 */
public class RegionTag extends TagBase
{
    private String name = null;
    private String scope = null;
    private String access = null;
    private String chrome = null;
    private boolean chromeless = false;

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public void setScope(String scope)
    {
        this.scope = scope;
    }

    public String getScope()
    {
        if (this.scope == null)
        {
            this.scope = WebFrameworkConstants.REGION_SCOPE_GLOBAL;
        }
        return this.scope;
    }

    public void setAccess(String access)
    {
        this.access = access;
    }

    public String getAccess()
    {
        return this.access;
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
        HttpServletRequest request = (HttpServletRequest) getPageContext().getRequest();
        HttpServletResponse response = (HttpServletResponse) getPageContext().getResponse();
        RequestContext context = getRequestContext();

        try
        {
            String chromeId = getChrome();
            if(!isChromeless())
            {
                PresentationUtil.renderRegion(context, request, response, context.getCurrentTemplate().getId(), getName(), getScope(), chromeId);
            }
            else
            {
                PresentationUtil.renderChromelessRegion(context, request, response, context.getCurrentTemplate().getId(), getName(), getScope());
            }            
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new JspException(t);
        }
        return SKIP_BODY;
    }
    
    public void release()
    {
        this.name = null;
        this.scope = null;
        this.access = null;
        this.chrome = null;
        this.chromeless = false;
        
        super.release();
    }
    
}
