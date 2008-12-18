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
package org.alfresco.module.vti.handler.alfresco;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.VtiException;
import org.alfresco.module.vti.handler.VtiMethodHandler;
import org.alfresco.module.vti.metadata.DocMetaInfo;
import org.alfresco.module.vti.metadata.DocsMetaInfo;
import org.alfresco.module.vti.metadata.Document;
import org.alfresco.module.vti.metadata.dialog.DialogMetaInfo;
import org.alfresco.module.vti.metadata.dialog.DialogsMetaInfo;
import org.alfresco.module.vti.metadata.dic.VtiError;
import org.alfresco.module.vti.metadata.dic.VtiSort;
import org.alfresco.module.vti.metadata.dic.VtiSortField;
import org.alfresco.module.vti.metadata.dic.options.GetOption;
import org.alfresco.module.vti.metadata.dic.options.PutOption;
import org.alfresco.module.vti.metadata.dic.options.RenameOption;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.repo.site.SiteInfo;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.repo.site.SiteService;
import org.alfresco.repo.version.VersionModel;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockType;
import org.alfresco.service.cmr.lock.NodeLockedException;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.cmr.version.VersionType;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author PavelYur
 *
 */
public class Alfresco3VtiMethodHandler implements VtiMethodHandler
{
    private final static Log logger = LogFactory.getLog(Alfresco3VtiMethodHandler.class);

    private NodeService nodeService;
    private CheckOutCheckInService checkOutCheckInService;
    private FileFolderService fileFolderService;
    private PermissionService permissionService;
    private AuthenticationService authenticationService;
    private VersionService versionService;
    private LockService lockService;
    private ContentService contentService;
    private TransactionService transactionService;
    private SiteService siteService;

    private VtiDocumentHepler documentHelper;
    private VtiPathHelper pathHelper;
    private ShareUtils shareUtils;    

