/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
package org.alfresco.module.vti.handler;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.vti.metadata.dic.CAMLMethod;
import org.alfresco.module.vti.metadata.model.DocumentBean;
import org.alfresco.module.vti.metadata.model.DwsBean;
import org.alfresco.module.vti.metadata.model.DwsData;
import org.alfresco.module.vti.metadata.model.DwsMetadata;
import org.alfresco.module.vti.metadata.model.LinkBean;
import org.alfresco.module.vti.metadata.model.UserBean;
import org.apache.commons.httpclient.HttpException;

/**
 * Document workspace site service fundamental API.
 * 
 * @author AndreyAk
 */
public interface DwsServiceHandler
{

    /**
     * Returns information about a document workspace site and the lists it contains
     * 
     * @param document the site-based URL of a document in a document library in the document workspace site
     * @param id an optional document globally unique identifier (GUID)
     * @param minimal determines whether the output includes information about the schemas, lists, documents, links, and tasks lists of a document workspace site
     * @return DwsMetadata information about a document workspace site and the lists it contains ({@link DwsMetadata})
     */
    public DwsMetadata getDWSMetaData(String document, String id, boolean minimal) throws Exception;

    /**
     * Returns information about a document workspace site and the lists it contains
     * 
     * @param document the site-based URL of a document in a document library in the document workspace site
     * @param lastUpdate returned in the results of a previous call to the GetDwsData or GetDwsMetadata method
     * @return DwsData information about a document workspace site ({@link DwsData})
     */
    public DwsData getDwsData(String document, String lastUpdate) throws Exception;

    /**
     * Creates a document workspace site
     * 
     * @param parentDwsUrl url of the parent dws
     * @param name the optional URL of the new document workspace site. If an empty string is passed, the URL of the new SharePoint site is based on the title
     * @param users an optional list of users to add to the new SharePoint site
     * @param title the title of the new document workspace site
     * @param documents an optional list of documents. Used by Microsoft Office Outlook 2003 when adding shared attachments to a new document workspace site
     * @param host application host
     * @param context application context
     * @param username user name
     * @param password user password
     * @return DwsBean information about a new document workspace site DwsData ({@link DwsBean})
     */
    public DwsBean createDws(String parentDwsUrl, String name, List<UserBean> users, String title, List<DocumentBean> documents, String host, String context, String username,
            String password);

    /**
     * Creates a subfolder in a document library of the current document workspace site
     * 
     * @param url the proposed site-based URL of the folder to create
     */
    public void createFolder(String url);

    /**
     * Deletes a subfolder from a document library of the current document workspace site
     * 
     * @param url The site-based URL of the folder to delete
     */
    public void deleteFolder(String url);

    /**
     * Deletes the current document workspace site and its contents
     * 
     * @param dwsUrl url of dws to delete
     * @param username user name
     * @param password user password
     */
    public void deleteDws(String dwsUrl, String username, String password);

    /**
     * Changes the title of the current document workspace site
     * 
     * @param oldDwsUrl url of dws to rename
     * @param title the new title for the document workspace site
     */
    public void renameDws(String oldDwsUrl, String title);

    /**
     * Removes the specified user from the list of users for the current document workspace site
     * 
     * @param dwsUrl URL of document workspace site
     * @param id the ID of the user to be removed from the list of users
     */
    public void removeDwsUser(String dwsUrl, String id);

    /**
     * Redirect request to the appropriate page
     * 
     * @param req HTTP request
     * @param resp HTTP response
     * @throws IOException
     * @throws HttpException
     */
    public void handleRedirect(HttpServletRequest req, HttpServletResponse resp) throws HttpException, IOException;

    /**
     * Update document workspace site data such as links
     * 
     * @param linkBean ({@link LinkBean})
     * @param method 
     * @param dws document workspace site name 
     * @return linkBean that was changed
     */
    public LinkBean updateDwsData(LinkBean linkBean, CAMLMethod method, String dws);
}
