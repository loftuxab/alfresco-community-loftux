/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.dictionary;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.InvalidTypeException;
import org.alfresco.service.namespace.QName;


public class DictionaryDAOTest extends TestCase
{
    
    private static final String TEST_MODEL = "org/alfresco/repo/dictionary/dictionarydaotest_model.xml";
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
        bootstrapModels.add("alfresco/model/dictionaryModel.xml");
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
        bootstrapModels.add("alfresco/model/dictionaryModel.xml");
        bootstrapModels.add("alfresco/model/systemModel.xml");
        bootstrapModels.add("alfresco/model/contentModel.xml");
        bootstrapModels.add("alfresco/model/applicationModel.xml");

        bootstrapModels.add("org/alfresco/repo/security/authentication/userModel.xml");
        bootstrapModels.add("org/alfresco/repo/action/actionModel.xml");
        bootstrapModels.add("org/alfresco/repo/rule/ruleModel.xml");
        bootstrapModels.add("org/alfresco/repo/version/version_model.xml");
        
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
            service.isSubClass(invalid, referencable);
            fail("Failed to catch invalid class parameter");
        }
        catch(InvalidTypeException e) {}

        try
        {
            service.isSubClass(referencable, invalid);
            fail("Failed to catch invalid class parameter");
        }
        catch(InvalidTypeException e) {}

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
