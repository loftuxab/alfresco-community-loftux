/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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

package org.alfresco.share.repository;

import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.JmxUtils;
import org.alfresco.share.util.TelnetUtil;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Created by Olga Lokhach
 */

@Listeners(FailedTestListener.class)

public class RepositoryConfiguringCifsTests extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(RepositoryConfiguringCifsTests.class);
    private String cifsObject = "Alfresco:Type=Configuration,Category=fileServers,id1=default";
    private String cifsPort = "cifs.tcpipSMB.port";
    private String cifsEnabled = "cifs.enabled";
    private String cifsDefaultValuePort = "445";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);

        try
        {
            // CIFS server is enabled
            JmxUtils.setAlfrescoServerProperty(shareUrl, cifsObject, cifsEnabled, true);

            // Specify default 445 CIFS port
            JmxUtils.setAlfrescoServerProperty(shareUrl, cifsObject, cifsPort, cifsDefaultValuePort);

            // Click operation "Start"
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, cifsObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, cifsObject, "start");

        }
        catch (Exception e)
        {
            throw new SkipException("Skipping as pre-condition step(s) fail: " + e);
        }

    }

    /**
     * Test: AONE-6380:Change cifs.tcpipSMB.port
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6380() throws Exception
    {
        String cifsValuePort = "2450";
        boolean cifsEnabledDefault = Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(shareUrl, cifsObject, cifsEnabled).toString());
        String cifsPortDefault = JmxUtils.getAlfrescoServerProperty(shareUrl, cifsObject, cifsPort).toString();

        try
        {
            // CIFS is enabled
            assertTrue(cifsEnabledDefault, "cifs.server isn't true by default");
            assertEquals(cifsPortDefault, cifsDefaultValuePort, "CIFS is not running at the default 445 port");

            // Set cifs.tcpipSMB.port=450 via JMX (MBeans->Alfresco->Configuration->fileServers);
            JmxUtils.setAlfrescoServerProperty(shareUrl, cifsObject, cifsPort, cifsValuePort);

            // click operation "Start"
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, cifsObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, cifsObject, "start");

            // Verify that new port used by CIFS at the server
            assertFalse(TelnetUtil.connectServer(shareUrl, cifsDefaultValuePort), "Default cifs port is using");
            assertTrue(TelnetUtil.connectServer(shareUrl, cifsValuePort), "New cifs port is not used");

        }
        finally
        {
            // Set default cifs port
            JmxUtils.setAlfrescoServerProperty(shareUrl, cifsObject, cifsPort, cifsDefaultValuePort);
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, cifsObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, cifsObject, "start");
        }

    }

    /**
     * AONE-6381:Disable/enable CIFS via JMX
     */

    @Test(groups = "EnterpriseOnly", timeOut = 400000)
    public void AONE_6381() throws Exception
    {

        boolean cifsEnabledDefault = Boolean.parseBoolean(JmxUtils.getAlfrescoServerProperty(shareUrl, cifsObject, cifsEnabled).toString());
        String cifsValuePort = "2450";

        try
        {
            // CIFS is enabled
            assertTrue(cifsEnabledDefault, "cifs.server isn't enabled by default");

            // Set cifs.enabled=false via JMX
            JmxUtils.setAlfrescoServerProperty(shareUrl, cifsObject, cifsEnabled, false);
            JmxUtils.setAlfrescoServerProperty(shareUrl, cifsObject, cifsPort, cifsValuePort);

            // Invoke Start method;
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, cifsObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, cifsObject, "start");

            // Connect to Alfresco via CIFS  - User is unable to connect via CIFS
            assertFalse(TelnetUtil.connectServer(shareUrl, cifsValuePort), "CIFS is enabled");

            // Set cifs.enabled=true via JMX
            JmxUtils.setAlfrescoServerProperty(shareUrl, cifsObject, cifsEnabled, true);

            // Invoke Start method;
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, cifsObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, cifsObject, "start");

            // Connect to Alfresco via CIFS  - User is able to connect via CIFS
            assertTrue(TelnetUtil.connectServer(shareUrl, cifsValuePort), "CIFS isn't enabled");
        }

        finally
        {
            // Set default cifs port
            JmxUtils.setAlfrescoServerProperty(shareUrl, cifsObject, cifsPort, cifsDefaultValuePort);
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, cifsObject, "stop");
            JmxUtils.invokeAlfrescoServerProperty(shareUrl, cifsObject, "start");
        }
    }

}
