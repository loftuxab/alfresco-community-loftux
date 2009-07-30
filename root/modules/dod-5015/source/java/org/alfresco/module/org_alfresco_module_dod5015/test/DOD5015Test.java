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
package org.alfresco.module.org_alfresco_module_dod5015.test;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.DOD5015Model;
import org.alfresco.module.org_alfresco_module_dod5015.DispositionAction;
import org.alfresco.module.org_alfresco_module_dod5015.EventCompletionDetails;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementSearchBehaviour;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
import org.alfresco.module.org_alfresco_module_dod5015.VitalRecordDefinition;
import org.alfresco.module.org_alfresco_module_dod5015.action.RecordsManagementActionService;
import org.alfresco.module.org_alfresco_module_dod5015.action.impl.CompleteEventAction;
import org.alfresco.module.org_alfresco_module_dod5015.action.impl.FreezeAction;
import org.alfresco.module.org_alfresco_module_dod5015.capability.RMPermissionModel;
import org.alfresco.module.org_alfresco_module_dod5015.caveat.RMCaveatConfigImpl;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.transform.AbstractContentTransformerTest;
import org.alfresco.repo.node.integrity.IntegrityException;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParser;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Period;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.security.PublicServiceAccessService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.BaseSpringTest;
import org.alfresco.util.PropertyMap;

/**
 * 
 * 
 * @author Roy Wetherall, Neil McErlean
 */
public class DOD5015Test extends BaseSpringTest implements DOD5015Model
{    
	private static final Period weeklyReview = new Period("week|1");
    private static final Period dailyReview = new Period("day|1");
    public static final long TWENTY_FOUR_HOURS_IN_MS = 24 * 60 * 60 * 1000; // hours * minutes * seconds * millis

	protected static StoreRef SPACES_STORE = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
	
	private NodeRef filePlan;
	
	private NodeService nodeService;
	private SearchService searchService;
	private ImporterService importService;
	private ContentService contentService;
    private RecordsManagementService rmService;
    private RecordsManagementActionService rmActionService;
    private ServiceRegistry serviceRegistry;
	private TransactionService transactionService;
	private RMCaveatConfigImpl caveatConfigImpl;
	
	private AuthenticationService authenticationService;
	private PersonService personService;
	private AuthorityService authorityService;
	private PermissionService permissionService;

    private PublicServiceAccessService publicServiceAccessService;
	
	// example base test data for supplemental markings list (see also recordsModel.xml)
	protected final static String NOFORN     = "NOFORN";     // Not Releasable to Foreign Nationals/Governments/Non-US Citizens
	protected final static String NOCONTRACT = "NOCONTRACT"; // Not Releasable to Contractors or Contractor/Consultants
	protected final static String FOUO       = "FOUO";       // For Official Use Only 
	protected final static String FGI        = "FGI";        // Foreign Government Information
	
	@Override
	protected void onSetUpInTransaction() throws Exception 
	{
		super.onSetUpInTransaction();

		// Get the service required in the tests
		this.nodeService = (NodeService)this.applicationContext.getBean("NodeService"); // use upper 'N'odeService (to test access config interceptor)
		
		this.authenticationService = (AuthenticationService)this.applicationContext.getBean("AuthenticationService");
		this.personService = (PersonService)this.applicationContext.getBean("PersonService");
		this.authorityService = (AuthorityService)this.applicationContext.getBean("AuthorityService");
		this.permissionService = (PermissionService)this.applicationContext.getBean("PermissionService");
		
		this.searchService = (SearchService)this.applicationContext.getBean("SearchService"); // use upper 'S'earchService (to test access config interceptor)
		this.importService = (ImporterService)this.applicationContext.getBean("importerComponent");
		this.contentService = (ContentService)this.applicationContext.getBean("ContentService");
        this.rmService = (RecordsManagementService)this.applicationContext.getBean("RecordsManagementService");
        this.rmActionService = (RecordsManagementActionService)this.applicationContext.getBean("RecordsManagementActionService");
        this.serviceRegistry = (ServiceRegistry)this.applicationContext.getBean("ServiceRegistry");
		this.transactionService = (TransactionService)this.applicationContext.getBean("TransactionService");
		
		this.caveatConfigImpl = (RMCaveatConfigImpl)this.applicationContext.getBean("caveatConfigImpl");
		
		this.publicServiceAccessService = (PublicServiceAccessService)this.applicationContext.getBean("PublicServiceAccessService");
		
		// Set the current security context as admin
		AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
		
		// Get the test data
		setUpTestData();
        
        URL url = AbstractContentTransformerTest.class.getClassLoader().getResource("testCaveatConfig1.json"); // from test-resources
        assertNotNull(url);
        File file = new File(url.getFile());
        assertTrue(file.exists());
        
        caveatConfigImpl.updateOrCreateCaveatConfig(file);
	}
	
	private void setUpTestData()
	{
	    // Don't reload the fileplan data on each test method.
	    if (retrieveJanuaryAISVitalFolders().size() != 1)
	    {
            filePlan = TestUtilities.loadFilePlanData(null, this.nodeService, this.importService, this.permissionService);
	    }
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

    /**
     * This test method creates a non-vital record and then moves it to a vital folder
     * (triggering a refile) and then moves it a second time to another vital record
     * having different metadata.
     * 
     * Moving a Record within the FilePlan should trigger a "refile". Refiling a record
     * will lead to the reconsideration of its disposition, vital and transfer/accession
     * metadata, with potential changes therein.
     */
    public void testMoveRefileRecord() throws Exception
    {
        // Create a record folder under a "non-vital" category
        NodeRef nonVitalRecordCategory = TestUtilities.getRecordCategory(searchService, "Reports", "Unit Manning Documents");    
        assertNotNull(nonVitalRecordCategory);

        NodeRef nonVitalFolder = createRecFolderNode(nonVitalRecordCategory);
        // Commit in order to trigger the setUpRecordFolder behaviour
        setComplete();
        endTransaction();
        
        UserTransaction tx1 = transactionService.getUserTransaction(false);
        tx1.begin();
        
        // Create a (non-vital) record under the above folder
        NodeRef recordUnderTest = createRecordNode(nonVitalFolder);

        rmActionService.executeRecordsManagementAction(recordUnderTest, "file");
        this.declareRecord(recordUnderTest);

        // No need to commit the transaction here as the record is non-vital and
        // there is no metadata to copy down.
        
        NodeRef vitalFolder = retrieveJanuaryAISVitalFolder();
        
        // Move the non-vital record under the vital folder.
        serviceRegistry.getFileFolderService().move(recordUnderTest, vitalFolder, null);

        tx1.commit();
        UserTransaction tx2 = transactionService.getUserTransaction(false);
        tx2.begin();
        
        // At this point, the formerly nonVitalRecord is now actually vital.
        assertTrue("Expected record.", rmService.isRecord(recordUnderTest));
        assertTrue("Expected declared.", rmService.isRecordDeclared(recordUnderTest));
        
        final VitalRecordDefinition recordVrd = rmService.getVitalRecordDefinition(recordUnderTest);
        assertNotNull("Moved record should now have a Vital Rec Defn", recordVrd);
        assertEquals("Moved record had wrong review period",
                rmService.getVitalRecordDefinition(vitalFolder).getReviewPeriod(), recordVrd.getReviewPeriod());
        assertNotNull("Moved record should now have a review-as-of date", nodeService.getProperty(recordUnderTest, PROP_REVIEW_AS_OF));
        
        // Create another folder with different vital/disposition instructions
        //TODO Change disposition instructions
        NodeRef vitalRecordCategory = TestUtilities.getRecordCategory(searchService, "Reports", "AIS Audit Records");    
        assertNotNull(vitalRecordCategory);
        NodeRef secondVitalFolder = createRecFolderNode(vitalRecordCategory);

        tx2.commit();
        UserTransaction tx3 = transactionService.getUserTransaction(false);
        tx3.begin();
        
        Map<QName, Serializable> props = nodeService.getProperties(secondVitalFolder);
        final Serializable secondVitalFolderReviewPeriod = props.get(PROP_REVIEW_PERIOD);
        assertEquals("Unexpected review period.", weeklyReview, secondVitalFolderReviewPeriod);
        
        // We are changing the review period of this second record folder.
        props.put(PROP_REVIEW_PERIOD, dailyReview);
        this.nodeService.setProperties(secondVitalFolder, props);
        
        Date reviewDate = (Date)nodeService.getProperty(recordUnderTest, PROP_REVIEW_AS_OF);
        
        // Move the newly vital record under the second vital folder. I expect the reviewPeriod
        // for the record to be changed again.
        serviceRegistry.getFileFolderService().move(recordUnderTest, secondVitalFolder, null);
        
        // Commit to trigger the copy-down behaviour. Necessary?
        tx3.commit();
        UserTransaction tx4 = transactionService.getUserTransaction(false);
        tx4.begin();

        Period newReviewPeriod = rmService.getVitalRecordDefinition(recordUnderTest).getReviewPeriod();
        assertEquals("Unexpected review period.", dailyReview, newReviewPeriod);
        
        Date updatedReviewDate = (Date)nodeService.getProperty(recordUnderTest, PROP_REVIEW_AS_OF);
        // The reviewAsOf date should have changed to "24 hours from now".
        assertFalse("reviewAsOf date was unchanged", reviewDate.equals(updatedReviewDate));
        long millisecondsUntilNextReview = updatedReviewDate.getTime() - new Date().getTime();
        assertTrue("new reviewAsOf date was not within 24 hours of now.",
                millisecondsUntilNextReview <= TWENTY_FOUR_HOURS_IN_MS);

        nodeService.deleteNode(recordUnderTest);
        nodeService.deleteNode(nonVitalFolder);
        nodeService.deleteNode(secondVitalFolder);
        tx4.commit();
    }

    public void off_testMoveRefileRecordFolder() throws Exception
    {
        //TODO Impl me
        fail("Not yet impl'd.");
    }

    public void off_testCopyRefileRecordFolder() throws Exception
    {
        //TODO Impl me
        fail("Not yet impl'd.");
    }

    public void off_testCopyRefileRecord() throws Exception
    {
        //TODO Impl me
        fail("Not yet impl'd.");
    }

    private NodeRef createRecFolderNode(NodeRef parentRecordCategory)
    {
        NodeRef newFolder = this.nodeService.createNode(parentRecordCategory,
                                   ContentModel.ASSOC_CONTAINS,
                                   QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "Test folder " + System.currentTimeMillis()),
                                   TYPE_RECORD_FOLDER).getChildRef();
        return newFolder;
    }

