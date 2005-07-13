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
package org.alfresco.repo.content;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.filestore.FileContentStore;
import org.alfresco.repo.content.transform.ContentTransformer;
import org.alfresco.repo.content.transform.ContentTransformerRegistry;
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
import org.alfresco.service.cmr.repository.datatype.ValueConverter;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.TempFileProvider;

/**
 * A content service that determines at runtime the store that the
 * content associated with a node should be routed to.
 * 
 * @author Derek Hulley
 */
public class RoutingContentService implements ContentService
{
    private DictionaryService dictionaryService;
    private NodeService nodeService;
    /** a registry of all available content transformers */
    private ContentTransformerRegistry transformerRegistry;
    /** TEMPORARY until we have a map to choose from at runtime */
    private ContentStore store;
    /** the store for all temporarily created content */
    private ContentStore tempStore;
    
    /**
     * @param dictionaryService used to check the validity of content node references
     * @param nodeService the node service that will be used to update nodes after
     *      content writes
     * @param store temporary measure to set a working store
     */
    public RoutingContentService(
            DictionaryService dictionaryService,
            NodeService nodeService,
            ContentTransformerRegistry transformerRegistry,
            ContentStore store)
    {
        // TODO: The store root should be set on the store directly and via a config file
        this.dictionaryService = dictionaryService;
        this.nodeService = nodeService;
        this.transformerRegistry = transformerRegistry;
        this.store = store;
        this.tempStore = new FileContentStore(TempFileProvider.getTempDir().getAbsolutePath());
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
        String contentUrl = ValueConverter.convert(String.class, contentUrlProperty);
        // check that the URL is available
        if (contentUrl == null)
        {
            // there is no URL - the interface specifies that this is not an error condition
            return null;
        }
        
        // TODO: Choose the store to read from at runtime
        ContentReader reader = store.getReader(contentUrl);
        
        // get the content mimetype
        String mimetype = (String) nodeService.getProperty(
                nodeRef,
                ContentModel.PROP_MIME_TYPE);
        reader.setMimetype(mimetype);
        // get the content encoding
        String encoding = (String) nodeService.getProperty(
                nodeRef,
                ContentModel.PROP_ENCODING);
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
        
        // TODO: Choose the store to write to at runtime
        ContentWriter writer = store.getWriter();

        // get the content mimetype
        String mimetype = (String) nodeService.getProperty(
                nodeRef,
                ContentModel.PROP_MIME_TYPE);
        writer.setMimetype(mimetype);
        // get the content encoding
        String encoding = (String) nodeService.getProperty(
                nodeRef,
                ContentModel.PROP_ENCODING);
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
        // get URL that is going to be written to
        String contentUrl = writer.getContentUrl();
        // need a listener to update the node when the stream closes
        WriteStreamListener listener = new WriteStreamListener(nodeRef, writer);
        listener.setNodeService(nodeService);
        writer.addListener(listener);
        // give back to the client
        return writer;
    }

    /**
     * @return Returns a writer to an anonymous location
     */
    public ContentWriter getTempWriter()
    {
        return tempStore.getWriter();
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
     * 
     * @author Derek Hulley
     */
    private static class WriteStreamListener implements ContentStreamListener
    {
        private NodeService nodeService;
        private NodeRef nodeRef;
        private ContentWriter writer;
        
        public WriteStreamListener(NodeRef nodeRef, ContentWriter writer)
        {
            this.nodeRef = nodeRef;
            this.writer = writer;
        }
        
        public void setNodeService(NodeService nodeService)
        {
            // TODO: Issue - The listener should get the node service from a registry
            this.nodeService = nodeService;
        }

        public void contentStreamClosed() throws ContentIOException
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
    }
}
