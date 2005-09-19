package org.alfresco.example.webservice;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class WebServiceBootstrapSystemTest extends TestCase
{   
    /**
     * NOTE:  You need to set the location of the indexes and content store to be a non-realtive
     *        location in the repository.properties file otherwise running this test here will not
     *        populate the correct index and content store and the test won't work when running against
     *        the repository
     */
    
    public static final String FOLDER_NAME = "test folder";
    public static final String CONTENT_NAME = "test content";
    
    public static final String PROP_STORE_REF = "storeRef";
    public static final String PROP_ROOT_NODE_REF = "rootNodeRef";
    public static final String PROP_FOLDER_NODE_REF = "folderNodeRef";
    
    private static final String TEMP_BOOTSTRAP_PROPERTIES = "./WebServiceTestBootstrap.properties";    
    
    private static final String TEST_CONTENT = "This is some test content.  This is some test content.";
    
    private NodeService nodeService;
    private ContentService contentService;
    
    /**
     * Runs the bootstrap and populates the property file with the infomration required for the tests
     */
    public void testBootstrap()
    {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:alfresco/application-context.xml");
        
        // Get the services
        this.nodeService = (NodeService)applicationContext.getBean("NodeService");
        this.contentService = (ContentService)applicationContext.getBean("ContentService");
        
        // Create the store
        StoreRef storeRef = this.nodeService.createStore(StoreRef.PROTOCOL_WORKSPACE, "Test_" + System.currentTimeMillis());
        NodeRef rootNodeRef = rootNodeRef = this.nodeService.getRootNode(storeRef);
        
        Map<QName, Serializable> folderProps = new HashMap<QName, Serializable>(1);
        folderProps.put(ContentModel.PROP_NAME, FOLDER_NAME);
        
        // Create a folder
        NodeRef folderNodeRef = this.nodeService.createNode(
                                        rootNodeRef, 
                                        ContentModel.ASSOC_CHILDREN,
                                        ContentModel.ASSOC_CHILDREN,
                                        ContentModel.TYPE_FOLDER,
                                        folderProps).getChildRef();
        
        Map<QName, Serializable> contentProps = new HashMap<QName, Serializable>(3);
        contentProps.put(ContentModel.PROP_MIME_TYPE, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        contentProps.put(ContentModel.PROP_ENCODING, "UTF-8");
        contentProps.put(ContentModel.PROP_NAME, CONTENT_NAME);
        
        // Create some test content        
        NodeRef testContent = this.nodeService.createNode(
                rootNodeRef,
                ContentModel.ASSOC_CHILDREN,
                ContentModel.ASSOC_CHILDREN,
                ContentModel.TYPE_CONTENT,
                contentProps).getChildRef();
        this.contentService.getUpdatingWriter(testContent).putContent(TEST_CONTENT);
        
        
        Properties properties = new Properties();
        properties.put(PROP_STORE_REF, storeRef.toString());
        properties.put(PROP_ROOT_NODE_REF, rootNodeRef.toString());
        properties.put(PROP_FOLDER_NODE_REF, folderNodeRef.toString());
        
        try
        {
            OutputStream outputStream = new FileOutputStream(TEMP_BOOTSTRAP_PROPERTIES);
            properties.store(outputStream, "Web service node store details");
            outputStream.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Unable to store bootstrap details.");
        }
    }
    
    public static Properties getBootstrapProperties()
    {
        Properties properties = new Properties();
        try
        {
            InputStream inputStream = new FileInputStream(TEMP_BOOTSTRAP_PROPERTIES);
            properties.load(inputStream);            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Unable to load test bootstrap details.  Try running WebServiceBootstrapSystem test, then re-start container and try again.");
        }
        return properties;
    }
}
