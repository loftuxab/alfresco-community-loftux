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
import java.util.Locale;
import java.util.Map;

import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.alfresco.i18n.I18NUtil;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.DOD5015Model;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.module.org_alfresco_module_dod5015.capability.Capability;
import org.alfresco.module.org_alfresco_module_dod5015.capability.RMPermissionModel;
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
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ContentService;
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
        
        recordsManagementService = (RecordsManagementService)ctx.getBean("RecordsManagementService");

        testTX = transactionService.getUserTransaction();
        testTX.begin();
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());

        StoreRef storeRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        rootNodeRef = nodeService.getRootNode(storeRef);

        filePlan = nodeService.createNode(rootNodeRef, ContentModel.ASSOC_CHILDREN, DOD5015Model.TYPE_FILE_PLAN, DOD5015Model.TYPE_FILE_PLAN).getChildRef();

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
        
    }
    
    public void testConfig()
    {
        assertEquals(6, recordsManagementService.getProtectedAspects().size());
        assertEquals(13, recordsManagementService.getProtectedProperties().size());
    }
    
    public void testFilePlan_system()
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
    
    public void testFilePlan_administrator()
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
    
    
    public void testFilePlan_records_manager()
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
    public void testFilePlan_security_officer()
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
    
    public void testFilePlan_power_user()
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
    
    public void testFilePlan_user()
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
