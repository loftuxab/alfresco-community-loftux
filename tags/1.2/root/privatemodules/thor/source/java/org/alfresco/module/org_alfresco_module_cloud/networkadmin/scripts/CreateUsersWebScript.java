package org.alfresco.module.org_alfresco_module_cloud.networkadmin.scripts;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.networkadmin.NetworkAdmin;
import org.alfresco.module.org_alfresco_module_cloud.networkadmin.NetworkAdmin.NetworkAdminRunAsWork;
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

public class CreateUsersWebScript extends DeclarativeWebScript
{
    protected static final String PARAM_SOURCE = "source";
    protected static final String PARAM_EMAILS = "emails";

    protected static final String PARAM_MESSAGE = "message";
    protected static final String PARAM_SOURCE_URL = "source_url";

    protected static final String PARAM_IP_ADDRESS       = "ipAddress";
    protected static final String PARAM_LANDING_TIME     = "landingTime";
    protected static final String PARAM_LANDING_REFERRER = "landingReferrer";
    protected static final String PARAM_LANDING_PAGE     = "landingPage";
    protected static final String PARAM_LANDING_KEYWORDS = "landingKeywords";
    protected static final String PARAM_UTM_SOURCE       = "utmSource";
    protected static final String PARAM_UTM_MEDIUM       = "utmMedium";
    protected static final String PARAM_UTM_TERM         = "utmTerm";
    protected static final String PARAM_UTM_CONTENT      = "utmContent";
    protected static final String PARAM_UTM_CAMPAIGN     = "utmCampaign";
    
	private RegistrationService registrationService;
    private NetworkAdmin networkAdmin;
    
    public void setNetworkAdmin(NetworkAdmin service)
    {
        this.networkAdmin = service;
    }

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

            final String sourceUrl = (String) json.get(PARAM_SOURCE_URL);

            Map<String, Serializable> optionalAnalyticParams = new HashMap<String, Serializable>();
            optionalAnalyticParams.put(PARAM_IP_ADDRESS,       (String) json.get(PARAM_IP_ADDRESS));
            optionalAnalyticParams.put(PARAM_LANDING_TIME,     (Long) json.get(PARAM_LANDING_TIME));
            optionalAnalyticParams.put(PARAM_LANDING_REFERRER, (String) json.get(PARAM_LANDING_REFERRER));
            optionalAnalyticParams.put(PARAM_LANDING_PAGE,     (String) json.get(PARAM_LANDING_PAGE));
            
            JSONArray landingKeywordsJson = (JSONArray) json.get(PARAM_LANDING_KEYWORDS);
            String[] landingKeywords = new String[0];
            if (landingKeywordsJson != null)
            {
                landingKeywords = (String[]) landingKeywordsJson.toArray(landingKeywords);
            }
            
            optionalAnalyticParams.put(PARAM_LANDING_KEYWORDS, (Serializable)Arrays.asList(landingKeywords));
            optionalAnalyticParams.put(PARAM_UTM_SOURCE,       (String) json.get(PARAM_UTM_SOURCE));
            optionalAnalyticParams.put(PARAM_UTM_MEDIUM,       (String) json.get(PARAM_UTM_MEDIUM));
            optionalAnalyticParams.put(PARAM_UTM_TERM,         (String) json.get(PARAM_UTM_TERM));
            optionalAnalyticParams.put(PARAM_UTM_CONTENT,      (String) json.get(PARAM_UTM_CONTENT));
            optionalAnalyticParams.put(PARAM_UTM_CAMPAIGN,     (String) json.get(PARAM_UTM_CAMPAIGN));
	        
    		List<InvalidEmail> invalidEmails = registrationService.registerEmails(emails, source, sourceUrl, message, optionalAnalyticParams);

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
        return networkAdmin.runAs(new NetworkAdminRunAsWork<Map<String, Object>>()
        {
            public Map<String, Object> doWork() throws Exception
            {
                return createUsers(req, status, cache);
            }
        });
    }
}
