/*
 * Created on 23-May-2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.dictionary.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.alfresco.repo.ref.NamespaceException;


public class NamespaceDAOImpl implements NamespaceDAO
{

    private List<String> uris = new ArrayList<String>();
    private HashMap<String, String> prefixes = new HashMap<String, String>();

    
    public NamespaceDAOImpl()
    {
    }

    public Collection<String> getURIs()
    {
        return Collections.unmodifiableCollection(uris);
    }

    public Collection<String> getPrefixes()
    {
        return Collections.unmodifiableCollection(prefixes.keySet());
    }

    
    public void addURI(String uri)
    {
        if (uris.contains(uri))
        {
            throw new NamespaceException("URI " + uri + " has already been defined");
        }
        uris.add(uri);
    }

    
    public void addPrefix(String prefix, String uri)
    {
        if (!uris.contains(uri))
        {
            throw new NamespaceException("Namespace URI " + uri + " does not exist");
        }
        prefixes.put(prefix, uri);
    }

    public void removeURI(String uri)
    {
        uris.remove(uri);
    }

    public void removePrefix(String prefix)
    {
        prefixes.remove(prefix);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.ref.NamespacePrefixResolver#getNamespaceURI(java.lang.String)
     */
    public String getNamespaceURI(String prefix)
    {
        return prefixes.get(prefix);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.ref.NamespacePrefixResolver#getPrefixes(java.lang.String)
     */
    public Collection<String> getPrefixes(String URI)
    {
        Collection<String> uriPrefixes = new ArrayList<String>();
        for (String key : prefixes.keySet())
        {
            String uri = prefixes.get(key);
            if ((uri != null) && (uri.equals(URI)))
            {
                uriPrefixes.add(key);
            }
        }
        return uriPrefixes;
    }

}
