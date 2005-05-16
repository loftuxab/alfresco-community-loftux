package org.alfresco.repo.policy;

import java.util.Collection;


public interface BehaviourIndex<B>
{
    public Collection<BehaviourDefinition<? extends Object>> getAll();
    
    public Collection<BehaviourDefinition<? extends Object>> find(B binding);

    public void addChangeListener(BehaviourChangeListener<B> listener);
}
