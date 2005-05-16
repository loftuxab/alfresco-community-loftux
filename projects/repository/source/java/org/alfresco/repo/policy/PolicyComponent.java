package org.alfresco.repo.policy;

import java.util.Collection;

import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.ref.QName;



public interface PolicyComponent
{
    public <P extends ClassPolicy> ClassPolicyDelegate<P> registerClassPolicy(Class<P> policy);

    public Collection<PolicyDefinition> getRegisteredPolicies();
    
    public PolicyDefinition getRegisteredPolicy(PolicyType policyType, QName policy);
    
    public boolean isRegisteredPolicy(PolicyType policyType, QName policy);
    
    public <P extends ClassPolicy> BehaviourDefinition<ClassRef, P> bindClassBehaviour(QName policy, ClassRef classRef, Behaviour<P> behaviour);
    
    //TODO: public <P extends ClassPolicy> BehaviourDefinition<ClassRef, P> bindClassBehaviour(QName policy, Object service, Behaviour<P> behaviour);
    
    //TODO: public BehaviourDefinition bindPropertyBehaviour(QName policy, ClassRef classRef, QName property, Behaviour behaviour);

}
