/**
 * Created on Apr 27, 2005
 */
package org.alfresco.repo.policy.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.alfresco.repo.policy.PolicyDefinitionService;

/**
 * Policy defition service default implementation
 * 
 * @author Roy Wetherall
 */
public class PolicyDefinitionServiceImpl implements PolicyDefinitionService
{
    /**
     * Map of objects and their exposed policies
     */
    private HashMap<Object, Set<Class>> policyRegister = new HashMap<Object, Set<Class>>();
    
    /**
     * @see PolicyDefinitionService#registerPolicy(Object, Class)
     */
    public void registerPolicy(Object exposingObject, Class policy)
    {        
        Set<Class> currentPolicies = this.policyRegister.get(exposingObject);
        if (currentPolicies == null)
        {
            currentPolicies = new HashSet<Class>();
        }
        currentPolicies.add(policy);
        policyRegister.put(exposingObject, currentPolicies);
    }
    
    /**
     * @see PolicyDefinitionService#hasPolicy(Object, Class)
     */
    public boolean hasPolicy(Object exposingObject, Class policy)
    {
        boolean result = false;
        Set<Class> currentPolicies = this.policyRegister.get(exposingObject);
        if (currentPolicies != null)
        {
            result = currentPolicies.contains(policy);
        }
        return result;
    }
    
    /**
     * @see PolicyDefinitionService#getRegisteredPolicies(Object)
     */
    public Set<Class> getRegisteredPolicies(Object exposingObject)
    {
        return this.policyRegister.get(exposingObject);
    }

}
