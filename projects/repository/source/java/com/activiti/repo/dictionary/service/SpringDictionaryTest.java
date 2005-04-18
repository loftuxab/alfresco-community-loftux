package com.activiti.repo.dictionary.service;

import java.util.Collection;

import com.activiti.repo.dictionary.ClassDefinition;
import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.dictionary.DictionaryService;
import com.activiti.repo.dictionary.NamespaceService;
import com.activiti.repo.dictionary.TypeDefinition;
import com.activiti.repo.dictionary.bootstrap.DictionaryBootstrap;
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
        ClassRef contentRef = DictionaryBootstrap.TYPE_CONTENT;
        ClassDefinition fileClass = dictionaryService.getClass(contentRef);
        assertNotNull(fileClass);
        assertTrue(fileClass instanceof TypeDefinition);
        assertEquals(contentRef, fileClass.getReference());
        assertEquals(false, fileClass.isAspect());
        assertEquals(DictionaryBootstrap.TYPE_QNAME_BASE, fileClass.getSuperClass().getQName());
    }

    
    public void testNamespace()
    {
        Collection<String> prefixes = namespaceService.getPrefixes();
        assertNotNull(prefixes);
        assertEquals(3, prefixes.size());
    }
    
    
}
