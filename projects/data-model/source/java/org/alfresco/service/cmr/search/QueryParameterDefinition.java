package org.alfresco.service.cmr.search;

import org.alfresco.api.AlfrescoPublicApi;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;

@AlfrescoPublicApi
public interface QueryParameterDefinition extends NamedQueryParameterDefinition
{   
    /**
     * This parameter may apply to a well known property type.
     * 
     * May be null
     * 
     * @return PropertyDefinition
     */
    public PropertyDefinition getPropertyDefinition();
    
    /**
     * Get the property type definition for this parameter.
     * It could come from the property type definition if there is one
     * 
     * Not null
     * 
     * @return DataTypeDefinition
     */
    public DataTypeDefinition getDataTypeDefinition();
    
    /**
     * Get the default value for this parameter.
     * 
     * @return String
     */
    public String getDefault();
    
    /**
     * Has this parameter got a default value?
     * 
     * @return boolean
     */
    public boolean hasDefaultValue();
}
