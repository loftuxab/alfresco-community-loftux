package org.alfresco.repo.policy;

import org.alfresco.repo.ref.QName;



public interface BehaviourDefinition<B, P extends Policy>
{
    public QName getPolicy();
    
    public PolicyDefinition getPolicyDefinition();
    
    public B getBinding();
    
    public Behaviour<P> getBehaviour();
    
}
