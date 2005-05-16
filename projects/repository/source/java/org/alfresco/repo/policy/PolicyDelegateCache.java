package org.alfresco.repo.policy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*package*/ class PolicyDelegateCache<B, IP extends Policy, P extends IP>
{

    private BehaviourIndex<B, IP> index;
    private Class<P> policyClass;

    private Map<B, P> aggregateCache = new HashMap<B, P>();
    private Map<B, List<P>> listCache = new HashMap<B, List<P>>();
    

    /*package*/ PolicyDelegateCache(Class<P> policyClass, BehaviourIndex<B, IP> index)
    {
        this.policyClass = policyClass;
        this.index = index;
        this.index.addChangeListener(new BehaviourChangeListener<B, IP>()
        {
            public void addition(B binding, Behaviour<? extends IP> behaviour)
            {
                if (binding == null)
                {
                    aggregateCache.clear();
                    listCache.clear();
                }
                else
                {
                    aggregateCache.remove(binding);
                    listCache.remove(binding);
                }
            }
        });
    }
    
    
    public P get(B key)
    {
        P policyInterface = aggregateCache.get(key);
        if (policyInterface == null)
        {
            Collection<P> policyInterfaces = getList(key);
            if (policyInterfaces.size() == 0)
            {
                policyInterface = (P)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{policyClass}, new NOOPHandler());
            }
            else if (policyInterfaces.size() == 1)
            {
                policyInterface = policyInterfaces.iterator().next();
            }
            else
            {
                policyInterface = (P)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{policyClass}, new MultiHandler<P>(policyInterfaces));
            }
            aggregateCache.put(key, policyInterface);
        }
        
        return policyInterface;
    }
    

    public Collection<P> getList(B key)
    {
        List<P> policyInterfaces = listCache.get(key);
        if (policyInterfaces == null)
        {
            policyInterfaces = new ArrayList<P>();
            Collection<BehaviourDefinition<? extends Object, ? extends IP>> behaviourDefs = index.find(key);
            for (BehaviourDefinition<? extends Object, ? extends IP> behaviourDef : behaviourDefs)
            {
                PolicyDefinition policyDef = behaviourDef.getPolicyDefinition();
                Behaviour<? extends IP> behaviour = behaviourDef.getBehaviour();
                P policyIF = behaviour.getInterface(policyClass);
                policyInterfaces.add(policyIF);
            }
            
            listCache.put(key, policyInterfaces);
        }
        
        return policyInterfaces;
    }
    
    
    
    private static class NOOPHandler implements InvocationHandler
    {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            // TODO: Handle toString, equals & hashCode
            
            return null;
        }
    }
    
    
    private static class MultiHandler<P> implements InvocationHandler
    {
        private Collection<P> policyInterfaces;
        
        public MultiHandler(Collection<P> policyInterfaces)
        {
            this.policyInterfaces = policyInterfaces;
        }
        
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            // TODO: Handle toString, equals & hashCode
            
            Object result = null;
            for (P policyInterface : policyInterfaces)
            {
                result = method.invoke(policyInterface, args);
            }  
            return result;
        }
    }
    
}
