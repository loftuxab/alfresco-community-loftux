/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.enterprise.repo.cluster.core;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Implementation of {@link NonMemberIPAddrPicker} that requests the local host's
 * IP address from the JVM and returns it.
 * 
 * TODO: implement an address picker that uses a similar algorithm to Hazelcast
 * and takes into account, the alfresco.cluster.interface property - matching
 * against its wildcarded value.
 * 
 * @author Matt Ward
 */
public class NonMemberIPAddressPickerImpl implements NonMemberIPAddrPicker
{
    @Override
    public String pick()
    {
        InetAddress inetAddr;
        try
        {
            inetAddr = InetAddress.getLocalHost();
        }
        catch (UnknownHostException error)
        {
            throw new RuntimeException("Unable to get local host info.", error);
        }
        
        String ipAddr = inetAddr.getHostAddress();
        return ipAddr;
    }   
}
