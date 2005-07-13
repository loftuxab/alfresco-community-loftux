/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.integrity;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an integrity violation
 * 
 * @author Derek Hulley
 */
public class IntegrityRecord
{
    private String msg;
    private List<StackTraceElement[]> traces;
    
    /**
     * @param msg the violation message
     */
    public IntegrityRecord(String msg)
    {
        this.msg = msg;
        this.traces = null;
    }
    
    /**
     * Add a stack trace to the list of traces associated with this failure
     * 
     * @param trace a stack trace
     */
    public void addTrace(StackTraceElement[] trace)
    {
        if (traces == null)
        {
            traces = new ArrayList<StackTraceElement[]>(2);
        }
        traces.add(trace);
    }
    
    public String getMessage()
    {
        return msg;
    }
    
    /**
     * Dumps the integrity message and, if present, the stack trace
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder(msg.length() * 2);
        if (traces == null)
        {
            sb.append(msg);
        }
        else
        {
            sb.append(msg);
            for (StackTraceElement[] trace : traces)
            {
                sb.append("\n   Trace of possible cause:");
                for (int i = 0; i < trace.length; i++)
                {
                    sb.append("\n      ").append(trace[i]);
                }
            }
        }
        return sb.toString();
    }
}
