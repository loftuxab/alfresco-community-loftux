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
 * The <code>NamespacePrefixResolver</code> provides a mapping between
 * namespace prefixes and namespace URIs.
 * 
 * @author David Caruana
 */
public interface NamespacePrefixResolver
{
    /**
     * Gets the namespace URI registered for the given prefix
     * 
     * @param prefix  prefix to lookup
     * @return  the namespace
     * @throws NamespaceException  if prefix has not been registered  
     */
    public String getNamespaceURI(String prefix)
        throws NamespaceException;
    
    /**
     * Gets the registered prefixes for the given namespace URI
     * 
     * @param namespaceURI  namespace URI to lookup
     * @return  the prefixes (or empty collection, if no prefixes registered against URI)
     * @throws NamespaceException  if URI has not been registered 
     */
    public Collection<String> getPrefixes(String namespaceURI)
        throws NamespaceException;
    
    /**
     * Gets all registered Prefixes
     * 
     * @return collection of all registered namespace prefixes
     */
    Collection<String> getPrefixes();
}
