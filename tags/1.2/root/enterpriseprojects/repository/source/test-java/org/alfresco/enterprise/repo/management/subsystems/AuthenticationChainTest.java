/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management.subsystems;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.alfresco.jlan.server.config.ServerConfigurationAccessor;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.util.BaseSpringTest;

/**
 * This test is primarily to ensure that the Authentication chaining subsystem is in order. Through the JMX interface,
 * it exercises various aspects of the authentication chain, including interaction with authentication filters and the
 * CIFS file server.
 * 
 * @author dward
 */
public class AuthenticationChainTest extends BaseSpringTest
{

    /** The mbean server. */
    private MBeanServerConnection mbeanServer;

    /** The default authentication chain order. */
    private String defaultOrder;

    /** The name of the MBean managing the authentication chain */
    private static final ObjectName AUTH_CHAIN_OBJECT_NAME;

    /** The name of the MBean attribute holding the authentication chain order. */
    private static final String ORDER_PROPERTY = "chain";

    static
    {
        try
        {
            AUTH_CHAIN_OBJECT_NAME = new ObjectName("Alfresco:Type=Configuration,Category=Authentication,id1=manager");
        }
        catch (MalformedObjectNameException e)
        {
            throw new ExceptionInInitializerError(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpInTransaction()
     */
    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        super.onSetUpInTransaction();
        mbeanServer = (MBeanServerConnection) getApplicationContext().getBean("alfrescoMBeanServer");
        defaultOrder = (String) mbeanServer.getAttribute(AUTH_CHAIN_OBJECT_NAME, ORDER_PROPERTY);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.test.AbstractTransactionalSpringContextTests#onTearDownInTransaction()
     */
    @Override
    protected void onTearDownInTransaction() throws Exception
    {
        // Restore the default authentication chain after each test
        mbeanServer.setAttribute(AUTH_CHAIN_OBJECT_NAME, new Attribute(ORDER_PROPERTY, defaultOrder));
        super.onTearDownInTransaction();
    }

    /**
     * Tests that an authentication chain with more than one member functions.
     * 
     * @throws Exception
     *             the exception
     */
    public void testChained() throws Exception
    {
        // Add an ldap instance in to the chain
        mbeanServer.setAttribute(AUTH_CHAIN_OBJECT_NAME, new Attribute(ORDER_PROPERTY,
                "alfrescoNtlm1:alfrescoNtlm,ldap1:ldap"));

        // Call a chaining authentication method (this will start both subsystems), ensure initialisation was successful
        // and expected results are obtained
        assertEquals(Collections.singleton(AuthenticationUtil.getAdminUserName()),
                ((AuthenticationComponent) getApplicationContext().getBean("AuthenticationComponent"))
                        .getDefaultAdministratorUserNames());
    }

    /**
     * Tests that when SSO is activated (a non-default filter set), the authentication subsystem still starts. Tries
     * in isolation and in a chain.
     * 
     * @throws Exception
     *             the exception
     */
    public void testSSO() throws Exception
    {
        // Test with just one instance in the chain
        mbeanServer.setAttribute(AUTH_CHAIN_OBJECT_NAME, new Attribute(ORDER_PROPERTY, "alfrescoNtlm1:alfrescoNtlm"));

        // Turn on SSO (i.e. swap over the filter set)
        ObjectName alfrescoNtlm1 = new ObjectName(
                "Alfresco:Type=Configuration,Category=Authentication,id1=managed,id2=alfrescoNtlm1");
        mbeanServer.setAttribute(alfrescoNtlm1, new Attribute("ntlm.authentication.sso.enabled", "true"));

        // Ensure the subsystem still starts, i.e. the filters are happy their authentication component is compatible
        mbeanServer.invoke(alfrescoNtlm1, "start", new Object[0], new String[0]);

        // Turn SSO back off again
        mbeanServer.setAttribute(alfrescoNtlm1, new Attribute("ntlm.authentication.sso.enabled", "false"));

        // Let's add another instance to the chain
        mbeanServer.setAttribute(AUTH_CHAIN_OBJECT_NAME, new Attribute(ORDER_PROPERTY,
                "alfrescoNtlm1:alfrescoNtlm,alfrescoNtlm2:alfrescoNtlm"));

        // Turn on SSO (i.e. swap over the filter set)
        ObjectName alfrescoNtlm2 = new ObjectName(
                "Alfresco:Type=Configuration,Category=Authentication,id1=managed,id2=alfrescoNtlm2");
        mbeanServer.setAttribute(alfrescoNtlm2, new Attribute("kerberos.authentication.sso.enabled", "true"));

        // Call a chaining authentication method (this will start both subsystems), ensure initialisation was successful
        // and expected results are obtained
        assertEquals(Collections.singleton(AuthenticationUtil.getAdminUserName()),
                ((AuthenticationComponent) getApplicationContext().getBean("AuthenticationComponent"))
                        .getDefaultAdministratorUserNames());
        
        // Test with just one passthru instance in the chain
        mbeanServer.setAttribute(AUTH_CHAIN_OBJECT_NAME, new Attribute(ORDER_PROPERTY, "passthru1:passthru"));

        // Turn on SSO (i.e. swap over the filter set) using a batch edit
        ObjectName passthru1 = new ObjectName(
                "Alfresco:Type=Configuration,Category=Authentication,id1=managed,id2=passthru1");
        Attribute[] attributes = new Attribute[]
        {
            new Attribute("ntlm.authentication.sso.enabled", "true"),
            new Attribute("passthru.authentication.domain", ""),
            new Attribute("passthru.authentication.servers", "localhost")
        };
        mbeanServer.setAttributes(passthru1, new AttributeList(Arrays.asList(attributes)));

        // Ensure the subsystem still starts, i.e. the filters are happy their authentication component is compatible
        mbeanServer.invoke(passthru1, "start", new Object[0], new String[0]);
        
        Object offlineCheckInterval = mbeanServer.getAttribute(passthru1, "passthru.authentication.offlineCheckInterval");        

        // Try a failed edit with an invalid value
        try
        {
            attributes = new Attribute[]
            {
                new Attribute("ntlm.authentication.sso.enabled", "true"),
                new Attribute("passthru.authentication.domain", ""),
                new Attribute("passthru.authentication.servers", "localhost"),
                new Attribute("passthru.authentication.offlineCheckInterval", "-900")
            };
            mbeanServer.setAttributes(passthru1, new AttributeList(Arrays.asList(attributes)));
            fail("Expected exception");
        }
        catch (Exception e)
        {
            // Ensure the invalid value isn't persisted
            assertEquals(offlineCheckInterval, mbeanServer.getAttribute(passthru1, "passthru.authentication.offlineCheckInterval"));
        }

        // Test with just one kerberos instance in the chain
        mbeanServer.setAttribute(AUTH_CHAIN_OBJECT_NAME, new Attribute(ORDER_PROPERTY, "kerberos1:kerberos"));

        // Turn off SSO (i.e. swap over the filter set)
        ObjectName kerberos1 = new ObjectName(
                "Alfresco:Type=Configuration,Category=Authentication,id1=managed,id2=kerberos1");
        mbeanServer.setAttribute(kerberos1, new Attribute("kerberos.authentication.sso.enabled", "false"));
        mbeanServer.setAttribute(kerberos1, new Attribute("kerberos.authentication.authenticateCIFS", "false"));

        // Ensure the subsystem still starts, i.e. the filters are happy their authentication component is compatible
        mbeanServer.invoke(kerberos1, "start", new Object[0], new String[0]);
        
    }

    /**
     * Tests that the CIFS server recognises when it can't start (i.e. there is no CIFSAuthenticator in place). Tries
     * first a chain with a single LDAP instance to make sure it doesn't start, then adds in NTLM to see if it does
     * start.
     * 
     * @throws Exception
     *             the exception
     */
    public void testCIFS() throws Exception
    {
        // If our native environment isn't set up to run CIFS, there's not much else we can do so exit!
        ServerConfigurationAccessor config = (ServerConfigurationAccessor) getApplicationContext().getBean(
                "fileServerConfiguration");
        if (!config.isServerRunning("CIFS"))
        {
            return;
        }

        // Shut down the file servers
        ObjectName fileServers = new ObjectName("Alfresco:Type=Configuration,Category=fileServers,id1=default");
        mbeanServer.invoke(fileServers, "stop", new Object[0], new String[0]);

        // Test with just one instance in the chain
        mbeanServer.setAttribute(AUTH_CHAIN_OBJECT_NAME, new Attribute(ORDER_PROPERTY, "ldap1:ldap"));

        // Ensure the subsystem still starts, i.e. the filters are happy their authentication component is compatible
        ObjectName ldap1 = new ObjectName("Alfresco:Type=Configuration,Category=Authentication,id1=managed,id2=ldap1");
        mbeanServer.invoke(ldap1, "start", new Object[0], new String[0]);

        // Start the file servers - we should find the CIFS server doesn't start because we don't have a compatible
        // authenticator
        mbeanServer.invoke(fileServers, "start", new Object[0], new String[0]);
        assertFalse(config.isServerRunning("CIFS"));

        // Shut down the file servers
        mbeanServer.invoke(fileServers, "stop", new Object[0], new String[0]);

        // Now add alfresco authentication to the chain. This is compatible with CIFS
        mbeanServer.setAttribute(AUTH_CHAIN_OBJECT_NAME, new Attribute(ORDER_PROPERTY,
                "ldap1:ldap,alfrescoNtlm1:alfrescoNtlm"));

        // Start the file servers - we should find the CIFS server starts
        mbeanServer.invoke(fileServers, "start", new Object[0], new String[0]);
        assertTrue(config.isServerRunning("CIFS"));
    }

}
