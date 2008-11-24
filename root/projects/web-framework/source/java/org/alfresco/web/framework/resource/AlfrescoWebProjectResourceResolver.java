/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.framework.resource;

import org.alfresco.web.framework.ModelPersistenceContext;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.RequestContext;

/**
 * Resolves URI references to Alfresco Repository objects hosted
 * within Alfresco 3.0 Sites
 * 
 * @author muzquiano
 */
public class AlfrescoWebProjectResourceResolver extends
        AbstractAlfrescoResourceResolver
{
    public AlfrescoWebProjectResourceResolver(Resource resource)
    {
        super(resource);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceResolver#getDownloadURI(org.alfresco.web.site.RequestContext)
     */
    public String getDownloadURI(RequestContext context)
    {
        StringBuilder builder = new StringBuilder(512);

        if (FrameworkHelper.getConfig().isWebStudioEnabled())
        {
            builder.append("/remotestore/get");

            // append the store id
            ModelPersistenceContext mpc = context.getModel().getObjectManager()
                    .getContext();
            String storeId = (String) mpc
                    .getValue(ModelPersistenceContext.REPO_STOREID);
            builder.append("/s");
            builder.append("/");
            builder.append(storeId);

            // append in the webapp id
            String webappId = (String) mpc
                    .getValue(ModelPersistenceContext.REPO_WEBAPPID);
            if (webappId != null)
            {
                builder.append("/w");
                builder.append("/");
                builder.append(webappId);
            }

            // append in the URI path
            String value = this.resource.getValue();
            if (value != null)
            {
                if (!value.startsWith("/"))
                {
                    value = "/" + value;
                }

                builder.append(value);
            }
        }
        else
        {
            builder.append(this.resource.getValue());
        }

        return builder.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.ResourceResolver#getMetadataURI(org.alfresco.web.site.RequestContext)
     */
    public String getMetadataURI(RequestContext context)
    {
        StringBuilder builder = new StringBuilder(512);
        if (FrameworkHelper.getConfig().isWebStudioEnabled())
        {
            String path = this.resource.getValue();

            // path treatment (special case)
            if (path.startsWith("avm://"))
            {
                // then it is a full path and we want to strip it down
                // into the format we expect
                // we expect a "/" separated path relative to the web
                // application
                path = path.replaceAll(";", "/");

                int x = path.indexOf("/www/avm_webapps/");
                if (x > -1)
                {
                    path = path.substring(x + 17);
                    int y = path.indexOf("/", 1);
                    if (y > -1)
                    {
                        path = path.substring(y + 1);
                    }
                }
            }

            String webappId = (String) context.getModel().getObjectManager()
                    .getContext().getValue(
                            ModelPersistenceContext.REPO_WEBAPPID);
            if (webappId == null)
            {
                // assume ROOT
                webappId = "ROOT";
            }

            // HTML encode the path
            path = path.replaceAll(" ", "%20");

            String storeId = (String) context.getModel().getObjectManager()
                    .getContext()
                    .getValue(ModelPersistenceContext.REPO_STOREID);
            if (storeId != null)
            {
                builder.append("/webframework/avm/metadata/");
                builder.append(storeId);
                builder.append("/");
                builder.append(webappId);
                builder.append("/");
                builder.append(path);
            }
        }

        return builder.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.AbstractResourceResolver#getProxiedDownloadURI(org.alfresco.web.site.RequestContext)
     */
    public String getProxiedDownloadURI(RequestContext context)
    {
        String url = getDownloadURI(context);

        if (FrameworkHelper.getConfig().isWebStudioEnabled())
        {
            url = "/proxy/{endpoint}" + url;

            String ep = this.resource.getEndpoint();
            if (ep == null)
            {
                ep = "alfresco";
            }
            url = url.replace("{endpoint}", ep);
        }

        return url;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.framework.resource.AbstractResourceResolver#getProxiedMetadataURI(org.alfresco.web.site.RequestContext)
     */
    public String getProxiedMetadataURI(RequestContext context)
    {
        String url = getMetadataURI(context);

        if (FrameworkHelper.getConfig().isWebStudioEnabled())
        {
            url = "/proxy/{endpoint}" + url;

            String ep = this.resource.getEndpoint();
            if (ep == null)
            {
                ep = "alfresco";
            }
            url = url.replace("{endpoint}", ep);
        }

        return url;
    }

}
