package com.activiti.repo.dictionary.service;

import java.util.Map;

import junit.framework.TestCase;

import com.activiti.repo.dictionary.AssociationDefinition;
import com.activiti.repo.dictionary.AssociationRef;
import com.activiti.repo.dictionary.ChildAssociationDefinition;
import com.activiti.repo.dictionary.ClassDefinition;
import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.dictionary.PropertyDefinition;
import com.activiti.repo.dictionary.PropertyRef;
import com.activiti.repo.dictionary.TypeDefinition;
import com.activiti.repo.dictionary.metamodel.emf.EMFMetaModelDAO;
import com.activiti.repo.ref.QName;


public class DictionaryServiceImplTest extends TestCase
{

    private DictionaryServiceImpl service;
    
    
    protected void setUp() throws Exception
    {
        EMFMetaModelDAO dao = new EMFMetaModelDAO();
        dao.setResourceURI("classpath:/com/activiti/repo/dictionary/metamodel/emf/testModel.xml");
        dao.init();
        service = new DictionaryServiceImpl();
        service.setMetaModelDAO(dao);
    }

    
    public void testGetClassDefinition()
    {
        ClassRef fileRef = new ClassRef(QName.createQName("test", "file"));
        ClassDefinition fileClass = service.getClass(fileRef);
        assertNotNull(fileClass);
        assertTrue(fileClass instanceof TypeDefinition);
        assertEquals(fileRef, fileClass.getReference());
        assertEquals(false, fileClass.isAspect());
        assertEquals(QName.createQName("test", "base"), fileClass.getSuperClass().getQName());
        Map fileProperties = fileClass.getProperties();
        assertNotNull(fileProperties);
        assertEquals(4, fileProperties.size());
        PropertyRef propRef = new PropertyRef(fileRef, "encoding");
        PropertyDefinition nameProp = (PropertyDefinition)fileProperties.get(propRef);
        assertNotNull(nameProp);
        assertEquals(propRef, nameProp.getReference());
        
        ClassRef folderRef = new ClassRef(QName.createQName("test", "folder"));
        ClassDefinition folderClass = service.getClass(folderRef);
        assertNotNull(folderClass);
        assertEquals(folderRef, folderClass.getReference());
        Map folderAssocs = folderClass.getAssociations();
        assertNotNull(folderAssocs);
        assertEquals(1, folderAssocs.size());
        AssociationRef assocRef = new AssociationRef(folderRef, "contents");
        AssociationDefinition contentsAssoc = (AssociationDefinition)folderAssocs.get(assocRef);
        assertNotNull(contentsAssoc);
        assertTrue(contentsAssoc instanceof ChildAssociationDefinition);
        assertEquals(assocRef, contentsAssoc.getReference());
        assertEquals(true, contentsAssoc.isChild());
        ClassRef toClass = (ClassRef)contentsAssoc.getRequiredToClasses().get(0);
        assertEquals(QName.createQName("test", "file"), toClass.getQName());
    }

    
}
