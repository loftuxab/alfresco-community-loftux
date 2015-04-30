package org.alfresco.module.org_alfresco_module_cloud.accounts.webscripts;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationService;
import org.alfresco.module.org_alfresco_module_cloud.registration.RegistrationServiceImpl.InvalidEmail;
import org.alfresco.util.ParameterCheck;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class AccountInitiatedSignupPost extends DeclarativeWebScript
{
    protected static final String PARAM_SOURCE = "source";
    protected static final String PARAM_EMAILS = "emails";

    protected static final String PARAM_MESSAGE = "message";

    private RegistrationService registrationService;

    public void setRegistrationService(RegistrationService registrationService)
    {
        this.registrationService = registrationService;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> createUsers(WebScriptRequest req, Status status, Cache cache)
    {
        JSONObject json = null;

        try
        {
            json = (JSONObject) JSONValue.parseWithException(req.getContent().getContent());

            final JSONArray jsonEmails = (JSONArray)json.get(PARAM_EMAILS);
            if(jsonEmails == null || jsonEmails.size() == 0)
            {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST, "must provided at least 1 email address");
            }

            final String message = (String)json.get(PARAM_MESSAGE);

            List<String> emails = new ArrayList<String>(jsonEmails.size());
            for(int i = 0; i < jsonEmails.size(); i++)
            {
                emails.add((String)jsonEmails.get(i));
            }

            // analytics data
            final String source = (String) json.get(PARAM_SOURCE);
            ParameterCheck.mandatoryString(PARAM_SOURCE, source);

            Map<String, Serializable> optionalAnalyticParams = new HashMap<String, Serializable>();

            List<InvalidEmail> invalidEmails = registrationService.registerEmails(emails, source, null, message, optionalAnalyticParams, false);

            Map<String, Object> model = new HashMap<String, Object>();
            model.put("invalidEmails", invalidEmails);
            return model;
        }
        catch (ParseException p)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not parse JSON from request.", p);
        }
        catch(IOException e)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected Map<String, Object> executeImpl(final WebScriptRequest req, final Status status, final Cache cache)
    {
        return createUsers(req, status, cache);
    }
}
