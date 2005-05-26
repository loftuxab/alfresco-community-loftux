package org.alfresco.repo.content.transform;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.Content;
import org.alfresco.repo.content.ContentReader;
import org.alfresco.repo.content.ContentWriter;

/**
 * Provides basic services for {@link org.alfresco.repo.content.transform.ContentTransformer}
 * implementations.
 * 
 * @author Derek Hulley
 */
public abstract class AbstractContentTransformer implements ContentTransformer
{
    private double averageTime = 0.0;
    private long count = 0L;
    
    /**
     * All transformers start with an average transformation time of 0.0ms.
     */
    protected AbstractContentTransformer()
    {
        averageTime = 0.0;
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName())
          .append("[ average=").append((long)averageTime).append("ms")
          .append("]");
        return sb.toString();
    }
    
    /**
     * Convenience to fetch and check the mimetype for the given content
     * 
     * @param content the reader/writer for the content
     * @return Returns the mimetype for the content
     * @throws AlfrescoRuntimeException if the content doesn't have a mimetype
     */
    protected String getMimetype(Content content)
    {
        String mimetype = content.getMimetype();
        if (mimetype == null)
        {
            throw new AlfrescoRuntimeException("Mimetype is mandatory for transformation: " + content);
        }
        // done
        return mimetype;
    }
    
    /**
     * Convenience method to check the reliability of a transformation
     * 
     * @param reader
     * @param writer
     * @throws AlfrescoRuntimeException if the reliability isn't > 0
     */
    protected void checkReliability(ContentReader reader, ContentWriter writer)
    {
        String sourceMimetype = getMimetype(reader);
        String targetMimetype = getMimetype(writer);
        if (getReliability(sourceMimetype, targetMimetype) <= 0.0)
        {
            throw new AlfrescoRuntimeException("Zero scoring transformation attempted: \n" +
                    "   reader: " + reader + "\n" +
                    "   writer: " + writer);
        }
        // it all checks out OK
    }

    /**
     * @return Returns the calculated running average of the current transformations
     */
    public synchronized long getTransformationTime()
    {
        return (long) averageTime;
    }

    /**
     * Records and updates the average transformation time for this transformer.
     * <p>
     * Subclasses should call this after every transformation in order to keep
     * the running average of the transformation times up to date.
     * 
     * @param transformationTime the time it took to perform the transformation.
     *      The value may be 0.
     */
    protected final synchronized void recordTime(long transformationTime)
    {
        if (count == Long.MAX_VALUE)
        {
            // we have reached the max count - reduce it by half
            // the average fluctuation won't be extreme
            count /= 2L;
        }
        // adjust the average
        count++;
        double diffTime = ((double) transformationTime) - averageTime;
        averageTime += diffTime / (double) count;
    }
}
