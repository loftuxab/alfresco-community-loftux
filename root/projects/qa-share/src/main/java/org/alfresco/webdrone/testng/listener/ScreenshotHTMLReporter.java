/*
 * #%L
 * qa-share
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.webdrone.testng.listener;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.alfresco.share.util.AbstractUtils;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.IResultListener2;
import org.uncommons.reportng.HTMLReporter;

/**
 * @author Ranjith Manyam
 */
public class ScreenshotHTMLReporter extends HTMLReporter implements IResultListener2
{

    protected static final ScreenshotReportNGUtils SS_UTILS = new ScreenshotReportNGUtils();
    public static final String SLASH = File.separator;

    private static final Logger logger = LoggerFactory.getLogger(ScreenshotHTMLReporter.class);


    @Override
    public void beforeConfiguration(ITestResult tr)
    {

    }

    @Override
    public void onConfigurationSuccess(ITestResult tr)
    {

    }

    @Override
    public void onStart(ITestContext context)
    {

    }

    @Override
    public void onFinish(ITestContext context)
    {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result)
    {

    }
    
    @Override
    public void onTestStart(ITestResult result)
    {

    }    

    @Override
    public void onTestSuccess(ITestResult result)
    {

    }

    @Override
    public void onTestFailure(ITestResult tr)
    {
        reportErrorWithScreenShot(tr);
    }

    @Override
    public void onTestSkipped(ITestResult tr)
    {
        reportErrorWithScreenShot(tr);
    }

    @Override
    public void onConfigurationFailure(ITestResult tr)
    {
        reportErrorWithScreenShot(tr);
    }

    @Override
    public void onConfigurationSkip(ITestResult tr)
    {
        reportErrorWithScreenShot(tr);
    }
    
    protected VelocityContext createContext()
    {
        VelocityContext context = super.createContext();
        context.put("utils", SS_UTILS);
        return context;
    }

    private void reportErrorWithScreenShot(ITestResult tr)
    {
        Object instace = tr.getInstance();
        if (instace instanceof AbstractUtils)
        {
            AbstractUtils abstractTests = (AbstractUtils) instace;
            Map<String, WebDrone> droneMap = abstractTests.getDroneMap();
            saveScreenShots(tr, droneMap);
        }
    }

    private void saveScreenShots(ITestResult tr, Map<String, WebDrone> droneMap)
    {
        for (Map.Entry<String, WebDrone> entry : droneMap.entrySet())
        {
            if (entry.getValue() != null)
            {
                try
                {
                    File file = entry.getValue().getScreenShot();

                    logger.debug("File: {} ", file.hashCode());

                    // output dir includes suite, so go up one level
                    String outputDir = tr.getTestContext().getOutputDirectory();
                    logger.debug("Output Directory: {}", outputDir);
                    outputDir = outputDir.substring(0, outputDir.lastIndexOf(SLASH)) + SLASH + "html";
                    File saved = new File(outputDir, entry.getKey() + tr.getMethod().getMethodName() + ".png");
                    FileUtils.copyFile(file, saved);

                    // save screenshot path as result attribute so generateReport can access it
                    tr.setAttribute(entry.getKey() + tr.getMethod().getMethodName(), saved.getName());
                }
                catch (IOException ex)
                {
                    logger.error("Error generating screenshot" + ex);
                }
            }
        }
    }
}
