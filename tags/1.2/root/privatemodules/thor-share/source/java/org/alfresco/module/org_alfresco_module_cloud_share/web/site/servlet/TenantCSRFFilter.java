package org.alfresco.module.org_alfresco_module_cloud_share.web.site.servlet;

import org.alfresco.web.site.servlet.CSRFFilter;

import javax.servlet.http.HttpServletRequest;

/**
 * A tenant specific CSRF Filter class that will make sure the paths are corrected (the tenant name is excluded).
 *
 * @author Erik Winlof
 */
public class TenantCSRFFilter extends CSRFFilter {

    /**
     * Returns the path for a request, where a path is the request uri with the request context and tenant name
     * stripped out.
     *
     * @param request The http request
     * @return The path for a request where a path is the request uri with the request context an tenant name stripped out.
     */
    @Override
    protected String getPath(HttpServletRequest request)
    {
        String path = super.getPath(request);
        // Remove the front slash
        path = path.substring(1);
        int i = path.indexOf("/");
        if (i > 0)
        {
            path = path.substring(i);
        }
        // Remove double slashes from certain rewritten urls
        path = path.replace("\\/\\/", "/");
        return path;
    }

}
