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
package org.alfresco.example.webservice.classification;

import javax.xml.rpc.ServiceException;

import junit.framework.AssertionFailedError;

import org.alfresco.example.webservice.BaseWebServiceSystemTest;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.FileProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClassificationServiceSystemTest extends BaseWebServiceSystemTest
{
   private static Log logger = LogFactory.getLog(ClassificationServiceSystemTest.class);
   
   private ClassificationServiceSoapBindingStub classificationService;
   

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();

      try 
      {
         EngineConfiguration config = new FileProvider(getResourcesDir(), "client-deploy.wsdd");
         this.classificationService = (ClassificationServiceSoapBindingStub)new ClassificationServiceLocator(config).getClassificationService();
      }
      catch (ServiceException jre) 
      {
         if (jre.getLinkedCause() != null)
         {
            jre.getLinkedCause().printStackTrace();
         }
         
         throw new AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
      }
      
      assertNotNull("contentService is null", this.classificationService);
      
      // Time out after a minute
      this.classificationService.setTimeout(60000);
   }
   
   /**
    * Tests the getClassifications service method
    * 
    * @throws Exception
    */
   public void testGetClassifications() throws Exception
   {
      try
      {
         this.classificationService.getClassifications();
         fail("This method should have thrown a classification fault");
      }
      catch (ClassificationFault cf)
      {
         // expected to get this
      }
   }
   
   /**
    * Tests the getChildCategories service method
    * 
    * @throws Exception
    */
   public void testGetChildCategories() throws Exception
   {
      try
      {
         this.classificationService.getChildCategories(null);
         fail("This method should have thrown a classification fault");
      }
      catch (ClassificationFault cf)
      {
         // expected to get this
      }
   }
   
   /**
    * Tests the getCategories service method
    * 
    * @throws Exception
    */
   public void testGetCategories() throws Exception
   {
      try
      {
         this.classificationService.getCategories(null);
         fail("This method should have thrown a classification fault");
      }
      catch (ClassificationFault cf)
      {
         // expected to get this
      }
   }
   
   /**
    * Tests the setCategories service method
    * 
    * @throws Exception
    */
   public void testSetCategories() throws Exception
   {
      try
      {
         this.classificationService.setCategories(null, null);
         fail("This method should have thrown a classification fault");
      }
      catch (ClassificationFault cf)
      {
         // expected to get this
      }
   }
   
   /**
    * Tests the describeClassification service method
    * 
    * @throws Exception
    */
   public void testDescribeClassification() throws Exception
   {
      try
      {
         this.classificationService.describeClassification(null);
         fail("This method should have thrown a classification fault");
      }
      catch (ClassificationFault cf)
      {
         // expected to get this
      }
   }
}
