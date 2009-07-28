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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementAction;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.BaseSpringTest;

/**
 * 
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementActionServiceImplTest extends BaseSpringTest implements RecordsManagementModel
{    
    protected static StoreRef SPACES_STORE = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
    
	private NodeService nodeService;
	private RecordsManagementActionService rmActionService;

	private NodeRef nodeRef;
	private List<NodeRef> nodeRefs;
	
	@Override
    protected String[] getConfigLocations()
    {
        return new String[] { "classpath:alfresco/application-context.xml", "classpath:org/alfresco/module/org_alfresco_module_dod5015/test/test-context.xml" };
    }
	
	@Override
	protected void onSetUpInTransaction() throws Exception 
	{
		super.onSetUpInTransaction();

		// Get the service required in the tests
		this.nodeService = (NodeService)this.applicationContext.getBean("NodeService"); 
		this.rmActionService = (RecordsManagementActionService)this.applicationContext.getBean("RecordsManagementActionService");

		// Set the current security context as admin
		AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
		
		// Create a node we can use for the tests
		NodeRef rootNodeRef = this.nodeService.getRootNode(SPACES_STORE);
		this.nodeRef = this.nodeService.createNode(
		        rootNodeRef, 
		        ContentModel.ASSOC_CHILDREN, 
		        QName.createQName(NamespaceService.CONTENT_MODEL_PREFIX, "temp.txt"), 
		        ContentModel.TYPE_CONTENT).getChildRef();
		
		// Create nodeRef list
		this.nodeRefs = new ArrayList<NodeRef>(5);
		for (int i = 0; i < 5; i++)
        {
		    this.nodeRefs.add(
		            this.nodeService.createNode(
		                    rootNodeRef, 
		                    ContentModel.ASSOC_CHILDREN, 
		                    QName.createQName(NamespaceService.CONTENT_MODEL_PREFIX, "temp.txt"), 
		                    ContentModel.TYPE_CONTENT).getChildRef());
        }
	}
	
	public void testGetActions()
	{
	    List<RecordsManagementAction> result = this.rmActionService.getRecordsManagementActions();
	    assertNotNull(result);
	    Map<String, RecordsManagementAction> resultMap = new HashMap<String, RecordsManagementAction>(8);
	    for (RecordsManagementAction action : result)
        {
	        resultMap.put(action.getName(), action);
        }
	    
	    assertTrue(resultMap.containsKey(TestAction.NAME));
        assertTrue(resultMap.containsKey(TestAction2.NAME));
	    
	    result = this.rmActionService.getDispositionActions();
	    resultMap = new HashMap<String, RecordsManagementAction>(8);
        for (RecordsManagementAction action : result)
        {
            resultMap.put(action.getName(), action);
        }
	    assertTrue(resultMap.containsKey(TestAction.NAME));
	    assertFalse(resultMap.containsKey(TestAction2.NAME));
	    
	    // get some specific actions and check the label
	    RecordsManagementAction cutoff = this.rmActionService.getDispositionAction("cutoff");
	    assertNotNull(cutoff);
	    assertEquals("Cutoff", cutoff.getLabel());
	    assertEquals("Cutoff", cutoff.getDescription());
	    
	    RecordsManagementAction freeze = this.rmActionService.getRecordsManagementAction("freeze");
        assertNotNull(freeze);
        assertEquals("Freeze", freeze.getLabel());
        assertEquals("Freeze", freeze.getLabel());
        
        // test non-existent actions
        assertNull(this.rmActionService.getDispositionAction("notThere"));
        assertNull(this.rmActionService.getRecordsManagementAction("notThere"));
	}
	
	public void testExecution()
	{
	    assertFalse(this.nodeService.hasAspect(this.nodeRef, ASPECT_RECORD));
	    Map<String, Serializable> params = new HashMap<String, Serializable>(1);
	    params.put(TestAction.PARAM, TestAction.PARAM_VALUE);
	    this.rmActionService.executeRecordsManagementAction(this.nodeRef, TestAction.NAME, params);
	    assertTrue(this.nodeService.hasAspect(this.nodeRef, ASPECT_RECORD));
	}
	
	public void testBulkExecution()
	{
	    for (NodeRef nodeRef : this.nodeRefs)
        {
	        assertFalse(this.nodeService.hasAspect(nodeRef, ASPECT_RECORD));
        }
	    
	    Map<String, Serializable> params = new HashMap<String, Serializable>(1);
        params.put(TestAction.PARAM, TestAction.PARAM_VALUE);
        this.rmActionService.executeRecordsManagementAction(this.nodeRefs, TestAction.NAME, params);    
	    
	    for (NodeRef nodeRef : this.nodeRefs)
        {
            assertTrue(this.nodeService.hasAspect(nodeRef, ASPECT_RECORD));
        }
	}
}
