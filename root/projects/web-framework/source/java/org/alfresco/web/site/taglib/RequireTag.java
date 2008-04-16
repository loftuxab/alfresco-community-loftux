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

import org.alfresco.web.site.RenderUtil;

/**
 * @author muzquiano
 */
public class RequireTag extends TagBase
{
    protected String script;
    protected String link;
    protected String name;
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return this.name;
    }

    public void setScript(String script)
    {
        this.script = script;
    }

    public String getScript()
    {
        return this.script;
    }

    public void setLink(String link)
    {
        this.link = link;
    }

    public String getLink()
    {
        return this.link;
    }

    public int doStartTag() throws JspException
    {
        // don't execute the body
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException
    {
        // get the body content and include if it is there
        if (getBodyContent() != null && !"".equals(getBodyContent()))
        {
            String tags = getBodyContent().getString();
            if (tags != null && !"".equals(tags))
            {
                RenderUtil.appendHeadTags(getRequestContext(), tags);
            }
        }

        // is there a script tag?  if so, include it
        if (getScript() != null)
        {
            String scriptImport = RenderUtil.renderScriptImport(
                    getRequestContext(), getScript());
            RenderUtil.appendHeadTags(getRequestContext(), scriptImport);
        }

        // is there a link tag?  if so, include it
        if (getLink() != null)
        {
            String linkImport = null;
            if(this.getName() != null)
            {
                linkImport = RenderUtil.renderLinkImport(getRequestContext(), getLink(), getName());
            }
            else
            {
                linkImport = RenderUtil.renderLinkImport(getRequestContext(), getLink());
            }
            RenderUtil.appendHeadTags(getRequestContext(), linkImport);
        }

        return SKIP_BODY;
    }
}
