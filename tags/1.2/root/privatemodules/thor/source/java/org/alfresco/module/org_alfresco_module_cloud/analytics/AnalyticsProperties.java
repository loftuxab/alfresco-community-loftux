/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_cloud.analytics;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Analytics Property helper object to pass multiple property types like String,
 * Integer, Boolean. Serialises to JSON so it can be passed to the Alfresco
 * Action for posting
 * 
 * @author David Gildeh
 */
public class AnalyticsProperties
{

    // Holds properties in JSON Object
    private JSONObject json;

    /**
     * Default Constructor
     */
    public AnalyticsProperties()
    {
        json = new JSONObject();
    }

    /**
     * Constructor that creates object from a JSON String
     * 
     * @param jsonString
     *            The JSON String to convert into a new Object
     * @throws JSONException
     */
    public AnalyticsProperties(String jsonString) throws JSONException
    {
        json = new JSONObject(jsonString);
    }

    /**
     * Add a new property
     * 
     * @param key
     *            The property key
     * @param value
     *            The property value
     * @throws JSONException
     */
    public void put(String key, Integer value) throws JSONException
    {
        json.put(key, value);
    }

    /**
     * Add a new property
     * 
     * @param key
     *            The property key
     * @param number
     *            The property value
     * @throws JSONException
     */
    public void put(String key, Long value) throws JSONException
    {
        json.put(key, value);
    }

    /**
     * Add a new property
     * 
     * @param key
     *            The property key
     * @param value
     *            The property value
     * @throws JSONException
     */
    public void put(String key, String value) throws JSONException
    {
        json.put(key, value);
    }

    /**
     * Add a new property
     * 
     * @param key
     *            The property key
     * @param value
     *            The property value
     * @throws JSONException
     */
    public void put(String key, Boolean value) throws JSONException
    {
        json.put(key, value);
    }

    /**
     * Gets a value from the JSON Object
     * 
     * @param key
     *            The property key
     * @return Value Object
     *            The value object or null if not found
     */
    public Object get(String key)
    {
        try {
            return json.get(key);
        } catch (JSONException je) {
            // Catch if not found and return null
            return null;
        }
    }

    /**
     * Gets an iterator to all the keys being stored by the JSON Object
     * 
     * @return The JSON Object Keys as String array
     */
    public String[] getKeys()
    {
        String[] keys = JSONObject.getNames(json);
        return keys == null ? new String[] {} : keys;
    }

    /**
     * Removes a value from the JSON Object
     * 
     * @param key
     *            The property key
     */
    public void remove(String key)
    {
        json.remove(key);
    }

    /**
     * Override default toString method to send back JSON String
     */
    @Override
    public String toString()
    {
        return json.toString();
    }
}
