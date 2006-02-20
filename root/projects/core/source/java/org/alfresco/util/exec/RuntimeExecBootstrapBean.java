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
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.util.exec.RuntimeExec.ExecutionResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Application bootstrap bean that is able to execute one or more
 * native executable statements upon startup and shutdown.
 * 
 * @author Derek Hulley
 */
public class RuntimeExecBootstrapBean implements ApplicationListener
{
    private static Log logger = LogFactory.getLog(RuntimeExecBootstrapBean.class);
    
    private List<RuntimeExec> startupCommands;

    /**
     * Initializes the bean with empty defaults, i.e. it will do nothing
     */
    public RuntimeExecBootstrapBean()
    {
        this.startupCommands = Collections.emptyList();
    }

    /**
     * Set the commands to execute, in sequence, when the application context
     * is initialized.
     * 
     * @param startupCommands list of commands
     */
    public void setStartupCommands(List<RuntimeExec> startupCommands)
    {
        this.startupCommands = startupCommands;
    }

    /**
     * Listens for the the context refresh and executes the startup commands.
     * Any failure of the commands will lead to context initialization failure.
     */
    public void onApplicationEvent(ApplicationEvent event)
    {
        if (!(event instanceof ContextRefreshedEvent))
        {
            return;
        }
        // execute
        for (RuntimeExec command : startupCommands)
        {
            ExecutionResult result = command.execute();
            // check for failure
            if (!result.getSuccess())
            {
                throw new AlfrescoRuntimeException("Bootstrap command failed: \n" + result);
            }
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Bootstrap execution of " + startupCommands.size() + " commands was successful");
        }
    }
}
