package org.alfresco.repo.policy;



public interface BehaviourChangeListener<B, P extends Policy>
{
    public void addition(B binding, Behaviour<? extends P> behaviour);

}
