
package org.alfresco.module.vti.handler.alfresco.soap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
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
import org.alfresco.module.vti.VtiDownloadContentServlet;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.handler.alfresco.VtiUtils;
import org.alfresco.module.vti.handler.soap.VersionsServiceHandler;
import org.alfresco.module.vti.metadata.soap.versions.DocumentVersionBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Alfresco implementation of VersionsServiceHandler interface
 *
 * @author Dmitry Lazurkin
 *
 */
public class AlfrescoVersionsServiceHandler implements VersionsServiceHandler
{
    private final static Log logger = LogFactory.getLog("org.alfresco.module.vti.handler");

    private NodeService nodeService;
    private FileFolderService fileFolderService;
    private VersionService versionService;

    private VtiPathHelper pathHelper;

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

    public List<DocumentVersionBean> getVersions(String fileName)
    {
        FileInfo documentFileInfo = pathHelper.resolvePathFileInfo(fileName);

        assertDocument(documentFileInfo);

        return getVersions(documentFileInfo);
    }

    public List<DocumentVersionBean> deleteVersion(String fileName, String fileVersion)
    {
        throw new UnsupportedOperationException("This method is unsupported");
    }

    public DocumentVersionBean deleteAllVersions(String fileName)
    {
        FileInfo documentFileInfo = pathHelper.resolvePathFileInfo(fileName);

        assertDocument(documentFileInfo);

        versionService.deleteVersionHistory(documentFileInfo.getNodeRef());

        return getDocumentVersionInfo(documentFileInfo);
    }

    public List<DocumentVersionBean> restoreVersion(String fileName, String fileVersion)
    {
        FileInfo documentFileInfo = pathHelper.resolvePathFileInfo(fileName);

        assertDocument(documentFileInfo);

        Map<String, Serializable> props = new HashMap<String, Serializable>(1, 1.0f);
        props.put(Version.PROP_DESCRIPTION, "");
        props.put(VersionModel.PROP_VERSION_TYPE, VersionType.MAJOR);
        versionService.createVersion(documentFileInfo.getNodeRef(), props);

        String alfrescoVersionLabel = VtiUtils.toAlfrescoVersionLabel(fileVersion);
        VersionHistory versionHistory = versionService.getVersionHistory(documentFileInfo.getNodeRef());
        Version version = versionHistory.getVersion(alfrescoVersionLabel);

        versionService.revert(documentFileInfo.getNodeRef(), version);

        return getVersions(documentFileInfo);
    }

    /**
     * Returns DocumentVersionBean list for file info
     *
     * @param documentFileInfo file info
     * @return list of DocumentVersionBean
     */
    private List<DocumentVersionBean> getVersions(FileInfo documentFileInfo)
    {
        List<DocumentVersionBean> versions = new LinkedList<DocumentVersionBean>();

        // add current version
        Version currentVersion = versionService.getCurrentVersion(documentFileInfo.getNodeRef());
        if (currentVersion != null)
        {
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
                    versions.add(getDocumentVersionInfo(version));
                }
            }
        }
        else
        {
            versions.add(getDocumentVersionInfo(documentFileInfo));
        }

        return versions;
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

        if (logger.isDebugEnabled())
        {
            logger.debug("New document version bean = " + docVersion);
        }

        return docVersion;
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
            throw new RuntimeException("That document doesn't exist");
        }

        if (documentFileInfo.isFolder() == true)
        {
            throw new RuntimeException("It isn't document. It is folder");
        }
    }    

}
