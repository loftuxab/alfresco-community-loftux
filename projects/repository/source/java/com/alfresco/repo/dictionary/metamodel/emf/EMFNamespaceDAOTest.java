package org.alfresco.repo.dictionary.metamodel.emf;

import java.util.Collection;
import java.util.List;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.bootstrap.BaseDictionaryTest;
import org.alfresco.repo.dictionary.metamodel.M2NamespacePrefix;
import org.alfresco.repo.dictionary.metamodel.M2NamespaceURI;

/**
 * EMF Meta Model DAO Tests
 * 
 * @author David Caruana
 */
public class EMFNamespaceDAOTest extends BaseDictionaryTest
{
    public void testDAOGetURIs()
    {
        Collection<String> uris = namespaceDao.getURIs();
        assertNotNull(uris);
        assertEquals(3, uris.size());
        
        Collection<String> prefixes = namespaceDao.getPrefixes();
        assertNotNull(prefixes);
        assertEquals(3, prefixes.size());
    }

    public void testDAOGetURI()
    {
        M2NamespaceURI uri = namespaceDao.getURI(NamespaceService.DEFAULT_URI);
        assertNotNull(uri);
        assertEquals(NamespaceService.DEFAULT_URI, uri.getURI());
        List<M2NamespacePrefix> prefixes = uri.getPrefixes();
        assertNotNull(prefixes);
        assertEquals(1, prefixes.size());
        M2NamespacePrefix prefix = prefixes.get(0);
        assertEquals(NamespaceService.DEFAULT_PREFIX, prefix.getPrefix());
        M2NamespaceURI invalidUri = namespaceDao.getURI("garbage");
        assertNull(invalidUri);
    }

    public void testDAOGetPrefix()
    {
        M2NamespacePrefix prefix = namespaceDao.getPrefix(NamespaceService.DEFAULT_PREFIX);
        assertNotNull(prefix);
        assertEquals(NamespaceService.DEFAULT_PREFIX, prefix.getPrefix());
        M2NamespaceURI uri = prefix.getURI();
        assertNotNull(uri);
        assertEquals(NamespaceService.DEFAULT_URI, uri.getURI());
        M2NamespacePrefix invalidPrefix = namespaceDao.getPrefix("garbage");
        assertNull(invalidPrefix);
    }
}
