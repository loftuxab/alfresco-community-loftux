/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.cmis.test.ws;

import java.io.PrintStream;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.ResultPrinter;
import junit.textui.TestRunner;

import org.alfresco.cmis.test.ws.wsi.Profiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CmisWebServiceTestSuite extends TestSuite
{
    private static Log LOGGER = LogFactory.getLog(CmisWebServiceTestSuite.class);

    private static int executed = 0;
    private static int failed = 0;
    private static long time = 0;
    private static boolean flag = true;

    public static void main(String[] args)
    {
        LOGGER.info("******************************************************************");
        LOGGER.info("*                                                                *");
        LOGGER.info("*                          CMIS TEST                             *");
        LOGGER.info("*       Copyright (C) 2005-2009 Alfresco Software Limited.       *");
        LOGGER.info("*                                                                *");
        LOGGER.info("******************************************************************");
        LOGGER.info("");
        LOGGER.info("\r Usage: Add '-wsi' option to run WS-I Profiler,\n if no option provided, UnitTests will be run\n");
        if (args != null && args.length > 0 && "-wsi".equalsIgnoreCase(args[0]))
        {
            LOGGER.info("Starting WS-I Profiler");
            Profiler.main(args);
        }
        else
        {
            LOGGER.info("Starting UnitTests");
            LOGGER.info("");
            TestRunner testRunner = new TestRunner();
            ResultPrinter printer = new CMISResultPrinter(System.out);
            testRunner.setPrinter(printer);
            LOGGER.info("Testing DiscoveryService");
            LOGGER.info("----------------------------");
            testRunner.doRun(new TestSuite(CmisDiscoveryServiceClient.class));
            LOGGER.info("Testing MultifilingService");
            LOGGER.info("----------------------------");
            testRunner.doRun(new TestSuite(CmisMultifilingServiceClient.class));
            LOGGER.info("Testing NavigationService");
            LOGGER.info("----------------------------");
            testRunner.doRun(new TestSuite(CmisNavigationServiceClient.class));
            LOGGER.info("Testing ObjectService");
            LOGGER.info("----------------------------");
            testRunner.doRun(new TestSuite(CmisObjectServiceClient.class));
            LOGGER.info("Testing RelationshipService");
            LOGGER.info("----------------------------");
            testRunner.doRun(new TestSuite(CmisRelationshipServiceClient.class));
            LOGGER.info("Testing RepositoryService");
            LOGGER.info("----------------------------");
            testRunner.doRun(new TestSuite(CmisRepositoryServiceClient.class));
            LOGGER.info("Testing VersioningService");
            LOGGER.info("----------------------------");
            testRunner.doRun(new TestSuite(CmisVersioningServiceClient.class));
            LOGGER.info("");
            LOGGER.info("");
            LOGGER.info("Finished");
            LOGGER.info("Totally spent time: " + time + " ms");
            LOGGER.info("------------------------------------------------------");
            LOGGER.info("Total passed: " + (executed - failed));
            LOGGER.info("Total failed: " + failed);
            LOGGER.info("Total executed: " + executed);
            LOGGER.info("------------------------------------------------------");
        }
    }

    private static class CMISResultPrinter extends ResultPrinter
    {

        public CMISResultPrinter(PrintStream writer)
        {
            super(writer);
        }

        @Override
        public void addError(Test test, Throwable t)
        {
            LOGGER.info("   !!! Test failed !!!");
            LOGGER.info("Message: " + t.toString());
            flag = false; 
        }

        @Override
        public void addFailure(Test test, AssertionFailedError t)
        {
            LOGGER.info("   !!! Test failed !!!");
            LOGGER.info("Message: " + t.toString());
            flag = false; 
        }

        @Override
        public void startTest(Test test)
        {
            LOGGER.info("Executing test " + test);
            flag = true;
        }

        @Override
        public void endTest(Test test)
        {
            if (flag)
            {
                LOGGER.info("... test passed");
            }
            LOGGER.info("");
        }

        @Override
        protected void printFooter(TestResult result)
        {
            LOGGER.info("----------------------------");
            LOGGER.info("Passed: " + (result.runCount() - result.failureCount() - result.errorCount()));
            LOGGER.info("Failed: " + (result.failureCount() + result.errorCount()));
            LOGGER.info("Executed : " + result.runCount());
            LOGGER.info("----------------------------");
            LOGGER.info("");
            LOGGER.info("");
            executed += result.runCount();
            failed += (result.errorCount() + result.failureCount());
        }

        @Override
        protected void printHeader(long runTime)
        {
            LOGGER.info("");
            LOGGER.info("Execution summary: ");
            LOGGER.info("Spent time: " + runTime + " ms");
            time += runTime;
        }
        
        @Override
        protected void printErrors(TestResult result)
        {
        }

        @Override
        protected void printFailures(TestResult result)
        {
        }

        @Override
        protected void printDefectHeader(TestFailure booBoo, int count)
        {
            // LOGGER.info(count + ") " + booBoo.failedTest());
        }

        @Override
        protected void printDefectTrace(TestFailure booBoo)
        {
            // LOGGER.info(BaseTestRunner.getFilteredTrace(booBoo.trace()));
        }
    }
}
