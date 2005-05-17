package org.alfresco.repo.policy;

import java.util.Collection;


/*package*/ interface BehaviourIndex<B extends BehaviourBinding>
{
    public Collection<BehaviourDefinition> getAll();
    
    public Collection<BehaviourDefinition> find(B binding);

    public void addChangeListener(BehaviourChangeListener<B> listener);
}
