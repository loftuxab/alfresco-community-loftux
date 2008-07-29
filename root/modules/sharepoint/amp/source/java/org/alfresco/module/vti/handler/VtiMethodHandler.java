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
package org.alfresco.module.vti.handler;

import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.alfresco.module.vti.VtiException;
import org.alfresco.module.vti.metadata.DocMetaInfo;
import org.alfresco.module.vti.metadata.DocsMetaInfo;
import org.alfresco.module.vti.metadata.Document;
import org.alfresco.module.vti.metadata.dic.options.GetOption;
import org.alfresco.module.vti.metadata.dic.options.PutOption;
import org.alfresco.module.vti.metadata.dic.options.RenameOption;

/**
 * @author andreyak
 *
 */
public interface VtiMethodHandler
{
    /**
     * @return String server time zone
     */
    public String getServertimeZone();

    /**
     * @param serviceName specifies the URL of the Web site that a method should act upon
     * @param listHiddenDocs <b>true</b> to list hidden documents in a Web site; otherwise, <b>false</b>
     * @param listExplorerDocs
     *    <pre>
     *         <i>true</i> to generate a list of the task list files (_x_todo.xml and _x_todh.xml).
     *         <i>false</i>, no task list data is sent from the originating server
     *    </pre>
     * @param platform identifies the operating system of the client
     * @param initialURL  URL of the folder from which to initially list documents or, if no folder is given, either "" or "/" to indicate the root folder of the Web site
     * @param listRecurse specifies if recursively list the subfolders of the service_name Web site in the return value
     * @param listLinkInfo specifies whether or not the return value of the method contains information about the links from the current page
     * @param listFolders <i>true</i> to include the names and meta-information of the folders in the service_name Web site; otherwise, <i>false</i>
     * @param listFiles <i>true</i> to list the metadata of files contained in each directory represented in the return code; otherwise, <i>false</i>
     * @param listIncludeParent specifies whether or not return parent directory
     * @param listDerived sending this parameter generates a list of files in <i>_derived</i> folders
     * @param listBorders <i>true</i> to generate a list of contents of the _borders directory that contains shared border pages; otherwise, <i>false</i>
     * @param validateWelcomeNames
     * @param folderList map contains folder URLs as keysr and a time stamp as values corresponding to the time the client computer last posted the list documents method to request a full list of the documents contained in that folder.
     * @param listChildWebs <i>true</i> if folder represents a web site; otherwise, <i>false</i>
     * @return DocsMetaInfo
     * @throws VtiException
     */
    public DocsMetaInfo getListDocuments(String serviceName, boolean listHiddenDocs, boolean listExplorerDocs, String platform, String initialURL, boolean listRecurse,
            boolean listLinkInfo, boolean listFolders, boolean listFiles, boolean listIncludeParent, boolean listDerived, boolean listBorders, boolean validateWelcomeNames,
            Map<String, Object> folderList, boolean listChildWebs) throws VtiException;

    /**
     * @param serviceName specifies the URL of the Web site that a method should act upon
     * @param documentName the site-relative URL of the current document relative to the root directory of the Web site
     * @param force parameter used with source control to undo check-out of a file that is checked out by some other user
     * @param docVersion version number other than the current version of a document
     * @param getOption specifies how documents are checked out from source control. The possible values are:
                    <pre><b>none</b>
                        Do not check out the file.
                    <b>chkoutExclusive</b>
                        Check out the file exclusively, which fails if the file is already checked out by another user.
                    <b>chkoutNonExclusive</b>
                        Check out the file non-exclusively, if the source control system in use is configured to allow non-exclusive check-outs.
                    </pre>
     * @param timeout provides the number of seconds a short-term lock is reserved. Within this time, the client computer must renew its lock to retain the lock
     * @return Document
     */
    public Document getDocument(String serviceName, String documentName, boolean force, String docVersion, EnumSet<GetOption> getOptionSet, int timeout);

    /**
     * @param serviceName specifies the URL of the Web site that a method should act upon
     * @param dir url of the directory to created
     * @return <b>true</b> if directory was created successfully, <b>false</b> otherwise
     */
    public boolean createDirectory(String serviceName, DocMetaInfo dir);

    /**
     * @return user name
     */
    public String getUserName();

    /**
     * @param serviceName specifies the URL of the Web site that a method should act upon
     * @param documentName the site-relative URL of the current document relative to the root directory of the Web site
     * @param force parameter used with source control to undo check-out of a file that is checked out by some other user
     * @param timeout Provides the number of seconds a short-term lock is reserved
     * @param validateWelcomeNames
     * @return DocMetaInfo
     */
    public DocMetaInfo checkOutDocument(String serviceName, String documentName, int force, int timeout, boolean validateWelcomeNames);

