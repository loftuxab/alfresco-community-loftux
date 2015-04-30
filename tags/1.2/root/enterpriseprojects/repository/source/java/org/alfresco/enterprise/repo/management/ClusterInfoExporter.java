/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.management.JMException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.modelmbean.ModelMBean;
import javax.management.modelmbean.RequiredModelMBean;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.enterprise.repo.cluster.checker.ClusterChecker;
import org.alfresco.enterprise.repo.cluster.checker.ClusterEvent;
import org.alfresco.enterprise.repo.cluster.checker.ClusterNodeExistsEvent;
import org.alfresco.enterprise.repo.cluster.checker.ClusterNodePairStatusEvent;
import org.alfresco.enterprise.repo.cluster.checker.ClusterNodeStopTrackingEvent;
import org.alfresco.enterprise.repo.cluster.checker.ClusterChecker.NodeStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.jmx.export.MBeanExportOperations;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.assembler.MBeanInfoAssembler;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;
import org.springframework.jmx.export.notification.ModelMBeanNotificationPublisher;
import org.springframework.jmx.export.notification.NotificationPublisherAware;

/**
 * 
 * @since Odin
 *
 */
public class ClusterInfoExporter implements ApplicationListener<ClusterEvent>
{
    private static final Log logger = LogFactory.getLog(ClusterInfoExporter.class);

    private ClusterChecker clusterChecker;
    private ClusterInfo clusterInfo;

    private WriteLock writeLock;
    
    private MBeanInfoAssembler assembler;
    
    /** The JMX exporter. */
    private MBeanExportOperations exporter;
    
    public ClusterInfoExporter()
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
    
    public void setClusterChecker(ClusterChecker clusterChecker)
    {
        this.clusterChecker = clusterChecker;
    }

    protected String getName()
    {
        return "Alfresco:Name=Cluster,Tool=QuickCheck";
    }

    protected String getClusterNodeName(String nodeId)
    {
        return getName() + ",Node=" + nodeId;
    }
    
    protected ObjectName getObjectName() throws MalformedObjectNameException
    {
        return new ObjectName(getName());
    }
    
    protected ObjectName getClusterNodeObjectName(String nodeId) throws MalformedObjectNameException
    {
        return new ObjectName(getClusterNodeName(nodeId));
    }

    protected void registerMBeans() throws RuntimeOperationsException, InvalidTargetObjectTypeException, JMException
    {
        try
        {
            writeLock.lock();

            ModelMBean mbean = new RequiredModelMBean();
            mbean.setModelMBeanInfo(this.assembler.getMBeanInfo(clusterInfo, getName()));
            mbean.setManagedResource(clusterInfo, "ObjectReference");

            this.exporter.registerManagedResource(mbean, getObjectName());
            ((NotificationPublisherAware)clusterInfo).setNotificationPublisher(new ModelMBeanNotificationPublisher(mbean, getObjectName(), clusterInfo));
        }
        finally
        {
            writeLock.unlock();
        }
    }

    protected void registerNodeMBean(String nodeId, String address) throws RuntimeOperationsException, InvalidTargetObjectTypeException, JMException
    {
        try
        {
            writeLock.lock();

            ClusterNodeInfo clusterNodeInfo = new ClusterNodeInfo(clusterChecker, nodeId);
            ModelMBean mbean = new RequiredModelMBean();
            mbean.setModelMBeanInfo(this.assembler.getMBeanInfo(clusterNodeInfo, getName()));
            mbean.setManagedResource(clusterNodeInfo, "ObjectReference");

            this.exporter.registerManagedResource(mbean, getClusterNodeObjectName(nodeId));
        }
        finally
        {
            writeLock.unlock();
        }
    }
    
    protected void unregisterNodeMBean(String nodeId) throws RuntimeOperationsException, InvalidTargetObjectTypeException, JMException
    {
        try
        {
            writeLock.lock();

            this.exporter.unregisterManagedResource(getClusterNodeObjectName(nodeId));
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
            this.clusterInfo = new ClusterInfo(clusterChecker);

            registerMBeans();
        }
        catch(Exception e)
        {
            throw new AlfrescoRuntimeException("", e);
        }
    }

    @Override
    public void onApplicationEvent(ClusterEvent event)
    {
        try
        {
            if (event instanceof ClusterNodeExistsEvent)
            {
                ClusterNodeExistsEvent clusterNodeExistsEvent = (ClusterNodeExistsEvent)event;
                String nodeId = clusterNodeExistsEvent.getNodeId();
                String address = clusterChecker.getNodeInfo(nodeId).getIPAddress();
                registerNodeMBean(nodeId, address);
                String message = "Cluster node: address = " + address + ", node id = " + nodeId;
                clusterInfo.sendNotification(ClusterNodeExistsEvent.NOTIFICATION_TYPE, message, address);
            }
            else if (event instanceof ClusterNodePairStatusEvent)
            {
                ClusterNodePairStatusEvent clusterNodePairStatusEvent = (ClusterNodePairStatusEvent)event;
                String sourceNodeId = clusterNodePairStatusEvent.getSourceNodeId();
                String targetNodeId = clusterNodePairStatusEvent.getTargetNodeId();
                NodeStatus status = clusterNodePairStatusEvent.getStatus();
                String address = clusterChecker.getNodeInfo(targetNodeId).getIPAddress();
                String message = "Source node = " + sourceNodeId + ", target node = " + targetNodeId + ", status = " + status;
                clusterInfo.sendNotification(ClusterNodePairStatusEvent.NOTIFICATION_TYPE, message, address);
            }
            else if (event instanceof ClusterNodeStopTrackingEvent)
            {
                ClusterNodeStopTrackingEvent clusterNodeStopTrackingEvent = (ClusterNodeStopTrackingEvent)event;
                String nodeId = clusterNodeStopTrackingEvent.getNodeId();
                unregisterNodeMBean(nodeId);
            }
        }
        catch(Throwable t)
        {
            throw new AlfrescoRuntimeException("Unable to register cluster node event", t);
        }
    }

}
