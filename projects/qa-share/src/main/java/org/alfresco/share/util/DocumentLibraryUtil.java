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
package org.alfresco.share.util;

import java.io.File;
import java.util.Map;

import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;

/**
 * This class will be responsible for handling common operation over DocumentLibrary
 * 
 * @author Paul Brodner
 */
public class DocumentLibraryUtil
{
    private final static Object waitObject = new Object();

    /**
     * assume that we are in the DocumentLibrary root
     * Return the properties details of a particular file from DocumentLibary
     * 
     * @param documentLibraryPage
     * @param filename
     * @return
     */
    public static Map<String, Object> getDocumentProperties(DocumentLibraryPage documentLibraryPage, String filename)
    {
        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(filename).render();
        return docDetailsPage.getProperties();
    }

    /**
     * assume that we are in the DocumentLibrary root
     * Use this method in order to Downlaod a file from DocumentLibrary
     * Add downloadedFile parameter - as the location of downloaded file
     * 
     * @param documentLibraryPage
     * @param downloadFileName
     * @param downloadedFile
     * @throws InterruptedException
     */
    public static void downloadFile(DocumentLibraryPage documentLibraryPage, String downloadFileName, File downloadedFile) throws InterruptedException
    {
        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(downloadFileName).render();
        docDetailsPage.selectDownloadFromActions(downloadedFile);
        synchronized (waitObject)
        {
            while (!downloadedFile.exists())
            {
                waitObject.wait();
            }
        }
        fileDownloaded(downloadedFile);

    }

    public static boolean isFileLockedByYou(DocumentLibraryPage documentLibraryPage, String filename)
    {
        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(filename).render();
        return docDetailsPage.isLockedByYou();
    }

    public static boolean isFileLocked(DocumentLibraryPage documentLibraryPage, String filename)
    {
        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(filename).render();
        return docDetailsPage.isCheckedOut();
    }

    public static void fileDownloaded(File file) throws InterruptedException
    {
        synchronized (waitObject)
        {
            if (file.exists())
            {
                waitObject.notifyAll();
            }
        }

    }
}
