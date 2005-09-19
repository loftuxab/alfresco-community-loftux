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
package org.alfresco.example.webservice.content;

import org.alfresco.example.webservice.BaseWebServiceSystemTest;
import org.alfresco.example.webservice.types.Content;
import org.alfresco.example.webservice.types.ContentFormat;
import org.alfresco.example.webservice.types.ParentReference;
import org.alfresco.example.webservice.types.Predicate;
import org.alfresco.example.webservice.types.Reference;
import org.alfresco.model.ContentModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ContentServiceSystemTest extends BaseWebServiceSystemTest
{
   private static Log logger = LogFactory.getLog(ContentServiceSystemTest.class);
   private static final String CONTENT = "This is a small piece of content to test the create service call";
   private static final String UPDATED_CONTENT = "This is some updated content to test the write service call";
   
   private static String newContentId;
   private String fileName = "unit-test.txt";
   
   /**
    * Tests the create content service method
    * 
    * @throws Exception
    */
   public void testCreate() throws Exception
   {
      // get the root node (hard code for now until we have a way to query for the root node)
      ParentReference parent = getFolderParentReference(ContentModel.ASSOC_CONTAINS);
      
      // Create the content
      String mimetype = "text/plain";
      Content content = this.contentService.create(parent, this.fileName, new ContentFormat(mimetype, "UTF-8"), CONTENT.getBytes());
      
      // Check the content
      assertNotNull("Returned content should not be null", content);
      assertNotNull("Format should not be null", content.getFormat());
      assertEquals("Mimetype should match what was sent", mimetype, content.getFormat().getMimetype());
      newContentId = content.getReference().getUuid();
      logger.debug("Created new content with id: " + newContentId);
   }
   
   /**
    * Tests the read method
    * 
    * @throws Exception
    */
   public void testRead() throws Exception
   {      
      if (newContentId == null)
      {
         fail("Failed to locate id of " + this.fileName);
      }
      
      Reference node = new Reference();
      node.setStore(getStore());
      node.setUuid(newContentId);
      
      ReadResult result = this.contentService.read(node);
      assertNotNull("read result should not be null", result);
      logger.debug("url for download is: " + result.getUrl());
      assertNotNull("Url to read content from must not be null", result.getUrl());
      
      // make sure length is reported as the same as the content used to create the file
      assertEquals("Content length's don't match", CONTENT.length(), result.getContent().getLength());
   }
   
   /**
    * Tests the write service method
    *
    * @throws Exception
    */
   public void testWrite() throws Exception
   {
      if (newContentId == null)
      {
         fail("Failed to locate id of " + this.fileName);
      }
      
      Reference node = new Reference();
      node.setStore(getStore());
      node.setUuid(newContentId);
      
      this.contentService.write(node, UPDATED_CONTENT.getBytes());

      ReadResult result = this.contentService.read(node);
      assertNotNull("read result should not be null", result);
      assertNotNull("Url to read content from must not be null", result.getUrl());
      
      long contentLength = result.getContent().getLength();
      assertEquals("Content length's don't match", UPDATED_CONTENT.length(), contentLength);
      assertTrue("Content length of update content should not be the same as the previous content length",
            contentLength != CONTENT.length());
      
      /* TODO: At some point we will have to provide the login credentials 
               for the download servlet (auth filter); it needs to look for a ticket
               on the URL */
               
      // read the contents of the URL and make sure they match
      String contentValue = getContentAsString(result.getUrl());
      
      // make sure the content in the repository is correct
      logger.debug("Content from repository: " + contentValue);
      assertEquals("Content does not match", UPDATED_CONTENT, contentValue);
   }
   
   /**
    * Tests the exists service method
    * 
    * @throws Exception
    */
   public void testExists() throws Exception
   {
      if (newContentId == null)
      {
         fail("Failed to locate id of " + this.fileName);
      }
      
      // create the predicate representation of the content
      Reference ref = new Reference();
      ref.setStore(getStore());
      ref.setUuid(newContentId);
      Predicate predicate = new Predicate(new Reference[] {ref}, null, null);
      
      ExistsResult[] existsResult = this.contentService.exists(predicate);
      assertNotNull("exists result should not be null", existsResult);
      
      // we only added one object so there should only be one result!
      assertTrue("There should be one result", existsResult.length == 1);
      assertTrue("The node should have existed", existsResult[0].isExists());
      assertTrue("Content length should match", existsResult[0].getLength() == UPDATED_CONTENT.length());
   }
   
   /**
    * Tests the describe service method
    * 
    * @throws Exception
    */
   public void testDescribe() throws Exception
   {
      if (newContentId == null)
      {
         fail("Failed to locate id of " + this.fileName);
      }
      
      // create the predicate representation of the content
      Reference ref = new Reference();
      ref.setStore(getStore());
      ref.setUuid(newContentId);
      Predicate predicate = new Predicate(new Reference[] {ref}, null, null);
      
      Content[] contentDesc = this.contentService.describe(predicate);
      assertNotNull("describe result should not be null", contentDesc);
      
      // we only added one object so there should only be one result!
      assertTrue("There should be one result", contentDesc.length == 1);
      
      // dump all the results
      Content content = contentDesc[0];
      String id = content.getReference().getUuid();
      String type = content.getType();
      String mimetype = content.getFormat().getMimetype();
      String encoding = content.getFormat().getEncoding();
      long length = content.getLength();
      if (logger.isDebugEnabled())
      {
         logger.debug("id = " + id);
         logger.debug("type = " + type);
         logger.debug("mimetype = " + mimetype);
         logger.debug("encoding = " + encoding);
         logger.debug("length = " + length);
      }
      
      // do some sanity checking
      assertEquals("The id is incorrect", newContentId, id);
      assertEquals("The type is incorrect", "{http://www.alfresco.org/model/content/1.0}content", type);
      assertEquals("The mimetype is incorrect", "text/plain", mimetype);
      assertEquals("The encoding is incorrect", "UTF-8", encoding);
      assertEquals("The length is incorrect", UPDATED_CONTENT.length(), length);
   }
   
   /**
    * Tests the delete service method
    * 
    * @throws Exception
    */
   public void testDelete() throws Exception
   {
      if (newContentId == null)
      {
         fail("Failed to locate id of " + this.fileName);
      }
      
      // create the predicate representation of the content
      Reference ref = new Reference();
      ref.setStore(getStore());
      ref.setUuid(newContentId);
      Predicate predicate = new Predicate(new Reference[] {ref}, null, null);
      
      Reference[] refs = this.contentService.delete(predicate);
      assertNotNull("delete result should not be null", refs);
      
      // now check that the node no longer exists
      ExistsResult[] existsResult = this.contentService.exists(predicate);
      assertNotNull("exists result should not be null", existsResult);
      assertFalse("The node should no longer exist", existsResult[0].isExists());
   }
}
