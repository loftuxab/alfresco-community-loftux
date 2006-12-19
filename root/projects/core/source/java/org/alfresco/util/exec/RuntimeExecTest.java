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
package org.alfresco.util.exec;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.util.exec.RuntimeExec.ExecutionResult;

import junit.framework.TestCase;

/**
 * @see org.alfresco.util.exec.RuntimeExec
 * 
 * @author Derek Hulley
 */
public class RuntimeExecTest extends TestCase
{
    public void testStreams() throws Exception
    {
        RuntimeExec exec = new RuntimeExec();
        
        // This test will return different results on Windows and Linux!
        // note that some Unix variants will error without a path
        HashMap<String, String> commandMap = new HashMap<String, String>(5);
        commandMap.put("*", "find / -maxdepth 1 -name var");
        commandMap.put("Windows.*", "find /?");
        exec.setCommandMap(commandMap);
        // execute
        ExecutionResult ret = exec.execute();
        
        String out = ret.getStdOut();
        String err = ret.getStdErr();
        
        assertEquals("Didn't expect error code", 0, ret.getExitValue());
        assertEquals("Didn't expect any error output", 0, err.length());
        assertTrue("No output found", out.length() > 0);
    }

    public void testWildcard() throws Exception
    {
        RuntimeExec exec = new RuntimeExec();

        // set the command
        Map<String, String> commandMap = new HashMap<String, String>(3, 1.0f);
        commandMap.put(".*", "TEST");
        exec.setCommandMap(commandMap);
        
        String commandStr = exec.getCommand();
        assertEquals("Expected default match to work", "TEST", commandStr);
    }
    
    public void testWithProperties() throws Exception
    {
        RuntimeExec exec = new RuntimeExec();

        // set the command
        Map<String, String> commandMap = new HashMap<String, String>(3, 1.0f);
        commandMap.put("Windows.*", "dir \"${path}\"");
        commandMap.put("Linux", "ls '${path}'");
        commandMap.put("Mac OS X", "ls '${path}'");
        commandMap.put("*", "wibble ${path}");
        exec.setCommandMap(commandMap);
        
        // set the default properties
        Map<String, String> defaultProperties = new HashMap<String, String>(1, 1.0f);
        defaultProperties.put("path", ".");
        exec.setDefaultProperties(defaultProperties);
        
        // check that the command lines generated are correct
        String defaultCommand = exec.getCommand();
        String dynamicCommand = exec.getCommand(Collections.singletonMap("path", "./"));
        // check
        String os = System.getProperty("os.name");
        String defaultCommandCheck = null;
        String dynamicCommandCheck = null;
        if (os.matches("Windows.*"))
        {
            defaultCommandCheck = "dir \".\"";
            dynamicCommandCheck = "dir \"./\"";
        }
        else if (os.equals("Linux") || os.equals("Mac OS X"))
        {
            defaultCommandCheck = "ls '.'";
            dynamicCommandCheck = "ls './'";
        }
        else
        {
            defaultCommandCheck = "wibble .";
            dynamicCommandCheck = "wibble ./";
        }
        assertEquals("Default command for OS " + os + " is incorrect", defaultCommandCheck, defaultCommand);
        assertEquals("Dynamic command for OS " + os + " is incorrect", dynamicCommandCheck, dynamicCommand);
    }
}
