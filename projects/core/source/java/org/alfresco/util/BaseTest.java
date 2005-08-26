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
package org.alfresco.util;

import java.io.File;

import junit.framework.TestCase;

/**
 * Base class for all JUnit tests
 * 
 * @author gavinc
 */
public abstract class BaseTest extends TestCase
{
   private String resourcesDir;
   
   public BaseTest()
   {
      this.resourcesDir = System.getProperty("user.dir") + File.separator + "source" + 
                          File.separator + "test-resources" + File.separator;
   }
   
   public String getResourcesDir()
   {
      return this.resourcesDir;
   }
}
