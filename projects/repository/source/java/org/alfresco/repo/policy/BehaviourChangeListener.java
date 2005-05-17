package org.alfresco.repo.policy;



/*package*/ interface BehaviourChangeListener<B extends BehaviourBinding>
{
    public void addition(B binding, Behaviour behaviour);

}
