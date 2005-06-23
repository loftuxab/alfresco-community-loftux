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
package org.alfresco.util;

import java.util.Collection;

/**
 * Utility class to perform various common parameter checks
 * 
 * @author gavinc
 */
public class ParameterCheck
{
   /**
    * Checks that the parameter with the given name has content i.e. it is not null
    * 
    * @param strParamName Name of parameter to check
    * @param object Value of the parameter to check
    */
   public static void mandatory(String strParamName, Object object)
   {
      if (strParamName == null || strParamName.length() == 0)
      {
         throw new IllegalArgumentException("Parameter name is mandatory");
      }
      
      // check that the given string value has content
      if (object == null)
      {
         throw new IllegalArgumentException(strParamName + " is a mandatory parameter");
      }
   }
   
   /**
    * Checks that the string parameter with the given name has content 
    * i.e. it is not null and not zero length
    * 
    * @param strParamName Name of parameter to check
    * @param strParamValue Value of the parameter to check
    */
   public static void mandatoryString(String strParamName, String strParamValue)
   {
      if (strParamName == null || strParamName.length() == 0)
      {
         throw new IllegalArgumentException("Parameter name is mandatory");
      }
      
      // check that the given string value has content
      if (strParamValue == null || strParamValue.length() == 0)
      {
         throw new IllegalArgumentException(strParamName + " is a mandatory parameter");
      }
   }
   
   
   /**
    * Checks that the collection parameter contains at least one item.
    * 
    * @param strParamName   Name of parameter to check
    * @param coll  collection to check
    */
   public static void mandatoryCollection(String strParamName, Collection coll)
   {
      if (strParamName == null || strParamName.length() == 0)
      {
         throw new IllegalArgumentException("Parameter name is mandatory");
      }
      
      if (coll == null || coll.size() == 0)
      {
         throw new IllegalArgumentException(strParamName + " collection must contain at least one item");
      }
   }

}
