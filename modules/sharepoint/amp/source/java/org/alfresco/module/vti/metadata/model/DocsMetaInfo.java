package org.alfresco.module.vti.metadata.model;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>Bean class that is used to store lists of files, folders and failed items.</p>
 * 
 * @author Michael Shavnev
 *
 */
public class DocsMetaInfo
{
    private List<DocMetaInfo> fileMetaInfoList = new LinkedList<DocMetaInfo>();
    private List<DocMetaInfo> folderMetaInfoList = new LinkedList<DocMetaInfo>();
    private List<DocMetaInfo> failedUrls = new LinkedList<DocMetaInfo>();

    public List<DocMetaInfo> getFileMetaInfoList()
    {
        return fileMetaInfoList;
    }

    public List<DocMetaInfo> getFolderMetaInfoList()
    {
        return folderMetaInfoList;
    }

    public List<DocMetaInfo> getFailedUrls()
    {
        return failedUrls;
    }

}
