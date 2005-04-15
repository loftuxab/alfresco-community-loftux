package com.activiti.repo.dictionary.service;

import java.util.List;

import com.activiti.repo.dictionary.AssociationDefinition;
import com.activiti.repo.dictionary.ChildAssociationDefinition;
import com.activiti.repo.dictionary.ClassDefinition;
import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.dictionary.DictionaryService;
import com.activiti.repo.dictionary.NamespaceService;
import com.activiti.repo.dictionary.PropertyDefinition;
import com.activiti.repo.dictionary.TypeDefinition;
import com.activiti.repo.dictionary.bootstrap.BaseDictionaryTest;
import com.activiti.repo.ref.QName;

/**
 * Data Dictionary Service Tests
 * 
 * @author David Caruana
 */
public class DictionaryComponentTest extends BaseDictionaryTest
{
    protected DictionaryService service;
    
    protected void setUp() throws Exception
    {
        // ensure model is loaded
        super.setUp();
        
        DictionaryComponent component = new DictionaryComponent();
        component.setMetaModelDAO(super.metaModelDao);
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
        assertEquals(QName.createQName(NamespaceService.ACTIVITI_URI, "base"), fileClass.getSuperClass().getQName());
        List<PropertyDefinition> fileProperties = fileClass.getProperties();
        assertNotNull(fileProperties);
        assertEquals(4, fileProperties.size());
        PropertyDefinition encodingProp = fileClass.getProperty("encoding");
        assertNotNull("Encoding property node found class", encodingProp);
        
        ClassRef folderRef = new ClassRef(QName.createQName(NamespaceService.ACTIVITI_TEST_URI, "folder"));
        ClassDefinition folderClass = service.getClass(folderRef);
        assertNotNull(folderClass);
        assertEquals(folderRef, folderClass.getReference());
        List<AssociationDefinition> folderAssocs = folderClass.getAssociations();
        assertNotNull(folderAssocs);
        assertEquals(1, folderAssocs.size());
        AssociationDefinition contentsAssoc = folderClass.getAssociation("contents");
        assertNotNull("contents association not found", contentsAssoc);
        assertTrue("Incorrect type for contents association",
                contentsAssoc instanceof ChildAssociationDefinition);
        assertEquals(true, contentsAssoc.isChild());
        ClassDefinition toClass = contentsAssoc.getRequiredToClasses().get(0);
        assertEquals(QName.createQName(NamespaceService.ACTIVITI_TEST_URI, "file"), toClass.getQName());
    }
}
