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
package org.alfresco.service.cmr.dictionary;

import org.alfresco.service.namespace.QName;


/**
 * Read-only definition of an Association.
 *  
 * @author David Caruana
 *
 */
public interface AssociationDefinition
{
    /**
     * @return  the qualified name
     */
    public QName getName();

    /**
     * @return the human-readable class title 
     */
    public String getTitle();
    
    /**
     * @return the human-readable class description 
     */
    public String getDescription();
    
    /**
     * Is this a child association?
     * 
     * @return true => child,  false => general relationship
     */
    public boolean isChild();
    
    /**
     * Is this association maintained by the Repository?
     * 
     * @return true => system maintained, false => client may maintain 
     */
    public boolean isProtected();

    /**
     * @return the source class
     */
    public ClassDefinition getSourceClass();

    /**
     * @return the role of the source class in this association? 
     */
    public QName getSourceRoleName();
    
    /**
     * Is the source class optional in this association?
     *  
     * @return true => cardinality > 0
     */
    public boolean isSourceMandatory();

    /**
     * Can there be many source class instances in this association? 
     * 
     * @return true => cardinality > 1, false => cardinality of 0 or 1
     */
    public boolean isSourceMany();

    /**
     * @return the target class  
     */
    public ClassDefinition getTargetClass();
    
    /**
     * @return the role of the target class in this association? 
     */
    public QName getTargetRoleName();
    
    /**
     * Is the target class optional in this association?
     *  
     * @return true => cardinality > 0
     */
    public boolean isTargetMandatory();

    /**
     * Can there be many target class instances in this association? 
     * 
     * @return true => cardinality > 1, false => cardinality of 0 or 1
     */
    public boolean isTargetMany();

}
