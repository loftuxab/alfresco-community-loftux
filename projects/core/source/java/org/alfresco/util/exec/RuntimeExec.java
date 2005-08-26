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

import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This acts as a session similar to the <code>java.lang.Process</code>,
 * but logs the system standard and error streams.
 * 
 * @author Derek Hulley
 */
public class RuntimeExec
{
   private static final int BUFFER_SIZE = 1024;
   
   private static Log logger = LogFactory.getLog(RuntimeExec.class);
   
   private String command;
   private int exitValue;
   private String execOut;
   private String execErr;
   
   /**
    * @param command a command to execute that <b>MUST NOT</b> require further input
    */
   public RuntimeExec(String command)
   {
      this.command = command;
   }
   
   /**
    * Executes the statement that this instance was constructed with
    * 
    * @return Returns the exit code of the statement
    * @throws Exception
    */
   public int execute() throws Exception
   {
      Runtime runtime = Runtime.getRuntime();
      Process process = null;
      try
      {
         process = runtime.exec(command);
      }
      catch (IOException e)
      {
         throw new AlfrescoRuntimeException("Failed to execute command: " + getCommand(), e);
      }

      // create the stream gobblers
      InputStreamReaderThread stdOutGobbler = new InputStreamReaderThread(process.getInputStream());
      InputStreamReaderThread stdErrGobbler = new InputStreamReaderThread(process.getErrorStream());
      
      // start gobbling
      stdOutGobbler.start();
      stdErrGobbler.start();
      
      // wait for the process to finish
      exitValue = process.waitFor();
      
      // ensure that the stream gobblers get to finish
      stdOutGobbler.waitForCompletion();
      stdErrGobbler.waitForCompletion();
      
      // get the stream values
      execOut = stdOutGobbler.getBuffer();
      execErr = stdErrGobbler.getBuffer();

      // done
      if (logger.isDebugEnabled())
      {
         logger.debug(this);
      }
      return exitValue;
   }
   
   public String getCommand()
   {
      return command;
   }
   
   public int getExitValue()
   {
      return exitValue;
   }
   
   public String getStdOut()
   {
      return execOut;
   }
   
   public String getStdErr()
   {
      return execErr;
   }
   
   public String toString()
   {
      StringBuffer sb = new StringBuffer(256);
      sb.append("RuntimeExec:\n")
        .append("   command: " + command + "\n")
        .append("   exit value: " + exitValue + "\n")
        .append("   stdout:\n" + execOut + "\n")
        .append("   stderr:\n" + execErr);
      return sb.toString();
   }
   
   /**
    * Gobbles an <code>InputStream</code> and writes it into a <code>StringBuffer</code>
    * <p>
    * The reading of the input stream is buffered.
    */
   public static class InputStreamReaderThread extends Thread
   {
      private InputStream m_is;
      private StringBuffer m_sb;
      private boolean m_isRunning;
      private boolean m_completed;

      /**
       * @param is an input stream to read - it will be wrapped in a buffer for reading
       */
      public InputStreamReaderThread(InputStream is)
      {
         super();
         setDaemon(true);   // must not hold up the VM if it is terminating
         m_is = is;
         m_sb = new StringBuffer(BUFFER_SIZE);
         m_isRunning = false;
         m_completed = false;
      }
       
      public synchronized void run()
      {
         // mark this thread as running
         m_isRunning = true;
         m_completed = false;
         
         byte[] bytes = new byte[BUFFER_SIZE];
         InputStream tempIs = null;
         try
         {
             tempIs = new BufferedInputStream(m_is, BUFFER_SIZE);
             int count = -2;
             while (count != -1)
             {
                // do we have something previously read?
                if (count > 0)
                {
                   String toWrite = new String(bytes, 0, count);
                   m_sb.append(toWrite);
                }
                // read the next set of bytes
                count = tempIs.read(bytes);
             }
             // done
             m_isRunning = false;
             m_completed = true;
         }
         catch(IOException e)
         {
            throw new AlfrescoRuntimeException("Unable to read stream", e);
         }
         finally
         {
            // close the input stream
            if (tempIs != null)
            {
               try { tempIs.close(); } catch (Exception e) {}
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
         while (!m_completed && !m_isRunning)
         {
            try
            {
               // release our lock and wait a bit
               this.wait(200L);   // 200 ms
            }
            catch (InterruptedException e) {}
         }
      }
      
      public boolean isComplete()
      {
         return m_completed;
      }
      /**
       * @return Returns the current state of the buffer
       */
      public String getBuffer()
      {
         return m_sb.toString();
      }
   }
}
