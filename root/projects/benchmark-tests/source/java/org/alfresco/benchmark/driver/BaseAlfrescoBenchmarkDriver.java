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

import org.alfresco.benchmark.util.AlfrescoUtils;
import org.alfresco.benchmark.util.RandUtils;

import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;

/**
 * @author Roy Wetherall
 */
public abstract class BaseAlfrescoBenchmarkDriver extends JapexDriverBase
{        
    public static final String TC_PARAM_ALFRESCO_BENCHAMRK_TYPE = "alfresco.bechmarkType";
    
    private Map<BenchmarkType, Integer> benchmarkTypeWeights;
    
    protected enum BenchmarkType
    {
        CREATE_CONTENT,
        READ_CONTENT
        //SEARCH
        //CHECK
        
        // ... and so on ...
    }
    
    public BaseAlfrescoBenchmarkDriver()
    {
        // TODO place these weights in a config file so that they can be modified easily
        
        // Initialise benchmark type weights
        this.benchmarkTypeWeights = new HashMap<BenchmarkType, Integer>();
        this.benchmarkTypeWeights.put(BenchmarkType.CREATE_CONTENT, 20);
        this.benchmarkTypeWeights.put(BenchmarkType.READ_CONTENT, 80);
    }
    
    private BenchmarkType getNextBenchmarkType()
    {
        BenchmarkType result = null;
        
        // TODO may want to sum and scale the weight to ensure they add up to 100
        int randValue = RandUtils.rand.nextInt(100);
        int runningValue = 0;
        for (Map.Entry<BenchmarkType, Integer> entry : this.benchmarkTypeWeights.entrySet())
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
    public void prepare(TestCase tc)
    {          
        // Set the location for the data files
        tc.setParam(
                "alfresco.outputFile",
                AlfrescoUtils.getOutputFileLocation(tc));
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
            tc.setParam(TC_PARAM_ALFRESCO_BENCHAMRK_TYPE, benchmarkType.toString());
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
            }
        }
        catch (Throwable exception)
        {
            exception.printStackTrace();
        }        
    }
    
    protected abstract void doCreateContentBenchmark(TestCase tc);
    
    protected abstract void doReadContentBenchmark(TestCase tc);
    
    //protected abstract void doSearchBenchmark();
}
