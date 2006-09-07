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
import org.alfresco.benchmark.framework.dataprovider.RepositoryProfile;
import org.doomdark.uuid.UUIDGenerator;

import com.sun.japex.TestCase;

/**
 * @author Roy Wetherall
 */
public class BenchmarkUtils
{    
    // Make repeatable
    public static final Random rand = new Random(0);
    
    public static final int BUFFER_SIZE = 4096;
 
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
            String filePath = testCase.getParam("alfresco.config-file-name");
            if (filePath == null)
            {
                filePath = "";
            }
            else
            {
                int index = filePath.lastIndexOf(File.separator);
                if (index == -1)
                {
                    index = filePath.lastIndexOf("/");
                }
                index ++;
                int end = filePath.indexOf(".");
                filePath = filePath.substring(index, end) + "_";
            }
            location = outputFolderLocation + File.separator + filePath + testCase.getName() + "_" + System.currentTimeMillis() + ".csv";
            testCaseOutputLocation.put(testCase.getName(), location);
        }
        return location;
    }
    
    /**
     * Generate a GUID
     * 
     * @return  a string GUID
     */
    public static synchronized String getGUID()
    {
        return UUIDGenerator.getInstance().generateTimeBasedUUID().toString();       
    }
    
    /**
     * Get the content data locations from the property file
     * @return
     */
    public static String[] getDataContentLocations()
    {
        loadProperties();
        return dataContentLocations;
    }
    
    /**
     * Load the property details from the property file
     */
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
        
    /**
     * Utility method to copy one stream to another
     * 
     * @param in
     * @param out
     * @return
     * @throws IOException
     */
    public static int copy(InputStream in, OutputStream out) throws IOException 
    {
        try 
        {
            int byteCount = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) 
            {
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
    
    /**
     * Get a file name based on its depth and index
     * 
     * @param depth
     * @param index
     * @return
     */
    public static String getFolderName(int depth, int index)
    {
        return "folder" + depth + "-" + index;
    }
    
    /**
     * Get a folder name based on its depth and index
     * 
     * @param depth
     * @param index
     * @return
     */
    public static String getFileName(int depth, int index)
    {
        return "file" + depth + "-" + index + ".bin";
    }
    
    public static String getRandomFolderPath(RepositoryProfile repositoryProfile, boolean includeAlfrescoNamespace)
    {
        if (repositoryProfile.getContainsFolders().size() == 0)
        {
            throw new RuntimeException("ERROR:  There are no folders available in the test data to build a random path for. (repository-profile=" + repositoryProfile.getProfileString() + ")");
        }
        
        int randPos = BenchmarkUtils.rand.nextInt(repositoryProfile.getContainsFolders().size());
        RepositoryProfile.RespoitoryProfileDetail detail = repositoryProfile.getContainsFolders().get(randPos);
        int atDepth = detail.getDepth(); 
        
        return getRandomFolderPathImpl(repositoryProfile, atDepth, includeAlfrescoNamespace);
    }
    
    public static String getRandomFolderPath(RepositoryProfile repositoryProfile, int atDepth, boolean includeAlfrescoNamespace)
    {
        return getRandomFolderPathImpl(repositoryProfile, atDepth, includeAlfrescoNamespace);
    }    
    
    private static String getRandomFolderPathImpl(RepositoryProfile repositoryProfile, int currentDepth, boolean includeAlfrescoNamespace)
    {
        RepositoryProfile.RespoitoryProfileDetail detail = null;
        String path = "";
        
        if (currentDepth > 0)
        {        
            int currentDepthIndex = currentDepth-1;
            
            detail = repositoryProfile.getDetails().get(currentDepthIndex);
            if (detail.getFolderCount() == 0)
            {
                throw new RuntimeException("ERROR:  Unable to build random folder path as profile indicates parent has no folders (repository-profile=" + repositoryProfile.getProfileString() + ")");
            }                   
            
            int folderCount = detail.getFolderCount();
            int index = BenchmarkUtils.rand.nextInt(folderCount);

            if (currentDepth > 1)
            {
                path = path + "/";            
            }
            path = path + getNamespace(includeAlfrescoNamespace) + BenchmarkUtils.getFolderName(currentDepth, index);       
            
            int newDepth = currentDepth-1;
            if (newDepth != 0)
            {
                path = getRandomFolderPath(repositoryProfile, newDepth, includeAlfrescoNamespace) + path;
            }
        }
        
        return path;
    }
    
    public static String getRandomFilePath(RepositoryProfile repositoryProfile, boolean includeAlfrescoNamespace)
    {
        return getRandomFilePath(repositoryProfile, 0, includeAlfrescoNamespace);
    }
    
    public static String getRandomFilePath(RepositoryProfile repositoryProfile, int atDepth, boolean includeAlfrescoNamespace)
    {
        RepositoryProfile.RespoitoryProfileDetail detail = null;
        String path = "";
        
        if (atDepth > 0)
        {   
            detail = repositoryProfile.getDetails().get(atDepth-1);
            if (detail.getFileCount() == 0)
            {
                throw new RuntimeException("ERROR:  The specified depth in the repository profile does not have any available files. (atDepth=" + atDepth + "; repository-profile=" + repositoryProfile.getProfileString() + ")");
            }
        }
        else
        {
            if (repositoryProfile.getContainsDocuments().size() == 0)
            {
                throw new RuntimeException("ERROR:  There are no file available in the test data to build a random path for. (repository-profile=" + repositoryProfile.getProfileString() + ")");
            }
            
            int randPos = BenchmarkUtils.rand.nextInt(repositoryProfile.getContainsDocuments().size());
            detail = repositoryProfile.getContainsDocuments().get(randPos);
            atDepth = detail.getDepth(); 
        }
        
        int fileCount = detail.getFileCount();
        int index = BenchmarkUtils.rand.nextInt(fileCount);
        int newDepth = atDepth -1;             
        
        path = getRandomFolderPathImpl(repositoryProfile, newDepth, includeAlfrescoNamespace);
        if (atDepth > 1)
        {
            path = path + "/";            
        }
        path = path + getNamespace(includeAlfrescoNamespace) + BenchmarkUtils.getFileName(atDepth, index);
        
        return path;
    } 
    
    private static String getNamespace(boolean includeNamespace)
    {
        if (includeNamespace == true)
        {
            return "cm:";
        }
        else
        {
            return "";
        }
    }
}
