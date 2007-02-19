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
package org.alfresco.sample.webservice;

import org.alfresco.webservice.classification.ClassificationServiceSoapBindingStub;
import org.alfresco.webservice.repository.QueryResult;
import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.types.Category;
import org.alfresco.webservice.types.Classification;
import org.alfresco.webservice.types.Query;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.ISO9075;
import org.alfresco.webservice.util.WebServiceFactory;

/**
 * Web service sample 6
 * <p>
 * Example showing how content can be queried for using categories
 * 
 * @author Roy Wetherall
 */
public class Categories extends SamplesBase
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
            
            // Get the services
            RepositoryServiceSoapBindingStub repositoryService = WebServiceFactory.getRepositoryService();
            ClassificationServiceSoapBindingStub classificationService = WebServiceFactory.getClassificationService(); 
           
            // Get all the classifications
            Classification[] classifications = classificationService.getClassifications(STORE); 
            
            // Output some details
            System.out.println("All classifications:");
            for (Classification classification : classifications)
            {
                System.out.println(classification.getClassification());
                System.out.println("Classification = " + classification.getTitle() + "; Root category = " + classification.getRootCategory().getTitle());            
            }
            
            // Get the class definition for the classification we are interested in
            Classification classification = classifications[0];
            
            // Get the child categories 
            Category[] categories = null; 
            if (classifications.length > 0)
            { 
                categories = classificationService.getChildCategories(classifications[0].getRootCategory().getId()); 
                if (categories != null)
                {
                    // Output some details
                    System.out.println("The child categories of classification '" + classifications[0].getTitle() + "':");
                    for (Category category : categories)
                    {
                        System.out.println("Title = " + category.getTitle());
                    }
                }
                else
                {
                    System.out.println("No child categories found.");
                }
            } 
          
            // Now build a path query 
            StringBuilder pathQuery = new StringBuilder(128);
            
            //pathQuery.append("PATH:\"cm:generalclassifiable/cm:MyTestCategory/cm:One/member\"");
            
            // Encode the root category name
            String encodedRoot = ISO9075.encode(classification.getRootCategory().getTitle());
        
            // Build up the search path
            if (categories != null && categories.length != 0) 
            { 
                for (int i=0; i<categories.length; i++) 
                { 
                    if (pathQuery.length() != 0) 
                    { 
                        pathQuery.append("OR"); 
                    } 
                    
                    String encoded = ISO9075.encode(categories[i].getTitle());
                    pathQuery.append(" PATH:\"cm:generalclassifiable/cm:" + encodedRoot + "/cm:" + encoded + "/member\" "); 
                } 
            } 
            
            System.out.println("Query path: " + pathQuery.toString());
            
            // Call the repository service to do search based on category 
            Query query = new Query(Constants.QUERY_LANG_LUCENE, pathQuery.toString()); 
    
            // Execute the query 
            QueryResult queryResult = repositoryService.query(STORE, query, true); 
    
            System.out.println("Category query results:");
            Query1.outputResultSet(queryResult.getResultSet().getRows());      
        }
        finally
        {
            AuthenticationUtils.endSession();
        }
    }
}
