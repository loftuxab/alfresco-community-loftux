package org.alfresco.service.cmr.security;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alfresco.api.AlfrescoPublicApi;  
import org.alfresco.service.namespace.QName;

@AlfrescoPublicApi
public class PermissionContext 
{
    private QName type;
    
    private HashSet<QName> aspects = new HashSet<QName>();
    
    private Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
    
    private Map<String, Set<String>> dynamicAuthorityAssignment = new HashMap<String, Set<String>>();
    
    private Map<String, Object> additionalContext = new HashMap<String, Object>();
    
    private Long storeAcl = null;
    
    public PermissionContext(QName type)
    {
        this.type = type;
    }

    public HashSet<QName> getAspects()
    {
        return aspects;
    }

    public Map<String, Set<String>> getDynamicAuthorityAssignment()
    {
        return dynamicAuthorityAssignment;
    }

    public void addDynamicAuthorityAssignment(String user, String dynamicAuthority)
    {
        Set<String> dynamicAuthorities = dynamicAuthorityAssignment.get(user);
        if(dynamicAuthorities == null)
        {
            dynamicAuthorities = new HashSet<String>();
            dynamicAuthorityAssignment.put(user, dynamicAuthorities);
        }
        dynamicAuthorities.add(dynamicAuthority);
    }
    
    public Map<String, Object> getAdditionalContext()
    {
        return additionalContext;
    }

    public Map<QName, Serializable> getProperties()
    {
        return properties;
    }

    public QName getType()
    {
        return type;
    }

    public Long getStoreAcl()
    {
        return storeAcl;
    }

    public void setStoreAcl(Long storeAcl)
    {
        this.storeAcl = storeAcl;
    }
    
    
    
}
