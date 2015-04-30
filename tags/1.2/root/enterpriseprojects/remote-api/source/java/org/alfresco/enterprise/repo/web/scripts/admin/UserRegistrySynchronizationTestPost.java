/*
 * Copyright 2013-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts.admin;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * @author Mark Rogers
 * @since 4.2
 */
public class UserRegistrySynchronizationTestPost extends UserRegistrySynchronizationTestWebScript
{
    protected static Log logger = LogFactory.getLog(UserRegistrySynchronizationTestPost.class);
    
    public void init()
    {
        super.init();
    }
    
    @Override
    TestCommand parseCommand(WebScriptRequest req)
    {
        TestCommand ret = new TestCommand();
        
        // read command line              
        // Extract command from JSON POS
        Content c = req.getContent();
        if (c == null)
        {
           throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Missing POST body.");
        }
        JSONObject json;
        try
        {               
            json = new JSONObject(c.getContent());
                      
            if (!json.has(PARAM_AUTHENTICATOR_NAME) || json.getString(PARAM_AUTHENTICATOR_NAME).length() == 0)
            {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                    PARAM_AUTHENTICATOR_NAME + " is a required POST parameter.");
            }
            ret.authenticator = json.getString(PARAM_AUTHENTICATOR_NAME);
            
            if (json.has(PARAM_MAX_ITEMS))
            {
            
                ret.maxItems = json.getInt(PARAM_MAX_ITEMS);
            }
            
            return ret;
        }
        catch (JSONException je)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST,
               "Unable to parse JSON body.", je);
        }
        catch (IOException je)
        {
            throw new WebScriptException(Status.STATUS_NO_CONTENT,
               "Unable to read body.", je);
        }
    }
}