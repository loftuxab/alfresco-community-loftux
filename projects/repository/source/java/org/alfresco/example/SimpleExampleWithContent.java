package org.alfresco.example;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.GUID;
import org.alfresco.util.debug.NodeStoreInspector;
import org.springframework.context.ApplicationContext;

/**
 * A quick example of how to
 * <ul>
 *   <li>get hold of the repository service</li>
 *   <li>initialise a model</li>
 *   <li>create nodes</li>
 *   <li>load in some content</li>
 * </ul>
 * <p>
 * <i>
 * All the normal checks for missing resources and so forth have been left out in the interests
 * of clarity of demonstration.
 * </i>
 * <p>
 * To change the model being used, make changes to the <b>dictionaryDAO</b> bean in the
 * application contenxt XML file.  For now, this example is written against the
 * generic <code>alfresco/model/contentModel.xml</code>.
 * <p>
 * The content store location can also be set in the application context.
 * 
 * 
 * @author Derek Hulley
 */
public class SimpleExampleWithContent
{
    private static final String NAMESPACE = "http://www.alfresco.org/test/SimpleExampleWithContent";
    
    public static void main(String[] args)
    {
        // initialise app content 
        ApplicationContext ctx = ApplicationContextHelper.getApplicationContext();
        // get registry of services
        ServiceRegistry serviceRegistry = (ServiceRegistry) ctx.getBean(ServiceRegistry.SERVICE_REGISTRY);
        
        // begin a UserTransaction
        // All the services are set to create or propogate the transaction.
        // This transaction will be recognised and propogated
        // The usual try-catch-finally code has been ommitted
        TransactionService transactionService = serviceRegistry.getTransactionService(); 
        UserTransaction txn = transactionService.getUserTransaction();
        try
        {
            txn.begin();
            doExample(serviceRegistry);
            txn.commit();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            try { txn.rollback(); } catch (Exception ee) { ee.printStackTrace(); }
        }
        System.exit(0);
    }

    private static void doExample(ServiceRegistry serviceRegistry) throws Exception
    {
        // get individual, required services
        NodeService nodeService = serviceRegistry.getNodeService();
        ContentService contentService = serviceRegistry.getContentService();
        
        // create a store, if one doesn't exist
        StoreRef storeRef = new StoreRef(
                StoreRef.PROTOCOL_WORKSPACE,
                "SimpleExampleWithContent-" + GUID.generate());
        if (!nodeService.exists(storeRef))
        {
            nodeService.createStore(storeRef.getProtocol(), storeRef.getIdentifier());
        }
        
        // get the root node from which to hang the next level of nodes
        NodeRef rootNodeRef = nodeService.getRootNode(storeRef);
        
        Map<QName, Serializable> nodeProperties = new HashMap<QName, Serializable>(7);
        
        // add a simple folder to the root node
        nodeProperties.clear();
        nodeProperties.put(ContentModel.PROP_NAME, "My First Folder");
        ChildAssociationRef assocRef = nodeService.createNode(
                rootNodeRef,
                ContentModel.ASSOC_CHILDREN,
                QName.createQName(NAMESPACE, QName.createValidLocalName("My First Folder")),
                ContentModel.TYPE_FOLDER,
                nodeProperties);
        NodeRef folderRef = assocRef.getChildRef();
        
        // create a file
        nodeProperties.clear();
        nodeProperties.put(ContentModel.PROP_NAME, "My First File");
        assocRef = nodeService.createNode(
                folderRef,
                ContentModel.ASSOC_CONTAINS,
                QName.createQName(NAMESPACE, QName.createValidLocalName("My First File")),
                ContentModel.TYPE_CONTENT,
                nodeProperties);
        NodeRef fileRef = assocRef.getChildRef();
        
        // the file is of type content, with mandatory property 'mimetype'
        // this need only be set before the end of the transaction to satisfy the node,
        // but the content services require the mimetype set before content can be
        // written or read
        nodeService.setProperty(fileRef, ContentModel.PROP_MIME_TYPE, "text/plain");
        // store string content as UTF-8
        nodeService.setProperty(fileRef, ContentModel.PROP_ENCODING, "UTF-8");
        
        // write some content - this API allows streaming and direct loading,
        // but for now we'll just upload a string
        // The writer, being updating, will take care of updating the node once the stream
        // closes.
        // just to demonstrate the node structure, dump it to the file
        ContentWriter writer = contentService.getUpdatingWriter(fileRef);
        String dump = NodeStoreInspector.dumpNodeStore(nodeService, storeRef);
        writer.putContent(dump);
        
        // get the URL.  It is also available directly from the writer
        String contentUrl = (String) nodeService.getProperty(fileRef, ContentModel.PROP_CONTENT_URL);
        System.out.println("Node store dumped to: " + contentUrl);
        
        // get a reader
        ContentReader reader = contentService.getReader(fileRef);
        if (reader.exists())
        {
            System.out.println("Node Store: \n" + reader.getContentString());
        }
        
        // and much, much more ...
    }
}
