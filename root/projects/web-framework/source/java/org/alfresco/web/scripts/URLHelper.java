package org.alfresco.web.scripts;

import javax.servlet.http.HttpServletRequest;

public class URLHelper
{
   String context;
   String url;
   String args;

   public URLHelper(HttpServletRequest req)
   {
      this.context = req.getContextPath();
      this.url = req.getRequestURI();
      this.args = (req.getQueryString() != null ? req.getQueryString() : "");
   }

   public String getContext()
   {
      return context;
   }
   
   public String getFull()
   {
      return url;
   }
   
   public String getArgs()
   {
      return this.args;
   }
}
