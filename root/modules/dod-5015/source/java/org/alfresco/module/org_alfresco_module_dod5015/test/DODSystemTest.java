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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementActionService;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.BaseSpringTest;
import org.alfresco.util.ISO9075;

/**
 * 
 * 
 * @author Roy Wetherall
 */
public class DODSystemTest extends BaseSpringTest implements RecordsManagementModel
{    
	protected static StoreRef SPACES_STORE = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
	
	private NodeRef filePlan;
	
	private NodeService nodeService;
	private SearchService searchService;
	private ImporterService importService;
	private ContentService contentService;
	private RecordsManagementActionService rmService;
	private TransactionService transactionService;
	
	private AuthenticationComponent authenticationComponent;
	
	// base test data for supplemental markings list (see also recordsModel.xml)
	protected final static String NOFORN     = "NOFORN";     // Not Releasable to Foreign Nationals/Governments/Non-US Citizens
	protected final static String NOCONTRACT = "NOCONTRACT"; // Not Releasable to Contractors or Contractor/Consultants
	protected final static String FOUO       = "FOUO";       // For Official Use Only 
	
	@Override
	protected void onSetUpInTransaction() throws Exception 
	{
		super.onSetUpInTransaction();

		// Get the service required in the tests
		this.nodeService = (NodeService)this.applicationContext.getBean("NodeService");
		this.authenticationComponent = (AuthenticationComponent)this.applicationContext.getBean("authenticationComponent");
		this.searchService = (SearchService)this.applicationContext.getBean("searchService");
		this.importService = (ImporterService)this.applicationContext.getBean("ImporterService");
		this.contentService = (ContentService)this.applicationContext.getBean("ContentService");
		this.rmService = (RecordsManagementActionService)this.applicationContext.getBean("RecordsManagementActionService");
		this.transactionService = (TransactionService)this.applicationContext.getBean("TransactionService");
		
		// Set the current security context as admin
		this.authenticationComponent.setCurrentUser(AuthenticationUtil.getAdminUserName());	
		
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
    
    @Override
    protected void onTearDownAfterTransaction() throws Exception
    {
        // TODO Auto-generated method stub
        super.onTearDownAfterTransaction();
    }
    
    public void testSetup()
    {
        // NOOP
    }
    
	public void testBasicFilingTest() throws Exception
	{	    
	    NodeRef recordCategory = getRecordCategory("Reports", "AIS Audit Records");    
	    assertNotNull(recordCategory);
	    assertEquals("AIS Audit Records", this.nodeService.getProperty(recordCategory, ContentModel.PROP_NAME));
        	    
	    Map<QName, Serializable> folderProps = new HashMap<QName, Serializable>(1);
	    folderProps.put(ContentModel.PROP_NAME, "March AIS Audit Records");
	    NodeRef recordFolder = this.nodeService.createNode(recordCategory, 
	                                                       ContentModel.ASSOC_CONTAINS, 
	                                                       QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "March AIS Audit Records"), 
	                                                       TYPE_RECORD_FOLDER).getChildRef();
	    
	    setComplete();
        endTransaction();
	    
        UserTransaction txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        // Check the folder to ensure everything has been inherited correctly
        assertTrue(((Boolean)this.nodeService.getProperty(recordFolder, PROP_VITAL_RECORD_INDICATOR)).booleanValue());
        assertEquals(this.nodeService.getProperty(recordCategory, PROP_REVIEW_PERIOD),
                     this.nodeService.getProperty(recordFolder, PROP_REVIEW_PERIOD));
	    
        // Create the document
	    Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
	    props.put(ContentModel.PROP_NAME, "MyRecord.txt");
	    NodeRef recordOne = this.nodeService.createNode(recordFolder, 
	                                                    ContentModel.ASSOC_CONTAINS, 
	                                                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "MyRecord.txt"), 
	                                                    ContentModel.TYPE_CONTENT).getChildRef();
	    
	    // Set the content
	    ContentWriter writer = this.contentService.getWriter(recordOne, ContentModel.PROP_CONTENT, true);
	    writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
	    writer.setEncoding("UTF-8");
	    writer.putContent("There is some content in this record");
	    
	    txn.commit();
        txn = transactionService.getUserTransaction(false);
        txn.begin();
        
