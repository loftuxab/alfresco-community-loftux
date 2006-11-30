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
package org.alfresco.benchmark.alfresco;

import junit.framework.TestCase;

import org.alfresco.benchmark.framework.DataLoaderComponent;
import org.alfresco.benchmark.framework.LoadedData;
import org.alfresco.benchmark.framework.dataprovider.RepositoryProfile;


/**
 * @author Roy Wetherall
 */
public class AlfrescoDataLoaderComponentImplTest extends TestCase
{
    public void testLoadData()
    {
        RepositoryProfile repositoryProfile = new RepositoryProfile("5,5,2,3,0,1");
        
        DataLoaderComponent dataLoaderComponent = (DataLoaderComponent)AlfrescoUtils.getApplicationContext().getBean("dataLoaderComponent");        
        LoadedData loadedData = dataLoaderComponent.loadData(repositoryProfile);
        
        System.out.println("Number of content objects created: " + loadedData.getContentCount());
        System.out.println("Number of folders created: " + loadedData.getFolderCount());
    }
    
}
