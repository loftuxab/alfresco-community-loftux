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
   
            // get the prefix URL to the AWE assets
            String urlPrefix = getWebEditorUrlPrefix();
            boolean debug = isDebugEnabled();
            
            out.write("\n<!-- **** Start of Alfresco Web Editor requirements **** -->\n");
   
            // NOTE: All the rendered CSS and JavaScript below should be replaced
            // with a single call to a bootstrap.js script which will generate
            // all the code below
   
            // render CSS required
            //awe-reset
            out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
            out.write(urlPrefix);
            out.write("/css/awe-reset.css\" />\n");
            out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
            out.write(urlPrefix);
            out.write("/yui/reset-fonts-grids/reset-fonts-grids.css\" />\n");
            out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
            out.write(urlPrefix);
            out.write("/yui/assets/skins/default/skin.css\" />\n");
            out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
            out.write(urlPrefix);
            out.write("/yui/assets/skins/default/container.css\" />\n");
            out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
            out.write(urlPrefix);
            out.write("/yui/assets/skins/default/menu.css\" />\n");
            out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
            out.write(urlPrefix);
            out.write("/yui/assets/skins/default/button.css\" />\n");
            out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
            out.write(urlPrefix);
            out.write("/css/base.css\" />\n");
   
            // render JavaScript required, depending on debug flag
            if (debug)
            {
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/yahoo/yahoo-debug.js\"></script>\n");
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/event/event-debug.js\"></script>\n");
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/dom/dom-debug.js\"></script>\n");
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/dragdrop/dragdrop-debug.js\"></script>\n");
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/animation/animation-debug.js\"></script>\n");
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/logger/logger-debug.js\"></script>\n");
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/connection/connection-debug.js\"></script>\n");
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/element/element-debug.js\"></script>\n");
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/get/get-debug.js\"></script>\n");
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/yuiloader/yuiloader-debug.js\"></script>\n");
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/button/button-debug.js\"></script>\n");
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/container/container-debug.js\"></script>\n");
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/menu/menu-debug.js\"></script>\n");
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/json/json-debug.js\"></script>\n");
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/selector/selector-debug.js\"></script>\n");
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/cookie/cookie-debug.js\"></script>\n");
            }
            else
            {
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/utilities/utilities.js\"></script>\n");
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/button/button-min.js\"></script>\n");
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/container/container-min.js\"></script>\n");
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/menu/menu-min.js\"></script>\n");
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/json/json-min.js\"></script>\n");
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/selector/selector-min.js\"></script>\n");
               out.write("<script type=\"text/javascript\" src=\"");
               out.write(urlPrefix);
               out.write("/yui/cookie/cookie-min.js\"></script>\n");
            }
   
            // render JavaScript always required
            out.write("<script type=\"text/javascript\" src=\"");
            out.write(urlPrefix);
            out.write("/yui/yui-patch.js\"></script>\n");
            out.write("<script type=\"text/javascript\" src=\"");
            out.write(urlPrefix);
            out.write("/js/yui-toolbar.js\"></script>\n");
            out.write("<script type=\"text/javascript\" src=\"");
            out.write(urlPrefix);
            out.write("/js/bubbling.v2.1.js\"></script>\n");
            out.write("<script type=\"text/javascript\" src=\"");
            out.write(urlPrefix);
            out.write("/service/messages.js?locale=en_US\"></script>\n");
                        
            // render JavaScript constants
            out.write("<script type=\"text/javascript\">//<![CDATA[\n");
            out.write("Alfresco.constants = Alfresco.constants || {};\n");
            out.write("Alfresco.constants.DEBUG = ");
            out.write(debug ? "true" : "false");
            out.write(";\n");
            out.write("Alfresco.constants.AUTOLOGGING = false;\n");
            out.write("Alfresco.constants.PROXY_URI = window.location.protocol + \"//\" + window.location.host + \"");
            out.write(urlPrefix);
            out.write("/proxy/alfresco/\";\n");
            out.write("Alfresco.constants.PROXY_URI_RELATIVE = \"");
            out.write(urlPrefix);
            out.write("/proxy/alfresco/\";\n");
            out.write("Alfresco.constants.THEME = \"default\";\n");
            out.write("Alfresco.constants.URL_CONTEXT = \"");
            out.write(urlPrefix);
            out.write("/\";\n");
            out.write("Alfresco.constants.URL_PAGECONTEXT = \"");
            out.write(urlPrefix);
            out.write("/p/\";\n");
            out.write("Alfresco.constants.URL_SERVICECONTEXT = \"");
            out.write(urlPrefix);
            out.write("/service/\";\n");
            out.write("Alfresco.constants.USERNAME = \"admin\";\n");
            out.write("Alfresco.constants.HTML_EDITOR = \"tinyMCE\";\n");
            out.write("//]]></script>\n");
   
            out.write("<script type=\"text/javascript\" src=\"");
            out.write(urlPrefix);
            out.write("/js/alfresco.js\"></script>\n");
            out.write("<script type=\"text/javascript\" src=\"");
            out.write(urlPrefix);
            out.write("/js/forms-runtime.js\"></script>\n");
   
            // render forms CSS & JavaScript dependencies
            out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
            out.write(urlPrefix);
            out.write("/yui/calendar/assets/calendar.css\" />\n");
            out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
            out.write(urlPrefix);
            out.write("/components/object-finder/object-finder.css\" />\n");
            out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
            out.write(urlPrefix);
            out.write("/components/form/form.css\" />\n");
            out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
            out.write(urlPrefix);
            out.write("/css/awe.css\" />\n");
            out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
            out.write(urlPrefix);
            out.write("/css/awe-toolbar.css\" />\n");
            out.write("<!--[if gte IE 6]>\n");
            out.write("   <link rel=\"stylesheet\" type=\"text/css\" href=\"");
            out.write(urlPrefix);
            out.write("/css/awe-toolbar-ie.css\" />\n");
            out.write("<![endif]-->\n");
   
            out.write("<script type=\"text/javascript\" src=\"");
            out.write(urlPrefix);
            out.write("/components/form/form-min.js\"></script>\n");
            out.write("<script type=\"text/javascript\" src=\"");
            out.write(urlPrefix);
            out.write("/components/form/date-min.js\"></script>\n");
            out.write("<script type=\"text/javascript\" src=\"");
            out.write(urlPrefix);
            out.write("/components/form/date-picker-min.js\"></script>\n");
            out.write("<script type=\"text/javascript\" src=\"");
            out.write(urlPrefix);
            out.write("/components/form/period-min.js\"></script>\n");
            out.write("<script type=\"text/javascript\" src=\"");
            out.write(urlPrefix);
            out.write("/components/object-finder/object-finder-min.js\"></script>\n");
            out.write("<script type=\"text/javascript\" src=\"");
            out.write(urlPrefix);
            out.write("/yui/calendar/calendar-min.js\"></script>\n");
            out.write("<script type=\"text/javascript\" src=\"");
            out.write(urlPrefix);
            out.write("/modules/editors/tiny_mce/tiny_mce.js\"></script>\n");
            out.write("<script type=\"text/javascript\" src=\"");
            out.write(urlPrefix);
            out.write("/modules/editors/tiny_mce-min.js\"></script>\n");
            out.write("<script type=\"text/javascript\" src=\"");
            out.write(urlPrefix);
            out.write("/components/form/rich-text-control-min.js\"></script>\n");
   
            // render AWE JavaScript dependencies
            out.write("<script type=\"text/javascript\" src=\"");
            out.write(urlPrefix);
            out.write("/js/awe.js\"></script>\n");
            out.write("<script type=\"text/javascript\" src=\"");
            out.write(urlPrefix);
            out.write("/js/awe-ribbon.js\"></script>\n");
            out.write("<script type=\"text/javascript\" src=\"");
            out.write(urlPrefix);
            out.write("/js/ariaplugins/containerariaplugin.js\"></script>\n");
            out.write("<script type=\"text/javascript\" src=\"");
            out.write(urlPrefix);
            out.write("/js/ariaplugins/buttonariaplugin.js\"></script>\n");
            out.write("<script type=\"text/javascript\" src=\"");
            out.write(urlPrefix);
            out.write("/js/ariaplugins/menuariaplugin.js\"></script>\n");
   
            out.write("<!-- **** End of Alfresco Web Editor requirements **** -->\n");
   
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
