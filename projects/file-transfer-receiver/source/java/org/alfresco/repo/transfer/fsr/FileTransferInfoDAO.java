package org.alfresco.repo.transfer.fsr;

import java.util.List;
/**
 *
 * @author philippe
 *
 */
public interface FileTransferInfoDAO
{
    FileTransferInfoEntity createFileTransferInfo(
            String nodeRef,
            String parent,
            String path,
            String content_name,
            String contentUrl,
            boolean isFolder,
            String sourceRepoId);

    FileTransferInfoEntity findFileTransferInfoByNodeRef(String nodeRef);

    List<FileTransferInfoEntity> findFileTransferInfoByParentNodeRef(String nodeRef);

    void updateFileTransferInfoByNodeRef(FileTransferInfoEntity modifiedEntity);

    void deleteFileTransferInfoByNodeRef(String nodeRef);

    FileTransferNodeRenameEntity createFileTransferNodeRenameEntity(String noderef, String transferId, String newName);

    void deleteNodeRenameByTransferIdAndNodeRef(String transferId, String nodeRef);

    List<FileTransferNodeRenameEntity> findFileTransferNodeRenameEntityByTransferId(String transferId);

    void updatePathOfChildren(String parentId, String newPath);
}
