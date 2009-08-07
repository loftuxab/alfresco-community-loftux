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
package org.alfresco.module.org_alfresco_module_dod5015.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.alfresco.i18n.I18NUtil;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.DOD5015Model;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.module.org_alfresco_module_dod5015.capability.Capability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.RMPermissionModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.DictionaryNamespaceComponent;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.NamespaceDAOImpl;
import org.alfresco.repo.domain.hibernate.HibernateL1CacheBulkLoader;
import org.alfresco.repo.node.BaseNodeServiceTest;
import org.alfresco.repo.search.QueryRegisterComponent;
import org.alfresco.repo.search.impl.lucene.AbstractLuceneIndexerAndSearcherFactory;
import org.alfresco.repo.search.impl.lucene.LuceneIndexerAndSearcher;
import org.alfresco.repo.search.impl.lucene.fts.FullTextSearchIndexer;
import org.alfresco.repo.search.impl.querymodel.QueryEngine;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.repo.security.permissions.impl.model.PermissionModel;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.view.ImporterBinding;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.service.cmr.view.ImporterBinding.UUID_BINDING;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author andyh
 */
public class CapabilitiesTest extends TestCase
{
    private static ApplicationContext ctx = ApplicationContextHelper.getApplicationContext();

    private NodeRef rootNodeRef;

    private NodeService nodeService;

    private TransactionService transactionService;

    private ImporterService importerService;

    private UserTransaction testTX;

    private NodeRef filePlan;

    private PermissionService permissionService;

    private RecordsManagementService recordsManagementService;
    
    private PermissionModel permissionModel;
    
    private ContentService contentService;

    private NodeRef recordSeries;

    private NodeRef recordCategory_1;

    private NodeRef recordCategory_2;

    private NodeRef recordFolder_1;

    private NodeRef recordFolder_2;

    private NodeRef record_1;

    /**
     * @param name
     */
    public CapabilitiesTest(String name)
    {
        super(name);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        nodeService = (NodeService) ctx.getBean("dbNodeService");
        transactionService = (TransactionService) ctx.getBean("transactionComponent");
        importerService = (ImporterService) ctx.getBean("ImporterService");
        permissionService = (PermissionService) ctx.getBean("permissionService");
        permissionModel = (PermissionModel) ctx.getBean("permissionsModelDAO");
        contentService = (ContentService)ctx.getBean("contentService");
        
        recordsManagementService = (RecordsManagementService)ctx.getBean("RecordsManagementService");

        testTX = transactionService.getUserTransaction();
        testTX.begin();
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());

        StoreRef storeRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        rootNodeRef = nodeService.getRootNode(storeRef);

