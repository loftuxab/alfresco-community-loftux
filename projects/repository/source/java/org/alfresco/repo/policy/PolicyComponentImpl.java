package org.alfresco.repo.policy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.ref.QName;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Policy Component Implementation.
 * 
 * @author David Caruana
 *
 */
public class PolicyComponentImpl implements PolicyComponent
{
    // Logger
    private static final Log logger = LogFactory.getLog(PolicyComponentImpl.class);
    
    // Policy interface annotations
    private static String ANNOTATION_NAMESPACE = "NAMESPACE";

    // Dictionary Service
    private DictionaryService dictionary;
    
    // Map of registered Policies
    private Map<PolicyKey, PolicyDefinition> registeredPolicies;; 

    // Map of Class Behaviours (by policy name)
    private Map<QName, ClassBehaviourIndex> classBehaviours = new HashMap<QName, ClassBehaviourIndex>();
    

    /**
     * Construct
     * 
     * @param dictionary  dictionary service
     */
    public PolicyComponentImpl(DictionaryService dictionary)
    {
        this.dictionary = dictionary;
        this.registeredPolicies = new HashMap<PolicyKey, PolicyDefinition>();
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.policy.PolicyComponent#registerClassPolicy()
     */
    public <T extends ClassPolicy> ClassPolicyDelegate<T> registerClassPolicy(Class<T> policy)
    {
        ParameterCheck.mandatory("Policy interface class", policy);
        PolicyDefinition definition = createPolicyDefinition(policy);
        registeredPolicies.put(new PolicyKey(definition.getType(), definition.getName()), definition);
        ClassPolicyDelegate<T> delegate = new ClassPolicyDelegate<T>(dictionary, policy, getClassBehaviourIndex(definition.getName()));
        
        if (logger.isInfoEnabled())
            logger.info("Registered class policy " + definition.getName() + " (" + definition.getPolicyInterface() + ")");
        
        return delegate;
    }
    
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.policy.PolicyComponent#getRegisteredPolicies()
     */
    public Collection<PolicyDefinition> getRegisteredPolicies()
    {
        return Collections.unmodifiableCollection(registeredPolicies.values());
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.policy.PolicyComponent#getRegisteredPolicy(org.alfresco.repo.policy.PolicyType, org.alfresco.repo.ref.QName)
     */
    public PolicyDefinition getRegisteredPolicy(PolicyType policyType, QName policy)
    {
        return registeredPolicies.get(new PolicyKey(policyType, policy));
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.policy.PolicyComponent#isRegisteredPolicy(org.alfresco.repo.policy.PolicyType, org.alfresco.repo.ref.QName)
     */
    public boolean isRegisteredPolicy(PolicyType policyType, QName policy)
    {
        return registeredPolicies.containsKey(new PolicyKey(policyType, policy));
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.policy.PolicyComponent#bindClassBehaviour(org.alfresco.repo.ref.QName, org.alfresco.repo.ref.QName, org.alfresco.repo.policy.Behaviour)
     */
    public BehaviourDefinition<ClassBehaviourBinding> bindClassBehaviour(QName policy, QName classRef, Behaviour behaviour)
    {
        // Validate arguments
        ParameterCheck.mandatory("Policy", policy);
        ParameterCheck.mandatory("Class Reference", classRef);
        ParameterCheck.mandatory("Behaviour", behaviour);

        // Validate Binding
        ClassDefinition classDefinition = dictionary.getClass(classRef);
        if (classDefinition == null)
        {
            throw new IllegalArgumentException("Class " + classRef + " has not been defined in the data dictionary");
        }
        
        // Create behaviour definition and bind to policy
        ClassBehaviourBinding binding = new ClassBehaviourBinding(dictionary, classRef);
        BehaviourDefinition<ClassBehaviourBinding> definition = createBehaviourDefinition(PolicyType.Class, policy, binding, behaviour);
        getClassBehaviourIndex(policy).putClassBehaviour(definition);
        
        if (logger.isInfoEnabled())
            logger.info("Bound " + behaviour + " to policy " + policy + " for class " + classRef);

        return definition;
    }
    
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.policy.PolicyComponent#bindClassBehaviour(org.alfresco.repo.ref.QName, java.lang.Object, org.alfresco.repo.policy.Behaviour)
     */
    public BehaviourDefinition<ServiceBehaviourBinding> bindClassBehaviour(QName policy, Object service, Behaviour behaviour)
    {
        // Validate arguments
        ParameterCheck.mandatory("Policy", policy);
        ParameterCheck.mandatory("Service", service);
        ParameterCheck.mandatory("Behaviour", behaviour);
        
        // Create behaviour definition and bind to policy
        ServiceBehaviourBinding binding = new ServiceBehaviourBinding(service);
        BehaviourDefinition<ServiceBehaviourBinding> definition = createBehaviourDefinition(PolicyType.Class, policy, binding, behaviour);
        getClassBehaviourIndex(policy).putServiceBehaviour(definition);
        
        if (logger.isInfoEnabled())
            logger.info("Bound " + behaviour + " to policy " + policy + " for service " + service);

        return definition;
    }    
    

    /**
     * Gets the Class behaviour index for the specified Policy
     * 
     * @param policy  the policy
     * @return  the class behaviour index
     */
    private synchronized ClassBehaviourIndex getClassBehaviourIndex(QName policy)
    {
        ClassBehaviourIndex index = classBehaviours.get(policy);
        if (index == null)
        {
            index = new ClassBehaviourIndex();
            classBehaviours.put(policy, index);
        }
        return index;
    }
    

    /**
     * Create a Behaviour Definition
     * 
     * @param <B>  the type of binding
     * @param type  policy type
     * @param policy  policy name
     * @param binding  the binding
     * @param behaviour  the behaviour
     * @return  the behaviour definition
     */
    private <B extends BehaviourBinding> BehaviourDefinition<B> createBehaviourDefinition(PolicyType type, QName policy, B binding, Behaviour behaviour)
    {
        // Determine if policy has already been registered
        PolicyDefinition policyDefinition = getRegisteredPolicy(type, policy);
        if (policyDefinition != null)
        {
            // Policy has already been registered, force validation of behaviour now
            behaviour.getInterface(policyDefinition.getPolicyInterface());
        }
        else
        {
            if (logger.isInfoEnabled())
                logger.info("Behaviour " + behaviour + " is binding (" + binding + ") to policy " + policy + " before the policy is registered");
        }
        
        // Construct the definition
        return new BehaviourDefinitionImpl<B>(type, policy, binding, behaviour);
    }
    

    /**
     * Create a Policy Definition
     * 
     * @param policyIF  the policy interface
     * @return  the policy definition
     */
    private PolicyDefinition createPolicyDefinition(Class policyIF)
    {
        // Extract Policy Namespace
        String namespaceURI = NamespaceService.DEFAULT_URI;
        try
        {
            Field metadata = policyIF.getField(ANNOTATION_NAMESPACE);
            if (!String.class.isAssignableFrom(metadata.getType()))
            {
                throw new PolicyException("NAMESPACE metadata incorrectly specified in policy " + policyIF.getCanonicalName());
            }
            namespaceURI = (String)metadata.get(null);
        }
        catch(NoSuchFieldException e)
        {
            // Assume default namespace
        }
        catch(IllegalAccessException e)
        {
            // Shouldn't get here (interface definitions must be accessible)
        }

        // Extract Policy Name
        Method[] methods = policyIF.getMethods();
        if (methods.length != 1)
        {
            throw new PolicyException("Policy " + policyIF.getCanonicalName() + " must declare only one method");
        }
        String name = methods[0].getName();

        // Create Policy Definition
        return new PolicyDefinitionImpl(QName.createQName(namespaceURI, name), policyIF);
    }

    
    /**
     * Policy Key (composite of policy type and name)
     * 
     * @author David Caruana
     *
     */
    private static class PolicyKey
    {
        private PolicyType type;
        private QName policy;
        
        private PolicyKey(PolicyType type, QName policy)
        {
            this.type = type;
            this.policy = policy;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj == this)
            {
                return true;
            }
            else if (obj == null || !(obj instanceof PolicyKey))
            {
                return false;
            }
            PolicyKey other = (PolicyKey)obj;
            return type.equals(other.type) && policy.equals(other.policy);
        }

        @Override
        public int hashCode()
        {
            return 37 * type.hashCode() + policy.hashCode();
        }
    }
    
    
    /**
     * Policy Definition implementation.
     * 
     * @author David Caruana
     *
     */
    /*package*/ class PolicyDefinitionImpl implements PolicyDefinition
    {
        private QName policy;
        private Class policyIF;

        /*package*/ PolicyDefinitionImpl(QName policy, Class policyIF)
        {
            this.policy = policy;
            this.policyIF = policyIF;
        }
        
        /* (non-Javadoc)
         * @see org.alfresco.repo.policy.PolicyDefinition#getName()
         */
        public QName getName()
        {
            return policy;
        }
        
        /* (non-Javadoc)
         * @see org.alfresco.repo.policy.PolicyDefinition#getPolicyInterface()
         */
        public Class getPolicyInterface()
        {
            return policyIF;
        }

        /* (non-Javadoc)
         * @see org.alfresco.repo.policy.PolicyDefinition#getType()
         */
        public PolicyType getType()
        {
            if (ClassPolicy.class.isAssignableFrom(policyIF))
            {
                return PolicyType.Class;
            }
            else if (PropertyPolicy.class.isAssignableFrom(policyIF))
            {
                return PolicyType.Property;
            }
            else
            {
                return PolicyType.Association; 
            }
        }
    }
    

    /**
     * Behaviour Definition implementation.
     * 
     * @author David Caruana
     *
     * @param <B>  the type of binding
     */
    /*package*/ class BehaviourDefinitionImpl<B extends BehaviourBinding> implements BehaviourDefinition<B>
    {
        private PolicyType type;
        private QName policy;
        private B binding;
        private Behaviour behaviour;
        
        /*package*/ BehaviourDefinitionImpl(PolicyType type, QName policy, B binding, Behaviour behaviour)
        {
            this.type = type;
            this.policy = policy;
            this.binding = binding;
            this.behaviour = behaviour;
        }
        
        /* (non-Javadoc)
         * @see org.alfresco.repo.policy.BehaviourDefinition#getPolicy()
         */
        public QName getPolicy()
        {
            return policy;
        }

        /* (non-Javadoc)
         * @see org.alfresco.repo.policy.BehaviourDefinition#getPolicyDefinition()
         */
        public PolicyDefinition getPolicyDefinition()
        {
            return getRegisteredPolicy(type, policy);
        }

        /* (non-Javadoc)
         * @see org.alfresco.repo.policy.BehaviourDefinition#getBinding()
         */
        public B getBinding()
        {
            return binding;
        }
        
        /* (non-Javadoc)
         * @see org.alfresco.repo.policy.BehaviourDefinition#getBehaviour()
         */
        public Behaviour getBehaviour()
        {
            return behaviour;
        }
    }

}
