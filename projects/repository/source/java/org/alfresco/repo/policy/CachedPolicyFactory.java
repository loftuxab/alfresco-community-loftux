package org.alfresco.repo.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/*package*/ class CachedPolicyFactory<B extends BehaviourBinding, P extends Policy> extends PolicyFactory<B, P> 
{

    // TODO: Synchronisation
    
    private Map<B, P> singleCache = new HashMap<B, P>();
    private Map<B, Collection<P>> listCache = new HashMap<B, Collection<P>>();
    

    /*package*/ CachedPolicyFactory(Class<P> policyClass, BehaviourIndex<B> index)
    {
        super(policyClass, index);
        
        index.addChangeListener(new BehaviourChangeListener<B>()
        {
            public void addition(B binding, Behaviour behaviour)
            {
                clearCache(singleCache, binding);
                clearCache(listCache, binding);
            }
        });
    }
    
    @Override
    public P create(B key)
    {
        P policyInterface = singleCache.get(key);
        if (policyInterface == null)
        {
            policyInterface = super.create(key);
            singleCache.put(key, policyInterface);
        }
        return policyInterface;
    }
    

    @Override
    public Collection<P> createList(B key)
    {
        Collection<P> policyInterfaces = listCache.get(key);
        if (policyInterfaces == null)
        {
            policyInterfaces = super.createList(key);
            listCache.put(key, policyInterfaces);
        }
        
        return policyInterfaces;
    }
    
    
    private void clearCache(Map<B, ?> cache, B binding)
    {
        if (binding == null)
        {
            cache.clear();
        }
        else
        {
            Collection<B> invalidBindings = new ArrayList<B>();
            for (B cachedBinding : cache.keySet())
            {
                BehaviourBinding generalisedBinding = cachedBinding;
                while(generalisedBinding != null)
                {
                    if (generalisedBinding.equals(binding))
                    {
                        invalidBindings.add(cachedBinding);
                        break;
                    }
                    generalisedBinding = generalisedBinding.generaliseBinding();
                }
            }
            
            for (B invalidBinding : invalidBindings)
            {
                cache.remove(invalidBinding);
            }
        }
    }
    
    
}
