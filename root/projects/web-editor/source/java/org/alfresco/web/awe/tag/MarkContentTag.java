/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.web.awe.tag;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.servlet.jsp.JspException;

/**
 * Tag used to indicate an editable piece of content.
 * 
 * @author Gavin Cornwell
 */
public class MarkContentTag extends AbstractWebEditorTag
{
    private static final long serialVersionUID = 1564711937667040715L;

    private String contentId;
    private String formId;
    private boolean nestedMarker = false;
    
    /**
     * Returns the identifier of the content to be edited
     * 
     * @return The identifier of the content to be edited
     */
    public String getId()
    {
        return this.contentId;
    }

    /**
     * Sets the identifier of the content to be edited
     * 
     * @param contentId The identifier of the content to be edited
     */
    public void setId(String contentId)
    {
        this.contentId = contentId;
    }

    /**
     * Returns the identifier of the form to use to edit the content
     * 
     * @return The identifier of the form to use to edit the content
     */
    public String getFormId()
    {
        return this.formId;
    }

    /**
     * Sets the identifier of the form to use to edit the content
     * 
     * @param formId The identifier of the form to use to edit the content
     */
    public void setFormId(String formId)
    {
        this.formId = formId;
    }

    /**
     * Returns a flag to indicate whether the marker is nested within the
     * content to be edited.
     * 
     * @return true if the marker is nested
     */
    public boolean isNestedMarker()
    {
        return this.nestedMarker;
    }

    /**
     * Sets whether the marker is nested within the content to be edited.
     * 
     * @param nestedMarker true to indicate the marker is nested
     */
    public void setNestedMarker(boolean nestedMarker)
    {
        this.nestedMarker = nestedMarker;
    }

    /**
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    public int doStartTag() throws JspException
    {
        // TODO: look for AWE enabled flag in request session, if not present don't render anything!
        
        try
        {
            Writer out = pageContext.getOut();
         
            // TODO: Also retrieve the host details from config, for now we'll
            //       presume the AWE app is on the same server
            String contextPath = getWebEditorContextPath();
            
            // generate a unique id for this marked content
            List<MarkedContent> markedContent = getMarkedContent();
            String markerIdPrefix = (String)this.pageContext.getRequest().getAttribute(KEY_MARKER_ID_PREFIX);
            String markerId = markerIdPrefix + "-" + (markedContent.size() + 1);
            
            // create marked content object and store
            MarkedContent content = new MarkedContent(markerId, this.contentId, this.formId);
            markedContent.add(content);
            
            // render edit link for content
            out.write("<span class=\"alfresco-content-marker\" id=\"");
            out.write(markerId);
            out.write("\"><a href=\"");
            out.write(contextPath);
            out.write("/page/metadata?nodeRef=");
            out.write(this.contentId);
            out.write("&showCancelButton=true&redirect=");
            // TODO: calculate this and encode url
            out.write("http://localhost:8081/customer");
            if (this.formId != null)
            {
                out.write("&formId=");
                out.write(this.formId);
            }
            out.write("\"><img src=\"");
            out.write(contextPath);
            out.write("/themes/default/images/edit.png\" alt=\"Edit ");
            // TODO: this needs to do a lookup for content title (done on client side?)
            out.write(markerId);
            out.write("\" border=\"0\" /></a></span>\n");
        }
        catch (IOException ioe)
        {
            throw new JspException(ioe.toString());
        }
      
        return SKIP_BODY;
    }
    
    /**
     * @see javax.servlet.jsp.tagext.TagSupport#release()
     */
    public void release()
    {
        super.release();
    }
}
