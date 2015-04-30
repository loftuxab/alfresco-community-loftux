/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.descriptor.Descriptor;
import org.alfresco.service.descriptor.DescriptorService;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.test_category.OwnJVMTestsCategory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Integration tests for the {@link ClusterServiceImpl} class.
 * 
 * @author Matt Ward
 */
public class ClusterServiceImplSpringTest extends AbstractLicensedClusterTestBase
{
    private static ClusterService clusterService;
    private static AttributeService attributeService;
    private static String ip;
    private static int port;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        clusterService = (ClusterService) getContext().getBean("clusterService");
        attributeService = (AttributeService) getContext().getBean("attributeService");
        
        forceWritable(new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                // Ensure a fresh cluster name is generated
                if (clusterService.getClusterName() != null)
                { 
                    attributeService.removeAttributes(new Serializable[] { ".clusterInfo", ".cluster_name" });
                }
                clusterService.generateClusterName();
                return null;
            }
        });
        
        // This machine's IP address and port as reported by the underlying clustering library.
        ip = clusterService.getMemberIP();
        port = clusterService.getMemberPort();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
        // Clear out old test data
        forceWritable(new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                AttributeService attributeService = getContext().getBean("attributeService", AttributeService.class); 
                attributeService.removeAttributes(new Serializable[] { ".clusterMembers" });
                attributeService.removeAttributes(new Serializable[] { ".clusterInfo" });
                return null;
            }
        });
    }
    
    @Test
    public void serverRegistersSelfOnStartup() throws UnknownHostException
    {
        List<InetAddress> localAddresses = localIPAddresses();
        Set<RegisteredServerInfoImpl> servers = clusterService.getAllRegisteredServers();
        boolean found = false;
        
        // Check through all this machine's IP addresses, to see if any of the
        // registered servers match.
        for (InetAddress localAddr : localAddresses)
        {
            for (RegisteredServerInfoImpl server : servers)
            {
                if (server.getIPAddress().equals(localAddr.getHostAddress()))
                {
                    found = true;
                    break;
                }
            }            
        }

        if (!found)
        {
            StringBuilder localIPs = new StringBuilder("[ ");
            Iterator<InetAddress> localAddrIt = localAddresses.iterator();
            while (localAddrIt.hasNext())
            {
                InetAddress localAddr = localAddrIt.next();
                localIPs.append(localAddr.getHostAddress());
                if (localAddrIt.hasNext())
                {
                    localIPs.append(", ");
                }
            }
            localIPs.append(" ]");
            
            StringBuilder serverIPs = new StringBuilder("[ ");
            Iterator<RegisteredServerInfoImpl> serverIt = servers.iterator();
            while (serverIt.hasNext())
            {
                RegisteredServerInfoImpl server = serverIt.next();
                serverIPs.append(server.getIPAddress());
                if (serverIt.hasNext())
                {
                         serverIPs.append(", ");
                }
            }
            serverIPs.append(" ]");
            
            StringBuilder message = new StringBuilder();
            message.append("Server did not register itself, but should have.")
                   .append("\n  Servers: ").append(serverIPs)
                   .append("\n  Local IPs: ").append(localIPs);
            
            fail(message.toString());
        }
    }

    @Test
    public void canGetOtherMembers()
    {
        forceWritable(new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                clusterService.registerMember("a.example.com", "192.168.1.101", 9201, "Alfresco repository");
                clusterService.registerMember("c.example.com", "192.168.1.103", 9201, "Alfresco repository");
                clusterService.registerNonMember("d.example.com", "192.168.1.104", 9201, "Alfresco repository");
                clusterService.registerNonMember("e.example.com", "192.168.1.105", 9201, "Alfresco repository");                
                
                
                Set<RegisteredServerInfoImpl> members = clusterService.getOtherRegisteredMembers(ip, port);
                
                // Sort the results to make assertions easier.
                List<RegisteredServerInfoImpl> memberList = sortByHostName(members);
                
                assertEquals(2, members.size());
                assertEquals("a.example.com", memberList.get(0).getHostName());
                assertEquals("c.example.com", memberList.get(1).getHostName());
                return null;
            }
        });
    }
    
    @Test
    public void canGetNonMembers()
    {
        forceWritable(new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                clusterService.registerMember("a.example.com", "192.168.1.101", 9201, "Alfresco repository");
                clusterService.registerNonMember("c.example.com", "192.168.1.103", 9201, "Alfresco repository");
                clusterService.registerMember("d.example.com", "192.168.1.104", 9201, "Alfresco repository");
                clusterService.registerNonMember("e.example.com", "192.168.1.105", 9201, "Alfresco repository");                
                
                Set<RegisteredServerInfoImpl> members = clusterService.getRegisteredNonMembers(ip, port);
                
                // Sort the results to make assertions easier.
                List<RegisteredServerInfoImpl> memberList = sortByHostName(members);
                
                assertEquals(2, members.size());
                assertEquals("c.example.com", memberList.get(0).getHostName());
                assertEquals("e.example.com", memberList.get(1).getHostName());
                return null;
            }
        });
    }
    
    @Test
    public void canSetServerLeft()
    {
        forceWritable(new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                clusterService.registerMember("a.example.com", "192.168.1.101", 9201, "Alfresco repository");
                clusterService.registerNonMember("c.example.com", "192.168.1.103", 9201, "Alfresco repository");
                clusterService.registerMember("d.example.com", "192.168.1.104", 9201, "Alfresco repository");
                clusterService.registerNonMember("e.example.com", "192.168.1.105", 9201, "Alfresco repository");                
                
                // Remove a and d
                clusterService.deregisterServer("192.168.1.101", 9201);
                clusterService.deregisterServer("192.168.1.104", 9201);
                
                Set<RegisteredServerInfoImpl> members = clusterService.getOtherRegisteredMembers(ip, port);
                assertEquals(0, members.size());
                
                Set<RegisteredServerInfoImpl> nonMembers = clusterService.getRegisteredNonMembers(ip, port);
                List<RegisteredServerInfoImpl> nonMemberList = sortByHostName(nonMembers);
                assertEquals(2, nonMembers.size());
                assertEquals("c.example.com", nonMemberList.get(0).getHostName());
                assertEquals("e.example.com", nonMemberList.get(1).getHostName());
                return null;
            }
        });
    }
    
    @Test
    public void canDeregisterNonClusteredServer()
    {
        forceWritable(new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                clusterService.registerMember("a.example.com", "192.168.1.101", 9201, "Alfresco repository");
                clusterService.registerNonMember("c.example.com", "192.168.1.103", 9201, "Alfresco repository");
                clusterService.registerMember("d.example.com", "192.168.1.104", 9201, "Alfresco repository");
                clusterService.registerNonMember("e.example.com", "192.168.1.105", 9201, "Alfresco repository");                
                
                // Remove a and d
                clusterService.deregisterNonClusteredServer("192.168.1.101", 9201);
                clusterService.deregisterNonClusteredServer("192.168.1.104", 9201);
                
                Set<RegisteredServerInfoImpl> members = clusterService.getOtherRegisteredMembers(ip, port);
                assertEquals(0, members.size());
                
                Set<RegisteredServerInfoImpl> nonMembers = clusterService.getRegisteredNonMembers(ip, port);
                List<RegisteredServerInfoImpl> nonMemberList = sortByHostName(nonMembers);
                assertEquals(2, nonMembers.size());
                assertEquals("c.example.com", nonMemberList.get(0).getHostName());
                assertEquals("e.example.com", nonMemberList.get(1).getHostName());
                
                return null;
            }
        });
    }
  
    @Test
    public void exceptionRaisedWhenServerNotFoundForDeregister()
    {
        try
        {
            clusterService.deregisterServer("192.168.1.123", 900);
            fail(ServerNotFoundException.class.getSimpleName() + " should have been raised.");
        }
        catch (ServerNotFoundException e)
        {
            // Good
        }
        
        try
        {
            clusterService.deregisterNonClusteredServer("192.168.1.123", 900);
            fail(ServerNotFoundException.class.getSimpleName() + " should have been raised.");
        }
        catch (ServerNotFoundException e)
        {
            // Good
        }
    }
    
    @Test
    public void canGetClusterName()
    {
        DescriptorService descriptorService = (DescriptorService) getContext().getBean("descriptorComponent");
        Descriptor descriptor = descriptorService.getCurrentRepositoryDescriptor();
        String repoName = descriptor.getName();
        String repoId = descriptor.getId();
        String clusterName = clusterService.getClusterName();
        assertCorrectClusterName(clusterName, repoName.replaceAll(" ", ""), repoId);
    }

    private void assertCorrectClusterName(String clusterName, String repoName, String repoId)
    {
        String[] clusterNameParts = clusterName.split("-", 2); 
        assertEquals("Cluster name should be two parts separated by '-'", 2, clusterNameParts.length);
        assertTrue("Cluster name should start with '" + repoName + "'.", clusterNameParts[0].equals(repoName));
        assertTrue("Cluster name should end with repository ID/GUID", clusterNameParts[1].equals(repoId));
    }

    private List<RegisteredServerInfoImpl> sortByHostName(Set<RegisteredServerInfoImpl> servers)
    {
        List<RegisteredServerInfoImpl> serverList = new ArrayList<RegisteredServerInfoImpl>(servers);
        Collections.sort(serverList, new Comparator<RegisteredServerInfoImpl>()
        {
            @Override
            public int compare(RegisteredServerInfoImpl o1, RegisteredServerInfoImpl o2)
            {
                // Sort by host name
                return o1.getHostName().compareTo(o2.getHostName());
            }
        });
        return serverList;
    }
    
    private List<InetAddress> localIPAddresses()
    {
        Enumeration<NetworkInterface> nics;
        try
        {
            nics = NetworkInterface.getNetworkInterfaces();
            
            List<InetAddress> localAddrs = new ArrayList<InetAddress>();
            
            while (nics.hasMoreElements())
            {
                NetworkInterface nic = nics.nextElement();
                Enumeration<InetAddress> inets = nic.getInetAddresses();
                while (inets.hasMoreElements())
                {
                    InetAddress inet = inets.nextElement();
                    if (nic.isUp() && !inet.isLoopbackAddress())
                    {
                        localAddrs.add(inet);
                    }
                }
            }
            
            return localAddrs;
        }
        catch (SocketException e)
        {
            throw new RuntimeException("Unable to enumerate local IP addresses.", e);
        }
    }
    
    private static void forceWritable(RetryingTransactionCallback<?> txWork)
    {
        TransactionService txService = (TransactionService) getContext().getBean("transactionService");
        RetryingTransactionHelper txHelper = txService.getRetryingTransactionHelper();
        txHelper.setForceWritable(true);
        txHelper.doInTransaction(txWork, false, true);
    }
}
