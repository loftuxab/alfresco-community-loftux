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
package org.alfresco.web.bean.repository;

/**
 * Simple interface used to implement small classes capable of calculating dynamic property values
 * for MapNodes at runtime. This allows bean responsible for building large lists of MapNodes to
 * encapsulate the code needed to retrieve non-standard Node properties. The values are then
 * calculated on demand by the property resolver.
 * 
 * @author Kevin Roast
 */
public interface NodePropertyResolver
{
   /**
    * Get the property value for this resolver
    * 
    * @param node       MapNode this property is for
    * 
    * @return property value
    */
   public Object get(MapNode node);
}