    /**
     * checkin document Method
     *
     * @param serviceName specifies the URL of the Web site that a method should act upon
     * @param documentName the site-relative URL of the current document relative to the root directory of the Web site
     * @param comment check-in comments
     * @param keepCheckedOut used when source control is in use. <i>true</i> to check in the specified document to source control and immediately check it back out. <i>false</i> to only check the document in to source control.
     * @param timeCheckedout the time and date at which the current object was last checked out
     * @param validateWelcomeNames
     * @return a DocMetaInfo to source control
     */
    public DocMetaInfo checkInDocument(String serviceName, String documentName, String comment, boolean keepCheckedOut, Date timeCheckedout, boolean validateWelcomeNames);

    /**
     * Undoes a check-out of a file. If the file changed since being checked out, this method causes those changes to be lost
     *
     * @param serviceName specifies the URL of the Web site that a method should act upon
     * @param documentName the site-relative URL of the current document relative to the root directory of the Web site
     * @param force parameter used with source control to undo check-out of a file that is checked out by some other user
     * @param timeCheckedOut The time and date at which the current object was last checked out
     * @param rlsshortterm <i>true</i> if there is a short-term lock on the file; otherwise, <i>false</i>
     * @param validateWelcomeNames
     * @return DocMetaInfo
     */
    public DocMetaInfo uncheckOutDocument(String serviceName, String documentName, boolean force, Date timeCheckedOut, boolean rlsshortterm, boolean validateWelcomeNames);

    /**
     * @param serviceName specifies the URL of the Web site that a method should act upon
     * @param listHiddenDocs <b>true</b> to list hidden documents in a Web site; otherwise, <b>false</b>
     * @param listLinkInfo Specifies whether or not the return value of the method contains information about the links from the current page
     * @param validateWelcomeNames
     * @param urlList the list of site-relative URLs
     * @return
     */
    public DocsMetaInfo getDocsMetaInfo(String serviceName, boolean listHiddenDocs, boolean listLinkInfo, boolean validateWelcomeNames, List<String> urlList);

    /**
     * @param url url to decompose
     * @param alfrescoContext Alfresco Context 
     * @return
     */
    public String[] decomposeURL(String url, String alfrescoContext);

    /**
     * @param uri check if resource exists
     * @return <i>true</i>, if resource exists; otherwise, <i>false</i>
     */
    public boolean existResource(String uri);

    /**
     * Deletes the specified documents or folders from the Web site
     *
     * @param serviceName specifies the URL of the Web site that a method should act upon
     * @param urlList the list of site-relative URLs for the current method
     * @param timeTokens  formerly contained a vector of time stamp values
     * @param validateWelcomeNames
     * @return
     */
    public DocsMetaInfo removeDocuments(String serviceName, List<String> urlList, List<Date> timeTokens, boolean validateWelcomeNames);

    /**
     * Rename the selected document to the new name
     *
     * @param serviceName specifies the URL of the Web site that a method should act upon
     * @param oldURL  the previous URL for a document that has changed names or directory location in the Web site
     * @param newURL the new URL for a document that has changed names or directory locations in the Web site.
     * @param urlList The list of site-relative URLs for the current method
     * @param renameOption parameter that specifies how links should handle to and from the new page.
     * @param putOption enables the server to overwrite an existing file if the value is set to <b>overwrite</b> and disallows overwrites if the value is set to <b>edit</b>
     * @param docopy  <b>true</b> to copy the file to the destination; <b>false</b> to move the file to the destination
     * @param validateWelcomeNames
     * @return DocMetaInfo
     */
    public DocsMetaInfo moveDocument(String serviceName, String oldURL, String newURL, List<String> urlList, EnumSet<RenameOption> renameOptionSet, EnumSet<PutOption> putOptionSet, boolean docopy, boolean validateWelcomeNames);

    /**
     * Writes a file to a directory in an existing Web site
     *
     * @param serviceName specifies the URL of the Web site that a method should act upon
     * @param document document to create
     * @param putOption enables the server to overwrite an existing file if the value is set to <b>overwrite</b> and disallows overwrites if the value is set to <b>edit</b>
     * @param comment provides a comment for the file being uploaded
     * @param keepCheckedOut <i>true</i> to check in the specified document to source control and immediately check it back out. <i>false</i> to only check the document in to source control.
     * @param validateWelcomeNames
     * @return DocMetaInfo
     */
    public DocMetaInfo putDocument(String serviceName, Document document, EnumSet<PutOption> putOptionSet, String comment, boolean keepCheckedOut, boolean validateWelcomeNames);
}
