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

import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Tag used in the head section of a page to indicate that the page potentially
 * contains editable Alfresco content.
 * 
 * @author Gavin Cornwell
 */
public class StartTemplateTag extends AbstractWebEditorTag
{
   private static final long serialVersionUID = -7242916874303242800L;
   private static final Log logger = LogFactory.getLog(StartTemplateTag.class);

   private static final String TOP = "top";
   private static final String LEFT = "left";
   private static final String RIGHT = "right";
   private static final String ALF = "alf_";
   
   private String toolbarLocation = TOP;

   /**
    * Returns the current value for the toolbar location
    * 
    * @return Toolbar location
    */
   public String getToolbarLocation()
   {
      return this.toolbarLocation;
   }

   /**
    * Sets the toolbar location
    * 
    * @param location Toolbar location
    */
   public void setToolbarLocation(String location)
   {
      if (location.equalsIgnoreCase(TOP))
      {
         this.toolbarLocation = TOP;
      }
      else if (location.equalsIgnoreCase(LEFT))
      {
         this.toolbarLocation = LEFT;
      }
      else if (location.equalsIgnoreCase(RIGHT))
      {
         this.toolbarLocation = RIGHT;
      }
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
              
              // bootstrap WEF
              out.write("<script type=\"text/javascript\" src=\"");
              out.write(getWebEditorUrlPrefix());
              out.write("/service/wef/bootstrap");
              if (isDebugEnabled())
              {
                  out.write("?debug=true");
              }
              out.write("\"></script>\n");
          
              // store the toolbar location into the request session
              this.pageContext.getRequest().setAttribute(KEY_TOOLBAR_LOCATION, getToolbarLocation());
   
              // store an id prefix to use in all content marker tags used on the page
              this.pageContext.getRequest().setAttribute(KEY_MARKER_ID_PREFIX, ALF + System.currentTimeMillis());
            
              if (logger.isDebugEnabled())
                  logger.debug("Completed startTemplate rendering");
          }
          catch (IOException ioe)
          {
             throw new JspException(ioe.toString());
          }
      }
      else if (logger.isDebugEnabled())
      {
         logger.debug("Skipping startTemplate rendering as editing is disabled");
      }
      
      return SKIP_BODY;
   }

   /**
    * @see javax.servlet.jsp.tagext.TagSupport#release()
    */
   public void release()
   {
      super.release();

      this.toolbarLocation = null;
   }
}
