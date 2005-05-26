package org.alfresco.repo.content.transform;

import org.alfresco.repo.content.ContentIOException;
import org.alfresco.repo.content.ContentReader;
import org.alfresco.repo.content.ContentWriter;
import org.alfresco.repo.content.MimetypeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Converts any textual format to plain text.
 * <p>
 * The transformation is sensitive to the source and target string encodings.
 * 
 * @author Derek Hulley
 */
public class StringExtractingContentTransformer extends AbstractContentTransformer
{
    public static final String PREFIX_TEXT = "text/";
    
    private static final Log logger = LogFactory.getLog(StringExtractingContentTransformer.class);

    public StringExtractingContentTransformer()
    {
    }
    
    /**
     * Gives a high reliability for all translations from <i>text/sometype</i> to
     * <i>text/plain</i>.  As the text formats are already text, the characters
     * are preserved and no actual conversion takes place.
     * <p>
     * Extraction of text from binary data is wholly unreliable.
     */
    public double getReliability(String sourceMimetype, String targetMimetype)
    {
        if (!targetMimetype.equals(MimetypeMap.MIMETYPE_TEXT_PLAIN))
        {
            // can only convert to plain text
            return 0.0;
        }
        else if (sourceMimetype.startsWith(PREFIX_TEXT))
        {
            // transformations from any text to plain text is OK
            return 1.0;
        }
        else
        {
            // extracting text from binary is not useful
            return 0.0;
        }
    }

    /**
     * Text to text conversions are done directly using the content reader and writer string
     * manipulation methods.
     * <p>
     * Extraction of text from binary content attempts to take the possible character
     * encoding into account.  The text produced from this will, if the encoding was correct,
     * be unformatted but valid. 
     */
    public void transform(ContentReader reader, ContentWriter writer) throws ContentIOException
    {
        // begin timing
        long before = System.currentTimeMillis();
        
        String sourceMimetype = getMimetype(reader);
        String targetMimetype = getMimetype(writer);
        // check the reliability - takes care of the check limiting the target mimetype to plain text
        checkReliability(reader, writer);
        // is this a straight text-text transformation
        transformText(reader, writer);
        
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
    
    private void transformText(ContentReader reader, ContentWriter writer)
    {
        // just read the text directly from the reader, which will handle encoding
        String text = reader.getContentString();
        // transfer it directly to the writer, which will handle encoding
        writer.putContent(text);
        // done
    }
}
