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
package org.alfresco.web.site;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Convenience methods for working with in-context state
 * 
 * @author muzquiano
 */
public class InContextUtil
{
    public static String INCONTEXT_ELEMENT_ID = "incontext";

    public static String INCONTEXT_TOGGLE_REQUEST_PARAM = "toggle";
    public static String INCONTEXT_ELEMENT_REQUEST_PARAM = "element";
    public static String INCONTEXT_VALUE_REQUEST_PARAM = "value";

    protected static Map getInContextMap(HttpSession session)
    {
        Map map = (Map) session.getAttribute("incontext.map");
        if (map == null)
        {
            map = new HashMap();
            session.setAttribute("incontext.map", map);

            // set up default values
            String[] elementIds = getInContextElementIds();
            for (int i = 0; i < elementIds.length; i++)
            {
                // enabled
                String enabledValue = (String) getInContextElementDefaultEnabled(elementIds[i]);
                boolean enabled = false;
                if ("true".equalsIgnoreCase(enabledValue))
                    enabled = true;
                InContextUtil.setInContextElementEnabled(session,
                        elementIds[i], enabled);

                // state
                String stateValue = (String) getInContextElementDefaultState(elementIds[i]);
                InContextUtil.toggleInContext(session, elementIds[i],
                        stateValue);
            }

        }
        return map;
    }

    public static boolean isEnabled(HttpSession session)
    {
        return isElementEnabled(session, INCONTEXT_ELEMENT_ID);
    }

    public static boolean isElementEnabled(HttpSession session, String elementId)
    {
        return getBoolean(session, elementId + ".enabled");

    }

    public static String getElementState(HttpSession session, String elementId)
    {
        return get(session, elementId + ".state");
    }

    /**
     * Handles the enable/disable of in-context elements
     * Also handles updates of in-context element states
     * This is primarily utilized by the InContextServlet
     * @param context
     * @param request
     */
    public static void processRequest(HttpServletRequest request)
    {
        HttpSession session = request.getSession();

        // was anything toggled?
        String toggle = (String) request.getParameter(INCONTEXT_TOGGLE_REQUEST_PARAM);
        if (toggle != null && !"".equals(toggle))
        {
            // what was toggled?
            String elementId = (String) request.getParameter(INCONTEXT_ELEMENT_REQUEST_PARAM);
            if (elementId != null && !"".equals(elementId))
            {
                // toggle something on
                if ("on".equalsIgnoreCase(toggle))
                {
                    setInContextElementEnabled(session, elementId, true);
                }

                // toggle something off
                if ("off".equalsIgnoreCase(toggle))
                {
                    setInContextElementEnabled(session, elementId, false);
                }

                // update state
                if ("update".equalsIgnoreCase(toggle))
                {
                    String value = (String) request.getParameter(INCONTEXT_VALUE_REQUEST_PARAM);
                    if (value != null && !"".equals(value))
                        toggleInContext(session, elementId + ".state", value);
                }
            }
        }
    }

    public static void setInContextElementEnabled(HttpSession session,
            String elementId, boolean value)
    {
        if (value)
            toggleInContext(session, elementId + ".enabled", "true");
        else
            toggleInContext(session, elementId + ".enabled", "false");
    }

    public static void setInContextElementState(HttpSession session,
            String elementId, String value)
    {
        toggleInContext(session, elementId + ".state", value);
    }

    protected static void toggleInContext(HttpSession session, String key,
            String value)
    {
        if (session != null)
        {
            getInContextMap(session).put(key, value);
        }
    }

    protected static String get(HttpSession session, String key)
    {
        return (String) getInContextMap(session).get(key);
    }

    protected static boolean getBoolean(HttpSession session, String key)
    {
        String value = get(session, key);
        if ("true".equalsIgnoreCase(value))
            return true;
        return false;
    }

    public static String[] getInContextElementIds()
    {
        return Framework.getConfig().getInContextElementIds();
    }

    public static String getInContextElementDefaultEnabled(String elementId)
    {
        return Framework.getConfig().getInContextElementDefaultEnabled(
                elementId);
    }

    public static String getInContextElementDefaultState(String elementId)
    {
        return Framework.getConfig().getInContextElementDefaultState(elementId);
    }

    public static String getInContextElementName(String elementId)
    {
        return Framework.getConfig().getInContextElementName(elementId);
    }

    public static String getInContextElementType(String elementId)
    {
        return Framework.getConfig().getInContextElementType(elementId);
    }
}
