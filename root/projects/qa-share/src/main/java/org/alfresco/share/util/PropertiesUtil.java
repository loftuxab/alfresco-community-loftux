/*
 * #%L
 * qa-share
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.share.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Properties;

import org.alfresco.po.share.exception.ShareException;

public class PropertiesUtil
{

    /***
     * Sets the key & value
     * 
     * @param path
     * @param key
     * @param value
     */
    public static void setPropertyValue(String path, String key, String value)
    {
        try
        {

            FileReader reader;
            Properties fileProperty = new Properties();
            reader = new FileReader(path);
            fileProperty.load(reader);
            fileProperty.setProperty(key, value);

            fileProperty.store(new FileWriter(path), null);
        }
        catch (Exception e)
        {
            throw new ShareException("Failed to set the value into the file " + path);

        }
    }
    
    
    /**
     * Sets the key & value
     * 
     * @param path
     * @param key
     * @return
     */
    public static String getPropertyValue(String path, String key)
    {
        try
        {

            FileReader reader;
            Properties fileProperty = new Properties();
            reader = new FileReader(path);
            fileProperty.load(reader);
            return fileProperty.getProperty(key);
                
        }
        catch (Exception e)
        {
            throw new ShareException("Failed to get the value from the file " + path + e.getMessage());
        }
    }
    
    
    /**
     * Returns the value for the key
     * 
     * @param path
     * @param key
     * @return
     */
    public static String getPropertyValue(InputStream path, String key)
    {
        try
        {
            Properties fileProperty = new Properties();
            fileProperty.load(path);
            return fileProperty.getProperty(key);
                
        }
        catch (Exception e)
        {
            throw new ShareException("Failed to get the value from the file " + path + e.getMessage());
        }
    }
    
}