        filePlan = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, DOD5015Model.TYPE_FILE_PLAN, DOD5015Model.TYPE_FILE_PLAN).getChildRef();
        recordSeries = createRecordSeries(filePlan, "RS", "RS-1", "Record Series", "My record series");
        recordCategory_1 = createRecordCategory(recordSeries, "Docs", "101-1", "Docs", "Docs", "week|1", true);
        recordCategory_2 = createRecordCategory(recordSeries, "More Docs", "101-2", "More Docs", "More Docs", "week|1", true);
        recordFolder_1 = createRecordFolder(recordCategory_1, "F1", "101-3", "title", "description",  "week|1", true);
        recordFolder_2 = createRecordFolder(recordCategory_2, "F2", "102-3", "title", "description",  "week|1", true);
        record_1 = createRecord(recordFolder_1);
     
        
        permissionService.setPermission(filePlan, "rm_user", RMPermissionModel.ROLE_USER, true);
        permissionService.setPermission(filePlan, "rm_power_user", RMPermissionModel.ROLE_POWER_USER, true);
        permissionService.setPermission(filePlan, "rm_security_officer", RMPermissionModel.ROLE_SECURITY_OFFICER, true);
        permissionService.setPermission(filePlan, "rm_records_manager", RMPermissionModel.ROLE_RECORDS_MANAGER, true);
        permissionService.setPermission(filePlan, "rm_administrator", RMPermissionModel.ROLE_ADMINISTRATOR, true);
        
        InputStream is = TestUtilities.class.getClassLoader().getResourceAsStream("alfresco/module/org_alfresco_module_dod5015/bootstrap/DODExampleFilePlan.xml");
        // "alfresco/module/org_alfresco_module_dod5015/bootstrap/temp.xml");
        Assert.assertNotNull("The DODExampleFilePlan.xml import file could not be found", is);
        Reader viewReader = new InputStreamReader(is);
        Location location = new Location(filePlan);
        importerService.importView(viewReader, location, REPLACE_BINDING, null);
        
        testTX.commit();
        testTX = transactionService.getUserTransaction();
        testTX.begin();
        
    }
    
    private NodeRef createRecord(NodeRef recordFolder)
    {
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
        props.put(ContentModel.PROP_NAME, "MyRecord.txt");
        NodeRef recordOne = this.nodeService.createNode(recordFolder, 
                                                        ContentModel.ASSOC_CONTAINS, 
                                                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "MyRecord.txt"), 
                                                        ContentModel.TYPE_CONTENT).getChildRef();
        
        // Set the content
        ContentWriter writer = this.contentService.getWriter(recordOne, ContentModel.PROP_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        writer.setEncoding("UTF-8");
        writer.putContent("There is some content in this record");
        return recordOne;
    }

    private NodeRef createRecordSeries(NodeRef filePlan, String name, String identifier, String title, String description)
    {
        HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(ContentModel.PROP_NAME, name);
        properties.put(DOD5015Model.PROP_IDENTIFIER, identifier);
        properties.put(ContentModel.PROP_TITLE, title);
        properties.put(ContentModel.PROP_DESCRIPTION, description);
        NodeRef answer = nodeService.createNode(filePlan, ContentModel.ASSOC_CONTAINS, DOD5015Model.TYPE_RECORD_SERIES, DOD5015Model.TYPE_RECORD_SERIES, properties).getChildRef();
        return answer;
    }
    
    private NodeRef createRecordCategory(NodeRef recordSeries, String name, String identifier, String title, String description, String review, boolean vital)
    {
        HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(ContentModel.PROP_NAME, name);
        properties.put(DOD5015Model.PROP_IDENTIFIER, identifier);
        properties.put(ContentModel.PROP_TITLE, title);
        properties.put(ContentModel.PROP_DESCRIPTION, description);
        properties.put(DOD5015Model.PROP_REVIEW_PERIOD, review);
        properties.put(DOD5015Model.PROP_VITAL_RECORD_INDICATOR, vital);
        NodeRef answer = nodeService.createNode(filePlan, ContentModel.ASSOC_CONTAINS, DOD5015Model.TYPE_RECORD_CATEGORY, DOD5015Model.TYPE_RECORD_CATEGORY, properties).getChildRef();
 
        properties = new HashMap<QName, Serializable>();
        properties.put(DOD5015Model.PROP_DISPOSITION_AUTHORITY, "N1-218-00-4 item 023");
        properties.put(DOD5015Model.PROP_DISPOSITION_INSTRUCTIONS, "Cut off monthly, hold 1 month, then destroy.");
        NodeRef ds = nodeService.createNode(answer, DOD5015Model.ASSOC_DISPOSITION_SCHEDULE, DOD5015Model.TYPE_DISPOSITION_SCHEDULE, DOD5015Model.TYPE_DISPOSITION_SCHEDULE, properties).getChildRef();
        
        createDispoistionAction(ds, "cutoff", "monthend|1", null);
        createDispoistionAction(ds, "destroy", "month|1", "{http://www.alfresco.org/model/recordsmanagement/1.0}cutOffDate");
        return answer;
    }
    
    private NodeRef createDispoistionAction(NodeRef disposition, String actionName, String period, String periodProperty)
    {
        HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(DOD5015Model.PROP_DISPOSITION_ACTION_NAME, actionName);
        properties.put(DOD5015Model.PROP_DISPOSITION_PERIOD, period);
        if(periodProperty != null)
        {
        properties.put(DOD5015Model.PROP_DISPOSITION_PERIOD_PROPERTY, periodProperty);
        }
        NodeRef answer = nodeService.createNode(disposition, DOD5015Model.ASSOC_DISPOSITION_ACTION_DEFINITIONS, DOD5015Model.TYPE_DISPOSITION_ACTION_DEFINITION, DOD5015Model.TYPE_DISPOSITION_ACTION_DEFINITION, properties).getChildRef();
        return answer;
    }
    
    private NodeRef createRecordFolder(NodeRef recordCategory, String name, String identifier, String title, String description, String review, boolean vital)
    {
        HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(ContentModel.PROP_NAME, name);
        properties.put(DOD5015Model.PROP_IDENTIFIER, identifier);
        properties.put(ContentModel.PROP_TITLE, title);
        properties.put(ContentModel.PROP_DESCRIPTION, description);
        properties.put(DOD5015Model.PROP_REVIEW_PERIOD, review);
        properties.put(DOD5015Model.PROP_VITAL_RECORD_INDICATOR, vital);
        NodeRef answer = nodeService.createNode(recordCategory, ContentModel.ASSOC_CONTAINS, DOD5015Model.TYPE_RECORD_FOLDER, DOD5015Model.TYPE_RECORD_FOLDER, properties).getChildRef();
        return answer;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {

        if (testTX.getStatus() == Status.STATUS_ACTIVE)
        {
            testTX.rollback();
        }
        AuthenticationUtil.clearCurrentSecurityContext();
        super.tearDown();
    }
    
    public void testPermissionsModel()
    {
        Set<PermissionReference> exposed = permissionModel.getExposedPermissions(RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT);
        assertEquals(5, exposed.size());
        assertTrue(exposed.contains(permissionModel.getPermissionReference(RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT, RMPermissionModel.ROLE_ADMINISTRATOR)));
        
        Set<PermissionReference> all = permissionModel.getAllPermissions(RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT);
        assertEquals(58*2+5, all.size());
        
        checkGranting(RMPermissionModel.ACCESS_AUDIT, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.ADD_MODIFY_EVENT_DATES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER, RMPermissionModel.ROLE_SECURITY_OFFICER, RMPermissionModel.ROLE_POWER_USER);
        checkGranting(RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CLOSE_FOLDERS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER, RMPermissionModel.ROLE_SECURITY_OFFICER, RMPermissionModel.ROLE_POWER_USER);
        checkGranting(RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER, RMPermissionModel.ROLE_SECURITY_OFFICER);
        checkGranting(RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER); 
        checkGranting(RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER, RMPermissionModel.ROLE_SECURITY_OFFICER, RMPermissionModel.ROLE_POWER_USER);
        checkGranting(RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CYCLE_VITAL_RECORDS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER, RMPermissionModel.ROLE_SECURITY_OFFICER, RMPermissionModel.ROLE_POWER_USER);
        checkGranting(RMPermissionModel.DECLARE_AUDIT_AS_RECORD, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.DECLARE_RECORDS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER, RMPermissionModel.ROLE_SECURITY_OFFICER, RMPermissionModel.ROLE_POWER_USER, RMPermissionModel.ROLE_USER);
        checkGranting(RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER, RMPermissionModel.ROLE_SECURITY_OFFICER, RMPermissionModel.ROLE_POWER_USER);
        checkGranting(RMPermissionModel.DELETE_AUDIT, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.DELETE_LINKS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.DELETE_RECORDS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.DESTROY_RECORDS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.DISPLAY_RIGHTS_REPORT, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.EDIT_NON_RECORD_METADATA, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER, RMPermissionModel.ROLE_SECURITY_OFFICER, RMPermissionModel.ROLE_POWER_USER);
        checkGranting(RMPermissionModel.EDIT_RECORD_METADATA, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER, RMPermissionModel.ROLE_SECURITY_OFFICER, RMPermissionModel.ROLE_POWER_USER);
        checkGranting(RMPermissionModel.EDIT_SELECTION_LISTS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.EXPORT_AUDIT, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        // File does not exists
        // checkGranting(RMPermissionModel.FILE_RECORDS, RMPermissionModel.ROLE_ADMINISTRATOR,
        // RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.MANAGE_ACCESS_CONTROLS, RMPermissionModel.ROLE_ADMINISTRATOR);
        checkGranting(RMPermissionModel.MANAGE_ACCESS_RIGHTS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.MAP_EMAIL_METADATA, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.MOVE_RECORDS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.PASSWORD_CONTROL, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.PLANNING_REVIEW_CYCLES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER, RMPermissionModel.ROLE_SECURITY_OFFICER, RMPermissionModel.ROLE_POWER_USER);
        checkGranting(RMPermissionModel.RE_OPEN_FOLDERS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER, RMPermissionModel.ROLE_SECURITY_OFFICER, RMPermissionModel.ROLE_POWER_USER);
        checkGranting(RMPermissionModel.SELECT_AUDIT_METADATA, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.TRIGGER_AN_EVENT, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.UNDECLARE_RECORDS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.UNFREEZE, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.UPDATE_CLASSIFICATION_DATES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER, RMPermissionModel.ROLE_SECURITY_OFFICER);
        checkGranting(RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER, RMPermissionModel.ROLE_SECURITY_OFFICER);
        checkGranting(RMPermissionModel.UPDATE_TRIGGER_DATES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER, RMPermissionModel.ROLE_SECURITY_OFFICER);
        checkGranting(RMPermissionModel.VIEW_RECORDS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER, RMPermissionModel.ROLE_SECURITY_OFFICER, RMPermissionModel.ROLE_POWER_USER, RMPermissionModel.ROLE_USER);
        checkGranting(RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        
    }
    
    private void checkGranting(String permission, String ... roles)
    {
        Set<PermissionReference> granting = permissionModel.getGrantingPermissions(permissionModel.getPermissionReference(RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT, permission));
        Set<PermissionReference> test = new HashSet<PermissionReference>();
        test.addAll(granting);
        Set<PermissionReference> nonRM = new HashSet<PermissionReference>();
        for(PermissionReference pr : granting)
        {
            if(!pr.getQName().equals(RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT))
            {
                nonRM.add(pr);
            }
        }
        test.removeAll(nonRM);
        assertEquals(roles.length + 1, test.size());
        for(String role : roles)
        {
            assertTrue(test.contains(permissionModel.getPermissionReference(RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT, role)));
        }
        
    }
    
    public void testConfig()
    {
        assertEquals(6, recordsManagementService.getProtectedAspects().size());
        assertEquals(13, recordsManagementService.getProtectedProperties().size());
        
        // Test action wire up
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.ACCESS_AUDIT).getActionNames().size());
        assertEquals(2, recordsManagementService.getCapability(RMPermissionModel.ADD_MODIFY_EVENT_DATES).getActionNames().size());
        assertEquals(1, recordsManagementService.getCapability(RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.AUTHORIZE_ALL_TRANSFERS).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.CHANGE_OR_DELETE_REFERENCES).getActionNames().size());
        assertEquals(1, recordsManagementService.getCapability(RMPermissionModel.CLOSE_FOLDERS).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES).getActionNames().size()); 
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS).getActionNames().size());
        assertEquals(2, recordsManagementService.getCapability(RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES).getActionNames().size());
        assertEquals(1, recordsManagementService.getCapability(RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS).getActionNames().size());
        assertEquals(1, recordsManagementService.getCapability(RMPermissionModel.CYCLE_VITAL_RECORDS).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.DECLARE_AUDIT_AS_RECORD).getActionNames().size());
        assertEquals(1, recordsManagementService.getCapability(RMPermissionModel.DECLARE_RECORDS).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.DELETE_AUDIT).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.DELETE_LINKS).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.DELETE_RECORDS).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.DESTROY_RECORDS).getActionNames().size());
        assertEquals(1, recordsManagementService.getCapability(RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.DISPLAY_RIGHTS_REPORT).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.EDIT_DECLARED_RECORD_METADATA).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.EDIT_NON_RECORD_METADATA).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.EDIT_RECORD_METADATA).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.EDIT_SELECTION_LISTS).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.EXPORT_AUDIT).getActionNames().size());
        assertEquals(1, recordsManagementService.getCapability(RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE).getActionNames().size());
        assertEquals(1, recordsManagementService.getCapability(RMPermissionModel.FILE_RECORDS).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.MANAGE_ACCESS_CONTROLS).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.MANAGE_ACCESS_RIGHTS).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.MAP_EMAIL_METADATA).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.MOVE_RECORDS).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.PASSWORD_CONTROL).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.PLANNING_REVIEW_CYCLES).getActionNames().size());
        assertEquals(1, recordsManagementService.getCapability(RMPermissionModel.RE_OPEN_FOLDERS).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.SELECT_AUDIT_METADATA).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.TRIGGER_AN_EVENT).getActionNames().size());
        assertEquals(1, recordsManagementService.getCapability(RMPermissionModel.UNDECLARE_RECORDS).getActionNames().size());
        assertEquals(2, recordsManagementService.getCapability(RMPermissionModel.UNFREEZE).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.UPDATE_CLASSIFICATION_DATES).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.UPDATE_TRIGGER_DATES).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.VIEW_RECORDS).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE).getActionNames().size());
        
    }
    
    public void testFilePlanAsSystem()
    {
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(filePlan);
        assertEquals(59, access.size());
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.ALLOWED);
        
    }
    
    public void testFilePlanAsAdministrator()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_administrator");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(filePlan);
        assertEquals(59, access.size());
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.ALLOWED);
    }
    
    
    public void testFilePlanAsRecordsManager()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_records_manager");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(filePlan);
        assertEquals(59, access.size()); // 58 + File
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.ALLOWED);
        
    }
    public void testFilePlanAsSecurityOfficer()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_security_officer");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(filePlan);
        assertEquals(59, access.size()); // 58 + File
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.DENIED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.DENIED);
    }
    
    public void testFilePlanAsPowerUser()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_power_user");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(filePlan);
        assertEquals(59, access.size()); // 58 + File
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.DENIED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.DENIED);
    }
    
    public void testFilePlanAsUser()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_user");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(filePlan);
        assertEquals(59, access.size()); // 58 + File
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.DENIED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.DENIED);
    }
    
    public void testRecordSeriesAsSystem()
    {
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(recordSeries);
        assertEquals(59, access.size());
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.ALLOWED);
        
    }
    
    public void testRecordSeriesAsAdministrator()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_administrator");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(recordSeries);
        assertEquals(59, access.size());
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.ALLOWED);
    }
    
    public void testRecordSeriesAsRecordsManager()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_records_manager");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(recordSeries);
        assertEquals(59, access.size()); // 58 + File
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.ALLOWED);
        
    }
    public void testRecordSeriesAsSecurityOfficer()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_security_officer");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(recordSeries);
        assertEquals(59, access.size()); // 58 + File
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.DENIED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.DENIED);
    }
    
    public void testRecordSeriesAsPowerUser()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_power_user");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(recordSeries);
        assertEquals(59, access.size()); // 58 + File
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.DENIED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.DENIED);
    }
    
    public void testRecordSeriesAsUser()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_user");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(recordSeries);
        assertEquals(59, access.size()); // 58 + File
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.DENIED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.DENIED);
    }
    
    public void testRecordCategoryAsSystem()
    {
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(recordCategory_1);
        assertEquals(59, access.size());
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.ALLOWED);
        
    }
    
    public void testRecordCategoryAsAdministrator()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_administrator");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(recordCategory_1);
        assertEquals(59, access.size());
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.ALLOWED);
    }
    
    public void testRecordCategoryAsRecordsManager()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_records_manager");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(recordCategory_1);
        assertEquals(59, access.size()); // 58 + File
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.ALLOWED);
        
    }
    public void testRecordCategoryAsSecurityOfficer()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_security_officer");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(recordCategory_1);
        assertEquals(59, access.size()); // 58 + File
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.DENIED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.DENIED);
    }
    
    public void testRecordCategoryAsPowerUser()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_power_user");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(recordCategory_1);
        assertEquals(59, access.size()); // 58 + File
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.DENIED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.DENIED);
    }
    
    public void testRecordCategoryAsUser()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_user");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(recordCategory_1);
        assertEquals(59, access.size()); // 58 + File
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.DENIED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.DENIED);
    }

    public void testRecordFolderAsSystem()
    {
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(recordFolder_1);
        assertEquals(59, access.size());
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.ALLOWED);
        
    }
    
    public void testRecordFolderAsAdministrator()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_administrator");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(recordFolder_1);
        assertEquals(59, access.size());
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.ALLOWED);
    }
    
    public void testRecordFolderAsRecordsManager()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_records_manager");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(recordFolder_1);
        assertEquals(59, access.size()); // 58 + File
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.ALLOWED);
        
    }
    public void testRecordFolderAsSecurityOfficer()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_security_officer");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(recordFolder_1);
        assertEquals(59, access.size()); // 58 + File
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.DENIED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.DENIED);
    }
    
    public void testRecordFolderAsPowerUser()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_power_user");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(recordFolder_1);
        assertEquals(59, access.size()); // 58 + File
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.DENIED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.DENIED);
    }
    
    public void testRecordFolderAsUser()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_user");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(recordFolder_1);
        assertEquals(59, access.size()); // 58 + File
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.DENIED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.DENIED);
    }
    
    
    public void testRecordAsSystem()
    {
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(record_1);
        assertEquals(59, access.size());
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.ALLOWED);
        
    }
    
    public void testRecordAsAdministrator()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_administrator");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(record_1);
        assertEquals(59, access.size());
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.ALLOWED);
    }
    
    public void testRecordAsRecordsManager()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_records_manager");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(record_1);
        assertEquals(59, access.size()); // 58 + File
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.ALLOWED);
        
    }
    public void testRecordAsSecurityOfficer()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_security_officer");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(record_1);
        assertEquals(59, access.size()); // 58 + File
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.DENIED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.DENIED);
    }
    
    public void testRecordAsPowerUser()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_power_user");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(record_1);
        assertEquals(59, access.size()); // 58 + File
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.DENIED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.DENIED);
    }
    
    public void testRecordAsUser()
    {
        AuthenticationUtil.setFullyAuthenticatedUser("rm_user");
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(record_1);
        assertEquals(59, access.size()); // 58 + File
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.CLOSE_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.CYCLE_VITAL_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_AUDIT_AS_RECORD, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.DELETE_LINKS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.DELETE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, AccessStatus.DENIED);
        check(access, RMPermissionModel.DISPLAY_RIGHTS_REPORT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_NON_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_RECORD_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.EDIT_SELECTION_LISTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.ENABLE_DISABLE_AUDIT_BY_TYPES, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXPORT_AUDIT, AccessStatus.DENIED);
        check(access, RMPermissionModel.EXTEND_RETENTION_PERIOD_OR_FREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAKE_OPTIONAL_PARAMETERS_MANDATORY, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_CONTROLS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANAGE_ACCESS_RIGHTS, AccessStatus.DENIED);
        check(access, RMPermissionModel.MANUALLY_CHANGE_DISPOSITION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_CLASSIFICATION_GUIDE_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MAP_EMAIL_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.MOVE_RECORDS, AccessStatus.UNDETERMINED);
        check(access, RMPermissionModel.PASSWORD_CONTROL, AccessStatus.DENIED);
        check(access, RMPermissionModel.PLANNING_REVIEW_CYCLES, AccessStatus.DENIED);
        check(access, RMPermissionModel.RE_OPEN_FOLDERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.SELECT_AUDIT_METADATA, AccessStatus.DENIED);
        check(access, RMPermissionModel.TRIGGER_AN_EVENT, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNDECLARE_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.UNFREEZE, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_CLASSIFICATION_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_TRIGGER_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, AccessStatus.DENIED);
        check(access, RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, AccessStatus.DENIED);
        check(access, RMPermissionModel.VIEW_RECORDS, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, AccessStatus.DENIED);
    }


    
    
    private void check(Map<Capability, AccessStatus> access, String name, AccessStatus accessStatus)
    {
        Capability capability = recordsManagementService.getCapability(name);
        assertNotNull(capability);
        assertEquals(accessStatus, access.get(capability));
    }
    
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
