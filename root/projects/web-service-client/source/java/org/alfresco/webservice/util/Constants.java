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
package org.alfresco.webservice.util;

/**
 * Constants class used by the web service client samples and tests
 * 
 * @author Roy Wetherall
 */
public class Constants
{
    /** Namespace constants */
    public static final String NAMESPACE_SYSTEM_MODEL = "http://www.alfresco.org/model/system/1.0";
    public static final String NAMESPACE_CONTENT_MODEL = "http://www.alfresco.org/model/content/1.0";
    
    /** Usefull model constants */
    public static final String ASSOC_CHILDREN =         createQNameString(NAMESPACE_SYSTEM_MODEL, "children");    
    public static final String TYPE_CMOBJECT =          createQNameString(NAMESPACE_CONTENT_MODEL, "cmobject");
    public static final String PROP_NAME =              createQNameString(NAMESPACE_CONTENT_MODEL, "name");
    public static final String TYPE_CONTENT =           createQNameString(NAMESPACE_CONTENT_MODEL, "content");
    public static final String PROP_CONTENT =           createQNameString(NAMESPACE_CONTENT_MODEL, "content");
    public static final String ASSOC_CONTAINS =         createQNameString(NAMESPACE_CONTENT_MODEL, "contains");
    public static final String ASPECT_VERSIONABLE =     createQNameString(NAMESPACE_CONTENT_MODEL, "versionable");
    public static final String PROP_CREATED =           createQNameString(NAMESPACE_CONTENT_MODEL, "created");
    public static final String PROP_DESCRIPTION =       createQNameString(NAMESPACE_CONTENT_MODEL, "description");    
    public static final String TYPE_FOLDER =            createQNameString(NAMESPACE_CONTENT_MODEL, "folder");
    public static final String ASPECT_CLASSIFIABLE =    createQNameString(NAMESPACE_CONTENT_MODEL, "classifiable"); 
    
    /** Mime types */
    public static final String MIMETYPE_TEXT_PLAIN  = "text/plain";
    public static final String MIMETYPE_TEXT_CSS    = "text/css";    

    /**
     * Helper function to create a QName string from a namespace URI and name
     * 
     * @param namespace     the namespace URI
     * @param name          the name
     * @return              QName string
     */
    public static String createQNameString(String namespace, String name)
    {
        return "{" + namespace + "}" + name;
    }
}
