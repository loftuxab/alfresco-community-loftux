package org.alfresco.repo.content.transform;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.ContentIOException;
import org.alfresco.repo.content.ContentReader;
import org.alfresco.repo.content.ContentWriter;
import org.alfresco.util.debug.CodeMonkey;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Converts any format to text by searching for and extracting any text
 * from the content stream.  The standard UTF-8 format is used and all
 * non-character bytes are converted to spaces.
 * 
 * @author Derek Hulley
 */
public class StringExtractingContentTransformer implements ContentTransformer
{
    public static final String PREFIX_TEXT = "text/";
    public static final String MIMETYPE_TEXT_PLAIN = "text/plain";
    
    private static final Log logger = LogFactory.getLog(StringExtractingContentTransformer.class);

    /**
     * Gives a high reliability for all translations from <i>text/sometype</i> to
     * <i>text/plain</i>.  As the text formats are already text, the characters
     * are preserved and no actual conversion takes place.
     * <p>
     * Extraction of text from binary data is unreliable.
     */
    public double getReliability(String sourceMimetype, String targetMimetype)
    {
        if (!targetMimetype.equals(MIMETYPE_TEXT_PLAIN))
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
            // will have to extract text from binary
            return 0.1;
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
        String sourceMimetype = reader.getMimetype();
        String targetMimetype = writer.getMimetype();
        if (sourceMimetype == null || targetMimetype == null)
        {
            throw new AlfrescoRuntimeException("Both source and target mimetypes must be valid: \n" +
                    "   reader: " + reader + "\n" +
                    "   writer: " + writer);
        }
        // check the reliability - takes care of the check limiting the target mimetype to plain text
        if (getReliability(sourceMimetype, targetMimetype) <= 0.0)
        {
            throw new AlfrescoRuntimeException("Zero scoring transformation attempted: \n" +
                    "   reader: " + reader + "\n" +
                    "   writer: " + writer);
        }
        // is this a straight text-text transformation
        if (sourceMimetype.startsWith(PREFIX_TEXT))
        {
            transformText(reader, writer);
        }
        else
        {
            transformBinary(reader, writer);
        }
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
    
    private void transformBinary(ContentReader reader, ContentWriter writer)
    {
        CodeMonkey.todo("Get the most likely string encoding for the source mimetype");  // TODO
        reader.setEncoding(writer.getEncoding());  // use the target encoding for now
        
        // for now, just dump it all into a string and hope for the best
        String text = reader.getContentString();
        // write out
        writer.putContent(text);
        // done
    }
}
