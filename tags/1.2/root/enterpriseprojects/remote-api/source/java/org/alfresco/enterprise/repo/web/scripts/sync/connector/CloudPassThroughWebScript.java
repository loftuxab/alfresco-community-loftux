/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync.connector;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.alfresco.enterprise.repo.sync.connector.CloudConnectorService;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorClientException;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorRequest;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorResponse;
import org.alfresco.service.cmr.remoteticket.RemoteSystemUnavailableException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tika.io.IOUtils;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * A special proxy webscript, which will pass configured webscript
 *  requests onwards to the cloud system, with the current
 *  user's cloud credential on the new request.
 *  
 * Buffers requests locally in case the request has to be re-sent
 *  with a new ticket, so shouldn't be used for large POSTs or PUTs
 * 
 * @author Nick Burch
 * @since TODO
 */
public class CloudPassThroughWebScript extends AbstractWebScript
{
    private static Log logger = LogFactory.getLog(CloudPassThroughWebScript.class);

    /** The service to talk to the cloud with */
    protected CloudConnectorService cloudConnectorService;
    
    /** 
     * Local URL pattern to Remote URL pattern mapping.
     * These need to be relative URLs, from everything after /service/,
     *  eg /cloud/networks (local) -> /api/networks (cloud) 
     */
    private Map<String,String> mapping;

    public void setCloudConnectorService(CloudConnectorService cloudConnectorService)
    {
        this.cloudConnectorService = cloudConnectorService;
    }
    
    public void setMapping(Map<String, String> mapping)
    {
        this.mapping = mapping;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        // Take the local URL, and strip off parts that shouldn't be passed along to the cloud
        // req.getPathInfo() returns the path with /service/ stripped off, which is what
        //  we need for passing along to the Cloud Connector Service
        String url = req.getPathInfo();

        // Exclude certain parts of the query string
        StringBuffer qs = new StringBuffer();
        for (String p : req.getParameterNames())
        {
            // Skip alf_ticket if present, cloud needs a different ticket to local
            // Also skip the network name, as that gets sent to cloud differently
            if (p.equals("alf_ticket") || p.equals("network"))
            {
                // Don't send over these parameters
            }
            else
            {
                if(qs.length() > 0) qs.append("&");
                qs.append(p);
                qs.append("=");
                qs.append(req.getParameter(p));
            }
        }
        if (qs.length() > 0)
        {
            url = url + "?" + qs.toString();
        }

        // Try to map from the local URL to the Cloud URL
        for (String localBase : mapping.keySet())
        {
            if (url.startsWith(localBase))
            {
                String origUrl = url;
                url = url.replace( localBase, mapping.get(localBase) );
                
                if (logger.isDebugEnabled())
                    logger.debug("Mapped local URL " + origUrl + " to remote " + url);
                break;
            }
        }
        
        // Identify the network they want to talk to
        String network = null;
        if (req.getParameter("network") != null)
        {
            network = req.getParameter("network");
        }
        else
        {
            Map<String,String> pathVars = req.getServiceMatch().getTemplateVars();
            if (pathVars.containsKey("network"))
            {
                network = pathVars.get("network");
            }
            else
            {
                if (logger.isInfoEnabled())
                    logger.info("No network supplied, assuming the default is needed");
                network = null;
            }
        }
        
        
        // At this point, our URL is un-escaped, but it needs to be escaped again
        //  before we supply it to HTTPClient
        url = URLEncoder.encodeUri(url);

        
        // Turn this into a cloud request
        RemoteConnectorRequest cloudReq = cloudConnectorService.buildCloudRequest(
                url, network, getDescription().getMethod());
        cloudReq.setContentType(req.getContentType());
        
        // Pass along the request body, if present (eg something like a POST or PUT)
        // We have to buffer this in memory, in case the request has
        //  to be re-sent (eg ticket had expired)
        Content reqContent = req.getContent();
        if (reqContent != null)
        {
            InputStream reqStream = reqContent.getInputStream();
            if (reqStream != null)
            {
                byte[] reqData = IOUtils.toByteArray(reqStream);
                cloudReq.setRequestBody(reqData);
                reqStream.close();
            }
        }

        // Log what we're about to do
        if (logger.isDebugEnabled())
            logger.debug("Passing through " + getDescription().getMethod() +
                         " request to " + url);
        
        // Perform the request against the cloud
        RemoteConnectorResponse cloudResp = null;
        try
        {
            cloudResp = cloudConnectorService.executeCloudRequest(cloudReq);
        }
        catch (AuthenticationException e)
        {
            // Their cloud credentials weren't accepted
            throw new WebScriptException(Status.STATUS_FORBIDDEN, e.getMessage());
        }
        catch (RemoteSystemUnavailableException re)
        {
            // Problem talking to the cloud
            throw new WebScriptException(Status.STATUS_PRECONDITION_FAILED, "Error communicating with the Cloud", re);
        }
        catch (RemoteConnectorClientException rcce)
        {
            throw new WebScriptException(rcce.getStatusCode(), "Error communicating with the Cloud : " + rcce.getMessage(), rcce);
        }

        
        // Filter the response headers, and certain ones back
        for (Header hdr : cloudResp.getResponseHeaders())
        {
            if (hdr.getName().startsWith("Content"))
            {
                res.addHeader(hdr.getName(), hdr.getValue());
            }
        }

        // Send the response data
        InputStream responseStream = cloudResp.getResponseBodyAsStream();
        int size = IOUtils.copy(responseStream, res.getOutputStream());
        responseStream.close();

        if (logger.isDebugEnabled())
            logger.debug("Sent response of " + size + " bytes");
    }
}