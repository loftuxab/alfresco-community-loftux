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
package org.alfresco.web.site.parser.tags;

import java.lang.reflect.Constructor;

import javax.servlet.jsp.tagext.TagAttributeInfo;

/**
 * @author muzquiano
 */
public class TagAttributeInfoHelper
{
    private static Constructor constructor = null;
    private static int which;

    private static final String[] JAKARTA_CLASSES = { "java.lang.String", "boolean", "java.lang.String", "boolean" };
    private static final String[] IPLANET_CLASSES = { "java.lang.String", "boolean", "boolean", "java.lang.String" };
    private static final int JAKARTA_CONSTRUCTOR = 1;
    private static final int IPLANET_CONSTRUCTOR = 2;

    static
    {
        try
        {
            Constructor[] constructors = Class.forName(
                    "javax.servlet.jsp.tagext.TagAttributeInfo").getConstructors();
            for (int i = 0; i < constructors.length; i++)
            {
                Class[] params = constructors[i].getParameterTypes();
                if (params.length == 4)
                {
                    if (params[0].getName().equals(JAKARTA_CLASSES[0]) && params[1].getName().equals(
                            JAKARTA_CLASSES[1]) && params[2].getName().equals(
                            JAKARTA_CLASSES[2]) && params[3].getName().equals(
                            JAKARTA_CLASSES[3]))
                    {
                        constructor = constructors[i];
                        which = JAKARTA_CONSTRUCTOR;
                        break;
                    }
                    else if (params[0].getName().equals(IPLANET_CLASSES[0]) && params[1].getName().equals(
                            IPLANET_CLASSES[1]) && params[2].getName().equals(
                            IPLANET_CLASSES[2]) && params[3].getName().equals(
                            IPLANET_CLASSES[3]))
                    {
                        constructor = constructors[i];
                        which = IPLANET_CONSTRUCTOR;
                        break;
                    }
                }
            }
        }
        catch (ClassNotFoundException cnfe)
        {
        }
        if (constructor == null)
        {
            throw new RuntimeException(
                    "TagAttributeInfo class is not recognized");
        }
    }

    public static TagAttributeInfo newTagAttributeInfo(String name,
            boolean isRequired, String type, boolean reqTime)
    {
        try
        {
            switch (which)
            {
                case JAKARTA_CONSTRUCTOR:
                    return (TagAttributeInfo) constructor.newInstance(new Object[] { name, new Boolean(
                            isRequired), type, new Boolean(reqTime) });
                case IPLANET_CONSTRUCTOR:
                    return (TagAttributeInfo) constructor.newInstance(new Object[] { name, new Boolean(
                            isRequired), new Boolean(reqTime), type });
                default:
                    throw new RuntimeException(
                            "Problem creating TagAttributeInfo.  No constructor found.");
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Problem creating TagAttributeInfo: " + e.getMessage());
        }
    }
}
