package org.alfresco.repo.content;

import org.alfresco.repo.node.InvalidNodeRefException;
import org.alfresco.repo.node.InvalidNodeTypeException;
import org.alfresco.repo.ref.NodeRef;

/**
 * Provides methods for accessing and transforming content.
 * <p>
 * Implementations of this service are primarily responsible for ensuring
 * that the correct store is used to access content, and that reads and
 * writes for the same node reference are routed to the same store instance.
 * <p>
 * The mechanism for selecting an appropriate store is not prescribed by
 * the interface, but typically the decision will be made on the grounds
 * of content type.
 * <p>
 * Whereas the content stores have no knowledge of nodes other than their
 * references, the <code>ContentService</code> <b>is</b> responsible for
 * ensuring that all the relevant node-content relationships are maintained.
 * 
 * @see org.alfresco.repo.content.ContentStore
 * @see org.alfresco.repo.content.ContentReader
 * @see org.alfresco.repo.content.ContentWriter
 * 
 * @author Derek Hulley
 */
public interface ContentService
{
    /**
     * Gets a reader for the content associated with the given node.
     * 
     * @param nodeRef a reference to a node with the <b>content</b> aspect
     * @return Returns a reader for the content associated with the node,
     *      or null if no content has been written for the node
     * @throws InvalidNodeRefException if the node doesn't exist
     * @throws InvalidNodeTypeException if the node is not of type <b>content</b>
     */
    public ContentReader getReader(NodeRef nodeRef)
            throws InvalidNodeRefException, InvalidNodeTypeException;

	/**
	 * Gets a writer for the content associated with the given node.
	 * <p>
	 * There is no work performed to update the node to point to the
	 * new {@link ContentWriter#getContentUrl() content URL}.
	 * 
     * @param nodeRef a reference to a node.
     * @return Returns a writer for the content associated with the node.
     * @throws InvalidNodeRefException if the node doesn't exist
     * @throws InvalidNodeTypeException if the node is not of type <b>content</b>
	 */
	public ContentWriter getWriter(NodeRef nodeRef)
            throws InvalidNodeRefException, InvalidNodeTypeException;
	
    /**
     * Gets a writer for the content associated with the given node.
     * <p>
     * When the writer output stream is closed the node will automatically
     * be updated to point to the new
     * {@link ContentWriter#getContentUrl() content URL}.
     * 
     * @param nodeRef a reference to a node with the <b>content</b> aspect.
     * @return Returns a writer for the content associated with the node.
     * @throws InvalidNodeRefException if the node doesn't exist
     * @throws InvalidNodeTypeException if the node is not of type <b>content</b>
     */
    public ContentWriter getUpdatingWriter(NodeRef nodeRef)
            throws InvalidNodeRefException, InvalidNodeTypeException;
    
    /**
     * Gets a writer to a temporary location.  The longevity of the stored
     * temporary content is determined by the system.
     * 
     * @return Returns a writer onto a temporary location
     */
    public ContentWriter getTempWriter();
    
    /**
     * Transforms the content from the reader and writes the content
     * back out to the writer.
     * <p>
     * The mimetypes used for the transformation must be set both on
     * the {@link Content#getMimetype() reader} and on the
     * {@link Content#getMimetype() writer}.
     * 
     * @param reader the source content location and mimetype
     * @param writer the target content location and mimetype
     * @throws NoTransformerException if no transformer exists for the
     *      given source and target mimetypes of the reader and writer
     * @throws ContentIOException if the transformation fails
     */
    public void transform(ContentReader reader, ContentWriter writer)
            throws NoTransformerException, ContentIOException;
}
