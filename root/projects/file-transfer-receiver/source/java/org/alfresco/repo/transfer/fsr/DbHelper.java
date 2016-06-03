package org.alfresco.repo.transfer.fsr;

import java.util.List;

public interface DbHelper
{

    FileTransferInfoEntity findFileTransferInfoByNodeRef(final String nodeRef);

    List<FileTransferInfoEntity> findFileTransferInfoByParentNodeRef(final String nodeRef);

    void updateFileTransferInfoByNodeRef(final FileTransferInfoEntity modifiedEntity);

    void deleteNodeByNodeRef(final String nodeRef);

    void createNodeInDB(final String nodeRef, final String parentNodeRef, final String path, final String name,
            final String contentUrl, final boolean isFolder);
    
    void updatePathOfChildren(final String parentId, final String parentPath);
}