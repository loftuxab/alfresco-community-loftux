/*
 * Created on 19-May-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search;

import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.dictionary.PropertyTypeDefinition;

public interface QueryParameterDefinition extends NamedQueryParameterDefinition
{   
    /**
     * This parameter may apply to a well known property type.
     * 
     * May be null
     * 
     * @return
     */
    public PropertyDefinition getPropertyDefinition();
    
    /**
     * Get the property type definition for this parameter.
     * It could come from the property type definition if there is one
     * 
     * Not null
     * 
     * @return
     */
    public PropertyTypeDefinition getPropertyTypeDefinition();
    
    /**
     * Get the default value for this parameter.
     * 
     * @return
     */
    public String getDefault();
    
    /**
     * Has this parameter got a default value?
     * 
     * @return
     */
    public boolean hasDefaultValue();
}
