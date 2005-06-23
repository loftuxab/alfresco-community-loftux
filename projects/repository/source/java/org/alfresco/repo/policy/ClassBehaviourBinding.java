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
package org.alfresco.repo.policy;

import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.QName;


/**
 * Behaviour binding to a Class (Type or Aspect) in the Content Model.
 * 
 * @author David Caruana
 *
 */
/*package*/ class ClassBehaviourBinding implements BehaviourBinding
{
    // The dictionary service
    private DictionaryService dictionary;
    
    // The class qualified name
    private QName classQName; 


    /**
     * Construct.
     * 
     * @param dictionary  the dictionary service
     * @param classQName  the Class qualified name
     */
    /*package*/ ClassBehaviourBinding(DictionaryService dictionary, QName classQName)
    {
        this.dictionary = dictionary;
        this.classQName = classQName;
    }

    /*package*/ DictionaryService getDictionary()
    {
        return dictionary;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.policy.BehaviourBinding#generaliseBinding()
     */
    public BehaviourBinding generaliseBinding()
    {
        BehaviourBinding generalisedBinding = null;
        ClassDefinition classDefinition = dictionary.getClass(classQName);
        QName parentClassName = classDefinition.getParentName();
        if (parentClassName != null)
        {
            generalisedBinding = new ClassBehaviourBinding(dictionary, parentClassName);
        }
        return generalisedBinding;
    }
    
    /**
     * Gets the class qualified name
     * 
     * @return  the class qualified name
     */
    public QName getClassQName()
    {
        return classQName;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof ClassBehaviourBinding))
        {
            return false;
        }
        return classQName.equals(((ClassBehaviourBinding)obj).classQName);
    }

    @Override
    public int hashCode()
    {
        return classQName.hashCode();
    }

    @Override
    public String toString()
    {
        return "ClassBinding[class=" + classQName + "]";
    }
    
}
