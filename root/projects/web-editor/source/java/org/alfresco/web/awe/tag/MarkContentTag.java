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
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Tag used to indicate an editable piece of content.
 * 
 * @author Gavin Cornwell
 */
public class MarkContentTag extends AbstractWebEditorTag
{
   private static final long serialVersionUID = 1564711937667040715L;
   private static final Log logger = LogFactory.getLog(MarkContentTag.class);

   private String contentId;
   private String contentTitle;
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
    * Returns the title of the content to be edited
    * 
    * @return The title of the content to be edited
    */
   public String getTitle()
   {
      return this.contentTitle;
   }
   
   /**
    * Sets the title of the content to be edited
    * 
    * @param title The title of the content to be edited
    */
   public void setTitle(String title)
   {
      this.contentTitle = title;
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
    * Returns a flag to indicate whether the marker is nested within the content
    * to be edited.
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
      if (isEditingEnabled())
      {
         try
         {
            Writer out = pageContext.getOut();
            
            // get the prefix URL to the AWE assets
            String urlPrefix = getWebEditorUrlPrefix();
            
            // generate a unique id for this marked content
            List<MarkedContent> markedContent = getMarkedContent();
            String markerIdPrefix = (String) this.pageContext.getRequest().getAttribute(
                     KEY_MARKER_ID_PREFIX);
            String markerId = markerIdPrefix + "-" + (markedContent.size() + 1);
   
            // create marked content object and store
            MarkedContent content = new MarkedContent(markerId, this.contentId, this.contentTitle, 
                     this.formId, this.nestedMarker);
            markedContent.add(content);
   
            // render edit link for content
            out.write("<span class=\"alfresco-content-marker\" id=\"");
            out.write(markerId);
            out.write("\"><a href=\"");
            out.write(urlPrefix);
            out.write("/page/metadata?nodeRef=");
            out.write(this.contentId);
            out.write("&showCancelButton=true");
            
            String redirectUrl = calculateRedirectUrl();
            if (redirectUrl != null)
            {
               out.write("&redirect=");
               out.write(calculateRedirectUrl());
            }
            
            if (this.formId != null)
            {
               out.write("&formId=");
               out.write(this.formId);
            }
            
            out.write("\"><img src=\"");
            out.write(urlPrefix);
            out.write("/awe/images/edit.png\" alt=\"");
            out.write(encode(this.contentTitle));
            out.write("\" title=\"");
            out.write(encode(this.contentTitle));
            out.write("\"border=\"0\" /></a></span>\n");
            
            if (logger.isDebugEnabled())
               logger.debug("Completed markContent rendering for: " + content);
         }
         catch (IOException ioe)
         {
            throw new JspException(ioe.toString());
         }
      }
      else if (logger.isDebugEnabled())
      {
         logger.debug("Skipping markContent rendering as editing is disabled");
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
   
   /**
    * Calculates the redirect url for form submission, this will
    * be the current request URL.
    * 
    * @return The redirect URL
    */
   private String calculateRedirectUrl()
   {
      // NOTE: This may become configurable in the future, for now
      //       this just returns the current page's URI
      
      String redirectUrl = null;
      HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
      try
      {
         StringBuffer url = request.getRequestURL();
         String queryString = request.getQueryString();
         if (queryString != null)
         {
            url.append("?").append(queryString);
         }
         
         redirectUrl = URLEncoder.encode(url.toString(), "UTF-8");
      }
      catch (UnsupportedEncodingException uee)
      {
         // just return null
      }
      
      return redirectUrl; 
   }
}
