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
package org.alfresco.sample.webservice;

import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.ContentFormat;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.types.StoreEnum;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.Utils;
import org.alfresco.webservice.util.WebServiceFactory;

/**
 * @author Roy Wetherall
 */
public class SamplesBase
{
    /** Admin user name and password used to connect to the repository */
    protected static final String USERNAME = "admin";
    protected static final String PASSWORD = "admin";
    
    /** The store used throughout the samples */
    protected static final Store STORE = new Store(StoreEnum.workspace, "SpacesStore");
    
    protected static final Reference SAMPLE_FOLDER = new Reference(STORE, null, "/app:company_home/cm:sample_folder"); 
    
    protected static void createSampleData() throws Exception
    {
        try
        {
            // Check to see if the sample folder has already been created or not
            WebServiceFactory.getRepositoryService().get(new Predicate(new Reference[]{SAMPLE_FOLDER}, STORE, null));
        }
        catch (Exception exception)
        {
            // Create parent reference to company home
            ParentReference parentReference = new ParentReference(
                    STORE,
                    null, 
                    "/app:company_home",
                    Constants.ASSOC_CONTAINS, 
                    Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, "sample_folder"));

            // Create folder
            NamedValue[] properties = new NamedValue[]{Utils.createNamedValue(Constants.PROP_NAME, "Web Service Sample Folder")};
            CMLCreate create = new CMLCreate("1", parentReference, Constants.TYPE_FOLDER, properties);
            CML cml = new CML();
            cml.setCreate(new CMLCreate[]{create});
            UpdateResult[] results = WebServiceFactory.getRepositoryService().update(cml);                
            
            // Create parent reference to sample folder
            Reference sampleFolder = results[0].getDestination();
            ParentReference parentReference2 = new ParentReference(
                    STORE,
                    sampleFolder.getUuid(),
                    null,
                    Constants.ASSOC_CONTAINS, 
                    Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, "sample_content"));
            
            // Create content
            NamedValue[] properties2 = new NamedValue[]{Utils.createNamedValue(Constants.PROP_NAME, "SampleContent.txt")};
            CMLCreate create2 = new CMLCreate("1", parentReference2, Constants.TYPE_CONTENT, properties2);
            CML cml2 = new CML();
            cml2.setCreate(new CMLCreate[]{create2});
            UpdateResult[] results2 = WebServiceFactory.getRepositoryService().update(cml2);  
            
            // Set content
            ContentFormat format = new ContentFormat(Constants.MIMETYPE_TEXT_PLAIN, "UTF-8");
            byte[] content = "This is some test content provided by the Alfresco development team!".getBytes();
            WebServiceFactory.getContentService().write(results2[0].getDestination(), Constants.PROP_CONTENT, content, format);
            
        }
    }
}
