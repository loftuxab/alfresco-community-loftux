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
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.transaction.TransactionUtil;
import org.alfresco.repo.transaction.TransactionUtil.TransactionWork;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;

/**
 * Tests the folder record behaviour specified in the record_folder.js script
 * 
 * @author Roy Wetherall
 */
public class IdGenerationTest extends BaseRecordManagementTest 
{	
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
				IdGenerationTest.filePlan, 
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
				ContentModel.TYPE_FOLDER).getChildRef();
		final NodeRef folder2 = this.nodeService.createNode(
				IdGenerationTest.filePlan, 
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, GUID.generate()),
				ContentModel.TYPE_FOLDER).getChildRef();
		final NodeRef folder3 = this.nodeService.createNode(
				IdGenerationTest.filePlan, 
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
				assertTrue(((String)IdGenerationTest.this.nodeService.getProperty(folder1, ContentModel.PROP_NAME)).contains("Folder 001"));
				assertEquals(
						recordCatId + "-001", 
						IdGenerationTest.this.nodeService.getProperty(
								folder1, 
								QName.createQName("http://www.alfresco.org/model/record/1.0", "recordIdentifier")));
				
				assertTrue(((String)IdGenerationTest.this.nodeService.getProperty(folder2, ContentModel.PROP_NAME)).contains("Folder 002"));
				assertEquals(
						recordCatId + "-002", 
						IdGenerationTest.this.nodeService.getProperty(
								folder2, 
								QName.createQName("http://www.alfresco.org/model/record/1.0", "recordIdentifier")));
				
				assertTrue(((String)IdGenerationTest.this.nodeService.getProperty(folder3, ContentModel.PROP_NAME)).contains("Folder 003"));
				assertEquals(
						recordCatId + "-003", 
						IdGenerationTest.this.nodeService.getProperty(
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
				IdGenerationTest.filePlan, 
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
				IdGenerationTest.filePlan, 
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
				IdGenerationTest.filePlan, 
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
				assertTrue(((String)IdGenerationTest.this.nodeService.getProperty(doc1, ContentModel.PROP_NAME)).contains("004"));
				assertTrue(((String)IdGenerationTest.this.nodeService.getProperty(doc2, ContentModel.PROP_NAME)).contains("005"));
				assertTrue(((String)IdGenerationTest.this.nodeService.getProperty(doc3, ContentModel.PROP_NAME)).contains("006"));
				return null;
			}
        });			
	}
	
	public void testCleanUp()
	{
		cleanUp();
	}
}
