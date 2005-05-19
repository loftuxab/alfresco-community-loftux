package org.alfresco.repo.content;

import org.alfresco.repo.content.filestore.FileContentStore;
import org.alfresco.repo.dictionary.bootstrap.DictionaryBootstrap;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.util.AspectMissingException;
import org.alfresco.util.debug.CodeMonkey;

/**
 * A content service that determines at runtime the store that the
 * content associated with a node should be routed to.
 * 
 * @author Derek Hulley
 */
public class RoutingContentService implements ContentService
{
    private NodeService nodeService;
    /** TEMPORARY until we have a map to choose from at runtime */
    private ContentStore store;
    
    /**
     * 
     * @param nodeService the node service that will be used to update nodes after
     *      content writes
     * @param storeRoot temporary measure to set a working store root
     */
    public RoutingContentService(NodeService nodeService, String storeRoot)
    {
        CodeMonkey.todo("The store root should be set on the store directly and via a config file");  // TODO
        this.nodeService = nodeService;
        this.store = new FileContentStore(storeRoot);
    }

    public ContentReader getReader(NodeRef nodeRef)
    {
        // ensure that the node exists and that it has the content aspect
        if (!nodeService.hasAspect(nodeRef, DictionaryBootstrap.ASPECT_CONTENT))
        {
            throw new AspectMissingException(DictionaryBootstrap.ASPECT_CONTENT, nodeRef);
        }
        
        // get the content URL
        CodeMonkey.todo("Use the value object toString conversion here"); // TODO
        String contentUrl = (String) nodeService.getProperty(
                nodeRef,
                DictionaryBootstrap.PROP_QNAME_CONTENT_URL);
        // check that the URL is available
        if (contentUrl == null)
        {
            // there is no URL - the interface specifies that this is not an error condition
            return null;
        }
        
        CodeMonkey.todo("Choose the store to read from at runtime");  // TODO
        ContentReader reader = store.getReader(contentUrl);
        
        // get the content mimetype
        String mimetype = (String) nodeService.getProperty(
                nodeRef,
                DictionaryBootstrap.PROP_QNAME_MIME_TYPE);
        reader.setMimetype(mimetype);
        // get the content encoding
        String encoding = (String) nodeService.getProperty(
                nodeRef,
                DictionaryBootstrap.PROP_QNAME_ENCODING);
        reader.setEncoding(encoding);
        
        // we don't listen for anything
        // result may be null - but interface contract says we may return null
        return reader;
    }

    public ContentWriter getWriter(NodeRef nodeRef)
    {
        CodeMonkey.todo("Choose the store to write to at runtime");  // TODO
        ContentWriter writer = store.getWriter(nodeRef);

        // get the content mimetype
        String mimetype = (String) nodeService.getProperty(
                nodeRef,
                DictionaryBootstrap.PROP_QNAME_MIME_TYPE);
        writer.setMimetype(mimetype);
        // get the content encoding
        String encoding = (String) nodeService.getProperty(
                nodeRef,
                DictionaryBootstrap.PROP_QNAME_ENCODING);
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
		// get the plain writer
		ContentWriter writer = getWriter(nodeRef);
		// get URL that is going to be written to
        String contentUrl = writer.getContentUrl();
        // need a listener to update the node when the stream closes
        WriteStreamListener listener = new WriteStreamListener(nodeRef, contentUrl);
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
        return store.getWriter();
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
        private String contentUrl;
        
        public WriteStreamListener(NodeRef nodeRef, String contentUrl)
        {
            this.nodeRef = nodeRef;
            this.contentUrl = contentUrl;
        }
        
        public void setNodeService(NodeService nodeService)
        {
            CodeMonkey.issue("The listener should get the node service from a registry"); // TODO
            this.nodeService = nodeService;
        }

        public void contentStreamClosed() throws ContentIOException
        {
            // change the content URL property of the node we are listening to
            nodeService.setProperty(
                    nodeRef,
                    DictionaryBootstrap.PROP_QNAME_CONTENT_URL,
                    contentUrl);
        }
    }

}
