/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import org.alfresco.enterprise.repo.cluster.core.RegisteredServerInfoImpl;

/**
 * Converts a set of non-cluster-member servers to a TabularData object suitable
 * for returning via JMX.
 *  
 * @author Matt Ward
 */
public final class ClusterNonMemberType
{
    private static final String KEY_HOST_NAME = "host.name";
    private static final String KEY_HOST_IP = "host.ip";
    private final static String[] nonMemberIndexes = { KEY_HOST_IP };
    private final static String[] nonMemberKeys = { KEY_HOST_NAME, KEY_HOST_IP };
    private final static String[] nonMemberDescriptions = { "Host Name", "IP Address" };
    private final static OpenType<?>[] nonMemberTypes = { SimpleType.STRING, SimpleType.STRING };
    private static CompositeType nonMemberCompositeType;
    private static TabularType nonMemberTabularType;
    
    /**
     * Instantiation is prohibited.
     */
    private ClusterNonMemberType()
    {
    }
    
    private static CompositeType getNonMemberCompositeType()
    {
        if (nonMemberCompositeType == null)
        {
            try
            {
                nonMemberCompositeType = new CompositeType(
                            "Cluster Member",
                            "Cluster Member",
                            nonMemberKeys,
                            nonMemberDescriptions,
                            nonMemberTypes);
            }
            catch (OpenDataException error)
            {
                throw new RuntimeException(error);
            }
        }
        return nonMemberCompositeType;
    }

    private static TabularType getNonMemberTabularType()
    {
        if (nonMemberTabularType == null)
        {
            try
            {
                nonMemberTabularType = new TabularType(
                            "Cluster Members",
                            "Cluster Members",
                            getNonMemberCompositeType(),
                            nonMemberIndexes);
            }
            catch (OpenDataException error)
            {
                throw new RuntimeException(error);
            }
        }
        return nonMemberTabularType;
    }
    
    public static TabularData tabularData(Set<RegisteredServerInfoImpl> nonMembers)
    {
        CompositeType compositeType = getNonMemberCompositeType();
        TabularType tabularType = getNonMemberTabularType();
        TabularDataSupport table = new TabularDataSupport(tabularType);

        for(RegisteredServerInfoImpl server : nonMembers)
        {
            Map<String, Object> values = new HashMap<String, Object>();
            values.put(KEY_HOST_NAME, server.getHostName());
            values.put(KEY_HOST_IP, server.getIPAddress());
            CompositeDataSupport row;
            try
            {
                row = new CompositeDataSupport(compositeType, values);
            }
            catch (OpenDataException error)
            {
                throw new RuntimeException(error);
            }
            table.put(row);
        }
        
        return table;
    }
}
