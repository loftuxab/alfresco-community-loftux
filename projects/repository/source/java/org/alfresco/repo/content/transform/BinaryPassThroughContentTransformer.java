package org.alfresco.repo.content.transform;

import org.alfresco.repo.content.ContentIOException;
import org.alfresco.repo.content.ContentReader;
import org.alfresco.repo.content.ContentWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Allows direct streaming from source to target when the respective mimetypes
 * are identical, except where the mimetype is text.
 * <p>
 * Text has to be transformed based on the encoding even if the mimetypes don't
 * reflect it. 
 * 
 * @see org.alfresco.repo.content.transform.StringExtractingContentTransformer
 * 
 * @author Derek Hulley
 */
public class BinaryPassThroughContentTransformer extends AbstractContentTransformer
{
    private static final Log logger = LogFactory.getLog(BinaryPassThroughContentTransformer.class);
    
    /**
     * @return Returns 1.0 if the formats are identical and not text
     */
    public double getReliability(String sourceMimetype, String targetMimetype)
    {
        if (sourceMimetype.startsWith(StringExtractingContentTransformer.PREFIX_TEXT))
        {
            // we can only stream binary content through
            return 0.0;
        }
        else if (!sourceMimetype.equals(targetMimetype))
        {
            // no transformation is possible so formats must be exact
            return 0.0;
        }
        else
        {
            // formats are the same and are not text
            return 1.0;
        }
    }

    /**
     * Performs a direct stream provided the preconditions are met
     */
    public void transform(ContentReader reader, ContentWriter writer) throws ContentIOException
    {
        // begin timing
        long before = System.currentTimeMillis();
        
        String sourceMimetype = getMimetype(reader);
        String targetMimetype = getMimetype(writer);
        // check the reliability - takes care of the check limiting the target mimetype to plain text
        checkReliability(reader, writer);
        
        // just stream it
        writer.putContent(reader.getContentInputStream());
        
        // record time
        long after = System.currentTimeMillis();
        recordTime(after - before);
        
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Completed transformation: \n" +
                    "   reader: " + reader + "\n" +
                    "   writer: " + writer);
        }
    }
}
