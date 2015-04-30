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

import static org.junit.Assert.assertEquals;

import org.alfresco.service.ServiceRegistry;
import org.alfresco.test_category.SharedJVMTestsCategory;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;

/**
 * Tests for the CloudWebDAVHelper class.
 * 
 * @author Matt Ward
 */
@RunWith(MockitoJUnitRunner.class)
@Category(SharedJVMTestsCategory.class)
public class CloudWebDAVHelperTest
{
    private CloudWebDAVHelper helper;
    private MockHttpServletRequest request;
    private @Mock ServiceRegistry serviceRegistry;
    
    @Before
    public void setUp()
    {
        helper = new CloudWebDAVHelper();
        helper.setUrlPathPrefix("");
        helper.setServiceRegistry(serviceRegistry);
        request = new MockHttpServletRequest(new MockServletContext("/alfresco"));
        request.setRequestURI("/alfresco/webdav");
        request.setServletPath("/webdav");
    }
    
    @Test
    public void canGetURLForPath()
    {
        assertCorrectURI("/alfresco/webdav/alfresco.com/engineering/myfolder/mydoc.txt",
                         "/alfresco.com/engineering/documentLibrary/myfolder/mydoc.txt");
        
        assertCorrectURI("/alfresco/webdav/some/other/path",
                         "/some/other/path");
    }

    @Test
    public void canInsertDocLibIntoPath()
    {
        assertEquals("/network/site/documentLibrary/folder/file",
                    helper.insertDocLibPathElement("/network/site/folder/file"));

        assertEquals("/network/site/documentLibrary/file", helper.insertDocLibPathElement("/network/site/file"));
        
        assertEquals("/network/site/documentLibrary", helper.insertDocLibPathElement("/network/site"));

        assertEquals("/network", helper.insertDocLibPathElement("/network"));

        assertEquals("/", helper.insertDocLibPathElement("/"));
    }
    
    private void assertCorrectURI(String expectedURI, String path)
    {
        assertEquals(expectedURI, helper.getURLForPath(request, path, false, ""));
    }

}
