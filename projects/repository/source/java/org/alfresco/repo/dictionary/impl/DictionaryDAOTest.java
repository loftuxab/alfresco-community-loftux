package org.alfresco.repo.dictionary.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.node.InvalidNodeTypeException;
import org.alfresco.repo.ref.QName;


public class DictionaryDAOTest extends TestCase
{
    
    private static final String TEST_MODEL = "org/alfresco/repo/dictionary/impl/dictionarydaotest_model.xml";
    private DictionaryService service; 
    
    
    @Override
    public void setUp()
    {
        // Load Test Model
        InputStream modelStream = getClass().getClassLoader().getResourceAsStream(TEST_MODEL);
        M2Model model = M2Model.createModel(modelStream);

        // Instantiate Dictionary Service
        NamespaceDAO namespaceDAO = new NamespaceDAOImpl();
        DictionaryDAOImpl dictionaryDAO = new DictionaryDAOImpl(namespaceDAO);
        List<String> bootstrapModels = new ArrayList<String>();
        bootstrapModels.add("org/alfresco/repo/dictionary/impl/dictionary_model.xml");
        dictionaryDAO.setBootstrapModels(bootstrapModels);
        dictionaryDAO.bootstrap();
        dictionaryDAO.putModel(model);
        
        DictionaryComponent component = new DictionaryComponent();
        component.setDictionaryDAO(dictionaryDAO);
        service = component;
    }
    

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

    
    public void testSubClassOf()
    {
        QName invalid = QName.createQName("http://www.alfresco.org/test/dictionarydaotest/1.0", "invalid");
        QName base = QName.createQName("http://www.alfresco.org/test/dictionarydaotest/1.0", "base");
        QName file = QName.createQName("http://www.alfresco.org/test/dictionarydaotest/1.0", "file");
        QName folder = QName.createQName("http://www.alfresco.org/test/dictionarydaotest/1.0", "folder");
        QName referencable = QName.createQName("http://www.alfresco.org/test/dictionarydaotest/1.0", "referencable");

        // Test invalid args
        try
        {
            boolean test = service.isSubClass(invalid, referencable);
            fail("Failed to catch invalid class parameter");
        }
        catch(InvalidNodeTypeException e) {}

        try
        {
            boolean test = service.isSubClass(referencable, invalid);
            fail("Failed to catch invalid class parameter");
        }
        catch(InvalidNodeTypeException e) {}

        // Test various flavours of subclassof
        boolean test1 = service.isSubClass(file, referencable);  // type vs aspect
        assertFalse(test1);
        boolean test2 = service.isSubClass(file, folder);   // seperate hierarchies
        assertFalse(test2);
        boolean test3 = service.isSubClass(file, file);   // self
        assertTrue(test3);
        boolean test4 = service.isSubClass(folder, base);  // subclass
        assertTrue(test4);
        boolean test5 = service.isSubClass(base, folder);  // reversed test
        assertFalse(test5);
    }
    
    
}