    public void setShareUtils(ShareUtils shareUtils)
    {
        this.shareUtils = shareUtils;
    }

    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }

    public void setLockService(LockService lockService)
    {
        this.lockService = lockService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setCheckOutCheckInService(CheckOutCheckInService checkOutCheckInService)
    {
        this.checkOutCheckInService = checkOutCheckInService;
    }

    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }

    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }

    public void setVersionService(VersionService versionService)
    {
        this.versionService = versionService;
    }
    
    public void setSiteService(SiteService siteService)
    {
        this.siteService = siteService;
    }

    public void setDocumentHelper(VtiDocumentHepler checkoutHelper)
    {
        this.documentHelper = checkoutHelper;
    }

    public void setPathHelper(VtiPathHelper pathHelper)
    {
        this.pathHelper = pathHelper;
    }
    
    public ShareUtils getShareUtils()
    {
        return shareUtils;
    }

    public DocMetaInfo checkInDocument(String serviceName, String documentName, String comment, boolean keepCheckedOut, Date timeCheckedout, boolean validateWelcomeNames)
    {
        // timeCheckedout ignored              
        if (logger.isDebugEnabled())        
        {
            logger.debug("Checkin document: " + documentName + ". Site name: " + serviceName);
        }
        FileInfo fileFileInfo = pathHelper.resolvePathFileInfo(serviceName + "/" + documentName);
        AlfrescoVtiMethodHandler.assertValidFileInfo(fileFileInfo);
        AlfrescoVtiMethodHandler.assertFile(fileFileInfo);
        FileInfo documentFileInfo = fileFileInfo;

        DocumentStatus documentStatus = documentHelper.getDocumentStatus(documentFileInfo.getNodeRef());

        // if document isn't checked out then throw exception
        if (VtiDocumentHepler.isCheckedout(documentStatus) == false)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Document is not checked out.");
            }
            throw VtiException.create(VtiError.V_DOC_NOT_CHECKED_OUT);
        }

        // if document is checked out, but user isn't owner, then throw exception
        if (VtiDocumentHepler.isCheckoutOwner(documentStatus) == false)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Unable to perform check in. Not an owner!!!");
            }
            throw VtiException.create(VtiError.V_DOC_CHECKED_OUT);
        }

        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();

            if (VtiDocumentHepler.isLongCheckedout(documentStatus))
            {
                // long-term checkout
                Map<String, Serializable> props = new HashMap<String, Serializable>(1, 1.0f);
                props.put(Version.PROP_DESCRIPTION, comment);
                props.put(VersionModel.PROP_VERSION_TYPE, VersionType.MAJOR);

                NodeRef resultNodeRef = checkOutCheckInService.checkin(checkOutCheckInService.getWorkingCopy(documentFileInfo.getNodeRef()), props, null, keepCheckedOut);

                documentFileInfo = fileFolderService.getFileInfo(resultNodeRef);
            }
            else
            {
                // short-term checkout
                lockService.unlock(documentFileInfo.getNodeRef());
                documentFileInfo = fileFolderService.getFileInfo(documentFileInfo.getNodeRef());
            }

            tx.commit();
            if (logger.isDebugEnabled())
            {
                logger.debug("Document successfully checked in.");
            }
        }
        catch (Throwable e)
        {
            try
            {
                tx.rollback();
            }
            catch (Exception tex) {}
            throw VtiExceptionUtils.createRuntimeException(e);
        }

        DocMetaInfo docMetaInfo = new DocMetaInfo(false);
        docMetaInfo.setPath(documentName);
        setDocMetaInfo(documentFileInfo, docMetaInfo);

        return docMetaInfo;
    }

    public DocMetaInfo checkOutDocument(String serviceName, String documentName, int force, int timeout, boolean validateWelcomeNames)
    {        
        if (logger.isDebugEnabled())        
        {
            logger.debug("Checkout document: " + documentName + ". Site name: " + serviceName);
        }
        FileInfo fileFileInfo = pathHelper.resolvePathFileInfo(serviceName + "/" + documentName);
        AlfrescoVtiMethodHandler.assertValidFileInfo(fileFileInfo);
        AlfrescoVtiMethodHandler.assertFile(fileFileInfo);
        FileInfo documentFileInfo = fileFileInfo;

        checkout(documentFileInfo, timeout);

        documentFileInfo = fileFolderService.getFileInfo(documentFileInfo.getNodeRef());
        DocMetaInfo docMetaInfo = new DocMetaInfo(false);
        setDocMetaInfo(documentFileInfo, docMetaInfo);
        docMetaInfo.setPath(pathHelper.toUrlPath(documentFileInfo));

        return docMetaInfo;
    }

    public boolean createDirectory(String serviceName, DocMetaInfo dir)
    {       
        if (logger.isDebugEnabled())
        {
            logger.debug("Creating directory: '" + dir.getPath() + "' in site: " + serviceName);
        }
        Pair<String, String> parentChildPaths = VtiPathHelper.splitPathParentChild(serviceName + "/" + dir.getPath());

        String parentName = parentChildPaths.getFirst();
        String childFolderName = parentChildPaths.getSecond();

        if (childFolderName.length() == 0)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Invalid name for new directory. Name should not be empty.");
            }
            throw VtiException.create(VtiError.V_BAD_URL);
        }

        FileInfo parentFileInfo = pathHelper.resolvePathFileInfo(parentName);
        if (parentFileInfo == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Parent folder not exists.");
            }
            throw VtiException.create(VtiError.PRIMARY_PARENT_NOT_EXIST);
        }

        AlfrescoVtiMethodHandler.assertFolder(parentFileInfo);

        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();

            fileFolderService.create(parentFileInfo.getNodeRef(), childFolderName, ContentModel.TYPE_FOLDER);

            tx.commit();
            
            if (logger.isDebugEnabled())
            {
                logger.debug("Folder successfully was created.");
            }
        }
        catch (Throwable e)
        {
            try
            {
                tx.rollback();
            }
            catch (Exception tex) {}

            if (e instanceof FileExistsException)
            {
                throw VtiException.create(e, VtiError.FOLDER_ALREADY_EXISTS);
            }

            throw VtiExceptionUtils.createRuntimeException(e);
        }

        return true;
    }

    public String[] decomposeURL(String url, String alfrescoContext)
    {
        if (!url.startsWith(alfrescoContext))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Url must start with alfresco context.");
            }
            throw VtiException.create(VtiError.V_BAD_URL);
        }
        
        if (url.equalsIgnoreCase(alfrescoContext))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("WebUrl: " + alfrescoContext + ", fileUrl: ''");
            }
            return new String[]{alfrescoContext, ""};
        }
        
        if (url.startsWith(alfrescoContext + "/history"))
        {
            return new String[]{alfrescoContext, url.substring(alfrescoContext.length() + 1)};
        }
        
        String webUrl = "";
        String fileUrl = "";        
        
        String[] splitPath = url.replaceAll(alfrescoContext, "").substring(1).split("/");
        
        StringBuilder tempWebUrl = new StringBuilder();
        
        for (int i = splitPath.length; i > 0; i--)
        {
            tempWebUrl.delete(0, tempWebUrl.length());
            
            for (int j = 0; j < i; j++)
            {
                if ( j < i-1)
                {
                    tempWebUrl.append(splitPath[j] + "/");
                }
                else
                {
                    tempWebUrl.append(splitPath[j]);
                }
            }            
            
            FileInfo fileInfo = pathHelper.resolvePathFileInfo(tempWebUrl.toString());
            
            if (fileInfo != null)
            {
                if (nodeService.getType(fileInfo.getNodeRef()).equals(SiteModel.TYPE_SITE))
                {
                    webUrl = alfrescoContext + "/" + tempWebUrl;
                    if (url.replaceAll(webUrl, "").startsWith("/"))
                    {
                        fileUrl = url.replaceAll(webUrl, "").substring(1);
                    }
                    else
                    {
                        fileUrl = url.replaceAll(webUrl, "");                        
                    }
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("WebUrl: " + webUrl + ", fileUrl: '" + fileUrl + "'");
                    }
                    return new String[]{webUrl, fileUrl};
                }
            }
        }
        if (webUrl.equals(""))
        {
            throw VtiException.create(VtiError.V_BAD_URL);
        }
        return new String[]{webUrl, fileUrl};
    }

    public boolean existResource(String uri)
    {
        return pathHelper.resolvePathFileInfo(uri) != null;
    }

    public DocsMetaInfo getDocsMetaInfo(String serviceName, boolean listHiddenDocs, boolean listLinkInfo, boolean validateWelcomeNames, List<String> urlList)
    {
        DocsMetaInfo docsMetaInfo = new DocsMetaInfo();

        if (urlList.isEmpty())
        {
            urlList.add("");
        }

        for (String url : urlList)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Retrieving meta-info for document: '" + url + "' from site: " + serviceName);
            }
            FileInfo fileInfo = pathHelper.resolvePathFileInfo(serviceName + "/" + url);

            if (fileInfo != null && fileInfo.isLink() == false)
            {
                DocMetaInfo docMetaInfo = new DocMetaInfo(fileInfo.isFolder());
                setDocMetaInfo(fileInfo, docMetaInfo);
                docMetaInfo.setPath(url);

                if (fileInfo.isFolder())
                {
                    docsMetaInfo.getFolderMetaInfoList().add(docMetaInfo);
                }
                else
                {
                    docsMetaInfo.getFileMetaInfoList().add(docMetaInfo);
                }
            }
            else
            {
                DocMetaInfo docMetaInfo = new DocMetaInfo(false);
                docMetaInfo.setPath(url);
                docsMetaInfo.getFailedUrls().add(docMetaInfo);
            }
        }

        return docsMetaInfo;
    }

    public Document getDocument(String serviceName, String documentName, boolean force, String docVersion, EnumSet<GetOption> getOptionSet, int timeout)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieving of document: '" + documentName + "' from site: " +serviceName);
        }
        FileInfo fileFileInfo = pathHelper.resolvePathFileInfo(serviceName + "/" + documentName);
        AlfrescoVtiMethodHandler.assertValidFileInfo(fileFileInfo);
        AlfrescoVtiMethodHandler.assertFile(fileFileInfo);
        FileInfo documentFileInfo = fileFileInfo;

        if (getOptionSet.contains(GetOption.none))
        {
            if (docVersion.length() > 0)
            {
                try
                {
                    VersionHistory versionHistory = versionService.getVersionHistory(documentFileInfo.getNodeRef());
                    Version version = versionHistory.getVersion(VtiUtils.toAlfrescoVersionLabel(docVersion));
                    NodeRef versionNodeRef = version.getFrozenStateNodeRef();

                    documentFileInfo = fileFolderService.getFileInfo(versionNodeRef);
                }
                catch (AccessDeniedException e)
                {
                    throw e;
                }
                catch (RuntimeException e)
                {
                    if (logger.isWarnEnabled())
                    {
                        logger.warn("Version '" + docVersion + "' retrieving exception", e);
                    }

                    // suppress all exceptions and returns the most recent version of the document
                }
            }
        }
        else if (getOptionSet.contains(GetOption.chkoutExclusive) || getOptionSet.contains(GetOption.chkoutNonExclusive))
        {
            try
            {
                // ignore version string parameter
                documentFileInfo = checkout(documentFileInfo, timeout);
            }
            catch (AccessDeniedException e)
            {
                // open document in read-only mode without cheking out (in case if user open content of other user)
                Document document = new Document();
                document.setPath(documentName);                
                ContentReader contentReader = fileFolderService.getReader(documentFileInfo.getNodeRef());                
                document.setInputStream(contentReader.getContentInputStream());                
                return document;
            }
        }

        Document document = new Document();
        document.setPath(documentName);
        setDocMetaInfo(documentFileInfo, document);
        ContentReader contentReader = fileFolderService.getReader(documentFileInfo.getNodeRef());
        if (contentReader != null)
        {
            document.setInputStream(contentReader.getContentInputStream());
        }
        else
        {
            // commons-io 1.1 haven't ClosedInputStream
            document.setInputStream(new ByteArrayInputStream(new byte[0]));
        }

        return document;
    }

    public DialogsMetaInfo getFileOpen(String siteUrl, String location, List<String> fileDialogFilterValue, String rootFolder, VtiSortField sortField, VtiSort sortDir, String view)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Generating list of items for site: '" + siteUrl + "' and location: " + location);
        }
        FileInfo folderFileInfo;
        folderFileInfo = pathHelper.resolvePathFileInfo(siteUrl + "/" + location);
        
        AlfrescoVtiMethodHandler.assertValidFileInfo(folderFileInfo);
        AlfrescoVtiMethodHandler.assertFolder(folderFileInfo);
        FileInfo sourceFileInfo = folderFileInfo;

        DialogsMetaInfo result = new DialogsMetaInfo();        
        
        for (FileInfo fileInfo : fileFolderService.list(sourceFileInfo.getNodeRef()))
        {      
            if (fileInfo.isFolder())
            {
                result.getDialogMetaInfoList().add(getDialogMetaInfo(fileInfo));
            }
            else if (nodeService.hasAspect(fileInfo.getNodeRef(), ContentModel.ASPECT_WORKING_COPY) == false
                    && VtiDocumentHepler.applyFilters(fileInfo.getName(), fileDialogFilterValue))
            {
                result.getDialogMetaInfoList().add(getDialogMetaInfo(fileInfo));
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieved " + result.getDialogMetaInfoList().size() + " items");
        }
        return result;
    }

    public DocsMetaInfo getListDocuments(String serviceName, boolean listHiddenDocs, boolean listExplorerDocs, String platform, String initialURL, boolean listRecurse,
            boolean listLinkInfo, boolean listFolders, boolean listFiles, boolean listIncludeParent, boolean listDerived, boolean listBorders, boolean validateWelcomeNames,
            Map<String, Object> folderList, boolean listChildWebs) throws VtiException
    {     
        // listHiddenDocs ignored
        // listExplorerDocs ignored
        
        DocsMetaInfo result = new DocsMetaInfo();
        
        FileInfo folderFileInfo = pathHelper.resolvePathFileInfo(serviceName + "/" + initialURL);
        AlfrescoVtiMethodHandler.assertValidFileInfo(folderFileInfo);
        AlfrescoVtiMethodHandler.assertFolder(folderFileInfo);
        FileInfo sourceFileInfo = folderFileInfo;

        // show the list of sites that user is member
        if (serviceName.equals(""))
        {          
            if (logger.isDebugEnabled())
            {
                logger.debug("Generating the list of sites the user is member of.");
            }
            // gets the list of sites that user is member
            List<SiteInfo> sites = siteService.listSites(authenticationService.getCurrentUserName());
            for (SiteInfo site : sites)
            {
                FileInfo siteFileInfo = fileFolderService.getFileInfo(site.getNodeRef());
                
                result.getFolderMetaInfoList().add(buildDocMetaInfo(siteFileInfo, folderList));                
            }       
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Generating list of items under site: " + serviceName + " and initialURL: " + initialURL);
            }
            // we are already in site (lists files and/or folders) 
            if (listFolders)
            {
                for (FileInfo folder : fileFolderService.listFolders(sourceFileInfo.getNodeRef()))
                {
                    result.getFolderMetaInfoList().add(buildDocMetaInfo(folder, folderList));
                }
            }

            if (listFiles)
            {
                for (FileInfo file : fileFolderService.listFiles(sourceFileInfo.getNodeRef()))
                {
                    if (file.isLink() == false)
                    {
                        if (nodeService.hasAspect(file.getNodeRef(), ContentModel.ASPECT_WORKING_COPY) == false)
                        {
                            result.getFileMetaInfoList().add(buildDocMetaInfo(file, folderList));
                        }
                    }
                }
            }            
        }        

        if (listIncludeParent)
        {
            result.getFolderMetaInfoList().add(buildDocMetaInfo(sourceFileInfo, folderList));
        }

        return result;
    }

    public String getServertimeZone()
    {
        return new SimpleDateFormat("Z").format(new Date());
    }

    public String getUserName()
    {
        return authenticationService.getCurrentUserName();
    }

    public DocsMetaInfo moveDocument(String serviceName, String oldURL, String newURL, List<String> urlList, EnumSet<RenameOption> renameOptionSet,
            EnumSet<PutOption> putOptionSet, boolean docopy, boolean validateWelcomeNames)
    {     
        // urlList ignored
        // validateWelcomeNames ignored       
        

        FileInfo sourceFileInfo = pathHelper.resolvePathFileInfo(serviceName + "/" + oldURL);

        AlfrescoVtiMethodHandler.assertValidFileInfo(sourceFileInfo);

        if (docopy == false)
        {
            if (sourceFileInfo.isFolder() == false)
            {
                AlfrescoVtiMethodHandler.assertRemovableDocument(documentHelper.getDocumentStatus(sourceFileInfo.getNodeRef()));
            }
        }
        
        Pair<String, String> parentChildPaths = VtiPathHelper.splitPathParentChild(serviceName + "/" + newURL);
        String destName = parentChildPaths.getSecond();
        if (destName.length() == 0)
        {
            throw VtiException.create(VtiError.V_BAD_URL);
        }

        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();

            //  determining existence of parent folder for newURL
            String parentPath = parentChildPaths.getFirst();
            FileInfo destParentFolder = pathHelper.resolvePathFileInfo(parentPath);
            if (destParentFolder == null)
            {
                // if "createdir" option presents then create only primary parent of new location
                if (putOptionSet.contains(PutOption.createdir) || renameOptionSet.contains(RenameOption.createdir))
                {
                    destParentFolder = createOnlyLastFolder(parentPath);
                }

                if (destParentFolder == null)
                {
                    throw VtiException.create(VtiError.PRIMARY_PARENT_NOT_EXIST);
                }
            }

            // determining existence of folder or file with newURL
            FileInfo destFileInfo = pathHelper.resolvePathFileInfo(destParentFolder, destName);
            if (destFileInfo != null)
            {
                // if "overwrite" option presents then overwrite existing file or folder
                if (putOptionSet.contains(PutOption.overwrite))
                {
                    if (destFileInfo.isFolder() == false)
                    {
                        DocumentStatus destDocumentStatus = documentHelper.getDocumentStatus(destFileInfo.getNodeRef());
                        AlfrescoVtiMethodHandler.assertRemovableDocument(destDocumentStatus);

                        // if destination document is long-term checked out then delete working copy
                        if (destDocumentStatus.equals(DocumentStatus.LONG_CHECKOUT_OWNER))
                        {
                            NodeRef workingCopyNodeRef = checkOutCheckInService.getWorkingCopy(destFileInfo.getNodeRef());
                            fileFolderService.delete(workingCopyNodeRef); // beforeDeleteNode policy unlocks original node
                        }
                    }

                    fileFolderService.delete(destFileInfo.getNodeRef());
                }
                else
                {
                    throw VtiException.create(VtiError.FILE_ALREADY_EXISTS);
                }
            }

            if (docopy)
            {

                if (logger.isDebugEnabled())
                {
                    logger.debug("Copy document: " + oldURL + " to new location: " + newURL + " in site: " + serviceName);
                }
                destFileInfo = fileFolderService.copy(sourceFileInfo.getNodeRef(), destParentFolder.getNodeRef(), destName);
            }
            else
            {
                if (sourceFileInfo.isFolder() == false)
                {
                    DocumentStatus sourceDocumentStatus = documentHelper.getDocumentStatus(sourceFileInfo.getNodeRef());

                    // if source document is long-term checked out then delete working copy
                    if (sourceDocumentStatus.equals(DocumentStatus.LONG_CHECKOUT_OWNER))
                    {
                        NodeRef workingCopyNodeRef = checkOutCheckInService.getWorkingCopy(sourceFileInfo.getNodeRef());
                        fileFolderService.delete(workingCopyNodeRef); // beforeDeleteNode policy unlocks original node
                    }
                }

                if (logger.isDebugEnabled())
                {
                    logger.debug("Move document: " + oldURL + " to new location: " + newURL + " in site: " + serviceName);
                }
                destFileInfo = fileFolderService.move(sourceFileInfo.getNodeRef(), destParentFolder.getNodeRef(), destName);
            }

            tx.commit();

            DocMetaInfo docMetaInfo = new DocMetaInfo(destFileInfo.isFolder());
            docMetaInfo.setPath(newURL);
            setDocMetaInfo(destFileInfo, docMetaInfo);

            DocsMetaInfo result = new DocsMetaInfo();

            if (destFileInfo.isFolder())
            {
                result.getFolderMetaInfoList().add(docMetaInfo);
                addFileFoldersRecursive(destFileInfo, result);
            }
            else
            {
                result.getFileMetaInfoList().add(docMetaInfo);
            }

            return result;
        }
        catch (Throwable e)
        {
            try
            {
                tx.rollback();
            }
            catch (Exception tex) {}

            if (e instanceof FileNotFoundException)
            {
                throw VtiException.create(e, VtiError.V_BAD_URL);
            }

            if (e instanceof NodeLockedException)
            {
                // only if source or destination folder is locked
                throw VtiException.create(e, VtiError.V_REMOVE_DIRECTORY);
            }

            throw VtiExceptionUtils.createRuntimeException(e);
        }
    }

    public DocMetaInfo putDocument(String serviceName, Document document, EnumSet<PutOption> putOptionSet, String comment, boolean keepCheckedOut, boolean validateWelcomeNames)
    {     
        // keepCheckedOut ignored
        // validateWelcomeNames

        // 'atomic' put-option              : ignored
        // 'checkin' put-option             : ignored
        // 'checkout' put-option            : ignored
        // 'createdir' put-option           : implemented
        // 'edit' put-option                : implemented
        // 'forceversions' put-option       : ignored
        // 'migrationsemantics' put-option  : ignored
        // 'noadd' put-option               : ignored
        // 'overwrite' put-option           : implemented
        // 'thicket' put-option             : ignored
             
        if (logger.isDebugEnabled())
        {
            logger.debug("Saving document: '" + document.getPath() + "' to the site: " + serviceName);
        }
        Pair<String, String> parentChildPaths = VtiPathHelper.splitPathParentChild(serviceName + "/" + document.getPath());
        String documentName = parentChildPaths.getSecond();
        if (documentName.length() == 0)
        {
            throw VtiException.create(VtiError.V_BAD_URL);
        }

        FileInfo curDocumentFileInfo; // file info for document for put_document method

        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();

            String parentPath = parentChildPaths.getFirst();
            FileInfo parentFileInfo = pathHelper.resolvePathFileInfo(parentPath);
            if (parentFileInfo == null)
            {
                if (putOptionSet.contains(PutOption.createdir))
                {
                    parentFileInfo = createOnlyLastFolder(parentPath);
                }

                if (parentFileInfo == null)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("The folder where file should be placed not exists.");
                    }
                    throw VtiException.create(VtiError.PRIMARY_PARENT_NOT_EXIST);
                }
            }

            DocumentStatus documentStatus = DocumentStatus.NORMAL; // default status for new document

            curDocumentFileInfo = pathHelper.resolvePathFileInfo(parentFileInfo, documentName);
            if (curDocumentFileInfo != null)
            {
                documentStatus = documentHelper.getDocumentStatus(curDocumentFileInfo.getNodeRef());

                if (documentStatus.equals(DocumentStatus.READONLY))
                {
                    // document is readonly
                    throw VtiException.create(VtiError.V_FILE_OPEN_FOR_WRITE);
                }

                if (VtiDocumentHepler.isCheckedout(documentStatus) && VtiDocumentHepler.isCheckoutOwner(documentStatus) == false)
                {
                    // document already checked out by another user
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Document is checked out by another user");
                    }
                    throw VtiException.create(VtiError.V_DOC_CHECKED_OUT);
                }

                if (VtiDocumentHepler.isLongCheckedout(documentStatus))
                {
                    NodeRef workingCopyNodeRef = checkOutCheckInService.getWorkingCopy(curDocumentFileInfo.getNodeRef());
                    curDocumentFileInfo = fileFolderService.getFileInfo(workingCopyNodeRef);
                }

                if ((putOptionSet.contains(PutOption.overwrite) == false && putOptionSet.contains(PutOption.edit) == false) ||
                        (putOptionSet.contains(PutOption.edit) && VtiUtils.compare(curDocumentFileInfo.getModifiedDate(), document.getTimelastmodified()) == false))
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("ModifiedDate for document on server = '" + VtiUtils.formatDate(curDocumentFileInfo.getModifiedDate()) + "', " +
                                "ModifiedDate for client document = '" + document.getTimelastmodified() + "'");
                    }

                    throw VtiException.create(VtiError.FILE_ALREADY_EXISTS);
                }
            }
            else
            {
                curDocumentFileInfo = fileFolderService.create(parentFileInfo.getNodeRef(), documentName, ContentModel.TYPE_CONTENT);
            }

            NodeRef curDocumentNodeRef = curDocumentFileInfo.getNodeRef();
            
            if (nodeService.hasAspect(curDocumentNodeRef, ContentModel.ASPECT_VERSIONABLE) == false)
            {
                nodeService.addAspect(curDocumentNodeRef, ContentModel.ASPECT_VERSIONABLE, null);                
            }

            if (nodeService.hasAspect(curDocumentNodeRef, ContentModel.ASPECT_AUTHOR) == false)
            {
                nodeService.addAspect(curDocumentNodeRef, ContentModel.ASPECT_AUTHOR, null);
            }
            nodeService.setProperty(curDocumentNodeRef, ContentModel.PROP_AUTHOR, authenticationService.getCurrentUserName());

            ContentWriter writer = contentService.getWriter(curDocumentNodeRef, ContentModel.PROP_CONTENT, true);
            writer.putContent(document.getInputStream());

            tx.commit();
        }
        catch (Throwable e)
        {
            try
            {
                // set the inputStream in null for correct answer from server
                document.setInputStream(null);
                tx.rollback();
            }
            catch (Exception tex) {}

            throw VtiExceptionUtils.createRuntimeException(e);
        }

        // refresh file info for new document
        curDocumentFileInfo = fileFolderService.getFileInfo(curDocumentFileInfo.getNodeRef());

        DocMetaInfo result = new DocMetaInfo(false);
        result.setPath(document.getPath());
        setDocMetaInfo(curDocumentFileInfo, result);

        return result;
    }

    public DocsMetaInfo removeDocuments(String serviceName, List<String> urlList, List<Date> timeTokens, boolean validateWelcomeNames)
    {     
        // timeTokens ignored
        // validateWelcomeNames ignored            
        
        DocsMetaInfo docsMetaInfo = new DocsMetaInfo();

        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();

            for (String url : urlList)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Removing item: '" + url + "' from site: " + serviceName);
                }
                FileInfo fileInfo = pathHelper.resolvePathFileInfo(serviceName + "/" + url);
                AlfrescoVtiMethodHandler.assertValidFileInfo(fileInfo);

                DocMetaInfo docMetaInfo = new DocMetaInfo(fileInfo.isFolder());
                docMetaInfo.setPath(url);
                setDocMetaInfo(fileInfo, docMetaInfo);

                if (fileInfo.isFolder())
                {
                    // add nested files and folders to meta info list
                    addFileFoldersRecursive(fileInfo, docsMetaInfo);
                    docsMetaInfo.getFolderMetaInfoList().add(docMetaInfo);
                }
                else
                {
                    DocumentStatus documentStatus = documentHelper.getDocumentStatus(fileInfo.getNodeRef());
                    AlfrescoVtiMethodHandler.assertRemovableDocument(documentStatus);

                    if (documentStatus.equals(DocumentStatus.LONG_CHECKOUT_OWNER))
                    {
                        NodeRef workingCopyNodeRef = checkOutCheckInService.getWorkingCopy(fileInfo.getNodeRef());
                        fileFolderService.delete(workingCopyNodeRef); // beforeDeletePolicy unlocks original document
                    }

                    docsMetaInfo.getFileMetaInfoList().add(docMetaInfo);
                }

                fileFolderService.delete(fileInfo.getNodeRef());
            }

            tx.commit();
        }
        catch (Throwable e)
        {
            try
            {
                tx.rollback();
            }
            catch (Exception tex) {}

            throw VtiExceptionUtils.createRuntimeException(e);
        }

        return docsMetaInfo;
    }

    public DocMetaInfo uncheckOutDocument(String serviceName, String documentName, boolean force, Date timeCheckedOut, boolean rlsshortterm, boolean validateWelcomeNames)
    {
        // force ignored
        // timeCheckedOut ignored

        if (logger.isDebugEnabled())
        {
            logger.debug("Unchecked out document: '" + documentName + "' from site: " + serviceName);
        }
        FileInfo fileFileInfo = pathHelper.resolvePathFileInfo(serviceName + "/" + documentName);
        AlfrescoVtiMethodHandler.assertValidFileInfo(fileFileInfo);
        AlfrescoVtiMethodHandler.assertFile(fileFileInfo);
        FileInfo documentFileInfo = fileFileInfo;

        DocumentStatus documentStatus = documentHelper.getDocumentStatus(documentFileInfo.getNodeRef());

        // if document isn't checked out then throw exception
        if (VtiDocumentHepler.isCheckedout(documentStatus) == false)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Document not already checked out!!!");
            }
            throw VtiException.create(VtiError.V_DOC_NOT_CHECKED_OUT);
        }

        // if document is checked out, but user isn't owner, then throw exception
        if (VtiDocumentHepler.isCheckoutOwner(documentStatus) == false)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Not an owner!!!");
            }
            throw VtiException.create(VtiError.V_DOC_CHECKED_OUT);
        }

        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();

            if (rlsshortterm)
            {
                // try to release short-term checkout
                // if user have long-term checkout then skip releasing short-term checkout
                if (documentStatus.equals(DocumentStatus.LONG_CHECKOUT_OWNER) == false)
                {
                    lockService.unlock(documentFileInfo.getNodeRef());
                }
            }
            else
            {
                // try to cancel long-term checkout
                NodeRef workingCopyNodeRef = checkOutCheckInService.getWorkingCopy(documentFileInfo.getNodeRef());
                checkOutCheckInService.cancelCheckout(workingCopyNodeRef);
            }

            tx.commit();
        }
        catch (Throwable e)
        {
            try
            {
                tx.rollback();
            }
            catch (Exception tex) {}

            if ((e instanceof VtiException) == false)
            {
                throw VtiException.create(e, VtiError.V_DOC_NOT_CHECKED_OUT);
            }

            throw VtiExceptionUtils.createRuntimeException(e);
        }

        // refresh file info for current document
        documentFileInfo = fileFolderService.getFileInfo(documentFileInfo.getNodeRef());

        DocMetaInfo docMetaInfo = new DocMetaInfo(false);
        docMetaInfo.setPath(documentName);
        setDocMetaInfo(documentFileInfo, docMetaInfo);

        return docMetaInfo;
    }
    
    /**
     * Build DocMetaInfo for getListDocuments method
     *
     * @param fileInfo file info
     * @param folderList dates list
     * @return builded DocMetaInfo
     */
    private DocMetaInfo buildDocMetaInfo(FileInfo fileInfo, Map<String, Object> folderList)
    {
        boolean isModified = false;

        String path = pathHelper.toUrlPath(fileInfo);

        Date cacheDate = (Date) folderList.get(path);
        Date srcDate = fileInfo.getModifiedDate();

        if (cacheDate == null || srcDate.after(cacheDate))
        {
            isModified = true;
        }

        DocMetaInfo docMetaInfo = new DocMetaInfo(fileInfo.isFolder());

        if (isModified)
        {
            setDocMetaInfo(fileInfo, docMetaInfo);
        }

        docMetaInfo.setPath(path);
        
        if (nodeService.getType(fileInfo.getNodeRef()).equals(SiteModel.TYPE_SITE))
        {
            docMetaInfo.setIschildweb("true");
        }

        return docMetaInfo;
    }
    
    /**
     * Sets metadata for docMetaInfo
     *
     * @param fileInfo file info for document, folder or working copy
     * @param docMetaInfo meta info
     */
    private void setDocMetaInfo(FileInfo fileInfo, DocMetaInfo docMetaInfo)
    {
        if (fileInfo.isFolder())
        {
            NodeRef folderNodeRef = fileInfo.getNodeRef();

            docMetaInfo.setTimecreated(VtiUtils.formatDate(fileInfo.getCreatedDate()));
            String modifiedDate = VtiUtils.formatDate(fileInfo.getModifiedDate());
            docMetaInfo.setTimelastmodified(modifiedDate);
            docMetaInfo.setTimelastwritten(modifiedDate);

            boolean isBrowsable = permissionService.hasPermission(folderNodeRef, PermissionService.READ_CHILDREN).equals(AccessStatus.ALLOWED);
            if (isBrowsable)
            {
                docMetaInfo.setHassubdirs(String.valueOf(fileFolderService.listFolders(folderNodeRef).isEmpty() == false));
            }
            docMetaInfo.setIsbrowsable(String.valueOf(isBrowsable));

            docMetaInfo.setIsexecutable(Boolean.FALSE.toString());
            docMetaInfo.setIsscriptable(Boolean.FALSE.toString());
        }
        else
        {
            FileInfo originalFileInfo = null;
            FileInfo workingCopyFileInfo = null;

            boolean isLongCheckedout = false;
            boolean isShortCheckedout = false;

            Map<QName, Serializable> originalProps = null;
            Map<QName, Serializable> workingCopyProps = null;

            if (nodeService.hasAspect(fileInfo.getNodeRef(), ContentModel.ASPECT_WORKING_COPY))
            {
                // we have working copy
                workingCopyFileInfo = fileInfo;
                workingCopyProps = workingCopyFileInfo.getProperties();

                originalFileInfo = fileFolderService.getFileInfo(documentHelper.getOriginalNodeRef(workingCopyFileInfo.getNodeRef()));

                isLongCheckedout = true;
            }
            else
            {
                // we have original document
                originalFileInfo = fileInfo;

                DocumentStatus documentStatus = documentHelper.getDocumentStatus(originalFileInfo.getNodeRef());
                isLongCheckedout = VtiDocumentHepler.isLongCheckedout(documentStatus);
                isShortCheckedout = VtiDocumentHepler.isShortCheckedout(documentStatus);

                if (isLongCheckedout)
                {
                    // retrieves file info and props for working copy
                    NodeRef workingCopyNodeRef = checkOutCheckInService.getWorkingCopy(originalFileInfo.getNodeRef());
                    workingCopyFileInfo = fileFolderService.getFileInfo(workingCopyNodeRef);
                    workingCopyProps = workingCopyFileInfo.getProperties();
                }
            }

            originalProps = originalFileInfo.getProperties();

            docMetaInfo.setTimecreated(VtiUtils.formatDate(originalFileInfo.getCreatedDate()));
            if (isLongCheckedout)
            {
                String modifiedDate = VtiUtils.formatDate(workingCopyFileInfo.getModifiedDate());
                docMetaInfo.setTimelastmodified(modifiedDate);
                docMetaInfo.setTimelastwritten(modifiedDate);

                docMetaInfo.setFilesize(String.valueOf(workingCopyFileInfo.getContentData().getSize()));

                docMetaInfo.setSourcecontrolcheckedoutby((String) workingCopyProps.get(ContentModel.PROP_WORKING_COPY_OWNER));
                docMetaInfo.setSourcecontroltimecheckedout(VtiUtils.formatDate((Date) workingCopyProps.get(ContentModel.PROP_CREATED)));
            }
            else
            {
                String modifiedDate = VtiUtils.formatDate(originalFileInfo.getModifiedDate());
                docMetaInfo.setTimelastmodified(modifiedDate);
                docMetaInfo.setTimelastwritten(modifiedDate);

                docMetaInfo.setFilesize(String.valueOf(originalFileInfo.getContentData().getSize()));

                if (isShortCheckedout)
                {
                    docMetaInfo.setSourcecontroltimecheckedout(VtiUtils.formatDate(new Date()));
                    docMetaInfo.setSourcecontrolcheckedoutby((String) originalProps.get(ContentModel.PROP_LOCK_OWNER));
                    if (originalProps.get(ContentModel.PROP_EXPIRY_DATE) != null)
                    {
                        docMetaInfo.setSourcecontrollockexpires(VtiUtils.formatDate((Date) originalProps.get(ContentModel.PROP_EXPIRY_DATE)));
                    }
                    else
                    {
                        // we have infinite lock
                        // SharePoint doesn't support locks without expiry date
                        // sets expiry date on 10000 years in future
                        Date expiryDate = new Date();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(expiryDate);
                        calendar.add(Calendar.YEAR, 10000);
                        expiryDate = calendar.getTime();
                        docMetaInfo.setSourcecontrollockexpires(VtiUtils.formatDate(expiryDate));
                    }
                }
            }

            docMetaInfo.setTitle((String) originalProps.get(ContentModel.PROP_TITLE));
            docMetaInfo.setAuthor((String) originalProps.get(ContentModel.PROP_CREATOR));
            docMetaInfo.setModifiedBy((String) originalProps.get(ContentModel.PROP_MODIFIER));

            Version currentVersion = versionService.getCurrentVersion(originalFileInfo.getNodeRef());
            if (currentVersion != null)
            {
                docMetaInfo.setSourcecontrolversion(currentVersion.getVersionLabel());
            }
            else
            {
                // this document isn't versionable, but SharePoint supports versionable libraries, not documents
                // so send first version for document
                docMetaInfo.setSourcecontrolversion("1.0");
            }
        }
    }
    /**
     * Returns DialogMetaInfo for FileInfo
     *
     * @param fileInfo file info
     * @return dialog meta info
     */
    private DialogMetaInfo getDialogMetaInfo(FileInfo fileInfo)
    {
        DialogMetaInfo dialogMetaInfo = new DialogMetaInfo(fileInfo.isFolder());
        dialogMetaInfo.setPath(pathHelper.toUrlPath(fileInfo));
        dialogMetaInfo.setName(fileInfo.getName());
        dialogMetaInfo.setModifiedBy((String) fileInfo.getProperties().get(ContentModel.PROP_MODIFIER));

        if (fileInfo.isFolder() == false)
        {
            DocumentStatus documentStatus = documentHelper.getDocumentStatus(fileInfo.getNodeRef());

            if (VtiDocumentHepler.isLongCheckedout(documentStatus))
            {
                NodeRef workingCopyNodeRef = checkOutCheckInService.getWorkingCopy(fileInfo.getNodeRef());
                FileInfo workingCopyFileInfo = fileFolderService.getFileInfo(workingCopyNodeRef);
                dialogMetaInfo.setModifiedTime(VtiUtils.formatVersionDate(workingCopyFileInfo.getModifiedDate()));
                dialogMetaInfo.setCheckedOutTo((String) nodeService.getProperty(workingCopyNodeRef, ContentModel.PROP_WORKING_COPY_OWNER));
            }
            else
            {
                dialogMetaInfo.setModifiedTime(VtiUtils.formatVersionDate(fileInfo.getModifiedDate()));
            }
        }
        else
        {
            dialogMetaInfo.setModifiedTime(VtiUtils.formatVersionDate(fileInfo.getModifiedDate()));
        }
        
        return dialogMetaInfo;
    }    
    
    /**
     * Helper method for short-term or long-term checkouts
     *
     * @param documentFileInfo file info for document
     * @param timeout timeout in minutes for short-term checkout, if equals 0, then uses long-term checkout
     * @return checked out document file info
     */
    private FileInfo checkout(FileInfo documentFileInfo, int timeout)
    {
        DocumentStatus documentStatus = documentHelper.getDocumentStatus(documentFileInfo.getNodeRef());

        if (documentStatus.equals(DocumentStatus.READONLY))
        {
            // document is readonly
            if (logger.isDebugEnabled())
            {
                logger.debug("Unable to perform checked out operation!!! Document is read only.");
            }
            throw VtiException.create(VtiError.V_FILE_OPEN_FOR_WRITE);
        }

        if (VtiDocumentHepler.isCheckedout(documentStatus) && VtiDocumentHepler.isCheckoutOwner(documentStatus) == false)
        {
            // document already checked out by another user
            if (logger.isDebugEnabled())
            {
                logger.debug("Unable to perform checked out operation!!! Document is already checked out.");
            }
            throw VtiException.create(VtiError.V_DOC_CHECKED_OUT);
        }

        FileInfo checkedoutDocumentFileInfo;

        if (documentStatus.equals(DocumentStatus.LONG_CHECKOUT_OWNER) == false)
        {
            UserTransaction tx = transactionService.getUserTransaction(false);
            try
            {
                tx.begin();

                if (timeout == 0)
                {
                    // clearing short-term checkout if necessary
                    if (VtiDocumentHepler.isShortCheckedout(documentStatus))
                    {
                        lockService.unlock(documentFileInfo.getNodeRef());
                    }

                    NodeRef workingCopyNodeRef = checkOutCheckInService.checkout(documentFileInfo.getNodeRef());
                    checkedoutDocumentFileInfo = fileFolderService.getFileInfo(workingCopyNodeRef);
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Long-term checkout.");
                    }
                }
                else
                {
                    lockService.lock(documentFileInfo.getNodeRef(), LockType.WRITE_LOCK, VtiUtils.toAlfrescoLockTimeout(timeout));
                    // refresh file info
                    checkedoutDocumentFileInfo = fileFolderService.getFileInfo(documentFileInfo.getNodeRef());
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Short-term checkout.");
                    }
                }

                tx.commit();
            }
            catch (Throwable e)
            {
                try
                {
                    tx.rollback();
                }
                catch (Exception tex) {}

                throw VtiExceptionUtils.createRuntimeException(e);
            }
        }
        else
        {
            // document already checked out by same user, just returns file info for working copy
            checkedoutDocumentFileInfo = fileFolderService.getFileInfo(checkOutCheckInService.getWorkingCopy(documentFileInfo.getNodeRef()));
        }

        return checkedoutDocumentFileInfo;
    }
    
    /**
     * Creates only last folder in path
     *
     * @param path path
     * @return FileInfo object for last folder in path if it was created, else returns null
     */
    private FileInfo createOnlyLastFolder(String path)
    {
        Pair<String, String> parentChildPaths = VtiPathHelper.splitPathParentChild(path);
        String parentPath = parentChildPaths.getFirst();
        String lastFolderName = parentChildPaths.getSecond();
        FileInfo parentFileInfo = pathHelper.resolvePathFileInfo(parentPath);

        FileInfo lastFolderFileInfo = null;

        if (parentFileInfo != null && parentFileInfo.isFolder() && lastFolderName.length() != 0)
        {
            try
            {
                lastFolderFileInfo = fileFolderService.create(parentFileInfo.getNodeRef(), lastFolderName, ContentModel.TYPE_FOLDER);
            }
            catch (FileExistsException e)
            {
            }
        }

        return lastFolderFileInfo;
    }
    
    private void addFileFoldersRecursive(FileInfo rootFolder, DocsMetaInfo docsMetaInfo)
    {
        for (FileInfo fileInfo : fileFolderService.list(rootFolder.getNodeRef()))
        {
            DocMetaInfo docMetaInfo = new DocMetaInfo(fileInfo.isFolder());
            docMetaInfo.setPath(pathHelper.toUrlPath(fileInfo));
            setDocMetaInfo(fileInfo, docMetaInfo);

            if (fileInfo.isFolder())
            {
                docsMetaInfo.getFolderMetaInfoList().add(docMetaInfo);
                addFileFoldersRecursive(fileInfo, docsMetaInfo);
            }
            else
            {
                DocumentStatus documentStatus = documentHelper.getDocumentStatus(fileInfo.getNodeRef());
                AlfrescoVtiMethodHandler.assertRemovableDocument(documentStatus);
                docsMetaInfo.getFileMetaInfoList().add(docMetaInfo);
            }
        }
    }    
}
