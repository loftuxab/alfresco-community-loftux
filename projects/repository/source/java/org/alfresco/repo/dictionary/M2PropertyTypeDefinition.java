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

import org.alfresco.service.cmr.dictionary.DictionaryException;
import org.alfresco.service.cmr.dictionary.PropertyTypeDefinition;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;


/**
 * Compiled Property Type Definition
 * 
 * @author David Caruana
 *
 */
/*package*/ class M2PropertyTypeDefinition implements PropertyTypeDefinition
{
    private QName name;
    private M2PropertyType propertyType;
    
    
    /*package*/ M2PropertyTypeDefinition(M2PropertyType propertyType, NamespacePrefixResolver resolver)
    {
        this.name = QName.createQName(propertyType.getName(), resolver);
        this.propertyType = propertyType;
    }


    /*package*/ void resolveDependencies(ModelQuery query)
    {
        // Ensure java class has been specified
        String javaClass = propertyType.getJavaClassName();
        if (javaClass == null)
        {
            throw new DictionaryException("Java class of property type " + name.toPrefixString() + " must be specified");
        }
        
        // Ensure java class is valid and referencable
        try
        {
            Class.forName(javaClass);
        }
        catch (ClassNotFoundException e)
        {
            throw new DictionaryException("Java class " + javaClass + " of property type " + name.toPrefixString() + " is invalid", e);
        }
    }
    
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyTypeDefinition#getName()
     */
    public QName getName()
    {
        return name;
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyTypeDefinition#getTitle()
     */
    public String getTitle()
    {
        return propertyType.getTitle();
    }
    

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyTypeDefinition#getDescription()
     */
    public String getDescription()
    {
        return propertyType.getDescription();
    }
    

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyTypeDefinition#getAnalyserClassName()
     */
    public String getAnalyserClassName()
    {
       return propertyType.getAnalyserClassName();
    }


    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.dictionary.PropertyTypeDefinition#getJavaClassName()
     */
    public String getJavaClassName()
    {
        return propertyType.getJavaClassName();
    }
    
}
