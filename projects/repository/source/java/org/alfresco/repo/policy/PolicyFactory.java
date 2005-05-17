package org.alfresco.repo.policy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/*package*/ class PolicyFactory<B extends BehaviourBinding, P extends Policy>
{

    private BehaviourIndex<B> index;
    private Class<P> policyClass;
    
    /*package*/ PolicyFactory(Class<P> policyClass, BehaviourIndex<B> index)
    {
        this.policyClass = policyClass;
        this.index = index;
    }
    
    
    public P create(B binding)
    {
        Collection<P> policyInterfaces = createList(binding);
        return toPolicy(policyInterfaces);
    }
    

    public Collection<P> createList(B binding)
    {
        List<P> policyInterfaces = new ArrayList<P>();
        Collection<BehaviourDefinition> behaviourDefs = index.find(binding);
        for (BehaviourDefinition behaviourDef : behaviourDefs)
        {
            PolicyDefinition policyDef = behaviourDef.getPolicyDefinition();
            Behaviour behaviour = behaviourDef.getBehaviour();
            P policyIF = behaviour.getInterface(policyClass);
            policyInterfaces.add(policyIF);
        }
        
        return policyInterfaces;
    }
    
    
    public P toPolicy(Collection<P> policyList)
    {
        if (policyList.size() == 0)
        {
            return (P)Proxy.newProxyInstance(policyList.getClass().getClassLoader(), new Class[]{policyClass}, new NOOPHandler());
        }
        else if (policyList.size() == 1)
        {
            return policyList.iterator().next();
        }
        else
        {
            return (P)Proxy.newProxyInstance(policyList.getClass().getClassLoader(), new Class[]{policyClass}, new MultiHandler<P>(policyList));
        }
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
