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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
