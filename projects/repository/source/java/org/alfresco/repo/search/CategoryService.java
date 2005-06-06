/*
 * Created on 02-Jun-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search;

import java.util.Collection;

import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;

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
    Collection<ChildAssocRef> getChildren(NodeRef categoryRef, Mode mode, Depth depth );

    /**
     * Get a list of all the categories appropriate for a given property
     * 
     * @param attributeQName
     * @param depth - the enum depth for what level to recover
     * @return a collection of all the nodes found identified by thier ChildAssocRef's
     */
    Collection<ChildAssocRef> getCategories(StoreRef storeRef, QName attributeQName, Depth depth );

    /**
     * Get all the root categories
     * 
     * @return
     */
    Collection<ChildAssocRef> getRootCategories(StoreRef storeRef);

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
