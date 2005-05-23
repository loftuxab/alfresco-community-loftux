package org.alfresco.repo.content.transform;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

import net.sf.joott.uno.DocumentConverter;
import net.sf.joott.uno.DocumentConverterFactory;
import net.sf.joott.uno.DocumentFormat;
import net.sf.joott.uno.UnoConnection;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.ContentIOException;
import org.alfresco.repo.content.ContentReader;
import org.alfresco.repo.content.ContentWriter;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Makes use of the OpenOffice Uno interfaces to convert the content.
 * <p>
 * The 
 * 
 * @author Derek Hulley
 */
public class UnoContentTransformer implements ContentTransformer
{
    private static final Log logger = LogFactory.getLog(UnoContentTransformer.class);
    
    /** map of <tt>DocumentFormat</tt> instances keyed by mimetype */
    private static Map<String, DocumentFormat> formatsByMimetype;

    static
    {
        // Build the map of known Uno document formats and store by mimetype 
        formatsByMimetype = new HashMap<String, DocumentFormat>(17);
//        formatsByMimetype.put(DocumentFormat.FLASH_IMPRESS.getMimeType(), DocumentFormat.FLASH_IMPRESS);
//        formatsByMimetype.put(DocumentFormat.HTML_CALC.getMimeType(), DocumentFormat.HTML_CALC);
        formatsByMimetype.put(DocumentFormat.HTML_WRITER.getMimeType(), DocumentFormat.HTML_WRITER);
        formatsByMimetype.put(DocumentFormat.MS_EXCEL_97.getMimeType(), DocumentFormat.MS_EXCEL_97);
        formatsByMimetype.put(DocumentFormat.MS_POWERPOINT_97.getMimeType(), DocumentFormat.MS_POWERPOINT_97);
        formatsByMimetype.put(DocumentFormat.MS_WORD_97.getMimeType(), DocumentFormat.MS_WORD_97);
//        formatsByMimetype.put(DocumentFormat.PDF_CALC.getMimeType(), DocumentFormat.PDF_CALC);
//        formatsByMimetype.put(DocumentFormat.PDF_IMPRESS.getMimeType(), DocumentFormat.PDF_IMPRESS);
        formatsByMimetype.put(DocumentFormat.PDF_WRITER.getMimeType(), DocumentFormat.PDF_WRITER);
//        formatsByMimetype.put(DocumentFormat.PDF_WRITER_WEB.getMimeType(), DocumentFormat.PDF_WRITER_WEB);
        formatsByMimetype.put(DocumentFormat.RTF.getMimeType(), DocumentFormat.RTF);
        formatsByMimetype.put(DocumentFormat.TEXT.getMimeType(), DocumentFormat.TEXT);
//        formatsByMimetype.put(DocumentFormat.TEXT_CALC.getMimeType(), DocumentFormat.TEXT_CALC);
//        formatsByMimetype.put(DocumentFormat.XML_CALC.getMimeType(), DocumentFormat.XML_CALC);
//        formatsByMimetype.put(DocumentFormat.XML_IMPRESS.getMimeType(), DocumentFormat.XML_IMPRESS);
        formatsByMimetype.put(DocumentFormat.XML_WRITER.getMimeType(), DocumentFormat.XML_WRITER);
//        formatsByMimetype.put(DocumentFormat.XML_WRITER_WEB.getMimeType(), DocumentFormat.XML_WRITER_WEB);
    }
    
    private UnoConnection connection;
    
    /**
     * Constructs the default transformer that will attempt to connect to the
     * Uno server using the default connect string.
     * 
     * @see UnoConnection#DEFAULT_CONNECTION_STRING
     */
    public UnoContentTransformer()
    {
        init(UnoConnection.DEFAULT_CONNECTION_STRING);
    }
    
//    /**
//     * Construct a transformer that will fetch its configuration from the given
//     * service.
//     * 
//     * @param configService a service containing the required configuration
//     */
//    public UnoContentTransformer(ConfigService configService)
//    {
//        // get the connection string from the service
//        init
//    }
    
    /**
     * Constructs a transformer that uses the given url to establish
     * a connection.
     * 
     * @param unoConnectionUrl the Uno server connection URL
     */
    public UnoContentTransformer(String unoConnectionUrl)
    {
        init(unoConnectionUrl);
    }
    
