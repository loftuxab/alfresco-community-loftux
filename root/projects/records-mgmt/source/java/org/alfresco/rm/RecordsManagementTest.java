/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.rm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.transaction.TransactionUtil;
import org.alfresco.repo.transaction.TransactionUtil.TransactionWork;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.BaseSpringTest;
import org.alfresco.util.GUID;

/**
 * Tests the folder record behaviour specified in the record_folder.js script
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementTest extends BaseSpringTest 
{
	protected static StoreRef SPACES_STORE = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
	
	protected NodeService nodeService;
	protected SearchService searchService;
	protected CopyService copyService;
	protected RuleService ruleService;
	protected TransactionService transactionService;
	protected ContentService contentService;
	
	protected static NodeRef companyHome;
	protected static NodeRef filePlan;
	
	@Override
	protected void onSetUpBeforeTransaction() 
		throws Exception 
	{
		// Get references to the relevant services
		this.nodeService = (NodeService)this.applicationContext.getBean("nodeService");
		this.searchService = (SearchService)this.applicationContext.getBean("searchService");
		this.copyService = (CopyService)this.applicationContext.getBean("copyService");
		this.ruleService = (RuleService)this.applicationContext.getBean("ruleService");
		this.transactionService = (TransactionService)this.applicationContext.getBean("transactionComponent");
		this.contentService = (ContentService)this.applicationContext.getBean("contentService");
	}
	
	protected void initialise()
	{	
		// Update the scripts held in the repository
		NodeRef nodeRef = this.searchService.query(SPACES_STORE, SearchService.LANGUAGE_XPATH, "/app:company_home/app:dictionary/app:scripts/cm:record_folder.js").getNodeRefs().get(0);
		ContentWriter contentWriter = this.contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);		
		contentWriter.putContent(Thread.currentThread().getContextClassLoader().getResourceAsStream("alfresco/rm/record_folder.js"));		
		NodeRef nodeRef2 = this.searchService.query(SPACES_STORE, SearchService.LANGUAGE_XPATH, "/app:company_home/app:dictionary/app:scripts/cm:record_lifecycle.js").getNodeRefs().get(0);
		ContentWriter contentWriter2 = this.contentService.getWriter(nodeRef2, ContentModel.PROP_CONTENT, true);		
		contentWriter2.putContent(Thread.currentThread().getContextClassLoader().getResourceAsStream("alfresco/rm/record_lifecycle.js"));		
		NodeRef nodeRef3 = this.searchService.query(SPACES_STORE, SearchService.LANGUAGE_XPATH, "/app:company_home/app:dictionary/app:scripts/cm:record_setup.js").getNodeRefs().get(0);
		ContentWriter contentWriter3 = this.contentService.getWriter(nodeRef3, ContentModel.PROP_CONTENT, true);		
		contentWriter3.putContent(Thread.currentThread().getContextClassLoader().getResourceAsStream("alfresco/rm/record_setup.js"));
		
		// Get a reference to the company home node
		ResultSet results1 = this.searchService.query(RecordsManagementTest.SPACES_STORE, SearchService.LANGUAGE_XPATH, "app:company_home");
		RecordsManagementTest.companyHome = results1.getNodeRefs().get(0);
		
		// Create a file plan based on the template file plan space
		ResultSet results2 = this.searchService.query(RecordsManagementTest.SPACES_STORE, SearchService.LANGUAGE_XPATH, "app:company_home/app:dictionary/app:space_templates/cm:File_x0020_Plan");
		NodeRef template = results2.getNodeRefs().get(0);
		assertNotNull("The file plan template is missing.", template);
		
		RecordsManagementTest.filePlan = this.copyService.copy(template, RecordsManagementTest.companyHome, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "Test File Plan"), true);
		this.nodeService.setProperty(RecordsManagementTest.filePlan, ContentModel.PROP_NAME, "Test File Plan " + GUID.generate());
		
		// Do a simple check to ensure that the created file plan has the required rules
		List<Rule> rules = this.ruleService.getRules(RecordsManagementTest.filePlan);
		assertNotNull(rules);
		assertEquals(4, rules.size());
		
		// Force the commit
		setComplete();
        endTransaction();
	}
	
	protected void cleanUp()
	{
		this.nodeService.deleteNode(RecordsManagementTest.filePlan);
		
		// Force the commit
		setComplete();
        endTransaction();
	}
	
	public void testInitialise()
	{	
		initialise();
	}
	
	public void testFolderIdGeneration()
	{
		final String recordCatId = (String)this.nodeService.getProperty(
				filePlan, 
				QName.createQName("http://www.alfresco.org/model/record/1.0", "recordCategoryIdentifier"));
		
		// Create a some new sub-folders
		final NodeRef folder1 = this.nodeService.createNode(
				RecordsManagementTest.filePlan, 
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
				ContentModel.TYPE_FOLDER).getChildRef();
		final NodeRef folder2 = this.nodeService.createNode(
				RecordsManagementTest.filePlan, 
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
				ContentModel.TYPE_FOLDER).getChildRef();
		final NodeRef folder3 = this.nodeService.createNode(
				RecordsManagementTest.filePlan, 
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
				ContentModel.TYPE_FOLDER).getChildRef();
		
		// Force the commit
		setComplete();
        endTransaction();
	
        TransactionUtil.executeInUserTransaction(this.transactionService, new TransactionWork<Object>()
        {
			public Object doWork() throws Exception 
			{				
				assertTrue(((String)RecordsManagementTest.this.nodeService.getProperty(folder1, ContentModel.PROP_NAME)).contains("Folder 001"));
				assertEquals(
						recordCatId + "-001", 
						RecordsManagementTest.this.nodeService.getProperty(
								folder1, 
								QName.createQName("http://www.alfresco.org/model/record/1.0", "recordIdentifier")));
				
				assertTrue(((String)RecordsManagementTest.this.nodeService.getProperty(folder2, ContentModel.PROP_NAME)).contains("Folder 002"));
				assertEquals(
						recordCatId + "-002", 
						RecordsManagementTest.this.nodeService.getProperty(
								folder2, 
								QName.createQName("http://www.alfresco.org/model/record/1.0", "recordIdentifier")));
				
				assertTrue(((String)RecordsManagementTest.this.nodeService.getProperty(folder3, ContentModel.PROP_NAME)).contains("Folder 003"));
				assertEquals(
						recordCatId + "-003", 
						RecordsManagementTest.this.nodeService.getProperty(
								folder3, 
								QName.createQName("http://www.alfresco.org/model/record/1.0", "recordIdentifier")));
				
				return null;
			}
        });			
	}	

	public void testContentIdGeneration()
	{
		// Create a some new sub-folders
		Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
		props.put(ContentModel.PROP_NAME, "MyDoc" + GUID.generate() + ".txt");
		final NodeRef doc1 = this.nodeService.createNode(
				RecordsManagementTest.filePlan, 
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
				ContentModel.TYPE_CONTENT,
				props).getChildRef();
		ContentWriter cw1 = this.contentService.getWriter(doc1, ContentModel.PROP_CONTENT, true);
		cw1.setEncoding("UTF-8");
		cw1.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
		cw1.putContent("Some content");
		Map<QName, Serializable> props2 = new HashMap<QName, Serializable>(1);
		props2.put(ContentModel.PROP_NAME, "MyDoc" + GUID.generate() + ".txt");
		final NodeRef doc2 = this.nodeService.createNode(
				RecordsManagementTest.filePlan, 
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
				ContentModel.TYPE_CONTENT,
				props2).getChildRef();
		ContentWriter cw2 = this.contentService.getWriter(doc2, ContentModel.PROP_CONTENT, true);
		cw2.setEncoding("UTF-8");
		cw2.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
		cw2.putContent("Some content");
		Map<QName, Serializable> props3 = new HashMap<QName, Serializable>(1);
		props3.put(ContentModel.PROP_NAME, "MyDoc" + GUID.generate() + ".txt");
		final NodeRef doc3 = this.nodeService.createNode(
				RecordsManagementTest.filePlan, 
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
				ContentModel.TYPE_CONTENT,
				props3).getChildRef();
		ContentWriter cw3 = this.contentService.getWriter(doc3, ContentModel.PROP_CONTENT, true);
		cw3.setEncoding("UTF-8");
		cw3.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
		cw3.putContent("Some content");
		
		// Force the commit
		setComplete();
        endTransaction();
	
        TransactionUtil.executeInUserTransaction(this.transactionService, new TransactionWork<Object>()
        {
			public Object doWork() throws Exception 
			{
				assertTrue(((String)RecordsManagementTest.this.nodeService.getProperty(doc1, ContentModel.PROP_NAME)).contains("004"));
				assertTrue(((String)RecordsManagementTest.this.nodeService.getProperty(doc2, ContentModel.PROP_NAME)).contains("005"));
				assertTrue(((String)RecordsManagementTest.this.nodeService.getProperty(doc3, ContentModel.PROP_NAME)).contains("006"));
				return null;
			}
        });			
	}
	
	public void testCleanUp()
	{
		cleanUp();
	}

}
