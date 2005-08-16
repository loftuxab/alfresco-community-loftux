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
package org.alfresco.repo.dictionary;

import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.PropertyTypeDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.namespace.QName;


/**
 * Access to model items.
 * 
 * @author David Caruana
 *
 */
/*package*/ interface ModelQuery
{

    /**
     * Gets the specified property type
     * 
     * @param name  name of the property type
     * @return  property type definition
     */
    public PropertyTypeDefinition getPropertyType(QName name);

    /**
     * Gets the specified type
     * 
     * @param name  name of the type
     * @return  type definition
     */
    public TypeDefinition getType(QName name);
    
    /**
     * Gets the specified aspect
     * 
     * @param name  name of the aspect
     * @return  aspect definition
     */
    public AspectDefinition getAspect(QName name);
    
    /**
     * Gets the specified class
     * 
     * @param name  name of the class
     * @return  class definition
     */
    public ClassDefinition getClass(QName name);
    
    /**
     * Gets the specified property
     * 
     * @param name  name of the property
     * @return  property definition
     */
    public PropertyDefinition getProperty(QName name);
    
    /**
     * Gets the specified association
     * 
     * @param name  name of the association
     * @return  association definition
     */
    public AssociationDefinition getAssociation(QName name);
    
}
