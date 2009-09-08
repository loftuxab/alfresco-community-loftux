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

import java.io.File;

import junit.framework.TestCase;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
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

	@SuppressWarnings("unused")
    private NodeRef filePlan;
	
	@Override
	protected void setUp() throws Exception 
	{
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
	    RetryingTransactionCallback<Void> testCallback = new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                RecordsManagementAuditQueryParameters params = new RecordsManagementAuditQueryParameters();
                @SuppressWarnings("unused")
                File auditTrail = rmAuditService.getAuditTrail(params);
                return null;
            }
        };
        txnHelper.doInTransaction(testCallback);
	}
}
