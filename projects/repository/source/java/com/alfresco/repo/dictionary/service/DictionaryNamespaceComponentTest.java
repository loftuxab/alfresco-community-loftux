package org.alfresco.repo.dictionary.service;

import java.util.Collection;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.bootstrap.BaseDictionaryTest;
import org.alfresco.repo.ref.NamespaceException;


/**
 * Data Dictionary Service Tests
 * 
 * @author David Caruana
 */
public class DictionaryNamespaceComponentTest extends BaseDictionaryTest
{
    protected NamespaceService service;
    
    protected void setUp() throws Exception
    {
        // ensure that test model is bootstrapped
        super.setUp();
        
        DictionaryNamespaceComponent component = new DictionaryNamespaceComponent();
        component.setNamespaceDAO(super.namespaceDao);
        service = component;
    }
    

    public void testGetPrefixes()
    {
        Collection<String> prefixes = service.getPrefixes();
        assertNotNull(prefixes);
        assertEquals(3, prefixes.size());
    }

    public void testGetURIs()
    {
        Collection<String> uris = service.getURIs();
        assertNotNull(uris);
        assertEquals(3, uris.size());
    }

    public void testGetURIFromPrefix()
    {
        String uri = service.getNamespaceURI(NamespaceService.alfresco_PREFIX);
        assertNotNull(uri);
        assertEquals(NamespaceService.alfresco_URI, uri);
        try
        {
            String invalidUri = service.getNamespaceURI("garbage");
            fail("Failed to catch invalid Prefix");
        }
        catch(NamespaceException e)
        {
        }
    }

    public void testGetPrefixesFromURI()
    {
        Collection<String> prefixes = service.getPrefixes(NamespaceService.alfresco_URI);
        assertNotNull(prefixes);
        assertEquals(1, prefixes.size());
        assertEquals(NamespaceService.alfresco_PREFIX, prefixes.toArray()[0]);
        
        try
        {
            Collection<String> invalidPrefixes = service.getPrefixes("garbage");
            fail("Failed to catch invalid URI");
        }
        catch(NamespaceException e)
        {
        }
    }
    
}
