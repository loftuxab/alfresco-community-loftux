/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.officeservices.vfs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.alfresco.enterprise.repo.officeservices.metadata.ContentFilter;
import org.alfresco.enterprise.repo.officeservices.metadata.ContentFilterProcessingResult;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.TempFileProvider;
import org.apache.commons.io.IOUtils;

public class FilteredContentResponse implements ContentResponse
{

    protected byte[] filteredContent = null;
    
    protected File tempFile = null;
    
    public final static long TEMP_FILE_THRESHOLD = 100 * 1024;
    
    public FilteredContentResponse(File tempFile)
    {
        this.tempFile = tempFile;
    }
    
    public FilteredContentResponse(byte[] filteredContent)
    {
        this.filteredContent = filteredContent;
    }
    
    public static ContentResponse createContentResponse(NodeRef nodeRef, ContentReader contentReader, ContentFilter contentFilter)
    {
        if(contentReader == null)
        {
            throw new AlfrescoRuntimeException("FilteredContentResponse cannot be created without a ContentReader");
        }
        // decide for temporary storage area depending on unfiltered file size
        OutputStream tempStorageOutputStream;
        File tempFileBuffer = null;
        if(contentReader.getSize() > TEMP_FILE_THRESHOLD)
        {
            try
            {
                tempFileBuffer = TempFileProvider.createTempFile("FilteredContentResponse", "tmp");
                tempStorageOutputStream = new FileOutputStream(tempFileBuffer);
            }
            catch(IOException ioe)
            {
                throw new AlfrescoRuntimeException("Error creating temp file for document filtering", ioe);
            }
        }
        else
        {
            tempStorageOutputStream = new ByteArrayOutputStream();
        }
        boolean isFiltered = false;
        InputStream contentInputStream = contentReader.getContentInputStream();
        try
        {
            ContentFilterProcessingResult processingResult = contentFilter.process(nodeRef, contentInputStream, tempStorageOutputStream, true);
            isFiltered = (processingResult != null) && processingResult.isModified();
        }
        catch(IOException ioe)
        {
            isFiltered = false;
        }
        finally
        {
            try
            {
                contentInputStream.close();
            }
            catch(IOException ioe)
            {
                ; // ignore
            }
        }
        if(!isFiltered)
        {
            try
            {
                tempStorageOutputStream.close();
                if(tempFileBuffer != null)
                {
                    tempFileBuffer.delete();
                }
            }
            catch(IOException ioe)
            {
                ; //
            }
            return null;
        }
        if(tempStorageOutputStream instanceof ByteArrayOutputStream)
        {
            return new FilteredContentResponse(((ByteArrayOutputStream)tempStorageOutputStream).toByteArray());
        }
        return new FilteredContentResponse(tempFileBuffer);
    }

    @Override
    public long getSize()
    {
        if(filteredContent != null)
        {
            return filteredContent.length;
        }
        else if(tempFile != null)
        {
            return tempFile.length();
        }
        else
        {
            return 0;
        }
    }

    @Override
    public void emitContent(OutputStream out) throws IOException, ContentIOException
    {
        if(filteredContent != null)
        {
            out.write(filteredContent);
        }
        else if(tempFile != null)
        {
            FileInputStream fis = new FileInputStream(tempFile);
            try
            {
                IOUtils.copy(fis, out);
            }
            finally
            {
                fis.close();
            }
        }
    }

    @Override
    public InputStream getContentInputStream() throws IOException, ContentIOException
    {
        return new FileInputStream(tempFile);
    }

    @Override
    public void releaseResources()
    {
        tempFile.delete();
    }

}
