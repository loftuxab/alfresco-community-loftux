package org.alfresco.repo;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.alfresco.repo.web.scripts.activities.SiteActivitySystemTest;
import org.alfresco.solr.client.SOLRAPIClientTest;

/**
 * Run suite of miscellaneous system tests (against embedded jetty)
 * 
 * @author janv
 */
public class MiscSystemTestSuite extends TestSuite
{
    /**
     * Creates the test suite
     * 
     * @return  the test suite
     */
    public static Test suite() throws Exception
    {
        TestSuite suite = new TestSuite();
        
        // start (embedded) Jetty
        suite.addTestSuite(RepoJettyStartTest.class);
        
        // the following tests rely on running repo
        
        // site activities
        suite.addTestSuite(SiteActivitySystemTest.class);
        
        // SOLR
        suite.addTestSuite(SOLRAPIClientTest.class);
        
        // stop (embedded) Jetty
        suite.addTestSuite(RepoJettyStopTest.class);
        
        return suite;
    }
}
