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
        DocumentDetailsPage docDetailsPage = documentLibraryPage.selectFile(filename);
        docDetailsPage.getDrone().getCurrentPage().render();
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
