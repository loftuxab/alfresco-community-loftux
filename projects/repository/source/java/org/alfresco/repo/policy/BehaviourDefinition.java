package org.alfresco.repo.policy;

import org.alfresco.repo.ref.QName;



public interface BehaviourDefinition<B extends BehaviourBinding>
{
    public QName getPolicy();
    
    public PolicyDefinition getPolicyDefinition();
    
    public B getBinding();
    
    public Behaviour getBehaviour();
    
}
