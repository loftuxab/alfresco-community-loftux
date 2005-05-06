package org.alfresco.repo.content;

import org.alfresco.repo.ref.NodeRef;

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
 * 
 * @author Derek Hulley
 */
public interface ContentStore
{
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
     * Get the accessor with which to write to the content
     * associated with the given <code>NodeRef</code>.    The
     * writer is <b>stateful</b> and should <b>only be used once</b>.
     * 
     * @param nodeRef the key against which the content is stored
     * @return Returns a write-only content accessor
     * 
     * @see ContentWriter#addListener(ContentStreamListener)
     */
    public ContentWriter getWriter(NodeRef nodeRef);
}
