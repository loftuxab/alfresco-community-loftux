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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Tag used at the end of the body section of a page to indicate the end
 * of a page that potentially contains editable Alfresco content.
 * 
 * @author Gavin Cornwell
 */
public class EndTemplateTag extends AbstractWebEditorTag
{
   private static final long serialVersionUID = -2917015141188997203L;
   private static final Log logger = LogFactory.getLog(EndTemplateTag.class);

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
      
            // get the toolbar location from the request session
            String toolbarLocation = (String)this.pageContext.getRequest().getAttribute(KEY_TOOLBAR_LOCATION);
            
            // render JavaScript to configure toolbar and edit icons
            List<MarkedContent> markedContent = getMarkedContent();
            
            out.write("<script type=\"text/javascript\">\n");
                 out.write("window.onload = function() {\n");
            out.write("AAF.boot(function handler()");
            out.write("{\n");
            out.write("AAF.loader.require({\n");
            out.write("name:'com.alfresco.awe.init',\n");
            out.write("repo:'lib',\n");
            out.write("requires:['com.alfresco.awe','com.alfresco.awe.ribbon']\n");
            out.write("});\n");
      
            out.write("AAF.loader.load(\n");
            out.write("{\n");
            out.write("fn:function AAF_Boot_SuccessHandler(o)\n");
            out.write("{\n");
            out.write("var y = (o.reference) ? o.reference : YAHOO;\n");
            out.write("// o.reference is the SANDBOXED YAHOO object\n");
            out.write("if (!o.reference)\n");
            out.write("{\n");
            out.write("console.log('Not running in sandbox mode');\n");
            out.write("}\n");
            out.write("else\n");
            out.write("{\n");
            out.write("console.log('must be running in sandbox mode');\n");
            out.write("// Anything root level needs to be added to YAHOO namespace eg:\n");
            out.write("// var AWE = o.reference.com.alfresco.awe;\n");
            out.write("}\n");
            out.write("\n\n");
            out.write("y.com.alfresco.awe.init([");
            boolean first = true;
            for (MarkedContent content : markedContent)
            {
               if (first == false)
               {
                  out.write(",");
               }
               else
               {
                  first = false;
               }
       
               out.write("\n{\n   id: \"");
               out.write(encode(content.getMarkerId()));
               out.write("\",\n   nodeRef: \"");
               out.write(encode(content.getContentId()));
               out.write("\",\n   title: \"");
               out.write(encode(content.getContentTitle()));
               out.write("\",\n   nested: ");
               out.write(Boolean.toString(content.isNested()));
               out.write(",\n   redirectUrl: window.location.href");
               if (content.getFormId() != null)
               {
                  out.write(",\n   formId: \"");
                  out.write(encode(content.getFormId()));
                  out.write("\"");
               }
               out.write("\n}");
            }
            out.write("]);\n");
            out.write("y.com.alfresco.awe.render();");
            out.write("},\n");
            out.write("scope:this\n");
            out.write("},\n");
            out.write("{\n");
            out.write("fn:function AAF_Boot_FailureHandler()\n");
            out.write("{\n");
            out.write("},\n");
            out.write("scope: this\n");
            out.write("}\n");
            out.write("\n);\n");
            out.write("}\n");
            out.write(");\n");

            out.write("}\n</script>");
            
            if (logger.isDebugEnabled())
               logger.debug("Completed endTemplate rendering for " + markedContent.size() + 
                        " marked content items with toolbar location of: " + toolbarLocation);
         }
        catch (IOException ioe)
        {
           throw new JspException(ioe.toString());
        }
      }
      else if (logger.isDebugEnabled())
      {
         logger.debug("Skipping endTemplate rendering as editing is disabled");
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
