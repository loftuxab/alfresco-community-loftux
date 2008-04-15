package org.alfresco.web.site;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.site.model.Endpoint;

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
        if (context != null && context instanceof HttpRequestContext)
        {
            HttpServletRequest request = ((HttpRequestContext) context).getRequest();
            return browser(request, relativeUrl);
        }
        return null;
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

    public static String getContentEditURL(RequestContext context,
            String endpointId, String itemRelativePath)
    {
        // use default endpoint id if none specified
        if (endpointId == null)
            endpointId = RenderUtil.DEFAULT_ALFRESCO_ENDPOINT_ID;

        // get the endpoint
        Endpoint endpoint = context.getModel().loadEndpoint(context, endpointId);

        // if the endpoint isn't found, just exit
        if (endpoint == null)
        {
            context.getLogger().debug("RenderUtil.getContentEditURL failed");
            context.getLogger().debug("Unable to find endpoint: " + endpointId);
            return "";
        }

        // endpoint settings
        String host = endpoint.getSetting("host");
        String port = endpoint.getSetting("port");
        String sandbox = context.getStoreId();
        String uri = "/alfresco/service/ads/redirect/incontext/" + sandbox + "/";

        // build the url
        String path = sandbox + ":/www/avm_webapps/ROOT" + itemRelativePath;
        String url = "http://" + host + ":" + port + uri + "?sandbox=" + sandbox + "&path=" + path + "&container=plain";

        return url;
    }

}
