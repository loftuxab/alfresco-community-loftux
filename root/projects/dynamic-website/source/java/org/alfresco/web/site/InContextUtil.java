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
 * Convenience methods for working with in-context state.
 * 
 * @author muzquiano
 */
public class InContextUtil
{
    
    /** The INCONTEX t_ elemen t_ id. */
    public static String INCONTEXT_ELEMENT_ID = "incontext";

    /** The INCONTEX t_ toggl e_ reques t_ param. */
    public static String INCONTEXT_TOGGLE_REQUEST_PARAM = "toggle";
    
    /** The INCONTEX t_ elemen t_ reques t_ param. */
    public static String INCONTEXT_ELEMENT_REQUEST_PARAM = "element";
    
    /** The INCONTEX t_ valu e_ reques t_ param. */
    public static String INCONTEXT_VALUE_REQUEST_PARAM = "value";

    /**
     * Gets the in context map.
     * 
     * @param session the session
     * 
     * @return the in context map
     */
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

    /**
     * Checks if is enabled.
     * 
     * @param session the session
     * 
     * @return true, if is enabled
     */
    public static boolean isEnabled(HttpSession session)
    {
        return isElementEnabled(session, INCONTEXT_ELEMENT_ID);
    }

    /**
     * Checks if is element enabled.
     * 
     * @param session the session
     * @param elementId the element id
     * 
     * @return true, if is element enabled
     */
    public static boolean isElementEnabled(HttpSession session, String elementId)
    {
        return getBoolean(session, elementId + ".enabled");

    }

    /**
     * Gets the element state.
     * 
     * @param session the session
     * @param elementId the element id
     * 
     * @return the element state
     */
    public static String getElementState(HttpSession session, String elementId)
    {
        return get(session, elementId + ".state");
    }

    /**
     * Handles the enable/disable of in-context elements
     * Also handles updates of in-context element states
     * This is primarily utilized by the InContextServlet.
     * 
     * @param request the request
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

    /**
     * Sets the in context element enabled.
     * 
     * @param session the session
     * @param elementId the element id
     * @param value the value
     */
    public static void setInContextElementEnabled(HttpSession session,
            String elementId, boolean value)
    {
        if (value)
            toggleInContext(session, elementId + ".enabled", "true");
        else
            toggleInContext(session, elementId + ".enabled", "false");
    }

    /**
     * Sets the in context element state.
     * 
     * @param session the session
     * @param elementId the element id
     * @param value the value
     */
    public static void setInContextElementState(HttpSession session,
            String elementId, String value)
    {
        toggleInContext(session, elementId + ".state", value);
    }

    /**
     * Toggle in context.
     * 
     * @param session the session
     * @param key the key
     * @param value the value
     */
    protected static void toggleInContext(HttpSession session, String key,
            String value)
    {
        if (session != null)
        {
            getInContextMap(session).put(key, value);
        }
    }

    /**
     * Gets the.
     * 
     * @param session the session
     * @param key the key
     * 
     * @return the string
     */
    protected static String get(HttpSession session, String key)
    {
        return (String) getInContextMap(session).get(key);
    }

    /**
     * Gets the boolean.
     * 
     * @param session the session
     * @param key the key
     * 
     * @return the boolean
     */
    protected static boolean getBoolean(HttpSession session, String key)
    {
        String value = get(session, key);
        if ("true".equalsIgnoreCase(value))
            return true;
        return false;
    }

    /**
     * Gets the in context element ids.
     * 
     * @return the in context element ids
     */
    public static String[] getInContextElementIds()
    {
        return Framework.getConfig().getInContextElementIds();
    }

    /**
     * Gets the in context element default enabled.
     * 
     * @param elementId the element id
     * 
     * @return the in context element default enabled
     */
    public static String getInContextElementDefaultEnabled(String elementId)
    {
        return Framework.getConfig().getInContextElementDefaultEnabled(
                elementId);
    }

    /**
     * Gets the in context element default state.
     * 
     * @param elementId the element id
     * 
     * @return the in context element default state
     */
    public static String getInContextElementDefaultState(String elementId)
    {
        return Framework.getConfig().getInContextElementDefaultState(elementId);
    }

    /**
     * Gets the in context element name.
     * 
     * @param elementId the element id
     * 
     * @return the in context element name
     */
    public static String getInContextElementName(String elementId)
    {
        return Framework.getConfig().getInContextElementName(elementId);
    }

    /**
     * Gets the in context element type.
     * 
     * @param elementId the element id
     * 
     * @return the in context element type
     */
    public static String getInContextElementType(String elementId)
    {
        return Framework.getConfig().getInContextElementType(elementId);
    }
}
