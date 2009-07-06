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

import java.util.List;

import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.event.RecordsManagementEvent;
import org.alfresco.module.org_alfresco_module_dod5015.event.RecordsManagementEventService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.util.BaseSpringTest;

/**
 * Event service implementation unit test
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementEventServiceTestImpl extends BaseSpringTest implements RecordsManagementModel
{    
    protected static StoreRef SPACES_STORE = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
    
	private NodeService nodeService;
	private RecordsManagementEventService rmEventService;

	private NodeRef nodeRef;
	private List<NodeRef> nodeRefs;
	
	//@Override
    //protected String[] getConfigLocations()
    //{
    //    return new String[] { "classpath:alfresco/application-context.xml", "classpath:org/alfresco/module/org_alfresco_module_dod5015/test/test-context.xml" };
    //}
	
	@Override
	protected void onSetUpInTransaction() throws Exception 
	{
		super.onSetUpInTransaction();

		// Get the service required in the tests
		this.nodeService = (NodeService)this.applicationContext.getBean("NodeService"); 
		this.rmEventService = (RecordsManagementEventService)this.applicationContext.getBean("RecordsManagementEventService");

		// Set the current security context as admin
		AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
	}
	
	public void testGetEventTypes()
	{
	    List<String> eventTypes = this.rmEventService.getEventTypes();
	    assertNotNull(eventTypes);
	    for (String eventType : eventTypes)
        {
            System.out.println(eventType);
        }
	}
	
	public void testGetEvents()
	{
	    List<RecordsManagementEvent> events = this.rmEventService.getEvents();
	    assertNotNull(events);
	    for (RecordsManagementEvent event : events)
        {
            System.out.println(event.getName());
        }
	}
	
	public void testAddRemoveEvents()
	{
	    List<RecordsManagementEvent> events = this.rmEventService.getEvents();
	    assertNotNull(events);
	    assertFalse(containsEvent(events, "myEvent"));
	    
	    this.rmEventService.addEvent("rmEventType.simple", "myEvent", "My Event");
	    
	    events = this.rmEventService.getEvents();
        assertNotNull(events);
        assertTrue(containsEvent(events, "myEvent"));
        
        this.rmEventService.removeEvent("myEvent");
        
        events = this.rmEventService.getEvents();
        assertNotNull(events);
        assertFalse(containsEvent(events, "myEvent"));               
	}
	
	private boolean containsEvent(List<RecordsManagementEvent> events, String eventName)
	{
	    boolean result = false;
	    for (RecordsManagementEvent event : events)
        {
            if (eventName.equals(event.getName()) == true)
            {
                result = true;
                break;
            }
        }
	    return result;
	}
}
