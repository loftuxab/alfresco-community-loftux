/**
 * 
 */
package org.alfresco.module.org_alfresco_module_dod5015.event;

import java.util.List;

/**
 * Records management event service interface
 * 
 * @author Roy Wetherall
 */
public interface RecordsManagementEventService
{
    /**
     * Register an event type
     * 
     * @param eventType     event type
     */
    void registerEventType(RecordsManagementEventType eventType);
    
    /**
     * Get a list of the event types
     * 
     * @return  List<String>    list of the event types
     */
    List<String> getEventTypes();
    
    /**
     * Get the records management event type
     * 
     * @param eventType                     name
     * @return RecordsManagementEventType   event type 
     */
    RecordsManagementEventType getEventType(String eventTypeName);
    
    /**
     * Get the list of available events
     * 
     * @return  List<RecordsManagementEvent>    list of events
     */
    List<RecordsManagementEvent> getEvents();
    
    /**
     * Get a records management event given its name.  Returns null if the event name is not
     * recognised.
     * 
     * @param eventName                 event name
     * @return RecordsManagementEvent   event
     */
    RecordsManagementEvent getEvent(String eventName);
    
    /**
     * Add an event
     * 
     * @param eventType             event type
     * @param eventName             event name
     * @param eventDisplayLabel     event display label
     */
    void addEvent(String eventType, String eventName, String eventDisplayLabel);
    
    /**
     * Remove an event
     * 
     * @param eventName     event name
     */
    void removeEvent(String eventName);  

}
