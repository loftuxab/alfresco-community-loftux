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
package org.alfresco.repo.dictionary.impl;

import java.util.Map;

import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;


/**
 * Compiled Aspect Definition.
 * 
 * @author David Caruana
 */
/*package*/ class M2AspectDefinition extends M2ClassDefinition
    implements AspectDefinition
{

    /*package*/ M2AspectDefinition(M2Aspect m2Aspect, NamespacePrefixResolver resolver, Map<QName, PropertyDefinition> modelProperties, Map<QName, AssociationDefinition> modelAssociations)
    {
        super(m2Aspect, resolver, modelProperties, modelAssociations);
    }
    
}
