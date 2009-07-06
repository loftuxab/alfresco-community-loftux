/**
 * 
 */
package org.alfresco.module.org_alfresco_module_dod5015.event;

/**
 * Records management event 
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementEvent
{ 
    /** Records management event type */
    private String type;
    
    /** Records management event name */
    private String name;
    
    /** Records management display label */
    private String displayLabel;
    
    /**
     * Constructor
     * 
     * @param type          event type
     * @param name          event name
     * @param displayLabel  event display label
     */
    public RecordsManagementEvent(String type, String name, String displayLabel)
    {
        this.type =  type;
        this.name = name;
        this.displayLabel = displayLabel;
    }
    
    /**
     * Get records management type
     * 
     * @return  String records management type
     */
    public String getType()
    {
        return this.type;
    }
    
    /**
     * Event name
     * 
     * @return String   event name
     */
    public String getName()
    {
        return this.name;
    }
    
    /**
     * 
     * @return
     */
    public String getDisplayLabel()
    {
        return displayLabel;
    }    
}
