/**
 * Created on Apr 27, 2005
 */
package org.alfresco.repo.policy.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.policy.PolicyRuntimeService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;

/**
 * Policy runtim service default implementation
 * 
 * @author Roy Wetherall
 */
public class PolicyRuntimeServiceImpl implements PolicyRuntimeService
{
    /**
     * The registered behaviours are stored here
     */
    private HashMap<BehaviourKey, Set<? extends Object>> behaviourMap = new HashMap<BehaviourKey, Set<? extends Object>>();
    
    /**
     * @see PolicyRuntimeService#registerClassBehaviour(java.lang.Object, org.alfresco.repo.ref.QName)
     */
    public <T> void registerBehaviour(Class<T> policy, T policyImpl, QName qname)
    {
        // TODO should check that the policy is registered with the policy defintion service
        
        BehaviourKey key = new BehaviourKey(policy, qname);
        
        Set<T> currentBehaviours = (Set<T>)behaviourMap.get(key);
        if (currentBehaviours == null)
        {
            currentBehaviours = new HashSet<T>();            
        }
        
        currentBehaviours.add(policyImpl);     
        behaviourMap.put(key, currentBehaviours);
    }
    
    /**
     * @see PolicyRuntimeService#registerClassBehaviour(Class<T>, T, ClassRef)
     */
    public <T> void registerClassBehaviour(Class<T> policy, T policyImpl, ClassRef classRef)
    {
        registerBehaviour(policy, policyImpl, classRef.getQName());
    }
    
    /**
     * @see PolicyRuntimeService#getClassBehaviour(Class<T>, QName)
     */
    public <T> T getBehaviour(Class<T> policy, QName qname)
    {
        return getBehaviourProxy(policy, getBehaviourImpl(policy, qname));
    }

    /**
     * 
     * @param <T>
     * @param policy
     * @param qname
     * @return
     */
    private <T> Set<T> getBehaviourImpl(Class<T> policy, QName qname)
    {       
        BehaviourKey key = new BehaviourKey(policy, qname);        
        return (Set<T>)behaviourMap.get(key);
    }
    
    /**
     * @see
     */
    public <T> T getClassBehaviour(Class<T> policy, ClassRef classRef)
    {
        return getBehaviourProxy(policy, getClassBehaviourImpl(policy, classRef));
    }
    
    /**
     * 
     * @param <T>
     * @param policy
     * @param classRef
     * @return
     */
    public <T> Set<T> getClassBehaviourImpl(Class<T> policy, ClassRef classRef)
    {
        // TODO this is not as simple as this ... need to pick up sub types as well !!
        // Go through all parent types calling get behaviour as you go !!
         
        return getBehaviourImpl(policy, classRef.getQName());
    }
    
    /**
     * @see PolicyRuntimeService#getClassBehaviour(Class<T>, NodeService, NodeRef)
     */
    public <T> T getClassBehaviour(Class<T> policy, NodeService nodeService, NodeRef nodeRef)
    {
        return getBehaviourProxy(policy, getClassBehaviourImpl(policy, nodeService, nodeRef));
    }    

    /**
     * 
     * @param <T>
     * @param policy
     * @param nodeRef
     * @return
     */
    public <T> Set<T> getClassBehaviourImpl(Class<T> policy, NodeService nodeService, NodeRef nodeRef)
    {
        Set<T> result = new HashSet<T>();
         
        // First get the type of the node ref
        ClassRef classRef = nodeService.getType(nodeRef);
        Set<T> classBehaviours = getClassBehaviourImpl(policy, classRef); 
        if (classBehaviours != null)
        {
           result.addAll(classBehaviours);
        }
         
        // Next get all the aspects of the nodeRef
        Set<ClassRef> apects = nodeService.getAspects(nodeRef);
        for (ClassRef aspect : apects)
        {
            Set<T> aspectBehaviours = getClassBehaviourImpl(policy, aspect);
            if (aspectBehaviours != null)
            {
               result.addAll(aspectBehaviours);
            }
        }
         
        return result;
    }

    /**
     * 
     * @param <T>
     * @param policy
     * @param behaviours
     * @return
     */
    private <T> T getBehaviourProxy(Class<T> policy, Set<T> behaviours)
    {
        T result = null;
        
        if (behaviours != null && behaviours.size() != 0)
        {
            if (behaviours.size() == 1)
            {
                // Return the single result
                Object[] temp = behaviours.toArray();
                result = (T)temp[0];
            }
            else
            {
                // Create a proxy to contain all the behaviours
                MultiBehaviourInvocationHandler<T> handler = new MultiBehaviourInvocationHandler<T>(behaviours);
                result = (T)Proxy.newProxyInstance(
                        PolicyRuntimeServiceImpl.class.getClassLoader(),
                        new Class[]{policy},
                        handler);
            }
        }
        else
        {
            // Create a proxy that is a shell and does nothing
            NoBehaviourInvocationHandler handler = new NoBehaviourInvocationHandler();
            result = (T)Proxy.newProxyInstance(
                    PolicyRuntimeServiceImpl.class.getClassLoader(),
                    new Class[]{policy},
                    handler);
        }
        
        return result;
    }
    
    /**
     * Used to key the registered behaviour
     * 
     * @author Roy Wetherall
     */
    private class BehaviourKey implements Serializable
    {
        private static final long serialVersionUID = 3979265841814910265L;
        private Class policy = null;
        private QName qname = null;
        
        public BehaviourKey(Class policy, QName qname)
        {
            this.policy = policy;
            this.qname = qname;
        }
        
        public Class getPolicy()
        {
            return this.policy;
        }
        
        public QName getQName()
        {
            return this.qname;
        }
        
        @Override
        public boolean equals(Object obj)
        {
            return ((this.policy.equals(((BehaviourKey)obj).policy) == true) &&
                    (this.qname.equals(((BehaviourKey)obj).qname) == true));
        }
        
        @Override
        public int hashCode()
        {
            return this.policy.hashCode() + this.qname.hashCode();
        }
        
        public String toString()
        {
            return this.policy.getName() + "#" + this.qname.toString();
        }
    }    
    
    /**
     * Proxy invocation handler that can be used to contain many bahaviours behind
     * the facard of one.
     * <p>
     * The order in which the behaviors get invoked is not guarenteed.
     * 
     * @author Roy Wetherall
     */
    private class MultiBehaviourInvocationHandler <T> implements InvocationHandler
    {
        /**
         * The behaviours
         */
        private Set<T> behaviours;
        
        /**
         * Constructor
         */
        public MultiBehaviourInvocationHandler(Set<T> behaviours)
        {
            this.behaviours = behaviours;
        }
        
        /**
         * Invocation handler invoke method
         */
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            for (T behaviour : this.behaviours)
            {
                method.invoke(behaviour, args);
            }  
            
            return null;
        }
    }
    
    /**
     * Proxy invocation handler that can be returned when no behaviour is present.  Can be called 
     * with no effect.
     * 
     * @author Roy Wetherall
     */
    private class NoBehaviourInvocationHandler implements InvocationHandler
    {
        /**
         * Invocation handler incoke method
         */
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            // Do nothing
            return null;
        }
    }
}
