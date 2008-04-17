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

import org.alfresco.web.site.URLUtil;

/**
 * @author muzquiano
 */
public class ObjectPrintTag extends ObjectAnchorTag
{
    private String iconUri = null;

    public String getIconUri()
    {
        return iconUri;
    }

    public void setIconUri(String iconUri)
    {
        this.iconUri = iconUri;
    }

    public int doStartTag() throws JspException
    {
        if(isContentId())
        {
            String format = getFormat();
            if (format == null)
                setFormat("print");
    
            int ret = super.doStartTag();
    
            String theIconUri = getIconUri();
            if (theIconUri == null)
                theIconUri = "/ui/images/icons/incontext/print_content.gif";
            theIconUri = URLUtil.browser(getRequestContext(), theIconUri);
    
            try
            {
                this.getOut().write("<img src='" + theIconUri + "' border='0'/>");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                throw new JspException(ex);
            }
            return ret;
        }
        return EVAL_BODY_INCLUDE;
    }
}
