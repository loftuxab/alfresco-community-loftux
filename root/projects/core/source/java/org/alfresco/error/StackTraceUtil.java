/*
 * Copyright (C) 2005 Alfresco, Inc.
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
package org.alfresco.error;

/**
 * Helper class around outputting stack traces.
 * 
 * @author Derek Hulley
 */
public class StackTraceUtil
{
    /**
     * Builds a message with the stack trace of the form:
     * <pre>
     *    SOME MESSAGE:
     *       Started at:
     *          com.package...
     *          com.package...
     *          ...
     * </pre>
     * 
     * @param msg the initial error message
     * @param stackTraceElements the stack trace elements
     * @param sb the buffer to append to
     * @param maxDepth the maximum number of trace elements to output.  0 or less means output all.
     */
    public static void buildStackTrace(
            String msg,
            StackTraceElement[] stackTraceElements,
            StringBuilder sb,
            int maxDepth)
    {
        sb.append(msg).append(" \n")
          .append("   Started at: \n");
        for (int i = 0; i < stackTraceElements.length; i++)
        {
            if (i > maxDepth && maxDepth > 0)
            {
                sb.append("      ...");
                break;
            }
            sb.append("      ").append(stackTraceElements[i]);
            if (i < stackTraceElements.length - 1)
            {
                sb.append("\n");
            }
        }
    }
}
