/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.web.scripts;

import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.repo.web.scripts.TestWebScriptRepoServer;
import org.springframework.extensions.webscripts.TestWebScriptServer;

/**
 * Parent class for Enterprise WebScript tests
 * 
 * @author Nick Burch
 */
public abstract class BaseEnterpriseWebScriptTest extends BaseWebScriptTest
{
    protected static final String ENTERPRISE_REMOTE_API_CONTEXT = "alfresco/enterprise-web-scripts-application-context.xml";
    protected static final String ENTERPRISE_REMOTE_API_TEST_CONTEXT = "alfresco/test-enterprise-web-scripts-application-context.xml";
    /**
     * Returns a Server with the enterprise web scripts loaded into it
     */
    @Override
    public TestWebScriptServer getServer()
    {
        return getTestServer();
    }
    
    public static TestWebScriptServer getTestServer()
    {
        // Ask for the server including the enterprise web scripts,
        //  and any test related enterprise scripts
        return TestWebScriptRepoServer.getTestServer(
                "classpath:" + ENTERPRISE_REMOTE_API_TEST_CONTEXT
        );
    }
}