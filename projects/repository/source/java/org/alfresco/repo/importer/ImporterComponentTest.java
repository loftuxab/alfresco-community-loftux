package org.alfresco.repo.importer;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.BaseSpringTest;
import org.alfresco.util.debug.NodeStoreInspector;


public class ImporterComponentTest extends BaseSpringTest
{
    private ImporterService importerService;
    private ImporterBootstrap importerBootstrap;
    private NodeService nodeService;
    private StoreRef storeRef;

    
    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        nodeService = (NodeService)applicationContext.getBean("AlfNodeService");
        importerService = (ImporterService)applicationContext.getBean("importerComponent");
        importerBootstrap = (ImporterBootstrap)applicationContext.getBean("importerBootstrap");
        
        // Create the store
        this.storeRef = nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
    }

    
    public void xtestImport()
    {
        InputStream test = getClass().getClassLoader().getResourceAsStream("org/alfresco/repo/importer/importercomponent_test.xml");
        TestProgress testProgress = new TestProgress();
        Location location = new Location(storeRef);
        Properties configuration = new Properties();
        configuration.put("username", "fredb");
        
        importerService.importNodes(test, location, configuration, testProgress);
        //System.out.println(NodeStoreInspector.dumpNodeStore(nodeService, storeRef));
    }
    
    
    public void testBootstrap()
    {
        Properties configuration = new Properties();
        configuration.put("companySpaceName", "test company name");
        configuration.put("companySpaceDescription", "test company description");
        configuration.put("glossaryName", "glossary name");
        configuration.put("templatesName", "template name");

        importerBootstrap.setStoreId(this.storeRef.getIdentifier());
        importerBootstrap.setConfiguration(configuration);
        importerBootstrap.bootstrap();
        System.out.println(NodeStoreInspector.dumpNodeStore(nodeService, storeRef));
    }
    
    
    
    private static class TestProgress implements Progress
    {
        public void nodeCreated(NodeRef nodeRef, NodeRef parentRef, QName assocName, QName childName)
        {
//            System.out.println("TestProgress: created node " + nodeRef + " within parent " + parentRef + " named " + childName +
//                    " (association " + assocName + ")");
        }

        public void contentCreated(NodeRef nodeRef, String sourceUrl)
        {
//            System.out.println("TestProgress: created content " + nodeRef + " from url " + sourceUrl);
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

