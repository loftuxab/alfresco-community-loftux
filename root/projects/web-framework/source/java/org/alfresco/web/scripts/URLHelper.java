package org.alfresco.web.scripts;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

/**
 * Class to represent the template model for a URL.
 * 
 * @author Kevin Roast
 */
public class URLHelper
{
    String context;
    String pageContext;
    String uri;
    String queryString;
    Map<String, String> args;

    /**
     * Construction
     * 
     * @param req       Servlet request to build URL model helper from
     */
    public URLHelper(HttpServletRequest req, Map<String, String> reqArgs)
    {
        this.context = req.getContextPath();
        this.uri = req.getRequestURI();
        String uriNoContext = req.getRequestURI().substring(this.context.length());
        StringTokenizer t = new StringTokenizer(uriNoContext, "/");
        if(t.hasMoreTokens())
        {
            this.pageContext = this.context + "/" + t.nextToken();
        }
        else
        {
            this.pageContext = this.context;
        }
        this.queryString = (req.getQueryString() != null ? req.getQueryString() : "");
        this.args = new HashMap<String, String>(reqArgs.size());
        this.args.putAll(reqArgs);
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
        return uri + (this.queryString.length() != 0 ? ("?" + this.queryString) : "");
    }

    public String getQueryString()
    {
        return this.queryString;
    }
    
    public Map<String, String> getArgs()
    {
        return this.args;
    }
}
