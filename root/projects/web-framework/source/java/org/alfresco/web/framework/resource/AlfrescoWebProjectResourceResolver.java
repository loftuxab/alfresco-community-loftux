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
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.framework.resource;

import org.alfresco.tools.WebUtil;
import org.alfresco.web.config.WebFrameworkConfigElement.ResourceResolverDescriptor;
import org.alfresco.web.site.FrameworkHelper;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.WebFrameworkConstants;

/**
 * Resolves URI references to Alfresco Repository objects hosted within Alfresco
 * 3.0 Sites
 * 
 * @author muzquiano
 */
public class AlfrescoWebProjectResourceResolver extends
        AbstractAlfrescoResourceResolver
{
    private static final String AVM_WEBAPPS_PREFIX = "/www/avm_webapps";

    /**
     * Instantiates a new alfresco web project resource resolver.
     * 
     * @param resource the resource
     */
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

        if (FrameworkHelper.getConfig().isPreviewEnabled())
        {
            // path to web application
            builder.append(context.getRequest().getContextPath());
            
            // virtualized content retrieval proxy
            builder.append("/v");            

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
            // get the alias path
            ResourceResolverDescriptor descriptor = FrameworkHelper.getConfig().getResourceResolverDescriptor(this.resource.getType());
            if (descriptor != null)
            {
                String aliasPath = descriptor.getStringProperty("alias-uri");
                
                // construct a URI to the alias'd path
                String relativePath = aliasPath + convertToRelativePath(this.resource.getValue()); 
                builder.append(relativePath);
            }
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

        // convert to relative path
        String relativePath = convertToRelativePath(this.resource.getValue());
        
        // html encode the relative path
        // TODO: this is a pretty lame attempt
        relativePath = relativePath.replace(" ", "%20");
        
        // determine the webappId
        String webappId = null;
        if (FrameworkHelper.getConfig().isPreviewEnabled())
        {
            webappId = (String) context.getValue(WebFrameworkConstants.WEBAPP_ID_REQUEST_CONTEXT_NAME);
        }
        else
        {
            webappId = extractWebappId(this.resource.getValue());
        }
        if (webappId == null)
        {
            // assume ROOT
            webappId = "ROOT";
        }
        
        // determine the storeId
        String storeId = null;
        if (FrameworkHelper.getConfig().isPreviewEnabled())
        {
            storeId = (String) context.getValue(WebFrameworkConstants.STORE_ID_REQUEST_CONTEXT_NAME);
        }
        else
        {
            storeId = extractStoreId(this.resource.getValue());
            
            if (storeId == null)
            {
                // get the alias path
                ResourceResolverDescriptor descriptor = FrameworkHelper.getConfig().getResourceResolverDescriptor(this.resource.getType());
                if (descriptor != null)
                {
                    storeId = descriptor.getStringProperty("store-id");
                }                
            }
        }
        
        builder.append("/webframework/avm/metadata/");
        builder.append(storeId);
        builder.append("/");
        builder.append(webappId);
        
        if (!relativePath.startsWith("/"))
        {
            builder.append("/");
        }
        
        builder.append(relativePath);

        return builder.toString();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.AbstractResourceResolver#getBrowserDownloadURI(org.alfresco.web.site.RequestContext)
     */
    public String getBrowserDownloadURI(RequestContext context)
    {
        String url = getDownloadURI(context);

        // if the URL starts with "/", then make it absolute
        url = WebUtil.toFullyQualifiedURL(context, url);            

        return url;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.AbstractResourceResolver#getBrowserMetadataURI(org.alfresco.web.site.RequestContext)
     */
    public String getBrowserMetadataURI(RequestContext context)
    {
        String url = getMetadataURI(context);
        
        // if the URL starts with "/", then make it absolute
        url = WebUtil.toFullyQualifiedURL(context, url);            
        
        return url;
    }

    /**
     * Converts an avm object id to a webapp relative path
     * 
     * Input can be of the form:
     * 
     * - avm://storeId/<version>;<comma-delimited-path>
     * avm://storeId/-1;www;avm_webapps;ROOT;products;product.xml
     * 
     * - /www/avm_webapps/ROOT/<relativePath>
     * /www/avm_webapps/ROOT/products/product.xml
     * 
     * Output will be of the form:
     * - <relativePath> /products/product.xml
     * 
     * @param objectId the object id
     * 
     * @return the relative path to the object
     */
    protected static String convertToRelativePath(String objectId)
    {
        String path = objectId;

        // convert down to path if starts with avm://
        if (path.startsWith("avm://"))
        {
            // object id looks like
            // avm://storeId/-1;www;avm_webapps;ROOT;products;product.xml

            // strip off the protocol, store id and version information
            int x = path.indexOf(";");
            path = path.substring(x);

            // convert to path
            path = path.replace(";", "/");
        }

        // strip down to relative path if starts with /www
        if (path.startsWith(AVM_WEBAPPS_PREFIX))
        {
            // object id looks like
            // /www/avm_webapps/<webappId>/<relativePath>

            // strip off the avm webapps prefix
            int x = path.indexOf(AVM_WEBAPPS_PREFIX);
            path = path.substring(x + AVM_WEBAPPS_PREFIX.length());

            // strip out the webapp id
            x = path.indexOf("/", 2);
            path = path.substring(x);
        }

        return path;
    }

    /**
     * Extracts the store id from the avm:// object id.
     * 
     * @param objectId the object id
     * 
     * @return the store id
     */
    protected static String extractStoreId(String objectId)
    {
        String storeId = null;
        
        if (objectId.startsWith("avm://"))
        {
            // object id looks like
            // avm://storeId/-1;www;avm_webapps;ROOT;products;product.xml

            int x = objectId.indexOf("/", 7);
            storeId = objectId.substring(6, x);
        }
        
        return storeId;
    }    

    /**
     * Extracts the webapp id from the avm:// object id.
     * 
     * @param objectId the object id
     * 
     * @return the webapp id
     */
    protected static String extractWebappId(String objectId)
    {
        String webappId = null;
        
        if (objectId.startsWith("avm://"))
        {
            // object id looks like
            // avm://storeId/-1;www;avm_webapps;ROOT;products;product.xml

            String path = objectId.replace(";", "/");
            int x = path.indexOf(AVM_WEBAPPS_PREFIX);
            
            path = path.substring(x + AVM_WEBAPPS_PREFIX.length() + 1);
            
            int y = path.indexOf("/");
            webappId = path.substring(0,y);
        }
        
        return webappId;
    }    
}
