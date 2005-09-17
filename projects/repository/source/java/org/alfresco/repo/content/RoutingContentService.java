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
package org.alfresco.repo.content;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.ContentServicePolicies.OnContentUpdatePolicy;
import org.alfresco.repo.content.filestore.FileContentStore;
import org.alfresco.repo.content.transform.ContentTransformer;
import org.alfresco.repo.content.transform.ContentTransformerRegistry;
import org.alfresco.repo.policy.ClassPolicyDelegate;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.InvalidTypeException;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentStreamListener;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NoTransformerException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.EqualsHelper;
import org.alfresco.util.TempFileProvider;


/**
 * A content service that determines at runtime the store that the
 * content associated with a node should be routed to.
 * 
 * @author Derek Hulley
 */
public class RoutingContentService implements ContentService
{
    private TransactionService transactionService;
    private DictionaryService dictionaryService;
    private NodeService nodeService;
    /** a registry of all available content transformers */
    private ContentTransformerRegistry transformerRegistry;
    /** TEMPORARY until we have a map to choose from at runtime */
    private ContentStore store;
    /** the store for all temporarily created content */
    private ContentStore tempStore;
    
    /**
     * The policy component
     */
    private PolicyComponent policyComponent;
    
    /**
     * The onContentService policy delegate
     */
    ClassPolicyDelegate<ContentServicePolicies.OnContentUpdatePolicy> onContentUpdateDelegate;
    
