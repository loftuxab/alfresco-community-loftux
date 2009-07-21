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

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.CustomAssociation;
import org.alfresco.module.org_alfresco_module_dod5015.CustomProperty;
import org.alfresco.module.org_alfresco_module_dod5015.DOD5015Model;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminService;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminServiceImpl;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.module.org_alfresco_module_dod5015.action.impl.DefineCustomPropertyAction;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.BaseSpringTest;

public class DynamicModelTest extends BaseSpringTest implements DOD5015Model
{
    public static final String RMC_CUSTOM_MODEL = "{http://www.alfresco.org/model/rmcustom/1.0}rmcustom";

    private NodeRef filePlan;
    private NodeRef testRecord;

    private ImporterService importService;
    private DictionaryService dictionaryService;
    private NamespaceService namespaceService;
    private NodeService nodeService;
    private PermissionService permissionService;
    private RecordsManagementAdminService rmAdminService;
    private RecordsManagementActionService rmActionService;
    private ServiceRegistry serviceRegistry;
    private TransactionService transactionService;
    
    @Override
    protected void onSetUpInTransaction() throws Exception 
    {
        super.onSetUpInTransaction();

        this.dictionaryService = (DictionaryService)this.applicationContext.getBean("dictionaryService");
        this.importService = (ImporterService)this.applicationContext.getBean("importerComponent");
        this.namespaceService = (NamespaceService)this.applicationContext.getBean("namespaceService");
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
        
        NodeRef parentRecordCategory = TestUtilities.getRecordCategory(serviceRegistry.getSearchService(),
                "Reports", "Unit Manning Documents");
        NodeRef parentFolder = this.nodeService.createNode(parentRecordCategory,
                ContentModel.ASSOC_CONTAINS,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "Test folder " + System.currentTimeMillis()),
                TYPE_RECORD_FOLDER).getChildRef();
        testRecord = this.nodeService.createNode(parentFolder,
                ContentModel.ASSOC_CONTAINS,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
                        "Record" + System.currentTimeMillis() + ".txt"),
                ContentModel.TYPE_CONTENT).getChildRef();
        ContentWriter writer = serviceRegistry.getContentService().getWriter(testRecord, ContentModel.PROP_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        writer.setEncoding("UTF-8");
        writer.putContent("Irrelevant content");

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
        
        String childAssocName = "rmc:dynamicAssocChild" + System.currentTimeMillis();
        
        HashMap<String, Serializable> actionParams = new HashMap<String, Serializable>();
        actionParams.put("name", childAssocName);
        actionParams.put("sourceRoleName", "superseding");
        actionParams.put("targetRoleName", "superseded");
        actionParams.put("isChild", Boolean.TRUE);
        this.rmActionService.executeRecordsManagementAction(RecordsManagementAdminServiceImpl.RM_CUSTOM_MODEL_NODE_REF, 
                "defineCustomAssociation", actionParams);

        String assocName = "rmc:dynamicAssocStandard" + System.currentTimeMillis();
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
        
        QName assocQName = QName.createQName(childAssocName, namespaceService);

        assertTrue("Custom assoc missing", updatedCustomAssocs.containsKey(assocQName));
        assertEquals("rma:record", updatedCustomAssocs.get(assocQName).getTargetClassName());
        assertEquals("rmc:superseding", updatedCustomAssocs.get(assocQName).getSourceRoleName());
    }

    public void testCreateCustomProperties() throws Exception
    {
        Set<QName> availableCustomProps = this.rmAdminService.getAvailableCustomProperties().keySet();
        final int initialCustomPropCount = availableCustomProps.size();

        // Define a new custom property.
        String propLocalName = "rmc:dynamicProperty" + System.currentTimeMillis();
        Map<String, Serializable> actionParams = new HashMap<String, Serializable>();
        actionParams.put(DefineCustomPropertyAction.PARAM_NAME, propLocalName);
        actionParams.put(DefineCustomPropertyAction.PARAM_TYPE, DataTypeDefinition.BOOLEAN);
        actionParams.put(DefineCustomPropertyAction.PARAM_MANDATORY, Boolean.TRUE);

        // Submit an action to have it created.
        this.rmActionService.executeRecordsManagementAction(RecordsManagementAdminServiceImpl.RM_CUSTOM_MODEL_NODE_REF, 
                "defineCustomProperty", actionParams);

        // Retrieve the updated custom property set from the Admin Service.
        Map<QName, CustomProperty> updatedCustomProps = rmAdminService.getAvailableCustomProperties();

        // Ensure the new custom property is in there.
        final int updatedCount = updatedCustomProps.size();
        assertEquals("Incorrect custom property count.", initialCustomPropCount + 1, updatedCount);
        
        QName propertyQName = QName.createQName(propLocalName, namespaceService);
        assertTrue("Custom property missing", updatedCustomProps.containsKey(propertyQName));
        assertEquals(updatedCustomProps.get(propertyQName).getType(), DataTypeDefinition.BOOLEAN);
        
        // Now to actually use the custom property
        Map<QName, Serializable> p = new HashMap<QName, Serializable>();
        p.put(propertyQName, "Hello World.");
        this.nodeService.addAspect(testRecord, QName.createQName("rmc:customProperties", namespaceService), p);
        
        assertNotNull(nodeService.getProperty(testRecord, propertyQName));

        // Ensure the new custom property is returned by the dictionary service.
        // TODO If I reimplement the adminService to use the dictionaryService, I can delete this check.
        
//        QName customPropsAspectQName = QName.createQName(RecordsManagementAdminServiceImpl.RMC_CUSTOM_PROPS, namespaceService);
//        final AspectDefinition customPropsAspect = dictionaryService.getAspect(customPropsAspectQName);
//        assertNotNull("The custom property aspect is not returned by the dictionaryService.",
//                customPropsAspect);
//        final PropertyDefinition newPropDefn = customPropsAspect.getProperties().get(propertyQName);
//        //TODO The new prop is not coming back from the dictionary service.
//        assertNotNull("The new custom property is not returned by the dictionaryService.", newPropDefn);
//        assertEquals("New custom property had wrong name.", newPropDefn, newPropDefn.getName());
//        assertEquals("New custom property had wrong type.", DataTypeDefinition.BOOLEAN, newPropDefn.getDataType());
    }
}
