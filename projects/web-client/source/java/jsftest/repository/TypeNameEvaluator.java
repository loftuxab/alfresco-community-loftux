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
package jsftest.repository;

import org.alfresco.config.evaluator.Evaluator;
import org.alfresco.web.bean.repository.Node;

/**
 * Evaluator that tests whether an object is of a particular type
 * 
 * @author gavinc
 */
public class TypeNameEvaluator implements Evaluator
{
   /**
    * Tests whether the given object has the type defined in the condition
    * 
    * @see org.alfresco.config.evaluator.Evaluator#applies(java.lang.Object, java.lang.String)
    */
   public boolean applies(Object obj, String condition)
   {
      boolean result = false;
      
      if (obj instanceof Node)
      {
         String typeName = ((Node)obj).getTypeName();
         result = typeName.equalsIgnoreCase(condition);
      }
      
      return result;
   }

}