	    // Checked that the document has been marked as incomplete
	    System.out.println("recordOne ...");
        assertTrue(this.nodeService.hasAspect(recordOne, ASPECT_UNDECLARED_RECORD));	   
	    assertTrue(this.nodeService.hasAspect(recordOne, ASPECT_RECORD));
        assertNotNull(this.nodeService.getProperty(recordOne, PROP_IDENTIFIER));
        System.out.println("Record id: " + this.nodeService.getProperty(recordOne, PROP_IDENTIFIER));
        assertNotNull(this.nodeService.getProperty(recordOne, PROP_DATE_FILED));
        System.out.println("Date filed: " + this.nodeService.getProperty(recordOne, PROP_DATE_FILED));
        
        // Check the review schedule
        assertTrue(this.nodeService.hasAspect(recordOne, ASPECT_VITAL_RECORD));
        assertNotNull(this.nodeService.getProperty(recordOne, PROP_REVIEW_AS_OF));
        System.out.println("Review as of: " + this.nodeService.getProperty(recordOne, PROP_REVIEW_AS_OF));

        // Check the disposition action
        assertTrue(this.nodeService.hasAspect(recordOne, ASPECT_DISPOSITION_SCHEDULE));
        assertNotNull(this.nodeService.getProperty(recordOne, PROP_DISPOSITION_ACTION_ID));
        System.out.println("Disposition action id: " + this.nodeService.getProperty(recordOne, PROP_DISPOSITION_ACTION_ID));
        assertEquals("cutoff", this.nodeService.getProperty(recordOne, PROP_DISPOSITION_ACTION));
        System.out.println("Disposition action: " + this.nodeService.getProperty(recordOne, PROP_DISPOSITION_ACTION));
        assertNotNull(this.nodeService.getProperty(recordOne, PROP_DISPOSITION_AS_OF));
        System.out.println("Disposition as of: " + this.nodeService.getProperty(recordOne, PROP_DISPOSITION_AS_OF));
        
	    // Test the declaration of a record by editing properties
        Map<QName, Serializable> propValues = this.nodeService.getProperties(recordOne);        
	    propValues.put(RecordsManagementModel.PROP_PUBLICATION_DATE, new Date());	    
	    List<String> smList = new ArrayList<String>(2);
        smList.add(FOUO);
        smList.add(NOFORN);
	    propValues.put(RecordsManagementModel.PROP_SUPPLEMENTAL_MARKING_LIST, (Serializable)smList);	    
	    propValues.put(RecordsManagementModel.PROP_MEDIA_TYPE, "mediaTypeValue"); 
	    propValues.put(RecordsManagementModel.PROP_FORMAT, "formatValue"); 
	    propValues.put(RecordsManagementModel.PROP_DATE_RECEIVED, new Date());
	    this.nodeService.setProperties(recordOne, propValues);
	    
        txn.commit(); 
        txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        assertTrue(this.nodeService.hasAspect(recordOne, ASPECT_UNDECLARED_RECORD));    
        
        propValues = this.nodeService.getProperties(recordOne);        
        propValues.put(RecordsManagementModel.PROP_ORIGINATOR, "origValue");
        propValues.put(RecordsManagementModel.PROP_ORIGINATING_ORGANIZATION, "origOrgValue");
        propValues.put(ContentModel.PROP_TITLE, "titleValue");
        this.nodeService.setProperties(recordOne, propValues);
        
        txn.commit(); 
        txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        // Assert that the record is no longer undeclared
        assertFalse(this.nodeService.hasAspect(recordOne, ASPECT_UNDECLARED_RECORD));
        
        // Execute the cutoff action
        this.rmService.executeRecordsManagementAction(recordOne, "cutoff", null);
        
        // Check the disposition action
        assertTrue(this.nodeService.hasAspect(recordOne, ASPECT_DISPOSITION_SCHEDULE));
        assertNotNull(this.nodeService.getProperty(recordOne, PROP_DISPOSITION_ACTION_ID));
        System.out.println("Disposition action id: " + this.nodeService.getProperty(recordOne, PROP_DISPOSITION_ACTION_ID));
        assertEquals("destroy", this.nodeService.getProperty(recordOne, PROP_DISPOSITION_ACTION));
        System.out.println("Disposition action: " + this.nodeService.getProperty(recordOne, PROP_DISPOSITION_ACTION));
        assertNotNull(this.nodeService.getProperty(recordOne, PROP_DISPOSITION_AS_OF));
        System.out.println("Disposition as of: " + this.nodeService.getProperty(recordOne, PROP_DISPOSITION_AS_OF));
        
