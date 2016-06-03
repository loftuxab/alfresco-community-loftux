package org.alfresco.opencmis.dictionary;

import java.util.Map;

import org.alfresco.service.namespace.QName;
import org.apache.chemistry.opencmis.commons.enums.Action;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;


/**
 * Encapsulate the mapping of property to property accessor
 * 
 * @author davidc
 */
public interface PropertyAccessorMapping
{
    /**
     * Gets a property accessor
     * 
     * @param propertyId property id
     * @return property accessor
     */
    public CMISPropertyAccessor getPropertyAccessor(String propertyId);

    /**
     * Create a direct node property accessor
     * 
     * @param propertyId  property id
     * @param propertyName  node property name
     * @return  property accessor
     */
    public CMISPropertyAccessor createDirectPropertyAccessor(String propertyId, QName propertyName);
    
    /**
     * Gets the Action Evaluators applicable for the given CMIS Scope
     * 
     * @param scope BaseTypeId
     */
    public Map<Action, CMISActionEvaluator> getActionEvaluators(BaseTypeId scope);
}
