/*
 * #%L
 * Alfresco File Transfer Receiver
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