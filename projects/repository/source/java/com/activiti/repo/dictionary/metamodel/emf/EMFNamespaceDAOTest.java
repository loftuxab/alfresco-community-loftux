package com.activiti.repo.dictionary.metamodel.emf;

import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import com.activiti.repo.dictionary.NamespaceService;
import com.activiti.repo.dictionary.metamodel.M2NamespacePrefix;
import com.activiti.repo.dictionary.metamodel.M2NamespaceURI;

/**
 * EMF Meta Model DAO Tests
 * 
 * @author David Caruana
 */
public class EMFNamespaceDAOTest extends TestCase
{

    private EMFNamespaceDAO dao = null;
    
    
    protected void setUp() throws Exception
    {
        // Create Resource
        EMFResource resource = new EMFResource();
        resource.setURI("classpath:/com/activiti/repo/dictionary/metamodel/emf/testModel.xml");
        resource.init();
        
        // Create DAO
        dao = new EMFNamespaceDAO();
        dao.setResource(resource);
        dao.init();
    }


    public void testDAOGetURIs()
    {
        Collection<String> uris = dao.getURIs();
        assertNotNull(uris);
        assertEquals(3, uris.size());
        
        Collection<String> prefixes = dao.getPrefixes();
        assertNotNull(prefixes);
        assertEquals(3, prefixes.size());
    }

    
    public void testDAOGetURI()
    {
        M2NamespaceURI uri = dao.getURI(NamespaceService.DEFAULT_URI);
        assertNotNull(uri);
        assertEquals(NamespaceService.DEFAULT_URI, uri.getURI());
        List<M2NamespacePrefix> prefixes = uri.getPrefixes();
        assertNotNull(prefixes);
        assertEquals(1, prefixes.size());
        M2NamespacePrefix prefix = prefixes.get(0);
        assertEquals(NamespaceService.DEFAULT_PREFIX, prefix.getPrefix());
        M2NamespaceURI invalidUri = dao.getURI("garbage");
        assertNull(invalidUri);
    }

    
    public void testDAOGetPrefix()
    {
        M2NamespacePrefix prefix = dao.getPrefix(NamespaceService.DEFAULT_PREFIX);
        assertNotNull(prefix);
        assertEquals(NamespaceService.DEFAULT_PREFIX, prefix.getPrefix());
        M2NamespaceURI uri = prefix.getURI();
        assertNotNull(uri);
        assertEquals(NamespaceService.DEFAULT_URI, uri.getURI());
        M2NamespacePrefix invalidPrefix = dao.getPrefix("garbage");
        assertNull(invalidPrefix);
    }

}
