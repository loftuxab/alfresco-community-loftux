package org.alfresco.web.site;

import org.alfresco.web.site.model.Endpoint;

public class URLUtil
{
    public static String toBrowserUrl(String rootRelativeUri)
    {
        if (rootRelativeUri == null)
            return "";

        // special case: "/"
        if (rootRelativeUri.equals("/"))
        {
            // the browser friendly url is just the preconfigured servlet
            // (i.e. /myapp/)
            String newUri = Framework.getConfig().getDefaultServletUri();
            return newUri;
        }

        // if it starts with "/", strip it off
        if (rootRelativeUri.startsWith("/"))
            rootRelativeUri = rootRelativeUri.substring(1,
                    rootRelativeUri.length());

        // now build the browser friendly ur
        String defaultUri = Framework.getConfig().getDefaultServletUri();
        String newUri = defaultUri + rootRelativeUri;
        return newUri;
    }

    public static String getContentEditURL(RequestContext context,
            String endpointId, String itemRelativePath)
    {
        // use default endpoint id if none specified
        if (endpointId == null)
            endpointId = RenderUtil.DEFAULT_ALFRESCO_ENDPOINT_ID;

        // get the endpoint
        Endpoint endpoint = context.getModel().loadEndpoint(context,
                endpointId);

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
