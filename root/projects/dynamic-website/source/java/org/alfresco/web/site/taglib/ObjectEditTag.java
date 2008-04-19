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

import org.alfresco.web.site.ADWUtil;
import org.alfresco.web.site.Framework;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.URLUtil;

/**
 * @author muzquiano
 */
public class ObjectEditTag extends AbstractObjectTag
{
    private String endpointId = null;
    private String target = null;
    private String iconUri = null;

    public String getIconUri()
    {
        return iconUri;
    }

    public void setIconUri(String iconUri)
    {
        this.iconUri = iconUri;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public String getEndpoint()
    {
        return this.endpointId;
    }

    public void setEndpoint(String endpointId)
    {
        this.endpointId = endpointId;
    }

    public int doStartTag() throws JspException
    {
        if (Framework.getConfig().isInContextEnabled())
        {
            RequestContext context = getRequestContext();

            // get the url
            String url = ADWUtil.getContentEditURL(context, getEndpoint(),
                    getId());

            // icon uri
            String newIconUri = "/themes/builder/images/default/icons/incontext/edit_content.gif";
            if (iconUri != null)
                newIconUri = iconUri;
            newIconUri = URLUtil.browser(context, newIconUri);

            // target
            String newTargetString = " target='_blank' ";
            if (target != null)
            {
                if ("".equals(target))
                    newTargetString = " ";
                else
                    newTargetString = " target='" + target + "' ";
            }

            // render
            StringBuffer buffer = new StringBuffer();
            buffer.append("<div id='ipe-" + context.getModel().newGUID() + "'>");
            buffer.append("<a href='" + url + "' " + newTargetString + ">");
            buffer.append("<img border='0' src='" + newIconUri + "'/>");
            buffer.append("</a>");
            buffer.append("</div>");
            print(buffer.toString());
        }
        return EVAL_BODY_INCLUDE;
    }
}
