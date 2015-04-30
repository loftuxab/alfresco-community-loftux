/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.webdav.WebDAVHelper;
import org.alfresco.repo.webdav.WebDAVServerException;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests for the cloud specific WebDAV PUT method.
 * 
 * @author Matt Ward
 */
@RunWith(MockitoJUnitRunner.class)
public class CloudPutMethodTest
{
    private MockHttpServletRequest req;
    private MockHttpServletResponse resp;
    private WebDAVHelper davHelper;
    private NodeRef nodeRef;
    private @Mock FileInfo fileInfo;
    private @Mock ServiceRegistry serviceRegistry;
    private @Mock VersionService versionService;
    private CloudPutMethod putMethod;
    
    @Before
    public void setUp() throws Exception
    {
        req = new MockHttpServletRequest("PUT", "/path/to/file.docx");
        resp = new MockHttpServletResponse();
        davHelper = new WebDAVHelper();

        davHelper.setServiceRegistry(serviceRegistry);
        when(serviceRegistry.getVersionService()).thenReturn(versionService);
        
        // The fictional NodeRef and FileInfo for the uploaded file.
        nodeRef = new NodeRef("workspace://SpacesStore/UPLOADED-FILE-UUID");
        when(fileInfo.getNodeRef()).thenReturn(nodeRef);
        
        // The fictional root node being used by WebDAV.
        NodeRef rootNode = new NodeRef("workspace://SpacesStore/ROOT-NODE-UUID");
        
        putMethod = new CloudPutMethod();
        putMethod.setDetails(req, resp, davHelper, rootNode);
        
        FieldUtils.writeField(putMethod, "contentNodeInfo", fileInfo, true);
    }
        
    @Test
    public void initialVersionForNewNonZeroByteFile() throws IllegalAccessException
    {
        // The uploaded file has content        
        FieldUtils.writeField(putMethod, "fileSize", 2048, true);
        // This is not an existing file
        FieldUtils.writeField(putMethod, "created", true, true);
        
        putMethod.ensureVersionable();
        
        Map<QName, Serializable> versionProps = new HashMap<QName, Serializable>(1, 1.0f);
        verify(versionService).ensureVersioningEnabled(eq(nodeRef), eq(versionProps));
    }
    
    @Test
    public void initialVersionForOverwriteOfZeroByteFile() throws WebDAVServerException, Exception
    {
        // The uploaded file has content        
        FieldUtils.writeField(putMethod, "fileSize", 2048, true);
        // Overwrite existing file
        FieldUtils.writeField(putMethod, "created", false, true);
        
        putMethod.ensureVersionable();
        
        Map<QName, Serializable> versionProps = new HashMap<QName, Serializable>(1, 1.0f);
        verify(versionService).ensureVersioningEnabled(eq(nodeRef), eq(versionProps));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void noVersionEnforcedForZeroByteFiles() throws IllegalAccessException
    {
        // The uploaded file has no content
        FieldUtils.writeField(putMethod, "fileSize", 0, true);
        
        putMethod.ensureVersionable();

        verify(versionService, never()).ensureVersioningEnabled(any(NodeRef.class), any(Map.class));
    }
}
