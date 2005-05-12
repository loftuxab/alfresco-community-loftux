package org.alfresco.web.ui.repo.tag;

import java.io.IOException;
import java.io.Writer;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.alfresco.web.MyFacesPortlet;
import org.alfresco.web.bean.ErrorBean;

/**
 * A non-JSF tag library that displays the currently stored system error
 * 
 * @author gavinc
 */
public class SystemErrorTag extends TagSupport
{
   private String styleClass;
   private String detailsStyleClass;
   private boolean showDetails = false;
   
   /**
    * @return Returns the showDetails.
    */
   public boolean isShowDetails()
   {
      return showDetails;
   }
   
   /**
    * @param showDetails The showDetails to set.
    */
   public void setShowDetails(boolean showDetails)
   {
      this.showDetails = showDetails;
   }
   
   /**
    * @return Returns the styleClass.
    */
   public String getStyleClass()
   {
      return styleClass;
   }
   
   /**
    * @param styleClass The styleClass to set.
    */
   public void setStyleClass(String styleClass)
   {
      this.styleClass = styleClass;
   }
   
   /**
    * @return Returns the detailsStyleClass.
    */
   public String getDetailsStyleClass()
   {
      return detailsStyleClass;
   }

   /**
    * @param detailsStyleClass The detailsStyleClass to set.
    */
   public void setDetailsStyleClass(String detailsStyleClass)
   {
      this.detailsStyleClass = detailsStyleClass;
   }

   /**
    * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      String errorMessage = "No error currently stored";
      String errorDetails = "No details";
      
      // get the error details from the bean, this may be in a portlet
      // session or a normal servlet session.
      ErrorBean errorBean = null;
      RenderRequest renderReq  = (RenderRequest)pageContext.getRequest().
                                   getAttribute("javax.portlet.request");
      if (renderReq != null)
      {
         PortletSession session = renderReq.getPortletSession();
         errorBean = (ErrorBean)session.getAttribute(MyFacesPortlet.ERROR_BEAN_NAME);
      }
      else
      {
         errorBean = (ErrorBean)pageContext.getSession().
                        getAttribute(MyFacesPortlet.ERROR_BEAN_NAME);
      }

      if (errorBean != null)
      {
         errorMessage = errorBean.getLastErrorMessage();
         errorDetails = errorBean.getStackTrace();
      }
      
      try
      {
         Writer out = pageContext.getOut();
         
         out.write("<div");
         
         if (this.styleClass != null)
         {
            out.write(" class='");
            out.write(this.styleClass);
            out.write("'");
         }
         
         out.write(">");
         out.write(errorMessage);
         out.write("</div>");
         
         // work out initial state
         boolean hidden = !this.showDetails; 
         String display = "none";
         String toggleTitle = "Hide";
         if (this.showDetails)
         {
            display = "inline";
            toggleTitle = "Show";
         }
         
         // output the script to handle toggling of details
         out.write("<script language='JavaScript'>\n");
         out.write("var hidden = ");
         out.write(Boolean.toString(hidden));
         out.write(";\n");   
         out.write("function toggleDetails() {\n");
         out.write("if (hidden) {\n");
         out.write("document.getElementById('detailsTitle').innerHTML = 'Hide Details<br/><br/>';\n");
         out.write("document.getElementById('details').style.display = 'inline';\n");
         out.write("hidden = false;\n");
         out.write("} else {\n");
         out.write("document.getElementById('detailsTitle').innerHTML = 'Show Details';\n");
         out.write("document.getElementById('details').style.display = 'none';\n");
         out.write("hidden = true;\n");
         out.write("} } </script>\n");
         
         // output the initial toggle state
         out.write("<br/>");
         out.write("<a id='detailsTitle' href='javascript:toggleDetails();'>");
         out.write(toggleTitle);
         out.write(" Details</a>");
         
         out.write("<div style='padding-top:5px;display:");
         out.write(display);
         out.write("' id='details'");
         
         if (this.detailsStyleClass != null)
         {
            out.write(" class='");
            out.write(this.detailsStyleClass);
            out.write("'");
         }
         
         out.write(">");
         out.write(errorDetails);
         out.write("</div>");
      }
      catch (IOException ioe)
      {
         throw new JspException(ioe);
      }
      
      return SKIP_BODY;
   }
   
   /**
    * @see javax.servlet.jsp.tagext.TagSupport#release()
    */
   public void release()
   {
      this.styleClass = null;
      
      super.release();
   }
}
