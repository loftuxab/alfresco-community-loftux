/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
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
            
            // render config required for ribbon and marked content
            out.write("<script type=\"text/javascript\">\n");
            out.write("WEF.ConfigRegistry.registerConfig('org.wef.ribbon',\n");
            out.write("{ position: \"");
            out.write(toolbarLocation);
            out.write("\" });\n");
            out.write("WEF.ConfigRegistry.registerConfig('org.alfresco.awe',{id:'awe',name:'awe',editables:[\n");
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
            out.write("]});\n");
            out.write("\n</script>");
            
            // request all the resources
            out.write("<script type=\"text/javascript\" src=\"");
            out.write(getWebEditorUrlPrefix());
            out.write("/service/wef/resources\"></script>\n");
            
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
