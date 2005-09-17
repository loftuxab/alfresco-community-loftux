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
package org.alfresco.jcr.session;

import java.util.Collection;

import javax.jcr.AccessDeniedException;
import javax.jcr.NamespaceException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;

import org.alfresco.service.namespace.DynamicNamespacePrefixResolver;
import org.alfresco.service.namespace.NamespacePrefixResolver;


/**
 * Alfresco implementation of a JCR Namespace registry
 * 
 * @author David Caruana
 */
public class NamespaceRegistryImpl implements NamespaceRegistry
{

    private DynamicNamespacePrefixResolver resolver;
    
    
    /**
     * Construct
     * 
     * @param resolver  namespace resolver
     */
    public NamespaceRegistryImpl(DynamicNamespacePrefixResolver resolver)
    {
        this.resolver = resolver;
    }
    
    /**
     * Get Namespace resolver
     * 
     * @return namespace prefix resolver
     */
    public NamespacePrefixResolver getNamespaceResolver()
    {
        return resolver;
    }
    
    /* (non-Javadoc)
     * @see javax.jcr.NamespaceRegistry#registerNamespace(java.lang.String, java.lang.String)
     */
    public void registerNamespace(String prefix, String uri) throws NamespaceException, UnsupportedRepositoryOperationException, AccessDeniedException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.NamespaceRegistry#unregisterNamespace(java.lang.String)
     */
    public void unregisterNamespace(String prefix) throws NamespaceException, UnsupportedRepositoryOperationException, AccessDeniedException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.NamespaceRegistry#getPrefixes()
     */
    public String[] getPrefixes() throws RepositoryException
    {
        Collection<String> prefixes = resolver.getPrefixes();
        return prefixes.toArray(new String[prefixes.size()]);
    }

    /* (non-Javadoc)
     * @see javax.jcr.NamespaceRegistry#getURIs()
     */
    public String[] getURIs() throws RepositoryException
    {
        Collection<String> uris = resolver.getURIs();
        return uris.toArray(new String[uris.size()]);        
    }

    /* (non-Javadoc)
     * @see javax.jcr.NamespaceRegistry#getURI(java.lang.String)
     */
    public String getURI(String prefix) throws NamespaceException, RepositoryException
    {
        String uri = resolver.getNamespaceURI(prefix);
        if (uri == null)
        {
            throw new NamespaceException("Prefix " + prefix + " is unknown.");
        }
        return uri;
    }

    /* (non-Javadoc)
     * @see javax.jcr.NamespaceRegistry#getPrefix(java.lang.String)
     */
    public String getPrefix(String uri) throws NamespaceException, RepositoryException
    {
        Collection<String> prefixes = resolver.getPrefixes(uri);
        if (prefixes.size() == 0)
        {
            throw new NamespaceException("URI " + uri + " is unknown.");
        }
        // Return first prefix registered for uri
        return prefixes.iterator().next();
    }

}
