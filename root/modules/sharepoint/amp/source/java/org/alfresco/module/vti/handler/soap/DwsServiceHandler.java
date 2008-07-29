/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * and Open Source Software ("FLOSS") applications as described in Alfresco's
 * FLOSS exception.  You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */

package org.alfresco.module.vti.handler.soap;

import java.util.List;

import org.alfresco.module.vti.metadata.soap.dws.DwsBean;
import org.alfresco.module.vti.metadata.soap.dws.DwsData;
import org.alfresco.module.vti.metadata.soap.dws.DwsMetadata;

/**
 * @author AndreyAk
 *
 */
public interface DwsServiceHandler
{
    /**
     * Returns information about a Document Workspace site and the lists it contains
     * 
     * @param document The site-based URL of a document in a document library in the Document Workspace site
     * @param id An optional document globally unique identifier (GUID)
     * @param minimal Determines whether the output includes information about the schemas, lists, documents,
     *                   links, and tasks lists of a Document Workspace site
     * @return DwsMetadata information about a Document Workspace site and the lists it contains
     */
    public DwsMetadata getDWSMetaData(String document, String id, boolean minimal) throws Exception;

    /**
     * Returns information about a Document Workspace site and the lists it contains
     *
     * @param document The site-based URL of a document in a document library in the Document Workspace site
     * @param lastUpdate returned in the results of a previous call to the GetDwsData or GetDwsMetadata method
     * @return DwsData
     */
    public DwsData getDwsData(String document, String lastUpdate) throws Exception;

    /**
     * Creates a Document Workspace site
     * @param parentDwsUrl url of the parent dws
     * @param name the optional URL of the new Document Workspace site. If an empty string is passed, the URL of the new SharePoint site is based on the title
     * @param users an optional list of users to add to the new SharePoint site
     * @param title the title of the new Document Workspace site
     * @param documents an optional list of documents. Used by Microsoft Office Outlook 2003 when adding shared attachments to a new Document Workspace site
     * @return bean with new dws description @see org.alfresco.module.vti.metadata.soap.dws.DwsBean
     *
     */
    public DwsBean createDws(String parentDwsUrl, String name, List users, String title, List documents);

    /**
     * creates a subfolder in a document library of the current Document Workspace site    
     * @param url the proposed site-based URL of the folder to create
     */
    public void createFolder(String url);

    /**
     * deletes a subfolder from a document library of the current Document Workspace site   
     * @param url The site-based URL of the folder to delete
     */
    public void deleteFolder(String url);

    /**
     * deletes the current Document Workspace site and its contents
     * @param dwsUrl url of dws to delete
     */
    public void deleteDws(String dwsUrl);

    /**
     * changes the title of the current Document Workspace site
     * @param oldDwsUrl url of dws to rename
     * @param title the new title for the Document Workspace site
     */
    public void renameDws(String oldDwsUrl, String title);

    /**
     * removes the specified user from the list of users for the current Document Workspace site
     * @param dwsUrl url of dws
     * @param id the ID of the user to be removed from the list of users
     */
    public void removeDwsUser(String dwsUrl, String id);
}
