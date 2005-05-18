package org.alfresco.repo.policy;

import java.util.Collection;

import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.ref.QName;


/**
 * Policy Component for managing Policies and Behaviours.
 *
 * This component provides the ability to:
 * 
 * a) Register policies
 * b) Bind behaviours to policies
 * c) Invoke policy behaviours
 * 
 * A behaviour may be bound to a Policy before the Policy is registered.  In
 * this case, the behaviour is not validated (i.e. checked to determine if it
 * supports the policy interface) until the Policy is registered.  Otherwise,
 * the behaviour is validated at bind-time.
 *
 * @author David Caruana
 *
 */
public interface PolicyComponent
{
    /**
     * Register a Class-level Policy
     * 
     * @param <P>  the policy interface  
     * @param policy  the policy interface class
     * @return  A delegate for the class-level policy (typed by the policy interface)
     */
    public <P extends ClassPolicy> ClassPolicyDelegate<P> registerClassPolicy(Class<P> policy);

    // TODO: register property and association level policies
    
    /**
     * Gets all registered Policies
     * 
     * @return  the collection of registered policy definitions
     */
    public Collection<PolicyDefinition> getRegisteredPolicies();

    /**
     * Gets the specified registered Policy
     * 
     * @param policyType  the policy type
     * @param policy  the policy name
     * @return  the policy definition (or null, if it has not been registered)
     */
    public PolicyDefinition getRegisteredPolicy(PolicyType policyType, QName policy);

    /**
     * Determine if the specified policy has been registered
     * 
     * @param policyType  the policy type
     * @param policy  the policy name
     * @return  true => registered, false => not yet
     */
    public boolean isRegisteredPolicy(PolicyType policyType, QName policy);

    /**
     * Bind a Class specific behaviour to a Class-level Policy
     * 
     * @param policy  the policy name
     * @param classRef  the class to bind against
     * @param behaviour  the behaviour
     * @return  the registered behaviour definition
     */
    public BehaviourDefinition<ClassBehaviourBinding> bindClassBehaviour(QName policy, ClassRef classRef, Behaviour behaviour);

    /**
     * Bind a Service behaviour to a Class-level Policy
     * 
     * @param policy  the policy name
     * @param service  the service (any object, in fact)
     * @param behaviour  the behaviour
     * @return  the registered behaviour definition
     */
    public BehaviourDefinition<ServiceBehaviourBinding> bindClassBehaviour(QName policy, Object service, Behaviour behaviour);
    
    // TODO: bind Property and Association level behaviours

}
