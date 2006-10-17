/*
 * Copyright (C) 2005-2006 Alfresco, Inc.
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
package org.alfresco.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A class that keeps track of the VM shutdown status.  It can be
 * used by threads either as a singleton to check if the
 * VM shutdown status has been activated.
 * <p>
 * <b>NOTE: </b> In order to prevent a proliferation of shutdown hooks,
 *      it is advisable to use instances as singletons only. 
 * <p>
 * This component should be used by long-running, but interruptable processes.
 * 
 * @author Derek Hulley
 */
public class VmShutdownListener
{
    private Log logger;
    private boolean vmShuttingDown;
    
    /**
     * Constructs this instance to listen to the VM shutdown call.
     *
     */
    public VmShutdownListener(final String name)
    {
        logger = LogFactory.getLog(VmShutdownListener.class);
        
        vmShuttingDown = false;
        Runnable shutdownRunnable = new Runnable()
        {
            public void run()
            {
                vmShuttingDown = true;
                if (logger.isDebugEnabled())
                {
                    logger.debug("VM shutdown detected by listener " + name);
                }
            };  
        };
        Thread shutdownThread = new Thread(shutdownRunnable);
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }

    /**
     * @return Returns true if the VM shutdown signal was detected.
     */
    public boolean isVmShuttingDown()
    {
        return vmShuttingDown;
    }
}
