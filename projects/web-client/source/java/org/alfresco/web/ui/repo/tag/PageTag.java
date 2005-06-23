/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.web.ui.repo.tag;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.alfresco.web.app.Application;

/**
 * A non-JSF tag library that adds the HTML begin and end tags if running in servlet mode
 * 
 * @author gavinc
 */
public class PageTag extends TagSupport
{
   private String title;
   
   /**
    * @return The title for the page
    */
   public String getTitle()
   {
      return title;
   }

   /**
    * @param title Sets the page title
    */
   public void setTitle(String title)
   {
      this.title = title;
   }

   /**
    * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      try
      {
         Writer out = pageContext.getOut();
         
         if (Application.inPortalServer(pageContext.getServletContext()) == false)
         {
            out.write("<html><head><title>");
            if (this.title == null)
            {
               out.write("Alfresco Web Client");
            }
            else
            {
               out.write(this.title);
            }
            out.write("</title></head>");
            out.write("<body>\n");
         }
         
         String reqPath = ((HttpServletRequest)pageContext.getRequest()).getContextPath();
         out.write(SCRIPTS_1);
         out.write(reqPath);
         out.write(SCRIPTS_2);
         out.write(STYLES_1);
         out.write(reqPath);
         out.write(STYLES_2);
      }
      catch (IOException ioe)
      {
         throw new JspException(ioe.toString());
      }
      
      return EVAL_BODY_INCLUDE;
   }

   public int doEndTag() throws JspException
   {
      if (Application.inPortalServer(pageContext.getServletContext()) == false)
      {
         try
         {
            pageContext.getOut().write("\n</body></html>");
         }
         catch (IOException ioe)
         {
            throw new JspException(ioe.toString());
         }
      }
      
      return super.doEndTag();
   }
   
   private final static String SCRIPTS_1 = "<script language=\"JavaScript1.2\" src=\"";
   private final static String SCRIPTS_2 = "/scripts/menu.js\"></script>\n";
   private final static String STYLES_1  = "<link rel=\"stylesheet\" href=\"";
   private final static String STYLES_2  = "/css/main.css\" TYPE=\"text/css\">\n";
}
