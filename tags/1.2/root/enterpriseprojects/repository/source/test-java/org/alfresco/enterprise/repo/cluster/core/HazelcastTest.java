/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.cluster.core;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Set;

import org.alfresco.enterprise.repo.cluster.messenger.HazelcastMessenger;
import org.alfresco.enterprise.repo.cluster.messenger.Messenger;
import org.alfresco.enterprise.repo.cluster.messenger.MessengerFactory;
import org.alfresco.enterprise.repo.cluster.messenger.MessengerTestHelper;
import org.alfresco.enterprise.repo.cluster.messenger.MessengerTestHelper.TestMessageReceiver;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.Pair;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.hazelcast.config.Config;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

/**
 * Tests for Hazelcast implementations of {@link Messenger} and related classes.
 * These are integration tests and configured through a Spring test context file.
 * 
 * @author Matt Ward
 */
public class HazelcastTest implements MessageListener<Pair<String, String>>
{
    private static ApplicationContext ctx;
    private MessengerTestHelper helper;
    private HazelcastInstanceFactory hiFactory;
    private HazelcastInstance hi;
    
    @BeforeClass
    public static void setUpClass()
    {
        ctx = ApplicationContextHelper.
                getApplicationContext(new String[] { "cluster-test/hazelcast-messenger-test.xml" });
    }
    
    @AfterClass
    public static void tearDownClass()
    {
        ApplicationContextHelper.closeApplicationContext();
    }
    
    @Before
    public void setUp()
    {
        helper = new MessengerTestHelper();
        hiFactory = ctx.getBean(HazelcastInstanceFactory.class);
        hiFactory.initInstance();
        hi = hiFactory.getInstance();
    }
 
    @After
    public void tearDown()
    {
        hiFactory.destroyAllInstances();
    }
    
    @Test
    public void canSendWithHazelcastMessengerFactory() throws InterruptedException
    {
        ITopic<Pair<String, String>> topic = hi.getTopic("testregion");
        
        topic.addMessageListener(this);
        
        MessengerFactory messengerFactory = (MessengerFactory) ctx.getBean("messengerFactory");
        Messenger<String> messenger = messengerFactory.createMessenger("testregion");
        messenger.send("Full test including spring.");
        
        helper.checkMessageReceivedWas("Full test including spring.");
    }

    @Test
    public void messengerWillNotReceiveMessagesFromSelf() throws InterruptedException
    {
        MessengerFactory messengerFactory = (MessengerFactory) ctx.getBean("messengerFactory");
        Messenger<String> m1 = messengerFactory.createMessenger("testregion");
        TestMessageReceiver r1 = new TestMessageReceiver();
        m1.setReceiver(r1);
     
        ITopic<Pair<String, String>> topic = hi.getTopic("testregion");
        Messenger<String> m2 = new HazelcastMessenger<String>(topic, "different-address-value");
        TestMessageReceiver r2 = new TestMessageReceiver();
        m2.setReceiver(r2);
        
        m1.send("This should be received by r2 but not r1");
        
        r2.helper.checkMessageReceivedWas("This should be received by r2 but not r1");
        r1.helper.checkNoMessageReceived();
    }

    @Test
    public void membershipListenerRecievesNotifications() throws Exception
    {
        TestMembershipListener ml = ctx.getBean(TestMembershipListener.class);
        
        assertFalse("Precondition of test not met", ml.isListenerNotified());
        
        // Configure the TcpIpConfig with the IP address that the first instance has bound to.
        TcpIpConfig tcpIpConfig = (TcpIpConfig) ctx.getBean("tcpIpConfig");
        tcpIpConfig.setMembers(Arrays.asList(hi.getCluster().getLocalMember().getInetAddress().getHostAddress()));

        // Create another member in the same cluster, to trigger the listener(s)
        HazelcastConfigFactoryBean configFactory = (HazelcastConfigFactoryBean) ctx.getBean("hazelcastConfig");
        Config config = configFactory.getConfig();
        HazelcastInstance hi2 = Hazelcast.newHazelcastInstance(config);
        
        assertTrue(ml.isListenerNotified());
    }
    
    @Override
    public void onMessage(Message<Pair<String, String>> message)
    {
        helper.setReceivedMsg(message.getMessageObject().getSecond());
    }
    
    
    /**
     * Used in the hazelcast-messenger-text.xml file
     */
    public static class TestClusterService implements ClusterService
    {
        @Override
        public boolean isClusteringEnabled()
        {
            return true;
        }

        @Override
        public void registerMember(String hostName, String ipAddress, int port, String nodeType)
        {
        }

        @Override
        public void registerNonMember(String hostName, String ipAddress, int port, String nodeType)
        {
        }

        @Override
        public Set<RegisteredServerInfoImpl> getOtherRegisteredMembers(String ipAddress, int port)
        {
            return null;
        }

        @Override
        public Set<RegisteredServerInfoImpl> getRegisteredNonMembers(String ipAddress, Integer port)
        {
            return null;
        }

        @Override
        public Set<RegisteredServerInfoImpl> getAllRegisteredServers()
        {
            return null;
        }

        @Override
        public void deregisterServer(String ipAddress, int port)
        {
        }
        
        @Override
        public void deregisterNonClusteredServer(String ipAddress, int port)
        {
        }

        @Override
        public String generateClusterName()
        {
            return getClusterName();
        }
        
        @Override
        public String getClusterName()
        {
            return "test-cluster";
        }

        @Override
        public void initClusterService()
        {
        }

        @Override
        public boolean isInitialised()
        {
            return true;
        }

        @Override
        public int getNumActiveClusterMembers()
        {
            return 0;
        }

        @Override
        public String getServerType()
        {
            return null;
        }

        @Override
        public String getMemberHostName()
        {
            return null;
        }

        @Override
        public String getMemberIP()
        {
            return null;
        }

        @Override
        public Integer getMemberPort()
        {
            return 0;
        }

        @Override
        public Set<RegisteredServerInfoImpl> getActiveMembers()
        {
            return null;
        }

        @Override
        public Set<RegisteredServerInfoImpl> getOfflineMembers()
        {
            return null;
        }

        @Override
        public void shutDownClusterService()
        {
        }

        @Override
        public Set<RegisteredServerInfoImpl> getAllRegisteredMembers()
        {
            return null;
        }

        @Override
        public void initNonMember()
        {
        }
    }
}
