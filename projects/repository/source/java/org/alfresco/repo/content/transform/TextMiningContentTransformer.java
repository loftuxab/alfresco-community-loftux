package org.alfresco.repo.content.transform;

import java.io.InputStream;

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.textmining.text.extraction.WordExtractor;

/**
 * Makes use of the {@link http://www.textmining.org/ TextMining} library to
 * perform conversions from MSWord documents to text.
 * 
 * @author Derek Hulley
 */
public class TextMiningContentTransformer extends AbstractContentTransformer
{
    private static final Log logger = LogFactory.getLog(TextMiningContentTransformer.class);
    
    private WordExtractor wordExtractor;
    
    public TextMiningContentTransformer()
    {
        this.wordExtractor = new WordExtractor();
    }
    
    /**
     * Currently the only transformation performed is that of text extraction from Word documents.
     */
    public double getReliability(String sourceMimetype, String targetMimetype)
    {
        if (!MimetypeMap.MIMETYPE_WORD.equals(sourceMimetype) ||
                !MimetypeMap.MIMETYPE_TEXT_PLAIN.equals(targetMimetype))
        {
            // only support DOC -> Text
            return 0.0;
        }
        else
        {
            return 1.0;
        }
    }

    public void transformInternal(ContentReader reader, ContentWriter writer) throws Exception
    {
        InputStream is = reader.getContentInputStream();
        String text = wordExtractor.extractText(is);
        // dump the text out
        writer.putContent(text);
    }
}
