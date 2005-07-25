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
package org.alfresco.repo.webservice;

import org.alfresco.service.namespace.QName;

/**
 * Represents a property in the repository
 * 
 * @author gavinc
 */
public class Property
{
   private QName name;
   private String value;
   
   /**
    * Constructs a instance using the given name and value
    * 
    * @param name The name of the property
    * @param value The value
    */
   public Property(QName name, String value)
   {
      this.name = name;
      this.value = value;
   }

   /**
    * Returns the name
    * 
    * @return The name
    */
   public QName getName()
   {
      return this.name;
   }

   /**
    * Sets the name
    * 
    * @param name The name
    */
   public void setName(QName name)
   {
      this.name = name;
   }

   /**
    * Returns the value
    * 
    * @return The value
    */
   public String getValue()
   {
      return this.value;
   }

   /**
    * Sets the value
    * 
    * @param value The value
    */
   public void setValue(String value)
   {
      this.value = value;
   }
}
