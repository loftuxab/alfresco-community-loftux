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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This acts as a session similar to the <code>java.lang.Process</code>, but
 * logs the system standard and error streams.
 * <p>
 * The bean can be configured to execute a command directly, or be given a map
 * of commands keyed by the <i>os.name</i> Java system property.  In this map,
 * the default key that is used when no match is found is the
 * <b>{@link #KEY_OS_DEFAULT *}</b> key.
 * <p>
 * Commands may use placeholders, e.g.
 * <pre><code>
 *    find -name ${filename}
 * </code></pre>
 * The <b>filename</b> property will be substituted for any supplied value prior to
 * each execution of the command.  Currently, no checks are made to get or check the
 * properties contained within the command string.  It is up to the client code to
 * dynamically extract the properties required if the required properties are not
 * known up front.
 * 
 * @author Derek Hulley
 */
public class RuntimeExec
{
    /** the key to use when specifying a command for any other OS: <b>*</b> */
    public static final String KEY_OS_DEFAULT = "*";
    
    private static final String KEY_OS_NAME = "os.name";
    private static final int BUFFER_SIZE = 1024;
    private static final String VAR_OPEN = "${";
    private static final String VAR_CLOSE = "}";

    private static Log logger = LogFactory.getLog(RuntimeExec.class);

    private String command;
    private Map<String, String> defaultProperties;
    private Set<Integer> errCodes;

    /**
     * Default constructor.  Initialize this instance by setting individual properties.
     */
    public RuntimeExec()
    {
        defaultProperties = Collections.emptyMap();
        // set default error codes
        this.errCodes = new HashSet<Integer>(2);
        errCodes.add(1);
        errCodes.add(2);
    }
    
    /**
     * Set the command to execute regardless of operating system
     * 
     * @param command the command string
     */
    public void setCommand(String command)
    {
        this.command = command;
    }
    
    /**
     * Supply a choice of commands to execute based on a mapping from the <i>os.name</i> system
     * property to the command to execute.  The {@link #KEY_OS_DEFAULT *} key can be used
     * to get a command where there is not direct match to the operating system key.
     * 
     * @param commandsByOS a map of command string keyed by operating system names
     */
    public void setCommandMap(Map<String, String> commandsByOS)
    {
        // get the current OS
        String serverOs = System.getProperty(KEY_OS_NAME);
        // attempt to find a match
        String command = commandsByOS.get(serverOs);
        if (command == null)
        {
            // go through the commands keys, looking for one that matches by regular expression matching
            for (String osName : commandsByOS.keySet())
            {
                // Ignore * options.  It is dealt with later.
                if (osName.equals(KEY_OS_DEFAULT))
                {
                    continue;
                }
                // Do regex match
                if (serverOs.matches(osName))
                {
                    command = commandsByOS.get(osName);
                    break;
                }
            }
            // if there is still no command, then check for the wildcard
            if (command == null)
            {
                command = commandsByOS.get(KEY_OS_DEFAULT);
            }
        }
        // check
        if (command == null)
        {
            throw new AlfrescoRuntimeException(
                    "No command found for OS " + serverOs + " or '" + KEY_OS_DEFAULT + "': \n" +
                    "   commands: " + commandsByOS);
        }
        this.command = command;
    }
    
    /**
     * Set the default properties to use when executing the command.  The properties
     * supplied during execution will overwrite the default properties.
     * <p>
     * <code>null</code> properties will be treated as an empty string for substitution
     * purposes.
     * 
     * @param defaultProperties property values
     */
    public void setDefaultProperties(Map<String, String> defaultProperties)
    {
        this.defaultProperties = defaultProperties;
    }
    
    /**
     * A comma or space separated list of values that, if returned by the executed command,
     * indicate an error value.  This defaults to <b>"1, 2"</b>.
     * 
     * @param erroCodesStr the error codes for the execution
     */
    public void setErrorCodes(String errCodesStr)
    {
        errCodes.clear();
        StringTokenizer tokenizer = new StringTokenizer(errCodesStr, " ,");
        while(tokenizer.hasMoreElements())
        {
            String errCodeStr = tokenizer.nextToken();
            // attempt to convert it to an integer
            try
            {
                int errCode = Integer.parseInt(errCodeStr);
                this.errCodes.add(errCode);
            }
            catch (NumberFormatException e)
            {
                throw new AlfrescoRuntimeException(
                        "Property 'errorCodes' must be comma-separated list of integers: " + errCodesStr);
            }
        }
    }
    
    /**
     * Executes the command using the default properties
     * 
     * @see #execute(Map)
     */
    public ExecutionResult execute()
    {
        return execute(defaultProperties);
    }

    /**
     * Executes the statement that this instance was constructed with.
     * <p>
     * <code>null</code> properties will be treated as an empty string for substitution
     * purposes.
     * 
     * @return Returns the full execution results
     */
    public ExecutionResult execute(Map<String, String> properties)
    {
        // check that the command has been set
        if (command == null)
        {
            throw new AlfrescoRuntimeException("Runtime command has not been set: \n" + this);
        }
        
        // create the properties
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        String commandToExecute = null;
        try
        {
            // execute the command with full property replacement
            commandToExecute = getCommand(properties);
            process = runtime.exec(commandToExecute);
        }
        catch (IOException e)
        {
            throw new AlfrescoRuntimeException("Failed to execute command: " + commandToExecute, e);
        }

        // create the stream gobblers
        InputStreamReaderThread stdOutGobbler = new InputStreamReaderThread(process.getInputStream());
        InputStreamReaderThread stdErrGobbler = new InputStreamReaderThread(process.getErrorStream());

        // start gobbling
        stdOutGobbler.start();
        stdErrGobbler.start();

        // wait for the process to finish
        int exitValue = 0;
        try
        {
            exitValue = process.waitFor();
        }
        catch (InterruptedException e)
        {
            // process was interrupted - generate an error message
            stdErrGobbler.addToBuffer(e.toString());
            exitValue = 1;
        }

        // ensure that the stream gobblers get to finish
        stdOutGobbler.waitForCompletion();
        stdErrGobbler.waitForCompletion();

        // get the stream values
        String execOut = stdOutGobbler.getBuffer();
        String execErr = stdErrGobbler.getBuffer();
        
        // construct the return value
        ExecutionResult result = new ExecutionResult(commandToExecute, errCodes, exitValue, execOut, execErr);

        // done
        if (logger.isDebugEnabled())
        {
            logger.debug(result);
        }
        return result;
    }

    /**
     * @return Returns the command that will be executed if no additional properties
     *      were to be supplied
     */
    public String getCommand()
    {
        return getCommand(defaultProperties);
    }
    
    /**
     * Get the command that will be executed post substitution.
     * <p>
     * <code>null</code> properties will be treated as an empty string for substitution
     * purposes.
     * 
     * @param properties the properties that might be executed with
     * @return Returns the command that will be executed should the additional properties
     *      be supplied
     */
    public String getCommand(Map<String, String> properties)
    {
        Map<String, String> execProperties = null;
        if (properties == defaultProperties)
        {
            // we are just using the default properties
            execProperties = defaultProperties;
        }
        else
        {
            execProperties = new HashMap<String, String>(defaultProperties);
            // overlay the supplied properties
            execProperties.putAll(properties);
        }
        // perform the substitution
        StringBuilder sb = new StringBuilder(command);
        for (Map.Entry<String, String> entry : execProperties.entrySet())
        {
            String key = entry.getKey();
            String value = entry.getValue();
            // ignore null
            if (value == null)
            {
                value = "";
            }
            // progressively replace the property in the command
            key = (VAR_OPEN + key + VAR_CLOSE);
            int index = sb.indexOf(key);
            while (index > -1)
            {
                // replace
                sb.replace(index, index + key.length(), value);
                // get the next one
                index = sb.indexOf(key, index + 1);
            }
        }
        // done
        return sb.toString();
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer(256);
        sb.append("RuntimeExec:\n")
          .append("   command:    ").append(command).append("\n")
          .append("   os:         ").append(System.getProperty(KEY_OS_NAME)).append("\n");
        return sb.toString();
    }
    
    /**
     * Object to carry the results of an execution to the caller.
     * 
     * @author Derek Hulley
     */
    public static class ExecutionResult
    {
        private final String command;
        private final Set<Integer> errCodes;
        private final int exitValue;
        private final String stdOut;
        private final String stdErr;
       
        private ExecutionResult(
                final String command,
                final Set<Integer> errCodes,
                final int exitValue,
                final String stdOut,
                final String stdErr)
        {
            this.command = command;
            this.errCodes = errCodes;
            this.exitValue = exitValue;
            this.stdOut = stdOut;
            this.stdErr = stdErr;
        }
        
        @Override
        public String toString()
        {
            String out = stdOut.length() > 250 ? stdOut.substring(0, 250) : stdOut;
            String err = stdErr.length() > 250 ? stdErr.substring(0, 250) : stdErr;
            
            StringBuilder sb = new StringBuilder(128);
            sb.append("Execution result: \n")
              .append("   os:         ").append(System.getProperty(KEY_OS_NAME)).append("\n")
              .append("   command:    ").append(command).append("\n")
              .append("   succeeded:  ").append(getSuccess()).append("\n")
              .append("   exit code:  ").append(exitValue).append("\n")
              .append("   out:        ").append(out).append("\n")
              .append("   err:        ").append(err);
            return sb.toString();
        }
        
        /**
         * @param exitValue the command exit value
         * @return Returns true if the code is a listed failure code
         * 
         * @see #setErrorCodes(String)
         */
        private boolean isFailureCode(int exitValue)
        {
            return errCodes.contains((Integer)exitValue);
        }
        
        /**
         * @return Returns true if the command was deemed to be successful according to the
         *      failure codes returned by the execution.
         */
        public boolean getSuccess()
        {
            return !isFailureCode(exitValue);
        }

        public int getExitValue()
        {
            return exitValue;
        }
        
        public String getStdOut()
        {
            return stdOut;
        }
    
        public String getStdErr()
        {
            return stdErr;
        }
    }

    /**
     * Gobbles an <code>InputStream</code> and writes it into a
     * <code>StringBuffer</code>
     * <p>
     * The reading of the input stream is buffered.
     */
    public static class InputStreamReaderThread extends Thread
    {
        private InputStream is;
        private StringBuffer buffer;          // we require the synchronization
        private boolean isRunning;
        private boolean completed;

        /**
         * @param is an input stream to read - it will be wrapped in a buffer
         *        for reading
         */
        public InputStreamReaderThread(InputStream is)
        {
            super();
            setDaemon(true); // must not hold up the VM if it is terminating
            this.is = is;
            this.buffer = new StringBuffer(BUFFER_SIZE);
            this.isRunning = false;
            this.completed = false;
        }

        public synchronized void run()
        {
            // mark this thread as running
            isRunning = true;
            completed = false;

            byte[] bytes = new byte[BUFFER_SIZE];
            InputStream tempIs = null;
            try
            {
                tempIs = new BufferedInputStream(is, BUFFER_SIZE);
                int count = -2;
                while (count != -1)
                {
                    // do we have something previously read?
                    if (count > 0)
                    {
                        String toWrite = new String(bytes, 0, count);
                        buffer.append(toWrite);
                    }
                    // read the next set of bytes
                    count = tempIs.read(bytes);
                }
                // done
                isRunning = false;
                completed = true;
            }
            catch (IOException e)
            {
                throw new AlfrescoRuntimeException("Unable to read stream", e);
            }
            finally
            {
                // close the input stream
                if (tempIs != null)
                {
                    try
                    {
                        tempIs.close();
                    }
                    catch (Exception e)
                    {
                    }
                }
            }
        }

        /**
         * Waits for the run to complete.
         * <p>
         * <b>Remember to <code>start</code> the thread first
         */
        public synchronized void waitForCompletion()
        {
            while (!completed && !isRunning)
            {
                try
                {
                    // release our lock and wait a bit
                    this.wait(200L); // 200 ms
                }
                catch (InterruptedException e)
                {
                }
            }
        }
        
        /**
         * @param msg the message to add to the buffer
         */
        public void addToBuffer(String msg)
        {
            buffer.append(msg);
        }

        public boolean isComplete()
        {
            return completed;
        }

        /**
         * @return Returns the current state of the buffer
         */
        public String getBuffer()
        {
            return buffer.toString();
        }
    }
}
