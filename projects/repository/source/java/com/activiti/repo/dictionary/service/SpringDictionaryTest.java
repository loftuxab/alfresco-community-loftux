package com.activiti.repo.dictionary.service;

import java.util.Collection;

import com.activiti.repo.dictionary.ClassDefinition;
import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.dictionary.DictionaryService;
import com.activiti.repo.dictionary.NamespaceService;
import com.activiti.repo.dictionary.TypeDefinition;
import com.activiti.repo.ref.QName;
import com.activiti.util.BaseSpringTest;

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
        ClassRef fileRef = new ClassRef(QName.createQName(NamespaceService.ACTIVITI_TEST_URI, "file"));
        ClassDefinition fileClass = dictionaryService.getClass(fileRef);
        assertNotNull(fileClass);
        assertTrue(fileClass instanceof TypeDefinition);
        assertEquals(fileRef, fileClass.getReference());
        assertEquals(false, fileClass.isAspect());
        assertEquals(QName.createQName(NamespaceService.ACTIVITI_TEST_URI, "base"), fileClass.getSuperClass().getQName());
    }

    
    public void testNamespace()
    {
        Collection<String> prefixes = namespaceService.getPrefixes();
        assertNotNull(prefixes);
        assertEquals(3, prefixes.size());
    }
    
    
}
