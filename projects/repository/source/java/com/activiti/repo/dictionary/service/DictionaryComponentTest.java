package com.activiti.repo.dictionary.service;

import java.util.Map;

import junit.framework.TestCase;

import com.activiti.repo.dictionary.AssociationDefinition;
import com.activiti.repo.dictionary.AssociationRef;
import com.activiti.repo.dictionary.ChildAssociationDefinition;
import com.activiti.repo.dictionary.ClassDefinition;
import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.dictionary.DictionaryService;
import com.activiti.repo.dictionary.NamespaceService;
import com.activiti.repo.dictionary.PropertyDefinition;
import com.activiti.repo.dictionary.PropertyRef;
import com.activiti.repo.dictionary.TypeDefinition;
import com.activiti.repo.dictionary.metamodel.emf.EMFMetaModelDAO;
import com.activiti.repo.dictionary.metamodel.emf.EMFResource;
import com.activiti.repo.ref.QName;


/**
 * Data Dictionary Service Tests
 * 
 * @author David Caruana
 */
public class DictionaryComponentTest extends TestCase
{
    protected DictionaryService service;
    

    protected void setUp() throws Exception
    {
        EMFResource resource = new EMFResource();
        resource.setURI("classpath:/com/activiti/repo/dictionary/metamodel/emf/testBootstrap.xml");
        resource.init();
        EMFMetaModelDAO dao = new EMFMetaModelDAO();
        dao.setResource(resource);
        dao.init();
        DictionaryComponent component = new DictionaryComponent();
        component.setMetaModelDAO(dao);
        service = component;
    }


    public void testGetClassDefinition()
    {
        ClassRef fileRef = new ClassRef(QName.createQName(NamespaceService.ACTIVITI_TEST_URI, "file"));
        ClassDefinition fileClass = service.getClass(fileRef);
        assertNotNull(fileClass);
        assertTrue(fileClass instanceof TypeDefinition);
        assertEquals(fileRef, fileClass.getReference());
        assertEquals(false, fileClass.isAspect());
        assertEquals(QName.createQName(NamespaceService.ACTIVITI_TEST_URI, "base"), fileClass.getSuperClass().getQName());
        Map<PropertyRef,PropertyDefinition> fileProperties = fileClass.getProperties();
        assertNotNull(fileProperties);
        assertEquals(4, fileProperties.size());
        PropertyRef propRef = new PropertyRef(fileRef, "encoding");
        PropertyDefinition nameProp = fileProperties.get(propRef);
        assertNotNull(nameProp);
        assertEquals(propRef, nameProp.getReference());
        
        ClassRef folderRef = new ClassRef(QName.createQName(NamespaceService.ACTIVITI_TEST_URI, "folder"));
        ClassDefinition folderClass = service.getClass(folderRef);
        assertNotNull(folderClass);
        assertEquals(folderRef, folderClass.getReference());
        Map<AssociationRef,AssociationDefinition> folderAssocs = folderClass.getAssociations();
        assertNotNull(folderAssocs);
        assertEquals(1, folderAssocs.size());
        AssociationRef assocRef = new AssociationRef(folderRef, "contents");
        AssociationDefinition contentsAssoc = folderAssocs.get(assocRef);
        assertNotNull(contentsAssoc);
        assertTrue(contentsAssoc instanceof ChildAssociationDefinition);
        assertEquals(assocRef, contentsAssoc.getReference());
        assertEquals(true, contentsAssoc.isChild());
        ClassRef toClass = contentsAssoc.getRequiredToClasses().get(0);
        assertEquals(QName.createQName(NamespaceService.ACTIVITI_TEST_URI, "file"), toClass.getQName());
    }


    
}
