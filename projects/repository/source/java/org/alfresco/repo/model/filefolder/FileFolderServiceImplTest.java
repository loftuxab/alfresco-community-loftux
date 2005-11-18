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
package org.alfresco.repo.model.filefolder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.UserTransaction;

import junit.framework.TestCase;

import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.cmr.view.Location;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;

/**
 * @see org.alfresco.repo.model.filefolder.FileFolderServiceImpl
 * 
 * @author Derek Hulley
 */
public class FileFolderServiceImplTest extends TestCase
{
    private static final String IMPORT_VIEW = "filefolder/filefolder-test-import.xml";
    
    private static final String L0_FILE_A = "L0: File A";
    private static final String L0_FILE_B = "L0: File B";
    private static final String L0_FOLDER_A = "L0: Folder A";
    private static final String L0_FOLDER_B = "L0: Folder B";
    private static final String L0_FOLDER_C = "L0: Folder C";
    private static final String L1_FOLDER_A = "L1: Folder A";
    private static final String L1_FOLDER_B = "L1: Folder B";
    private static final String L1_FILE_A = "L1: File A";
    private static final String L1_FILE_B = "L1: File B";
    private static final String L1_FILE_C = "L1: File C (%_)";
    
    private static final ApplicationContext ctx = ApplicationContextHelper.getApplicationContext();

    private TransactionService transactionService;
    private NodeService nodeService;
    private FileFolderService fileFolderService;
    private UserTransaction txn;
    private NodeRef rootNodeRef;
    
    @Override
    public void setUp() throws Exception
    {
        ServiceRegistry serviceRegistry = (ServiceRegistry) ctx.getBean("ServiceRegistry");
        transactionService = serviceRegistry.getTransactionService();
        nodeService = serviceRegistry.getNodeService();
        fileFolderService = serviceRegistry.getFileFolderService();
        AuthenticationComponent authenticationComponent = (AuthenticationComponent) ctx.getBean("authenticationComponent");

        // start the transaction
        txn = transactionService.getUserTransaction();
        txn.begin();
        
        // authenticate
        authenticationComponent.setCurrentUser(authenticationComponent.getSystemUserName());
        
        // create a test store
        StoreRef storeRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, getName() + System.currentTimeMillis());
        rootNodeRef = nodeService.getRootNode(storeRef);
        
        // import the test data
        ImporterService importerService = serviceRegistry.getImporterService();
        Location importLocation = new Location(rootNodeRef);
        InputStream is = getClass().getClassLoader().getResourceAsStream(IMPORT_VIEW);
        if (is == null)
        {
            throw new NullPointerException("Test resource not found: " + IMPORT_VIEW);
        }
        Reader reader = new InputStreamReader(is);
        importerService.importView(reader, importLocation, null, null);
    }
    
    public void tearDown() throws Exception
    {
        txn.rollback();
    }

    /**
     * Checks that the names and numbers of files and folders in the provided list is correct
     * 
     * @param files the list of files
     * @param expectedFileCount the number of uniquely named files expected
     * @param expectedFolderCount the number of uniquely named folders expected
     * @param expectedNames the names of the files and folders expected
     */
    private void checkFileList(List<FileInfo> files, int expectedFileCount, int expectedFolderCount, String[] expectedNames)
    {
        int fileCount = 0;
        int folderCount = 0;
        List<String> check = new ArrayList<String>(8);
        for (String filename : expectedNames)
        {
            check.add(filename);
        }
        for (FileInfo file : files)
        {
            if (file.isFolder())
            {
                folderCount++;
            }
            else
            {
                fileCount++;
            }
            check.remove(file.getName());
        }
        assertTrue("Name list was not exact - remaining: " + check, check.size() == 0);
        assertEquals("Incorrect number of files", expectedFileCount, fileCount);
        assertEquals("Incorrect number of folders", expectedFolderCount, folderCount);
    }
    
    public void testShallowFilesAndFoldersList() throws Exception
    {
        List<FileInfo> files = fileFolderService.list(rootNodeRef);
        // check
        String[] expectedNames = new String[] {L0_FILE_A, L0_FILE_B, L0_FOLDER_A, L0_FOLDER_B, L0_FOLDER_C};
        checkFileList(files, 2, 3, expectedNames);
    }
    
    public void testShallowFilesOnlyList() throws Exception
    {
        List<FileInfo> files = fileFolderService.listFiles(rootNodeRef);
        // check
        String[] expectedNames = new String[] {L0_FILE_A, L0_FILE_B};
        checkFileList(files, 2, 0, expectedNames);
    }
    
    public void testShallowFoldersOnlyList() throws Exception
    {
        List<FileInfo> files = fileFolderService.listFolders(rootNodeRef);
        // check
        String[] expectedNames = new String[] {L0_FOLDER_A, L0_FOLDER_B, L0_FOLDER_C};
        checkFileList(files, 0, 3, expectedNames);
    }
    
    public void testShallowFileSearch() throws Exception
    {
        List<FileInfo> files = fileFolderService.search(
                rootNodeRef,
                L0_FILE_B,
                true,
                false,
                false);
        // check
        String[] expectedNames = new String[] {L0_FILE_B};
        checkFileList(files, 1, 0, expectedNames);
    }
    
    public void testDeepFilesAndFoldersSearch() throws Exception
    {
        List<FileInfo> files = fileFolderService.search(
                rootNodeRef,
                "?1:*",
                true,
                true,
                true);
        // check
        String[] expectedNames = new String[] {L1_FOLDER_A, L1_FOLDER_B, L1_FILE_A, L1_FILE_B, L1_FILE_C};
        checkFileList(files, 3, 2, expectedNames);
    }
    
    public void testDeepFilesOnlySearch() throws Exception
    {
        List<FileInfo> files = fileFolderService.search(
                rootNodeRef,
                "?1:*",
                true,
                false,
                true);
        // check
        String[] expectedNames = new String[] {L1_FILE_A, L1_FILE_B, L1_FILE_C};
        checkFileList(files, 3, 0, expectedNames);
    }
}
