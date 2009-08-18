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

import java.util.List;

import javax.transaction.UserTransaction;

import org.alfresco.module.org_alfresco_module_dod5015.CustomModelUtil;
import org.alfresco.module.org_alfresco_module_dod5015.DOD5015Model;
import org.alfresco.module.org_alfresco_module_dod5015.RecordsManagementAdminServiceImpl;
import org.alfresco.module.org_alfresco_module_dod5015.action.impl.DefineCustomAssociationAction;
import org.alfresco.repo.dictionary.M2Aspect;
import org.alfresco.repo.dictionary.M2Association;
import org.alfresco.repo.dictionary.M2ClassAssociation;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.BaseSpringTest;

/**
 * TODO Delete this class
 * 
 * @author Neil McErlean
 */
public class DynamicModellingSystemTest extends BaseSpringTest implements DOD5015Model
{    
	private CustomModelUtil customModelUtil;
	
	private NodeRef filePlan;
	
    private ContentService contentService;
    private DictionaryService dictionaryService;
	private ImporterService importService;
    private NamespaceService namespaceService;
    private NodeService nodeService;
	private SearchService searchService;
	private TransactionService transactionService;
	
	private PermissionService permissionService;
	
	@Override
	protected void onSetUpInTransaction() throws Exception 
	{
		super.onSetUpInTransaction();

        this.dictionaryService = (DictionaryService)this.applicationContext.getBean("DictionaryService");
        this.contentService = (ContentService)this.applicationContext.getBean("ContentService");
		this.importService = (ImporterService)this.applicationContext.getBean("importerComponent");
		this.namespaceService = (NamespaceService)this.applicationContext.getBean("NamespaceService");
        this.nodeService = (NodeService)this.applicationContext.getBean("NodeService");
        this.permissionService = (PermissionService)this.applicationContext.getBean("PermissionService");
        this.searchService = (SearchService)this.applicationContext.getBean("SearchService");
		this.transactionService = (TransactionService)this.applicationContext.getBean("TransactionService");
		
		// Set the current security context as admin
		AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
		
		// Get the test data
		setUpTestData();
		
		customModelUtil = new CustomModelUtil();
		customModelUtil.setContentService(contentService);
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
        }
    }
    
    public void testCreateAndDeleteStandardAssociation() throws Exception {
    	String name = createStandardAssociationDefinition();
    	
        setComplete();
        endTransaction();
        
        UserTransaction tx1 = transactionService.getUserTransaction(false);
        tx1.begin();

        deleteStandardAssociationDefinition(name);
        
        tx1.commit();
    }
    
    public void testCreateAndRenameStandardAssociation() throws Exception {
    	String name = createStandardAssociationDefinition();
    	
        setComplete();
        endTransaction();
        
        UserTransaction tx1 = transactionService.getUserTransaction(false);
        tx1.begin();

        renameStandardAssociationDefinition(name);
        
        tx1.commit();
    }
    
    private String createStandardAssociationDefinition() {
    	M2Model m2Model = customModelUtil.readCustomContentModel();

        M2Aspect customAssocsAspect = m2Model.getAspect(RecordsManagementAdminServiceImpl.RMC_CUSTOM_ASSOCS);

        String name = "rmc:std_" + System.currentTimeMillis();
		M2Association newAssoc = customAssocsAspect.createAssociation(name);
        newAssoc.setTargetClassName(DefineCustomAssociationAction.RMA_RECORD);
        
        customModelUtil.writeCustomContentModel(m2Model);
        
        return name;
    }
    
    private String deleteStandardAssociationDefinition(String name) {
    	M2Model m2Model = customModelUtil.readCustomContentModel();

        M2Aspect customAssocsAspect = m2Model.getAspect(RecordsManagementAdminServiceImpl.RMC_CUSTOM_ASSOCS);
        
        customAssocsAspect.removeAssociation(name);

        customModelUtil.writeCustomContentModel(m2Model);
        
        return name;
    }

    private String renameStandardAssociationDefinition(String oldname) {
    	M2Model m2Model = customModelUtil.readCustomContentModel();

        M2Aspect customAssocsAspect = m2Model.getAspect(RecordsManagementAdminServiceImpl.RMC_CUSTOM_ASSOCS);

        String newName = "rmc:std_" + System.currentTimeMillis();
        
        M2ClassAssociation assoc = customAssocsAspect.getAssociation(oldname);
        assoc.setName(newName);
        
        customModelUtil.writeCustomContentModel(m2Model);
        
        return newName;
    }
    

    private List<NodeRef> retrieveJanuaryAISVitalFolders()
    {
        String typeQuery = "TYPE:\"" + TYPE_RECORD_FOLDER + "\" AND @cm\\:name:\"January AIS Audit Records\"";
        ResultSet types = this.searchService.query(TestUtilities.SPACES_STORE, SearchService.LANGUAGE_LUCENE, typeQuery);
        
        final List<NodeRef> resultNodeRefs = types.getNodeRefs();
        return resultNodeRefs;
    }
}
