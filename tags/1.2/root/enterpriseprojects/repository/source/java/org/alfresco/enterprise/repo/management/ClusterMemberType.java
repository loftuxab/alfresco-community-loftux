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
 * Converts a set of cluster members to a TabularData object suitable
 * for returning via JMX.
 * 
 * @author Matt Ward
 */
public final class ClusterMemberType
{
    private static final String KEY_LAST_REGISTERED = "last.registered";
    private static final String KEY_HOST_NAME = "host.name";
    private static final String KEY_HOST_PORT = "host.port";
    private static final String KEY_HOST_IP = "host.ip";
    private final static String[] memberIndexes = { KEY_HOST_IP, KEY_HOST_PORT };
    private final static String[] memberKeys = { KEY_HOST_NAME, KEY_HOST_IP, KEY_HOST_PORT, KEY_LAST_REGISTERED };
    private final static String[] memberDescriptions = { "Host Name", "IP Address", "Port", "Last Registered" };
    private final static OpenType<?>[] memberTypes = { SimpleType.STRING, SimpleType.STRING, SimpleType.INTEGER, SimpleType.DATE };
    private static CompositeType memberCompositeType;
    private static TabularType memberTabularType;

    /**
     * Instantiation is prohibited.
     */
    private ClusterMemberType()
    {
    }
    
    private static CompositeType getMemberCompositeType()
    {
        if (memberCompositeType == null)
        {
            try
            {
                memberCompositeType = new CompositeType(
                            "Cluster Member",
                            "Cluster Member",
                            memberKeys,
                            memberDescriptions,
                            memberTypes);
            }
            catch (OpenDataException error)
            {
                throw new RuntimeException(error);
            }
        }
        return memberCompositeType;
    }

    private static TabularType getMemberTabularType()
    {
        if (memberTabularType == null)
        {
            try
            {
                memberTabularType = new TabularType(
                            "Cluster Members",
                            "Cluster Members",
                            getMemberCompositeType(),
                            memberIndexes);
            }
            catch (OpenDataException error)
            {
                throw new RuntimeException(error);
            }
        }
        return memberTabularType;
    }
    
    public static TabularData tabularData(Set<RegisteredServerInfoImpl> clusterMembers)
    {
        CompositeType compositeType = getMemberCompositeType();
        TabularType tabularType = getMemberTabularType();
        TabularDataSupport table = new TabularDataSupport(tabularType);

        for(RegisteredServerInfoImpl member : clusterMembers)
        {
            Map<String, Object> values = new HashMap<String, Object>();
            values.put(KEY_HOST_NAME, member.getHostName());
            values.put(KEY_HOST_IP, member.getIPAddress());
            values.put(KEY_HOST_PORT, member.getPort());
            values.put(KEY_LAST_REGISTERED, member.getLastRegistered());
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
