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

import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryException;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.PropertyTypeDefinition;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;


/**
 * Compiled Property Definition
 * 
 * @author David Caruana
 */
/*package*/ class M2PropertyDefinition implements PropertyDefinition
{
    private ClassDefinition classDef;
    private M2Property property;
    private QName name;
    private QName propertyTypeName;
    private PropertyTypeDefinition propertyType;
    
    
    /*package*/ M2PropertyDefinition(ClassDefinition classDef, M2Property m2Property, NamespacePrefixResolver resolver)
    {
        this.classDef = classDef;
        this.property = m2Property;

        // Resolve Names
        this.name = QName.createQName(property.getName(), resolver);
        this.propertyTypeName = QName.createQName(property.getType(), resolver);
    }
    
    
    /*package*/ M2PropertyDefinition(ClassDefinition classDef, PropertyDefinition propertyDef, M2PropertyOverride override)
    {
        this.classDef = classDef;
        this.property = createOverriddenProperty(propertyDef, override);
        this.name = propertyDef.getName();
        this.propertyType = propertyDef.getPropertyType();
        this.propertyTypeName = this.propertyType.getName();
    }
    
    
    /*package*/ void resolveDependencies(ModelQuery query)
    {
        if (propertyTypeName == null)
        {
            throw new DictionaryException("Property type of property " + name.toPrefixString() + " must be specified");
        }
        propertyType = query.getPropertyType(propertyTypeName);
        if (propertyType == null)
        {
            throw new DictionaryException("Property type " + propertyTypeName.toPrefixString() + " of property " + name.toPrefixString() + " is not found");
        }
    }
    
    
    /**
     * Create a property definition whose values are overridden
     * 
     * @param propertyDef  the property definition to override
     * @param override  the overridden values
     * @return  the property definition
     */
    private M2Property createOverriddenProperty(PropertyDefinition propertyDef, M2PropertyOverride override)
    {
        M2Property property = new M2Property();
        
        // Process Default Value
        String defaultValue = override.getDefaultValue();
        property.setDefaultValue(defaultValue == null ? propertyDef.getDefaultValue() : defaultValue);

        // Process Mandatory Value
        Boolean isMandatory = override.isMandatory();
        if (isMandatory != null)
        {
            if (propertyDef.isMandatory() == true && isMandatory == false)
            {
                throw new DictionaryException("Cannot relax mandatory attribute of property " + propertyDef.getName().toPrefixString());
            }
        }
        property.setMandatory(isMandatory == null ? propertyDef.isMandatory() : isMandatory);

        // Copy all other properties as they are
        property.setDescription(propertyDef.getDescription());
        property.setIndexed(propertyDef.isIndexed());
        property.setIndexedAtomically(propertyDef.isIndexedAtomically());
        property.setMultiValued(propertyDef.isMultiValued());
        property.setProtected(propertyDef.isProtected());
        property.setStoredInIndex(propertyDef.isStoredInIndex());
        property.setTitle(propertyDef.getTitle());
        property.setTokenisedInIndex(propertyDef.isTokenisedInIndex());
        
        return property;
    }
    
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyDefinition#getName()
     */
    public QName getName()
    {
        return name;
    }
    
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyDefinition#getTitle()
     */
    public String getTitle()
    {
        return property.getTitle();
    }
    

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyDefinition#getDescription()
     */
    public String getDescription()
    {
        return property.getDescription();
    }
    

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyDefinition#getDefaultValue()
     */
    public String getDefaultValue()
    {
        return property.getDefaultValue();
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyDefinition#getPropertyType()
     */
    public PropertyTypeDefinition getPropertyType()
    {
        return propertyType;
    }
    

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyDefinition#getContainerClass()
     */
    public ClassDefinition getContainerClass()
    {
        return classDef;
    }
    

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyDefinition#isMultiValued()
     */
    public boolean isMultiValued()
    {
        return property.isMultiValued();
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyDefinition#isMandatory()
     */
    public boolean isMandatory()
    {
        return property.isMandatory();
    }
    

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyDefinition#isProtected()
     */
    public boolean isProtected()
    {
        return property.isProtected();
    }
    

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyDefinition#isIndexed()
     */
    public boolean isIndexed()
    {
        return property.isIndexed();
    }
    

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyDefinition#isStoredInIndex()
     */
    public boolean isStoredInIndex()
    {
        return property.isStoredInIndex();
    }
    

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyDefinition#isIndexedAtomically()
     */
    public boolean isIndexedAtomically()
    {
        return property.isIndexedAtomically();
    }
    

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyDefinition#isTokenisedInIndex()
     */
    public boolean isTokenisedInIndex()
    {
        return property.isTokenisedInIndex();
    }
    
}
