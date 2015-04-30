package org.alfresco.enterprise.repo;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.alfresco.enterprise.repo.officeservices.Office2010OnWindows7SystemTest;
import org.alfresco.enterprise.repo.officeservices.OfficeServicesRootSystemTest;
import org.alfresco.enterprise.repo.officeservices.OfficeServicesVtiBinSystemTest;

/**
 * Run suite of enterprise system tests (against embedded jetty)
 * 
 * @author Stefan Kopf
 */
public class EnterpriseSystemTestSuite extends TestSuite
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
        suite.addTestSuite(EnterpriseRepoJettyStartTest.class);
        
        // enterprise remote API
        suite.addTestSuite(OfficeServicesRootSystemTest.class);
        suite.addTestSuite(OfficeServicesVtiBinSystemTest.class);
        suite.addTestSuite(Office2010OnWindows7SystemTest.class);
        //suite.addTestSuite(org.alfresco.enterprise.repo.officeservices.OfficeServicesPauseSystemTest.class);
        
        // stop (embedded) Jetty
        suite.addTestSuite(EnterpriseRepoJettyStopTest.class);
        
        return suite;
    }
}