    private NodeRef createRecordNode(NodeRef parentFolder)
    {
        NodeRef newRecord = this.nodeService.createNode(parentFolder,
                                    ContentModel.ASSOC_CONTAINS,
                                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
                                            "Record" + System.currentTimeMillis() + ".txt"),
                                    ContentModel.TYPE_CONTENT).getChildRef();
        ContentWriter writer = this.contentService.getWriter(newRecord, ContentModel.PROP_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        writer.setEncoding("UTF-8");
        writer.putContent("Irrelevant content");
        return newRecord;
    }
    
    private NodeRef retrieveJanuaryAISVitalFolder()
    {
        final List<NodeRef> resultNodeRefs = retrieveJanuaryAISVitalFolders();
        final int folderCount = resultNodeRefs.size();
        assertTrue("There should only be one 'January AIS Audit Records' folder. Were " + folderCount, folderCount == 1);
        
        // This nodeRef should have rma:VRI=true, rma:reviewPeriod=week|1, rma:isClosed=false
        return resultNodeRefs.get(0);
    }

    private List<NodeRef> retrieveJanuaryAISVitalFolders()
    {
        String typeQuery = "TYPE:\"" + TYPE_RECORD_FOLDER + "\" AND @cm\\:name:\"January AIS Audit Records\"";
        ResultSet types = this.searchService.query(SPACES_STORE, SearchService.LANGUAGE_LUCENE, typeQuery);
        
        final List<NodeRef> resultNodeRefs = types.getNodeRefs();
        return resultNodeRefs;
    }

    public void testDispositionLifecycle_0318_01_basictest() throws Exception
	{	    
	    NodeRef recordCategory = TestUtilities.getRecordCategory(this.searchService, "Reports", "AIS Audit Records");    
	    assertNotNull(recordCategory);
	    assertEquals("AIS Audit Records", this.nodeService.getProperty(recordCategory, ContentModel.PROP_NAME));
        	    
	    NodeRef recordFolder = createRecordFolder(recordCategory, "March AIS Audit Records");
	    
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
	    
	    txn.commit(); // - triggers FileAction
        txn = transactionService.getUserTransaction(false);
        txn.begin();
        
	    // Checked that the document has been marked as incomplete
	    System.out.println("recordOne ...");
	    
	    assertTrue(this.nodeService.hasAspect(recordOne, ASPECT_RECORD));
        assertNotNull(this.nodeService.getProperty(recordOne, PROP_IDENTIFIER));
        System.out.println("Record id: " + this.nodeService.getProperty(recordOne, PROP_IDENTIFIER));
        assertNotNull(this.nodeService.getProperty(recordOne, PROP_DATE_FILED));
        System.out.println("Date filed: " + this.nodeService.getProperty(recordOne, PROP_DATE_FILED));
        
        // Check the review schedule
        assertTrue(this.nodeService.hasAspect(recordOne, ASPECT_VITAL_RECORD));
        assertNotNull(this.nodeService.getProperty(recordOne, PROP_REVIEW_AS_OF));
        System.out.println("Review as of: " + this.nodeService.getProperty(recordOne, PROP_REVIEW_AS_OF));

        // NOTE the disposition is being managed at a folder level ...
        
        // Check the disposition action
        assertFalse(this.nodeService.hasAspect(recordOne, ASPECT_DISPOSITION_LIFECYCLE));
        assertTrue(this.nodeService.hasAspect(recordFolder, ASPECT_DISPOSITION_LIFECYCLE));
        
        NodeRef ndNodeRef = this.nodeService.getChildAssocs(recordFolder, ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL).get(0).getChildRef();
        assertNotNull(ndNodeRef);        
        
        assertNotNull(this.nodeService.getProperty(ndNodeRef, PROP_DISPOSITION_ACTION_ID));
        System.out.println("Disposition action id: " + this.nodeService.getProperty(ndNodeRef, PROP_DISPOSITION_ACTION_ID));
        assertEquals("cutoff", this.nodeService.getProperty(ndNodeRef, PROP_DISPOSITION_ACTION));
        System.out.println("Disposition action: " + this.nodeService.getProperty(ndNodeRef, PROP_DISPOSITION_ACTION));
        assertNotNull(this.nodeService.getProperty(ndNodeRef, PROP_DISPOSITION_AS_OF));
        System.out.println("Disposition as of: " + this.nodeService.getProperty(ndNodeRef, PROP_DISPOSITION_AS_OF));
        
        // Check for the search properties having been populated
        checkSearchAspect(ndNodeRef, recordFolder, 0);
        
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
        
        // Try and declare, expected failure
        try
        {
            this.rmActionService.executeRecordsManagementAction(recordOne, "declareRecord");
            fail("Should not be able to declare a record that still has mandatory properties unset");
        }
        catch (Exception e)
        {
            // Expected
        }
        
        txn.rollback(); 
        txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        assertTrue("Before test DECLARED aspect was set", 
        		this.nodeService.hasAspect(recordOne, ASPECT_DECLARED_RECORD) == false);    
        
        propValues = this.nodeService.getProperties(recordOne);        
        propValues.put(RecordsManagementModel.PROP_ORIGINATOR, "origValue");
        propValues.put(RecordsManagementModel.PROP_ORIGINATING_ORGANIZATION, "origOrgValue");
        propValues.put(ContentModel.PROP_TITLE, "titleValue");
        this.nodeService.setProperties(recordOne, propValues);
        
        // Declare the record as we have set everything we should have
        this.rmActionService.executeRecordsManagementAction(recordOne, "declareRecord");
        assertTrue(" the record is not declared", this.nodeService.hasAspect(recordOne, ASPECT_DECLARED_RECORD));
        
        txn.commit(); 
        txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        // Execute the cutoff action (should fail because this is being done at the record level)
        try
        {
            this.rmActionService.executeRecordsManagementAction(recordFolder, "cutoff", null);
            fail(("Shouldn't have been able to execute cut off at the record level"));
        }
        catch (Exception e)
        {
            // expected
        }
        
        // Execute the cutoff action (should fail becuase it is not yet eligiable)
        try
        {
            this.rmActionService.executeRecordsManagementAction(recordFolder, "cutoff", null);
            fail(("Shouldn't have been able to execute because it is not yet eligiable"));
        }
        catch (Exception e)
        {
            // expected
        }
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        
        txn.rollback();
        txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        // Clock the asOf date back to ensure eligibility
        ndNodeRef = this.nodeService.getChildAssocs(recordFolder, ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL).get(0).getChildRef();
        this.nodeService.setProperty(ndNodeRef, PROP_DISPOSITION_AS_OF, calendar.getTime());        
        this.rmActionService.executeRecordsManagementAction(recordFolder, "cutoff", null);
        
        txn.commit();
        txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        // Check the disposition action
        assertFalse(this.nodeService.hasAspect(recordOne, ASPECT_DISPOSITION_LIFECYCLE));
        assertTrue(this.nodeService.hasAspect(recordFolder, ASPECT_DISPOSITION_LIFECYCLE));
        
        ndNodeRef = this.nodeService.getChildAssocs(recordFolder, ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL).get(0).getChildRef();
        assertNotNull(ndNodeRef);                
        
        assertNotNull(this.nodeService.getProperty(ndNodeRef, PROP_DISPOSITION_ACTION_ID));
        System.out.println("Disposition action id: " + this.nodeService.getProperty(ndNodeRef, PROP_DISPOSITION_ACTION_ID));
        assertEquals("destroy", this.nodeService.getProperty(ndNodeRef, PROP_DISPOSITION_ACTION));
        System.out.println("Disposition action: " + this.nodeService.getProperty(ndNodeRef, PROP_DISPOSITION_ACTION));
        assertNotNull(this.nodeService.getProperty(ndNodeRef, PROP_DISPOSITION_AS_OF));
        System.out.println("Disposition as of: " + this.nodeService.getProperty(ndNodeRef, PROP_DISPOSITION_AS_OF));
        assertNull(this.nodeService.getProperty(recordFolder, RecordsManagementSearchBehaviour.PROP_RS_DISPOSITION_EVENTS));
        
        // Check the previous action details
        // TODO check the history association
        //assertEquals("cutoff", this.nodeService.getProperty(recordFolder, PROP_PREVIOUS_DISPOSITION_DISPOSITION_ACTION));
        //assertNotNull(this.nodeService.getProperty(recordFolder, PROP_PREVIOUS_DISPOSITION_DISPOSITION_DATE));
        //System.out.println("Previous aciont date: " + this.nodeService.getProperty(recordFolder, PROP_PREVIOUS_DISPOSITION_DISPOSITION_DATE).toString());

        // Check for the search properties having been populated
        checkSearchAspect(ndNodeRef, recordFolder, 0);
        
        // Execute the destroy action
        ndNodeRef = this.nodeService.getChildAssocs(recordFolder, ASSOC_NEXT_DISPOSITION_ACTION, RegexQNamePattern.MATCH_ALL).get(0).getChildRef();
        this.nodeService.setProperty(ndNodeRef, PROP_DISPOSITION_AS_OF, calendar.getTime());
        this.rmActionService.executeRecordsManagementAction(recordFolder, "destroy", null);
        
        // Check that the node has been destroyed
        assertFalse(this.nodeService.exists(recordFolder));
        assertFalse(this.nodeService.exists(recordOne));
        
        txn.commit();
    }
    
