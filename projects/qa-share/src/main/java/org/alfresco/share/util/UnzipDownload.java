/*
 * #%L
 * qa-share
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
/**
 * 
 */
package org.alfresco.share.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class does the unzip of the downloaded archieve.
 * 
 * @author cbairaajoni
 * 
 */
public class UnzipDownload
{

    private static Log logger = LogFactory.getLog(UnzipDownload.class);

    /**
     * This method is used to unzip and extract the folder or files of the downloaded archieve 
     * 
     * @param zipFile input zip file
     * @param outputFolder zip file output folder
     * @return boolean : This is true if unzip is successful, otherwise false.
     */
    public boolean unzip(String zipFile, String outputFolder)
    {
        logger.info("Extracting the file : " + zipFile + " to " + outputFolder);

        String fileName = null;
        File newFile = null;
        FileOutputStream fos = null;
        byte[] buffer = new byte[1024];

        try
        {
            // get the zip file content
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));

            // get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            if (ze == null)
            {
                logger.error("There is no file:" + zipFile + " present to extract.");
                zis.close();
                return false;
            }

            while (ze != null)
            {
                fileName = ze.getName();
                newFile = new File(outputFolder + File.separator + fileName);

                logger.info("file unzip : " + newFile.getAbsoluteFile());

                // Creating the folders of zip
                if (ze.isDirectory())
                {
                    (new File(newFile.getAbsolutePath())).mkdir();
                    ze = zis.getNextEntry();
                    continue;
                }

                // creating the files of zip
                fos = new FileOutputStream(newFile);
                int len;

                while ((len = zis.read(buffer)) > 0)
                {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            logger.info("Extracting the file : " + zipFile + " done.");
            zis.closeEntry();
            zis.close();

            return true;
        }
        catch (Exception e)
        {
            logger.error("Error in unzip() :" + zipFile + e);
            return false;
        }
    }
}