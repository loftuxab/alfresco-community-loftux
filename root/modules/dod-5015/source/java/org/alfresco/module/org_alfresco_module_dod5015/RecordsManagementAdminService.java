/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.org_alfresco_module_dod5015;

import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Records management custom model service interface.
 * 
 * @author Neil McErlean, janv
 */
public interface RecordsManagementAdminService
{
    /**
     * This method returns the custom properties that have been defined for the specified
     * customisable RM element.
     * 
     * @param customisedElement
     * @return
     */
    public Map<QName, PropertyDefinition> getAvailableCustomProperties(CustomisableRmElement customisedElement);
    
    /**
     * This method returns the custom properties that have been defined for all of
     * the specified customisable RM elements.
     */
    public Map<QName, PropertyDefinition> getAvailableCustomProperties();
    
    /**
     * 
     * @param aspectName
     * @param propQName
     * @param dataType
     * @param title
     * @param description
     * @param defaultValue
     * @param multiValued
     * @param mandatory
     * @param isProtected
     */
    public void addCustomPropertyDefinition(String aspectName, QName propQName, QName dataType, String title, String description, String defaultValue, boolean multiValued, boolean mandatory, boolean isProtected);
    
    /**
     * 
     * @param propQName
     * @deprecated currently throws UnsupportedOperationException
     */
    public void removeCustomPropertyDefinition(QName propQName);
    
    /**
     * This method returns the custom references that have been defined in the custom
     * model.
     * 
     * @return The Map of custom references (both parent-child and standard).
     */
    public Map<QName, AssociationDefinition> getAvailableCustomReferences();
    
    /**
     * 
     * @param node
     * @return
     */
    public List<AssociationRef> getCustomReferencesFor(NodeRef node);
    
    /**
     * 
     * @param node
     * @return
     */
    public List<ChildAssociationRef> getCustomChildReferencesFor(NodeRef node);
    
    /**
     * This method adds the specified custom reference instance between the specified nodes.
     * 
     * @param fromNode
     * @param toNode
     * @param assocId the server-side qname e.g. {http://www.alfresco.org/model/rmcustom/1.0}supported__null__null
     */
    public void addCustomReference(NodeRef fromNode, NodeRef toNode, QName assocId);

    /**
     * This method removes the specified custom reference instance from the specified node.
     * 
     * @param fromNode
     * @param toNode
     * @param assocId the server-side qname e.g. {http://www.alfresco.org/model/rmcustom/1.0}supported__null__null
     */
    public void removeCustomReference(NodeRef fromNode, NodeRef toNode, QName assocId);
    
    public void addCustomAssocDefinition(String label);
    
    public void addCustomChildAssocDefinition(String source, String target);
    
    /**
     * This method returns ConstraintDefinition objects defined in the rmc model (note: not property references or in-line defs)
     */
    public List<ConstraintDefinition> getCustomConstraintDefinitions();

    /**
     * This method adds a Constraint definition to the custom model.
     * The implementation of this method would have to go into the M2Model and insert
     * the relevant M2Objects for this new constraint.
     * 
     * param type not included as it would always be RMListOfValuesConstraint for RM.
     * 
     * @param constraintName the name e.g. rmc:foo
     * @param title the human-readable title e.g. My foo list
     * @param caseSensitive
     * @param allowedValues the allowed values list
     * 
     * TODO exceptions?
     */
    public void addCustomConstraintDefinition(QName constraintName, String title, boolean caseSensitive, List<String> allowedValues);
    
    /**
     * This method would remove a constraint definition from the custom model.
     * The implementation of this method would have to go into the M2Model and
     * remove the specified M2Constraint object from the model.
     * It would be subject to the same limitations as other non-incremental changes.
     * 
     * @param constraintName the name e.g. rmc:foo.
     * 
     * TODO exceptions?
     * 
     * @deprecated currently throws UnsupportedOperationException
     */
    public void removeCustomConstraintDefinition(QName constraintName);
    
    /**
     * This method would change the list of values supported in a custom constraint
     * 
     * @param name the name e.g. rmc:foo of the custom constraint.
     * @param newValues
     * 
     * TODO exceptions?
     */
    public void changeCustomConstraintValues(QName constraintName, List<String> newValues);
    
    /**
     * 
     * @param constraintName
     * @param title
     */
    public void changeCustomConstraintTitle(QName constraintName, String title);
}