    public void testFreeze() throws Exception
    {      
        NodeRef recordCategory = TestUtilities.getRecordCategory(this.searchService, "Reports", "AIS Audit Records");    
        assertNotNull(recordCategory);
        assertEquals("AIS Audit Records", this.nodeService.getProperty(recordCategory, ContentModel.PROP_NAME));
        
        // Before we start just remove any outstanding holds
        NodeRef rootNode = this.rmService.getRecordsManagementRoot(recordCategory);
        List<ChildAssociationRef> tempAssocs = this.nodeService.getChildAssocs(rootNode, ASSOC_HOLDS, RegexQNamePattern.MATCH_ALL);
        for (ChildAssociationRef tempAssoc : tempAssocs)
        {
            this.nodeService.deleteNode(tempAssoc.getChildRef());
        }
        
        NodeRef recordFolder = createRecordFolder(recordCategory, "March AIS Audit Records");
        
        setComplete();
        endTransaction();
        
        UserTransaction txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        createRecord(recordFolder);
        
        NodeRef recordOne = createRecord(recordFolder, "one.txt");
        NodeRef recordTwo = createRecord(recordFolder, "two.txt");
        NodeRef recordThree = createRecord(recordFolder, "three.txt");
        
        txn.commit(); 
        txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        assertTrue(this.nodeService.hasAspect(recordOne, ASPECT_RECORD));
        assertTrue(this.nodeService.hasAspect(recordOne, ASPECT_FILE_PLAN_COMPONENT));
        
        // Freeze the record
        Map<String, Serializable> params = new HashMap<String, Serializable>(1);
        params.put(FreezeAction.PARAM_REASON, "one");
        this.rmActionService.executeRecordsManagementAction(recordOne, "freeze", params);
        
        txn.commit(); 
        txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        // Check the hold exists 
        List<ChildAssociationRef> holdAssocs = this.nodeService.getChildAssocs(rootNode, ASSOC_HOLDS, RegexQNamePattern.MATCH_ALL);
        assertNotNull(holdAssocs);
        assertEquals(1, holdAssocs.size());        
        NodeRef holdNodeRef = holdAssocs.get(0).getChildRef();
        assertEquals("one", this.nodeService.getProperty(holdNodeRef, PROP_HOLD_REASON));
        List<ChildAssociationRef> freezeAssocs = this.nodeService.getChildAssocs(holdNodeRef);
        assertNotNull(freezeAssocs);
        assertEquals(1, freezeAssocs.size());
        
        // Check the nodes are frozen
        assertTrue(this.nodeService.hasAspect(recordOne, ASPECT_FROZEN));
        assertFalse(this.nodeService.hasAspect(recordTwo, ASPECT_FROZEN));
        assertFalse(this.nodeService.hasAspect(recordThree, ASPECT_FROZEN));
        
        // Freeze a number of records
        params = new HashMap<String, Serializable>(1);
        params.put(FreezeAction.PARAM_REASON, "two");
        List<NodeRef> records = new ArrayList<NodeRef>(2);
        records.add(recordOne);
        records.add(recordTwo);
        records.add(recordThree);
        this.rmActionService.executeRecordsManagementAction(records, "freeze", params);
        
        txn.commit(); 
        txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        // Check the holds exist
        holdAssocs = this.nodeService.getChildAssocs(rootNode, ASSOC_HOLDS, RegexQNamePattern.MATCH_ALL);
        assertNotNull(holdAssocs);
        assertEquals(2, holdAssocs.size());
        for (ChildAssociationRef holdAssoc : holdAssocs)
        {
            String reason = (String)this.nodeService.getProperty(holdAssoc.getChildRef(), PROP_HOLD_REASON);
            if (reason.equals("two") == true)
            {
                freezeAssocs = this.nodeService.getChildAssocs(holdAssoc.getChildRef());
                assertNotNull(freezeAssocs);
                assertEquals(3, freezeAssocs.size());
            }
            else if (reason.equals("one") == true)
            {
                freezeAssocs = this.nodeService.getChildAssocs(holdAssoc.getChildRef());
                assertNotNull(freezeAssocs);
                assertEquals(1, freezeAssocs.size());
            }
        }
        
        // Check the nodes are frozen
        assertTrue(this.nodeService.hasAspect(recordOne, ASPECT_FROZEN));
        assertTrue(this.nodeService.hasAspect(recordTwo, ASPECT_FROZEN));
        assertTrue(this.nodeService.hasAspect(recordThree, ASPECT_FROZEN));
        
        // Unfreeze a node
        this.rmActionService.executeRecordsManagementAction(recordThree, "unfreeze");
        
        // Check the holds
        holdAssocs = this.nodeService.getChildAssocs(rootNode, ASSOC_HOLDS, RegexQNamePattern.MATCH_ALL);
        assertNotNull(holdAssocs);
        assertEquals(2, holdAssocs.size());
        for (ChildAssociationRef holdAssoc : holdAssocs)
        {
            String reason = (String)this.nodeService.getProperty(holdAssoc.getChildRef(), PROP_HOLD_REASON);
            if (reason.equals("two") == true)
            {
                freezeAssocs = this.nodeService.getChildAssocs(holdAssoc.getChildRef());
                assertNotNull(freezeAssocs);
                assertEquals(2, freezeAssocs.size());
            }
            else if (reason.equals("one") == true)
            {
                freezeAssocs = this.nodeService.getChildAssocs(holdAssoc.getChildRef());
                assertNotNull(freezeAssocs);
                assertEquals(1, freezeAssocs.size());
            }
        }
        
        // Check the nodes are frozen
        assertTrue(this.nodeService.hasAspect(recordOne, ASPECT_FROZEN));
        assertTrue(this.nodeService.hasAspect(recordTwo, ASPECT_FROZEN));
        assertFalse(this.nodeService.hasAspect(recordThree, ASPECT_FROZEN));
        
        // Relinquish the first hold
        this.rmActionService.executeRecordsManagementAction(holdNodeRef, "relinquishHold");
        
        // Check the holds
        holdAssocs = this.nodeService.getChildAssocs(rootNode, ASSOC_HOLDS, RegexQNamePattern.MATCH_ALL);
        assertNotNull(holdAssocs);
        assertEquals(1, holdAssocs.size());
        holdNodeRef = holdAssocs.get(0).getChildRef();
        assertEquals("two", this.nodeService.getProperty(holdNodeRef, PROP_HOLD_REASON));
        freezeAssocs = this.nodeService.getChildAssocs(holdNodeRef);
        assertNotNull(freezeAssocs);
        assertEquals(2, freezeAssocs.size());
        
        // Check the nodes are frozen
        assertTrue(this.nodeService.hasAspect(recordOne, ASPECT_FROZEN));
        assertTrue(this.nodeService.hasAspect(recordTwo, ASPECT_FROZEN));
        assertFalse(this.nodeService.hasAspect(recordThree, ASPECT_FROZEN));
        
        // Unfreeze
        this.rmActionService.executeRecordsManagementAction(recordOne, "unfreeze");
        
        // Check the holds
        holdAssocs = this.nodeService.getChildAssocs(rootNode, ASSOC_HOLDS, RegexQNamePattern.MATCH_ALL);
        assertNotNull(holdAssocs);
        assertEquals(1, holdAssocs.size());
        holdNodeRef = holdAssocs.get(0).getChildRef();
        assertEquals("two", this.nodeService.getProperty(holdNodeRef, PROP_HOLD_REASON));
        freezeAssocs = this.nodeService.getChildAssocs(holdNodeRef);
        assertNotNull(freezeAssocs);
        assertEquals(1, freezeAssocs.size());
        
        // Check the nodes are frozen
        assertFalse(this.nodeService.hasAspect(recordOne, ASPECT_FROZEN));
        assertTrue(this.nodeService.hasAspect(recordTwo, ASPECT_FROZEN));
        assertFalse(this.nodeService.hasAspect(recordThree, ASPECT_FROZEN));
        
        // Unfreeze
        this.rmActionService.executeRecordsManagementAction(recordTwo, "unfreeze");
        
        // Check the holds
        holdAssocs = this.nodeService.getChildAssocs(rootNode, ASSOC_HOLDS, RegexQNamePattern.MATCH_ALL);
        assertNotNull(holdAssocs);
        assertEquals(0, holdAssocs.size());
        
        // Check the nodes are frozen
        assertFalse(this.nodeService.hasAspect(recordOne, ASPECT_FROZEN));
        assertFalse(this.nodeService.hasAspect(recordTwo, ASPECT_FROZEN));
        assertFalse(this.nodeService.hasAspect(recordThree, ASPECT_FROZEN));
        
        txn.commit();         
    }
	
