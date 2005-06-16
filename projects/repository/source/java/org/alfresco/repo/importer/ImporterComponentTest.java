package org.alfresco.repo.importer;

import java.io.InputStream;
import java.io.Serializable;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.BaseSpringTest;


public class ImporterComponentTest extends BaseSpringTest
{
    private ImporterService importerService;
    private NodeService nodeService;
    private StoreRef storeRef;

    
    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        nodeService = (NodeService)applicationContext.getBean("AlfNodeService");
        importerService = (ImporterService)applicationContext.getBean("importerComponent");
        
        // Create the store
        this.storeRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
    }

    
    public void testImport()
    {
        InputStream test = getClass().getClassLoader().getResourceAsStream("org/alfresco/repo/importer/importercomponent_test.xml");
        TestProgress testProgress = new TestProgress();

        importerService.importNodes(test, storeRef, testProgress);
//        System.out.println(NodeStoreInspector.dumpNodeStore(nodeService, storeRef));
    }
    
    
    
    private static class TestProgress implements ImporterProgress
    {
        public void nodeCreated(NodeRef nodeRef, NodeRef parentRef, QName assocName, QName childName)
        {
//            System.out.println("TestProgress: created node " + nodeRef + " within parent " + parentRef + " named " + childName +
//                    " (association " + assocName + ")");
        }

        public void propertySet(NodeRef nodeRef, QName property, Serializable value)
        {
//            System.out.println("TestProgress: set property " + property + " on node " + nodeRef + " to value " + value);
        }

        public void aspectAdded(NodeRef nodeRef, QName aspect)
        {
//            System.out.println("TestProgress: added aspect " + aspect + " to node ");
        }
    }
    
}

