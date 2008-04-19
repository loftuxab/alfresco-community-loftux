package org.alfresco.web.scripts;

import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

public class URLHelper
{
    String context;
    String pageContext;
    String uri;
    String args;
    

    public URLHelper(HttpServletRequest req)
    {
       this.context = req.getContextPath();
       this.uri = req.getRequestURI();
       String uriNoContext = req.getRequestURI().substring(this.context.length());
       StringTokenizer t = new StringTokenizer(uriNoContext, "/");
       if(t.hasMoreTokens())
       {
           this.pageContext = this.context + "/" + t.nextToken();
       }
       this.args = (req.getQueryString() != null ? req.getQueryString() : "");
    }

    public String getContext()
    {
       return context;
    }
    
    public String getServletContext()
    {
       return pageContext;
    }
    
    public String getUri()
    {
       return uri;
    }
    
    public String getUrl()
    {
       return uri + (this.args.length() != 0 ? ("?" + this.args) : "");
    }
    
    public String getArgs()
    {
       return this.args;
    }
}
