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
package org.alfresco.module.vti.handler.alfresco.soap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.vti.VtiDownloadContentServlet;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.handler.alfresco.VtiUtils;
import org.alfresco.module.vti.handler.soap.VersionsServiceHandler;
import org.alfresco.module.vti.metadata.soap.versions.DocumentVersionBean;
import org.alfresco.repo.version.VersionModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.cmr.version.VersionType;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author PavelYur
 *
 */
public class Alfresco3VersionsServiceHandler implements VersionsServiceHandler
{
    private NodeService nodeService;
    private FileFolderService fileFolderService;
    private VersionService versionService;

    private VtiPathHelper pathHelper;
    private TransactionService transactionService;
    
    private static Log logger = LogFactory.getLog(Alfresco3UserGroupServiceHandler.class);
        
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    } 

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }

    public void setVersionService(VersionService versionService)
    {
        this.versionService = versionService;
    }

    public void setPathHelper(VtiPathHelper pathHelper)
    {
        this.pathHelper = pathHelper;
    }

    public DocumentVersionBean deleteAllVersions(String fileName)
    {
        if (logger.isDebugEnabled())
            logger.debug("Method with name 'deleteAllVersions' is not realized.");
        return null;
    }

    public List<DocumentVersionBean> deleteVersion(String fileName, String fileVersion)
    {
        if (logger.isDebugEnabled())
            logger.debug("Method with name 'deleteVersion' is not realized.");
        return null;
    }

    public List<DocumentVersionBean> getVersions(String fileName)
    {
        if (logger.isDebugEnabled())
            logger.debug("Method with name 'getVersions' is started.");
        
        FileInfo documentFileInfo = pathHelper.resolvePathFileInfo(fileName);

        if (logger.isDebugEnabled())
            logger.debug("Asserting documentFileInfo for file '" + fileName + "'.");
        
        assertDocument(documentFileInfo);

        List<DocumentVersionBean> result = getVersions(documentFileInfo);
        
        if (logger.isDebugEnabled())
            logger.debug("Method with name 'getVersions' is finished.");
        
        return result;
    }

    public List<DocumentVersionBean> restoreVersion(String fileName, String fileVersion)
    {
        if (logger.isDebugEnabled())
            logger.debug("Method with name 'restoreVersion' is started.");
        
        FileInfo documentFileInfo = pathHelper.resolvePathFileInfo(fileName);

        assertDocument(documentFileInfo);
        UserTransaction tx = transactionService.getUserTransaction(false);
        try
        {
            tx.begin();
            
            Map<String, Serializable> props = new HashMap<String, Serializable>(1, 1.0f);
            props.put(Version.PROP_DESCRIPTION, "");
            props.put(VersionModel.PROP_VERSION_TYPE, VersionType.MAJOR);
            
            if (logger.isDebugEnabled())
                logger.debug("Creating a new version for '" + fileName + "'.");
            versionService.createVersion(documentFileInfo.getNodeRef(), props);
    
            String alfrescoVersionLabel = VtiUtils.toAlfrescoVersionLabel(fileVersion);
            VersionHistory versionHistory = versionService.getVersionHistory(documentFileInfo.getNodeRef());
            Version version = versionHistory.getVersion(alfrescoVersionLabel);
            if (logger.isDebugEnabled())
                logger.debug("Reverting version '" + fileVersion + " for '" + fileName + "'.");
            versionService.revert(documentFileInfo.getNodeRef(), version);
            
            tx.commit();
        }
        catch (Exception e) 
        {
            try
            {
                tx.rollback();
            }
            catch (Exception tex){}
            if (logger.isDebugEnabled())
                logger.debug("Error: version was not restored. ", e);
        }

        List<DocumentVersionBean> result = getVersions(documentFileInfo); 
        
        if (logger.isDebugEnabled())
            logger.debug("Method with name 'restoreVersion' is finished.");
        
        return result;
    }
    
    /**
     * Asserts file info for existent document
     *
     * @param documentFileInfo document file info
     */
    private void assertDocument(FileInfo documentFileInfo)
    {
        if (documentFileInfo == null)
        {
            if (logger.isDebugEnabled())
                logger.debug("Error: That document doesn't exist.");
            throw new RuntimeException("That document doesn't exist");
        }

        if (documentFileInfo.isFolder() == true)
        {
            if (logger.isDebugEnabled())
                logger.debug("Error: It isn't document. It is folder.");
            throw new RuntimeException("It isn't document. It is folder");
        }
    }
    
    /**
     * Returns DocumentVersionBean list for file info
     *
     * @param documentFileInfo file info
     * @return list of DocumentVersionBean
     */
    private List<DocumentVersionBean> getVersions(FileInfo documentFileInfo)
    {
        if (logger.isDebugEnabled())
            logger.debug("Getting all versions for '" + documentFileInfo.getName() + "'.");
        
        List<DocumentVersionBean> versions = new LinkedList<DocumentVersionBean>();

        if (logger.isDebugEnabled())
            logger.debug("Getting current version.");
        Version currentVersion = versionService.getCurrentVersion(documentFileInfo.getNodeRef());
        if (currentVersion != null)
        {
            if (logger.isDebugEnabled())
                logger.debug("Adding current version to result.");

            versions.add(getDocumentVersionInfo(currentVersion));

            boolean currentFound = false;
            for (Version version : versionService.getVersionHistory(documentFileInfo.getNodeRef()).getAllVersions())
            {
                if (currentFound == false && currentVersion.getVersionLabel().equals(version.getVersionLabel()))
                {
                    currentFound = true;
                }
                else
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Adding version '" + version.getVersionLabel() + "' to result.");
                    versions.add(getDocumentVersionInfo(version));
                }
            }
        }
        else
        {
            if (logger.isDebugEnabled())
                logger.debug("Current version doesn't exist. Creating a new current version.");
            versions.add(getDocumentVersionInfo(documentFileInfo));
        }

        return versions;
    }
    
    /**
     * Returns DocumentVersionBean for version
     *
     * @param version version
     * @return document version bean
     */
    private DocumentVersionBean getDocumentVersionInfo(Version version)
    {
        DocumentVersionBean docVersion = new DocumentVersionBean();

        NodeRef versionNodeRef = version.getFrozenStateNodeRef();
        FileInfo documentFileInfo = fileFolderService.getFileInfo(versionNodeRef);

        docVersion.setUrl(VtiDownloadContentServlet.generateDownloadURL(documentFileInfo.getNodeRef(), documentFileInfo.getName()));
        docVersion.setVersion(version.getVersionLabel());
        docVersion.setCreatedBy(version.getCreator());
        docVersion.setCreatedTime(VtiUtils.formatVersionDate(version.getCreatedDate()));
        ContentData content = (ContentData) nodeService.getProperty(version.getFrozenStateNodeRef(), ContentModel.PROP_CONTENT);
        docVersion.setSize(content.getSize());

        String versionDescription = version.getDescription();
        if (versionDescription != null)
        {
            docVersion.setComments(versionDescription);
        }
        else
        {
            docVersion.setComments("");
        }

        return docVersion;
    }
    
    /**
     * Returns DocumentVersionBean for document without version history
     *
     * @param documentFileInfo document file info
     * @return document version bean
     */
    private DocumentVersionBean getDocumentVersionInfo(FileInfo documentFileInfo)
    {
        DocumentVersionBean docVersion = new DocumentVersionBean();

        docVersion.setUrl("/" + pathHelper.toUrlPath(documentFileInfo));
        docVersion.setVersion("1.0");
        docVersion.setCreatedBy((String) documentFileInfo.getProperties().get(ContentModel.PROP_CREATOR));
        docVersion.setCreatedTime(VtiUtils.formatVersionDate(documentFileInfo.getCreatedDate()));
        docVersion.setSize(documentFileInfo.getContentData().getSize());
        docVersion.setComments("");

        return docVersion;
    }
}
