package org.alfresco.service.cmr.search;

import org.alfresco.service.namespace.QName;

/**
 * The metadata for a ResultSet selector.
 * 
 * @author andyh
 *
 */
public interface ResultSetSelector
{
    /**
     * The unique name for the selector.
     * @return - the unique name for the selector
     */
   public String getName();
   
   /**
    * Get the Alfresco type QName for the type or aspect
    * @return - the type or aspect 
    */
   public QName getType();
}
