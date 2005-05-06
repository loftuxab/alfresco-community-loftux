package org.alfresco.repo.dictionary.bootstrap;

import java.io.File;
import java.util.Collection;

import junit.framework.TestCase;

import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.dictionary.metamodel.M2PropertyType;
import org.alfresco.repo.dictionary.metamodel.emf.EMFMetaModelDAO;
import org.alfresco.repo.dictionary.metamodel.emf.EMFNamespaceDAO;
import org.alfresco.repo.dictionary.metamodel.emf.EMFResource;

/**
 * Test Dictionary Bootstrap
 * 
 * @author David Caruana
 */
public class BaseDictionaryTest extends TestCase
{
    private File tempFile;
    protected EMFResource resource;
    protected DictionaryBootstrap bootstrap;
    protected EMFNamespaceDAO namespaceDao;
    protected EMFMetaModelDAO metaModelDao;
    
    protected void setUp() throws Exception
    {
        // Construct Bootstrap Service
        resource = new EMFResource();
        tempFile = File.createTempFile("testBootstrap", ".xml");
        resource.setURI(tempFile.getAbsolutePath());
        resource.initCreate();

        // Construct EMF Namespace DAO
        namespaceDao = new EMFNamespaceDAO();
        namespaceDao.setResource(resource);
        namespaceDao.init();
        
        // Construct EMF Meta Model DAO
        metaModelDao = new EMFMetaModelDAO();
        metaModelDao.setResource(resource);
        metaModelDao.init();

        // Construct Bootstrap Service
        bootstrap = new DictionaryBootstrap();
        bootstrap.setNamespaceDAO(namespaceDao);
        bootstrap.setMetaModelDAO(metaModelDao);

        // Create test Bootstrap definitions
        bootstrap.bootstrapTestModel();
    }

    /**
     * Checks that the model can be saved and reloaded
     */
    public void testBootstrap()
    {
        // save the resource
        resource.save();
        // reinitialise
        resource = new EMFResource();
        resource.setURI("file://" + tempFile.getAbsolutePath());
        resource.init();
        
        // Construct DAO (to read boostrap namespace definitions)
        EMFNamespaceDAO namespaceDAO = new EMFNamespaceDAO();
        namespaceDAO.setResource(resource);
        namespaceDAO.init();

        // Ensure Namespaces exist
        Collection<String> uris = namespaceDAO.getURIs();
        assertNotNull(uris);
        assertEquals(3, uris.size());
        Collection<String> prefixes = namespaceDAO.getPrefixes();
        assertNotNull(uris);
        assertEquals(3, uris.size());
        
        // Construct DAO (to read boostrap meta model definitions)
        EMFMetaModelDAO metaModelDAO = new EMFMetaModelDAO();
        metaModelDAO.setResource(resource);
        metaModelDAO.init();
        
        // Ensure Property Types exist
        M2PropertyType propString = metaModelDAO.getPropertyType(PropertyTypeDefinition.TEXT);
        assertNotNull(propString);
        assertEquals(PropertyTypeDefinition.TEXT, propString.getQName());
        M2PropertyType propDate = metaModelDAO.getPropertyType(PropertyTypeDefinition.DATE);
        assertNotNull(propDate);
        assertEquals(PropertyTypeDefinition.DATE, propDate.getQName());
    }
}
