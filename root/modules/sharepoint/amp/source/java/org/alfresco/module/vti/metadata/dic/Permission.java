package org.alfresco.module.vti.metadata.dic;

/**
 * <p>Enum of the standard permissions that user may have in dws.</p> 
 * 
 * @author PavelYur
 */
public enum Permission
{
    
    /**
     * Add items to lists, add documents to document libraries.
     */
    INSERT_LIST_ITEMS ("InsertListItems"),    
    
    /**
     * Edit items in lists, edit documents in document libraries.
     */
    EDIT_LIST_ITEMS ("EditListItems"),        
    
    /**
     * Delete items from a list, documents from a document library.
     */
    DELETE_LIST_ITEMS ("DeleteListItems"),    
    
    /**
     * Manage a site, including the ability to perform all administration tasks for the site and manage contents and permissions
     */
    MANAGE_WEB ("ManageWeb"),                 
    
    /**
     * Create, change, and delete site groups, including adding users to the site groups and specifying which rights are assigned to a site group.
     */
    MANAGE_ROLES ("ManageRoles"),             
    
    /**
     * Manage or create subsites.
     */
    MANAGE_SUBWEBS ("ManageSubwebs"),         
    
    /**
     * Approve content in lists, add or remove columns in a list, and add or remove public views of a list.
     */
    MANAGE_LISTS  ("ManageLists");               
    
    private final String value;
    
    Permission(String value) 
     {
         this.value = value;
     }
     
     public String toString()
     {
         return value;
     }
}
