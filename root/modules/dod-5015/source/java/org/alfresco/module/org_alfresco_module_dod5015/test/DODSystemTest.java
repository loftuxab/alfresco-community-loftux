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
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementModel;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementService;
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
import org.alfresco.util.BaseSpringTest;
import org.alfresco.util.ISO9075;

/**
 * 
 * 
 * @author Roy Wetherall
 */
public class DODSystemTest extends BaseSpringTest 
{    
	protected static StoreRef SPACES_STORE = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
	
	private NodeRef filePlan;
	
	private NodeService nodeService;
	private SearchService searchService;
	private ImporterService importService;
	private ContentService contentService;
	private RecordsManagementService rmService;
	
	private AuthenticationComponent authenticationComponent;
	
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
		this.rmService = (RecordsManagementService)this.applicationContext.getBean("RecordsManagementService");
		
		// Set the current security context as admin
		this.authenticationComponent.setCurrentUser(AuthenticationUtil.getSystemUserName());	
		
		// Get the test data
		setUpTestData();
	}
	
	private void setUpTestData()
	{
        filePlan = TestUtilities.loadFilePlanData(null, this.nodeService, this.importService);
	}
	
	private NodeRef getRecordCategory(String seriesName, String categoryName)
	{
	    SearchParameters searchParameters = new SearchParameters();
	    searchParameters.addStore(SPACES_STORE);
	    String query = "PATH:\"rma:filePlan/cm:" + ISO9075.encode(seriesName) + "/cm:" + ISO9075.encode(categoryName) + "\"";
	    System.out.println("Query: " + query);
	    searchParameters.setQuery(query);
	    searchParameters.setLanguage(SearchService.LANGUAGE_LUCENE);
	    ResultSet rs = this.searchService.query(searchParameters);
	    
        //setComplete();
        //endTransaction();
	    
	    return rs.getNodeRef(0);
	}
	
	public void testBasicFileingTest()
	{
	    // Get a record category to file into
	    NodeRef recordCategory = getRecordCategory("Reports", "AIS Audit Records");	    
	    
	    assertNotNull(recordCategory);
	    System.out.println(this.nodeService.getProperty(recordCategory, ContentModel.PROP_NAME));
	    
	    /* Programatic filing */
	    
	    // Create the document
	    NodeRef recordOne = this.nodeService.createNode(recordCategory, 
	                                                    ContentModel.ASSOC_CONTAINS, 
	                                                    QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "MyRecord.txt"), 
	                                                    ContentModel.TYPE_CONTENT).getChildRef();
	    
	    // Set the content
	    ContentWriter writer = this.contentService.getWriter(recordOne, ContentModel.PROP_CONTENT, true);
	    writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
	    writer.setEncoding("UTF-8");
	    writer.putContent("There is some content in this record");
	    
	    // Checked that the document has been marked as incomplete
	    //assertTrue(this.nodeService.hasAspect(recordOne, RecordsManagementModel.ASPECT_INCOMPLETE_RECORD));	    
	    
	    // File a document in the record category
	    Map<String, Serializable> stateContext = new HashMap<String, Serializable>(5);
	    stateContext.put("fileableNode", recordCategory);
	    Map<String, Serializable> propValues = new HashMap<String, Serializable>(5);
	    propValues.put(RecordsManagementModel.PROP_PUBLICATION_DATE.toString(), new Date());
	    propValues.put(RecordsManagementModel.PROP_SUPPLEMENTAL_MARKING_LIST.toString(), "markingListValue");
	    propValues.put(RecordsManagementModel.PROP_MEDIA_TYPE.toString(), "mediaTypeValue"); 
	    propValues.put(RecordsManagementModel.PROP_FORMAT.toString(), "formatValue"); 
	    propValues.put(RecordsManagementModel.PROP_DATE_RECEIVED.toString(), new Date());
	    stateContext.put("recordProperties", (Serializable)propValues);
	    rmService.addRecordState(recordOne, "filed", stateContext);
	    
	    // Check the values of the filed record
	    assertNotNull(this.nodeService.getProperty(recordOne, RecordsManagementModel.PROP_REVIEW_AS_OF));
	    //assertNotNull(this.nodeService.getProperty(recordOne, RecordsManagementModel.PROP_CUT_OFF_AS_OF));
	   // System.out.println("Cut off as of date: " + this.nodeService.getProperty(recordOne, RecordsManagementModel.PROP_CUT_OFF_AS_OF).toString());
	    System.out.println("Review as of date: " + this.nodeService.getProperty(recordOne, RecordsManagementModel.PROP_REVIEW_AS_OF).toString());
	    
	    // TODO commit to check that the filed record can be created correctly
	    
	}
        
        

}
