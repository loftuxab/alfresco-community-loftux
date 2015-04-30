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
package org.alfresco.module.org_alfresco_module_cloud_share.extensibility;

import java.util.Map;

import org.alfresco.web.extensibility.SlingshotEvaluatorUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.ExtensionModuleEvaluator;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.connector.User;

/**
 * <p>
 * Evaluator used to decide if an extension module (and its {@code<components>} & {@code<customizations>}) shall be
 * used for this request.
 * </p>
 *
 * <p>
 * Makes it possible to decide if we are inside the cloud and if the user's account matches the one defined
 * in the {@code<accountClassNames>} parameter. The available account types can be found in the thor modules
 * org.alfresco.module.org_alfresco_module_cloud.accounts.AccountClass's Name enum.
 * </p>
 *
 * <p>
 * Note! The regexp is expressed without using the surrounding // characters.
 * </p>
 *
 * <p>
 * Example:
 * </p>
 *
 * <pre>{@code
 * <evaluator type="cloud-account.module.evaluator">
 *    <params>
 *       <accountClassNames>PUBLIC_EMAIL_DOMAIN|PRIVATE_EMAIL_DOMAIN</accountClassNames>
 *    </params>
 * </evaluator>
 * }</pre>
 *
 * <p>
 * Will return true of we are viewed from inside a portal and that portal's url matches the regexp in the
 * {@code<accountClassNames>} parameter.
 * </p>
 *
 * @author ewinlof
 */
public class SlingshotCloudAccountModuleEvaluator implements ExtensionModuleEvaluator
{
    private static Log logger = LogFactory.getLog(SlingshotCloudAccountModuleEvaluator.class);

    /* Evaluator parameters */
    public static final String ACCOUNT_CLASS_NAMES_FILTER = "accountClassNames";

    protected SlingshotEvaluatorUtil util = null;

    public void setSlingshotEvaluatorUtil(SlingshotEvaluatorUtil slingshotExtensibilityUtil)
    {
        this.util = slingshotExtensibilityUtil;
    }

    public String[] getRequiredProperties()
    {
        String[] properties = new String[1];
        properties[0] = ACCOUNT_CLASS_NAMES_FILTER;
        return properties;
    }

    /**
     * Decides if we are inside the cloud with a specified account url
     *
     * @param context
     * @param params
     * @return true if we are in a portlet and its url matches the {@code<accountClassNames>} param (defaults to ".*")
     */
    public boolean applyModule(RequestContext context, Map<String, String> params)
    {
        String accountClassNamesFilter = util.getEvaluatorParam(params, ACCOUNT_CLASS_NAMES_FILTER, ".*");
        if (accountClassNamesFilter.equals(".*"))
        {
            // Avoid making the rest api call
            return true;
        }
        
        String accountClassName = null;
        try
        {
            // attempt to use the cached information for the current user request context
            RequestContext rc = ThreadLocalRequestContext.getRequestContext();
            if (rc != null)
            {
                User user = rc.getUser();
                if (user != null && !user.isGuest())
                {
                    accountClassName = (String)user.getProperty("accountClassName");
                }
            }
            if (accountClassName == null)
            {
                JSONObject info = util.jsonGet("/internal/cloud/current-user");
                if (info != null)
                {
                    if (info.has("accountClassName"))
                    {
                        accountClassName = info.getString("accountClassName");
                    }
                }
            }
        }
        catch (JSONException e)
        {
            if (logger.isErrorEnabled())
                logger.error("Could not get a current user from json.");
        }
        
        // Check if we are viewed from inside a portlet
        // Turn id into a string and use a regexp to match against the configured account types
        return accountClassName != null ? accountClassName.matches(accountClassNamesFilter) : false;
    }
}
