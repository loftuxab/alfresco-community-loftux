package org.alfresco.repo;

import org.alfresco.repo.solr.EmbeddedSolrTest;
import org.alfresco.solr.client.SOLRAPIClientTest;
import org.alfresco.solr.test.CMISDataCreatorTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Andy
 *
 */
public class SolrClientTestSuite extends TestSuite
{

    public static Test suite() throws Exception
    {
        TestSuite suite = new TestSuite();
        // start (embedded) Jetty
        suite.addTestSuite(RepoJettyStartTest.class);
        
        // the following tests rely on running repo
        suite.addTestSuite(EmbeddedSolrTest.class);
        suite.addTestSuite(SOLRAPIClientTest.class);
        suite.addTestSuite(CMISDataCreatorTest.class);
        // stop (embedded) Jetty
        suite.addTestSuite(RepoJettyStopTest.class);
        return suite;
    }
}
