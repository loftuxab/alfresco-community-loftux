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

import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.module.org_alfresco_module_dod5015.audit.RecordsManagementAuditEntry;
import org.alfresco.module.org_alfresco_module_dod5015.audit.RecordsManagementAuditQueryParameters;
import org.alfresco.module.org_alfresco_module_dod5015.audit.RecordsManagementAuditService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;

/**
 * @see RecordsManagementAuditService
 * 
 * @author Derek Hulley
 * @since 3.2
 */
public class RecordsManagementAuditServiceImplTest extends TestCase 
{
    private ApplicationContext ctx;
    
    private ServiceRegistry serviceRegistry;
    private TransactionService transactionService;
    private RetryingTransactionHelper txnHelper;
    private RecordsManagementService rmService;
    private RecordsManagementAuditService rmAuditService;

    private Date testStartTime;
    @SuppressWarnings("unused")
    private NodeRef filePlan;
    
    @Override
    protected void setUp() throws Exception 
    {
        testStartTime = new Date();
        ctx = ApplicationContextHelper.getApplicationContext();

        this.serviceRegistry = (ServiceRegistry) ctx.getBean(ServiceRegistry.SERVICE_REGISTRY);
        this.transactionService = serviceRegistry.getTransactionService();
        this.txnHelper = transactionService.getRetryingTransactionHelper();
        
        this.rmService = (RecordsManagementService) ctx.getBean("RecordsManagementService");
        this.rmAuditService = (RecordsManagementAuditService) ctx.getBean("RecordsManagementAuditService");

        // Set the current security context as admin
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        
        RetryingTransactionCallback<NodeRef> setUpCallback = new RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                NodeRef nodeRef = TestUtilities.loadFilePlanData(
                        null,
                        serviceRegistry.getNodeService(),
                        serviceRegistry.getImporterService(),
                        serviceRegistry.getPermissionService());
                // Do some stuff
                rmService.getRecordsManagementRoot(nodeRef);
                return nodeRef;
            }
        };
        filePlan = txnHelper.doInTransaction(setUpCallback);
    }
    
    @Override
    protected void tearDown()
    {
        AuthenticationUtil.clearCurrentSecurityContext();
    }
    
    public void testQuery_All()
    {
        RetryingTransactionCallback<List<RecordsManagementAuditEntry>> testCallback =
            new RetryingTransactionCallback<List<RecordsManagementAuditEntry>>()
        {
            public List<RecordsManagementAuditEntry> execute() throws Throwable
            {
                RecordsManagementAuditQueryParameters params = new RecordsManagementAuditQueryParameters();
                List<RecordsManagementAuditEntry> entries = rmAuditService.getAuditTrail(params);
                return entries;
            }
        };
        txnHelper.doInTransaction(testCallback);
    }
    
    public void testQuery_UserLimited()
    {
        final int limit = 1;
        final String user = AuthenticationUtil.getAdminUserName();        // The user being tested
        
        RetryingTransactionCallback<List<RecordsManagementAuditEntry>> testCallback =
            new RetryingTransactionCallback<List<RecordsManagementAuditEntry>>()
        {
            public List<RecordsManagementAuditEntry> execute() throws Throwable
            {
                RecordsManagementAuditQueryParameters params = new RecordsManagementAuditQueryParameters();
                params.setUser(user);
                params.setMaxEntries(limit);
                List<RecordsManagementAuditEntry> entries = rmAuditService.getAuditTrail(params);
                return entries;
            }
        };
        List<RecordsManagementAuditEntry> entries = txnHelper.doInTransaction(testCallback);
        assertNotNull(entries);
        assertEquals("Expected results to be limited", limit, entries.size());
    }
    
    public void testQuery_Node()
    {
        RetryingTransactionCallback<List<RecordsManagementAuditEntry>> allResultsCallback =
            new RetryingTransactionCallback<List<RecordsManagementAuditEntry>>()
        {
            public List<RecordsManagementAuditEntry> execute() throws Throwable
            {
                RecordsManagementAuditQueryParameters params = new RecordsManagementAuditQueryParameters();
                params.setDateFrom(testStartTime);
                List<RecordsManagementAuditEntry> entries = rmAuditService.getAuditTrail(params);
                return entries;
            }
        };
        List<RecordsManagementAuditEntry> entries = txnHelper.doInTransaction(allResultsCallback);
        assertNotNull("Expect a list of results for the query", entries);
        
        // Find all results for a given node
        NodeRef chosenNodeRef = null;
        int count = 0;
        for (RecordsManagementAuditEntry entry : entries)
        {
            NodeRef nodeRef = entry.getNodeRef();
            assertNotNull("Found ntry with null nodeRef: " + entry, nodeRef);
            if (chosenNodeRef == null)
            {
                chosenNodeRef = nodeRef;
                count++;
            }
            else if (nodeRef.equals(chosenNodeRef))
            {
                count++;
            }
        }
        
        final NodeRef chosenNodeRefFinal = chosenNodeRef;
        // Now search again, but for the chosen node
        RetryingTransactionCallback<List<RecordsManagementAuditEntry>> nodeResultsCallback =
            new RetryingTransactionCallback<List<RecordsManagementAuditEntry>>()
        {
            public List<RecordsManagementAuditEntry> execute() throws Throwable
            {
                RecordsManagementAuditQueryParameters params = new RecordsManagementAuditQueryParameters();
                params.setDateFrom(testStartTime);
                params.setNodeRef(chosenNodeRefFinal);
                List<RecordsManagementAuditEntry> entries = rmAuditService.getAuditTrail(params);
                return entries;
            }
        };
        entries = txnHelper.doInTransaction(nodeResultsCallback);
        assertNotNull("Expect a list of results for the query", entries);
        assertTrue("No results were found for node: " + chosenNodeRefFinal, entries.size() > 0);
        assertEquals("Incorrect number of results for node: " + chosenNodeRefFinal, count, entries.size());
    }
}
