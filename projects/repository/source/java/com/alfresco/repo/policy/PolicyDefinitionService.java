/**
 * Created on Apr 27, 2005
 */
package org.alfresco.repo.policy;

import java.util.Set;

/**
 * Policy Defintion Service Interface
 * 
 * @author Roy Wetherall
 */
public interface PolicyDefinitionService
{
    /**
     * Registers a policy against the exposing object.
     * 
     * @param exposingObject  the exposing object
     * @param policy          the policy
     */
    public void registerPolicy(Object exposingObject, Class policy);
    
    /**
     * Indicates whether a policy is registered against a perticular 
     * object.
     * 
     * @param exposingObject  the exposing object
     * @param policy          the policy
     * @return                true if the object exposes the policy, false
     *                        otherwise
     */
    public boolean hasPolicy(Object exposingObject, Class policy);
    
    /**
     * Gets the policies registered for a perticular object
     * 
     * @param exposingObject  the exposing object
     * @return                a set containing the exposed policies, null if 
     *                        none.
     */
    public Set<Class> getRegisteredPolicies(Object exposingObject);
}
