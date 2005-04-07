package com.activiti.repo.dictionary.service;

import java.util.Collection;

import junit.framework.TestCase;

import com.activiti.repo.dictionary.NamespaceService;
import com.activiti.repo.dictionary.metamodel.emf.EMFNamespaceDAO;
import com.activiti.repo.dictionary.metamodel.emf.EMFResource;
import com.activiti.repo.ref.NamespaceException;


/**
 * Data Dictionary Service Tests
 * 
 * @author David Caruana
 */
public class DictionaryNamespaceComponentTest extends TestCase
{

    private DictionaryNamespaceComponent service;
    
    
    protected void setUp() throws Exception
    {
        EMFResource resource = new EMFResource();
        resource.setURI("classpath:/com/activiti/repo/dictionary/metamodel/emf/testModel.xml");
        resource.init();
        EMFNamespaceDAO dao = new EMFNamespaceDAO();
        dao.setResource(resource);
        dao.init();
        service = new DictionaryNamespaceComponent();
        service.setNamespaceDAO(dao);
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
        String uri = service.getNamespaceURI(NamespaceService.ACTIVITI_PREFIX);
        assertNotNull(uri);
        assertEquals(NamespaceService.ACTIVITI_URI, uri);
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
        Collection<String> prefixes = service.getPrefixes(NamespaceService.ACTIVITI_URI);
        assertNotNull(prefixes);
        assertEquals(1, prefixes.size());
        assertEquals(NamespaceService.ACTIVITI_PREFIX, prefixes.toArray()[0]);
        
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