        // Check the previous action details
        assertEquals("cutoff", this.nodeService.getProperty(recordOne, PROP_PREVIOUS_DISPOSITION_DISPOSITION_ACTION));
        assertNotNull(this.nodeService.getProperty(recordOne, PROP_PREVIOUS_DISPOSITION_DISPOSITION_DATE));
        System.out.println("Previous aciont date: " + this.nodeService.getProperty(recordOne, PROP_PREVIOUS_DISPOSITION_DISPOSITION_DATE).toString());
        
        // Execute the destroy action
        this.rmService.executeRecordsManagementAction(recordOne, "destroy", null);
        
        // Check that the node has been destroyed
        assertFalse(this.nodeService.exists(recordOne));
        
        txn.commit();
	}
	
	/**
	 * This test case reads several sample Record Folders from the filePlan to ensure
	 * that they have been imported correctly.
	 */
	public void testReadRecordFolders()
	{
        NodeRef recordFolder = TestUtilities.getRecordFolder(searchService, "Reports", "AIS Audit Records", "January AIS Audit Records");     
        assertNotNull(recordFolder);
        
        // Include this as it has brackets in its name.
        recordFolder = TestUtilities.getRecordFolder(searchService, "Miscellaneous Files", "Civilian Employee Training Program Records", "Chuck Stevens Training Records (2008)");
        assertNotNull(recordFolder);
        
        // Include this as it has a slash in its name.
        recordFolder = TestUtilities.getRecordFolder(searchService, "Miscellaneous Files", "Monthly Cockpit Crew Training", "January Cockpit Crew Training");     
        assertNotNull(recordFolder);
	}

	/**
	 * This test case reads all the record categories under the spaces store and asserts
	 * that each has a cm:description and that it is non-null and has non-whitespace
	 * content.
	 */
    public void testRecordCategoryDescriptions()
    {
        // See Table 2-1.8 in DoD 5015.02-STD v3 Baseline RMA Compliance Test Procedures
        List<NodeRef> recordCategories = this.getAllRecordCategories();
        assertNotNull(recordCategories);
        
        for (NodeRef recordCategory : recordCategories)
        {
            Map<QName, Serializable> props = nodeService.getProperties(recordCategory);
            final Serializable recCatDescription = props.get(ContentModel.PROP_DESCRIPTION);
            assertNotNull(recCatDescription);
            assertTrue(recCatDescription.toString().trim().length() > 0);
        }
        
        // This test formerly tested a single RecordCategory like so:
        // NodeRef recordCategory = this.getRecordCategory("Miscellaneous Files", "Civilian Employee Training Program Records");
    }
    
    /**
     * Gets all Record Categories under the SPACES_STORE.
     * @return
     */
    private List<NodeRef> getAllRecordCategories()
    {
        String typeQuery = "TYPE:\"" + RecordsManagementModel.TYPE_RECORD_CATEGORY + "\"";
        ResultSet types = this.searchService.query(SPACES_STORE, SearchService.LANGUAGE_LUCENE, typeQuery);
        
        return types.getNodeRefs();
    }

    private NodeRef getRecordCategory(String seriesName, String categoryName)
    {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.addStore(SPACES_STORE);
        String query = "PATH:\"rma:filePlan/cm:" + ISO9075.encode(seriesName)
            + "/cm:" + ISO9075.encode(categoryName) + "\"";
        System.out.println("Query: " + query);
        searchParameters.setQuery(query);
        searchParameters.setLanguage(SearchService.LANGUAGE_LUCENE);
        ResultSet rs = this.searchService.query(searchParameters);
        
        return rs.getNodeRef(0);
    }
    
    @SuppressWarnings("unused")
    private NodeRef getRecordFolder(String seriesName, String categoryName, String folderName)
    {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.addStore(SPACES_STORE);
        String query = "PATH:\"rma:filePlan/cm:" + ISO9075.encode(seriesName)
            + "/cm:" + ISO9075.encode(categoryName)
            + "/cm:" + ISO9075.encode(folderName) + "\"";
        System.out.println("Query: " + query);
        searchParameters.setQuery(query);
        searchParameters.setLanguage(SearchService.LANGUAGE_LUCENE);
        ResultSet rs = this.searchService.query(searchParameters);
        
        return rs.getNodeRef(0);
    }
}
