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
package org.alfresco.util.log;

import java.lang.reflect.Method;

/**
 * A stand in for the org.apache.log4j.NDC class that avoids introducing runtime dependencies against the otherwise
 * optional log4j.
 * 
 * @author dward
 */
public class NDC
{
    /** Log4J reflection to clear the NDC */
    private static Method ndcRemoveMethod;

    /** Log4J reflection to push the NDC */
    private static Method ndcPushMethod;

    static
    {
        try
        {
            Class<?> ndc = Class.forName("org.apache.log4j.NDC");
            ndcRemoveMethod = ndc.getMethod("remove");
            ndcPushMethod = ndc.getMethod("push", String.class);
        }
        catch (Throwable e)
        {
            // We just ignore it
        }
    }

    /**
     * Push new diagnostic context information for the current thread.
     * 
     * @param message
     *            The new diagnostic context information.
     */
    public static void push(String message)
    {
        try
        {
            ndcPushMethod.invoke(null, message);
        }
        catch (Throwable e)
        {
            // We just ignore it
        }
    }

    /**
     * Remove the diagnostic context for this thread.
     */
    static public void remove()
    {
        try
        {
            ndcRemoveMethod.invoke(null);
        }
        catch (Throwable e)
        {
            // We just ignore it
        }
    }
}
