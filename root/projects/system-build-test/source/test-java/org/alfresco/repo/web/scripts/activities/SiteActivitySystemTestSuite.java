
package org.alfresco.repo.web.scripts.activities;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.alfresco.repo.RepoJettyStartTest;
import org.alfresco.repo.RepoJettyStopTest;

/**
 * Site Activities system test suite (runs with embedded jetty)
 * 
 * @author janv
 */
public class SiteActivitySystemTestSuite extends TestSuite
{
    /**
     * Creates the test suite
     * 
     * @return  the test suite
     */
    public static Test suite() throws Exception
    {
        TestSuite suite = new TestSuite();
        
        RepoJettyStartTest.startJetty(); 
        
        // the following test rely on running repo
        suite.addTestSuite(SiteActivitySystemTest.class);
        
        suite.addTestSuite(RepoJettyStopTest.class);
        
        return suite;
    }
}
