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
package org.alfresco.benchmark.framework.dataprovider;

import java.util.ArrayList;



public class RepositoryProfile
{
    private static final String PROFILE_DELIMETER = ",";
    
    private String profileString;
    
    private ArrayList<RespoitoryProfileDetail> details;
    private ArrayList<RespoitoryProfileDetail> containsFolders;
    private ArrayList<RespoitoryProfileDetail> containsDocuments;
    
    
    public RepositoryProfile(String profileString)
    {
        this.profileString = profileString;
        this.details = new ArrayList<RespoitoryProfileDetail>();
        this.containsFolders = new ArrayList<RespoitoryProfileDetail>();
        this.containsDocuments = new ArrayList<RespoitoryProfileDetail>();
        
        String[] values = profileString.split(RepositoryProfile.PROFILE_DELIMETER);  
        int depth = 1;
        for (int i = 0; i < values.length; i=i+2)
        {
            if (i+1 >= values.length)
            {
                throw new RuntimeException("An invalid repository profile has been provided ('" + profileString + "').  Check that there is a folder and file count for each repository depth.");
            }
            
            String folderCountValue = values[i].trim();
            if (folderCountValue.length() == 0)
            {
                throw new RuntimeException("No folder count value specified in profile string .'" + profileString + "'");
            }
            int folderCount = Integer.parseInt(folderCountValue);
            
            String fileCountValue = values[i+1].trim();
            if (fileCountValue.length() == 0)
            {
                throw new RuntimeException("No file count value specified in profile string .'" + profileString + "'");
            }
            int fileCount = Integer.parseInt(fileCountValue);
            
            RespoitoryProfileDetail detail = new RespoitoryProfileDetail(depth, folderCount, fileCount); 
            this.details.add(detail);
            if (folderCount != 0)
            {
                this.containsFolders.add(detail);
            }
            if (fileCount != 0)
            {
                this.containsDocuments.add(detail);
            }
            
            depth++;
        }
        
    }
    
    public ArrayList<RespoitoryProfileDetail> getDetails()
    {
        return details;
    }
    
    public ArrayList<RespoitoryProfileDetail> getContainsDocuments()
    {
        return containsDocuments;
    }
    
    public ArrayList<RespoitoryProfileDetail> getContainsFolders()
    {
        return containsFolders;
    }
    
    public String getProfileString()
    {
        return profileString;
    }
    
    public class RespoitoryProfileDetail
    {
        private int depth;
        private int folderCount;
        private int fileCount;
        
        public RespoitoryProfileDetail(int depth, int folderCount, int fileCount)
        {
            this.depth = depth;
            this.folderCount = folderCount;
            this.fileCount = fileCount;
        }
        
        public int getDepth()
        {
            return depth;
        }
        
        public int getFolderCount()
        {
            return folderCount;
        }
        
        public int getFileCount()
        {
            return fileCount;
        }
    }
}