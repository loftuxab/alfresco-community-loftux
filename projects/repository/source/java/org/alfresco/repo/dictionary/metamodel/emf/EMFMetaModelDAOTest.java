package org.alfresco.repo.dictionary.metamodel.emf;

import java.util.Collection;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.bootstrap.BaseDictionaryTest;
import org.alfresco.repo.dictionary.metamodel.M2Aspect;
import org.alfresco.repo.dictionary.metamodel.M2Class;
import org.alfresco.repo.dictionary.metamodel.M2Property;
import org.alfresco.repo.dictionary.metamodel.M2Type;
import org.alfresco.repo.ref.QName;

/**
 * EMF Meta Model DAO Tests
 * 
 * @author David Caruana
 */
public class EMFMetaModelDAOTest extends BaseDictionaryTest
{
    public void testDAOGetTypes()
    {
        Collection qnames = metaModelDao.getTypes();
        assertNotNull(qnames);
        assertEquals(3, qnames.size());
    }
    
    public void testDAOGetClass()
    {
        QName fileQName = QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "file");
        M2Class m2FileClass = metaModelDao.getClass(fileQName);
        assertNotNull(m2FileClass);
        assertEquals(fileQName, m2FileClass.getQName());
        
        QName referenceQName = QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "referenceable");
        M2Class m2ReferenceClass = metaModelDao.getClass(referenceQName);
        assertNotNull(m2ReferenceClass);
        assertEquals(referenceQName, m2ReferenceClass.getQName());
    }

    public void testDAOGetType()
    {
        QName fileQName = QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "file");
        M2Type m2FileClass = metaModelDao.getType(fileQName);
        assertNotNull(m2FileClass);
        assertEquals(fileQName, m2FileClass.getQName());
        
        QName referenceQName = QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "referenceable");
        M2Type m2ReferenceClass = metaModelDao.getType(referenceQName);
        assertNull(m2ReferenceClass);
    }
    
    public void testDAOGetAspect()
    {
        QName referenceQName = QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "referenceable");
        M2Aspect m2ReferenceClass = metaModelDao.getAspect(referenceQName);
        assertNotNull(m2ReferenceClass);
        assertEquals(referenceQName, m2ReferenceClass.getQName());

        QName fileQName = QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "file");
        M2Aspect m2FileClass = metaModelDao.getAspect(fileQName);
        assertNull(m2FileClass);
    }

    public void testDAOGetProperty()
    {
        QName fileQName = QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "file");
        M2Property m2Property = metaModelDao.getProperty(fileQName, "encoding");
        assertNotNull(m2Property);
        assertEquals(QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "encoding"),
                m2Property.getPropertyDefinition().getQName());
    }
}
