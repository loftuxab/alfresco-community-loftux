package org.alfresco.repo.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.util.ParameterCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;


/**
 * This factory provides component redirection based on Store or Node
 * References passed into the component.
 *
 * Redirection is driven by StoreRef and NodeRef parameters.  If none
 * are given in the method call, the default component is called.  Otherwise,
 * the store type is extracted from these parameters and the appropriate
 * component called for the store type. 
 * 
 * An error is thrown if multiple store types are found.
 * 
 * @author David Caruana
 *
 * @param <I>  The component interface class
 */
public class StoreRedirectorProxyFactory<I> implements FactoryBean, InitializingBean
{
    // Logger
    private static final Log logger = LogFactory.getLog(StoreRedirectorProxyFactory.class);

    // The component interface class
    private Class<I> proxyInterface = null;
    
    // The default component binding
    private I binding = null;
    
    // The map of store types to component bindings
    private Map<String, I> redirectedBindings = null; 

    // The proxy responsible for redirection based on store type
    private I redirectorProxy = null;


    /**
     * Sets the proxy interface
     * 
     * @param proxyInterface  the proxy interface
     */
    public void setProxyInterface(Class<I> proxyInterface)
    {
        this.proxyInterface = proxyInterface;
    }

    /**
     * Sets the default component binding
     * 
     * @param binding  the component to call by default
     */
    public void setDefaultBinding(I binding)
    {
        this.binding = binding;
    }
    
    /**
     * Sets the binding of store type to component
     * 
     * @param bindings  the bindings
     */
    public void setRedirectedBindings(Map<String, I> bindings)
    {
        this.redirectedBindings = bindings;
    }
    

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet()
        throws ServiceException
    {
        ParameterCheck.mandatory("Proxy Interface", proxyInterface);
        ParameterCheck.mandatory("Default Binding", binding);
        ParameterCheck.mandatory("Redirected Bindings", binding);

        // Setup the redirector proxy
        this.redirectorProxy = (I)Proxy.newProxyInstance(proxyInterface.getClassLoader(), 
                new Class[] {proxyInterface, StoreRedirector.class}, new RedirectorInvocationHandler());
    }


    /* (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public I getObject()
    {
        return redirectorProxy;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class getObjectType()
    {
        return proxyInterface;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton()
    {
        return true;
    }

    
    /**
     * Invocation handler that redirects based on store type
     */
    /*package*/ class RedirectorInvocationHandler
        implements InvocationHandler, StoreRedirector
    {
        /* (non-Javadoc)
         * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
         */
        public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable
        {
            // Handle StoreRedirector Interface
            if (method.getDeclaringClass().equals(StoreRedirector.class))
            {
                return method.invoke(this, args);
            }
            
            // Otherwise, determine the apropriate implementation to invoke for the service interface method
            Object binding = null;
            String storeType = getStoreType(args);
            if (storeType == null)
            {
                binding = StoreRedirectorProxyFactory.this.binding;
            }
            else
            {
                binding = StoreRedirectorProxyFactory.this.redirectedBindings.get(storeType);
                if (binding ==  null)
                {
                    throw new ServiceException("Store type " + storeType + " is not supported");
                }
            }
            
            if (logger.isDebugEnabled())
                logger.debug("Redirecting method " + method + " to binding " + binding + " based on store type " + storeType);
            
            // Invoke the implementation
            return method.invoke(binding, args);
        }

        
        /* (non-Javadoc)
         * @see org.alfresco.repo.service.ServiceDescriptor#getSupportedStores()
         */
        public Collection<String> getSupportedStores()
        {
            return Collections.unmodifiableCollection(StoreRedirectorProxyFactory.this.redirectedBindings.keySet());
        }
        

        /**
         * Determine store type from array of method arguments
         * 
         * @param args  the method arguments
         * @return  the store type (or null, if one is not specified)
         */
        private String getStoreType(Object[] args)
        {
            String storeType = null;
            
            for (Object arg : args)
            {
                // Extract store type from argument, if store type provided
                String argStoreType = null;
                if (arg instanceof NodeRef)
                {
                    argStoreType = ((NodeRef)arg).getStoreRef().getProtocol();
                }
                else if (arg instanceof StoreRef)
                {
                    argStoreType = ((StoreRef)arg).getProtocol();
                }
                
                // Only allow one store type
				if (argStoreType != null)
				{
					if (storeType != null && !storeType.equals(argStoreType))
					{
	                    throw new ServiceException("Multiple store types are not supported - types " + storeType + " and " + argStoreType + " passed");
					}
                    storeType = argStoreType;
				}
            }
            
            return storeType;
        }
    }

}