    /**
     * Default constructor sets up a temporary store 
     */
    public RoutingContentService()
    {
        this.tempStore = new FileContentStore(TempFileProvider.getTempDir().getAbsolutePath());
    }

    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }
    
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setTransformerRegistry(ContentTransformerRegistry transformerRegistry)
    {
        this.transformerRegistry = transformerRegistry;
    }
    
    public void setStore(ContentStore store)
    {
        this.store = store;
    }
    
    public void setPolicyComponent(PolicyComponent policyComponent)
	{
		this.policyComponent = policyComponent;
	}
    
    /**
     * Service initialise 
     */
    public void init()
    {
    	// Bind on update properties behaviour
    	this.policyComponent.bindClassBehaviour(
    			QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"),
    			this,
    			new JavaBehaviour(this, "onUpdateProperties"));
    	
    	// Register on content update policy
    	this.onContentUpdateDelegate = this.policyComponent.registerClassPolicy(OnContentUpdatePolicy.class);
    }
    
    /**
     * Update properties policy behaviour
     * 
     * @param nodeRef	the node reference
     * @param before	the before values of the properties
     * @param after		the after values of the properties
     */
    public void onUpdateProperties(
            NodeRef nodeRef,
            Map<QName, Serializable> before,
            Map<QName, Serializable> after)
    {
    	String beforeContentUrl = (String)before.get(ContentModel.PROP_CONTENT_URL);
    	String afterContentUrl = (String)after.get(ContentModel.PROP_CONTENT_URL);
    	if (EqualsHelper.nullSafeEquals(beforeContentUrl, afterContentUrl) == false)
	    {
    		// Fire the content update policy
    		Set<QName> types = new HashSet<QName>(this.nodeService.getAspects(nodeRef));
    		types.add(this.nodeService.getType(nodeRef));
    		OnContentUpdatePolicy policy = this.onContentUpdateDelegate.get(types);
    		policy.onContentUpdate(nodeRef);
	    }
    }
    
    public ContentReader getReader(NodeRef nodeRef)
    {
        // ensure that the node exists and is of type content
        QName nodeType = nodeService.getType(nodeRef);
        if (!dictionaryService.isSubClass(nodeType, ContentModel.TYPE_CONTENT))
        {
            throw new InvalidTypeException("The node must be an instance of type content", nodeType);
        }
        
        // get the content URL
        Object contentUrlProperty = nodeService.getProperty(
                nodeRef,
                ContentModel.PROP_CONTENT_URL);
        String contentUrl = DefaultTypeConverter.INSTANCE.convert(String.class, contentUrlProperty);
        // check that the URL is available
        if (contentUrl == null)
        {
            // there is no URL - the interface specifies that this is not an error condition
            return null;
        }
        
        // TODO: Choose the store to read from at runtime
        ContentReader reader = store.getReader(contentUrl);
        
        // get node properties
        Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);
        // get the content mimetype
        String mimetype = (String) properties.get(ContentModel.PROP_MIME_TYPE);
        reader.setMimetype(mimetype);
        // get the content encoding
        String encoding = (String) properties.get(ContentModel.PROP_ENCODING);
        reader.setEncoding(encoding);
        
        // we don't listen for anything
        // result may be null - but interface contract says we may return null
        return reader;
    }

    public ContentWriter getWriter(NodeRef nodeRef)
    {
        // ensure that the node exists and is of type content
        QName nodeType = nodeService.getType(nodeRef);
        if (!dictionaryService.isSubClass(nodeType, ContentModel.TYPE_CONTENT))
        {
            throw new InvalidTypeException("The node must be an instance of type content", nodeType);
        }
        
        // check for an existing URL
        ContentReader existingContentReader = getReader(nodeRef);
        
        // TODO: Choose the store to write to at runtime
        
        // get the content using the (potentially) existing content - the new content
        // can be wherever the store decides.
        ContentWriter writer = store.getWriter(existingContentReader, null);

        // get node properties
        Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);
        // get the content mimetype
        String mimetype = (String) properties.get(ContentModel.PROP_MIME_TYPE);
        writer.setMimetype(mimetype);
        // get the content encoding
        String encoding = (String) properties.get(ContentModel.PROP_ENCODING);
        writer.setEncoding(encoding);
        
        // give back to the client
        return writer;
    }

   /**
    * Add a listener to the plain writer 
    * 
    * @see #getWriter(NodeRef)
    */
    public ContentWriter getUpdatingWriter(NodeRef nodeRef)
    {
        // ensure that the node exists and is of type content
        QName nodeType = nodeService.getType(nodeRef);
        if (!dictionaryService.isSubClass(nodeType, ContentModel.TYPE_CONTENT))
        {
            throw new InvalidTypeException("The node must be an instance of type content", nodeType);
        }
        
        // get the plain writer
        ContentWriter writer = getWriter(nodeRef);
        // need a listener to update the node when the stream closes
        WriteStreamListener listener = new WriteStreamListener(nodeService, nodeRef, writer);
        writer.addListener(listener);
        writer.setTransactionService(transactionService);
        // give back to the client
        return writer;
    }

    /**
     * @return Returns a writer to an anonymous location
     */
    public ContentWriter getTempWriter()
    {
        // there is no existing content and we don't specify the location of the new content
        return tempStore.getWriter(null, null);
    }

    /**
     * @see org.alfresco.repo.content.transform.ContentTransformerRegistry
     * @see org.alfresco.repo.content.transform.ContentTransformer
     */
    public void transform(ContentReader reader, ContentWriter writer)
            throws NoTransformerException, ContentIOException
    {
        // check that source and target mimetypes are available
        String sourceMimetype = reader.getMimetype();
        if (sourceMimetype == null)
        {
            throw new AlfrescoRuntimeException("The content reader mimetype must be set: " + reader);
        }
        String targetMimetype = writer.getMimetype();
        if (targetMimetype == null)
        {
            throw new AlfrescoRuntimeException("The content writer mimetype must be set: " + writer);
        }
        // look for a transformer
        ContentTransformer transformer = transformerRegistry.getTransformer(sourceMimetype, targetMimetype);
        if (transformer == null)
        {
            throw new NoTransformerException(sourceMimetype, targetMimetype);
        }
        // we have a transformer, so do it
        transformer.transform(reader, writer);
        // done
    }
    
    /**
     * @see org.alfresco.repo.content.transform.ContentTransformerRegistry
     * @see org.alfresco.repo.content.transform.ContentTransformer
     */
    public boolean isTransformable(ContentReader reader, ContentWriter writer)
    {
        // check that source and target mimetypes are available
        String sourceMimetype = reader.getMimetype();
        if (sourceMimetype == null)
        {
            throw new AlfrescoRuntimeException("The content reader mimetype must be set: " + reader);
        }
        String targetMimetype = writer.getMimetype();
        if (targetMimetype == null)
        {
            throw new AlfrescoRuntimeException("The content writer mimetype must be set: " + writer);
        }
        
        // look for a transformer
        ContentTransformer transformer = transformerRegistry.getTransformer(sourceMimetype, targetMimetype);
        return (transformer != null);
    }

    /**
     * Ensures that, upon closure of the output stream, the node is updated with
     * the latest URL of the content to which it refers.
     * <p>
     * The listener close operation does not need a transaction as the 
     * <code>ContentWriter</code> takes care of that.
     * 
     * @author Derek Hulley
     */
    private static class WriteStreamListener implements ContentStreamListener
    {
        private NodeService nodeService;
        private NodeRef nodeRef;
        private ContentWriter writer;
        
        public WriteStreamListener(
                NodeService nodeService,
                NodeRef nodeRef,
                ContentWriter writer)
        {
            this.nodeService = nodeService;
            this.nodeRef = nodeRef;
            this.writer = writer;
        }
        
        public void contentStreamClosed() throws ContentIOException
        {
            try
            {
                // change the content URL property of the node we are listening for
                String contentUrl = writer.getContentUrl();
                nodeService.setProperty(
                        nodeRef,
                        ContentModel.PROP_CONTENT_URL,
                        contentUrl);
                // get the size of the document
                ContentReader reader = writer.getReader();
                long length = reader.getLength();
                nodeService.setProperty(
                        nodeRef,
                        ContentModel.PROP_SIZE,
                        new Long(length));
            }
            catch (Throwable e)
            {
                throw new ContentIOException("Failed to set URL and size upon stream closure", e);
            }
        }
    }
}
