package com.activiti.repo.dictionary.metamodel.emf;

import java.util.Collection;

import junit.framework.TestCase;

import com.activiti.repo.dictionary.metamodel.M2Aspect;
import com.activiti.repo.dictionary.metamodel.M2Class;
import com.activiti.repo.dictionary.metamodel.M2Property;
import com.activiti.repo.dictionary.metamodel.M2Type;
import com.activiti.repo.ref.QName;

public class EMFMetaModelDAOTest extends TestCase
{

    public void testDAOGetTypes()
    {
        EMFMetaModelDAO dao = new EMFMetaModelDAO();
        dao.setResourceURI("classpath:/com/activiti/repo/dictionary/metamodel/emf/testModel.xml");
        dao.init();
        
        Collection qnames = dao.getTypes();
        assertNotNull(qnames);
        assertEquals(3, qnames.size());
    }

    
    public void testDAOGetClass()
    {
        EMFMetaModelDAO dao = new EMFMetaModelDAO();
        dao.setResourceURI("classpath:/com/activiti/repo/dictionary/metamodel/emf/testModel.xml");
        dao.init();
        
        QName fileQName = QName.createQName("test", "file");
        M2Class m2FileClass = dao.getClass(fileQName);
        assertNotNull(m2FileClass);
        assertEquals(fileQName, m2FileClass.getName());
        
        QName referenceQName = QName.createQName("test", "referenceable");
        M2Class m2ReferenceClass = dao.getClass(referenceQName);
        assertNotNull(m2ReferenceClass);
        assertEquals(referenceQName, m2ReferenceClass.getName());
    }


    public void testDAOGetType()
    {
        EMFMetaModelDAO dao = new EMFMetaModelDAO();
        dao.setResourceURI("classpath:/com/activiti/repo/dictionary/metamodel/emf/testModel.xml");
        dao.init();
        
        QName fileQName = QName.createQName("test", "file");
        M2Type m2FileClass = dao.getType(fileQName);
        assertNotNull(m2FileClass);
        assertEquals(fileQName, m2FileClass.getName());
        
        QName referenceQName = QName.createQName("test", "referenceable");
        M2Type m2ReferenceClass = dao.getType(referenceQName);
        assertNull(m2ReferenceClass);
    }

    
    public void testDAOGetAspect()
    {
        EMFMetaModelDAO dao = new EMFMetaModelDAO();
        dao.setResourceURI("classpath:/com/activiti/repo/dictionary/metamodel/emf/testModel.xml");
        dao.init();
        
        QName referenceQName = QName.createQName("test", "referenceable");
        M2Aspect m2ReferenceClass = dao.getAspect(referenceQName);
        assertNotNull(m2ReferenceClass);
        assertEquals(referenceQName, m2ReferenceClass.getName());

        QName fileQName = QName.createQName("test", "file");
        M2Aspect m2FileClass = dao.getAspect(fileQName);
        assertNull(m2FileClass);
    }

    
    public void testDAOGetProperty()
    {
        EMFMetaModelDAO dao = new EMFMetaModelDAO();
        dao.setResourceURI("classpath:/com/activiti/repo/dictionary/metamodel/emf/testModel.xml");
        dao.init();
        
        QName fileQName = QName.createQName("test", "file");
        M2Property m2Property = dao.getProperty(fileQName, "encoding");
        assertNotNull(m2Property);
        assertEquals(QName.createQName("test", "file/encoding"), m2Property.getReference().getQName());
    }

    
    
}
