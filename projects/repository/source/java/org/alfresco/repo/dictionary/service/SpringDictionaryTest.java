package org.alfresco.repo.dictionary.service;

import java.util.Collection;

import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.alfresco.repo.dictionary.AspectDefinition;
import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.DictionaryRef;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.dictionary.PropertyRef;
import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.dictionary.TypeDefinition;
import org.alfresco.repo.dictionary.bootstrap.DictionaryBootstrap;
import org.alfresco.util.BaseSpringTest;

public class SpringDictionaryTest extends BaseSpringTest
{
    private DictionaryService dictionaryService = null;
    private NamespaceService namespaceService = null;
    
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    public void testDictionary()
    {
        ClassRef contentRef = DictionaryBootstrap.TYPE_CONTENT;
        ClassDefinition contentClass = dictionaryService.getClass(contentRef);
        assertNotNull(contentClass);
        assertTrue(contentClass instanceof TypeDefinition);
        assertEquals(contentRef, contentClass.getReference());
        assertEquals(false, contentClass.isAspect());
        assertEquals("Content type is not derived from base type", 
                DictionaryBootstrap.TYPE_QNAME_BASE,
                contentClass.getSuperClass().getQName());
        
        ClassRef fileRef = DictionaryBootstrap.TYPE_FILE;
        ClassDefinition fileClass = dictionaryService.getClass(fileRef);
        assertNotNull(fileClass);
        assertTrue(contentClass instanceof TypeDefinition);
        assertEquals(false, contentClass.isAspect());
        assertEquals("File type is not derived from content type",
                DictionaryBootstrap.TYPE_QNAME_CONTENT,
                fileClass.getSuperClass().getQName());
    }

    public void testCategoryTypes()
    {
        ClassRef rootRef = DictionaryBootstrap.ASPECT_ROOT;
        AspectDefinition rootAspect = dictionaryService.getAspect(rootRef);
        assertNotNull(rootAspect);
        assertEquals(rootRef, rootAspect.getReference());
        assertEquals(0, rootAspect.getAssociations().size());
        assertEquals(0, rootAspect.getChildAssociations().size());
        assertEquals(0, rootAspect.getProperties().size());
        
        ClassRef storeRootRef = DictionaryBootstrap.TYPE_STOREROOT;
        TypeDefinition storeRootType = dictionaryService.getType(storeRootRef);
        assertNotNull(storeRootType);
        assertEquals(storeRootRef, storeRootType.getReference());
        assertEquals(1, storeRootType.getAssociations().size());
        assertEquals(1, storeRootType.getChildAssociations().size());
        assertEquals(0, storeRootType.getProperties().size());
        
        ClassRef categoryRootRef = DictionaryBootstrap.TYPE_CATEGORYROOT;
        TypeDefinition categoryRootType = dictionaryService.getType(categoryRootRef);
        assertNotNull(categoryRootType);
        assertEquals(categoryRootRef, categoryRootType.getReference());
        assertEquals(1, categoryRootType.getAssociations().size());
        assertEquals(1, categoryRootType.getChildAssociations().size());
        assertEquals(0, categoryRootType.getProperties().size());
        
        ClassRef categoryRef = DictionaryBootstrap.TYPE_CATEGORY;
        TypeDefinition categoryType = dictionaryService.getType(categoryRef);
        assertNotNull(categoryType);
        assertEquals(categoryRef, categoryType.getReference());
        assertEquals(1, categoryType.getAssociations().size());
        assertEquals(1, categoryType.getChildAssociations().size());
        assertEquals(0, categoryType.getProperties().size());
        
    }
    
    public void testIndexingProperties()
    {
        // Check types know about thier tokeniser - we should change to analyser ...
        DictionaryRef ref = new DictionaryRef(PropertyTypeDefinition.TEXT);
        PropertyTypeDefinition textType = dictionaryService.getPropertyType(ref);
        assertNotNull(textType);
        assertEquals(StandardAnalyzer.class.getName(), textType.getAnalyserClassName());
        
        // Find a property definition by Qname
        PropertyRef propRef = new PropertyRef(new ClassRef(DictionaryBootstrap.ASPECT_QNAME_CONTENT), DictionaryBootstrap.PROP_ENCODING);
        PropertyDefinition propDef = dictionaryService.getProperty(propRef.getQName());
        assertNotNull(propDef);
        assertEquals(true, propDef.isIndexed());
        assertEquals(true, propDef.isIndexedAtomically());
        assertEquals(false, propDef.isStoredInIndex());
        assertEquals(false, propDef.isTokenisedInIndex());
        
        propDef = dictionaryService.getProperty(propRef);
        assertNotNull(propDef);
        assertEquals(true, propDef.isIndexed());
        assertEquals(true, propDef.isIndexedAtomically());
        assertEquals(false, propDef.isStoredInIndex());
        assertEquals(false, propDef.isTokenisedInIndex());
        
        
        
    }
    
    public void testNamespace()
    {
        Collection<String> prefixes = namespaceService.getPrefixes();
        assertNotNull(prefixes);
        assertEquals(3, prefixes.size());
    }
}
