/*
 * Copyright (C) 2005 Alfresco, Inc.
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
package org.alfresco.sample.webservice;

import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLAddAspect;
import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.Utils;
import org.alfresco.webservice.util.WebServiceFactory;

/**
 * Web service sample 4
 * <p>
 * This sample shows how to construct and execute CML queries using the respository web service.
 * 
 * @author Roy Wetherall
 */
public class CMLUpdates extends SamplesBase
{
    /**
     * Main function
     */
    public static void main(String[] args)
        throws Exception
    {
        AuthenticationUtils.startSession(USERNAME, PASSWORD);
        try
        {        
            // Make sure smaple data has been created
            createSampleData();
            
            // Get the repository 
            RepositoryServiceSoapBindingStub repositoryService = WebServiceFactory.getRepositoryService();        
            
            // Create the CML structure
            // When executed this cml update query will create a new content node beneth the tutorial folder and the add the
            // versionable aspect to the newly created node
            ParentReference parentReference = new ParentReference(STORE, null, "/app:company_home/cm:sample_folder", Constants.ASSOC_CONTAINS, Constants.ASSOC_CONTAINS);            
            NamedValue[] properties = new NamedValue[]{Utils.createNamedValue(Constants.PROP_NAME, System.currentTimeMillis() + "_WebServiceSample4.txt")};
            CMLCreate create = new CMLCreate("id1", parentReference, null, null, null, Constants.TYPE_CONTENT, properties);        
            CMLAddAspect addAspect = new CMLAddAspect(Constants.ASPECT_VERSIONABLE, null, null, "id1");
            CML cml = new CML();
            cml.setCreate(new CMLCreate[]{create});
            cml.setAddAspect(new CMLAddAspect[]{addAspect});
            
            // Execute the update
            UpdateResult[] updateResults = repositoryService.update(cml);
                   
            for (UpdateResult updateResult : updateResults)
            {
                String sourceId = "none";
                Reference source = updateResult.getSource();
                if (source != null)
                {
                    sourceId = source.getUuid();
                }
                
                String destinationId = "none";
                Reference destination = updateResult.getDestination();
                if (destination != null)
                {
                    destinationId = destination.getUuid();
                }
                
                System.out.println(
                        "Command = " + updateResult.getStatement() + 
                        "; Source = " + sourceId +
                        "; Destination = " + destinationId);
            }
        }
        finally
        {
            // End the session
            AuthenticationUtils.endSession();
        }
    }    
}
