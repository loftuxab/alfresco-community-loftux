package org.alfresco.repo.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*package*/ class BehaviourMap<B, P extends Policy>
{

    private Map<B, BehaviourDefinition<B, ? extends P>> index = new HashMap<B, BehaviourDefinition<B, ? extends P>>();
    private List<BehaviourChangeListener<B, ? extends P>> listeners = new ArrayList<BehaviourChangeListener<B, ? extends P>>();
    

    public void addChangeListener(BehaviourChangeListener<B, P> listener)
    {
        listeners.add(listener);
    }
    
    
    public void put(B binding, BehaviourDefinition<B, ? extends P> behaviour)
    {
        index.put(binding, behaviour);
        for (BehaviourChangeListener<B, ? extends P> listener : listeners)
        {
            listener.addition(binding, behaviour.getBehaviour());
        }
    }
    
    public BehaviourDefinition<B, ? extends P> get(B binding)
    {
        return index.get(binding);
    }

    
    public Collection<BehaviourDefinition<B, ? extends P>> getAll()
    {
        return index.values();
    }
    
    public int size()
    {
        return index.size();
    }
    
}
