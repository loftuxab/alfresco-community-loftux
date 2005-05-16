package org.alfresco.repo.content;

import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.StoreRef;

/**
 * Provides low-level retrieval of content
 * {@link org.alfresco.repo.content.ContentReader readers} and
 * {@link org.alfresco.repo.content.ContentWriter writers}.
 * <p>
 * Implementations of this interface should be soley responsible for
 * providing persistence and retrieval of the content against a
 * <code>NodeRef</code>.  Problems such as whether the node exists or
 * not are irrelevant - rather the <code>NodeRef</code> should be regarded
 * as key against which to store the content.
 * <p>
 * The nature of the API means that it is <b>never</b> possible to
 * dictate the location of a write operation.
 * 
 * @author Derek Hulley
 */
public interface ContentStore
{
    public static final StoreRef TEMP_STOREREF = new StoreRef("tempstore", "files");
    public static final NodeRef TEMP_NODEREF = new NodeRef(TEMP_STOREREF, "tempfile");

    /**
     * Get the accessor with which to read from the content
     * at the given URL.  The reader is <b>stateful</b> and
     * should <b>only be used once</b>.
     * 
     * @param contentUrl the store-specific URL where the content is located
     * @return Returns a read-only content accessor or null if no content
     *      is present at the URL given
     */
    public ContentReader getReader(String contentUrl);
    
    /**
     * Get an accessor with which to write content to an anonymous location
     * within the store.  The writer is <b>stateful</b> and should
     * <b>only be used once</b>.
     * <p>
     * Every call to this method will return a writer onto a <b>new</b>
     * content URL.  It is never possible to write the same physical
     * location twice.
     *  
     * @return Returns a write-only content accessor
     *
     * @see #getWriter(NodeRef)
     * @see ContentWriter#addListener(ContentStreamListener)
     * @see ContentWriter#getContentUrl()
     */
    public ContentWriter getWriter();

    /**
     * Get the accessor with which to write content associated with
     * the given <code>NodeRef</code>.    The writer is <b>stateful</b>
     * and should <b>only be used once</b>.
     * <p>
     * Every call to this method will return a writer onto a <b>new</b>
     * content URL.  It is never possible to write the same physical
     * location twice. 
     * 
     * @param nodeRef the key against which the content is stored
     * @return Returns a write-only content accessor
     * 
     * @see #getWriter()
     * @see ContentWriter#addListener(ContentStreamListener)
     * @see ContentWriter#getContentUrl()
     */
	public ContentWriter getWriter(NodeRef nodeRef);
}
