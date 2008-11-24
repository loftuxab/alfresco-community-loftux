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
package org.alfresco.web.studio.servlet;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.web.site.servlet.BaseServlet;
import org.alfresco.web.studio.WebStudio;
import org.alfresco.web.studio.client.BrowserStateBean;
import org.alfresco.web.studio.client.WebStudioStateBean;
import org.alfresco.web.studio.client.WebStudioStateProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author muzquiano
 */
public class ClientStatePersistenceServlet extends BaseServlet
{
    public static final String PARAM_CONFIG = "config";
    public static final String RESULT_STATUS_OK = "ok";
    public static final String RESULT_STATUS = "status";

    public static final String OBJECT_TYPE_APPLET = "applet";
    public static final String OBJECT_TYPE_APPLICATION = "application";

    public static final String COMMAND_ENABLE = "enable";
    public static final String COMMAND_DISABLE = "disable";
    public static final String COMMAND_PUT = "put";
    public static final String COMMAND_GET = "get";
    public static final String COMMAND_REMOVE = "remove";
    public static final String COMMAND_DEPS = "deps";

    public void init() throws ServletException
    {
        super.init();
    }

    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        String uri = request.getRequestURI();

        // skip server context path and build the path to the resource
        // we are looking for
        uri = uri.substring(request.getContextPath().length());

        // validate and return the resource path - stripping the
        // servlet context
        StringTokenizer t = new StringTokenizer(uri, "/");
        String servletName = t.nextToken();
        if (!t.hasMoreTokens())
        {
            throw new ServletException("Invalid URL: " + uri);
        }

        // the type of thing we're looking at (application, applet)
        String objectType = t.nextToken();

        // the id of the thing
        String objectId = t.nextToken();

        // the command (put, get, remove, deps)
        String command = t.nextToken();

        // get the web studio state for the connecting browser
        WebStudioStateProvider provider = WebStudio.getWebStudioStateProvider();
        WebStudioStateBean state = provider.provide(request);

        // determine what we're acting upon
        BrowserStateBean bean = null;
        if (OBJECT_TYPE_APPLICATION.equals(objectType))
        {
            bean = state.getApplicationState(objectId);
        }
        else if (OBJECT_TYPE_APPLET.equals(objectType))
        {
            bean = state.getAppletState(objectId);
        }

        if (bean != null)
        {
            // JSON return
            boolean processed = false;
            JSONObject json = new JSONObject();

            try
            {
                // remove command
                if (COMMAND_REMOVE.equals(command))
                {
                    if (t.hasMoreTokens())
                    {
                        // remove a single property
                        String key = (String) t.nextToken();
                        bean.remove(key);

                        // update status
                        json.put(RESULT_STATUS, RESULT_STATUS_OK);
                        processed = true;
                    }
                    else
                    {
                        // remove all properties
                        bean.removeProperties();

                        // update status
                        json.put(RESULT_STATUS, RESULT_STATUS_OK);
                        processed = true;
                    }
                }

                // puts a single property
                if (COMMAND_PUT.equals(command))
                {
                    if (t.hasMoreTokens())
                    {
                        String key = (String) t.nextToken();
                        if (t.hasMoreTokens())
                        {
                            String value = t.nextToken();
                            bean.put(key, value);

                            // update status
                            json.put(RESULT_STATUS, RESULT_STATUS_OK);
                            processed = true;
                        }
                    }
                }

                // gets properties
                if (COMMAND_GET.equals(command))
                {
                    if (t.hasMoreTokens())
                    {
                        // get a single property
                        String key = (String) t.nextToken();
                        String value = bean.get(key);

                        // update status
                        json.put(RESULT_STATUS, RESULT_STATUS_OK);
                        json.put(key, value);
                        processed = true;
                    }
                    else
                    {
                        Map<String, String> properties = bean.getProperties();
                        Iterator it = properties.keySet().iterator();
                        while (it.hasNext())
                        {
                            String key = (String) it.next();
                            String value = (String) properties.get(key);

                            json.put(key, value);
                        }

                        json.put(RESULT_STATUS, RESULT_STATUS_OK);
                        processed = true;
                    }
                }

                if (COMMAND_ENABLE.equals(command))
                {
                    bean.enable();
                    json.put(RESULT_STATUS, RESULT_STATUS_OK);
                    processed = true;
                }

                if (COMMAND_DISABLE.equals(command))
                {
                    bean.disable();
                    json.put(RESULT_STATUS, RESULT_STATUS_OK);
                    processed = true;
                }

                if (COMMAND_DEPS.equals(command))
                {
                    JSONObject config = null;

                    String options = request.getParameter(PARAM_CONFIG);
                    if (options != null)
                    {
                        config = new JSONObject(options);

                        // clear existing dependencies
                        bean.clearDependencies();

                        // add in js files
                        JSONArray jsArray = config.getJSONArray("js");
                        for (int i = 0; i < jsArray.length(); i++)
                        {
                            String file = jsArray.getString(i);
                            bean.addJsFile(file);
                        }

                        // add in css files
                        JSONArray cssArray = config.getJSONArray("css");
                        for (int i = 0; i < cssArray.length(); i++)
                        {
                            String file = cssArray.getString(i);
                            bean.addCssFile(file);
                        }

                        // add in dom files
                        JSONArray domArray = config.getJSONArray("dom");
                        for (int i = 0; i < domArray.length(); i++)
                        {
                            String file = domArray.getString(i);
                            bean.addDomFile(file);
                        }

                        json.put(RESULT_STATUS, RESULT_STATUS_OK);
                        processed = true;
                    }
                }

                // write out the json
                if (processed)
                {
                    response.getWriter().println(json.toString());
                }
                else
                {
                    response.getWriter().println(
                            "Unable to execute command: " + command);
                }
            }
            catch (JSONException jsonException)
            {
                throw new ServletException(jsonException);
            }
        }
    }
}
