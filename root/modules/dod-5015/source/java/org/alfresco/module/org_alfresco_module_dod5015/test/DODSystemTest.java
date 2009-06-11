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
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementActionService;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.caveat.RMCaveatConfigImpl;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.transform.AbstractContentTransformerTest;
import org.alfresco.repo.node.integrity.IntegrityException;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParser;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.BaseSpringTest;
import org.alfresco.util.ISO9075;
import org.alfresco.util.PropertyMap;

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
	private RMCaveatConfigImpl caveatConfigImpl;
	
	private AuthenticationService authenticationService;
	private PersonService personService;
	private AuthorityService authorityService;
	private PermissionService permissionService;
	
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
		this.importService = (ImporterService)this.applicationContext.getBean("ImporterService");
		this.contentService = (ContentService)this.applicationContext.getBean("ContentService");
		this.rmService = (RecordsManagementActionService)this.applicationContext.getBean("RecordsManagementActionService");
		this.transactionService = (TransactionService)this.applicationContext.getBean("TransactionService");
		
		this.caveatConfigImpl = (RMCaveatConfigImpl)this.applicationContext.getBean("caveatConfigImpl");
		
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
        
        NodeRef recordCategory = getRecordCategory("Reports", "AIS Audit Records");
        assertNotNull(recordCategory);
        assertEquals("AIS Audit Records", this.nodeService.getProperty(recordCategory, ContentModel.PROP_NAME));
        
        Map<QName, Serializable> folderProps = new HashMap<QName, Serializable>(1);
        folderProps.put(ContentModel.PROP_NAME, "March AIS Audit Records");
        NodeRef recordFolder = this.nodeService.createNode(recordCategory, 
                                                           ContentModel.ASSOC_CONTAINS, 
                                                           QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "March AIS Audit Records"), 
                                                           TYPE_RECORD_FOLDER).getChildRef();
        
        // temp
        permissionService.setPermission(recordFolder, PermissionService.ALL_AUTHORITIES, PermissionService.ADD_CHILDREN, true);
        
        setComplete();
        endTransaction();
        
        startNewTransaction();
        
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
        
        sanityCheckAccess("dmartinz", recordFolder, recordOne, RECORD_NAME, SOME_CONTENT, true);
        sanityCheckAccess("gsmith", recordFolder, recordOne, RECORD_NAME, SOME_CONTENT, true);
        sanityCheckAccess("dsandy", recordFolder, recordOne, RECORD_NAME, SOME_CONTENT, true);
        
        // Test setting properties (with restricted set of allowed values)
        
        // Set supplemental markings list (on record)
        // TODO - set supplemental markings list (on record folder)
        
        AuthenticationUtil.setFullyAuthenticatedUser("dfranco");
        
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
        
        sanityCheckAccess("dmartinz", recordFolder, recordOne, RECORD_NAME, SOME_CONTENT, true);
        sanityCheckAccess("gsmith", recordFolder, recordOne, RECORD_NAME, SOME_CONTENT, false); // denied by rma:prjList ("Project A")
        
        startNewTransaction();
        
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        
        addToGroup("gsmith", "Engineering");
        
        setComplete();
        endTransaction();
        
        sanityCheckAccess("gsmith", recordFolder, recordOne, RECORD_NAME, SOME_CONTENT, true);
        sanityCheckAccess("dsandy", recordFolder, recordOne, RECORD_NAME, SOME_CONTENT, false); // denied by rma:smList  ("NOFORN", "FOUO")
        
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
    
    private void sanityCheckAccess(String user, NodeRef recordFolder, NodeRef record, String expectedName, String expectedContent, boolean expectedAllowed)
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
            assertEquals(1, childAssocs.size());
            assertEquals(record.toString(), childAssocs.get(0).getChildRef().toString());
        }
        else
        {
            assertEquals(0, childAssocs.size());
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
