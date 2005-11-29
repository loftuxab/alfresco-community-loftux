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

import javax.jcr.RepositoryException;

import org.alfresco.model.ContentModel;
import org.alfresco.service.namespace.QName;


/**
 * Responsible for mapping Alfresco Classes to JCR Types / Mixins and vice versa.
 * 
 * @author David Caruana
 */
public class ClassMap
{
    /** Map of Alfresco Class to JCR Class */
    private static Map<QName, QName> JCRToAlfresco = new HashMap<QName, QName>();
    static
    {
        JCRToAlfresco.put(NodeTypeImpl.MIX_REFERENCEABLE, ContentModel.ASPECT_REFERENCEABLE);
        JCRToAlfresco.put(NodeTypeImpl.MIX_LOCKABLE, ContentModel.ASPECT_LOCKABLE);
        JCRToAlfresco.put(NodeTypeImpl.MIX_VERSIONABLE, ContentModel.ASPECT_VERSIONABLE);
    }

    /** Map of JCR Class to Alfresco Class */
    private static Map<QName, QName> AlfrescoToJCR = new HashMap<QName, QName>();
    static
    {
        AlfrescoToJCR.put(ContentModel.ASPECT_REFERENCEABLE, NodeTypeImpl.MIX_REFERENCEABLE);
        AlfrescoToJCR.put(ContentModel.ASPECT_LOCKABLE, NodeTypeImpl.MIX_LOCKABLE);
        AlfrescoToJCR.put(ContentModel.ASPECT_VERSIONABLE, NodeTypeImpl.MIX_VERSIONABLE);
    }
    
    /**
     * Convert an Alfresco Class to a JCR Type
     * 
     * @param jcrType  JCR Type
     * @return  Alfresco Class
     * @throws RepositoryException
     */
    public static QName convertTypeToClass(QName jcrType)
    {
        return JCRToAlfresco.get(jcrType);
    }

    /**
     * Convert an Alfresco Class to a JCR Type
     * 
     * @param  alfrescoClass  Alfresco Class
     * @return  JCR Type
     * @throws RepositoryException
     */
    public static QName convertClassToType(QName alfrescoClass)
    {
        return JCRToAlfresco.get(alfrescoClass);
    }

}
