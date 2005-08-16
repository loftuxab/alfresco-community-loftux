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
package org.alfresco.repo.webservice.content;

import java.rmi.RemoteException;

import org.alfresco.repo.webservice.types.Content;
import org.alfresco.repo.webservice.types.ContentFormat;
import org.alfresco.repo.webservice.types.ParentReference;
import org.alfresco.repo.webservice.types.Predicate;
import org.alfresco.repo.webservice.types.Reference;
import org.alfresco.service.cmr.repository.ContentService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Web service implementation of the ContentService.
 * The WSDL for this service can be accessed from http://localhost:8080/alfresco/api/ContentService?wsdl
 *  
 * @author gavinc
 */
public class ContentWebService implements ContentServiceSoapPort
{
   private static Log logger = LogFactory.getLog(ContentWebService.class);
   
   private ContentService contentService;
   
   /**
    * Sets the ContentService instance to use
    * 
    * @param contentSvc The ContentService
    */
   public void setContentService(ContentService contentSvc)
   {
      this.contentService = contentSvc;
   }

   /**
    * @see org.alfresco.repo.webservice.content.ContentServiceSoapPort#describe(org.alfresco.repo.webservice.types.Predicate[])
    */
   public Content[] describe(Predicate[] items) throws RemoteException, ContentFault
   {
      throw new ContentFault(1, "describe() is not implemented yet!");
   }

   /**
    * @see org.alfresco.repo.webservice.content.ContentServiceSoapPort#read(org.alfresco.repo.webservice.types.Reference)
    */
   public byte[] read(Reference node) throws RemoteException, ContentFault
   {
      throw new ContentFault(1, "read() is not implemented yet!");
   }

   /**
    * @see org.alfresco.repo.webservice.content.ContentServiceSoapPort#readChunk(org.alfresco.repo.webservice.types.Reference, org.alfresco.repo.webservice.content.ContentSegment)
    */
   public ReadResult readChunk(Reference node, ContentSegment segment) throws RemoteException, ContentFault
   {
      throw new ContentFault(1, "readChunk() is not implemented yet!");
   }

   /**
    * @see org.alfresco.repo.webservice.content.ContentServiceSoapPort#readNext(java.lang.String)
    */
   public ReadResult readNext(String readSession) throws RemoteException, ContentFault
   {
      throw new ContentFault(1, "readNext() is not implemented yet!");
   }

   /**
    * @see org.alfresco.repo.webservice.content.ContentServiceSoapPort#write(org.alfresco.repo.webservice.types.Reference, byte[])
    */
   public void write(Reference node, byte[] content) throws RemoteException, ContentFault
   {
      throw new ContentFault(1, "write() is not implemented yet!");
   }

   /**
    * @see org.alfresco.repo.webservice.content.ContentServiceSoapPort#create(org.alfresco.repo.webservice.types.ParentReference, org.alfresco.repo.webservice.types.ContentFormat, byte[])
    */
   public Content create(ParentReference parent, ContentFormat format, byte[] content) throws RemoteException, ContentFault
   {
      throw new ContentFault(1, "create() is not implemented yet!");
   }

   /**
    * @see org.alfresco.repo.webservice.content.ContentServiceSoapPort#delete(org.alfresco.repo.webservice.types.Predicate)
    */
   public Reference[] delete(Predicate items) throws RemoteException, ContentFault
   {
      throw new ContentFault(1, "delete() is not implemented yet!");
   }

   /**
    * @see org.alfresco.repo.webservice.content.ContentServiceSoapPort#exists(org.alfresco.repo.webservice.types.Predicate)
    */
   public ExistsResult[] exists(Predicate items) throws RemoteException, ContentFault
   {
      throw new ContentFault(1, "exists() is not implemented yet!");
   }
}
