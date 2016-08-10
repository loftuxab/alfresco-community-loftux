/*
 * #%L
 * Alfresco Sharepoint Protocol
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
