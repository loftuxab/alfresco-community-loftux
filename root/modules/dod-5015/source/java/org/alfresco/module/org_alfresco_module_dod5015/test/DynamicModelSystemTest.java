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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015.test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.transaction.UserTransaction;

import org.alfresco.module.org_alfresco_module_dod5015.CustomAssociation;
import org.alfresco.module.org_alfresco_module_dod5015.CustomProperty;
import org.alfresco.module.org_alfresco_module_dod5015.DOD5015Model;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminServiceImpl;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.BaseSpringTest;

public class DynamicModelSystemTest extends BaseSpringTest implements DOD5015Model
{
    public static final String RMC_CUSTOM_MODEL = "{http://www.alfresco.org/model/rmcustom/1.0}rmcustom";

    private NodeRef filePlan;

    private ImporterService importService;
    private NodeService nodeService;
    private RecordsManagementAdminService rmAdminService;
    private RecordsManagementActionService rmActionService;
    private ServiceRegistry serviceRegistry;
    private TransactionService transactionService;

    private PermissionService permissionService;
    
    @Override
    protected void onSetUpInTransaction() throws Exception 
    {
        super.onSetUpInTransaction();

        this.importService = (ImporterService)this.applicationContext.getBean("importerComponent");
        this.nodeService = (NodeService)this.applicationContext.getBean("NodeService"); // use upper 'N'odeService (to test access config interceptor)
        this.rmAdminService = (RecordsManagementAdminService)this.applicationContext.getBean("RecordsManagementAdminService");
        this.rmActionService = (RecordsManagementActionService)this.applicationContext.getBean("RecordsManagementActionService");
        this.serviceRegistry = (ServiceRegistry)this.applicationContext.getBean("ServiceRegistry");
        this.transactionService = (TransactionService)this.applicationContext.getBean("TransactionService");
        this.permissionService = (PermissionService)this.applicationContext.getBean("PermissionService");
        
        // Set the current security context as admin
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        
        // Get the test data
        setUpTestData();
    }
    
    private void setUpTestData()
    {
        filePlan = TestUtilities.loadFilePlanData(null, this.nodeService, this.importService, permissionService);
    }

    @Override
    protected void onTearDownInTransaction() throws Exception
    {
        UserTransaction txn = transactionService.getUserTransaction(false);
        txn.begin();
        this.nodeService.deleteNode(filePlan);
        txn.commit();
    }
    
    public void testCreateCustomAssociations() throws Exception
    {
        Set<QName> availableCustomAssocs = this.rmAdminService.getAvailableCustomAssociations().keySet();
        final int initialCustomAssocCount = availableCustomAssocs.size();
        
        String childAssocName = "dynamicAssocChild" + System.currentTimeMillis();
        
        HashMap<String, Serializable> actionParams = new HashMap<String, Serializable>();
        actionParams.put("name", childAssocName);
        actionParams.put("sourceRoleName", "superseding");
        actionParams.put("targetRoleName", "superseded");
        actionParams.put("isChild", Boolean.TRUE);
        this.rmActionService.executeRecordsManagementAction(RecordsManagementAdminServiceImpl.RM_CUSTOM_MODEL_NODE_REF, 
                "defineCustomAssociation", actionParams);

        String assocName = "dynamicAssocStandard" + System.currentTimeMillis();
        actionParams.clear();
        actionParams.put("name", assocName);
        actionParams.put("sourceRoleName", "supporting");
        actionParams.put("targetRoleName", "supported");
        actionParams.put("isChild", Boolean.FALSE);
        this.rmActionService.executeRecordsManagementAction(RecordsManagementAdminServiceImpl.RM_CUSTOM_MODEL_NODE_REF, 
                "defineCustomAssociation", actionParams);
        
        Map<QName, CustomAssociation> updatedCustomAssocs = rmAdminService.getAvailableCustomAssociations();
        final int updatedCount = updatedCustomAssocs.size();
        assertEquals("Incorrect custom assoc count.", initialCustomAssocCount + 2, updatedCount);
        
        //TODO Need to cleanly separate parent/child from bi-di assocs in the service API.

        QName assocQName = QName.createQName(RecordsManagementAdminServiceImpl.CUSTOM_MODEL_PREFIX,
                childAssocName, serviceRegistry.getNamespaceService());

        assertTrue("Custom assoc missing", updatedCustomAssocs.containsKey(assocQName));
        assertEquals("rma:record", updatedCustomAssocs.get(assocQName).getTargetClassName());
        assertEquals("rmc:superseding", updatedCustomAssocs.get(assocQName).getSourceRoleName());
    }

    public void testCreateCustomProperties() throws Exception
    {
        Set<QName> availableCustomProps = this.rmAdminService.getAvailableCustomProperties().keySet();
        final int initialCustomPropCount = availableCustomProps.size();
        
        String propLocalName = "dynamicProperty" + System.currentTimeMillis();
        
        Map<String, Serializable> actionParams = new HashMap<String, Serializable>();
        actionParams.put("name", propLocalName);
        actionParams.put("type", DataTypeDefinition.BOOLEAN);
        this.rmActionService.executeRecordsManagementAction(RecordsManagementAdminServiceImpl.RM_CUSTOM_MODEL_NODE_REF, 
                "defineCustomProperty", actionParams);

        Map<QName, CustomProperty> updatedCustomProps = rmAdminService.getAvailableCustomProperties();

        final int updatedCount = updatedCustomProps.size();
        assertEquals("Incorrect custom property count.", initialCustomPropCount + 1, updatedCount);
        
        // I'm tolerating a dependency from test code to the custom model prefix here.
        QName propertyQName = QName.createQName(RecordsManagementAdminServiceImpl.CUSTOM_MODEL_PREFIX,
                                                propLocalName, serviceRegistry.getNamespaceService());
        assertTrue("Custom property missing", updatedCustomProps.containsKey(propertyQName));
        assertEquals(updatedCustomProps.get(propertyQName).getType(), DataTypeDefinition.BOOLEAN);
    }
}
