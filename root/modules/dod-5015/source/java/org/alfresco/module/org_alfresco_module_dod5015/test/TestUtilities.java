/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_dod5015.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.Assert;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.DOD5015Model;
import org.alfresco.module.org_alfresco_module_dod5015.capability.RMPermissionModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.view.ImporterBinding;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO9075;

/**
 * This class is an initial placeholder for miscellaneous helper methods used in
 * the testing or test initialisation of the DOD5015 module.
 * 
 * @author neilm
 */
public class TestUtilities implements DOD5015Model
{
    protected static StoreRef SPACES_STORE = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
    
    public static NodeRef loadFilePlanData(String siteName, NodeService nodeService,
            ImporterService importerService, PermissionService permissionService)
    {
        NodeRef filePlan = null;

        // If no siteName is provided create a filePlan in a well known location
        if (siteName == null)
        {
            // For now creating the filePlan beneath the
            NodeRef rootNode = nodeService.getRootNode(SPACES_STORE);
            filePlan = nodeService.createNode(rootNode, ContentModel.ASSOC_CHILDREN,
                    TYPE_FILE_PLAN,
                    TYPE_FILE_PLAN).getChildRef();
            permissionService.setPermission(filePlan, PermissionService.ALL_AUTHORITIES, RMPermissionModel.ROLE_USER, true);
        } else
        {
            // Find the file plan in the site provided
            // TODO
        }

        // Do the data load into the the provided filePlan node reference
        // TODO ...
        InputStream is = TestUtilities.class.getClassLoader().getResourceAsStream(
                "alfresco/module/org_alfresco_module_dod5015/bootstrap/DODExampleFilePlan.xml");
        //"alfresco/module/org_alfresco_module_dod5015/bootstrap/temp.xml");
        Assert.assertNotNull("The DODExampleFilePlan.xml import file could not be found", is);
        Reader viewReader = new InputStreamReader(is);
        Location location = new Location(filePlan);
        importerService.importView(viewReader, location, REPLACE_BINDING, null);

        return filePlan;
    }
    
    protected static NodeRef getRecordSeries(SearchService searchService, String seriesName)
    {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.addStore(SPACES_STORE);
        
        String query = "PATH:\"dod:filePlan/cm:" + ISO9075.encode(seriesName) + "\"";

        searchParameters.setQuery(query);
        searchParameters.setLanguage(SearchService.LANGUAGE_LUCENE);
        ResultSet rs = searchService.query(searchParameters);
        
        //setComplete();
        //endTransaction();
        return rs.getNodeRefs().isEmpty() ? null : rs.getNodeRef(0);
    }
    
    protected static NodeRef getRecordCategory(SearchService searchService, String seriesName, String categoryName)
    {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.addStore(SPACES_STORE);
        
        String query = "PATH:\"dod:filePlan/cm:" + ISO9075.encode(seriesName) + "/cm:" + ISO9075.encode(categoryName) + "\"";

        searchParameters.setQuery(query);
        searchParameters.setLanguage(SearchService.LANGUAGE_LUCENE);
        ResultSet rs = searchService.query(searchParameters);
        
        //setComplete();
        //endTransaction();
        return rs.getNodeRefs().isEmpty() ? null : rs.getNodeRef(0);
    }
    
    protected static NodeRef getRecordFolder(SearchService searchService, String seriesName, String categoryName, String folderName)
    {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.addStore(SPACES_STORE);
        String query = "PATH:\"dod:filePlan/cm:" + ISO9075.encode(seriesName)
            + "/cm:" + ISO9075.encode(categoryName)
            + "/cm:" + ISO9075.encode(folderName) + "\"";
        System.out.println("Query: " + query);
        searchParameters.setQuery(query);
        searchParameters.setLanguage(SearchService.LANGUAGE_LUCENE);
        ResultSet rs = searchService.query(searchParameters);
        
        //setComplete();
        //endTransaction();
        
        return rs.getNodeRefs().isEmpty() ? null : rs.getNodeRef(0);
    }

    
    // TODO .. do we need to redeclare this here ??
    private static ImporterBinding REPLACE_BINDING = new ImporterBinding()
    {

        public UUID_BINDING getUUIDBinding()
        {
            return UUID_BINDING.UPDATE_EXISTING;
        }

        public String getValue(String key)
        {
            return null;
        }

        public boolean allowReferenceWithinTransaction()
        {
            return false;
        }

        public QName[] getExcludedClasses()
        {
            return null;
        }

    };
}
