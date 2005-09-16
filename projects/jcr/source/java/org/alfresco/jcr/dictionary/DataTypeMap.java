/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.jcr.dictionary;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.namespace.QName;


/**
 * Responsible for mapping Alfresco Data Types to JCR Property Types and vice versa.
 * 
 * @author David Caruana
 */
public class DataTypeMap
{

    /** Map of Alfresco Data Type to JCR Property Type */
    private static Map<QName, Integer> dataTypeToPropertyType = new HashMap<QName, Integer>();
    static
    {
        dataTypeToPropertyType.put(DataTypeDefinition.TEXT, PropertyType.STRING);
        dataTypeToPropertyType.put(DataTypeDefinition.CONTENT, PropertyType.BINARY);
        dataTypeToPropertyType.put(DataTypeDefinition.INT, PropertyType.LONG);
        dataTypeToPropertyType.put(DataTypeDefinition.LONG, PropertyType.LONG);
        dataTypeToPropertyType.put(DataTypeDefinition.FLOAT, PropertyType.DOUBLE);
        dataTypeToPropertyType.put(DataTypeDefinition.DOUBLE, PropertyType.DOUBLE);
        dataTypeToPropertyType.put(DataTypeDefinition.DATE, PropertyType.DATE);
        dataTypeToPropertyType.put(DataTypeDefinition.DATETIME, PropertyType.DATE);
        dataTypeToPropertyType.put(DataTypeDefinition.BOOLEAN, PropertyType.BOOLEAN);
        dataTypeToPropertyType.put(DataTypeDefinition.QNAME, PropertyType.NAME);
        dataTypeToPropertyType.put(DataTypeDefinition.GUID, PropertyType.STRING);
        dataTypeToPropertyType.put(DataTypeDefinition.CATEGORY, PropertyType.STRING);  // TODO: Check this mapping
        dataTypeToPropertyType.put(DataTypeDefinition.NODE_REF, PropertyType.REFERENCE);
        dataTypeToPropertyType.put(DataTypeDefinition.PATH, PropertyType.PATH);
    }
    
    
    /**
     * Convert an Alfresco Data Type to a JCR Property Type
     * 
     * @param datatype  alfresco data type
     * @return  JCR property type
     * @throws RepositoryException
     */
    public static int convertDataTypeToPropertyType(QName datatype)
    {
        Integer propertyType = dataTypeToPropertyType.get(datatype);
        if (propertyType == null)
        {
            throw new AlfrescoRuntimeException("Cannot map Alfresco data type " + datatype + " to JCR property type.");
        }
        return propertyType;
    }
    
}
