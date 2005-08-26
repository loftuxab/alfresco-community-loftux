/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.importer;

import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;


/**
 * Description of node to import.
 * 
 * @author David Caruana
 *
 */
public interface ImportNode
{
    /**
     * @return  the parent context
     */
    public ImportParent getParentContext();

    /**
     * @return  the type definition
     */
    public TypeDefinition getTypeDefinition();
    
    /**
     * @return  the node ref
     */
    public NodeRef getNodeRef();
    
    /**
     * @return  the child name
     */
    public String getChildName();
    
    /**
     * Gets the properties of the node for the specified class (type or aspect)
     * 
     * @param className  the type or aspect
     * @return  the properties
     */
    public Map<QName, String> getProperties(QName className);

    /**
     * @return  the aspects of this node
     */
    public Set<QName> getNodeAspects();
    
}
