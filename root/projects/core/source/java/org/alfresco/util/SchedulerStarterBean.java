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
package org.alfresco.util;

import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationEvent;

public class SchedulerStarterBean extends AbstractLifecycleBean
{
    protected final static Log log = LogFactory.getLog(SchedulerStarterBean.class);    
    
    private Scheduler scheduler;

    @Override
    protected void onBootstrap(ApplicationEvent event)
    {
        try
        {
            log.info("Scheduler started");
            scheduler.start();
        }
        catch (SchedulerException e)
        {
            throw new AlfrescoRuntimeException("Scheduler failed to start", e);
        }
    }

    @Override
    protected void onShutdown(ApplicationEvent event)
    {
        // Nothing required
        // This is done by the SchedulerFactoryBean.destroy() - DisposableBean 
    }

    public Scheduler getScheduler()
    {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler)
    {
        this.scheduler = scheduler;
    }

}
