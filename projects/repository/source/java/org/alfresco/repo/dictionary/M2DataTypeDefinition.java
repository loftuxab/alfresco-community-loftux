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
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;


/**
 * Compiled Property Type Definition
 * 
 * @author David Caruana
 *
 */
/*package*/ class M2DataTypeDefinition implements DataTypeDefinition
{
    private QName name;
    private M2DataType dataType;
    
    
    /*package*/ M2DataTypeDefinition(M2DataType propertyType, NamespacePrefixResolver resolver)
    {
        this.name = QName.createQName(propertyType.getName(), resolver);
        this.dataType = propertyType;
    }


    /*package*/ void resolveDependencies(ModelQuery query)
    {
        // Ensure java class has been specified
        String javaClass = dataType.getJavaClassName();
        if (javaClass == null)
        {
            throw new DictionaryException("Java class of data type " + name.toPrefixString() + " must be specified");
        }
        
        // Ensure java class is valid and referencable
        try
        {
            Class.forName(javaClass);
        }
        catch (ClassNotFoundException e)
        {
            throw new DictionaryException("Java class " + javaClass + " of data type " + name.toPrefixString() + " is invalid", e);
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
        return dataType.getTitle();
    }
    

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyTypeDefinition#getDescription()
     */
    public String getDescription()
    {
        return dataType.getDescription();
    }
    

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.PropertyTypeDefinition#getAnalyserClassName()
     */
    public String getAnalyserClassName()
    {
       return dataType.getAnalyserClassName();
    }


    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.dictionary.PropertyTypeDefinition#getJavaClassName()
     */
    public String getJavaClassName()
    {
        return dataType.getJavaClassName();
    }
    
}
