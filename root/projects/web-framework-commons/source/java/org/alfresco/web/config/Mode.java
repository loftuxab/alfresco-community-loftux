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
package org.alfresco.web.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public enum Mode
{
    VIEW, EDIT, CREATE;
    
    private static Log logger = LogFactory.getLog(Mode.class);
    
    public static Mode modeFromString(String modeString)
    {
        if ("create".equalsIgnoreCase(modeString)) {
            return Mode.CREATE;
        }
        else if ("edit".equalsIgnoreCase(modeString))
        {
            return Mode.EDIT;
        }
        else if ("view".equalsIgnoreCase(modeString))
        {
            return Mode.VIEW;
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Illegal modeString: " + modeString);
            }
            return null;
        }
    }
    
    public static List<Mode> modesFromString(String commaSeparatedModesString)
    {
        if (commaSeparatedModesString == null)
        {
            return Collections.emptyList();
        }
        List<Mode> result = new ArrayList<Mode>();
        StringTokenizer st = new StringTokenizer(commaSeparatedModesString, ",");
        while (st.hasMoreTokens())
        {
            String nextToken = st.nextToken().trim();
            Mode nextMode = Mode.modeFromString(nextToken);
            result.add(nextMode);
        }
        return result;
    }
}