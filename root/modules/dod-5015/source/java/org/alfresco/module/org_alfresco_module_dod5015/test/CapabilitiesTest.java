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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Status;
import javax.transaction.UserTransaction;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.DOD5015Model;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.module.org_alfresco_module_dod5015.action.impl.CompleteEventAction;
import org.alfresco.module.org_alfresco_module_dod5015.action.impl.FreezeAction;
import org.alfresco.module.org_alfresco_module_dod5015.action.impl.TransferAction;
import org.alfresco.module.org_alfresco_module_dod5015.action.impl.TransferCompleteAction;
import org.alfresco.module.org_alfresco_module_dod5015.capability.Capability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.RMEntryVoter;
import org.alfresco.module.org_alfresco_module_dod5015.capability.RMPermissionModel;
import org.alfresco.module.org_alfresco_module_dod5015.event.RecordsManagementEventService;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.repo.security.permissions.impl.model.PermissionModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.view.ImporterBinding;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;

/**
 * @author andyh
 */
public class CapabilitiesTest extends TestCase
{
    private static ApplicationContext ctx = ApplicationContextHelper.getApplicationContext();

    private NodeRef rootNodeRef;

    private NodeService nodeService;

    private NodeService publicNodeService;

    private TransactionService transactionService;

    private ImporterService importerService;

    private UserTransaction testTX;

    private NodeRef filePlan;

    private PermissionService permissionService;

    private RecordsManagementService recordsManagementService;

    private RecordsManagementActionService recordsManagementActionService;

    private RecordsManagementEventService recordsManagementEventService;

    private PermissionModel permissionModel;

    private ContentService contentService;

    private NodeRef recordSeries;

    private NodeRef recordCategory_1;

    private NodeRef recordCategory_2;

    private NodeRef recordFolder_1;

    private NodeRef recordFolder_2;

    private NodeRef record_1;

    private NodeRef record_2;

    private RMEntryVoter rmEntryVoter;

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
        publicNodeService = (NodeService) ctx.getBean("NodeService");
        transactionService = (TransactionService) ctx.getBean("transactionComponent");
        importerService = (ImporterService) ctx.getBean("ImporterService");
        permissionService = (PermissionService) ctx.getBean("permissionService");
        permissionModel = (PermissionModel) ctx.getBean("permissionsModelDAO");
        contentService = (ContentService) ctx.getBean("contentService");

        recordsManagementService = (RecordsManagementService) ctx.getBean("RecordsManagementService");
        recordsManagementActionService = (RecordsManagementActionService) ctx.getBean("RecordsManagementActionService");
        recordsManagementEventService = (RecordsManagementEventService) ctx.getBean("RecordsManagementEventService");
        rmEntryVoter = (RMEntryVoter) ctx.getBean("rmEntryVoter");

