/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
 package org.alfresco.enterprise.repo.management;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.alfresco.repo.security.sync.SynchronizeDirectoryStartEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.jmx.export.MBeanExportOperations;

/**
 * Class to un-export directory synchronization JMX batch beans on 
 * sync start - cleans out old junk batch processes at the start of the user
 * directory sync
 * 
 * @author mrogers
 */
public class SynchronizationUnExporter implements ApplicationListener<SynchronizeDirectoryStartEvent>
{
    
    /** The JMX exporter. */
    private MBeanExportOperations exporter;
    
    private static final Log logger = LogFactory.getLog(SynchronizationUnExporter.class);
    
    /**
     * Sets the JMX exporter.
     * 
     * @param exporter
     *            the JMX exporter
     */
    public void setJmxExporter(MBeanExportOperations exporter)
    {
        this.exporter = exporter;
    }

    @Override
    public void onApplicationEvent(SynchronizeDirectoryStartEvent event)
    {
       for(String batchName : event.getBatchProcessNames()) 
       {
           try
           {
               exporter.unregisterManagedResource(new ObjectName("Alfresco:Name=BatchJobs,Type="+batchName));
           } 
           catch (MalformedObjectNameException e)
           {
               logger.error(e);
           }
       }
    }
}