    /**
     * @param unoConnectionUrl the URL of the Uno server
     */
    private void init(String unoConnectionUrl)
    {
        connection = new UnoConnection(unoConnectionUrl);
    }
    
    /**
     * @return Returns true if a connection to the Uno server could be established
     */
    public boolean isConnected()
    {
        try
        {
            DocumentConverterFactory.getConnection();
            return true;
        }
        catch (ConnectException e)
        {
            // connection failed
            return false;
        }
    }

    /**
     * Checks how reliable the conversion will be when performed by the Uno server.
     * <p>
     * The connection for the Uno server is checked in order to have any chance of
     * being reliable.
     * <p>
     * The conversions can either be 100% reliable if both mimetypes are supported
     * by the Uno converter, or 0% reliable if one of the mimetypes is not supported.
     */
    public double getReliability(String sourceMimetype, String targetMimetype)
    {
        // check if a connection to the Uno server can be established
        if (!isConnected())
        {
            // no connection means that conversion is not possible
            return 0.0;
        }
        // check if the source and target mimetypes are supported
        if (UnoContentTransformer.formatsByMimetype.get(sourceMimetype) == null)
        {
            return 0.0;
        }
        else if (UnoContentTransformer.formatsByMimetype.get(targetMimetype) == null)
        {
            return 0.0;
        }
        else
        {
            // both formats are supported and conversion is therefore fully supported
            return 1.0;
        }
    }

    /**
     * Uses the standard 
     */
    public void transform(ContentReader reader, ContentWriter writer) throws ContentIOException
    {
        String sourceMimetype = reader.getMimetype();
        String targetMimetype = writer.getMimetype();
        if (sourceMimetype == null || targetMimetype == null)
        {
            throw new AlfrescoRuntimeException(
                    "Content reader and writer must have mimetypes set \n" +
                    "   reader: " + reader + "\n" +
                    "   writer: " + writer);
        }
        // check that the conversion is reliable
        double reliability = getReliability(sourceMimetype, targetMimetype);
        if (reliability < 1.0)
        {
            throw new AlfrescoRuntimeException("Unreliable Uno server conversion: \n" +
                    "   reader: " + reader + "\n" +
                    "   writer: " + writer);
        }
        // document formats
        DocumentFormat sourceFormat = UnoContentTransformer.formatsByMimetype.get(sourceMimetype);
        DocumentFormat targetFormat = UnoContentTransformer.formatsByMimetype.get(targetMimetype);
        if (sourceFormat == null || targetFormat == null)
        {
            throw new AlfrescoRuntimeException("Conversion formats not supported: \n" +
                    "   reader: " + reader + "\n" +
                    "   writer: " + writer);
        }
        // create temporary files to convert from and to
        File tempFromFile = TempFileProvider.createTempFile("UnoContentTransformer",
                "." + sourceFormat.getFileExtension());
        File tempToFile = TempFileProvider.createTempFile("UnoContentTransformer",
                "." + targetFormat.getFileExtension());
        // download the content from the source reader
        reader.getContent(tempFromFile);
        
        // perform the conversion using the Uno server
        try
        {
            DocumentConverter converter = DocumentConverterFactory.getConverter();
            converter.convert(tempFromFile, tempToFile, targetFormat);
            // conversion success
        }
        catch (ConnectException e)
        {
            throw new ContentIOException("Connection to Uno server failed: \n" +
                    "   reader: " + reader + "\n" +
                    "   writer: " + writer,
                    e);
        }
        catch (IOException e)
        {
            throw new ContentIOException("Uno server conversion failed: \n" +
                    "   reader: " + reader + "\n" +
                    "   writer: " + writer + "\n" +
                    "   from file: " + tempFromFile + "\n" +
                    "   to file: " + tempToFile,
                    e);
        }
        
        // upload the temp output to the writer given us
        writer.putContent(tempToFile);
        
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Uno server conversion succeeded: \n" +
                    "   reader: " + reader + "\n" +
                    "   writer: " + writer + "\n" +
                    "   from file: " + tempFromFile + "\n" +
                    "   to file: " + tempToFile);
        }
    }
}
