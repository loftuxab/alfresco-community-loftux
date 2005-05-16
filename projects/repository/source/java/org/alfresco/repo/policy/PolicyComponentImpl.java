package org.alfresco.repo.policy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.ref.QName;
import org.alfresco.util.ParameterCheck;



public class PolicyComponentImpl implements PolicyComponent
{

    private static String ANNOTATION_NAMESPACE = "NAMESPACE";

    private Map<PolicyKey, PolicyDefinition> registeredPolicies;; 

    private DictionaryService dictionary;
    
    private Map<QName, ClassBehaviourIndex<ClassPolicy>> classPolicies = new HashMap<QName, ClassBehaviourIndex<ClassPolicy>>();
    
    
    public PolicyComponentImpl(DictionaryService dictionary)
    {
        this.dictionary = dictionary;
        this.registeredPolicies = new HashMap<PolicyKey, PolicyDefinition>();
    }

    
    
    public <T extends ClassPolicy> ClassPolicyDelegate<T> registerClassPolicy(Class<T> policy)
    {
        ParameterCheck.mandatory("Policy interface class", policy);
        PolicyDefinition definition = createPolicyDefinition(policy);
        registeredPolicies.put(new PolicyKey(definition.getType(), definition.getName()), definition);
        return new ClassPolicyDelegate<T>(definition.getPolicyInterface(), getClassBehaviourIndex(definition.getName()));
    }

    
    private synchronized ClassBehaviourIndex<ClassPolicy> getClassBehaviourIndex(QName policy)
    {
        ClassBehaviourIndex<ClassPolicy> index = classPolicies.get(policy);
        if (index == null)
        {
            index = new ClassBehaviourIndex<ClassPolicy>(dictionary);
            classPolicies.put(policy, index);
        }
        return index;
    }
    
    
    public Collection<PolicyDefinition> getRegisteredPolicies()
    {
        return Collections.unmodifiableCollection(registeredPolicies.values());
    }


    public PolicyDefinition getRegisteredPolicy(PolicyType policyType, QName policy)
    {
        return registeredPolicies.get(new PolicyKey(policyType, policy));
    }


    public boolean isRegisteredPolicy(PolicyType policyType, QName policy)
    {
        return registeredPolicies.containsKey(new PolicyKey(policyType, policy));
    }


    public <P extends ClassPolicy> BehaviourDefinition<ClassRef, P> bindClassBehaviour(QName policy, ClassRef classRef, Behaviour<P> behaviour)
    {
        // Validate arguments
        ParameterCheck.mandatory("Policy", policy);
        ParameterCheck.mandatory("Class Reference", classRef);
        ParameterCheck.mandatory("Behaviour", behaviour);

        // Create behaviour definition and bind to policy
        BehaviourDefinition<ClassRef, P> definition = createBehaviourDefinition(PolicyType.Class, policy, classRef, behaviour);
        getClassBehaviourIndex(policy).putClassBehaviour(definition);
        
        return definition;
    }

    
    private <B, P extends ClassPolicy> BehaviourDefinition<B, P> createBehaviourDefinition(PolicyType type, QName policy, B binding, Behaviour<P> behaviour)
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
            // TODO: log policy not registered
        }
        
        // Construct the definition
        return new BehaviourDefinitionImpl<B, P>(type, policy, binding, behaviour);
    }
    

    public BehaviourDefinition bindPropertyBehaviour(QName policy, ClassRef classRef, QName property, Behaviour behaviour)
    {
        // TODO Auto-generated method stub
        return null;
    }


    private PolicyDefinition createPolicyDefinition(Class policyIF)
    {
        // TODO: Validate arguments
        
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

    
    /*package*/ static class PolicyKey
    {
        private PolicyType type;
        private QName policy;
        
        /*package*/ PolicyKey(PolicyType type, QName policy)
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
    
    
    /*package*/ class PolicyDefinitionImpl implements PolicyDefinition
    {
        private QName policy;
        private Class policyIF;

        
        /*package*/ PolicyDefinitionImpl(QName policy, Class policyIF)
        {
            this.policy = policy;
            this.policyIF = policyIF;
        }
        
        
        public QName getName()
        {
            return policy;
        }
        
        public Class getPolicyInterface()
        {
            return policyIF;
        }

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
    

    /*package*/ class BehaviourDefinitionImpl<B, P extends Policy> implements BehaviourDefinition<B, P>
    {
        
        private PolicyType type;
        private QName policy;
        private B binding;
        private Behaviour<P> behaviour;
        
        
        /*package*/ BehaviourDefinitionImpl(PolicyType type, QName policy, B binding, Behaviour<P> behaviour)
        {
            this.type = type;
            this.policy = policy;
            this.binding = binding;
            this.behaviour = behaviour;
        }
        
        
        public PolicyType getType()
        {
            return type;
        }
        
        public QName getPolicy()
        {
            return policy;
        }


        public PolicyDefinition getPolicyDefinition()
        {
            return getRegisteredPolicy(type, policy);
        }

        public B getBinding()
        {
            return binding;
        }
        
        public Behaviour<P> getBehaviour()
        {
            return behaviour;
        }

    }    
}
