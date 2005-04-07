package com.activiti.repo.dictionary.metamodel.emf;

import java.util.Collection;

import junit.framework.TestCase;

import com.activiti.repo.dictionary.NamespaceService;
import com.activiti.repo.dictionary.metamodel.M2Aspect;
import com.activiti.repo.dictionary.metamodel.M2Class;
import com.activiti.repo.dictionary.metamodel.M2Property;
import com.activiti.repo.dictionary.metamodel.M2Type;
import com.activiti.repo.ref.QName;

/**
 * EMF Meta Model DAO Tests
 * 
 * @author David Caruana
 */
public class EMFMetaModelDAOTest extends TestCase
{

    private EMFMetaModelDAO dao = null;
    
    
    protected void setUp() throws Exception
    {
        // Create Resource
        EMFResource resource = new EMFResource();
        resource.setURI("classpath:/com/activiti/repo/dictionary/metamodel/emf/testModel.xml");
        resource.init();
        
        // Create DAO
        dao = new EMFMetaModelDAO();
        dao.setResource(resource);
        dao.init();
    }


    public void testDAOGetTypes()
    {
        Collection qnames = dao.getTypes();
        assertNotNull(qnames);
        assertEquals(3, qnames.size());
    }

    
    public void testDAOGetClass()
    {
        QName fileQName = QName.createQName(NamespaceService.ACTIVITI_TEST_URI, "file");
        M2Class m2FileClass = dao.getClass(fileQName);
        assertNotNull(m2FileClass);
        assertEquals(fileQName, m2FileClass.getName());
        
        QName referenceQName = QName.createQName(NamespaceService.ACTIVITI_TEST_URI, "referenceable");
        M2Class m2ReferenceClass = dao.getClass(referenceQName);
        assertNotNull(m2ReferenceClass);
        assertEquals(referenceQName, m2ReferenceClass.getName());
    }


    public void testDAOGetType()
    {
        QName fileQName = QName.createQName(NamespaceService.ACTIVITI_TEST_URI, "file");
        M2Type m2FileClass = dao.getType(fileQName);
        assertNotNull(m2FileClass);
        assertEquals(fileQName, m2FileClass.getName());
        
        QName referenceQName = QName.createQName(NamespaceService.ACTIVITI_TEST_URI, "referenceable");
        M2Type m2ReferenceClass = dao.getType(referenceQName);
        assertNull(m2ReferenceClass);
    }

    
    public void testDAOGetAspect()
    {
        QName referenceQName = QName.createQName(NamespaceService.ACTIVITI_TEST_URI, "referenceable");
        M2Aspect m2ReferenceClass = dao.getAspect(referenceQName);
        assertNotNull(m2ReferenceClass);
        assertEquals(referenceQName, m2ReferenceClass.getName());

        QName fileQName = QName.createQName(NamespaceService.ACTIVITI_TEST_URI, "file");
        M2Aspect m2FileClass = dao.getAspect(fileQName);
        assertNull(m2FileClass);
    }

    
    public void testDAOGetProperty()
    {
        QName fileQName = QName.createQName(NamespaceService.ACTIVITI_TEST_URI, "file");
        M2Property m2Property = dao.getProperty(fileQName, "encoding");
        assertNotNull(m2Property);
        assertEquals(QName.createQName(NamespaceService.ACTIVITI_TEST_URI, "file/encoding"), m2Property.getReference().getQName());
    }

}
