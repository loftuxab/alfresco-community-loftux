/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *
 * Created on 27-Jun-2005
 */
package org.alfresco.repo.importer;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.List;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import junit.framework.TestCase;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.transform.AbstractContentTransformerTest;
import org.alfresco.repo.security.authentication.AuthenticationService;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;

public class FileImporterTest extends TestCase
{
    static ApplicationContext ctx = ApplicationContextHelper.getApplicationContext();

    private NodeService nodeService;
    private DictionaryService dictionaryService;
    private ContentService contentService;
    private AuthenticationService authenticationService;
    private MimetypeService mimetypeService;
    private NamespaceService namespaceService;
    private ServiceRegistry serviceRegistry;
    private NodeRef rootNodeRef;

    public FileImporterTest()
    {
        super();
    }

    public FileImporterTest(String arg0)
    {
        super(arg0);
    }

    public void setUp()
    {
        serviceRegistry = (ServiceRegistry) ctx.getBean("serviceRegistry");
        nodeService = serviceRegistry.getNodeService();
        dictionaryService = serviceRegistry.getDictionaryService();
        contentService = serviceRegistry.getContentService();
        authenticationService = (AuthenticationService) ctx.getBean("authenticationService");
        mimetypeService = serviceRegistry.getMimetypeService();
        namespaceService = serviceRegistry.getNamespaceService();

        StoreRef storeRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        rootNodeRef = nodeService.getRootNode(storeRef);
    }

    private FileImporter createFileImporter()
    {
        FileImporterImpl fileImporter = new FileImporterImpl();
        fileImporter.setAuthenticationService(authenticationService);
        fileImporter.setContentService(contentService);
        fileImporter.setMimetypeService(mimetypeService);
        fileImporter.setNodeService(nodeService);
        fileImporter.setDictionaryService(dictionaryService);
        return fileImporter;
    }

    public void testCreateFile() throws Exception
    {
        FileImporter fileImporter = createFileImporter();
        File file = AbstractContentTransformerTest.loadQuickTestFile("xml");
        fileImporter.loadFile(rootNodeRef, file);
    }

    public void testLoadRootNonRecursive1()
    {
        FileImporter fileImporter = createFileImporter();
        URL url = this.getClass().getClassLoader().getResource("");
        fileImporter.loadFile(rootNodeRef, new File(url.getFile()));
    }

    public void testLoadRootNonRecursive2()
    {
        FileImporter fileImporter = createFileImporter();
        URL url = this.getClass().getClassLoader().getResource("");
        fileImporter.loadFile(rootNodeRef, new File(url.getFile()), null, false);
    }

    public void testLoadXMLFiles()
    {
        FileImporter fileImporter = createFileImporter();
        URL url = this.getClass().getClassLoader().getResource("");
        FileFilter filter = new XMLFileFilter();
        fileImporter.loadFile(rootNodeRef, new File(url.getFile()), filter, true);
    }

    public void testLoadSourceTestResources()
    {
        FileImporter fileImporter = createFileImporter();
        URL url = this.getClass().getClassLoader().getResource("quick");
        FileFilter filter = new QuickFileFilter();
        fileImporter.loadFile(rootNodeRef, new File(url.getFile()), filter, true);
    }

    private static class XMLFileFilter implements FileFilter
    {
        public boolean accept(File file)
        {
            return file.getName().endsWith(".xml");
        }
    }
    
    private static class QuickFileFilter implements FileFilter
    {
        public boolean accept(File file)
        {
            return file.getName().startsWith("quick");
        }
    }

    /**
     * @param args
     *            <ol>
     *            <li>StoreRef
     *            <li>Store Path
     *            <li>Directory
     *            </ol>
     * @throws SystemException 
     * @throws NotSupportedException 
     * @throws HeuristicRollbackException 
     * @throws HeuristicMixedException 
     * @throws RollbackException 
     * @throws IllegalStateException 
     * @throws SecurityException 
     */
    public static final void main(String[] args) throws Exception
    {
        FileImporterTest test = new FileImporterTest();
        test.setUp();
        
        UserTransaction tx = test.serviceRegistry.getUserTransaction();
        tx.begin();
       
        StoreRef spacesStore = new StoreRef(args[0]);
        if (!test.nodeService.exists(spacesStore))
        {
            test.nodeService.createStore(spacesStore.getProtocol(), spacesStore.getIdentifier());
        }
        NodeRef storeRoot = test.nodeService.getRootNode(spacesStore);
        List<NodeRef> location = test.nodeService.selectNodes(storeRoot, args[1], null, test.namespaceService, false);
        if (location.size() > 0)
        {    
            long start = System.nanoTime();
            test.createFileImporter().loadFile(location.get(0), new File(args[2]), true);
            long end = System.nanoTime();
            System.out.println("Created in "+((end-start)/1000000.0));
            start = System.nanoTime();
            tx.commit();
            end = System.nanoTime();
            System.out.println("Commit in "+((end-start)/1000000.0));
        }
        else
        {
            tx.rollback();
            throw new AlfrescoRuntimeException("Can not find node for import");
        }
    }
}
