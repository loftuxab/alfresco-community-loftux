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
