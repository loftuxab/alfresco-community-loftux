package org.alfresco.repo.policy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.util.ParameterCheck;


public class JavaBehaviour implements Behaviour
{

    private Object instance;
    private String method;
    private Map<Class, Object> proxies = new HashMap<Class, Object>();
    
    
    public JavaBehaviour(Object instance, String method)
    {
        ParameterCheck.mandatory("Instance", instance);
        ParameterCheck.mandatory("Method", method);
        this.instance = instance;
        this.method = method;
    }


    public synchronized <T> T getInterface(Class<T> policy)
    {
        ParameterCheck.mandatory("Policy class", policy);
        Object proxy = proxies.get(policy);
        if (proxy == null)
        {
            InvocationHandler handler = getDelegateHandler(instance, method, policy);
            proxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{policy}, handler);
            proxies.put(policy, proxy);
        }
        return (T)proxy;
    }

    public String getDescription()
    {
        return "Java behaviour[class=" + instance.getClass() + ", method=" + method + ", instance=" + instance.toString();
    }

    
    private <T> InvocationHandler getDelegateHandler(Object instance, String method, Class<T> policyIF)
    {
        Method[] policyIFMethods = policyIF.getMethods();
        if (policyIFMethods.length != 1)
        {
            throw new PolicyException("Policy interface " + policyIF.getCanonicalName() + " must have only one method");
        }

        try
        {
            Class instanceClass = instance.getClass();
            Method delegateMethod = instanceClass.getMethod(method, (Class[])policyIFMethods[0].getParameterTypes());
            return new DelegateHandler(instance, delegateMethod);
        }
        catch (NoSuchMethodException e)
        {
            throw new PolicyException("Method " + method + " not found or accessible on " + instance.getClass(), e);
        }
    }
    
    

    private static class DelegateHandler implements InvocationHandler
    {

        private Object instance;
        private Method delegateMethod;
        
        
        public DelegateHandler(Object instance, Method delegateMethod)
        {
            this.instance = instance;
            this.delegateMethod = delegateMethod;
        }

        
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            // TODO: Handle toString, equals & hashCode
            
            try
            {
                return delegateMethod.invoke(instance, args);
            }
            catch (InvocationTargetException e)
            {
                throw e.getCause();
            }
            
        }
    }
    
    
}
