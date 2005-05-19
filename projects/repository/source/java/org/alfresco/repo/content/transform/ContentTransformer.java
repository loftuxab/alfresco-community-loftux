package org.alfresco.repo.content.transform;

import org.alfresco.repo.content.ContentIOException;
import org.alfresco.repo.content.ContentReader;
import org.alfresco.repo.content.ContentWriter;

/**
 * Interface for class that allow content transformation from one mimetype to another.
 * 
 * @author Derek Hulley
 */
public interface ContentTransformer
{
    /**
     * Provides a score of transforming content from a specific mimetype to another.
     * <p>
     * This method is used to determine, up front, which of a set of
     * transformers will be used to perform a specific transformation.
     * 
     * @param sourceMimetype the source mimetype
     * @param targetMimetype the target mimetype 
     * @return Returns a score 0.0 to 1.0.  0.0 indicates that the
     *      transformation cannot be performed at all.  1.0 indicates that
     *      the transformation can be performed perfectly.
     */
    public double getReliability(String sourceMimetype, String targetMimetype);
    
    /**
     * Transforms the content provided by the reader and source mimetype
     * to the writer and target mimetype.
     * <p>
     * The transformation viability can be determined by an up front call
     * to {@link #getReliability(String, String)}.
     * <p>
     * The source and target mimetypes <b>must</b> be available on the
     * {@link org.alfresco.repo.content.Content#getMimetype()} methods of
     * both the reader and the writer.
     * 
     * @param reader the source of the content
     * @param writer the destination of the transformed content
     * @throws ContentIOException if an IO exception occurs
     */
    public void transform(ContentReader reader, ContentWriter writer) throws ContentIOException;
}