	private void checkSearchAspect(NodeRef dispositionAction, NodeRef record, int eventCount)
	{
        assertTrue(this.nodeService.hasAspect(record, RecordsManagementSearchBehaviour.ASPECT_RM_SEARCH));
        assertEquals(this.nodeService.getProperty(dispositionAction, PROP_DISPOSITION_ACTION),
                     this.nodeService.getProperty(record, RecordsManagementSearchBehaviour.PROP_RS_DISPOSITION_ACTION_NAME));
        assertEquals(this.nodeService.getProperty(dispositionAction, PROP_DISPOSITION_AS_OF),
                     this.nodeService.getProperty(record, RecordsManagementSearchBehaviour.PROP_RS_DISPOSITION_ACTION_AS_OF));
        assertEquals(this.nodeService.getProperty(dispositionAction, PROP_DISPOSITION_EVENTS_ELIGIBLE),
                     this.nodeService.getProperty(record, RecordsManagementSearchBehaviour.PROP_RS_DISPOSITION_EVENTS_ELIGIBLE));
        
        Collection<String> events = (Collection<String>)this.nodeService.getProperty(record, RecordsManagementSearchBehaviour.PROP_RS_DISPOSITION_EVENTS);
        if (eventCount == 0)
        {
            assertNull(events);
        }
        else
        {
            assertEquals(eventCount, events.size());
        }
	}

	
	public void testDispositionLifecycle_0412_01() throws Exception
    {
	    
    }
	
	public void testDispositionLifecycle_0412_03_eventtest() throws Exception
    {
	    NodeRef recordCategory = TestUtilities.getRecordCategory(this.searchService, "Military Files", "Personnel Security Program Records");    
        assertNotNull(recordCategory);
        assertEquals("Personnel Security Program Records", this.nodeService.getProperty(recordCategory, ContentModel.PROP_NAME));
                
        Map<QName, Serializable> folderProps = new HashMap<QName, Serializable>(1);
        folderProps.put(ContentModel.PROP_NAME, "My Folder");
        NodeRef recordFolder = this.nodeService.createNode(recordCategory, 
                                                           ContentModel.ASSOC_CONTAINS, 
                                                           QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "My Folder"), 
                                                           TYPE_RECORD_FOLDER).getChildRef();        
        setComplete();
        endTransaction();
        
        UserTransaction txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        NodeRef recordOne = createRecord(recordFolder);
        
        txn.commit();
        txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        declareRecord(recordOne);
        
        // NOTE the disposition is being managed at a folder level ...
        
        // Check the disposition action
        assertFalse(this.nodeService.hasAspect(recordOne, ASPECT_DISPOSITION_LIFECYCLE));
        assertTrue(this.nodeService.hasAspect(recordFolder, ASPECT_DISPOSITION_LIFECYCLE));
        
        // Check the dispostion action
        DispositionAction da = rmService.getNextDispositionAction(recordFolder);
        assertNotNull(da);
        assertEquals("cutoff", da.getDispositionActionDefinition().getName());
        assertNull(da.getAsOfDate());
        assertFalse((Boolean)this.nodeService.getProperty(da.getNodeRef(), PROP_DISPOSITION_EVENTS_ELIGIBLE));
        assertEquals(false, da.getDispositionActionDefinition().eligibleOnFirstCompleteEvent());
        List<EventCompletionDetails> events = da.getEventCompletionDetails();
        assertNotNull(events);
        assertEquals(3, events.size());
        
        checkSearchAspect(da.getNodeRef(), recordFolder, 3);
        
        txn.commit();
        txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        EventCompletionDetails ecd = events.get(0);
        assertFalse(ecd.isEventComplete());
        assertNull(ecd.getEventCompletedBy());
        assertNull(ecd.getEventCompletedAt());
        
        Map<String, Serializable> params = new HashMap<String, Serializable>(3);
        params.put(CompleteEventAction.PARAM_EVENT_NAME, events.get(0).getEventName());
        params.put(CompleteEventAction.PARAM_EVENT_COMPLETED_AT, new Date());
        params.put(CompleteEventAction.PARAM_EVENT_COMPLETED_BY, "roy");
        
        checkSearchAspect(da.getNodeRef(), recordFolder, 3);
        
        this.rmActionService.executeRecordsManagementAction(recordFolder, "completeEvent", params);
        
        txn.commit();
        txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        assertFalse((Boolean)this.nodeService.getProperty(da.getNodeRef(), PROP_DISPOSITION_EVENTS_ELIGIBLE));
        assertEquals(false, da.getDispositionActionDefinition().eligibleOnFirstCompleteEvent());
        events = da.getEventCompletionDetails();
        assertNotNull(events);
        assertEquals(3, events.size());
        
        params = new HashMap<String, Serializable>(3);
        params.put(CompleteEventAction.PARAM_EVENT_NAME, events.get(1).getEventName());
        params.put(CompleteEventAction.PARAM_EVENT_COMPLETED_AT, new Date());
        params.put(CompleteEventAction.PARAM_EVENT_COMPLETED_BY, "roy");
        
        checkSearchAspect(da.getNodeRef(), recordFolder, 3);
        
        this.rmActionService.executeRecordsManagementAction(recordFolder, "completeEvent", params);
        
        txn.commit();
        txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        assertFalse((Boolean)this.nodeService.getProperty(da.getNodeRef(), PROP_DISPOSITION_EVENTS_ELIGIBLE));
        
        params = new HashMap<String, Serializable>(3);
        params.put(CompleteEventAction.PARAM_EVENT_NAME, events.get(2).getEventName());
        params.put(CompleteEventAction.PARAM_EVENT_COMPLETED_AT, new Date());
        params.put(CompleteEventAction.PARAM_EVENT_COMPLETED_BY, "roy");
        
        checkSearchAspect(da.getNodeRef(), recordFolder, 3);
        
        this.rmActionService.executeRecordsManagementAction(recordFolder, "completeEvent", params);
        
        txn.commit();
        txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        assertTrue((Boolean)this.nodeService.getProperty(da.getNodeRef(), PROP_DISPOSITION_EVENTS_ELIGIBLE));
        
        events = da.getEventCompletionDetails();
        assertNotNull(events);
        assertEquals(3, events.size());        
        for (EventCompletionDetails e : events)
        {
            assertTrue(e.isEventComplete());
            assertEquals("roy", e.getEventCompletedBy());
            assertNotNull(e.getEventCompletedAt());
        }
        
        checkSearchAspect(da.getNodeRef(), recordFolder, 3);
        
        // Test undo
        
        params = new HashMap<String, Serializable>(1);
        params.put(CompleteEventAction.PARAM_EVENT_NAME, events.get(2).getEventName());
        this.rmActionService.executeRecordsManagementAction(recordFolder, "undoEvent", params);
        
        txn.commit();
        txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        assertFalse((Boolean)this.nodeService.getProperty(da.getNodeRef(), PROP_DISPOSITION_EVENTS_ELIGIBLE));
        
        params = new HashMap<String, Serializable>(3);
        params.put(CompleteEventAction.PARAM_EVENT_NAME, events.get(2).getEventName());
        params.put(CompleteEventAction.PARAM_EVENT_COMPLETED_AT, new Date());
        params.put(CompleteEventAction.PARAM_EVENT_COMPLETED_BY, "roy");
                
        this.rmActionService.executeRecordsManagementAction(recordFolder, "completeEvent", params);
        
        txn.commit();
        txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        assertTrue((Boolean)this.nodeService.getProperty(da.getNodeRef(), PROP_DISPOSITION_EVENTS_ELIGIBLE));
        
        // Do the commit action
        this.rmActionService.executeRecordsManagementAction(recordFolder, "cutoff", null);
        
        txn.commit();
        txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        // Check events are gone
        da = rmService.getNextDispositionAction(recordFolder);
        
        assertNotNull(da);
        assertEquals("destroy", da.getDispositionActionDefinition().getName());
        assertNotNull(da.getAsOfDate());
        assertFalse((Boolean)this.nodeService.getProperty(da.getNodeRef(), PROP_DISPOSITION_EVENTS_ELIGIBLE));
        events = da.getEventCompletionDetails();
        assertNotNull(events);
        assertEquals(0, events.size());
        
        checkSearchAspect(da.getNodeRef(), recordFolder, 0);
        
