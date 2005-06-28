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
import org.alfresco.repo.security.authentication.AuthenticationService;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FileImporterTest extends TestCase
{
    static ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");

    private NodeService nodeService;

    private ContentService contentService;

    private AuthenticationService authenticationService;

    private MimetypeService mimetypeServide;

    private NodeRef rootNodeRef;

    private NamespaceService namespaceService;
    
    private ServiceRegistry serviceRegistry;

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
        nodeService = (NodeService) ctx.getBean("dbNodeService");
        contentService = (ContentService) ctx.getBean("contentService");
        authenticationService = (AuthenticationService) ctx.getBean("authenticationService");
        mimetypeServide = (MimetypeService) ctx.getBean("mimetypeMap");
        namespaceService = (NamespaceService) ctx.getBean("namespaceService");
        serviceRegistry = (ServiceRegistry) ctx.getBean("serviceRegistry");

        StoreRef storeRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        rootNodeRef = nodeService.getRootNode(storeRef);
    }

    private FileImporter createFileImporter()
    {
        FileImporterImpl fileImporter = new FileImporterImpl();
        fileImporter.setAuthenticationService(authenticationService);
        fileImporter.setContentService(contentService);
        fileImporter.setMimetypeService(mimetypeServide);
        fileImporter.setNodeService(nodeService);
        return fileImporter;
    }

    public void testCreateFile()
    {
        FileImporter fileImporter = createFileImporter();
        URL url = this.getClass().getClassLoader().getResource("applicationContext.xml");
        fileImporter.loadFile(rootNodeRef, new File(url.getFile()));
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
        fileImporter.loadFile(rootNodeRef, new File(url.getFile()), true);
    }

    private static class XMLFileFilter implements FileFilter
    {
        public boolean accept(File file)
        {
            return file.getName().endsWith(".xml");
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
        StoreRef spacesStore = new StoreRef(args[0]);
        if (!test.nodeService.exists(spacesStore))
        {
            test.nodeService.createStore(spacesStore.getProtocol(), spacesStore.getIdentifier());
        }
        NodeRef storeRoot = test.nodeService.getRootNode(spacesStore);
        List<NodeRef> location = test.nodeService.selectNodes(storeRoot, args[1], null, test.namespaceService, false);
        if (location.size() > 0)
        {
            UserTransaction tx = test.serviceRegistry.getUserTransaction();
            tx.begin();
            test.createFileImporter().loadFile(location.get(0), new File(args[2]), true);
            tx.commit();
        }
        else
        {
            throw new AlfrescoRuntimeException("Can not find node for import");
        }
    }
}
