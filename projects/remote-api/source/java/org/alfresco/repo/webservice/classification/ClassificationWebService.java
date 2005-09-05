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
package org.alfresco.repo.webservice.classification;

import java.rmi.RemoteException;

import org.alfresco.repo.webservice.AbstractWebService;
import org.alfresco.repo.webservice.types.Category;
import org.alfresco.repo.webservice.types.ClassDefinition;
import org.alfresco.repo.webservice.types.Classification;
import org.alfresco.repo.webservice.types.Predicate;
import org.alfresco.repo.webservice.types.Reference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Web service implementation of the ClassificationService.
 * The WSDL for this service can be accessed from http://localhost:8080/alfresco/wsdl/classification-service.wsdl
 *  
 * @author gavinc
 */
public class ClassificationWebService extends AbstractWebService implements ClassificationServiceSoapPort
{
   private static Log logger = LogFactory.getLog(ClassificationWebService.class);

   /**
    * @see org.alfresco.repo.webservice.classification.ClassificationServiceSoapPort#getClassifications()
    */
   public Classification[] getClassifications() throws RemoteException, ClassificationFault
   {
      throw new ClassificationFault(1, "getClassifications() is not implemented yet!");
   }

   /**
    * @see org.alfresco.repo.webservice.classification.ClassificationServiceSoapPort#getChildCategories(org.alfresco.repo.webservice.types.Reference)
    */
   public Category[] getChildCategories(Reference parentCategory) throws RemoteException, ClassificationFault
   {
      throw new ClassificationFault(1, "getChildCategories() is not implemented yet!");
   }

   /**
    * @see org.alfresco.repo.webservice.classification.ClassificationServiceSoapPort#getCategories(org.alfresco.repo.webservice.types.Predicate)
    */
   public CategoriesResult[] getCategories(Predicate items) throws RemoteException, ClassificationFault
   {
      throw new ClassificationFault(1, "getCategories() is not implemented yet!");
   }

   /**
    * @see org.alfresco.repo.webservice.classification.ClassificationServiceSoapPort#setCategories(org.alfresco.repo.webservice.types.Predicate, org.alfresco.repo.webservice.classification.AppliedCategory[])
    */
   public CategoriesResult[] setCategories(Predicate items, AppliedCategory[] categories) throws RemoteException, ClassificationFault
   {
      throw new ClassificationFault(1, "setCategories() is not implemented yet!");
   }

   /**
    * @see org.alfresco.repo.webservice.classification.ClassificationServiceSoapPort#describeClassification(org.alfresco.repo.webservice.types.Reference)
    */
   public ClassDefinition describeClassification(Reference classification) throws RemoteException, ClassificationFault
   {
      throw new ClassificationFault(1, "describeClassification() is not implemented yet!");
   }
}