        txn.commit();
    }
	
	private NodeRef createRecord(NodeRef recordFolder)
	{
	    return createRecord(recordFolder, "MyRecord.txt");
	}
	
	private NodeRef createRecord(NodeRef recordFolder, String name)
	{
    	// Create the document
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
        props.put(ContentModel.PROP_NAME, name);
        NodeRef recordOne = this.nodeService.createNode(recordFolder, 
                                                        ContentModel.ASSOC_CONTAINS, 
                                                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name), 
                                                        ContentModel.TYPE_CONTENT).getChildRef();
        
        // Set the content
        ContentWriter writer = this.contentService.getWriter(recordOne, ContentModel.PROP_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        writer.setEncoding("UTF-8");
        writer.putContent("There is some content in this record");
        
        return recordOne;
	}   
      
    private void declareRecord(NodeRef recordOne)
    {
        // Declare record
        Map<QName, Serializable> propValues = this.nodeService.getProperties(recordOne);        
        propValues.put(RecordsManagementModel.PROP_PUBLICATION_DATE, new Date());       
        List<String> smList = new ArrayList<String>(2);
        smList.add(FOUO);
        smList.add(NOFORN);
        propValues.put(RecordsManagementModel.PROP_SUPPLEMENTAL_MARKING_LIST, (Serializable)smList);        
        propValues.put(RecordsManagementModel.PROP_MEDIA_TYPE, "mediaTypeValue"); 
        propValues.put(RecordsManagementModel.PROP_FORMAT, "formatValue"); 
        propValues.put(RecordsManagementModel.PROP_DATE_RECEIVED, new Date());       
        propValues.put(RecordsManagementModel.PROP_ORIGINATOR, "origValue");
        propValues.put(RecordsManagementModel.PROP_ORIGINATING_ORGANIZATION, "origOrgValue");
        propValues.put(ContentModel.PROP_TITLE, "titleValue");
        this.nodeService.setProperties(recordOne, propValues);
        this.rmActionService.executeRecordsManagementAction(recordOne, "declareRecord");        
	}
    
    /**
     * This method tests the filing of a custom type, as defined in DOD 5015.
     */
    public void testFileDOD5015CustomTypes() throws Exception
    {
        NodeRef recordCategory = TestUtilities.getRecordCategory(this.searchService, "Reports", "AIS Audit Records");    
                
        NodeRef recordFolder = createRecordFolder(recordCategory, "March AIS Audit Records");
        setComplete();
        endTransaction();
        
        UserTransaction txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        NodeRef testDocument = this.nodeService.createNode(recordFolder, 
                ContentModel.ASSOC_CONTAINS, 
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "CustomType"), 
                ContentModel.TYPE_CONTENT).getChildRef();

        // It's not necessary to set content for this test.
        
        // File the record.
        rmActionService.executeRecordsManagementAction(testDocument, "file");

        assertTrue("testDocument should be a record.", rmService.isRecord(testDocument));

        // Have the customType aspect applied..
        Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put(PROP_SCANNED_FORMAT.toPrefixString(serviceRegistry.getNamespaceService()), "f");
        props.put(PROP_SCANNED_FORMAT_VERSION.toPrefixString(serviceRegistry.getNamespaceService()), "1.0");
        props.put(PROP_RESOLUTION_X.toPrefixString(serviceRegistry.getNamespaceService()), "100");
        props.put(PROP_RESOLUTION_Y.toPrefixString(serviceRegistry.getNamespaceService()), "100");
        props.put(PROP_SCANNED_BIT_DEPTH.toPrefixString(serviceRegistry.getNamespaceService()), "10");
        rmActionService.executeRecordsManagementAction(testDocument, "applyScannedRecord", props);

        assertTrue("Custom type should have ScannedRecord aspect.", nodeService.hasAspect(testDocument, DOD5015Model.ASPECT_SCANNED_RECORD));
        
        txn.rollback();
    }

    /**
     * This method tests the filing of an already existing document i.e. one that is
     * already contained within the document library.
     */
    public void testFileFromDoclib() throws Exception
    {
        // Get the relevant RecordCategory and create a RecordFolder underneath it.
        NodeRef recordCategory = TestUtilities.getRecordCategory(this.searchService, "Reports", "AIS Audit Records");    
                
        NodeRef recordFolder = createRecordFolder(recordCategory, "March AIS Audit Records");
        setComplete();
        endTransaction();
        
        UserTransaction txn = transactionService.getUserTransaction(false);
        txn.begin();
        
        // Unlike testBasicFilingTest, we now create a normal Alfresco content node
        // rather than a fully-fledged record. The content must also be outside the
        // fileplan.

        // Create a site - to put the content in.
        final String rmTestSiteShortName = "rmTest" + System.currentTimeMillis();
        this.serviceRegistry.getSiteService().createSite("RMTestSite", rmTestSiteShortName,
                "Test site for Records Management", "", SiteVisibility.PUBLIC);

        NodeRef siteRoot = this.serviceRegistry.getSiteService().getSite(rmTestSiteShortName).getNodeRef();
        NodeRef siteDocLib = this.nodeService.createNode(siteRoot, 
                                                   ContentModel.ASSOC_CONTAINS, 
                                                   QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "documentLibrary"), 
                                                   ContentModel.TYPE_FOLDER).getChildRef();
        // Create the test document
        NodeRef testDocument = this.nodeService.createNode(siteDocLib,
                                                    ContentModel.ASSOC_CONTAINS, 
                                                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "PreexistingDocument.txt"), 
                                                    ContentModel.TYPE_CONTENT).getChildRef();
        // Set some content
        ContentWriter writer = this.contentService.getWriter(testDocument, ContentModel.PROP_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        writer.setEncoding("UTF-8");
        writer.putContent("Some dummy content.");

        txn.commit();
        txn = transactionService.getUserTransaction(false);
        txn.begin();

        // Clearly, this should not be a record at this point.
        assertFalse(this.nodeService.hasAspect(testDocument, ASPECT_RECORD));

        // Now we want to file this document as a record within the RMA.
        // To do this we simply move a document into the fileplan and file
        this.serviceRegistry.getFileFolderService().move(testDocument, recordFolder, null);
        rmActionService.executeRecordsManagementAction(testDocument, "file");

        assertTrue("testDocument should be a record.", rmService.isRecord(testDocument));
        assertNotNull(this.nodeService.getProperty(testDocument, PROP_IDENTIFIER));
        assertNotNull(this.nodeService.getProperty(testDocument, PROP_DATE_FILED));
        
        // Check the review schedule
        assertTrue(this.nodeService.hasAspect(testDocument, ASPECT_VITAL_RECORD));
        assertNotNull(this.nodeService.getProperty(testDocument, PROP_REVIEW_AS_OF));
        
        txn.commit();
    }

    /**
     * This method tests the filing of non-electronic record.
     */
    public void testFileNonElectronicRecord() throws Exception
    {
        // Get the relevant RecordCategory and create a RecordFolder underneath it.
        NodeRef recordCategory = TestUtilities.getRecordCategory(this.searchService, "Reports", "AIS Audit Records");    
                
        NodeRef recordFolder = createRecordFolder(recordCategory, "March AIS Audit Records");
        
        setComplete();
        endTransaction();
        
        UserTransaction txn1 = transactionService.getUserTransaction(false);
        txn1.begin();
        
        // Create the document
        NodeRef testRecord = this.nodeService.createNode(recordFolder,
                                    ContentModel.ASSOC_CONTAINS,
                                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "Non-electronic Record"),
                                    RecordsManagementModel.TYPE_NON_ELECTRONIC_DOCUMENT).getChildRef();

        // There is no content on a non-electronic record.

        // These properties are required in order to declare the record.
        Map<QName, Serializable> props = nodeService.getProperties(testRecord);
        props.put(RecordsManagementModel.PROP_ORIGINATING_ORGANIZATION, "alfresco");
        props.put(RecordsManagementModel.PROP_ORIGINATOR, "admin");
        props.put(RecordsManagementModel.PROP_PUBLICATION_DATE, new Date());
        nodeService.setProperties(testRecord, props);
        
        txn1.commit();
        UserTransaction txn2 = transactionService.getUserTransaction(false);
        txn2.begin();
        
        assertTrue("Expected non-electronic record to be a record.", rmService.isRecord(testRecord));
        assertFalse("Expected non-electronic record not to be declared yet.", rmService.isRecordDeclared(testRecord));
        
        rmActionService.executeRecordsManagementAction(testRecord, "file");
        rmActionService.executeRecordsManagementAction(testRecord, "declareRecord");
        
        assertTrue("Non-electronic record should now be declared.", rmService.isRecordDeclared(testRecord));
        
        // These properties are added automatically when the record is filed
        assertNotNull(nodeService.getProperty(testRecord, RecordsManagementModel.PROP_IDENTIFIER));
        assertNotNull(nodeService.getProperty(testRecord, RecordsManagementModel.PROP_DATE_FILED));
        
