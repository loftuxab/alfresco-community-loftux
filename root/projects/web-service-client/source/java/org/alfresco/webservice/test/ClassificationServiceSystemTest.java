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

import java.io.InputStream;

import org.alfresco.webservice.action.Action;
import org.alfresco.webservice.classification.AppliedCategory;
import org.alfresco.webservice.classification.CategoriesResult;
import org.alfresco.webservice.classification.ClassificationServiceSoapBindingStub;
import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.Category;
import org.alfresco.webservice.types.ClassDefinition;
import org.alfresco.webservice.types.Classification;
import org.alfresco.webservice.types.ContentFormat;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.ContentUtils;
import org.alfresco.webservice.util.WebServiceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClassificationServiceSystemTest extends BaseWebServiceSystemTest
{
    private static Log logger = LogFactory
            .getLog(ClassificationServiceSystemTest.class);

    private ClassificationServiceSoapBindingStub classificationService;

    private static boolean categoriesLoaded = false;
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        this.classificationService = WebServiceFactory.getClassificationService();
        
        if (ClassificationServiceSystemTest.categoriesLoaded == false)
        {
            // Import categories into the new store
            InputStream viewStream = getClass().getClassLoader().getResourceAsStream("org/alfresco/webservice/test/resources/test.acp");
            byte[] byteArray = ContentUtils.convertToByteArray(viewStream);
            
            // Create the node that will contain the category import file
            ParentReference categoryParentRef = new ParentReference(BaseWebServiceSystemTest.store, BaseWebServiceSystemTest.rootReference.getUuid(), null, Constants.ASSOC_CHILDREN, "{test}testContent");
            NamedValue[] categoryProperties = new NamedValue[]{new NamedValue(Constants.PROP_NAME, false, "categories.acp", null)};
            CMLCreate createCategory = new CMLCreate("categoryImport", categoryParentRef, Constants.TYPE_CONTENT, categoryProperties);
            CML cml2 = new CML();
            cml2.setCreate(new CMLCreate[]{createCategory});            
            UpdateResult[] updateResult = this.repositoryService.update(cml2);
            Reference categoryReference = updateResult[0].getDestination();
            
            // Upload the content to the node
            this.contentService.write(
                    categoryReference, 
                    Constants.PROP_CONTENT, 
                    byteArray, 
                    new ContentFormat("application/acp", "UTF-8"));
            
            // Create an action to import the categories
            String rootNodeRef = store.getScheme().getValue() + "://" + store.getAddress() + "/" + rootReference.getUuid();
            Action importAction = new Action();
            importAction.setActionName("import");
            NamedValue[] params = new NamedValue[]{
                    new NamedValue("encoding", false, "UTF-8", null),
                    new NamedValue("destination", false, rootNodeRef, null)
            };
            importAction.setParameters(params);
            
            WebServiceFactory.getActionService().executeActions(
                    new Predicate(new Reference[]{categoryReference}, store, null),
                    new Action[]{importAction});
            
            ClassificationServiceSystemTest.categoriesLoaded = true;
        }
    }

    /**
     * Tests the getClassifications service method
     * 
     * @throws Exception
     */
    public void testGetClassifications() throws Exception
    {
        Classification[] classifications = this.classificationService
                .getClassifications(BaseWebServiceSystemTest.store);

        assertNotNull(classifications);
        assertTrue((classifications.length != 0));
        Classification classification = classifications[0];
        assertNotNull(classification.getTitle());
        assertNotNull(classification.getRootCategory());
        assertNotNull(classification.getRootCategory().getId());
        assertNotNull(classification.getRootCategory().getTitle());
        
        if (logger.isDebugEnabled() == true)
        {
            for (Classification item : classifications)
            {
                logger.debug(
                        "Classification '" +
                        item.getTitle() +
                        "' with root category '" +
                        item.getRootCategory().getTitle() +
                        "'");
            }
        }
    }

    /**
     * Tests the getChildCategories service method
     * 
     * @throws Exception
     */
    public void testGetChildCategories() throws Exception
    {
        Classification[] classifications = this.classificationService.getClassifications(BaseWebServiceSystemTest.store);
        Reference parentCategory = classifications[0].getRootCategory().getId();
        
        Category[] categories = this.classificationService.getChildCategories(parentCategory);
        assertNotNull(categories);
        assertTrue((categories.length != 0));
        Category item = categories[0];
        assertNotNull(item.getId());
        assertNotNull(item.getTitle());
        
        if (logger.isDebugEnabled() == true)
        {
            for (Category category : categories)
            {
                logger.debug(
                        "Sub-category '" +
                        category.getTitle() +
                        "'");
            }
        }
    }

    /**
     * Tests the getCategories and setCategories service methods
     * 
     * @throws Exception
     */
    public void testGetAndSetCategories() throws Exception
    {
        Classification[] classifications = this.classificationService.getClassifications(BaseWebServiceSystemTest.store);
        String classification = classifications[0].getClassification();
        Reference category = classifications[0].getRootCategory().getId();
        
        Reference reference = createContentAtRoot("TestContent" + System.currentTimeMillis(), "Any old content.");
        Predicate predicate = convertToPredicate(reference);
        
        // First try and get the categories for a uncategoried node
        CategoriesResult[] result1 = this.classificationService.getCategories(predicate);
        assertNotNull(result1);
        assertEquals(1, result1.length);
        assertNull(result1[0].getCategories());
        
        AppliedCategory appliedCategory = new AppliedCategory();
        appliedCategory.setCategories(new Reference[]{category});
        appliedCategory.setClassification(classification);
        
        AppliedCategory[] appliedCategories = new AppliedCategory[]{appliedCategory};
        
        // Now classify the node
        CategoriesResult[] result2 = this.classificationService.setCategories(predicate, appliedCategories);
        assertNotNull(result2);
        assertEquals(1, result2.length);
        
        // Now get the value back
        CategoriesResult[] result3 = this.classificationService.getCategories(predicate);
        assertNotNull(result3);
        assertEquals(1, result3.length);
        CategoriesResult categoryResult = result3[0];
        assertEquals(reference.getUuid(), categoryResult.getNode().getUuid());
        AppliedCategory[] appCats = categoryResult.getCategories();
        assertNotNull(appCats);
        assertEquals(1, appCats.length);
        AppliedCategory appCat = appCats[0];
        assertEquals(classification, appCat.getClassification());
        Reference[] refs = appCat.getCategories();
        assertNotNull(refs);
        assertEquals(1, refs.length);
        Reference ref = refs[0];
        assertEquals(category.getUuid(), ref.getUuid());
        
        // TODO test multiple classifiations 
        // TODO test clearing the classifications
        // TODO test updating the classifications
    }


    /**
     * Tests the describeClassification service method
     * 
     * @throws Exception
     */
    public void testDescribeClassification() throws Exception
    {
        Classification[] classifications = this.classificationService.getClassifications(BaseWebServiceSystemTest.store);
        String classification = classifications[0].getClassification();
        
        ClassDefinition classDefinition = this.classificationService.describeClassification(classification);
        
        assertNotNull(classDefinition);
        
        // TODO test the result more rigiously
    }
}
