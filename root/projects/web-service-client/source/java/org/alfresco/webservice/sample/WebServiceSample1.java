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
package org.alfresco.webservice.sample;

import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.WebServiceFactory;

/**
 * Web service sample 1.
 * <p>
 * Connect to the reposity and get a list of all the stores available in the repository.
 * 
 * @author Roy Wetherall
 */
public class WebServiceSample1 implements WebServiceSampleConfig
{
    /**
     * Connect to the respository and print out the names of the available 
     * 
     * @param args
     */
    public static void main(String[] args) 
        throws Exception
    {
        // Start the session
        AuthenticationUtils.startSession(USERNAME, PASSWORD);
        
        try
        {
            // Get the respoitory service
            RepositoryServiceSoapBindingStub repositoryService = WebServiceFactory.getRepositoryService();
            
            // Get array of stores available in the repository
            Store[] stores = repositoryService.getStores();
            if (stores == null)
            {
                // NOTE: empty array are returned as a null object, this is a issue with the generated web service code.
                System.out.println("There are no stores avilable in the repository.");
            }
            else
            {
                // Output the names of all the stores available in the repository
                System.out.println("The following stores are available in the repository:");
                for (Store store : stores)
                {
                    System.out.println(store.getAddress());
                }
            }
        }
        finally
        {
            // End the session
            AuthenticationUtils.endSession();
        }
    }       
}
