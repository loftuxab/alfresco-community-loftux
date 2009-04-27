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

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.view.ImporterBinding;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.BaseSpringTest;

/**
 * 
 * 
 * @author Roy Wetherall
 */
public class DODDataLoadSystemTest extends BaseSpringTest 
{    
	protected static StoreRef SPACES_STORE = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
	
	private NodeService nodeService;
	private AuthenticationComponent authenticationComponent;
	private ImporterService importer;
	
	@Override
	protected void onSetUpInTransaction() throws Exception 
	{
		super.onSetUpInTransaction();

		// Get the service required in the tests
		this.nodeService = (NodeService)this.applicationContext.getBean("NodeService");
		this.authenticationComponent = (AuthenticationComponent)this.applicationContext.getBean("authenticationComponent");
		this.importer = (ImporterService)this.applicationContext.getBean("ImporterService");
		
		// Set the current security context as admin
		this.authenticationComponent.setCurrentUser(AuthenticationUtil.getSystemUserName());		
	}
        
	public void testLoadFilePlanData()
	{
	    loadFilePlanData(null, false);
	}
	
	public void loadFilePlanData(String siteName, boolean bCommit)
	{
	    NodeRef filePlan = null;
	    
	    // If no siteName is provided create a filePlan in a well known location
	    if (siteName == null)
	    {
	        // For now creating the filePlan beneth the
	        NodeRef rootNode = this.nodeService.getRootNode(SPACES_STORE);
	        filePlan = this.nodeService.createNode(
	                                    rootNode, 
	                                    ContentModel.ASSOC_CHILDREN, 
	                                    QName.createQName(RecordsManagementModel.RM_URI, "filePlan"), 
	                                    RecordsManagementModel.TYPE_FILE_PLAN).getChildRef();
	    }
	    else
	    {
	        // Find the file plan in the site provided
	        // TODO
	    }
	    
	    // Do the data load into the the provided filePlan node reference
	    // TODO ...
	    InputStream is = this.getClass().getClassLoader().getResourceAsStream("alfresco/module/org_alfresco_module_dod5015/bootstrap/DODExampleFilePlan.xml");
	    assertNotNull("The DODExampleFilePlan.xml import file could not be found", is);
	    Reader viewReader = new InputStreamReader(is);
	    Location location = new Location(filePlan);
	    importer.importView(viewReader, location, REPLACE_BINDING, null);
	    
	    // Commit the uploaded data if asked to
	    // TODO ...
	}
	
	// TODO .. do we need to redecalre this here ??
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
