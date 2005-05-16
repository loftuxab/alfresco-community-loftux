package org.alfresco.repo.policy;

import java.util.Collection;


public interface BehaviourIndex<B, P extends Policy>
{
    public Collection<BehaviourDefinition<? extends Object, ? extends P>> getAll();
    
    public Collection<BehaviourDefinition<? extends Object, ? extends P>> find(B binding);

    public void addChangeListener(BehaviourChangeListener<B, P> listener);
}
