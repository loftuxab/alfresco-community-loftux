package org.alfresco.module.vti.handler;

import java.util.List;
import java.util.Map;

import org.alfresco.module.vti.metadata.model.ListInfoBean;
import org.alfresco.module.vti.metadata.model.ListTypeBean;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.alfresco.service.cmr.dictionary.InvalidTypeException;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.DuplicateChildNodeNameException;
import org.alfresco.service.namespace.QName;

/**
 * Site list service fundamental API.
 * 
 * @author Nick Burch
 */
public interface ListServiceHandler
{
   /**
    * The different kinds of operation which can be performed on
    *  items within a list. This is defined "Method CMD" element
    */
   public enum ListItemOperationType
   {
       New, Update, Delete
   };
    
   /**
    * Fetches an existing Data List. This should ideally be called
    *  for a List Name, but optionally could be called for a list ID (GUID)
    */
   public ListInfoBean getList(String listName, String dws)
      throws SiteDoesNotExistException, FileNotFoundException;
   
    /**
     * Creates a Data List of the given type
     */
    public ListInfoBean createList(String listName, String description, String dws, int templateId)
       throws SiteDoesNotExistException, DuplicateChildNodeNameException, InvalidTypeException;

    /**
     * Deletes a Data List
     * 
     * @param listName String
     * @param dws String
     */
    public void deleteList(String listName, String dws) 
       throws SiteDoesNotExistException, FileNotFoundException;
    
    /**
     * Returns the list of available List Types
     */
    public List<ListTypeBean> getAvailableListTypes();
    
    /**
     * Returns the names and GUIDs for all the lists in the site.
     * 
     * @param siteName the name of site 
     * @return the list of site' lists
     */
    public List<ListInfoBean> getListCollection(String siteName) throws SiteDoesNotExistException;
    
    /**
     * Performs the specified add/update/delete operations against a 
     *  single list item.
     */
    public void updateListItem(ListInfoBean list, ListItemOperationType operation, 
            String id, Map<QName,String> fields) throws FileNotFoundException;
}
