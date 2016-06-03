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
package org.alfresco.share.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;

/**
 * Provides single entry point for creating datasets for tests
 * 
 * @author mbhave
 * 
 */
public class TestDataSetup extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(TestDataSetup.class);
    private static final String PROP_FILE = "src/main/resources/webdrone.properties";
    
    public static void main(String[] args)
    {
        Properties properties = new Properties();
        try
        {
            properties.load(new FileInputStream(PROP_FILE));
            List<XmlSuite> suites = new ArrayList<XmlSuite>();
            List<String> files = new ArrayList<String>();

            String baseDirectory = properties.getProperty("date.prep.baseDirectory");
            String suiteFiles = properties.getProperty("data.prep.testng.suite.files");

            StringTokenizer suiteFileTokenizer = new StringTokenizer(suiteFiles, ",");
            while (suiteFileTokenizer.hasMoreElements())
            {
                String suiteFile = (String) suiteFileTokenizer.nextElement();
                files.add(baseDirectory + suiteFile.trim());
            }

            XmlSuite suite = new XmlSuite();
            suite.setName("Data Prepartion suite");
            suite.setSuiteFiles(files);
            suites.add(suite);
            TestNG testNG = new TestNG();

            testNG.setXmlSuites(suites);
            testNG.run();
        }
        catch (IOException exception)
        {
            logger.error("Not able to read the property file: " + PROP_FILE);
        }
        
    }
 }