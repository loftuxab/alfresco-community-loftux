/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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