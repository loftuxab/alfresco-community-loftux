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
package org.alfresco.service.cmr.search;

import java.util.Collection;

import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;

/**
 * Category Service
 *
 * The service for querying and creatikng categories.
 * All other management can be carried out using the node service.
 * 
 * @author andyh
 *
 */
public interface CategoryService
{
    /**
     * Enum for navigation control.
     * MEMBERS - get only category members
     * SUB_CATEGORIES - get sub categories only
     * ALL - get both of the above
     */
    public enum Mode {MEMBERS, SUB_CATEGORIES, ALL};
    
    /**
     * Depth from which to get nodes
     * IMMEDIATE - only immediate sub categories or members
     * ANY - find subcategories or members at any level 
     */
    public enum Depth {IMMEDIATE, ANY};

    /**
     * Get the children of a given category node
     * 
     * @param categoryRef - the category node
     * @param mode - the enum mode for what to recover
     * @param depth - the enum depth for what level to recover
     * @return a collection of all the nodes found identified by thier ChildAssocRef's
     */
    Collection<ChildAssociationRef> getChildren(NodeRef categoryRef, Mode mode, Depth depth );

    /**
     * Get a list of all the categories appropriate for a given property
     * 
     * @param aspectQName
     * @param depth - the enum depth for what level to recover
     * @return a collection of all the nodes found identified by thier ChildAssocRef's
     */
    Collection<ChildAssociationRef> getCategories(StoreRef storeRef, QName aspectQName, Depth depth );

    /**
     * Get all the root categories
     * 
     * @return
     */
    Collection<ChildAssociationRef> getRootCategories(StoreRef storeRef);

    /**
     * Get all the types that represent categories
     * 
     * @return
     */
    Collection<QName> getCategoryAspects();

    /**
     * Create a new category
     * This will extend the category types in the data dictionary
     * All it needs is the type name and the attribute to use for categorisation
     * 
     * @param typeName
     * @param attributeName
     */
    NodeRef newCategory(QName typeName, String attributeName);

}
