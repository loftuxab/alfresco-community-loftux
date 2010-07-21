/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
package org.alfresco.web.config.forms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.extensions.surf.exception.ConnectorServiceException;

/**
 * Evaluator that determines whether a given task has a particular node type.
 * 
 * @author Gavin Cornwell
 */
public class TaskTypeEvaluator extends ServiceBasedEvaluator
{
    protected static final String JSON_DATA = "data";
    protected static final String JSON_DEFINITION = "definition";
    protected static final String JSON_TYPE = "type";
    protected static final String JSON_NAME = "name";
    
    protected static final Pattern taskIdPattern = Pattern.compile(".+\\$[0-9]+");
    
    private static Log logger = LogFactory.getLog(TaskTypeEvaluator.class);

    @Override
    protected Log getLogger()
    {
        return logger;
    }

    /**
     * Determines whether the given node type matches the path of the given object.
     * 
     * @see org.alfresco.config.evaluator.Evaluator#applies(java.lang.Object,
     *      java.lang.String)
     */
    public boolean applies(Object obj, String condition)
    {
        boolean result = false;

        if (obj instanceof String)
        {
            String taskId = (String)obj;
            
            // make sure we're looking for something that looks like a task id
            Matcher m = taskIdPattern.matcher(taskId);
            if (m.matches())
            {
                try
                {
                    // get the task instance details
                    String jsonResponseString = callService("/api/task-instances/" + taskId);
    
                    // determine whether the condition matches
                    if (jsonResponseString != null)
                    {
                        JSONObject json = new JSONObject(new JSONTokener(jsonResponseString));
                        if (json.has(JSON_DATA))
                        {
                            JSONObject dataObj = json.getJSONObject(JSON_DATA);
                            if (dataObj.has(JSON_DEFINITION))
                            {
                                JSONObject defObj = dataObj.getJSONObject(JSON_DEFINITION);
                                if (defObj.has(JSON_TYPE))
                                {
                                    JSONObject typeObj = defObj.getJSONObject(JSON_TYPE);
                                    if (typeObj.has(JSON_NAME))
                                    {
                                        String type = dataObj.getString(JSON_NAME);
                                        result = (condition.equals(type));
                                    }
                                }
                            }
                        }
                    }
                    else if (getLogger().isWarnEnabled())
                    {
                        getLogger().warn("Task instances service response appears to be null!");
                    }
                }
                catch (NotAuthenticatedException ne)
                {
                   // ignore the fact that the lookup failed, the form UI component
                   // will handle this and return the appropriate status code.
                }
                catch (ConnectorServiceException e)
                {
                    if (getLogger().isWarnEnabled())
                    {
                        getLogger().warn("Failed to connect to task instances service.", e);
                    }
                }
                catch (JSONException je)
                {
                    if (getLogger().isWarnEnabled())
                    {
                        getLogger().warn("Failed to find task type in JSON response from task instances service.", je);
                    }
                }
            }
        }

        return result;
    }
}