package com.activiti.repo.dictionary.bootstrap;

import java.util.Collection;

import junit.framework.TestCase;

import com.activiti.repo.dictionary.PropertyTypeDefinition;
import com.activiti.repo.dictionary.metamodel.M2PropertyType;
import com.activiti.repo.dictionary.metamodel.emf.EMFMetaModelDAO;
import com.activiti.repo.dictionary.metamodel.emf.EMFNamespaceDAO;
import com.activiti.repo.dictionary.metamodel.emf.EMFResource;


/**
 * Test Dictionary Bootstrap
 * 
 * @author David Caruana
 */
public class DictionaryBootstrapTest extends TestCase
{

    private EMFResource resource = null;
    private DictionaryBootstrap bootstrap = null;
    
    
    protected void setUp() throws Exception
    {
        // Construct Bootstrap Service
        resource = new EMFResource();
        resource.setURI("W:/company-name/HEAD/projects/repository/source/java/com/activiti/repo/dictionary/metamodel/emf/testBootstrap.xml");
        resource.initCreate();

        // Construct EMF Namespace DAO
        EMFNamespaceDAO namespaceDao = new EMFNamespaceDAO();
        namespaceDao.setResource(resource);
        namespaceDao.init();
        
        // Construct EMF Meta Model DAO
        EMFMetaModelDAO metaModelDao = new EMFMetaModelDAO();
        metaModelDao.setResource(resource);
        metaModelDao.init();

        // Construct Bootstrap Service
        bootstrap = new DictionaryBootstrap();
        bootstrap.setNamespaceDAO(namespaceDao);
        bootstrap.setMetaModelDAO(metaModelDao);
        bootstrap.setCreateTestModel(true);
        bootstrap.setCreateVersionModel(true);
    }

    
    public void testBootstrap()
    {
        // Create Bootstrap definitions
        bootstrap.bootstrap();
        
        // Save EMF Resource
        resource.save();
        
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
        M2PropertyType propString = metaModelDAO.getPropertyType(PropertyTypeDefinition.STRING);
        assertNotNull(propString);
        assertEquals(PropertyTypeDefinition.STRING, propString.getName());
        M2PropertyType propDate = metaModelDAO.getPropertyType(PropertyTypeDefinition.DATE);
        assertNotNull(propDate);
        assertEquals(PropertyTypeDefinition.DATE, propDate.getName());
    }
    
}
