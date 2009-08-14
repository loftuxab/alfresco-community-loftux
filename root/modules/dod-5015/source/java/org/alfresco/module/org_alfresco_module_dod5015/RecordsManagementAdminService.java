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
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Records management custom model service interface.
 * 
 * @author Neil McErlean
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
     * This method returns the custom references that have been defined in the custom
     * model.
     * 
     * @return The Map of custom references (both parent-child and standard).
     */
    public Map<QName, AssociationDefinition> getAvailableCustomReferences();
    
    public List<AssociationRef> getCustomReferencesFor(NodeRef node);
    
    public List<ChildAssociationRef> getCustomChildReferencesFor(NodeRef node);
    
    /**
     * This method adds the specified custom reference instance between the specified nodes.
     * 
     * @param fromNode
     * @param toNode
     * @param assocId the server-side qname e.g. {http://www.alfresco.org/model/rmcustom/1.0}customRefStandard1250274577519__supported__null__null
     */
    public void addCustomReference(NodeRef fromNode, NodeRef toNode, QName assocId);
}
