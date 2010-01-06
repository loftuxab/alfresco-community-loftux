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
package org.alfresco.module.org_alfresco_module_dod5015.test.webscript;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.test.TestUtilities;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.springframework.extensions.webscripts.TestWebScriptServer.GetRequest;

/**
 * This class tests the Rest API for disposition related operations
 * 
 * @author Roy Wetherall
 */
public class BootstraptestDataRestApiTest extends BaseWebScriptTest implements RecordsManagementModel
{
    protected static StoreRef SPACES_STORE = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
    protected static final String URL = "/api/rma/bootstraptestdata";
    protected static final String SERVICE_URL_PREFIX = "/alfresco/service";
    protected static final String APPLICATION_JSON = "application/json";
    
    protected NodeService nodeService;
    protected RetryingTransactionHelper transactionHelper;
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        nodeService = (NodeService) getServer().getApplicationContext().getBean("NodeService");
        transactionHelper = (RetryingTransactionHelper)getServer().getApplicationContext().getBean("retryingTransactionHelper");           
    }   

    public void testBoostrapTestData() throws Exception
    {
        final NodeRef filePlan = transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());             
                return TestUtilities.loadFilePlanData(getServer().getApplicationContext(), false, true);
            }           
        });      
        
        sendRequest(new GetRequest(URL), 200);
        
        transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<NodeRef>()
        {
            public NodeRef execute() throws Throwable
            {
                AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getSystemUserName());
                nodeService.deleteNode(filePlan);
                return null;
            }           
        });  
    }
    
}
