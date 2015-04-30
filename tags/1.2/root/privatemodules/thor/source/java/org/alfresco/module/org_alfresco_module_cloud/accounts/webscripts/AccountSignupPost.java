/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_cloud.accounts.webscripts;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.directory.InvalidEmailAddressException;
import org.alfresco.module.org_alfresco_module_cloud.registration.Registration;
import org.alfresco.util.ParameterCheck;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the account-signup.post web script.
 * 
 * @author Neil Mc Erlean
 * @since Alfresco Cloud Module 0.1 (Thor)
 */
public class AccountSignupPost extends AbstractAccountSignupWebscript
{
    private static final String FTL_MODEL_SIGNUP_REQUEST = "signupRequest";
    private static final String PARAM_SOURCE = "source";
    private static final String PARAM_SOURCE_URL = "sourceUrl";
    
    public static final String PARAM_IP_ADDRESS       = "ipAddress";
    public static final String PARAM_LANDING_TIME     = "landingTime";
    public static final String PARAM_LANDING_REFERRER = "landingReferrer";
    public static final String PARAM_LANDING_PAGE     = "landingPage";
    public static final String PARAM_LANDING_KEYWORDS = "landingKeywords";
    public static final String PARAM_UTM_SOURCE       = "utmSource";
    public static final String PARAM_UTM_MEDIUM       = "utmMedium";
    public static final String PARAM_UTM_TERM         = "utmTerm";
    public static final String PARAM_UTM_CONTENT      = "utmContent";
    public static final String PARAM_UTM_CAMPAIGN     = "utmCampaign";
    
    @SuppressWarnings("unchecked")
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>();
        
        try
        {
            JSONObject json = (JSONObject) JSONValue.parseWithException(req.getContent().getContent());
            
            // Mandatory JSON data
            final String email = (String) json.get(PARAM_EMAIL);
            final String source = (String) json.get(PARAM_SOURCE);
            ParameterCheck.mandatoryString(PARAM_EMAIL, email);
            ParameterCheck.mandatoryString(PARAM_SOURCE, source);

            // optional, indicates preRegistered signup
            final String firstName = (String) json.get(PARAM_FIRST_NAME);
            final String lastName = (String) json.get(PARAM_LAST_NAME);
            final String password = (String) json.get(PARAM_PASSWORD);

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
            
            Registration registration = getRegistrationService().registerEmail(email, firstName, lastName, password, source, sourceUrl, null, optionalAnalyticParams);
            
            model.put(FTL_MODEL_SIGNUP_REQUEST, registration);
        }
        catch (ParseException p)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not parse JSON from request.", p);
        }
        catch (InvalidEmailAddressException e)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Invalid Email Address.", e);
        }
        catch (IOException e)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Could not read content from req.", e);
        }
        
        return model;
    }
}
