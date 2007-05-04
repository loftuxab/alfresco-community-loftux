/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing
 */

package org.alfresco.deployment.util;

import java.io.File;

/**
 * A Class that represents a deployment path.
 * @author britt
 */
public class Path
{
    private String[] fComponents;
    
    public Path(String path)
    {
        path = path.replace("^/+", "").replace("/+$", "");
        fComponents = path.split("/+");
        if (fComponents.length == 1 && fComponents[0].equals(""))
        {
            fComponents = new String[0];
        }
    }
    
    public Path(String[] components)
    {
        fComponents = components;
    }
    
    /**
     * Get the number of components in the path.
     * @return
     */
    public int size()
    {
        return fComponents.length;
    }
    
    /**
     * Get the indexth component.
     * @param index
     * @return
     */
    public String get(int index)
    {
        return fComponents[index];
    }
    
    /**
     * Get the parent Path of this Path.
     * @return
     */
    public Path getParent()
    {
        if (fComponents.length == 0)
        {
            return null;
        }
        String[] result = new String[fComponents.length - 1];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = fComponents[i];
        }
        return new Path(result);
    }
    
    /**
     * Get the last component of the Path. Don't call on the root Path.
     * @return
     */
    public String getBaseName()
    {
        return fComponents[fComponents.length - 1];
    }
    
    /**
     * Get the Path that is this Path extended by one component.
     * @param name 
     * @return
     */
    public Path extend(String name)
    {
        String[] result = new String[fComponents.length + 1];
        for (int i = 0; i < fComponents.length; i++)
        {
            result[i] = fComponents[i];
        }
        result[fComponents.length] = name;
        return new Path(result);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        if (fComponents.length == 0)
        {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fComponents.length - 1; i++)
        {
            builder.append(fComponents[i]);
            builder.append(File.separatorChar);
        }
        builder.append(fComponents[fComponents.length - 1]);
        return builder.toString();
    }
}
