/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.enterprise.repo.management;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.modelmbean.ModelMBean;
import javax.management.modelmbean.RequiredModelMBean;

import org.alfresco.enterprise.repo.admin.indexcheck.SOLRIndexCheckService;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.solr.SolrActiveEvent;
import org.alfresco.repo.solr.SolrEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationListener;
import org.springframework.jmx.export.MBeanExportOperations;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.assembler.MBeanInfoAssembler;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;

/**
 * Exports {@link SOLRIndexMBean} instances
 * 
 * @since 4.0
 */
public class SOLRIndexExporter implements ApplicationListener<SolrEvent>, DisposableBean
{
	private static final Log logger = LogFactory.getLog(SOLRIndexExporter.class);

	private SOLRIndexCheckService service;

	private WriteLock writeLock;

    private List<String> registeredCores = new ArrayList<String>();
    
    private MBeanInfoAssembler assembler;
    
    /** The JMX exporter. */
    private MBeanExportOperations exporter;
    
    volatile boolean destroyed = false;

    public SOLRIndexExporter()
    {
        assembler = new MetadataMBeanInfoAssembler(new AnnotationJmxAttributeSource());
    }

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
    
    public void setSolrIndexCheckService(SOLRIndexCheckService service)
	{
		this.service = service;
	}

	protected ObjectName getCoreObjectName(String core) throws MalformedObjectNameException
	{
		return new ObjectName(getCoreName(core));
	}
	
	protected String getCoreName(String core)
	{
	    return "Alfresco:Name=SolrIndexes,Core=" + core;
	}

    protected void registerMBeans() throws RuntimeOperationsException, InvalidTargetObjectTypeException, JMException
    {
		List<String> cores = service.getRegisteredCores();

    	try
    	{
    		writeLock.lock();
    		
    		if(destroyed)
    		{
    		    return;
    		}

    		// check for currently-registered core MBeans that are no longer available
			for(String core : registeredCores)
			{
				if(!cores.contains(core))
				{
					this.exporter.unregisterManagedResource(getCoreObjectName(core));
		        	registeredCores.remove(core);
				}
			}

			// check for cores MBeans that have not been exported
			for(String core : cores)
			{
				if(!registeredCores.contains(core))
				{
				    SOLRIndex solrIndex = new SOLRIndex(core, service);
				    ModelMBean mbean = new RequiredModelMBean();
		            mbean.setModelMBeanInfo(this.assembler.getMBeanInfo(solrIndex, getCoreName(core)));
		            mbean.setManagedResource(solrIndex, "ObjectReference");
				    
					this.exporter.registerManagedResource(mbean, getCoreObjectName(core));
		        	registeredCores.add(core);
				}
			}
    	}
    	finally
    	{
    		writeLock.unlock();
    	}
    }

    public void init()
    {
    	try
    	{
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
            writeLock = lock.writeLock();

            registerMBeans();
    	}
    	catch(Exception e)
    	{
    		throw new AlfrescoRuntimeException("", e);
    	}
    }

	@Override
	public void onApplicationEvent(SolrEvent event)
	{
		if(event instanceof SolrActiveEvent)
		{
			// wait for an event indicating that Solr is active then construct and export the Solr index mbeans.
			try
			{
				registerMBeans();
			}
			catch(Exception e)
			{
				throw new AlfrescoRuntimeException("Unable to register Solr index MBeans", e);
			}
		}
	}

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception
    {
        try
        {
            writeLock.lock();

            // check for currently-registered core MBeans that are no longer available
            for(String core : registeredCores)
            {
                this.exporter.unregisterManagedResource(getCoreObjectName(core));
            }
            registeredCores.clear();
            destroyed = true;
        }
        finally
        {
            writeLock.unlock();
        }

    }
    
}
