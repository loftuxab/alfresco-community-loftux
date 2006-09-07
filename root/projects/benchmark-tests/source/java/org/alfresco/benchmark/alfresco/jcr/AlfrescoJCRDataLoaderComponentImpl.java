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
package org.alfresco.benchmark.alfresco.jcr;

import javax.jcr.Repository;

import org.alfresco.benchmark.framework.DataLoaderComponent;
import org.alfresco.benchmark.framework.LoadedData;
import org.alfresco.benchmark.framework.dataprovider.RepositoryProfile;
import org.alfresco.benchmark.framework.jcr.JCRDataLoaderComponentImpl;

/**
 * @author Roy Wetherall
 */
public class AlfrescoJCRDataLoaderComponentImpl extends JCRDataLoaderComponentImpl
{
    protected Repository getRepository()
    {
        return AlfrescoJCRUtils.getRepository();
    }
    
    public static void main(String[] args)
    {
        String repositoryProfileValue = "3,5,3,5,0,5";
        if (args != null && args.length != 0 && args[0] != null)
        {            
            repositoryProfileValue = args[0];
        } 
        
        // Get data loader component
        DataLoaderComponent dataLoaderComponent = new AlfrescoJCRDataLoaderComponentImpl();
        
        // Load the data into the repo
        LoadedData loadedData = dataLoaderComponent.loadData(new RepositoryProfile(repositoryProfileValue));
        
        // Report the data loaded
        System.out.println("The data has been loaded into folder " + loadedData.getRootFolder() + " :");
        System.out.println("  - Folder count = " + loadedData.getFolderCount());
        System.out.println("  - Content count = " + loadedData.getContentCount());
        System.out.println("  - Total count = " + (loadedData.getFolderCount() + loadedData.getContentCount()));       
       
    }
}
