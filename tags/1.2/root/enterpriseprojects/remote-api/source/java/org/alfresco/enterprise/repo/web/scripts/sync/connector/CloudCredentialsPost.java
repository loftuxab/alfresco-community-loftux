/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.sync.connector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.enterprise.repo.web.scripts.sync.AbstractCloudSyncDeclarativeWebScript;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.service.cmr.remoteticket.RemoteSystemUnavailableException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the SOURCE controller for the Cloud Credentials credentials.post web script.
 * 
 * @author Nick Burch
 * @since 4.1
 */
public class CloudCredentialsPost extends AbstractCloudSyncDeclarativeWebScript
{
    @Override protected Map<String, Object> executeSyncImpl(WebScriptRequest req, Status status, Cache cache)
    {
        // Parse the JSON
        JSONObject json = null;
        JSONParser parser = new JSONParser();
        try
        {
            json = (JSONObject)parser.parse(req.getContent().getContent());
        }
        catch (IOException io)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Invalid JSON", io);
        }
        catch (ParseException je)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Invalid JSON", je);
        }

        // Grab their details
        String username = (String)json.get("username");
        String password = (String)json.get("password");
        if (username == null || password == null)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Required JSON data missing");
        }

        // Have the details checked + stored
        boolean loginValid = false;
        boolean remoteSystemAvailable = false;
        String message = null;
        try
        {
            cloudConnectorService.storeCloudCredentials(username, password);
            loginValid = true;
            remoteSystemAvailable = true;
        }
        catch(AuthenticationException ae)
        {
            loginValid = false;
            remoteSystemAvailable = true;
            message = "Login Invalid";
        }
        catch(RemoteSystemUnavailableException e)
        {
            // We can't talk to cloud
            remoteSystemAvailable = false;
            loginValid = false;
            message = e.getMessage();
        }

        // Report how it went
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("remoteSystemAvailable", remoteSystemAvailable);
        model.put("loginValid", loginValid);
        model.put("username", username);
        model.put("message", message);

        return model;
    }
}