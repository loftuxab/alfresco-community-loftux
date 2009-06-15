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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.DispositionAction;
import org.alfresco.module.org_alfresco_module_dod5015.DispositionInstructions;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.BaseSpringTest;

/**
 * 
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementServiceTestImpl extends BaseSpringTest implements RecordsManagementModel
{    
	protected static StoreRef SPACES_STORE = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
	
	private NodeRef filePlan;
	
	private NodeService nodeService;
	private ImporterService importService;
	private TransactionService transactionService;
	private RecordsManagementService rmService;
	private SearchService searchService;
	
	@Override
	protected void onSetUpInTransaction() throws Exception 
	{
		super.onSetUpInTransaction();

		// Get the service required in the tests
		this.nodeService = (NodeService)this.applicationContext.getBean("NodeService"); 
		this.importService = (ImporterService)this.applicationContext.getBean("importerComponent");
		this.transactionService = (TransactionService)this.applicationContext.getBean("TransactionService");
		this.searchService = (SearchService)this.applicationContext.getBean("searchService");
		this.rmService = (RecordsManagementService)this.applicationContext.getBean("recordsManagementService");

		// Set the current security context as admin
		AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
		
		// Get the test data
		setUpTestData();    
	}
	
	private void setUpTestData()
	{
        filePlan = TestUtilities.loadFilePlanData(null, this.nodeService, this.importService);
	}

    @Override
    protected void onTearDownInTransaction() throws Exception
    {
        try
        {
            UserTransaction txn = transactionService.getUserTransaction(false);
            txn.begin();
            this.nodeService.deleteNode(filePlan);
            txn.commit();
        }
        catch (Exception e)
        {
            // Nothing
            //System.out.println("DID NOT DELETE FILE PLAN!");
        }
    }
    
	public void testGetDispositionInstructions() throws Exception
	{	
	    // Get a record
	    // TODO
	    
	    // Get a record folder
	    NodeRef folderRecord = TestUtilities.getRecordFolder(searchService, "Reports", "AIS Audit Records", "January AIS Audit Records");
	    assertNotNull(folderRecord);
	    assertEquals("January AIS Audit Records", this.nodeService.getProperty(folderRecord, ContentModel.PROP_NAME));
	    
	    assertFalse(rmService.isRecord(folderRecord));
	    assertTrue(rmService.isRecordFolder(folderRecord));
	    assertFalse(rmService.isRecordsManagementContainer(folderRecord));	 
	    
	    DispositionInstructions di = this.rmService.getDispositionInstructions(folderRecord);
	    assertNotNull(di);
	    assertEquals("N1-218-00-4 item 023", di.getDispositionAuthority());
	    assertEquals("Cut off monthly, hold 1 month, then destroy.", di.getDispositionInstructions());
	    assertFalse(di.isRecordLevelDisposition());
	    
	    // Get a record category
	    NodeRef recordCategory = TestUtilities.getRecordCategory(this.searchService, "Reports", "AIS Audit Records");    
	    assertNotNull(recordCategory);
	    assertEquals("AIS Audit Records", this.nodeService.getProperty(recordCategory, ContentModel.PROP_NAME));
        	    
	    assertFalse(rmService.isRecord(recordCategory));
        assertFalse(rmService.isRecordFolder(recordCategory));
        assertTrue(rmService.isRecordsManagementContainer(recordCategory));   
        
        di = this.rmService.getDispositionInstructions(recordCategory);
        assertNotNull(di);
        assertEquals("N1-218-00-4 item 023", di.getDispositionAuthority());
        assertEquals("Cut off monthly, hold 1 month, then destroy.", di.getDispositionInstructions());
        assertFalse(di.isRecordLevelDisposition());
        
        List<DispositionAction> das = di.getDispositionActions();
        assertNotNull(das);
        assertEquals(2, das.size());
        assertEquals("cutoff", das.get(0).getName());
        assertEquals("destroy", das.get(1).getName());
    }
    
	public void testUpdateNextDispositionAction()
	{
	    // Get a record folder
        NodeRef folderRecord = TestUtilities.getRecordFolder(searchService, "Reports", "AIS Audit Records", "January AIS Audit Records");
        assertNotNull(folderRecord);
        assertEquals("January AIS Audit Records", this.nodeService.getProperty(folderRecord, ContentModel.PROP_NAME));
        
        DispositionInstructions di = this.rmService.getDispositionInstructions(folderRecord);
        assertNotNull(di);
        assertEquals("N1-218-00-4 item 023", di.getDispositionAuthority());
        assertEquals("Cut off monthly, hold 1 month, then destroy.", di.getDispositionInstructions());
        assertFalse(di.isRecordLevelDisposition());
        
        assertFalse(this.nodeService.hasAspect(folderRecord, ASPECT_DISPOSITION_SCHEDULE));
        
        this.rmService.updateNextDispositionAction(folderRecord);
        
        assertTrue(this.nodeService.hasAspect(folderRecord, ASPECT_DISPOSITION_SCHEDULE));
        assertNull(this.nodeService.getProperty(folderRecord, PROP_PREVIOUS_DISPOSITION_DISPOSITION_ACTION));
        assertNull(this.nodeService.getProperty(folderRecord, PROP_PREVIOUS_DISPOSITION_DISPOSITION_DATE));
        assertEquals("cutoff", this.nodeService.getProperty(folderRecord, PROP_DISPOSITION_ACTION));
        assertEquals(di.getDispositionActions().get(0).getId(), this.nodeService.getProperty(folderRecord, PROP_DISPOSITION_ACTION_ID));
        assertNotNull(this.nodeService.getProperty(folderRecord, PROP_DISPOSITION_AS_OF));
        
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
        props.put(PROP_CUT_OFF_DATE, new Date());
        this.nodeService.addAspect(folderRecord, ASPECT_CUT_OFF, props);        
        this.rmService.updateNextDispositionAction(folderRecord);
        
        assertTrue(this.nodeService.hasAspect(folderRecord, ASPECT_DISPOSITION_SCHEDULE));
        assertEquals("cutoff", this.nodeService.getProperty(folderRecord, PROP_PREVIOUS_DISPOSITION_DISPOSITION_ACTION));
        assertNotNull(this.nodeService.getProperty(folderRecord, PROP_PREVIOUS_DISPOSITION_DISPOSITION_DATE));
        assertEquals("destroy", this.nodeService.getProperty(folderRecord, PROP_DISPOSITION_ACTION));
        assertEquals(di.getDispositionActions().get(1).getId(), this.nodeService.getProperty(folderRecord, PROP_DISPOSITION_ACTION_ID));
        assertNotNull(this.nodeService.getProperty(folderRecord, PROP_DISPOSITION_AS_OF));
        
        this.rmService.updateNextDispositionAction(folderRecord);
        
        assertTrue(this.nodeService.hasAspect(folderRecord, ASPECT_DISPOSITION_SCHEDULE));
        assertEquals("destroy", this.nodeService.getProperty(folderRecord, PROP_PREVIOUS_DISPOSITION_DISPOSITION_ACTION));
        assertNotNull(this.nodeService.getProperty(folderRecord, PROP_PREVIOUS_DISPOSITION_DISPOSITION_DATE));
        assertNull(this.nodeService.getProperty(folderRecord, PROP_DISPOSITION_ACTION));
        assertNull(this.nodeService.getProperty(folderRecord, PROP_DISPOSITION_ACTION_ID));
        assertNull(this.nodeService.getProperty(folderRecord, PROP_DISPOSITION_AS_OF));
	}
	
}
