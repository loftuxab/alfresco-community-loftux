package org.alfresco.repo.dictionary.service;

import java.util.List;

import org.alfresco.repo.dictionary.AssociationDefinition;
import org.alfresco.repo.dictionary.ChildAssociationDefinition;
import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.dictionary.TypeDefinition;
import org.alfresco.repo.dictionary.bootstrap.BaseDictionaryTest;
import org.alfresco.repo.ref.QName;

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
        ClassRef fileRef = new ClassRef(QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "file"));
        ClassDefinition fileClass = service.getClass(fileRef);
        assertNotNull(fileClass);
        assertTrue(fileClass instanceof TypeDefinition);
        assertEquals(fileRef, fileClass.getReference());
        assertEquals(false, fileClass.isAspect());
        assertEquals(QName.createQName(NamespaceService.ALFRESCO_URI, "base"), fileClass.getSuperClass().getQName());
        List<PropertyDefinition> fileProperties = fileClass.getProperties();
        assertNotNull(fileProperties);
        assertEquals(4, fileProperties.size());
        PropertyDefinition encodingProp = fileClass.getProperty("encoding");
        assertNotNull("Encoding property node found class", encodingProp);
        
        ClassRef folderRef = new ClassRef(QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "folder"));
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
        assertEquals(QName.createQName(NamespaceService.ALFRESCO_TEST_URI, "file"), toClass.getQName());
    }
}
