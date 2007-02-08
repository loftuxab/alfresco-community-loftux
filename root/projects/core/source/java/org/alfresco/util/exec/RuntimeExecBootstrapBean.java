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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.alfresco.util.exec;

import java.util.Collections;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.util.AbstractLifecycleBean;
import org.alfresco.util.exec.RuntimeExec.ExecutionResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;

/**
 * Application bootstrap bean that is able to execute one or more
 * native executable statements upon startup and shutdown.
 * 
 * @author Derek Hulley
 */
public class RuntimeExecBootstrapBean extends AbstractLifecycleBean
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

    @Override
    protected void onBootstrap(ApplicationEvent event)
    {
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

    @Override
    protected void onShutdown(ApplicationEvent event)
    {
        // NOOP
    }
}