        testTX = transactionService.getUserTransaction();
        testTX.begin();
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());

        StoreRef storeRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        rootNodeRef = nodeService.getRootNode(storeRef);

        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());

        recordsManagementEventService.getEvents();
        recordsManagementEventService.addEvent("rmEventType.simple", "event", "My Event");

        filePlan = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, DOD5015Model.TYPE_FILE_PLAN, DOD5015Model.TYPE_FILE_PLAN).getChildRef();
        recordSeries = createRecordSeries(filePlan, "RS", "RS-1", "Record Series", "My record series");
        recordCategory_1 = createRecordCategory(recordSeries, "Docs", "101-1", "Docs", "Docs", "week|1", true, false);
        recordCategory_2 = createRecordCategory(recordSeries, "More Docs", "101-2", "More Docs", "More Docs", "week|1", true, true);
        recordFolder_1 = createRecordFolder(recordCategory_1, "F1", "101-3", "title", "description", "week|1", true);
        recordFolder_2 = createRecordFolder(recordCategory_2, "F2", "102-3", "title", "description", "week|1", true);
        record_1 = createRecord(recordFolder_1);
        record_2 = createRecord(recordFolder_2);

        permissionService.setPermission(filePlan, "rm_user", RMPermissionModel.ROLE_USER, true);
        permissionService.setPermission(filePlan, "rm_user", RMPermissionModel.FILING, true);
        permissionService.setPermission(filePlan, "rm_power_user", RMPermissionModel.ROLE_POWER_USER, true);
        permissionService.setPermission(filePlan, "rm_power_user", RMPermissionModel.FILING, true);
        permissionService.setPermission(filePlan, "rm_security_officer", RMPermissionModel.ROLE_SECURITY_OFFICER, true);
        permissionService.setPermission(filePlan, "rm_security_officer", RMPermissionModel.FILING, true);
        permissionService.setPermission(filePlan, "rm_records_manager", RMPermissionModel.ROLE_RECORDS_MANAGER, true);
        permissionService.setPermission(filePlan, "rm_records_manager", RMPermissionModel.FILING, true);
        permissionService.setPermission(filePlan, "rm_administrator", RMPermissionModel.ROLE_ADMINISTRATOR, true);

        testTX.commit();
        testTX = transactionService.getUserTransaction();
        testTX.begin();

    }

    private NodeRef createRecord(NodeRef recordFolder)
    {
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
        props.put(ContentModel.PROP_NAME, "MyRecord.txt");
        NodeRef recordOne = this.nodeService.createNode(recordFolder, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "MyRecord.txt"),
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

    private NodeRef createRecordCategory(NodeRef recordSeries, String name, String identifier, String title, String description, String review, boolean vital,
            boolean recordLevelDisposition)
    {
        HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(ContentModel.PROP_NAME, name);
        properties.put(DOD5015Model.PROP_IDENTIFIER, identifier);
        properties.put(ContentModel.PROP_TITLE, title);
        properties.put(ContentModel.PROP_DESCRIPTION, description);
        properties.put(DOD5015Model.PROP_REVIEW_PERIOD, review);
        properties.put(DOD5015Model.PROP_VITAL_RECORD_INDICATOR, vital);
        NodeRef answer = nodeService.createNode(filePlan, ContentModel.ASSOC_CONTAINS, DOD5015Model.TYPE_RECORD_CATEGORY, DOD5015Model.TYPE_RECORD_CATEGORY, properties)
                .getChildRef();

        properties = new HashMap<QName, Serializable>();
        properties.put(DOD5015Model.PROP_DISPOSITION_AUTHORITY, "N1-218-00-4 item 023");
        properties.put(DOD5015Model.PROP_DISPOSITION_INSTRUCTIONS, "Cut off monthly, hold 1 month, then destroy.");
        properties.put(DOD5015Model.PROP_RECORD_LEVEL_DISPOSITION, recordLevelDisposition);
        NodeRef ds = nodeService.createNode(answer, DOD5015Model.ASSOC_DISPOSITION_SCHEDULE, DOD5015Model.TYPE_DISPOSITION_SCHEDULE, DOD5015Model.TYPE_DISPOSITION_SCHEDULE,
                properties).getChildRef();

        createDispoistionAction(ds, "cutoff", "monthend|1", null, "event");
        createDispoistionAction(ds, "transfer", "month|1", null, null);
        createDispoistionAction(ds, "accession", "month|1", null, null);
        createDispoistionAction(ds, "destroy", "month|1", "{http://www.alfresco.org/model/recordsmanagement/1.0}cutOffDate", null);
        return answer;
    }

    private NodeRef createDispoistionAction(NodeRef disposition, String actionName, String period, String periodProperty, String event)
    {
        HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(DOD5015Model.PROP_DISPOSITION_ACTION_NAME, actionName);
        properties.put(DOD5015Model.PROP_DISPOSITION_PERIOD, period);
        if (periodProperty != null)
        {
            properties.put(DOD5015Model.PROP_DISPOSITION_PERIOD_PROPERTY, periodProperty);
        }
        if (event != null)
        {
            properties.put(DOD5015Model.PROP_DISPOSITION_EVENT, event);
        }
        NodeRef answer = nodeService.createNode(disposition, DOD5015Model.ASSOC_DISPOSITION_ACTION_DEFINITIONS, DOD5015Model.TYPE_DISPOSITION_ACTION_DEFINITION,
                DOD5015Model.TYPE_DISPOSITION_ACTION_DEFINITION, properties).getChildRef();
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
        NodeRef answer = nodeService.createNode(recordCategory, ContentModel.ASSOC_CONTAINS, DOD5015Model.TYPE_RECORD_FOLDER, DOD5015Model.TYPE_RECORD_FOLDER, properties)
                .getChildRef();
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
        else if (testTX.getStatus() == Status.STATUS_MARKED_ROLLBACK)
        {
            testTX.rollback();
        }
        AuthenticationUtil.clearCurrentSecurityContext();
        super.tearDown();
    }

    public void testPermissionsModel()
    {
        Set<PermissionReference> exposed = permissionModel.getExposedPermissions(RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT);
        assertEquals(6, exposed.size());
        assertTrue(exposed.contains(permissionModel.getPermissionReference(RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT, RMPermissionModel.ROLE_ADMINISTRATOR)));

        Set<PermissionReference> all = permissionModel.getAllPermissions(RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT);
        assertEquals(58 /* capbilities */* 2 + 5 /* roles */+ (2 /* Read+File */* 2) + 1 /* Filing */, all.size());

        checkGranting(RMPermissionModel.ACCESS_AUDIT, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.ADD_MODIFY_EVENT_DATES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER,
                RMPermissionModel.ROLE_SECURITY_OFFICER, RMPermissionModel.ROLE_POWER_USER);
        checkGranting(RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CLOSE_FOLDERS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER, RMPermissionModel.ROLE_SECURITY_OFFICER,
                RMPermissionModel.ROLE_POWER_USER);
        checkGranting(RMPermissionModel.CREATE_AND_ASSOCIATE_SELECTION_LISTS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CREATE_MODIFY_DESTROY_CLASSIFICATION_GUIDES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER,
                RMPermissionModel.ROLE_SECURITY_OFFICER);
        checkGranting(RMPermissionModel.CREATE_MODIFY_DESTROY_EVENTS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_METADATA, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CREATE_MODIFY_DESTROY_FILEPLAN_TYPES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CREATE_MODIFY_DESTROY_FOLDERS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER,
                RMPermissionModel.ROLE_SECURITY_OFFICER, RMPermissionModel.ROLE_POWER_USER);
        checkGranting(RMPermissionModel.CREATE_MODIFY_DESTROY_RECORD_TYPES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CREATE_MODIFY_DESTROY_REFERENCE_TYPES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CREATE_MODIFY_DESTROY_ROLES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CREATE_MODIFY_DESTROY_TIMEFRAMES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CREATE_MODIFY_DESTROY_USERS_AND_GROUPS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CREATE_MODIFY_RECORDS_IN_CUTOFF_FOLDERS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.CYCLE_VITAL_RECORDS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER, RMPermissionModel.ROLE_SECURITY_OFFICER,
                RMPermissionModel.ROLE_POWER_USER);
        checkGranting(RMPermissionModel.DECLARE_AUDIT_AS_RECORD, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.DECLARE_RECORDS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER, RMPermissionModel.ROLE_SECURITY_OFFICER,
                RMPermissionModel.ROLE_POWER_USER, RMPermissionModel.ROLE_USER);
        checkGranting(RMPermissionModel.DECLARE_RECORDS_IN_CLOSED_FOLDERS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER,
                RMPermissionModel.ROLE_SECURITY_OFFICER, RMPermissionModel.ROLE_POWER_USER);
        checkGranting(RMPermissionModel.DELETE_AUDIT, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.DELETE_LINKS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.DELETE_RECORDS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.DESTROY_RECORDS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.DESTROY_RECORDS_SCHEDULED_FOR_DESTRUCTION, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.DISPLAY_RIGHTS_REPORT, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.EDIT_DECLARED_RECORD_METADATA, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.EDIT_NON_RECORD_METADATA, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER,
                RMPermissionModel.ROLE_SECURITY_OFFICER, RMPermissionModel.ROLE_POWER_USER);
        checkGranting(RMPermissionModel.EDIT_RECORD_METADATA, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER,
                RMPermissionModel.ROLE_SECURITY_OFFICER, RMPermissionModel.ROLE_POWER_USER);
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
        checkGranting(RMPermissionModel.PLANNING_REVIEW_CYCLES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER,
                RMPermissionModel.ROLE_SECURITY_OFFICER, RMPermissionModel.ROLE_POWER_USER);
        checkGranting(RMPermissionModel.RE_OPEN_FOLDERS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER, RMPermissionModel.ROLE_SECURITY_OFFICER,
                RMPermissionModel.ROLE_POWER_USER);
        checkGranting(RMPermissionModel.SELECT_AUDIT_METADATA, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.TRIGGER_AN_EVENT, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.UNDECLARE_RECORDS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.UNFREEZE, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.UPDATE_CLASSIFICATION_DATES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER,
                RMPermissionModel.ROLE_SECURITY_OFFICER);
        checkGranting(RMPermissionModel.UPDATE_EXEMPTION_CATEGORIES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER,
                RMPermissionModel.ROLE_SECURITY_OFFICER);
        checkGranting(RMPermissionModel.UPDATE_TRIGGER_DATES, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.UPDATE_VITAL_RECORD_CYCLE_INFORMATION, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);
        checkGranting(RMPermissionModel.UPGRADE_DOWNGRADE_AND_DECLASSIFY_RECORDS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER,
                RMPermissionModel.ROLE_SECURITY_OFFICER);
        checkGranting(RMPermissionModel.VIEW_RECORDS, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER, RMPermissionModel.ROLE_SECURITY_OFFICER,
                RMPermissionModel.ROLE_POWER_USER, RMPermissionModel.ROLE_USER);
        checkGranting(RMPermissionModel.VIEW_UPDATE_REASONS_FOR_FREEZE, RMPermissionModel.ROLE_ADMINISTRATOR, RMPermissionModel.ROLE_RECORDS_MANAGER);

    }

    private void checkGranting(String permission, String... roles)
    {
        Set<PermissionReference> granting = permissionModel.getGrantingPermissions(permissionModel.getPermissionReference(RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT,
                permission));
        Set<PermissionReference> test = new HashSet<PermissionReference>();
        test.addAll(granting);
        Set<PermissionReference> nonRM = new HashSet<PermissionReference>();
        for (PermissionReference pr : granting)
        {
            if (!pr.getQName().equals(RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT))
            {
                nonRM.add(pr);
            }
        }
        test.removeAll(nonRM);
        assertEquals(roles.length + 1, test.size());
        for (String role : roles)
        {
            assertTrue(test.contains(permissionModel.getPermissionReference(RecordsManagementModel.ASPECT_FILE_PLAN_COMPONENT, role)));
        }

    }

    public void testConfig()
    {
        assertEquals(6, recordsManagementService.getProtectedAspects().size());
        assertEquals(12, recordsManagementService.getProtectedProperties().size());

        // Test action wire up
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.ACCESS_AUDIT).getActionNames().size());
        assertEquals(2, recordsManagementService.getCapability(RMPermissionModel.ADD_MODIFY_EVENT_DATES).getActionNames().size());
        assertEquals(1, recordsManagementService.getCapability(RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF).getActionNames().size());
        assertEquals(0, recordsManagementService.getCapability(RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES).getActionNames().size());
        assertEquals(2, recordsManagementService.getCapability(RMPermissionModel.AUTHORIZE_ALL_TRANSFERS).getActionNames().size());
        assertEquals(2, recordsManagementService.getCapability(RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS).getActionNames().size());
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
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
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

    public void testFilePlanAsAdmin()
    {
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(filePlan);
        assertEquals(59, access.size());
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
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
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
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
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
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
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
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

    public void testRecordSeriesAsAdmin()
    {
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(recordSeries);
        assertEquals(59, access.size());
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
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
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
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
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
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
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
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

    public void testRecordCategoryAsAdmin()
    {
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(recordCategory_1);
        assertEquals(59, access.size());
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
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
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
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
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
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
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
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

    public void testRecordFolderAsAdmin()
    {
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(recordFolder_1);
        assertEquals(59, access.size());
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
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
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
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
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
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
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
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

    public void testRecordAsAdmin()
    {
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        Map<Capability, AccessStatus> access = recordsManagementService.getCapabilities(record_1);
        assertEquals(59, access.size());
        check(access, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        check(access, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        check(access, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
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
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
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
        check(access, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        check(access, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
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

    private void checkCapability(String user, NodeRef nodeRef, String permission, AccessStatus accessStstus)
    {
        AuthenticationUtil.setFullyAuthenticatedUser(user);
        Map<Capability, AccessStatus> access = rmEntryVoter.getCapabilities(nodeRef);
        check(access, permission, accessStstus);
    }

    private void checkPermission(String user, NodeRef nodeRef, String permission, AccessStatus accessStstus)
    {
        AuthenticationUtil.setFullyAuthenticatedUser(user);
        assertTrue(permissionService.hasPermission(nodeRef, permission) == accessStstus);
    }

    public void testAccessAuditCapability()
    {
        // capability is checked above - just check permission assignments
        checkPermission(AuthenticationUtil.getSystemUserName(), filePlan, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        checkPermission("rm_administrator", filePlan, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        checkPermission("rm_records_manager", filePlan, RMPermissionModel.ACCESS_AUDIT, AccessStatus.ALLOWED);
        checkPermission("rm_security_officer", filePlan, RMPermissionModel.ACCESS_AUDIT, AccessStatus.DENIED);
        checkPermission("rm_power_user", filePlan, RMPermissionModel.ACCESS_AUDIT, AccessStatus.DENIED);
        checkPermission("rm_user", filePlan, RMPermissionModel.ACCESS_AUDIT, AccessStatus.DENIED);
    }

    public void testAddModifyEventDatesCapability()
    {
        // Folder
        checkPermission(AuthenticationUtil.getSystemUserName(), recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkPermission("rm_administrator", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkPermission("rm_records_manager", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkPermission("rm_security_officer", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkPermission("rm_power_user", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkPermission("rm_user", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);

        // Record
        checkPermission(AuthenticationUtil.getSystemUserName(), record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkPermission("rm_administrator", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkPermission("rm_records_manager", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkPermission("rm_security_officer", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkPermission("rm_power_user", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkPermission("rm_user", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);

        // folder level

        checkCapability(AuthenticationUtil.getSystemUserName(), recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkCapability("rm_administrator", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkCapability("rm_records_manager", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkCapability("rm_security_officer", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkCapability("rm_power_user", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkCapability("rm_user", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);

        checkCapability(AuthenticationUtil.getSystemUserName(), record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("rm_administrator", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("rm_records_manager", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("rm_security_officer", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("rm_power_user", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("rm_user", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);

        // record level

        checkCapability(AuthenticationUtil.getSystemUserName(), recordFolder_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("rm_administrator", recordFolder_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("rm_records_manager", recordFolder_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("rm_security_officer", recordFolder_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("rm_power_user", recordFolder_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("rm_user", recordFolder_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);

        checkCapability(AuthenticationUtil.getSystemUserName(), record_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkCapability("rm_administrator", record_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkCapability("rm_records_manager", record_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkCapability("rm_security_officer", record_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkCapability("rm_power_user", record_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkCapability("rm_user", record_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);

        // check person with no access and add read and write
        // Filing

        checkCapability("test_user", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);

        permissionService.setPermission(filePlan, "test_user", RMPermissionModel.VIEW_RECORDS, true);
        permissionService.setInheritParentPermissions(recordCategory_1, false);
        permissionService.setInheritParentPermissions(recordCategory_2, false);
        permissionService.setPermission(recordCategory_1, "test_user", RMPermissionModel.READ_RECORDS, true);
        permissionService.setPermission(recordCategory_2, "test_user", RMPermissionModel.READ_RECORDS, true);
        permissionService.setPermission(recordFolder_1, "test_user", RMPermissionModel.FILING, true);
        permissionService.setPermission(recordFolder_2, "test_user", RMPermissionModel.FILING, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);

        permissionService.setPermission(filePlan, "test_user", RMPermissionModel.DECLARE_RECORDS, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);

        permissionService.setPermission(filePlan, "test_user", RMPermissionModel.ADD_MODIFY_EVENT_DATES, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);

        permissionService.deletePermission(filePlan, "test_user", RMPermissionModel.DECLARE_RECORDS);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);

        permissionService.setPermission(filePlan, "test_user", RMPermissionModel.DECLARE_RECORDS, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);

        permissionService.deletePermission(filePlan, "test_user", RMPermissionModel.VIEW_RECORDS);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);

        permissionService.setPermission(filePlan, "test_user", RMPermissionModel.VIEW_RECORDS, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);

        permissionService.deletePermission(recordFolder_1, "test_user", RMPermissionModel.FILING);
        permissionService.deletePermission(recordFolder_2, "test_user", RMPermissionModel.FILING);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);

        permissionService.setPermission(recordFolder_1, "test_user", RMPermissionModel.FILING, true);
        permissionService.setPermission(recordFolder_2, "test_user", RMPermissionModel.FILING, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);

        // check frozen

        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
        Map<String, Serializable> params = new HashMap<String, Serializable>(1);
        params.put(FreezeAction.PARAM_REASON, "one");
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "freeze", params);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);

        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
        params = new HashMap<String, Serializable>(1);
        params.put(FreezeAction.PARAM_REASON, "Two");
        recordsManagementActionService.executeRecordsManagementAction(record_2, "freeze", params);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);

        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "unfreeze");
        recordsManagementActionService.executeRecordsManagementAction(record_2, "unfreeze");

        checkCapability("test_user", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);

        // Check closed
        // should make no difference
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "closeRecordFolder");
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_2, "closeRecordFolder");

        checkCapability("test_user", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);

        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "openRecordFolder");
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_2, "openRecordFolder");

        checkCapability("test_user", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.ALLOWED);

        // try and complete some events

        AuthenticationUtil.setFullyAuthenticatedUser("test_user");
        Map<String, Serializable> eventDetails = new HashMap<String, Serializable>(3);
        eventDetails.put(CompleteEventAction.PARAM_EVENT_NAME, "event");
        eventDetails.put(CompleteEventAction.PARAM_EVENT_COMPLETED_AT, new Date());
        eventDetails.put(CompleteEventAction.PARAM_EVENT_COMPLETED_BY, "test_user");
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "completeEvent", eventDetails);
        try
        {
            recordsManagementActionService.executeRecordsManagementAction(recordFolder_2, "completeEvent", eventDetails);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
        try
        {
            recordsManagementActionService.executeRecordsManagementAction(record_1, "completeEvent", eventDetails);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
        recordsManagementActionService.executeRecordsManagementAction(record_2, "completeEvent", eventDetails);

        // check protected properties

        try
        {
            publicNodeService.setProperty(record_1, RecordsManagementModel.PROP_EVENT_EXECUTION_COMPLETE, true);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
        try
        {
            publicNodeService.setProperty(record_1, RecordsManagementModel.PROP_EVENT_EXECUTION_COMPLETED_AT, new Date());
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
        try
        {
            publicNodeService.setProperty(record_1, RecordsManagementModel.PROP_EVENT_EXECUTION_COMPLETED_BY, "me");
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }

        // check cutoff

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());

        nodeService.setProperty(record_1, RecordsManagementModel.PROP_ORIGINATOR, "origValue");
        nodeService.setProperty(record_1, RecordsManagementModel.PROP_ORIGINATING_ORGANIZATION, "origOrgValue");
        nodeService.setProperty(record_1, RecordsManagementModel.PROP_PUBLICATION_DATE, new Date());
        nodeService.setProperty(record_1, ContentModel.PROP_TITLE, "titleValue");
        recordsManagementActionService.executeRecordsManagementAction(record_1, "declareRecord");

        nodeService.setProperty(record_2, RecordsManagementModel.PROP_ORIGINATOR, "origValue");
        nodeService.setProperty(record_2, RecordsManagementModel.PROP_ORIGINATING_ORGANIZATION, "origOrgValue");
        nodeService.setProperty(record_2, RecordsManagementModel.PROP_PUBLICATION_DATE, new Date());
        nodeService.setProperty(record_2, ContentModel.PROP_TITLE, "titleValue");
        recordsManagementActionService.executeRecordsManagementAction(record_2, "declareRecord");

        NodeRef ndNodeRef = this.nodeService.getChildAssocs(recordFolder_1, RecordsManagementModel.ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL).get(0).getChildRef();
        this.nodeService.setProperty(ndNodeRef, RecordsManagementModel.PROP_DISPOSITION_AS_OF, calendar.getTime());
        ndNodeRef = this.nodeService.getChildAssocs(record_2, RecordsManagementModel.ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL).get(0).getChildRef();
        this.nodeService.setProperty(ndNodeRef, RecordsManagementModel.PROP_DISPOSITION_AS_OF, calendar.getTime());
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "cutoff", null);
        recordsManagementActionService.executeRecordsManagementAction(record_2, "cutoff", null);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.ADD_MODIFY_EVENT_DATES, AccessStatus.DENIED);
    }

    public void testApproveRecordsScheduledForCutoffCapability()
    {
        // Folder
        checkPermission(AuthenticationUtil.getSystemUserName(), recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);
        checkPermission("rm_administrator", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);
        checkPermission("rm_records_manager", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);
        checkPermission("rm_security_officer", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkPermission("rm_power_user", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkPermission("rm_user", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);

        // Record
        checkPermission(AuthenticationUtil.getSystemUserName(), record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);
        checkPermission("rm_administrator", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);
        checkPermission("rm_records_manager", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);
        checkPermission("rm_security_officer", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkPermission("rm_power_user", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkPermission("rm_user", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);

        // folder level - not eligible all deny

        checkCapability(AuthenticationUtil.getSystemUserName(), recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_administrator", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_records_manager", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_security_officer", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_power_user", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_user", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);

        checkCapability(AuthenticationUtil.getSystemUserName(), record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_administrator", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_records_manager", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_security_officer", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_power_user", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_user", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);

        // record level - not eligible all deny

        checkCapability(AuthenticationUtil.getSystemUserName(), recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_administrator", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_records_manager", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_security_officer", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_power_user", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_user", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);

        checkCapability(AuthenticationUtil.getSystemUserName(), record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_administrator", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_records_manager", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_security_officer", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_power_user", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_user", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);

        // Set appropriate state - declare records and make eligible

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());

        nodeService.setProperty(record_1, RecordsManagementModel.PROP_ORIGINATOR, "origValue");
        nodeService.setProperty(record_1, RecordsManagementModel.PROP_ORIGINATING_ORGANIZATION, "origOrgValue");
        nodeService.setProperty(record_1, RecordsManagementModel.PROP_PUBLICATION_DATE, new Date());
        nodeService.setProperty(record_1, ContentModel.PROP_TITLE, "titleValue");
        recordsManagementActionService.executeRecordsManagementAction(record_1, "declareRecord");

        nodeService.setProperty(record_2, RecordsManagementModel.PROP_ORIGINATOR, "origValue");
        nodeService.setProperty(record_2, RecordsManagementModel.PROP_ORIGINATING_ORGANIZATION, "origOrgValue");
        nodeService.setProperty(record_2, RecordsManagementModel.PROP_PUBLICATION_DATE, new Date());
        nodeService.setProperty(record_2, ContentModel.PROP_TITLE, "titleValue");
        recordsManagementActionService.executeRecordsManagementAction(record_2, "declareRecord");

        NodeRef ndNodeRef = this.nodeService.getChildAssocs(recordFolder_1, RecordsManagementModel.ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL).get(0).getChildRef();
        this.nodeService.setProperty(ndNodeRef, RecordsManagementModel.PROP_DISPOSITION_AS_OF, calendar.getTime());
        ndNodeRef = this.nodeService.getChildAssocs(record_2, RecordsManagementModel.ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL).get(0).getChildRef();
        this.nodeService.setProperty(ndNodeRef, RecordsManagementModel.PROP_DISPOSITION_AS_OF, calendar.getTime());

        // folder level

        checkCapability(AuthenticationUtil.getSystemUserName(), recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);
        checkCapability("rm_administrator", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);
        checkCapability("rm_records_manager", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);
        checkCapability("rm_security_officer", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_power_user", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_user", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);

        checkCapability(AuthenticationUtil.getSystemUserName(), record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_administrator", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_records_manager", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_security_officer", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_power_user", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_user", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);

        // record level

        checkCapability(AuthenticationUtil.getSystemUserName(), recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_administrator", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_records_manager", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_security_officer", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_power_user", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_user", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);

        checkCapability(AuthenticationUtil.getSystemUserName(), record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);
        checkCapability("rm_administrator", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);
        checkCapability("rm_records_manager", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);
        checkCapability("rm_security_officer", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_power_user", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("rm_user", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);

        // check person with no access and add read and write
        // Filing

        checkCapability("test_user", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);

        permissionService.setPermission(filePlan, "test_user", RMPermissionModel.VIEW_RECORDS, true);
        permissionService.setInheritParentPermissions(recordCategory_1, false);
        permissionService.setInheritParentPermissions(recordCategory_2, false);
        permissionService.setPermission(recordCategory_1, "test_user", RMPermissionModel.READ_RECORDS, true);
        permissionService.setPermission(recordCategory_2, "test_user", RMPermissionModel.READ_RECORDS, true);
        permissionService.setPermission(recordFolder_1, "test_user", RMPermissionModel.FILING, true);
        permissionService.setPermission(recordFolder_2, "test_user", RMPermissionModel.FILING, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);

        permissionService.setPermission(filePlan, "test_user", RMPermissionModel.DECLARE_RECORDS, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);

        permissionService.setPermission(filePlan, "test_user", RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);

        permissionService.deletePermission(filePlan, "test_user", RMPermissionModel.DECLARE_RECORDS);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);

        permissionService.setPermission(filePlan, "test_user", RMPermissionModel.DECLARE_RECORDS, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);

        permissionService.deletePermission(filePlan, "test_user", RMPermissionModel.VIEW_RECORDS);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);

        permissionService.setPermission(filePlan, "test_user", RMPermissionModel.VIEW_RECORDS, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);

        permissionService.deletePermission(recordFolder_1, "test_user", RMPermissionModel.FILING);
        permissionService.deletePermission(recordFolder_2, "test_user", RMPermissionModel.FILING);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);

        permissionService.setPermission(recordFolder_1, "test_user", RMPermissionModel.FILING, true);
        permissionService.setPermission(recordFolder_2, "test_user", RMPermissionModel.FILING, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);

        // check frozen

        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
        Map<String, Serializable> params = new HashMap<String, Serializable>(1);
        params.put(FreezeAction.PARAM_REASON, "one");
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "freeze", params);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);

        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
        params = new HashMap<String, Serializable>(1);
        params.put(FreezeAction.PARAM_REASON, "Two");
        recordsManagementActionService.executeRecordsManagementAction(record_2, "freeze", params);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);

        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "unfreeze");
        recordsManagementActionService.executeRecordsManagementAction(record_2, "unfreeze");

        checkCapability("test_user", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);

        // Check closed
        // should make no difference
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "closeRecordFolder");
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_2, "closeRecordFolder");

        checkCapability("test_user", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);

        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "openRecordFolder");
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_2, "openRecordFolder");

        checkCapability("test_user", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.ALLOWED);

        // try and cut off

        AuthenticationUtil.setFullyAuthenticatedUser("test_user");
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "cutoff", null);
        try
        {
            recordsManagementActionService.executeRecordsManagementAction(recordFolder_2, "cutoff", null);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
        try
        {
            recordsManagementActionService.executeRecordsManagementAction(record_1, "cutoff", null);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
        recordsManagementActionService.executeRecordsManagementAction(record_2, "cutoff", null);

        // check protected properties

        try
        {
            publicNodeService.setProperty(record_1, RecordsManagementModel.PROP_CUT_OFF_DATE, new Date());
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }

        // check cutoff again (it is already cut off)

        try
        {
            recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "cutoff", null);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
        try
        {
            recordsManagementActionService.executeRecordsManagementAction(record_2, "cutoff", null);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }

        checkCapability("test_user", recordFolder_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.APPROVE_RECORDS_SCHEDULED_FOR_CUTOFF, AccessStatus.DENIED);
    }

    public void testAttachRulesToMetadataPropertiesCapability()
    {
        // capability is checked above - just check permission assignments
        checkPermission(AuthenticationUtil.getSystemUserName(), filePlan, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        checkPermission("rm_administrator", filePlan, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        checkPermission("rm_records_manager", filePlan, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.ALLOWED);
        checkPermission("rm_security_officer", filePlan, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.DENIED);
        checkPermission("rm_power_user", filePlan, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.DENIED);
        checkPermission("rm_user", filePlan, RMPermissionModel.ATTACH_RULES_TO_METADATA_PROPERTIES, AccessStatus.DENIED);
    }

    public void testAuthorizeAllTransfersCapability()
    {
        // Folder
        checkPermission(AuthenticationUtil.getSystemUserName(), recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        checkPermission("rm_administrator", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        checkPermission("rm_records_manager", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        checkPermission("rm_security_officer", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkPermission("rm_power_user", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkPermission("rm_user", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);

        // Record
        checkPermission(AuthenticationUtil.getSystemUserName(), record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        checkPermission("rm_administrator", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        checkPermission("rm_records_manager", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        checkPermission("rm_security_officer", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkPermission("rm_power_user", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkPermission("rm_user", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);

        // folder level - not eligible all deny

        checkCapability(AuthenticationUtil.getSystemUserName(), recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_administrator", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_records_manager", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_security_officer", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_power_user", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_user", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);

        checkCapability(AuthenticationUtil.getSystemUserName(), record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_administrator", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_records_manager", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_security_officer", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_power_user", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_user", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);

        // record level - not eligible all deny

        checkCapability(AuthenticationUtil.getSystemUserName(), recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_administrator", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_records_manager", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_security_officer", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_power_user", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_user", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);

        checkCapability(AuthenticationUtil.getSystemUserName(), record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_administrator", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_records_manager", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_security_officer", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_power_user", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_user", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);

        // Set appropriate state - declare records and make eligible

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());

        nodeService.setProperty(record_1, RecordsManagementModel.PROP_ORIGINATOR, "origValue");
        nodeService.setProperty(record_1, RecordsManagementModel.PROP_ORIGINATING_ORGANIZATION, "origOrgValue");
        nodeService.setProperty(record_1, RecordsManagementModel.PROP_PUBLICATION_DATE, new Date());
        nodeService.setProperty(record_1, ContentModel.PROP_TITLE, "titleValue");
        recordsManagementActionService.executeRecordsManagementAction(record_1, "declareRecord");

        nodeService.setProperty(record_2, RecordsManagementModel.PROP_ORIGINATOR, "origValue");
        nodeService.setProperty(record_2, RecordsManagementModel.PROP_ORIGINATING_ORGANIZATION, "origOrgValue");
        nodeService.setProperty(record_2, RecordsManagementModel.PROP_PUBLICATION_DATE, new Date());
        nodeService.setProperty(record_2, ContentModel.PROP_TITLE, "titleValue");
        recordsManagementActionService.executeRecordsManagementAction(record_2, "declareRecord");

        NodeRef ndNodeRef = this.nodeService.getChildAssocs(recordFolder_1, RecordsManagementModel.ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL).get(0).getChildRef();
        this.nodeService.setProperty(ndNodeRef, RecordsManagementModel.PROP_DISPOSITION_AS_OF, calendar.getTime());
        ndNodeRef = this.nodeService.getChildAssocs(record_2, RecordsManagementModel.ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL).get(0).getChildRef();
        this.nodeService.setProperty(ndNodeRef, RecordsManagementModel.PROP_DISPOSITION_AS_OF, calendar.getTime());

        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "cutoff", null);
        recordsManagementActionService.executeRecordsManagementAction(record_2, "cutoff", null);

        ndNodeRef = this.nodeService.getChildAssocs(recordFolder_1, RecordsManagementModel.ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL).get(0).getChildRef();
        this.nodeService.setProperty(ndNodeRef, RecordsManagementModel.PROP_DISPOSITION_AS_OF, calendar.getTime());
        ndNodeRef = this.nodeService.getChildAssocs(record_2, RecordsManagementModel.ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL).get(0).getChildRef();
        this.nodeService.setProperty(ndNodeRef, RecordsManagementModel.PROP_DISPOSITION_AS_OF, calendar.getTime());

        // folder level

        checkCapability(AuthenticationUtil.getSystemUserName(), recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("rm_administrator", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("rm_records_manager", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("rm_security_officer", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_power_user", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_user", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);

        checkCapability(AuthenticationUtil.getSystemUserName(), record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_administrator", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_records_manager", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_security_officer", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_power_user", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_user", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);

        // record level

        checkCapability(AuthenticationUtil.getSystemUserName(), recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_administrator", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_records_manager", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_security_officer", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_power_user", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_user", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);

        checkCapability(AuthenticationUtil.getSystemUserName(), record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("rm_administrator", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("rm_records_manager", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("rm_security_officer", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_power_user", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_user", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);

        // check person with no access and add read and write
        // Filing

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);

        permissionService.setPermission(filePlan, "test_user", RMPermissionModel.VIEW_RECORDS, true);
        permissionService.setInheritParentPermissions(recordCategory_1, false);
        permissionService.setInheritParentPermissions(recordCategory_2, false);
        permissionService.setPermission(recordCategory_1, "test_user", RMPermissionModel.READ_RECORDS, true);
        permissionService.setPermission(recordCategory_2, "test_user", RMPermissionModel.READ_RECORDS, true);
        permissionService.setPermission(recordFolder_1, "test_user", RMPermissionModel.FILING, true);
        permissionService.setPermission(recordFolder_2, "test_user", RMPermissionModel.FILING, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);

        permissionService.setPermission(filePlan, "test_user", RMPermissionModel.DECLARE_RECORDS, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);

        permissionService.setPermission(filePlan, "test_user", RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);

        permissionService.deletePermission(filePlan, "test_user", RMPermissionModel.DECLARE_RECORDS);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);

        permissionService.setPermission(filePlan, "test_user", RMPermissionModel.DECLARE_RECORDS, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);

        permissionService.deletePermission(filePlan, "test_user", RMPermissionModel.VIEW_RECORDS);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);

        permissionService.setPermission(filePlan, "test_user", RMPermissionModel.VIEW_RECORDS, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);

        permissionService.deletePermission(recordFolder_1, "test_user", RMPermissionModel.FILING);
        permissionService.deletePermission(recordFolder_2, "test_user", RMPermissionModel.FILING);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);

        permissionService.setPermission(recordFolder_1, "test_user", RMPermissionModel.FILING, true);
        permissionService.setPermission(recordFolder_2, "test_user", RMPermissionModel.FILING, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);

        // check frozen

        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
        Map<String, Serializable> params = new HashMap<String, Serializable>(1);
        params.put(FreezeAction.PARAM_REASON, "one");
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "freeze", params);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);

        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
        params = new HashMap<String, Serializable>(1);
        params.put(FreezeAction.PARAM_REASON, "Two");
        recordsManagementActionService.executeRecordsManagementAction(record_2, "freeze", params);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);

        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "unfreeze");
        recordsManagementActionService.executeRecordsManagementAction(record_2, "unfreeze");

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);

        // Check closed
        // should make no difference
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "closeRecordFolder");
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_2, "closeRecordFolder");

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);

        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "openRecordFolder");
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_2, "openRecordFolder");

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);

        // try and transfer

        AuthenticationUtil.setFullyAuthenticatedUser("test_user");
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "transfer", null);
        try
        {
            recordsManagementActionService.executeRecordsManagementAction(recordFolder_2, "transfer", null);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
        try
        {
            recordsManagementActionService.executeRecordsManagementAction(record_1, "transfer", null);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
        recordsManagementActionService.executeRecordsManagementAction(record_2, "transfer", null);

        // check protected properties

        // PROP_DISPOSITION_ACTION_STARTED_AT
        // PROP_DISPOSITION_ACTION_STARTED_BY
        // PROP_DISPOSITION_ACTION_COMPLETED_AT
        // PROP_DISPOSITION_ACTION_COMPLETED_BY

        try
        {
            publicNodeService.setProperty(record_1, RecordsManagementModel.PROP_DISPOSITION_ACTION_STARTED_AT, true);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }

        try
        {
            publicNodeService.setProperty(record_1, RecordsManagementModel.PROP_DISPOSITION_ACTION_STARTED_BY, true);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }

        try
        {
            publicNodeService.setProperty(record_1, RecordsManagementModel.PROP_DISPOSITION_ACTION_COMPLETED_AT, true);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }

        try
        {
            publicNodeService.setProperty(record_1, RecordsManagementModel.PROP_DISPOSITION_ACTION_COMPLETED_BY, true);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }

        // check cutoff again (it is already cut off)

        try
        {
            recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "transfer", null);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
        catch(AlfrescoRuntimeException are)
        {
            
        }
        try
        {
            recordsManagementActionService.executeRecordsManagementAction(record_2, "transfer", null);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
        catch(AlfrescoRuntimeException are)
        {
            
        }
        
        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_ALL_TRANSFERS, AccessStatus.ALLOWED);

        // check each action

        TransferAction transfer = (TransferAction) ctx.getBean("transfer");
        assertFalse(transfer.isExecutable(recordFolder_1, null));
        assertFalse(transfer.isExecutable(record_1, null));
        assertFalse(transfer.isExecutable(recordFolder_2, null));
        assertFalse(transfer.isExecutable(record_2, null));

        TransferCompleteAction transferComplete = (TransferCompleteAction) ctx.getBean("transferComplete");
        assertTrue(transferComplete.isExecutable(recordFolder_1, null));
        assertFalse(transferComplete.isExecutable(record_1, null));
        assertFalse(transferComplete.isExecutable(recordFolder_2, null));
        assertTrue(transferComplete.isExecutable(record_2, null));

        // try and complete the transfer

        AuthenticationUtil.setFullyAuthenticatedUser("test_user");
        recordsManagementActionService.executeRecordsManagementAction(getTranferObject(recordFolder_1), "transferComplete", null);
        try
        {
            recordsManagementActionService.executeRecordsManagementAction(recordFolder_2, "transferComplete", null);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
        try
        {
            recordsManagementActionService.executeRecordsManagementAction(record_1, "transferComplete", null);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
        try
        {
            // will fail as this is in the same transafer which is now done.
            recordsManagementActionService.executeRecordsManagementAction(getTranferObject(record_2), "transferComplete", null);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }

        // try again - should fail

        try
        {
            recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "transferComplete", null);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
        try
        {
            recordsManagementActionService.executeRecordsManagementAction(record_2, "transferComplete", null);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
    }

    private NodeRef getTranferObject(NodeRef fp)
    {
        List<ChildAssociationRef> assocs = this.nodeService.getParentAssocs(fp, RecordsManagementModel.ASSOC_TRANSFERRED, RegexQNamePattern.MATCH_ALL);
        if (assocs.size() > 0)
        {
            return assocs.get(0).getParentRef();
        }
        else
        {
            return fp;
        }
    }

    public void testAuthorizeNominatedTransfersCapability()
    {
        // Folder
        checkPermission(AuthenticationUtil.getSystemUserName(), recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        checkPermission("rm_administrator", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        checkPermission("rm_records_manager", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        checkPermission("rm_security_officer", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkPermission("rm_power_user", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkPermission("rm_user", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);

        // Record
        checkPermission(AuthenticationUtil.getSystemUserName(), record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        checkPermission("rm_administrator", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        checkPermission("rm_records_manager", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        checkPermission("rm_security_officer", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkPermission("rm_power_user", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkPermission("rm_user", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);

        // folder level - not eligible all deny

        checkCapability(AuthenticationUtil.getSystemUserName(), recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_administrator", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_records_manager", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_security_officer", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_power_user", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_user", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);

        checkCapability(AuthenticationUtil.getSystemUserName(), record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_administrator", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_records_manager", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_security_officer", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_power_user", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_user", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);

        // record level - not eligible all deny

        checkCapability(AuthenticationUtil.getSystemUserName(), recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_administrator", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_records_manager", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_security_officer", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_power_user", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_user", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);

        checkCapability(AuthenticationUtil.getSystemUserName(), record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_administrator", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_records_manager", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_security_officer", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_power_user", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_user", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);

        // Set appropriate state - declare records and make eligible

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());

        nodeService.setProperty(record_1, RecordsManagementModel.PROP_ORIGINATOR, "origValue");
        nodeService.setProperty(record_1, RecordsManagementModel.PROP_ORIGINATING_ORGANIZATION, "origOrgValue");
        nodeService.setProperty(record_1, RecordsManagementModel.PROP_PUBLICATION_DATE, new Date());
        nodeService.setProperty(record_1, ContentModel.PROP_TITLE, "titleValue");
        recordsManagementActionService.executeRecordsManagementAction(record_1, "declareRecord");

        nodeService.setProperty(record_2, RecordsManagementModel.PROP_ORIGINATOR, "origValue");
        nodeService.setProperty(record_2, RecordsManagementModel.PROP_ORIGINATING_ORGANIZATION, "origOrgValue");
        nodeService.setProperty(record_2, RecordsManagementModel.PROP_PUBLICATION_DATE, new Date());
        nodeService.setProperty(record_2, ContentModel.PROP_TITLE, "titleValue");
        recordsManagementActionService.executeRecordsManagementAction(record_2, "declareRecord");

        NodeRef ndNodeRef = this.nodeService.getChildAssocs(recordFolder_1, RecordsManagementModel.ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL).get(0).getChildRef();
        this.nodeService.setProperty(ndNodeRef, RecordsManagementModel.PROP_DISPOSITION_AS_OF, calendar.getTime());
        ndNodeRef = this.nodeService.getChildAssocs(record_2, RecordsManagementModel.ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL).get(0).getChildRef();
        this.nodeService.setProperty(ndNodeRef, RecordsManagementModel.PROP_DISPOSITION_AS_OF, calendar.getTime());

        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "cutoff", null);
        recordsManagementActionService.executeRecordsManagementAction(record_2, "cutoff", null);

        ndNodeRef = this.nodeService.getChildAssocs(recordFolder_1, RecordsManagementModel.ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL).get(0).getChildRef();
        this.nodeService.setProperty(ndNodeRef, RecordsManagementModel.PROP_DISPOSITION_AS_OF, calendar.getTime());
        ndNodeRef = this.nodeService.getChildAssocs(record_2, RecordsManagementModel.ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL).get(0).getChildRef();
        this.nodeService.setProperty(ndNodeRef, RecordsManagementModel.PROP_DISPOSITION_AS_OF, calendar.getTime());

        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "transfer", null);
        recordsManagementActionService.executeRecordsManagementAction(record_2, "transfer", null);
        recordsManagementActionService.executeRecordsManagementAction(getTranferObject(recordFolder_1), "transferComplete", null);
        
        assertTrue(this.nodeService.exists(recordFolder_1));
        ndNodeRef = this.nodeService.getChildAssocs(recordFolder_1, RecordsManagementModel.ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL).get(0).getChildRef();
        this.nodeService.setProperty(ndNodeRef, RecordsManagementModel.PROP_DISPOSITION_AS_OF, calendar.getTime());
        assertTrue(this.nodeService.exists(recordFolder_1));
        ndNodeRef = this.nodeService.getChildAssocs(record_2, RecordsManagementModel.ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL).get(0).getChildRef();
        this.nodeService.setProperty(ndNodeRef, RecordsManagementModel.PROP_DISPOSITION_AS_OF, calendar.getTime());
        
        // folder level

        assertTrue(this.nodeService.exists(recordFolder_1));
        checkCapability(AuthenticationUtil.getSystemUserName(), recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("rm_administrator", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("rm_records_manager", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("rm_security_officer", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_power_user", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_user", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);

        checkCapability(AuthenticationUtil.getSystemUserName(), record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_administrator", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_records_manager", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_security_officer", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_power_user", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_user", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);

        // record level

        checkCapability(AuthenticationUtil.getSystemUserName(), recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_administrator", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_records_manager", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_security_officer", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_power_user", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_user", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);

        checkCapability(AuthenticationUtil.getSystemUserName(), record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("rm_administrator", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("rm_records_manager", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("rm_security_officer", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_power_user", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("rm_user", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);

        // check person with no access and add read and write
        // Filing

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);

        permissionService.setPermission(filePlan, "test_user", RMPermissionModel.VIEW_RECORDS, true);
        permissionService.setInheritParentPermissions(recordCategory_1, false);
        permissionService.setInheritParentPermissions(recordCategory_2, false);
        permissionService.setPermission(recordCategory_1, "test_user", RMPermissionModel.READ_RECORDS, true);
        permissionService.setPermission(recordCategory_2, "test_user", RMPermissionModel.READ_RECORDS, true);
        permissionService.setPermission(recordFolder_1, "test_user", RMPermissionModel.FILING, true);
        permissionService.setPermission(recordFolder_2, "test_user", RMPermissionModel.FILING, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);

        permissionService.setPermission(filePlan, "test_user", RMPermissionModel.DECLARE_RECORDS, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);

        permissionService.setPermission(filePlan, "test_user", RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);

        permissionService.deletePermission(filePlan, "test_user", RMPermissionModel.DECLARE_RECORDS);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);

        permissionService.setPermission(filePlan, "test_user", RMPermissionModel.DECLARE_RECORDS, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);

        permissionService.deletePermission(filePlan, "test_user", RMPermissionModel.VIEW_RECORDS);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);

        permissionService.setPermission(filePlan, "test_user", RMPermissionModel.VIEW_RECORDS, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);

        permissionService.deletePermission(recordFolder_1, "test_user", RMPermissionModel.FILING);
        permissionService.deletePermission(recordFolder_2, "test_user", RMPermissionModel.FILING);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);

        permissionService.setPermission(recordFolder_1, "test_user", RMPermissionModel.FILING, true);
        permissionService.setPermission(recordFolder_2, "test_user", RMPermissionModel.FILING, true);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);

        // check frozen

        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
        Map<String, Serializable> params = new HashMap<String, Serializable>(1);
        params.put(FreezeAction.PARAM_REASON, "one");
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "freeze", params);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);

        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
        params = new HashMap<String, Serializable>(1);
        params.put(FreezeAction.PARAM_REASON, "Two");
        recordsManagementActionService.executeRecordsManagementAction(record_2, "freeze", params);

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);

        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "unfreeze");
        recordsManagementActionService.executeRecordsManagementAction(record_2, "unfreeze");

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);

        // Check closed
        // should make no difference
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "closeRecordFolder");
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_2, "closeRecordFolder");

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);

        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "openRecordFolder");
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_2, "openRecordFolder");

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);

        // try accession

        AuthenticationUtil.setFullyAuthenticatedUser("test_user");
        recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "accession", null);
        try
        {
            recordsManagementActionService.executeRecordsManagementAction(recordFolder_2, "accession", null);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
        try
        {
            recordsManagementActionService.executeRecordsManagementAction(record_1, "accession", null);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
        recordsManagementActionService.executeRecordsManagementAction(record_2, "accession", null);

        // check protected properties

        // PROP_DISPOSITION_ACTION_STARTED_AT
        // PROP_DISPOSITION_ACTION_STARTED_BY
        // PROP_DISPOSITION_ACTION_COMPLETED_AT
        // PROP_DISPOSITION_ACTION_COMPLETED_BY

        try
        {
            publicNodeService.setProperty(record_1, RecordsManagementModel.PROP_DISPOSITION_ACTION_STARTED_AT, true);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }

        try
        {
            publicNodeService.setProperty(record_1, RecordsManagementModel.PROP_DISPOSITION_ACTION_STARTED_BY, true);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }

        try
        {
            publicNodeService.setProperty(record_1, RecordsManagementModel.PROP_DISPOSITION_ACTION_COMPLETED_AT, true);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }

        try
        {
            publicNodeService.setProperty(record_1, RecordsManagementModel.PROP_DISPOSITION_ACTION_COMPLETED_BY, true);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }

        // check cutoff again (it is already cut off)

        try
        {
            recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "accession", null);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
        catch(AlfrescoRuntimeException are)
        {
            
        }
        try
        {
            recordsManagementActionService.executeRecordsManagementAction(record_2, "accession", null);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
        catch(AlfrescoRuntimeException are)
        {
            
        }

        checkCapability("test_user", recordFolder_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);
        checkCapability("test_user", record_1, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", recordFolder_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.DENIED);
        checkCapability("test_user", record_2, RMPermissionModel.AUTHORIZE_NOMINATED_TRANSFERS, AccessStatus.ALLOWED);

        // check each action

        TransferAction transfer = (TransferAction) ctx.getBean("accession");
        assertFalse(transfer.isExecutable(recordFolder_1, null));
        assertFalse(transfer.isExecutable(record_1, null));
        assertFalse(transfer.isExecutable(recordFolder_2, null));
        assertFalse(transfer.isExecutable(record_2, null));

        TransferCompleteAction transferComplete = (TransferCompleteAction) ctx.getBean("accessionComplete");
        assertTrue(transferComplete.isExecutable(recordFolder_1, null));
        assertFalse(transferComplete.isExecutable(record_1, null));
        assertFalse(transferComplete.isExecutable(recordFolder_2, null));
        assertTrue(transferComplete.isExecutable(record_2, null));

        // try and complete the transfer

        AuthenticationUtil.setFullyAuthenticatedUser("test_user");
        recordsManagementActionService.executeRecordsManagementAction(getTranferObject(recordFolder_1), "accessionComplete", null);
        try
        {
            recordsManagementActionService.executeRecordsManagementAction(recordFolder_2, "accessionComplete", null);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
        try
        {
            recordsManagementActionService.executeRecordsManagementAction(record_1, "accessionComplete", null);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
        try
        {
            // will fail as this is in the same transafer which is now done.
            recordsManagementActionService.executeRecordsManagementAction(getTranferObject(record_2), "accessionComplete", null);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }

        // try again - should fail

        try
        {
            recordsManagementActionService.executeRecordsManagementAction(recordFolder_1, "accessionComplete", null);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
        try
        {
            recordsManagementActionService.executeRecordsManagementAction(record_2, "accessionComplete", null);
            fail();
        }
        catch (AccessDeniedException ade)
        {

        }
    }

    public void testChangeOrDeleteReferencesCapability()
    {
        // capability is checked above - just check permission assignments
        checkPermission(AuthenticationUtil.getSystemUserName(), filePlan, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.ALLOWED);
        checkPermission("rm_administrator", filePlan, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.ALLOWED);
        checkPermission("rm_records_manager", filePlan, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.ALLOWED);
        checkPermission("rm_security_officer", filePlan, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.DENIED);
        checkPermission("rm_power_user", filePlan, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.DENIED);
        checkPermission("rm_user", filePlan, RMPermissionModel.CHANGE_OR_DELETE_REFERENCES, AccessStatus.DENIED);
    }

    public void testCloseFoldersCapability()
    {

    }

    public void testCreateAndAssociateSelectionListsCapability()
    {

    }

    public void testCreateModifyDestroyClassificationGuidesCapability()
    {

    }

    public void testCreateModifyDestroyEventsCapability()
    {

    }

    public void testCreateModifyDestroyFileplanMetadataCapability()
    {

    }

    public void testCreateModifyDestroyFileplanTypesCapability()
    {

    }

    public void testCreateModifyDestroyFoldersCapability()
    {

    }

    public void testCreateModifyDestroyRecordTypesCapability()
    {

    }

    public void testCreateModifyDestroyReferenceTypesCapability()
    {

    }

    public void testCreateModifyDestroyRolesCapability()
    {

    }

    public void testCreateModifyDestroyTimeframesCapability()
    {

    }

    public void testCreateModifyDestroyUsersAndGroupsCapability()
    {

    }

    public void testCreateModifyRecordsInCuttoffFoldersCapability()
    {

    }

    public void testCycleVitalRecordsCapability()
    {

    }

    public void testDeclareAuditAsRecordCapability()
    {

    }

    public void testDeclareRecordsCapability()
    {

    }

    public void testDeclareRecordsInClosedFoldersCapability()
    {

    }

    public void testDeleteAuditCapability()
    {

    }

    public void testDeleteLinksCapability()
    {

    }

    public void testDeleteRecordsCapability()
    {

    }

    public void testDestroyRecordsCapability()
    {

    }

    public void testDestroyRecordsScheduledForDestructionCapability()
    {

    }

    public void testDisplayRightsReportCapability()
    {

    }

    public void testEditDeclaredRecordMetadataCapability()
    {

    }

    public void testEditNonRecordMetadataCapability()
    {

    }

    public void testEditRecordMetadataCapability()
    {

    }

    public void testEditSelectionListsCapability()
    {

    }

    public void testEnableDisableAuditByTypesCapability()
    {

    }

    public void testExportAuditCapability()
    {

    }

    public void testExtendRetentionPeriodOrFreezeCapability()
    {

    }

    public void testFileRecordsCapability()
    {

    }

    public void testMakeOptionalPropertiesMandatoryCapability()
    {

    }

    public void testManageAccessControlsCapability()
    {

    }

    public void testManageAccessRightsCapability()
    {

    }

    public void testManuallyChangeDispositionDatesCapability()
    {

    }

    public void testMapClassificationGuideMetadataCapability()
    {

    }

    public void testMapEmailMetadataCapability()
    {

    }

    public void testMoveRecordsCapability()
    {

    }

    public void testPasswordControlCapability()
    {

    }

    public void testPlanningReviewCyclesCapability()
    {

    }

    public void testReOpenFoldersCapability()
    {

    }

    public void testSelectAuditMetadataCapability()
    {

    }

    public void testTriggerAnEventCapability()
    {

    }

    public void testUndeclareRecordsCapability()
    {

    }

    public void testUnfreezeCapability()
    {

    }

    public void testUpdateClassificationDatesCapability()
    {

    }

    public void testUpdateExemptionCategoriesCapability()
    {

    }

    public void testUpdateTriggerDatesCapability()
    {

    }

    public void testUpdateVitalRecordCycleInformationCapability()
    {

    }

    public void testUpgradeDowngradeAndDeclassifyRecordsCapability()
    {

    }

    public void testViewRecordsCapability()
    {

    }

    public void testViewUpdateReasonsForFreezeCapability()
    {

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
