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
 */
package org.alfresco.repo.node;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.dictionary.impl.DictionaryComponent;
import org.alfresco.repo.dictionary.impl.DictionaryDAO;
import org.alfresco.repo.dictionary.impl.M2Model;
import org.alfresco.repo.transaction.AlfrescoTransactionManager;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.DynamicNamespacePrefixResolver;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.transaction.SpringAwareUserTransaction;
import org.apache.lucene.index.IndexWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ConcurrentNodeServiceTest extends TestCase
{

    public static final String NAMESPACE = "http://www.alfresco.org/test/BaseNodeServiceTest";

    public static final String TEST_PREFIX = "test";

    public static final QName TYPE_QNAME_TEST_CONTENT = QName.createQName(NAMESPACE, "content");

    public static final QName ASPECT_QNAME_TEST_TITLED = QName.createQName(NAMESPACE, "titled");

    public static final QName PROP_QNAME_TEST_TITLE = QName.createQName(NAMESPACE, "title");

    public static final QName PROP_QNAME_TEST_MIMETYPE = QName.createQName(NAMESPACE, "mimetype");

    public static final QName ASSOC_TYPE_QNAME_TEST_CONTAINS = ContentModel.ASSOC_CONTAINS;

    static ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");

    private DictionaryComponent dictionaryService;

    private ContentService contentService;

    private NodeService nodeService;

    private AlfrescoTransactionManager transactionManager;

    private NodeRef rootNodeRef;

    public ConcurrentNodeServiceTest()
    {
        super();
    }

    public ConcurrentNodeServiceTest(String arg0)
    {
        super(arg0);
    }

    protected void setUp() throws Exception
    {
        DictionaryDAO dictionaryDao = (DictionaryDAO) ctx.getBean("dictionaryDAO");
        // load the system model
        ClassLoader cl = BaseNodeServiceTest.class.getClassLoader();
        InputStream modelStream = cl.getResourceAsStream("org/alfresco/model/content_model.xml");
        assertNotNull(modelStream);
        M2Model model = M2Model.createModel(modelStream);
        dictionaryDao.putModel(model);
        // load the test model
        modelStream = cl.getResourceAsStream("org/alfresco/repo/node/BaseNodeServiceTest_model.xml");
        assertNotNull(modelStream);
        model = M2Model.createModel(modelStream);
        dictionaryDao.putModel(model);

        DictionaryComponent dictionary = new DictionaryComponent();
        dictionary.setDictionaryDAO(dictionaryDao);
        dictionaryService = dictionary;

        nodeService = (NodeService) ctx.getBean("dbNodeService");
        contentService = (ContentService) ctx.getBean("contentService");
        transactionManager = (AlfrescoTransactionManager) ctx.getBean("transactionManager");

        // create a first store directly
        SpringAwareUserTransaction tx = new SpringAwareUserTransaction(transactionManager);
        tx.begin();
        StoreRef storeRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        rootNodeRef = nodeService.getRootNode(storeRef);
        tx.commit();
    }

    protected Map<QName, ChildAssociationRef> buildNodeGraph() throws Exception
    {
        String ns = BaseNodeServiceTest.NAMESPACE;
        QName qname = null;
        ChildAssociationRef assoc = null;
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
        Map<QName, ChildAssociationRef> ret = new HashMap<QName, ChildAssociationRef>(13);

        // LEVEL 0

        // LEVEL 1
        qname = QName.createQName(ns, "root_p_n1");
        assoc = nodeService.createNode(rootNodeRef, ASSOC_TYPE_QNAME_TEST_CONTAINS, qname, ContentModel.TYPE_CONTAINER);
        ret.put(qname, assoc);
        NodeRef n1 = assoc.getChildRef();

        qname = QName.createQName(ns, "root_p_n2");
        assoc = nodeService.createNode(rootNodeRef, ASSOC_TYPE_QNAME_TEST_CONTAINS, qname, ContentModel.TYPE_CONTAINER);
        ret.put(qname, assoc);
        NodeRef n2 = assoc.getChildRef();

        // LEVEL 2

        properties.clear();
        properties.put(QName.createQName(ns, "animal"), "monkey");
        properties.put(QName.createQName(ns, "reference"), n2.toString());

        qname = QName.createQName(ns, "n1_p_n3");
        assoc = nodeService.createNode(n1, ASSOC_TYPE_QNAME_TEST_CONTAINS, qname, ContentModel.TYPE_CONTAINER, properties);
        ret.put(qname, assoc);
        NodeRef n3 = assoc.getChildRef();

        qname = QName.createQName(ns, "n2_p_n4");
        assoc = nodeService.createNode(n2, ASSOC_TYPE_QNAME_TEST_CONTAINS, qname, ContentModel.TYPE_CONTAINER);
        ret.put(qname, assoc);
        NodeRef n4 = assoc.getChildRef();

        qname = QName.createQName(ns, "n1_n4");
        assoc = nodeService.addChild(n1, n4, ASSOC_TYPE_QNAME_TEST_CONTAINS, qname);
        ret.put(qname, assoc);

        qname = QName.createQName(ns, "n2_p_n5");
        assoc = nodeService.createNode(n2, ASSOC_TYPE_QNAME_TEST_CONTAINS, qname, ContentModel.TYPE_CONTAINER);
        ret.put(qname, assoc);
        NodeRef n5 = assoc.getChildRef();

        // LEVEL 3
        qname = QName.createQName(ns, "n3_p_n6");
        assoc = nodeService.createNode(n3, ASSOC_TYPE_QNAME_TEST_CONTAINS, qname, ContentModel.TYPE_CONTAINER);
        ret.put(qname, assoc);
        NodeRef n6 = assoc.getChildRef();
        nodeService.addAspect(n6, ContentModel.ASPECT_ROOT, Collections.<QName, Serializable> emptyMap());

        qname = QName.createQName(ns, "n4_n6");
        assoc = nodeService.addChild(n4, n6, ASSOC_TYPE_QNAME_TEST_CONTAINS, qname);
        ret.put(qname, assoc);

        qname = QName.createQName(ns, "n5_p_n7");
        assoc = nodeService.createNode(n5, ASSOC_TYPE_QNAME_TEST_CONTAINS, qname, ContentModel.TYPE_CONTAINER);
        ret.put(qname, assoc);
        NodeRef n7 = assoc.getChildRef();

        // LEVEL 4
        properties.clear();
        properties.put(PROP_QNAME_TEST_MIMETYPE, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        properties.put(PROP_QNAME_TEST_TITLE, "node8");
        qname = QName.createQName(ns, "n6_p_n8");
        assoc = nodeService.createNode(n6, ASSOC_TYPE_QNAME_TEST_CONTAINS, qname, TYPE_QNAME_TEST_CONTENT, properties);
        ret.put(qname, assoc);
        NodeRef n8 = assoc.getChildRef();
        nodeService.getPaths(n8, false);

        qname = QName.createQName(ns, "n7_n8");
        assoc = nodeService.addChild(n7, n8, ASSOC_TYPE_QNAME_TEST_CONTAINS, qname);
        ret.put(qname, assoc);

        // flush and clear
        // getSession().flush();
        // getSession().clear();

        // done
        return ret;
    }

    protected Map<QName, ChildAssociationRef> commitNodeGraph() throws Exception
    {
        SpringAwareUserTransaction tx = new SpringAwareUserTransaction(transactionManager);
        tx.begin();
        Map<QName, ChildAssociationRef> asnwer = buildNodeGraph();
        tx.commit();
        return asnwer;
    }

    public void testConcurrent() throws Exception
    {
        IndexWriter.COMMIT_LOCK_TIMEOUT = 100000;
        int count = 5;
        int repeats = 5;

        Map<QName, ChildAssociationRef> assocRefs = commitNodeGraph();
        Thread runner = null;

        for (int i = 0; i < count; i++)
        {
            runner = new Nester("Concurrent-" + i, runner, repeats);
        }
        if (runner != null)
        {
            runner.start();

            try
            {
                runner.join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        assertEquals(2 * ((count * repeats) + 1), nodeService.selectNodes(rootNodeRef, "/*", null, getNamespacePrefixReolsver(""), false).size());
        SearchService searcher = (SearchService) ctx.getBean("searcherComponent");
        ResultSet results = searcher.query(rootNodeRef.getStoreRef(), "lucene", "PATH:\"/*\"");
        // n6 has root aspect - there are three things at the root level in the index
        assertEquals(3 * ((count * repeats) + 1), results.length());
        results.close();
    }

    class Nester extends Thread
    {
        Thread waiter;

        int repeats;

        Nester(String name, Thread waiter, int repeats)
        {
            super(name);
            this.waiter = waiter;
            this.repeats = repeats;
        }

        public void run()
        {
            if (waiter != null)
            {
                waiter.start();
            }
            try
            {
                //System.out.println("Start " + this.getName());
                for (int i = 0; i < repeats; i++)
                {
                    Map<QName, ChildAssociationRef> assocRefs = commitNodeGraph();
                    //System.out.println(" " + this.getName() + " " + i);
                }
                //System.out.println("End " + this.getName());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            if (waiter != null)
            {
                try
                {
                    waiter.join();
                }
                catch (InterruptedException e)
                {
                }
            }
        }

    }

    private NamespacePrefixResolver getNamespacePrefixReolsver(String defaultURI)
    {
        DynamicNamespacePrefixResolver nspr = new DynamicNamespacePrefixResolver(null);
        nspr.addDynamicNamespace(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI);
        nspr.addDynamicNamespace("namespace", "namespace");
        nspr.addDynamicNamespace(NamespaceService.DEFAULT_PREFIX, defaultURI);
        return nspr;
    }

}
