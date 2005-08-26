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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A non-JSF tag library that adds the HTML begin and end tags if running in servlet mode
 * 
 * @author gavinc
 */
public class PageTag extends TagSupport
{
   private static final long serialVersionUID = 8142765393181557228L;
   
   private final static String SCRIPTS_1 = "<script language=\"JavaScript1.2\" src=\"";
   private final static String SCRIPTS_2 = "/scripts/menu.js\"></script>\n";
   private final static String STYLES_1  = "<link rel=\"stylesheet\" href=\"";
   private final static String STYLES_2  = "/css/main.css\" TYPE=\"text/css\">\n";
   private final static String ALF_URL   = "http://www.alfrescosoftware.com";
   private final static String ALF_LOGO  = "http://www.alfresco.org/alfresco_logo.gif";
   private final static String ALF_TEXT  = "Content managed by Alfresco";
   private final static String ALF_COPY  = "Alfresco Software Inc. © 2005 All rights reserved.";
   
   private static Log logger = LogFactory.getLog(PageTag.class);
   private static String alfresco = null;
   private static String loginPage = null;
   
   private long startTime = 0;
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
      if (logger.isDebugEnabled())
         startTime = System.currentTimeMillis();
      
      try
      {
         Writer out = pageContext.getOut();
         
         if (Application.inPortalServer() == false)
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

   /**
    * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
    */
   public int doEndTag() throws JspException
   {
      try
      {
         HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
         if (req.getRequestURI().endsWith(getLoginPage()) == false)
         {
            pageContext.getOut().write(getAlfrescoButton());
         }
         
         if (Application.inPortalServer() == false)
         {
            pageContext.getOut().write("\n</body></html>");
         }
      }
      catch (IOException ioe)
      {
         throw new JspException(ioe.toString());
      }
      
      if (logger.isDebugEnabled())
      {
         long endTime = System.currentTimeMillis();
         logger.debug("Time to generate page: " + (endTime - startTime) + "ms");
      }
      
      return super.doEndTag();
   }
   
   private String getLoginPage()
   {
      if (loginPage == null)
      {
         loginPage = Application.getLoginPage(pageContext.getServletContext());
      }
      
      return loginPage;
   }
   
   private String getAlfrescoButton()
   {
      if (alfresco == null)
      {
         alfresco = "<center>" +
                    "<a href='" + ALF_URL + "'>" +
                    "<img border=0 alt='' title='" + ALF_TEXT + "' align=absmiddle src='" + ALF_LOGO + "'>" +
                    "</a>&nbsp;" +
                    "<span style='font-family:Arial,Helvetica,sans-serif;font-size:10px'>" + ALF_COPY +
                    "</span></center>";
      }
      
      return alfresco;
   }
}
