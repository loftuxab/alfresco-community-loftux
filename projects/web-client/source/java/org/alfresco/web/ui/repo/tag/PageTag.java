package org.alfresco.web.ui.repo.tag;

import java.io.IOException;
import java.io.Writer;

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
      if (Application.inPortalServer(pageContext.getServletContext()) == false)
      {
         try
         {
            Writer out = pageContext.getOut();
            
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
         catch (IOException ioe)
         {
            throw new JspException(ioe.toString());
         }
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
}
