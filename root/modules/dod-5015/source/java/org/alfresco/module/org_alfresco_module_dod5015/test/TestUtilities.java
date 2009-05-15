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
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.view.ImporterBinding;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.service.namespace.QName;

/**
 * This class is an initial placeholder for miscellaneous helper methods used in
 * the testing or test initialisation of the DOD5015 module.
 * 
 * @author neilm
 */
public class TestUtilities
{
    protected static StoreRef SPACES_STORE = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
    
    /**
     * Loads a set of rma:dispositionAction objects into the repository as children
     * of the given node.
     * 
     * @param parent Parent node for newly created actions
     * @param nodeService NodeService to use to create nodes
     */
    public static void loadDispositionActions(NodeRef parent, NodeService nodeService)
    {
        // TODO: Replace these with actions actually required for the demo
        
        // destroy immediately
        createDispositionAction(parent, "Destroy Immediately", "destroy", "immediately|0", nodeService);
        
        // destroy at end of quarter
        createDispositionAction(parent, "Destroy At End Of Quarter", "destroy", "quarterend|1", nodeService);
        
        // transfer immediately
        createDispositionAction(parent, "Transfer Immediately", "transfer", "immediately|0", nodeService);
        
        // review at end of financial year
        createDispositionAction(parent, "Review At End of Financial Year", "review", "fyend|1", nodeService);
        
        // review every 2nd month
        createDispositionAction(parent, "Review Every Other Month", "review", "monthend|2", nodeService);
    }
    
    public static NodeRef loadFilePlanData(String siteName, NodeService nodeService,
            ImporterService importerService)
    {
        NodeRef filePlan = null;

        // If no siteName is provided create a filePlan in a well known location
        if (siteName == null)
        {
            // For now creating the filePlan beneth the
            NodeRef rootNode = nodeService.getRootNode(SPACES_STORE);
            filePlan = nodeService.createNode(rootNode, ContentModel.ASSOC_CHILDREN,
                    QName.createQName(RecordsManagementModel.RM_URI, "filePlan"),
                    RecordsManagementModel.TYPE_FILE_PLAN).getChildRef();
        } else
        {
            // Find the file plan in the site provided
            // TODO
        }

        // Do the data load into the the provided filePlan node reference
        // TODO ...
        InputStream is = TestUtilities.class.getClassLoader().getResourceAsStream(
                "alfresco/module/org_alfresco_module_dod5015/bootstrap/DODExampleFilePlan.xml");
        Assert.assertNotNull("The DODExampleFilePlan.xml import file could not be found", is);
        Reader viewReader = new InputStreamReader(is);
        Location location = new Location(filePlan);
        importerService.importView(viewReader, location, REPLACE_BINDING, null);

        return filePlan;
    }

    protected static void createDispositionAction(NodeRef parent, String name, String action, 
                String period, NodeService nodeService)
    {
        NodeRef node = nodeService.createNode(parent, ContentModel.ASSOC_CONTAINS, 
                    QName.createQName(RecordsManagementModel.RM_URI, QName.createValidLocalName(name)),
                    RecordsManagementModel.TYPE_DISPOSITION_ACTION).getChildRef();
        
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(4);
        props.put(ContentModel.PROP_NAME, name);
        props.put(RecordsManagementModel.PROP_DISPOSITION_ACTION, action);
        props.put(RecordsManagementModel.PROP_DISPOSITION_PERIOD, period);
        
        nodeService.addProperties(node, props);
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
