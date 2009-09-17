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

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.util.BaseSpringTest;

/**
 * 
 * 
 * @author Roy Wetherall
 */
public class DODDataLoadSystemTest extends BaseSpringTest 
{    
	private NodeService nodeService;
	private AuthenticationComponent authenticationComponent;
	private ImporterService importer;
    private PermissionService permissionService;
    private SearchService searchService;
    private RecordsManagementService rmService;
    private RecordsManagementActionService rmActionService;
	
	@Override
	protected void onSetUpInTransaction() throws Exception 
	{
		super.onSetUpInTransaction();

		// Get the service required in the tests
		this.nodeService = (NodeService)this.applicationContext.getBean("NodeService");
		this.authenticationComponent = (AuthenticationComponent)this.applicationContext.getBean("authenticationComponent");
		this.importer = (ImporterService)this.applicationContext.getBean("ImporterService");
		this.permissionService = (PermissionService)this.applicationContext.getBean("PermissionService");
		searchService = (SearchService)applicationContext.getBean("SearchService");
		rmService = (RecordsManagementService)applicationContext.getBean("RecordsManagementService");
        rmActionService = (RecordsManagementActionService)applicationContext.getBean("RecordsManagementActionService");
		
		
		// Set the current security context as admin
		this.authenticationComponent.setCurrentUser(AuthenticationUtil.getSystemUserName());		
	}

    public void testSetup()
    {
        // NOOP
    }
    
	public void testLoadFilePlanData()
	{
	    TestUtilities.loadFilePlanData(null, nodeService, importer, permissionService, searchService, rmService, rmActionService);
	    
	    setComplete();
        endTransaction();
	}
}
