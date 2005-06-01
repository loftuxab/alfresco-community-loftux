package org.alfresco.repo.content.transform;

import java.io.IOException;

import org.alfresco.repo.content.ContentIOException;
import org.alfresco.repo.content.ContentReader;
import org.alfresco.repo.content.ContentWriter;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.util.debug.CodeMonkey;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

/**
 * Makes use of the {@link http://www.pdfbox.org/ PDFBox} library to
 * perform conversions from PDF files to text.
 * 
 * @author Derek Hulley
 */
public class PdfBoxContentTransformer extends AbstractContentTransformer
{
    private static final Log logger = LogFactory.getLog(PdfBoxContentTransformer.class);
    
    /**
     * Currently the only transformation performed is that of text extraction from PDF documents.
     */
    public double getReliability(String sourceMimetype, String targetMimetype)
    {
        CodeMonkey.todo("Expand PDFBox usage to convert images to PDF and investigate other conversions"); // TODO
        
        if (!MimetypeMap.MIMETYPE_PDF.equals(sourceMimetype) ||
                !MimetypeMap.MIMETYPE_TEXT_PLAIN.equals(targetMimetype))
        {
            // only support PDF -> Text
            return 0.0;
        }
        else
        {
            return 1.0;
        }
    }

    public void transformInternal(ContentReader reader, ContentWriter writer) throws Exception
    {
        PDDocument pdf = null;
        try
        {
            // stream the document in
            pdf = PDDocument.load(reader.getContentInputStream());
            // strip the text out
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(pdf);
            
            // dump it all to the writer
            writer.putContent(text);
        }
        catch (IOException e)
        {
            throw new ContentIOException("PDF text stripping failed: \n" +
                    "   reader: " + reader);
        }
        finally
        {
            if (pdf != null)
            {
                try { pdf.close(); } catch (Throwable e) {e.printStackTrace(); }
            }
        }
    }
}
