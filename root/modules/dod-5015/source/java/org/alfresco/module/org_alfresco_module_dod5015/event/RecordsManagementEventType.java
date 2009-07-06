/**
 * 
 */
package org.alfresco.module.org_alfresco_module_dod5015.event;

/**
 * Records management event type interface
 * 
 * @author Roy Wetherall
 */
public interface RecordsManagementEventType
{
    /**
     * Get the name of the records management event type
     * 
     * @return  String  event type name
     */
    String getName();
    
    /**
     * Indicates whether the event is automatic or not
     * 
     * @return  boolean     true if automatic, false otherwise
     */
    boolean isAutomaticEvent();
}
