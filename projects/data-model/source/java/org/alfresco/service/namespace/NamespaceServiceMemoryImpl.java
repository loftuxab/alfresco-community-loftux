
package org.alfresco.service.namespace;

import java.util.Collection;

import org.alfresco.util.OneToManyHashBiMap;

/**
 * A basic implementation of the NamespaceService interface intended for use in
 * unit tests. This implementation does not persist any changes beyond the
 * lifetime of the object.
 * 
 * @author Nick Smith
 */
public class NamespaceServiceMemoryImpl implements NamespaceService
{
    // URI to Prefix map.
    private final OneToManyHashBiMap<String, String> map = new OneToManyHashBiMap<String, String>();

    public void registerNamespace(String prefix, String uri)
    {
        map.putSingleValue(uri, prefix);
    }

    public void unregisterNamespace(String prefix)
    {
        map.removeValue(prefix);
    }

    public String getNamespaceURI(String prefix) throws NamespaceException
    {
        return map.getKey(prefix);
    }

    public Collection<String> getPrefixes(String namespaceURI) throws NamespaceException
    {
        return map.get(namespaceURI);
    }

    public Collection<String> getPrefixes()
    {
        return map.flatValues();
    }

    public Collection<String> getURIs()
    {
        return map.keySet();
    }

}