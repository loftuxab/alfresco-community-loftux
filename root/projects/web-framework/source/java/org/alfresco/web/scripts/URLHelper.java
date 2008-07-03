package org.alfresco.web.scripts;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

/**
 * Class to represent the template model for a URL.
 * 
 * This class is immutable.
 * 
 * @author Kevin Roast
 */
public final class URLHelper implements Serializable
{
    private final String context;
    private final String pageContext;
    private final String uri;
    private final String queryString;
    private final Map<String, String> args;
    private final Map<String, String> templateArgs = new HashMap<String, String>(4, 1.0f);

    /**
     * Construction
     * 
     * @param req       Servlet request to build URL model helper from
     */
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
        else
        {
            this.pageContext = this.context;
        }
        this.queryString = (req.getQueryString() != null ? req.getQueryString() : "");
        
        Map<String, String> args = new HashMap<String, String>(req.getParameterMap().size());
        Enumeration names = req.getParameterNames();
        while (names.hasMoreElements())
        {
            String name = (String)names.nextElement();
            args.put(name, req.getParameter(name));
        }
        this.args = Collections.unmodifiableMap(args);
    }
    
    /**
     * Construction
     * 
     * @param req       Servlet request to build URL model helper from
     */
    public URLHelper(HttpServletRequest req, Map<String, String> templateArgs)
    {
        this(req);
        if (templateArgs != null)
        {
            this.templateArgs.putAll(templateArgs);
        }
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
    
    public Map<String, String> getTemplateArgs()
    {
        return Collections.unmodifiableMap(this.templateArgs);
    }
}
