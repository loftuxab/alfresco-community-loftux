package com.activiti.repo.dictionary.bootstrap;

import junit.framework.TestCase;

import com.activiti.repo.dictionary.PropertyTypeDefinition;
import com.activiti.repo.dictionary.metamodel.M2PropertyType;
import com.activiti.repo.dictionary.metamodel.emf.EMFMetaModelDAO;


/**
 * Test Dictionary Bootstrap
 * 
 * @author David Caruana
 */
public class DictionaryBootstrapTest extends TestCase
{

    private DictionaryBootstrap bootstrap;
    
    
    protected void setUp() throws Exception
    {
        // Construct Bootstrap Service
        EMFMetaModelDAO bootstrapDao = new EMFMetaModelDAO();
        bootstrapDao.setResourceURI("w://datadictionary/src/com/activiti/repo/dictionary/metamodel/emf/testBootstrap.xml");
        bootstrapDao.initCreate();
        bootstrap = new DictionaryBootstrap();
        bootstrap.setMetaModelDAO(bootstrapDao);
    }

    
    public void testBootstrap()
    {
        // Create Bootstrap definitions
        bootstrap.bootstrap();
        
        // Construct Dictionary Service (to read boostrap definitions)
        EMFMetaModelDAO dao = new EMFMetaModelDAO();
        dao.setResourceURI("file:w://datadictionary/src/com/activiti/repo/dictionary/metamodel/emf/testBootstrap.xml");
        dao.init();
        
        // Ensure Property Types exist
        M2PropertyType propString = dao.getPropertyType(PropertyTypeDefinition.STRING);
        assertNotNull(propString);
        assertEquals(PropertyTypeDefinition.STRING, propString.getName());
        M2PropertyType propDate = dao.getPropertyType(PropertyTypeDefinition.DATE);
        assertNotNull(propDate);
        assertEquals(PropertyTypeDefinition.DATE, propDate.getName());
    }
    
}
