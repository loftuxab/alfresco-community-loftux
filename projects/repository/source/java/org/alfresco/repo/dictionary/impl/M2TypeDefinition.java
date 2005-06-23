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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryException;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;


/**
 * Compiled Type Definition
 * 
 * @author David Caruana
 */
/*package*/ class M2TypeDefinition extends M2ClassDefinition
    implements TypeDefinition
{
    private List<QName> defaultAspectNames = new ArrayList<QName>();
    private List<AspectDefinition> defaultAspects = new ArrayList<AspectDefinition>();
    private List<AspectDefinition> inheritedDefaultAspects = new ArrayList<AspectDefinition>();


    /*package*/ M2TypeDefinition(M2Type m2Type, NamespacePrefixResolver resolver, Map<QName, PropertyDefinition> modelProperties, Map<QName, AssociationDefinition> modelAssociations)
    {
        super(m2Type, resolver, modelProperties, modelAssociations);

        // Resolve qualified names
        for (String aspectName : m2Type.getMandatoryAspects())
        {
            QName name = QName.createQName(aspectName, resolver);
            if (!defaultAspectNames.contains(name))
            {
                defaultAspectNames.add(name);
            }
        }
    }
    

    @Override
    /*package*/ void resolveDependencies(ModelQuery query)
    {
        super.resolveDependencies(query);
        
        for (QName aspectName : defaultAspectNames)
        {
            AspectDefinition aspect = query.getAspect(aspectName);
            if (aspect == null)
            {
                throw new DictionaryException("Mandatory aspect " + aspectName.toPrefixString() + " of class " + name.toPrefixString() + " is not found");
            }
            defaultAspects.add(aspect);
        }
    }


    @Override
    /*package*/ void resolveInheritance(ModelQuery query)
    {
        super.resolveInheritance(query);
        
        // Retrieve parent type
        TypeDefinition parentType = (parentName == null) ? null : query.getType(parentName);
        
        // Build list of inherited default aspects
        if (parentType != null)
        {
            inheritedDefaultAspects.addAll(parentType.getDefaultAspects());
        }
        
        // Append list of defined default aspects
        for (AspectDefinition def : defaultAspects)
        {
            if (!inheritedDefaultAspects.contains(def))
            {
                inheritedDefaultAspects.add(def);
            }
        }
    }
    

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.TypeDefinition#getDefaultAspects()
     */
    public List<AspectDefinition> getDefaultAspects()
    {
        return inheritedDefaultAspects;
    }
    
}
