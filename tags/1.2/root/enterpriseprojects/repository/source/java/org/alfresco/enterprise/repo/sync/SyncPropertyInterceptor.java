/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.DynamicNamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Interceptor to inject attributes as node properties 
 * <p>
 * Primary use case is to inject transient errors as sync set member node properties 
 * 
 * @see org.alfresco.service.cmr.repository.NodeService#getProperty(NodeRef, QName)
 * @see org.alfresco.service.cmr.repository.NodeService#getProperties(NodeRef)
 * @see org.alfresco.service.cmr.repository.NodeService#setProperty(NodeRef, QName, Serializable)
 * @see org.alfresco.service.cmr.repository.NodeService#setProperties(NodeRef, Map)
 * 
 * @author Mark Rogers
 * @since 4.1
 */
public class SyncPropertyInterceptor implements MethodInterceptor
{
    private static Log logger = LogFactory.getLog(SyncPropertyInterceptor.class);
    
    /** Direct access to the NodeService */
    private NodeService nodeService;
    private NamespaceService namespaceService;
    private AttributeService attributeService;
    
    private boolean enabled = true;
    
     /** 
     * Decorate this aspect 
     */
    private QName aspectQName;
    
    /**
     * Attribute key to read
     */
    public final static Serializable keys[] = {"sync", "transientErrorCode"};
    
    /**
     * The property name to inject with the value from the attribute service
     */
    private QName injectQName;
    
    public void setInjectName(String injectName)
    {
        QName injectQName = QName.createQName(injectName, namespaceService);
        setInjectQName(injectQName);
    }

    
    public void setAspectName(String aspectName)
    {
        QName aspectQName = QName.createQName(aspectName, namespaceService);
        setAspectQName(aspectQName);
    }
    public void setAspectQName(QName aspectName)
    {
        this.aspectQName = aspectName;
    }
    
    public void setInjectQName(QName propertyName)
    {
        this.injectQName = propertyName;
    }
    
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
    
    public void setNodeService(NodeService bean)
    {
        this.nodeService = bean;
    }

    public void setAttributeService(AttributeService attributeService)
    {
        this.attributeService = attributeService;
    }
    
    public void setNamespaceService(NamespaceService namespaceService)
    {
        DynamicNamespacePrefixResolver dynamic =  new DynamicNamespacePrefixResolver(namespaceService);
        dynamic.registerNamespace("sync", SyncModel.SYNC_MODEL_1_0_URI);
        this.namespaceService = dynamic;
        
    }
    
    public void init()
    {
        PropertyCheck.mandatory(this, "attributeService", attributeService);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "aspectQName", aspectQName);
        PropertyCheck.mandatory(this, "injectQName", injectQName);
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "namespaceService", namespaceService);
    }
    
    @SuppressWarnings("unchecked")
    public Object invoke(final MethodInvocation invocation) throws Throwable
    {       
        if (!enabled)
        {
            // Don't interfere, just go ahead.
            return invocation.proceed();
        }
        
        Object ret = null;
        
        final String methodName = invocation.getMethod().getName();
        final Object[] args = invocation.getArguments();
        
        if (methodName.equals("getProperty"))
        {
            NodeRef nodeRef = (NodeRef) args[0];
            QName propertyQName = (QName) args[1];
            
            if(injectQName != null && injectQName.equals(propertyQName))
            {
                // Get the transient error code and inject it   
                Serializable attribute = attributeService.getAttribute(keys);
                // return the attribute
                ret = attribute;
            }
            else
            {
                ret = (Serializable) invocation.proceed();
            }    
        }
        else if (methodName.equals("getProperties"))
        {
            NodeRef nodeRef = (NodeRef) args[0];
            
            Map<QName, Serializable> properties = (Map<QName, Serializable>) invocation.proceed();
            Map<QName, Serializable> convertedProperties = new HashMap<QName, Serializable>(properties.size() * 2);
            convertedProperties.putAll(properties);
           
            if(nodeService.hasAspect(nodeRef, aspectQName))
            {
                // Get the transient error code and inject it
                Serializable attribute = attributeService.getAttribute(keys);
                if(attribute != null)
                {
                    convertedProperties.put(injectQName, attribute);
                }
            }
            ret = convertedProperties;
        }
        else if (methodName.equals("setProperties"))
        {
            NodeRef nodeRef = (NodeRef) args[0];
            Map<QName, Serializable> newProperties =(Map<QName, Serializable>) args[1];
            
            if(newProperties.containsKey(injectQName))
            {
                Map<QName, Serializable> convertedProperties = new HashMap<QName, Serializable>(newProperties.size() * 2);
                convertedProperties.putAll(newProperties);
                convertedProperties.remove(injectQName);
          
                // Now complete the call by passing the converted properties
                nodeService.setProperties(nodeRef, convertedProperties);
            }
            else
            {
                invocation.proceed();
            }
            // Done
        }
        else if (methodName.equals("addProperties"))
        {
            NodeRef nodeRef = (NodeRef) args[0];
            Map<QName, Serializable> newProperties =(Map<QName, Serializable>) args[1];
            
            if(newProperties.containsKey(injectQName))
            {
                // remove the spoofed property
                Map<QName, Serializable> convertedProperties = new HashMap<QName, Serializable>(newProperties.size() * 2);
                convertedProperties.putAll(newProperties);
                convertedProperties.remove(injectQName);
          
                // Now complete the call by passing the converted properties
                nodeService.addProperties(nodeRef, convertedProperties);
            }
            else
            {
                invocation.proceed();
            }
            // Done
        }
        else if (methodName.equals("setProperty"))
        {
            NodeRef nodeRef = (NodeRef) args[0];
            QName propertyQName = (QName) args[1];
            Serializable inboundValue = (Serializable) args[2];
            
            //TODO Ignore spoofed property
            if(!propertyQName.equals(injectQName))
            {
                // Not the spoofed property
                invocation.proceed();
            }
            // Done
        }
        else if (methodName.equals("getAspects"))
        {
            NodeRef nodeRef = (NodeRef) args[0];
            
            if(nodeService.hasAspect(nodeRef, aspectQName))
            {
                if(attributeService.exists(keys))
                {
                    Set<QName> stuff = (Set<QName>)invocation.proceed();
                    
                    Set<QName> retVal = new HashSet<QName>();
                    if(stuff != null)
                    {
                        retVal.addAll(stuff);
                    }
                    retVal.add(QName.createQName("sync:transientError", namespaceService)); 
                    
                    ret = retVal;
                }
                else
                {
                    ret = invocation.proceed();
                }
            }
            else
            {
                ret = invocation.proceed();
            }
        }
        else
        {
            ret = invocation.proceed();
        }
        // done
        return ret;
    }
}
