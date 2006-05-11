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
package org.alfresco.benchmark.framework;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.alfresco.benchmark.framework.dataprovider.DataProviderComponent;
import org.doomdark.uuid.UUIDGenerator;

import com.sun.japex.TestCase;

/**
 * @author Roy Wetherall
 */
public class BenchmarkUtils
{    
    // Make repeatable
    public static final Random rand = new Random(0);
 
    private static boolean propertiesLoaded = false;
    
    private static Map<String, String> testCaseOutputLocation = new HashMap<String, String>(5);
    
    private static String[] dataContentLocations;
    
    private static String outputFolderLocation;
    
    /**
     * Gets a random number from a normal distribution where the numbers center arouns the 'mean' interger
     * and the 90% of the generated numbers will fall within the range [mean-(2*variation)<=x<=mean+(2*variation)] and 70%
     * will fall in within the range [mean-variation<=x<=mean+variation].
     * 
     * Note: all negative numbers will returned as 0, please ensure that you take this into account when specifying the 
     *       variation value
     *  
     * @param mean          the mean
     * @param variation     the variation
     * @return              the generated number
     */
    public static int nextGaussianInteger(int mean, int variation)
    {
        double number = BenchmarkUtils.rand.nextGaussian();        
        int value = (int)Math.round((number*variation)+mean);
        if (value < 0)
        {
            value = 0;
        }
        return value;
    }
    
    public static synchronized String getOutputFileLocation()
    {
        return outputFolderLocation;
    }
    
    public static synchronized String getOutputFileLocation(TestCase testCase)
    {
        String location = testCaseOutputLocation.get(testCase.getName());
        if (location == null)
        {
            loadProperties();
            location = outputFolderLocation + File.separator + "testCase_" + testCase.getName() + "_" + System.currentTimeMillis() + ".csv";
            testCaseOutputLocation.put(testCase.getName(), location);
        }
        return location;
    }
    
    public static synchronized String getGUID()
    {
        return UUIDGenerator.getInstance().generateTimeBasedUUID().toString();       
    }
    
    public static String[] getDataContentLocations()
    {
        loadProperties();
        return dataContentLocations;
    }
    
    private static void loadProperties()
    {
        if (propertiesLoaded == false)
        {
            try
            {
                Properties props = new Properties();
                InputStream is = DataProviderComponent.class.getClassLoader().getResourceAsStream("benchmark-config.properties");
                props.load(is);
                
                dataContentLocations = props.getProperty("benchmark.data_content_location").split(";");
                outputFolderLocation = props.getProperty("benchmark.output_folder");
            }
            catch (IOException exception)
            {
                throw new RuntimeException("Unable to load benckmark config file.");
            }
            propertiesLoaded = true;
        }
    }
    
    public static final int BUFFER_SIZE = 4096;
    
    public static int copy(InputStream in, OutputStream out) throws IOException 
    {
        try {
            int byteCount = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            out.flush();
            return byteCount;
        }
        finally 
        {
            try 
            {
                in.close();
            }
            catch (IOException ex) 
            {
                
            }
            try 
            {
                out.close();
            }
            catch (IOException ex) 
            {
                
            }
        }
    }
}
