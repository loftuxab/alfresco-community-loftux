/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
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
