/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.benchmark.driver;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.benchmark.dataprovider.RepositoryProfile;
import org.alfresco.benchmark.util.AlfrescoUtils;
import org.alfresco.benchmark.util.RandUtils;

import org.alfresco.benchmark.report.AlfrescoReport;
import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;

/**
 * @author Roy Wetherall
 */
public abstract class BaseAlfrescoBenchmarkDriver extends JapexDriverBase
{        
    public static final String PARAM_ALFRESCO_BENCHAMRK_TYPE = "alfresco.bechmarkType";    
    public static final String PARAM_CONTENT_SIZE = "alfresco.contentSize";
    public static final String PARAM_CONTENT_MIMETYPE = "alfresco.contentMimetype";
    public static final String PARAM_USER_NAME = "alfresco.userName";
    public static final String PARAM_NUMBER_OF_AVAILABLE_USERS  = "alfresco.numberOfAvailableUsers";
    
    public static final int DEFAULT_NUMBER_OF_AVAILABLE_USERS = 50; 
    
    protected RepositoryProfile repositoryProfile;
    
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
        int randValue = RandUtils.rand.nextInt(100);
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
        // Set the repository profile
        this.repositoryProfile = RepositoryProfile.createRespoitoryProfile(testCase);
        
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
                AlfrescoUtils.getOutputFileLocation(testCase));
    }
    
    @Override
    public void warmup(TestCase tc)
    {
        // Do nothing!!
    }
    
    @Override
    public void run(final TestCase tc)
    {
        try
        {
            BenchmarkType benchmarkType = getNextBenchmarkType();
            
            if (benchmarkType == null)
            {
                throw new RuntimeException("Unable to determine the next benchmark type.  Has a usage policy been specified in the test case config?");
            }
            
            // Reset content size and mimetype
            tc.setParam(PARAM_CONTENT_SIZE, "");
            tc.setParam(PARAM_CONTENT_MIMETYPE, "");
            
            tc.setParam(PARAM_ALFRESCO_BENCHAMRK_TYPE, benchmarkType.toString());
            switch (benchmarkType)
            {            
                case CREATE_CONTENT:
                {
                    doCreateContentBenchmark(tc);
                    break;
                }
                case READ_CONTENT:
                {
                    doReadContentBenchmark(tc);
                    break;
                }
                case CREATE_FOLDER:
                {
                    doCreateFolder(tc);
                    break;
                }
                case CREATE_VERSION:
                {
                    doCreateVersion(tc);
                    break;
                }
                case READ_PROPERTIES:
                {
                    doReadProperties(tc);
                    break;
                }
            }
        }
        catch (Throwable exception)
        {
            exception.printStackTrace();
        }        
    }
    
    @Override
    public void finish(TestCase testCase)
    {
        AlfrescoReport.generate(testCase);
    }
    
    protected abstract void doCreateContentBenchmark(TestCase tc);
    
    protected abstract void doReadContentBenchmark(TestCase tc);
    
    protected abstract void doCreateFolder(TestCase tc);
    
    protected abstract void doCreateVersion(TestCase tc);
    
    protected abstract void doReadProperties(TestCase tc);
}
