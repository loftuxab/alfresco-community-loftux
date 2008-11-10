package org.alfresco.web.site;

import javax.servlet.http.HttpServletRequest;

public class URLUtil
{
    /**
     * Converts the web application relative URL to a browser URL
     * @param context
     * @param relativeUrl
     * @return
     */
    public static String browser(RequestContext context, String relativeUrl)
    {
        return browser(context.getRequest(), relativeUrl);
    }
    
    public static String browser(HttpServletRequest request, String relativeUrl)
    {
        if (relativeUrl == null)
            relativeUrl = "";

        String path = request.getContextPath();
        if (path.endsWith("/"))
        {
            path = path.substring(0, path.length() - 1);
        }
        if (!relativeUrl.startsWith("/"))
        {
            relativeUrl = "/" + relativeUrl;
        }

        return path + relativeUrl;
    }
}
