/*
 * Copyright (C) 2005 Alfresco, Inc.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.alfresco.benchmark.framework;

import java.util.HashMap;
import java.util.Map;

import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;

/**
 * @author Roy Wetherall
 */
public abstract class BaseBenchmarkDriver extends JapexDriverBase
{        
    public static final String PARAM_ALFRESCO_BENCHAMRK_TYPE = "alfresco.bechmarkType";    
//    public static final String PARAM_CONTENT_SIZE = "alfresco.contentSize";
//    public static final String PARAM_CONTENT_MIMETYPE = "alfresco.contentMimetype";
//    public static final String PARAM_USER_NAME = "alfresco.userName";
    public static final String PARAM_NUMBER_OF_AVAILABLE_USERS  = "alfresco.numberOfAvailableUsers";
    public static final String PARAM_LOAD_DEPTH = "alfresco.loadDepth";
    
    public static final int DEFAULT_NUMBER_OF_AVAILABLE_USERS = 50; 
    
    private Map<BenchmarkType, Integer> usageProfile;
    
    protected enum BenchmarkType
    {
        CREATE_CONTENT,
        READ_CONTENT,
        CREATE_FOLDER,
        READ_PROPERTIES,
        CREATE_VERSION,
        FTS_SEARCH;
        
        private static final String PARAM_PREFIX = "alfresco.usageProfile.";
        private static final String LABEL_CREATE_CONTENT = "createContent";
        private static final String LABEL_READ_CONTENT = "readContent";
        private static final String LABEL_CREATE_FOLDER = "createFolder";
        private static final String LABEL_READ_PROPERTIES = "readProperties";
        private static final String LABEL_CREATE_VERSION = "createVersion";
        private static final String LABEL_FTS_SEARCH = "ftsSearch";      
        
        public String getParamName()
        {
            return PARAM_PREFIX + this.toString();
        }         
                
        @Override
        public String toString()
        {
            switch (this)
            {
                case CREATE_CONTENT:
                    return LABEL_CREATE_CONTENT;
                case CREATE_FOLDER:
                    return LABEL_CREATE_FOLDER;                    
                case CREATE_VERSION:
                    return LABEL_CREATE_VERSION;
                case FTS_SEARCH:
                    return LABEL_FTS_SEARCH;
                case READ_CONTENT:
                    return LABEL_READ_CONTENT;
                case READ_PROPERTIES:
                    return LABEL_READ_PROPERTIES;
                default:
                    throw new RuntimeException("Benchmark type does not have toString conversion");
            }
        }
    }
    
    private BenchmarkType getNextBenchmarkType()
    {
        BenchmarkType result = null;
        
        // TODO may want to sum and scale the weights to ensure they add up to 100
        int randValue = BenchmarkUtils.rand.nextInt(100);
        int runningValue = 0;
        for (Map.Entry<BenchmarkType, Integer> entry : this.usageProfile.entrySet())
        {
            runningValue += entry.getValue().intValue();
            if (randValue < runningValue)
            {
                result = entry.getKey();
                break;
            }
        }
        
        return result;
    }
    
    @Override
    public void prepare(TestCase testCase)
    {              
        // Set the usage profile for this test case
        this.usageProfile = new HashMap<BenchmarkType, Integer>();
        for (BenchmarkType benchmarkType : BenchmarkType.values())
        {
            String paramName = benchmarkType.getParamName();
            if (testCase.hasParam(paramName) == true)
            {
                int value = testCase.getIntParam(paramName); 
                this.usageProfile.put(benchmarkType, Integer.valueOf(value));
            }
        }        
        
        // Set the location for the data files
        testCase.setParam(
                "alfresco.outputFile",
                BenchmarkUtils.getOutputFileLocation(testCase));
    }
    
    @Override
    public void warmup(TestCase tc)
    {
        // Do nothing!!
    }
    
    @Override
    public void run(final TestCase tc)
    {
        if (this instanceof UnitsOfWork)
        {
            try
            {
                BenchmarkType benchmarkType = getNextBenchmarkType();
                
                if (benchmarkType == null)
                {
                    throw new RuntimeException("Unable to determine the next benchmark type.  Has a usage policy been specified in the test case config?");
                }
                
                // Reset content size and mimetype
//                tc.setParam(PARAM_CONTENT_SIZE, "");
//                tc.setParam(PARAM_CONTENT_MIMETYPE, "");
                
                tc.setParam(PARAM_ALFRESCO_BENCHAMRK_TYPE, benchmarkType.toString());
                switch (benchmarkType)
                {            
                    case CREATE_CONTENT:
                    {
                        ((UnitsOfWork)this).doCreateContentBenchmark(tc);
                        break;
                    }
                    case READ_CONTENT:
                    {
                        ((UnitsOfWork)this).doReadContentBenchmark(tc);
                        break;
                    }
                    case CREATE_FOLDER:
                    {
                        ((UnitsOfWork)this).doCreateFolder(tc);
                        break;
                    }
                    case CREATE_VERSION:
                    {
                        ((UnitsOfWork)this).doCreateVersion(tc);
                        break;
                    }
                    case READ_PROPERTIES:
                    {
                        ((UnitsOfWork)this).doReadProperties(tc);
                        break;
                    }
                }
            }
            catch (Throwable exception)
            {
                exception.printStackTrace();
                throw new RuntimeException(exception.getMessage(), exception);
            }
        }
        
        // Otherwise do nothing, leaving it up to the implementation of the driver to sort it out
    }
    
    @Override
    public void finish(TestCase testCase)
    {
        ReportFactory.generate(testCase);
    }
}
