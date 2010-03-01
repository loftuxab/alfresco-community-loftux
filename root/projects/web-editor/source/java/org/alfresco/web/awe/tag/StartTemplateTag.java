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
            
            out.write("<script type=\"text/javascript\" src=\"");
            out.write(urlPrefix);
            out.write("/service/wef/bootstrap");
            if (debug)
            {
                out.write("?debug=true");
            }
            out.write("\"></script>\n");
            
            // TODO: Remove all the addResource() calls below, call
            //       /service/wef/resources script instead from EndTemplateTag
            // TODO: add/amend method so that addResource(s) accepts arrays (perf);
            out.write("<script type=\"text/javascript\">");
            out.write("WEF.addResource({\n");
            out.write("name: \"com.yahoo.bubbling\",\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/js/bubbling.v2.1.js\",\n");
            out.write("varName: \"YAHOO.Bubbling\",\n");
            out.write("requires:['utilities']\n");
            out.write("});\n");
            out.write("WEF.addResource({\n");
            out.write("name: \"org.alfresco.messages\",\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/service/messages.js?locale=en_US\",\n");
            out.write("varName: \"Alfresco\"\n");
            out.write("});\n");
            out.write("WEF.addResource({\n");
            out.write("name: \"org.alfresco.alfresco\",\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/js/alfresco.js\",\n");
            out.write("varName: \"Alfresco\",\n");
            out.write("requires: ['utilities','animation','selector','cookie','accessibility.aria.plugins.yui.menu','accessibility.aria.plugins.yui.container','accessibility.aria.plugins.yui.button','org.alfresco.messages','com.yahoo.bubbling']\n");
            out.write("});\n");
            
            out.write("WEF.addResource({\n");
            out.write("name: \"org.alfresco.awe\",\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/js/org/alfresco/awe/awe.js\",\n");
            out.write("varName: \"AWE\",\n");
            out.write("requires:['org.alfresco.alfresco','org.wef','org.alfresco.awe.ui.form-panel','org.alfresco.awe.ui.login-panel','org.alfresco.forms','org.alfresco.awe.css','force-yui-skin']\n");
            out.write("});\n");
            	
            out.write("WEF.addResource({\n");
            out.write("name: \"org.wef\",\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/wef/wef.js\",\n");
            out.write("requires:['utilities','com.yahoo.bubbling','org.wef.reset.css','org.wef.css']\n");
            out.write("});\n");
            out.write("WEF.addResource({\n");
            out.write("name: \"force-yui-skin\",\n");
            out.write("type: \"css\",\n");
            out.write("path: \"http://localhost:8081/awe/yui/assets/skins/default/skin.css\"\n");
            out.write("});\n");
            out.write("WEF.addResource({\n");
            out.write("name: \"org.wef.reset.css\",\n");
            out.write("type: \"css\",\n");
            out.write("path: \"http://localhost:8081/awe/wef/wef-reset.css\"\n");
            out.write("});\n");
            out.write("WEF.addResource({\n");
            out.write("name: \"org.wef.css\",\n");
            out.write("type: \"css\",\n");
            out.write("path: \"http://localhost:8081/awe/wef/wef.css\"\n");
            out.write("});\n");
            out.write("WEF.addResource({\n");
            out.write("name: \"org.alfresco.awe.css\",\n");
            out.write("type: \"css\",\n");
            out.write("path: \"http://localhost:8081/awe/js/org/alfresco/awe/awe.css\"\n");
            out.write("});\n");

            out.write("WEF.addResource({\n");
            out.write("name: \"org.alfresco.awe.ui.panel\",\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/js/org/alfresco/awe/ui/panel/panel.js\",\n");
            out.write("requires: ['org.alfresco.alfresco']\n");
            out.write("});\n");

            out.write("WEF.addResource({\n");
            out.write("name: \"org.alfresco.awe.ui.form-panel\",\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/js/org/alfresco/awe/ui/panel/form-panel.js\",\n");
            out.write("requires: ['org.alfresco.awe.ui.panel']\n");
            out.write("});\n");
            
            out.write("WEF.addResource({\n");
            out.write("name: \"org.alfresco.awe.ui.login-panel\",\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/js/org/alfresco/awe/ui/panel/login-panel.js\",\n");
            out.write("requires: ['org.alfresco.awe.ui.panel']\n");
            out.write("});\n");
            
            out.write("WEF.addResource({\n");
            out.write("name: \"org.wef.ui.ribbon\",\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/wef/ui/ribbon/ribbon.js\",\n");
            out.write("varName: \"WEF\",\n");
            out.write("requires: ['org.wef.ui.ribbon.css','org.wef.ui.tabbed-toolbar']\n");
            out.write("});\n");            
            
            out.write("WEF.addResource({\n");
            out.write("name: \"org.wef.ui.ribbon.css\",\n");
            out.write("type: \"css\",\n");
            out.write("path: \"http://localhost:8081/awe/wef/ui/ribbon/ribbon.css\",\n");
            out.write("varName: \"WEF\",\n");
            out.write("requires: ['org.wef']\n");
            out.write("});\n");            

            out.write("WEF.addResource({\n");
            out.write("name: \"org.wef.ui.tabbed-toolbar\",\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/wef/ui/tabbed-toolbar/tabbed-toolbar.js\",\n");
            out.write("varName: \"WEF\",\n");
            out.write("requires: ['org.wef','org.wef.ui.tabbed-toolbar.css','accessibility.aria.plugins.yui.tabview','org.wef.ui.toolbar']\n");
            out.write("});\n");            
            
            out.write("WEF.addResource({\n");
            out.write("name: \"org.wef.ui.tabbed-toolbar.css\",\n");
            out.write("type: \"css\",\n");
            out.write("path: \"http://localhost:8081/awe/wef/ui/tabbed-toolbar/tabbed-toolbar.css\",\n");
            out.write("varName: \"WEF\",\n");
            out.write("requires: ['org.wef']\n");
            out.write("});\n");
            
            out.write("WEF.addResource({\n");
            out.write("name: \"org.wef.ui.toolbar\",\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/wef/ui/toolbar/toolbar.js\",\n");
            out.write("varName: \"WEF\",\n");
            out.write("requires: ['org.wef','org.wef.ui.toolbar.css']\n");
            out.write("});\n");            
            
            out.write("WEF.addResource({\n");
            out.write("name: \"org.wef.ui.toolbar.css\",\n");
            out.write("type: \"css\",\n");
            out.write("path: \"http://localhost:8081/awe/wef/ui/toolbar/toolbar.css\",\n");
            out.write("varName: \"WEF\",\n");
            out.write("requires: ['org.wef']\n");
            out.write("});\n");
            
            // TOOD: work out how to add this.
            // out.write("<!--[if gte IE 6]>\n");
            // out.write("WEF.addResource({\n");
            // out.write("name: 'org.alfresco.awe.ribbon.ie6.css',\n");
            // out.write("type: \"css\",\n");
            // out.write("path: \"http://localhost:8081/awe/css/awe-toolbar-ie.css\"\n");
            // out.write("});\n");            
            // out.write("<![endif]-->\n");


            
            out.write("WEF.addResource({\n");
            out.write("name: 'org.alfresco.forms',\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/components/form/form-min.js\",\n");
            out.write("requires: ['org.alfresco.forms.runtime','org.alfresco.forms.forms','org.alfresco.forms.datepicker','org.alfresco.forms.period','org.alfresco.forms.object-finder','calendar','org.alfresco.forms.rich-text-control','org.alfresco.alfresco.editors.tinymce','org.alfresco.forms.content']\n");
            out.write("});\n");
            
            out.write("WEF.addResource({\n");
            out.write("name: 'org.alfresco.forms.runtime',\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/js/forms-runtime.js\"\n");
            out.write("});\n");
            
            out.write("WEF.addResource({\n");
            out.write("name: 'org.alfresco.forms.forms',\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/components/form/form-min.js\",\n");
            out.write("requires:['org.alfresco.forms.css']");
            out.write("});\n");
            
            out.write("WEF.addResource({\n");
            out.write("name: 'org.alfresco.forms.css',\n");
            out.write("type: \"css\",\n");
            out.write("path: \"http://localhost:8081/awe/components/form/form.css\"\n");
            out.write("});\n");

            out.write("WEF.addResource({\n");
            out.write("name: 'org.alfresco.forms.datepicker',\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/components/form/date-picker-min.js\"\n");
            out.write("});\n");

            out.write("WEF.addResource({\n");
            out.write("name: 'org.alfresco.forms.period',\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/components/form/period-min.js\"\n");
            out.write("});\n");

            out.write("WEF.addResource({\n");
            out.write("name: 'org.alfresco.forms.object-finder',\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/components/object-finder/object-finder-min.js\"\n");
            out.write("});\n");

            out.write("WEF.addResource({\n");
            out.write("name: 'org.alfresco.forms.object-finder',\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/components/object-finder/object-finder-min.js\",\n");
            out.write("requires:['org.alfresco.forms.object-finder.css']");
            out.write("});\n");

            out.write("WEF.addResource({\n");
            out.write("name: 'org.alfresco.forms.object-finder.css',\n");
            out.write("type: \"css\",\n");
            out.write("path: \"http://localhost:8081/awe/components/object-finder/object-finder.css\"\n");
            out.write("});\n");

            out.write("WEF.addResource({\n");
            out.write("name: 'org.alfresco.forms.rich-text-control',\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/components/form/rich-text-min.js\"\n");
            out.write("});\n");

            out.write("WEF.addResource({\n");
            out.write("name: 'org.alfresco.forms.content',\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/components/form/content-min.js\"\n");
            out.write("});\n");


            out.write("WEF.addResource({\n");
            out.write("name: 'org.alfresco.alfresco.editors.tinymce',\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/modules/editors/tiny_mce-min.js\",\n");
            out.write("requires:['org.alfresco.alfresco','com.tinymce']\n");
            out.write("});\n");

            out.write("WEF.addResource({\n");
            out.write("name: 'com.tinymce',\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/modules/editors/tiny_mce/tiny_mce.js\",\n");
            out.write("});\n");

            out.write("WEF.addResource({\n");
            out.write("name: 'accessibility.aria.plugins.yui',\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/lib/accessibility/aria/plugins/yui/yui-aria.js\",\n");            
            out.write("requires:['accessibility.aria.plugins.yui.menu','accessibility.aria.plugins.yui.button','accessibility.aria.plugins.yui.container','accessibility.aria.plugins.yui.tabview']\n");
            out.write("});\n");

            out.write("WEF.addResource({\n");
            out.write("name: 'accessibility.aria.plugins.yui.menu',\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/lib/accessibility/aria/plugins/yui/menuariaplugin.js\",\n");
            out.write("requires:['menu']\n");
            out.write("});\n");

            out.write("WEF.addResource({\n");
            out.write("name: 'accessibility.aria.plugins.yui.container',\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/lib/accessibility/aria/plugins/yui/containerariaplugin.js\",\n");
            out.write("requires:['container']\n");
            out.write("});\n");

            out.write("WEF.addResource({\n");
            out.write("name: 'accessibility.aria.plugins.yui.button',\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/lib/accessibility/aria/plugins/yui/buttonariaplugin.js\",\n");
            out.write("requires:['button']\n");
            out.write("});\n");

            out.write("WEF.addResource({\n");
            out.write("name: 'accessibility.aria.plugins.yui.tabview',\n");
            out.write("type: \"js\",\n");
            out.write("path: \"http://localhost:8081/awe/lib/accessibility/aria/plugins/yui/tabviewariaplugin.js\",\n");
            out.write("requires:['tabview']\n");
            out.write("});\n");
            
            out.write("WEF.addResource({\n");
            out.write("name:'org.wefapps.awe',\n");
            out.write("repo:'core',\n");
            out.write("requires:['org.alfresco.awe','org.wef','org.wef.ui.ribbon']\n");
            out.write("});\n");
         
            out.write("</script>\n");            
                           
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
