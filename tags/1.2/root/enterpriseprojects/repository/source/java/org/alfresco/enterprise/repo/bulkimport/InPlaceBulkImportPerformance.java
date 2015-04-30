/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
package org.alfresco.enterprise.repo.bulkimport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.alfresco.enterprise.repo.bulkimport.impl.InPlaceNodeImporterFactory;
import org.alfresco.model.ContentModel;
import org.alfresco.query.CannedQueryPageDetails;
import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.bulkimport.BulkImportParameters;
import org.alfresco.repo.bulkimport.NodeImporter;
import org.alfresco.repo.bulkimport.impl.MultiThreadedBulkFilesystemImporter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;

/**
 * Tests the in-place bulk importer. Copy some content _into_ your content store and set RELATIVE_PATH to 
 * the relative path of the root of this content. For example, if you copy some content into your content
 * store at 2010/10/8 then RELATIVE_PATH should be set to "2010/10/8".
 * 
 * @since 4.0
 *
 * Note: source/test-resources/bulkimporttest needs to be on the classpath.
 */
public class InPlaceBulkImportPerformance
{
    private final static String STORE = "default";
    private final static String RELATIVE_PATH = "2010";
    
    protected static ApplicationContext ctx = null;

    protected FileFolderService fileFolderService;
    protected NodeService nodeService;
    protected TransactionService transactionService;
    protected ContentService contentService;
    protected UserTransaction txn = null;
    protected MultiThreadedBulkFilesystemImporter bulkImporter;
    protected MultiThreadedBulkFilesystemImporter topDownBulkFilesystemImporter;
    protected InPlaceNodeImporterFactory inPlaceNodeImporterFactory;
    protected ServiceRegistry serviceRegistry;
    protected SearchService searchService;
    protected NamespaceService namespaceService;
    
    protected NodeRef rootNodeRef;
    protected FileInfo topLevelFolder;

    static
    {
        startContext(new String[] {"classpath:bulkimporttest/alfresco/overrides-context.xml", ApplicationContextHelper.CONFIG_LOCATIONS[0]});
    }
    
    protected static void startContext()
    {
        ctx = ApplicationContextHelper.getApplicationContext();
    }

    protected static void startContext(String[] configLocations)
    {
        ctx = ApplicationContextHelper.getApplicationContext(configLocations);        
    }

    public InPlaceBulkImportPerformance() throws Exception
    {
        setup();
    }

    protected void stopContext()
    {
        ApplicationContextHelper.closeApplicationContext();        
    }

    public void setup() throws Exception
    {
        nodeService = (NodeService)ctx.getBean("nodeService");
        fileFolderService = (FileFolderService)ctx.getBean("fileFolderService");
        transactionService = (TransactionService)ctx.getBean("transactionService");
        bulkImporter = (MultiThreadedBulkFilesystemImporter)ctx.getBean("bulkFilesystemImporter");
        contentService = (ContentService)ctx.getBean("contentService");
        inPlaceNodeImporterFactory = (InPlaceNodeImporterFactory)ctx.getBean("inPlaceNodeImporterFactory");
        serviceRegistry = (ServiceRegistry) ctx.getBean("ServiceRegistry");
        searchService = serviceRegistry.getSearchService();
        namespaceService = serviceRegistry.getNamespaceService();
        
        AuthenticationUtil.setRunAsUser("admin");

        String s = "BulkFilesystemImport" + System.currentTimeMillis();

        txn = transactionService.getUserTransaction();
        txn.begin();

        StoreRef workspaceSpacesStore = new StoreRef("workspace", "SpacesStore");
        
        List<NodeRef> nodeRefs = searchService.selectNodes(nodeService.getRootNode(workspaceSpacesStore), "/app:company_home", null, namespaceService, false);
        NodeRef companyHomeNodeRef = nodeRefs.get(0);
        
        topLevelFolder = fileFolderService.create(companyHomeNodeRef, s, ContentModel.TYPE_FOLDER);

        txn.commit();

        txn = transactionService.getUserTransaction();
        txn.begin();
    }

    protected List<FileInfo> getFolders(NodeRef parent, String pattern)
    {
        PagingResults<FileInfo> page = fileFolderService.list(parent, false, true, pattern, null, null, new PagingRequest(CannedQueryPageDetails.DEFAULT_PAGE_SIZE));
        List<FileInfo> folders = page.getPage();
        return folders;
    }

    protected List<FileInfo> getFiles(NodeRef parent, String pattern)
    {
        PagingResults<FileInfo> page = fileFolderService.list(parent, true, false, pattern, null, null, new PagingRequest(CannedQueryPageDetails.DEFAULT_PAGE_SIZE));
        List<FileInfo> files = page.getPage();
        return files;
    }
    
    protected Map<String, FileInfo> toMap(List<FileInfo> list)
    {
        Map<String, FileInfo> map = new HashMap<String, FileInfo>(list.size());
        for(FileInfo fileInfo : list)
        {
            map.put(fileInfo.getName(), fileInfo);
        }
        return map;
    }
    
    public void striping(String contentStore, String contentStorePath) throws Exception
    {
        NodeRef folderNode = topLevelFolder.getNodeRef();

        // import with in-place importer
        NodeImporter nodeImporter = inPlaceNodeImporterFactory.getNodeImporter(contentStore, contentStorePath);
        BulkImportParameters bulkImportParameters = new BulkImportParameters();
        bulkImportParameters.setTarget(folderNode);
        bulkImportParameters.setReplaceExisting(true);
        bulkImportParameters.setBatchSize(80);
        bulkImportParameters.setNumThreads(4);
        bulkImportParameters.setDisableRulesService(true);
        bulkImporter.bulkImport(bulkImportParameters, nodeImporter);
    }
    
    public void execute(String contentStore, String contentStorePath) throws Exception
    {
        striping(contentStore, contentStorePath);
        
        System.out.println(bulkImporter.getStatus());

        if(txn != null)
        {
            txn.commit();
        }

        stopContext();
    }
    
    public static void main(String[] args)
    {
        try
        {
            new InPlaceBulkImportPerformance().execute(STORE, RELATIVE_PATH);
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
    }
}
