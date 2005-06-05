package org.alfresco.repo.dictionary.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;


public class DictionaryDAOTest extends TestCase
{
    
    public void testBootstrap()
    {
        NamespaceDAO namespaceDAO = new NamespaceDAOImpl();
        DictionaryDAOImpl dictionaryDAO = new DictionaryDAOImpl(namespaceDAO);
        
        List<String> bootstrapModels = new ArrayList<String>();
        bootstrapModels.add("org/alfresco/repo/dictionary/impl/dictionary_model.xml");
        bootstrapModels.add("org/alfresco/repo/dictionary/impl/content_model.xml");
        bootstrapModels.add("org/alfresco/repo/dictionary/impl/version_model.xml");
        dictionaryDAO.setBootstrapModels(bootstrapModels);
        dictionaryDAO.bootstrap();
    }

}
