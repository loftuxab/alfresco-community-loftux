/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.service.namespace;

import java.util.Collection;



/**
 * Namespace Service.
 * 
 * The Namespace Service provides access to and definition of namespace
 * URIs and Prefixes. 
 * 
 * @author David Caruana
 */
public interface NamespaceService extends NamespacePrefixResolver
{

    /**
     * Default Namespace URI
     */
    public static final String DEFAULT_URI = "";
    
    /**
     * Default Namespace Prefix
     */
    public static final String DEFAULT_PREFIX = "";

    /**
     * Alfresco Dictionary Namespace URI
     */
    public static final String ALFRESCO_DICTIONARY_URI = "http://www.alfresco.org/dictionary/0.1";
    
    /**
     * Alfresco Dictionary Namespace Prefix
     */
    public static final String ALFRESCO_DICTIONARY_PREFIX = "d";

    /**
     * Alfresco View Namespace URI
     */
    public static final String ALFRESCO_VIEW_URI = "http://www.alfresco.org/repository/view/0.1";
    
    /**
     * Alfresco View Namespace Prefix
     */
    public static final String ALFRESCO_VIEW_PREFIX = "v";
    
    /**
     * Alfresco Namespace URI
     */
    public static final String ALFRESCO_URI = "http://www.alfresco.org/1.0";
    
    /**
     * Alfresco Namespace Prefix
     */
    public static final String ALFRESCO_PREFIX = "alf";

    
    /**
     * Gets all registered Namespace URIs
     * 
     * @return collection of all registered namespace URIs
     */
    Collection<String> getURIs();

  
    
}
