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
package org.alfresco.benchmark.dataloader;

import java.util.List;

import org.alfresco.benchmark.dataprovider.RepositoryProfile;

/**
 * @author Roy Wetherall
 */
public interface DataLoaderComponent
{
    /**
     * Load data into the repository.  A new folder is created into which the
     * new data is looaded.  The respoitory profile is used to determine
     * the structure and size of the data loaded.
     * 
     * @param repositoryProfile     the repository profile
     * @return                      detais of the loaded data
     */
    public LoadedData loadData(RepositoryProfile repositoryProfile);
    
    /**
     * Create a number of test users
     * 
     * @param count     the number of users to create
     */
    public List<String> createUsers(int count);
}
