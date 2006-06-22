/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.webservice.test;

import org.alfresco.webservice.dictionary.ClassPredicate;
import org.alfresco.webservice.types.ClassDefinition;
import org.alfresco.webservice.util.WebServiceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class DictionaryServiceSystemTest extends BaseWebServiceSystemTest
{
    private static Log logger = LogFactory.getLog(DictionaryServiceSystemTest.class);

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    public void testGetClasses() throws Exception
    {
        ClassDefinition[] classDefs = WebServiceFactory.getDictionaryService().getClasses(null, null);
        assertNotNull(classDefs);
        assertTrue(classDefs.length >= 1);
    }

    public void testSingleTypePredicate() throws Exception
    {
        ClassPredicate types = new ClassPredicate(new String[] {"cm:content"}, false, false);
        ClassPredicate aspects = new ClassPredicate(new String[] {}, false, false);
        
        ClassDefinition[] classDefs = WebServiceFactory.getDictionaryService().getClasses(types, aspects);
        assertNotNull(classDefs);
        assertEquals(1, classDefs.length);
        assertEquals("{http://www.alfresco.org/model/content/1.0}content", classDefs[0].getName());
    }

    public void testSingleAspectPredicate() throws Exception
    {
        ClassPredicate types = new ClassPredicate(new String[] {}, false, false);
        ClassPredicate aspects = new ClassPredicate(new String[] {"cm:auditable"}, false, false);
        
        ClassDefinition[] classDefs = WebServiceFactory.getDictionaryService().getClasses(types, aspects);
        assertNotNull(classDefs);
        assertEquals(1, classDefs.length);
        assertEquals("{http://www.alfresco.org/model/content/1.0}auditable", classDefs[0].getName());
    }
    
    public void testSingleTypeAspectPredicate() throws Exception
    {
        ClassPredicate types = new ClassPredicate(new String[] {"cm:content"}, false, false);
        ClassPredicate aspects = new ClassPredicate(new String[] {"cm:auditable"}, false, false);
        
        ClassDefinition[] classDefs = WebServiceFactory.getDictionaryService().getClasses(types, aspects);
        assertNotNull(classDefs);
        assertEquals(2, classDefs.length);
    }
    
    public void testSingleTypeAllAspectsPredicate() throws Exception
    {
        ClassPredicate types = new ClassPredicate(new String[] {"cm:content"}, false, false);
        ClassDefinition[] classDefs = WebServiceFactory.getDictionaryService().getClasses(types, null);
        assertNotNull(classDefs);
        assertTrue(classDefs.length > 1);
    }

    public void testSingleAspectAllTypesPredicate() throws Exception
    {
        ClassPredicate aspects = new ClassPredicate(new String[] {"cm:auditable"}, false, false);
        ClassDefinition[] classDefs = WebServiceFactory.getDictionaryService().getClasses(null, aspects);
        assertNotNull(classDefs);
        assertTrue(classDefs.length > 1);
    }

    public void testTypeWithSubTypesPredicate() throws Exception
    {
        ClassPredicate types = new ClassPredicate(new String[] {"cm:content"}, true, false);
        ClassDefinition[] classDefs = WebServiceFactory.getDictionaryService().getClasses(types, null);
        assertNotNull(classDefs);
        assertTrue(classDefs.length > 1);
    }
}
