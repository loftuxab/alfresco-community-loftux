/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.exporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.alfresco.service.cmr.view.ExportPackageHandler;
import org.alfresco.service.cmr.view.ExporterException;
import org.alfresco.util.TempFileProvider;


/**
 * Handler for exporting Repository to zip file
 * 
 * @author David Caruana
 */
public class ZipExportPackageHandler
    implements ExportPackageHandler
{
    protected OutputStream outputStream;
    protected File dataFile;
    protected File contentDir;
    protected File tempDataFile;
    protected OutputStream tempDataFileStream;
    protected ZipOutputStream zipStream;
    protected int iFileCnt = 0;

    
    /**
     * Construct
     * 
     * @param destDir
     * @param zipFile
     * @param dataFile
     * @param contentDir
     */
    public ZipExportPackageHandler(File destDir, File zipFile, File dataFile, File contentDir, boolean overwrite)
    {
        try
        {
            File absZipFile = new File(destDir, zipFile.getPath());
            log("Exporting to package zip file " + absZipFile.getAbsolutePath());

            if (absZipFile.exists())
            {
                if (overwrite == false)
                {
                    throw new ExporterException("Package zip file " + absZipFile.getAbsolutePath() + " already exists.");
                }
                log("Warning: Overwriting existing package zip file " + absZipFile.getAbsolutePath());
            }
            
            this.outputStream = new FileOutputStream(absZipFile);
            this.dataFile = dataFile;
            this.contentDir = contentDir;
        }
        catch (FileNotFoundException e)
        {
            throw new ExporterException("Failed to create zip file", e);
        }
    }

    /**
     * Construct
     * 
     * @param outputStream
     * @param dataFile
     * @param contentDir
     */
    public ZipExportPackageHandler(OutputStream outputStream, File dataFile, File contentDir)
    {
        this.outputStream = outputStream;
        this.dataFile = dataFile;
        this.contentDir = contentDir;
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.ExportPackageHandler#startExport()
     */
    public void startExport()
    {
        zipStream = new ZipOutputStream(outputStream);
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.ExportPackageHandler#createDataStream()
     */
    public OutputStream createDataStream()
    {
        tempDataFile = TempFileProvider.createTempFile("exportDataStream", ".xml");
        try
        {
            tempDataFileStream = new FileOutputStream(tempDataFile); 
            return tempDataFileStream;
        }
        catch (FileNotFoundException e)
        {
            throw new ExporterException("Failed to create data file stream", e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.ExportStreamHandler#exportStream(java.io.InputStream)
     */
    public String exportStream(InputStream exportStream)
    {
        // create zip entry for stream to export
        File file = new File(contentDir.getPath(), "content" + iFileCnt++ + ".bin");
        
        try
        {
            ZipEntry zipEntry = new ZipEntry(file.getPath());
            zipStream.putNextEntry(zipEntry);
            
            // copy export stream to zip
            copyStream(zipStream, exportStream);
        }
        catch (IOException e)
        {
            throw new ExporterException("Failed to zip export stream", e);
        }
        
        return file.getPath();
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.view.ExportPackageHandler#endExport()
     */
    public void endExport()
    {
        // add data file to zip stream
        ZipEntry zipEntry = new ZipEntry(dataFile.getPath());
        
        try
        {
            // close data file stream and place temp data file into zip output stream
            tempDataFileStream.close();
            zipStream.putNextEntry(zipEntry);
            InputStream dataFileStream = new FileInputStream(tempDataFile);
            copyStream(zipStream, dataFileStream);
            dataFileStream.close();
        }
        catch (IOException e)
        {
            throw new ExporterException("Failed to zip data stream file", e);
        }
        
        try
        {
            // close zip stream
            zipStream.close();
        }
        catch(IOException e)
        {
            throw new ExporterException("Failed to close zip package stream", e);
        }
    }
    
    /**
     * Log Export Message
     * 
     * @param message  message to log
     */
    protected void log(String message)
    {
    }

    /**
     * Copy input stream to output stream
     * 
     * @param output  output stream
     * @param in  input stream
     * @throws IOException
     */
    private void copyStream(OutputStream output, InputStream in)
        throws IOException
    {
        byte[] buffer = new byte[2048 * 10];
        int read = in.read(buffer, 0, 2048 *10);
        while (read != -1)
        {
            output.write(buffer, 0, read);
            read = in.read(buffer, 0, 2048 *10);
        }
    }
    
}