//      assertNotNull(nodeService.getProperty(testRecord, ContentModel.PROP_TITLE));
//      assertNotNull(nodeService.getProperty(testRecord, RecordsManagementModel.PROP_SUPPLEMENTAL_MARKING_LIST));
//      assertNotNull(nodeService.getProperty(testRecord, RecordsManagementModel.PROP_MEDIA_TYPE));
//      assertNotNull(nodeService.getProperty(testRecord, RecordsManagementModel.PROP_FORMAT));
//      assertNotNull(nodeService.getProperty(testRecord, RecordsManagementModel.PROP_DATE_RECEIVED));
//      assertEquals("foo", nodeService.getProperty(testRecord, RecordsManagementModel.PROP_ADDRESS));
//      assertEquals("foo", nodeService.getProperty(testRecord, RecordsManagementModel.PROP_OTHER_ADDRESS));
//      assertNotNull(nodeService.getProperty(testRecord, RecordsManagementModel.PROP_LOCATION));
//      assertEquals("foo", nodeService.getProperty(testRecord, RecordsManagementModel.PROP_PROJECT_NAME));
        
        //TODO Add links to other records as per test doc.
        txn2.commit();
    }

    private NodeRef createRecordFolder(NodeRef recordCategory, String folderName)
    {
        Map<QName, Serializable> folderProps = new HashMap<QName, Serializable>(1);
        folderProps.put(ContentModel.PROP_NAME, folderName);
        NodeRef recordFolder = this.nodeService.createNode(recordCategory, 
                                                           ContentModel.ASSOC_CONTAINS, 
                                                           QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, folderName), 
                                                           TYPE_RECORD_FOLDER).getChildRef();
        return recordFolder;
    }

	/**
	 * Vital Record Test
	 * 
	 * @throws Exception
	 */
    public void testVitalRecords() throws Exception
    {
        //
        // Create a record folder under a "vital" category
        //
        
        // TODO Don't think I need to do this. Can I reuse the existing January one?
        
        NodeRef vitalRecCategory =
            TestUtilities.getRecordCategory(this.searchService, "Reports", "AIS Audit Records");    
        
        assertNotNull(vitalRecCategory);
        assertEquals("AIS Audit Records",
                this.nodeService.getProperty(vitalRecCategory, ContentModel.PROP_NAME));

        NodeRef vitalRecFolder = this.nodeService.createNode(vitalRecCategory, 
                                                    ContentModel.ASSOC_CONTAINS, 
                                                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
                                                            "March AIS Audit Records"), 
                                                    TYPE_RECORD_FOLDER).getChildRef();
        setComplete();
        endTransaction();
        UserTransaction txn1 = transactionService.getUserTransaction(false);
        txn1.begin();
        
        // Check the Vital Record data
        VitalRecordDefinition vitalRecCatDefinition = rmService.getVitalRecordDefinition(vitalRecCategory);
        assertNotNull("This record category should have a VitalRecordDefinition", vitalRecCatDefinition);
        assertTrue(vitalRecCatDefinition.isVitalRecord());
        
        VitalRecordDefinition vitalRecFolderDefinition = rmService.getVitalRecordDefinition(vitalRecFolder);
        assertNotNull("This record folder should have a VitalRecordDefinition", vitalRecFolderDefinition);
        assertTrue(vitalRecFolderDefinition.isVitalRecord());
        
        assertEquals("The Vital Record reviewPeriod in the folder did not match its parent category",
        		vitalRecFolderDefinition.getReviewPeriod(),
                vitalRecCatDefinition.getReviewPeriod());
        
        // Create a vital record
        NodeRef vitalRecord = this.nodeService.createNode(vitalRecFolder, 
                                                        ContentModel.ASSOC_CONTAINS, 
                                                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI,
                                                                "MyVitalRecord" + System.currentTimeMillis() +".txt"), 
                                                        ContentModel.TYPE_CONTENT).getChildRef();
        
        // Set the content
        ContentWriter writer = this.contentService.getWriter(vitalRecord, ContentModel.PROP_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        writer.setEncoding("UTF-8");
        writer.putContent("There is some content in this record");
        
        rmActionService.executeRecordsManagementAction(vitalRecord, "file");
        
        txn1.commit();
        
        UserTransaction txn2 = transactionService.getUserTransaction(false);
        txn2.begin();
        
        // Check the review schedule
        
        assertTrue(this.nodeService.hasAspect(vitalRecord, ASPECT_VITAL_RECORD));
        VitalRecordDefinition vitalRecDefinition = rmService.getVitalRecordDefinition(vitalRecord);
        assertTrue(vitalRecDefinition.isVitalRecord());
        Date vitalRecordAsOfDate = (Date)this.nodeService.getProperty(vitalRecord, PROP_REVIEW_AS_OF);
        assertNotNull("vitalRecord should have a reviewAsOf date.", vitalRecordAsOfDate);
        
        //
        // Create a record folder under a "non-vital" category
        //
        NodeRef nonVitalRecordCategory = TestUtilities.getRecordCategory(this.searchService, "Reports", "Unit Manning Documents");    
        assertNotNull(nonVitalRecordCategory);
        assertEquals("Unit Manning Documents", this.nodeService.getProperty(nonVitalRecordCategory, ContentModel.PROP_NAME));

        NodeRef nonVitalFolder = this.nodeService.createNode(nonVitalRecordCategory,
                                                           ContentModel.ASSOC_CONTAINS, 
                                                           QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "4th Quarter Unit Manning Documents"), 
                                                           TYPE_RECORD_FOLDER).getChildRef();
        txn2.commit();

        UserTransaction txn3 = transactionService.getUserTransaction(false);
        txn3.begin();
        
        // Check the Vital Record data
        assertFalse(rmService.getVitalRecordDefinition(nonVitalRecordCategory).isVitalRecord());
        assertFalse(rmService.getVitalRecordDefinition(nonVitalFolder).isVitalRecord());
        assertEquals("The Vital Record reviewPeriod in the folder did not match its parent category",
                rmService.getVitalRecordDefinition(nonVitalFolder).getReviewPeriod(),
                rmService.getVitalRecordDefinition(nonVitalRecordCategory).getReviewPeriod());

        
        // Create a record
        NodeRef nonVitalRecord = this.nodeService.createNode(nonVitalFolder, 
                                                        ContentModel.ASSOC_CONTAINS, 
                                                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "MyNonVitalRecord.txt"), 
                                                        ContentModel.TYPE_CONTENT).getChildRef();
        
        // Set content
        writer = this.contentService.getWriter(nonVitalRecord, ContentModel.PROP_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        writer.setEncoding("UTF-8");
        writer.putContent("There is some content in this record");
        
        this.rmActionService.executeRecordsManagementAction(nonVitalRecord, "file");
        
        txn3.commit();
        
        UserTransaction txn4 = transactionService.getUserTransaction(false);
        txn4.begin();
        
        // Check the review schedule
        assertTrue(this.nodeService.hasAspect(nonVitalRecord, ASPECT_VITAL_RECORD) == false);
        
        assertFalse(rmService.getVitalRecordDefinition(nonVitalRecord).isVitalRecord());
        assertEquals("The Vital Record reviewPeriod did not match its parent category",
                rmService.getVitalRecordDefinition(nonVitalRecord).getReviewPeriod(),
                rmService.getVitalRecordDefinition(nonVitalFolder).getReviewPeriod());

        // Declare as a record
        assertTrue(this.nodeService.hasAspect(nonVitalRecord, ASPECT_RECORD)); 
 
        assertTrue("Declared record already on prior to test", 
        	this.nodeService.hasAspect(nonVitalRecord, ASPECT_DECLARED_RECORD) == false);  

        Map<QName, Serializable> propValues = this.nodeService.getProperties(nonVitalRecord);        
        propValues.put(RecordsManagementModel.PROP_PUBLICATION_DATE, new Date());       
        List<String> smList = new ArrayList<String>(2);
        smList.add(FOUO);
        smList.add(NOFORN);
        propValues.put(RecordsManagementModel.PROP_SUPPLEMENTAL_MARKING_LIST, (Serializable)smList);        
        propValues.put(RecordsManagementModel.PROP_MEDIA_TYPE, "mediaTypeValue"); 
        propValues.put(RecordsManagementModel.PROP_FORMAT, "formatValue"); 
        propValues.put(RecordsManagementModel.PROP_DATE_RECEIVED, new Date());
        propValues.put(RecordsManagementModel.PROP_ORIGINATOR, "origValue");
        propValues.put(RecordsManagementModel.PROP_ORIGINATING_ORGANIZATION, "origOrgValue");
        propValues.put(ContentModel.PROP_TITLE, "titleValue");
        this.nodeService.setProperties(nonVitalRecord, propValues);

        this.rmActionService.executeRecordsManagementAction(nonVitalRecord, "declareRecord");
        assertTrue(this.nodeService.hasAspect(nonVitalRecord, ASPECT_RECORD));    
        assertTrue("Declared aspect not set", this.nodeService.hasAspect(nonVitalRecord, ASPECT_DECLARED_RECORD));  
        
        //
        // Now we will change the vital record indicator in the containers above these records
        // and ensure that the change is reflected down to the record.
        //
        
        // 1. Switch parent folder from non-vital to vital.
        Map<QName, Serializable> nonVitalFolderProps = this.nodeService.getProperties(nonVitalFolder);
        nonVitalFolderProps.put(PROP_VITAL_RECORD_INDICATOR, true);
        nonVitalFolderProps.put(PROP_REVIEW_PERIOD, "week|1");
        this.nodeService.setProperties(nonVitalFolder, nonVitalFolderProps);
        
        txn4.commit(); // TODO Shouldn't this trigger the behaviour? Need to check that it's working.
        
        UserTransaction txn5 = transactionService.getUserTransaction(false);
        txn5.begin();
        
        NodeRef formerlyNonVitalRecord = nonVitalRecord;

        assertTrue("Expected VitalRecord aspect not present", nodeService.hasAspect(formerlyNonVitalRecord, ASPECT_VITAL_RECORD));
        VitalRecordDefinition formerlyNonVitalRecordDefinition = rmService.getVitalRecordDefinition(formerlyNonVitalRecord);
        assertNotNull(formerlyNonVitalRecordDefinition);
        
        assertEquals("The Vital Record reviewPeriod is wrong.", new Period("week|1"),
                rmService.getVitalRecordDefinition(formerlyNonVitalRecord).getReviewPeriod());
        assertNotNull("formerlyNonVitalRecord should now have a reviewAsOf date.",
                      nodeService.getProperty(formerlyNonVitalRecord, PROP_REVIEW_AS_OF));


        // 2. Switch parent folder from vital to non-vital.
        Map<QName, Serializable> vitalFolderProps = this.nodeService.getProperties(vitalRecFolder);
        vitalFolderProps.put(PROP_VITAL_RECORD_INDICATOR, false);
        this.nodeService.setProperties(vitalRecFolder, vitalFolderProps);
        
        txn5.commit();
        
        UserTransaction txn6 = transactionService.getUserTransaction(false);
        txn6.begin();
        
        NodeRef formerlyVitalRecord = vitalRecord;

        assertTrue("Unexpected VitalRecord aspect present",
                nodeService.hasAspect(formerlyVitalRecord, ASPECT_VITAL_RECORD) == false);
        VitalRecordDefinition formerlyVitalRecordDefinition = rmService.getVitalRecordDefinition(formerlyVitalRecord);
        assertNotNull(formerlyVitalRecordDefinition);
        assertNull("formerlyVitalRecord should now not have a reviewAsOf date.",
                nodeService.getProperty(formerlyVitalRecord, PROP_REVIEW_AS_OF));
        

        // 3. override the VitalRecordDefinition between Category, Folder, Record and ensure
        // the overrides work
        
        // First switch the non-vital record folder back to vital.
        vitalFolderProps = this.nodeService.getProperties(vitalRecFolder);
        vitalFolderProps.put(PROP_VITAL_RECORD_INDICATOR, true);
        this.nodeService.setProperties(vitalRecFolder, vitalFolderProps);
        
        txn6.commit();
        UserTransaction txn7 = transactionService.getUserTransaction(false);
        txn7.begin();
        
        assertTrue("Unexpected VitalRecord aspect present",
                nodeService.hasAspect(vitalRecord, ASPECT_VITAL_RECORD));

        // The reviewAsOf date should be changing as the parent review periods are updated.
        Date initialReviewAsOfDate = (Date)nodeService.getProperty(vitalRecord, PROP_REVIEW_AS_OF);
        assertNotNull("record should have a reviewAsOf date.",
                initialReviewAsOfDate);

        // Change some of the VitalRecordDefinition in Record Category
        Map<QName, Serializable> recCatProps = this.nodeService.getProperties(vitalRecCategory);
        
        // Run this test twice (after a clean db) and it fails at the below line.
        assertEquals(new Period("week|1"), recCatProps.get(PROP_REVIEW_PERIOD));
        recCatProps.put(PROP_REVIEW_PERIOD, new Period("day|1"));
        this.nodeService.setProperties(vitalRecCategory, recCatProps);
        
        txn7.commit();
        UserTransaction txn8 = transactionService.getUserTransaction(false);
        txn8.begin();

        assertEquals(new Period("day|1"), rmService.getVitalRecordDefinition(vitalRecCategory).getReviewPeriod());
        assertEquals(new Period("day|1"), rmService.getVitalRecordDefinition(vitalRecFolder).getReviewPeriod());

        
        // Change some of the VitalRecordDefinition in Record Folder
        Map<QName, Serializable> folderProps = this.nodeService.getProperties(vitalRecFolder);
        assertEquals(new Period("day|1"), folderProps.get(PROP_REVIEW_PERIOD));
        folderProps.put(PROP_REVIEW_PERIOD, new Period("month|1"));
        this.nodeService.setProperties(vitalRecFolder, folderProps);

        txn8.commit();
        UserTransaction txn9 = transactionService.getUserTransaction(false);
        txn9.begin();

        assertEquals(new Period("day|1"), rmService.getVitalRecordDefinition(vitalRecCategory).getReviewPeriod());
        assertEquals(new Period("month|1"), rmService.getVitalRecordDefinition(vitalRecFolder).getReviewPeriod());

        // Need to commit the transaction to trigger the behaviour that handles changes to VitalRecord Definition.
        txn9.commit();
        UserTransaction txn10 = transactionService.getUserTransaction(false);
        txn10.begin();
        
        Date newReviewAsOfDate = (Date)nodeService.getProperty(vitalRecord, PROP_REVIEW_AS_OF);
        assertNotNull("record should have a reviewAsOf date.", initialReviewAsOfDate);
        assertTrue("reviewAsOfDate should have changed.",
                initialReviewAsOfDate.toString().equals(newReviewAsOfDate.toString()) == false);
        
        // Now clean up after this test.
        nodeService.deleteNode(vitalRecord);
        nodeService.deleteNode(vitalRecFolder);
        nodeService.deleteNode(nonVitalRecord);
        nodeService.deleteNode(nonVitalFolder);
        nodeService.setProperty(vitalRecCategory, PROP_REVIEW_PERIOD, new Period("week|1"));
        
        txn10.commit();
    }
    
    /**
     * Caveat Config
     * 
     * @throws Exception
     */
    public void testCaveatConfig() throws Exception
    {
        setComplete();
        endTransaction();
        
        cleanCaveatConfigData();
        setupCaveatConfigData();
        
        startNewTransaction();
        
        // Test list of allowed values for caveats
        
        List<String> allowedValues = AuthenticationUtil.runAs(new RunAsWork<List<String>>()
        {
            public List<String> doWork()
            {
                // get allowed values for given caveat (for current user)
                return caveatConfigImpl.getRMAllowedValues("rma:smList");
            }
        }, "dfranco");
        
        assertEquals(2, allowedValues.size());
        assertTrue(allowedValues.contains(NOFORN));
        assertTrue(allowedValues.contains(FOUO));
        
        
        allowedValues = AuthenticationUtil.runAs(new RunAsWork<List<String>>()
        {
            public List<String> doWork()
            {
                // get allowed values for given caveat (for current user)
                return caveatConfigImpl.getRMAllowedValues("rma:smList");
            }
        }, "dmartinz");
        
        assertEquals(4, allowedValues.size());
        assertTrue(allowedValues.contains(NOFORN));
        assertTrue(allowedValues.contains(NOCONTRACT));
        assertTrue(allowedValues.contains(FOUO));
        assertTrue(allowedValues.contains(FGI));
        
        
        // Create record category / record folder
        
        NodeRef recordCategory = TestUtilities.getRecordCategory(searchService, "Reports", "AIS Audit Records");
        assertNotNull(recordCategory);
        assertEquals("AIS Audit Records", this.nodeService.getProperty(recordCategory, ContentModel.PROP_NAME));
        
        NodeRef recordFolder = createRecordFolder(recordCategory, "March AIS Audit Records");
        
        // Requires explicit RM permissions
        permissionService.setPermission(recordFolder, PermissionService.ALL_AUTHORITIES, RMPermissionModel.ROLE_USER, true);
        
        setComplete();
        endTransaction();
        
        startNewTransaction();
        
        int expectedChildCount = 1;
        assertEquals(expectedChildCount, nodeService.getChildAssocs(recordFolder).size());
        
        final String RECORD_NAME = "MyRecord.txt";
        final String SOME_CONTENT = "There is some content in this record";
        
        AuthenticationUtil.setFullyAuthenticatedUser("dfranco");
        
        // Create the document
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
        props.put(ContentModel.PROP_NAME, RECORD_NAME);
        NodeRef recordOne = this.nodeService.createNode(recordFolder, 
                                                        ContentModel.ASSOC_CONTAINS, 
                                                        QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, RECORD_NAME), 
                                                        ContentModel.TYPE_CONTENT,
                                                        props).getChildRef();
        
        // Set the content
        ContentWriter writer = this.contentService.getWriter(recordOne, ContentModel.PROP_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        writer.setEncoding("UTF-8");
        writer.putContent(SOME_CONTENT);
        
        // force behaviours
        setComplete();
        endTransaction();
        
        startNewTransaction();
        
        assertTrue(this.nodeService.hasAspect(recordOne, ASPECT_RECORD));
        
        setComplete();
        endTransaction();
        
        //
        // Test caveats (security interceptors) BEFORE setting properties
        //
        
        sanityCheckAccess("dmartinz", recordFolder, recordOne, RECORD_NAME, SOME_CONTENT, true, expectedChildCount);
        sanityCheckAccess("gsmith", recordFolder, recordOne, RECORD_NAME, SOME_CONTENT, true, expectedChildCount);
        sanityCheckAccess("dsandy", recordFolder, recordOne, RECORD_NAME, SOME_CONTENT, true, expectedChildCount);
        
        // Test setting properties (with restricted set of allowed values)
        
        // Set supplemental markings list (on record)
        // TODO - set supplemental markings list (on record folder)
        
        AuthenticationUtil.setFullyAuthenticatedUser("dfranco");
        assertEquals(AccessStatus.ALLOWED, publicServiceAccessService.hasAccess("NodeService", "exists", recordFolder));
        
        try
        {
            startNewTransaction();
            
            // Set smList
            
            Map<QName, Serializable> propValues = new HashMap<QName, Serializable>(1);
            List<String> smList = new ArrayList<String>(3);
            smList.add(FOUO);
            smList.add(NOFORN);
            smList.add(NOCONTRACT);
            propValues.put(RecordsManagementModel.PROP_SUPPLEMENTAL_MARKING_LIST, (Serializable)smList);
            this.nodeService.addProperties(recordOne, propValues);
            
            // force integrity checking
            setComplete();
            endTransaction();
            
            fail("Should fail with integrity exception"); // user 'dfranco' not allowed 'NOCONTRACT'
        }
        catch (IntegrityException ie)
        {
            // expected
        }
        
        try
        {
            startNewTransaction();
            
            // Set smList
            
            Map<QName, Serializable> propValues = new HashMap<QName, Serializable>(1);
            List<String> smList = new ArrayList<String>(2);
            smList.add(FOUO);
            smList.add(NOFORN);
            propValues.put(RecordsManagementModel.PROP_SUPPLEMENTAL_MARKING_LIST, (Serializable)smList);
            this.nodeService.addProperties(recordOne, propValues);
            
            // force integrity checking
            setComplete();
            endTransaction();
        }
        catch (IntegrityException ie)
        {
            fail(""+ie);
        }
        
        startNewTransaction();
        
        @SuppressWarnings("unchecked")
        List<String> smList = (List<String>)this.nodeService.getProperty(recordOne, RecordsManagementModel.PROP_SUPPLEMENTAL_MARKING_LIST);
        assertEquals(2, smList.size());
        assertTrue(smList.contains(NOFORN));
        assertTrue(smList.contains(FOUO));
        
        setComplete();
        endTransaction();
        
        // Set user-defined field (in this case, "prjList" on record)
        
        try
        {
            startNewTransaction();
            
            // Set prjList
            
            Map<QName, Serializable> propValues = new HashMap<QName, Serializable>(1);
            List<String> prjList = new ArrayList<String>(3);
            prjList.add("Project A");
            prjList.add("Project B");
            propValues.put(QName.createQName(RecordsManagementModel.RM_URI, "projectNameList"), (Serializable)prjList);
            this.nodeService.addProperties(recordOne, propValues);
            
            // force integrity checking
            setComplete();
            endTransaction();
            
            fail("Should fail with integrity exception"); // user 'dfranco' not allowed 'Project B'
        }
        catch (IntegrityException ie)
        {
            // expected
        }
        
        try
        {
            startNewTransaction();
            
            // Set prjList
            
            Map<QName, Serializable> propValues = new HashMap<QName, Serializable>(1);
            List<String> prjList = new ArrayList<String>(3);
            prjList.add("Project A");
            propValues.put(QName.createQName(RecordsManagementModel.RM_URI, "projectNameList"), (Serializable)prjList);
            this.nodeService.addProperties(recordOne, propValues);
            
            // force integrity checking
            setComplete();
            endTransaction();
        }
        catch (IntegrityException ie)
        {
            fail(""+ie);
        }
        
        startNewTransaction();
        
        @SuppressWarnings("unchecked")
        List<String> prjList = (List<String>)this.nodeService.getProperty(recordOne, QName.createQName(RecordsManagementModel.RM_URI, "projectNameList"));
        assertEquals(1, prjList.size());
        assertTrue(prjList.contains("Project A"));
        
        setComplete();
        endTransaction();
        
        //
        // Test caveats (security interceptors) AFTER setting properties
        //
        
        sanityCheckAccess("dmartinz", recordFolder, recordOne, RECORD_NAME, SOME_CONTENT, true, expectedChildCount);
        sanityCheckAccess("gsmith", recordFolder, recordOne, RECORD_NAME, SOME_CONTENT, false, expectedChildCount); // denied by rma:prjList ("Project A")
        
        startNewTransaction();
        
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        
        addToGroup("gsmith", "Engineering");
        
        setComplete();
        endTransaction();
        
        sanityCheckAccess("gsmith", recordFolder, recordOne, RECORD_NAME, SOME_CONTENT, true, expectedChildCount);
        sanityCheckAccess("dsandy", recordFolder, recordOne, RECORD_NAME, SOME_CONTENT, false, expectedChildCount); // denied by rma:smList  ("NOFORN", "FOUO")
        
        cleanCaveatConfigData();
    }
    
    private void cleanCaveatConfigData()
    {
        startNewTransaction();
        
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        
        deleteUser("jrangel");
        deleteUser("dmartinz");
        deleteUser("jrogers");
        deleteUser("hmcneil");
        deleteUser("dfranco");
        deleteUser("gsmith");
        deleteUser("eharris");
        deleteUser("bbayless");
        deleteUser("mhouse");
        deleteUser("aly");
        deleteUser("dsandy");
        deleteUser("driggs");
        deleteUser("test1");
        
        deleteGroup("Engineering");
        deleteGroup("Finance");
        deleteGroup("test1");
        
        caveatConfigImpl.updateOrCreateCaveatConfig("{}"); // empty config !
        
        setComplete();
        endTransaction();
    }
    
    private void setupCaveatConfigData()
    {
        startNewTransaction();
        
        // Switch to admin
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        
        // Create test users/groups (if they do not already exist)
        
        createUser("jrangel");
        createUser("dmartinz");
        createUser("jrogers");
        createUser("hmcneil");
        createUser("dfranco");
        createUser("gsmith");
        createUser("eharris");
        createUser("bbayless");
        createUser("mhouse");
        createUser("aly");
        createUser("dsandy");
        createUser("driggs");
        createUser("test1");
        
        createGroup("Engineering");
        createGroup("Finance");
        createGroup("test1");
        
        addToGroup("jrogers", "Engineering");
        addToGroup("dfranco", "Finance");
        
        // not in grouo to start with - added later
        //addToGroup("gsmith", "Engineering");
        
        
        URL url = AbstractContentTransformerTest.class.getClassLoader().getResource("testCaveatConfig2.json"); // from test-resources
        assertNotNull(url);
        File file = new File(url.getFile());
        assertTrue(file.exists());
        
        caveatConfigImpl.updateOrCreateCaveatConfig(file);
        
        setComplete();
        endTransaction();
    }
    
    protected void createUser(String userName)
    {
        if (! authenticationService.authenticationExists(userName))
        {
            authenticationService.createAuthentication(userName, "PWD".toCharArray());
        }
        
        if (! personService.personExists(userName))
        {
            PropertyMap ppOne = new PropertyMap(4);
            ppOne.put(ContentModel.PROP_USERNAME, userName);
            ppOne.put(ContentModel.PROP_FIRSTNAME, "firstName");
            ppOne.put(ContentModel.PROP_LASTNAME, "lastName");
            ppOne.put(ContentModel.PROP_EMAIL, "email@email.com");
            ppOne.put(ContentModel.PROP_JOBTITLE, "jobTitle");
            
            personService.createPerson(ppOne);
        }
    }
    
    protected void deleteUser(String userName)
    {
        if (personService.personExists(userName))
        {
            personService.deletePerson(userName);
        }
        
        if (authenticationService.authenticationExists(userName))
        {
            authenticationService.deleteAuthentication(userName);
        }
    }
    
    protected void createGroup(String groupShortName)
    {
        createGroup(null, groupShortName);
    }
    
    protected void createGroup(String parentGroupShortName, String groupShortName)
    {
        if (parentGroupShortName != null)
        {
            String parentGroupFullName = authorityService.getName(AuthorityType.GROUP, parentGroupShortName);
            if (authorityService.authorityExists(parentGroupFullName) == false)
            {
                authorityService.createAuthority(AuthorityType.GROUP, groupShortName, groupShortName, null);
                authorityService.addAuthority(parentGroupFullName, groupShortName);
            }
        }
        else
        {
            authorityService.createAuthority(AuthorityType.GROUP, groupShortName, groupShortName, null);
        }
    }
    
    protected void deleteGroup(String groupShortName)
    {
        String groupFullName = authorityService.getName(AuthorityType.GROUP, groupShortName);
        if (authorityService.authorityExists(groupFullName) == true)
        {
            authorityService.deleteAuthority(groupFullName);
        }
    }
    
    protected void addToGroup(String authorityName, String groupShortName)
    {
        authorityService.addAuthority(authorityService.getName(AuthorityType.GROUP, groupShortName), authorityName);
    }
    
    protected void removeFromGroup(String authorityName, String groupShortName)
    {
        authorityService.removeAuthority(authorityService.getName(AuthorityType.GROUP, groupShortName), authorityName);
    }
    
    private void sanityCheckAccess(String user, NodeRef recordFolder, NodeRef record, String expectedName, String expectedContent, boolean expectedAllowed, int baseCount)
    {
        //startNewTransaction();
        
        AuthenticationUtil.setFullyAuthenticatedUser(user);
        
        // Sanity check search service - eg. query
        
        String query = "ID:"+LuceneQueryParser.escape(record.toString());
        ResultSet rs = this.searchService.query(SPACES_STORE, SearchService.LANGUAGE_LUCENE, query);
        
        if (expectedAllowed)
        {
            assertEquals(1, rs.length());
            assertEquals(record.toString(), rs.getNodeRef(0).toString());
        }
        else
        {
            assertEquals(0, rs.length());
        }
        
        // Sanity check node service - eg. getProperty, getChildAssocs
        
        try
        {
            Serializable value = this.nodeService.getProperty(record, ContentModel.PROP_NAME);
            
            if (expectedAllowed)
            {
                assertNotNull(value);
                assertEquals(expectedName, (String)value);
            }
            else
            {
                fail("Unexpected - access should be denied by caveats");
            }
        }
        catch (AccessDeniedException ade)
        {
            if (expectedAllowed)
            {
                fail("Unexpected - access should be allowed by caveats");
            }
            
            // expected
        }
        
        List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(recordFolder);
        
        if (expectedAllowed)
        {
            assertEquals(baseCount+1, childAssocs.size());
            assertEquals(record.toString(), childAssocs.get(baseCount).getChildRef().toString());
        }
        else
        {
            assertEquals(baseCount, childAssocs.size());
        }
        
        // Sanity check content service - eg. getReader
        
        try
        {
            ContentReader reader = this.contentService.getReader(record, ContentModel.PROP_CONTENT);
            
            if (expectedAllowed)
            {
                assertNotNull(reader);
                assertEquals(expectedContent, reader.getContentString());
            }
            else
            {
                fail("Unexpected - access should be denied by caveats");
            }
        }
        catch (AccessDeniedException ade)
        {
            if (expectedAllowed)
            {
                fail("Unexpected - access should be allowed by caveats");
            }
            
            // expected
        }
        
        //setComplete();
        //endTransaction();
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
	 * that each has a cm:description and that it is non-null.
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
        String typeQuery = "TYPE:\"" + TYPE_RECORD_CATEGORY + "\"";
        ResultSet types = this.searchService.query(SPACES_STORE, SearchService.LANGUAGE_LUCENE, typeQuery);
        
        return types.getNodeRefs();
    }
}
