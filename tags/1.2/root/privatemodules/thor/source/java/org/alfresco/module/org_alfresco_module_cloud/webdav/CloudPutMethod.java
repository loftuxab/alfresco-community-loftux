/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */
package org.alfresco.module.org_alfresco_module_cloud.webdav;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.analytics.Analytics;
import org.alfresco.repo.webdav.PutMethod;
import org.alfresco.repo.webdav.WebDAVServerException;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.namespace.QName;

/**
 * Cloud-specific WebDAV PUT method. This adds the behaviour of raising an analytics
 * event after an upload has occurred.
 * 
 * @author Matt Ward
 */
public class CloudPutMethod extends PutMethod
{
    /**
     * Overriding this method, since it is called outside of the retryable transaction
     * that the PUT method's main logic resides in.
     * <p>
     * Whilst posting activities should and does happen within the retryable transaction the
     * analytics event should not - so as to avoid publishing multiple events for the same PUT request.
     */
    @Override
    protected void generateResponseImpl() throws Exception
    {
        super.generateResponseImpl();
        
        FileInfo fileInfo = getContentNodeInfo();
        // Don't raise analytics events for hidden files, resource forks etc.
        if (!fileInfo.isHidden())
        {
            boolean fileModified = !isCreated();
            String mimeType = getContentType();
            long fileSize = getFileSize();
            Analytics.record_UploadDocument(mimeType, fileSize, fileModified);            
        }
    }
    
    @Override
    protected void executeImpl() throws WebDAVServerException, Exception
    {
        super.executeImpl();
        ensureVersionable();
    }

    /**
     * Once a file contains "real" content (not a zero-byte placeholder file for example) then
     * we must make sure that it is versionable: quotas rely on this.
     */
    protected void ensureVersionable()
    {
        if (getFileSize() > 0)
        {
            // THOR-1188
            Map<QName, Serializable> initialVersionProps = new HashMap<QName, Serializable>(1, 1.0f);
            getServiceRegistry().getVersionService().ensureVersioningEnabled(getContentNodeInfo().getNodeRef(), initialVersionProps);
        }
    }
}
