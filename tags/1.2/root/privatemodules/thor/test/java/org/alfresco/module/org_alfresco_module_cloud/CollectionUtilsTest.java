/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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

package org.alfresco.module.org_alfresco_module_cloud;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.collections.CollectionUtils;
import org.alfresco.util.collections.Function;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

/**
 * @author Neil Mc Erlean
 * @since Thor
 */
public class CollectionUtilsTest
{
    private static ApplicationContext TEST_CONTEXT;
    private static NamespaceService NAMESPACE_SERVICE;
    /**
     * Initialise various services required by the test.
     */
    @BeforeClass public static void initTestsContext() throws Exception
    {
        TEST_CONTEXT = ApplicationContextHelper.getApplicationContext();
        
        NAMESPACE_SERVICE  = (NamespaceService) TEST_CONTEXT.getBean("NamespaceService");
    }
    
    @Test public void transformMapKeyTypes() throws Exception
    {
        Map<QName, Serializable> inputMap = new HashMap<QName, Serializable>();
        inputMap.put(ContentModel.ASPECT_ARCHIVED, "ignored value");
        inputMap.put(ContentModel.TYPE_AUTHORITY, "ignored value");
        
        Function<QName, String> transformFunction = new Function<QName, String>()
        {
            public String apply(QName value)
            {
                return value.toPrefixString(NAMESPACE_SERVICE);
            }
        };
        
        Map<String, Serializable> transformedMap = CollectionUtils.transformKeys(inputMap, transformFunction);
        
        assertEquals(inputMap.size(), transformedMap.size());
        
        // Need to convert to standard ArrayList instances to get simple equals() implementation.
        assertEquals(new ArrayList<Serializable>(inputMap.values()),
                     new ArrayList<Serializable>(transformedMap.values()));
        
        Set<String> expectedKeys = new HashSet<String>();
        expectedKeys.add("cm:authority");
        expectedKeys.add("sys:archived");
        assertEquals(expectedKeys, transformedMap.keySet());
    }
}
