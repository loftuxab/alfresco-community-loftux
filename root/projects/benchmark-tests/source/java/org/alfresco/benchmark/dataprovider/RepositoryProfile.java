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
package org.alfresco.benchmark.dataprovider;

public class RepositoryProfile
{
    private int averageFolderDepth = 3;
    private int folderDepthVariation = 1;
    
    private int averageNumberOfDocumentsInFolder = 5;
    private int numberOfDocumentsInFolderVariation = 1;
    
    private int averageNumberOfSubFolders = 3;
    private int numberOfSubFoldersVariation = 1;
    
    public int getAverageFolderDepth()
    {
        return averageFolderDepth;
    }
    
    public void setAverageFolderDepth(int averageFolderDepth)
    {
        this.averageFolderDepth = averageFolderDepth;
    }
    
    public int getFolderDepthVariation()
    {
        return folderDepthVariation;
    }
    
    public void setFolderDepthVariation(int folderDepthVariation)
    {
        this.folderDepthVariation = folderDepthVariation;
    }
    
    public int getAverageNumberOfDocumentsInFolder()
    {
        return averageNumberOfDocumentsInFolder;
    }
    
    public void setAverageNumberOfDocumentsInFolder(int averageFolderSize)
    {
        this.averageNumberOfDocumentsInFolder = averageFolderSize;
    }
    
    public int getNumberOfDocumentsInFolderVariation()
    {
        return numberOfDocumentsInFolderVariation;
    }
    
    public void setNumberOfDocumentsInFolderVariation(int folderSizeVariation)
    {
        this.numberOfDocumentsInFolderVariation = folderSizeVariation;
    }
    
    public int getAverageNumberOfSubFolders()
    {
        return averageNumberOfSubFolders;
    }
    
    public void setAverageNumberOfSubFolders(int averageNumberOfSubFolders)
    {
        this.averageNumberOfSubFolders = averageNumberOfSubFolders;
    }
    
    public int getNumberOfSubFoldersVariation()
    {
        return numberOfSubFoldersVariation;
    }
    
    public void setNumberOfSubFoldersVariation(int numberOfSubFoldersVariation)
    {
        this.numberOfSubFoldersVariation = numberOfSubFoldersVariation;
    }
}