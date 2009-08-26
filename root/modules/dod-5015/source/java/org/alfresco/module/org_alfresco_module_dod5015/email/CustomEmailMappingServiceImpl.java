package org.alfresco.module.org_alfresco_module_dod5015.email;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.content.metadata.RFC822MetadataExtracter;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;

public class CustomEmailMappingServiceImpl implements CustomEmailMappingService
{
    
    private RFC822MetadataExtracter extracter;
    private NodeService nodeService;
    private NamespacePrefixResolver nspr;
    
    /**
     * Get the name space prefix resolver
     * @return the name space prefix resolver
     */
    public NamespacePrefixResolver getNamespacePrefixResolver()
    {
        return nspr;
    }

    /**
     * Set the name space prefix resolver
     * @param nspr
     */
    public void setNamespacePrefixResolver(NamespacePrefixResolver nspr)
    {
        this.nspr = nspr;
    }
    
    
    public void init()
    {
        // TODO Need to retrieve custom properties
    }
    
    public Set<CustomMapping> getCustomMappings()
    {
      
        // add all the lists data to a Map
        Set<CustomMapping> emailMap = new HashSet<CustomMapping>();
        
        Map<String, Set<QName>> currentMapping = extracter.getCurrentMapping();
        
        for(String key : currentMapping.keySet())
        {
            Set<QName> set = currentMapping.get(key);
            
            for(QName qname : set)
            {
                CustomMapping value = new CustomMapping();
                value.setFrom(key);
                QName resolvedQname = qname.getPrefixedQName(nspr);
                value.setTo(resolvedQname.toPrefixString());  
                emailMap.add(value);
            }
        }
        
        return emailMap;
    }
    

    public void addCustomMapping(String from, String to)
    {
        // Get the read only existing configuration
        Map<String, Set<QName>> currentMapping = extracter.getCurrentMapping();
        
        Map<String, Set<QName>> newMapping = new HashMap<String, Set<QName>>(17);
        newMapping.putAll(currentMapping);
        
        QName newQName = QName.createQName(to, nspr);
        
        Set<QName> values = newMapping.get(from);
        if(values == null)
        {
            values = new HashSet<QName>();
            newMapping.put(from, values);
        }
        values.add(newQName);
        
        // Crash in the new config.
        extracter.setMapping(newMapping);
    }

    public void deleteCustomMapping(String from, String to)
    {
        // Get the read only existing configuration
        Map<String, Set<QName>> currentMapping = extracter.getCurrentMapping();
        
        Map<String, Set<QName>> newMapping = new HashMap<String, Set<QName>>(17);
        newMapping.putAll(currentMapping);
        
        QName oldQName = QName.createQName(to, nspr);
        
        Set<QName> values = newMapping.get(from);
        if(values != null)
        {
            values.remove(oldQName);
        }
        
        // Crash in the new config.
        extracter.setMapping(newMapping);
    }

    public void setExtracter(RFC822MetadataExtracter extractor)
    {
        this.extracter = extractor;
    }

    public RFC822MetadataExtracter getExtracter()
    {
        return extracter;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public NodeService getNodeService()
    {
        return nodeService;
    }
}
