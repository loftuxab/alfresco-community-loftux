/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package org.alfresco.enterprise.repo.management.subsystems;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.alfresco.util.BaseSpringTest;

/**
 * This test is to ensure that on revert method invocation AuthenticatorDeletedEvent is published in
 * SynchronizationBridge#onApplicationEvent method only for instances of ChildApplicationContextFactory
 *
 * https://issues.alfresco.com/jira/browse/MNT-10359
 * @see org.alfresco.enterprise.repo.management.SynchronizationBridge#onApplicationEvent
 *
 * @author Andrey Chernov
 */
public class RevertMethodForAbstractPropertyBackedBeanChildrenTest extends BaseSpringTest
{
    // The mbean server.
    private MBeanServerConnection mbeanServer;

    /*
     * (non-Javadoc)
     * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpInTransaction()
     */
    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        super.onSetUpInTransaction();
        mbeanServer = (MBeanServerConnection) getApplicationContext().getBean("alfrescoMBeanServer");
    }


    /**
     * Test that onApplicationEvent does not throw ClassCastException
     *
     * org.alfresco.repo.management.subsystems.DefaultChildApplicationContextManager
     *
     * @throws Exception
     *     the exception
     */

    public void testAuthenticationManagerRevert() throws Exception
    {
        ObjectName authenticationManager = new ObjectName(
                "Alfresco:Type=Configuration,Category=Authentication,id1=manager");
        try
        {
            mbeanServer.invoke(authenticationManager, "revert", new Object[0], new String[0]);
        }
        catch(Exception e)
        {
            fail("Revert method invocation failed due to:" + e.getMessage());
        }
    }


    /**
     * Test that onApplicationEvent does not throw ClassCastException
     *
     * org.alfresco.repo.management.subsystems.SwitchableApplicationContextFactory
     *
     * @throws Exception
     *     the exception
     */

    public void testSearchManagerRevert() throws Exception
    {
        ObjectName searchManager = new ObjectName(
                "Alfresco:Type=Configuration,Category=Search,id1=manager");
        try
        {
            mbeanServer.invoke(searchManager, "revert", new Object[0], new String[0]);
        }
        catch(Exception e)
        {
            fail("Revert method invocation failed due to:" + e.getMessage());
        }
    }


    /**
     * Test that onApplicationEvent does not throw ClassCastException
     *
     * org.alfresco.repo.management.subsystems.CompositeDataBean
     * org.alfresco.repo.search.impl.solr.SolrChildApplicationContextFactory
     *
     * @throws Exception
     *     the exception
     */

    public void testSOLRManagedRevert() throws Exception
    {
        ObjectName solrManaged = new ObjectName(
                "Alfresco:Type=Configuration,Category=Search,id1=managed,id2=solr");
        try
        {
            mbeanServer.invoke(solrManaged, "revert", new Object[0], new String[0]);
        }
        catch(Exception e)
        {
            fail("Revert method invocation failed due to:" + e.getMessage());
        }
    }

    /**
     * Test that onApplicationEvent does not throw ClassCastException
     * org.alfresco.repo.management.subsystems.CompositeDataBean
     * org.alfresco.repo.search.impl.solr.SolrChildApplicationContextFactory
     * 
     * @throws Exception the exception
     */

    public void testSOLR4ManagedRevert() throws Exception
    {
        ObjectName solrManaged = new ObjectName(
                "Alfresco:Type=Configuration,Category=Search,id1=managed,id2=solr4");
        try
        {
            mbeanServer.invoke(solrManaged, "revert", new Object[0], new String[0]);
        }
        catch (Exception e)
        {
            fail("Revert method invocation failed due to:" + e.getMessage());
        }
    }

    /**
     * Test that onApplicationEvent does not throw ClassCastException
     *
     * org.alfresco.repo.management.subsystems.LuceneChildApplicationContextFactory
     *
     * @throws Exception
     *     the exception
     */

    public void testLuceneManagedRevert() throws Exception
    {
        ObjectName luceneManaged = new ObjectName(
                "Alfresco:Type=Configuration,Category=Search,id1=managed,id2=lucene");
        Exception ex = null;
        try
        {
            mbeanServer.invoke(luceneManaged, "revert", new Object[0], new String[0]);
            fail("Lucene should be gone, as it is not supported.");
        }
        catch (InstanceNotFoundException infe)
        {
            ex = infe;
        }

        assertTrue("Lucene should be gone, as it is not supported.", ex instanceof InstanceNotFoundException);
    }


    /**
     * Test that onApplicationEvent does not throw ClassCastException
     *
     * org.alfresco.repo.audit.model.AuditModelRegistryImpl
     *
     * @throws Exception
     *     the exception
     */

    public void testAuditDefaultRevert() throws Exception
    {
        ObjectName auditDefault = new ObjectName(
                "Alfresco:Type=Configuration,Category=Audit,id1=default");
        try
        {
            mbeanServer.invoke(auditDefault, "revert", new Object[0], new String[0]);
        }
        catch(Exception e)
        {
            fail("Revert method invocation failed due to:" + e.getMessage());
        }
    }
}
