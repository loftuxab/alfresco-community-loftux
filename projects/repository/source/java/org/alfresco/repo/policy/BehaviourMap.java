package org.alfresco.repo.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*package*/ class BehaviourMap<B extends BehaviourBinding>
{

    private Map<B, BehaviourDefinition<B>> index = new HashMap<B, BehaviourDefinition<B>>();
    private List<BehaviourChangeListener<B>> listeners = new ArrayList<BehaviourChangeListener<B>>();
    

    public void addChangeListener(BehaviourChangeListener<B> listener)
    {
        listeners.add(listener);
    }
    
    
    public void put(BehaviourDefinition<B> behaviourDefinition)
    {
        B binding = behaviourDefinition.getBinding();
        index.put(binding, behaviourDefinition);
        for (BehaviourChangeListener<B> listener : listeners)
        {
            listener.addition(binding, behaviourDefinition.getBehaviour());
        }
    }
    
    public BehaviourDefinition<B> get(B binding)
    {
        return index.get(binding);
    }

    
    public Collection<BehaviourDefinition<B>> getAll()
    {
        return index.values();
    }
    
    public int size()
    {
        return index.size();
    }
    
}
