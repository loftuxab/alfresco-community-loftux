/**
 * 
 */
package org.alfresco.repo.rule.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.content.ContentReader;
import org.alfresco.repo.content.ContentService;
import org.alfresco.repo.content.ContentWriter;
import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.rule.Rule;
import org.alfresco.repo.rule.RuleServiceException;
import org.alfresco.repo.rule.RuleType;


/**
 * @author Roy Wetherall
 */
/*package*/ class RuleStore
{
    /**
     * The node service
     */
    private NodeService nodeService;
    
    /**
     * The content service
     */
    private ContentService contentService; 
    
    /**
     * The rule config
     */
    private RuleConfig ruleConfig;
    
    /**
     * Rule cache entries indexed by node reference
     */
    private Map<NodeRef, RuleCacheEntry> ruleCache = new HashMap<NodeRef, RuleCacheEntry>();
    
    /**
     * Constructor
     * 
     * @param nodeService       the node service
     * @param contentService    the content service
     * @param ruleConfig        the rule config
     */
    public RuleStore(
            NodeService nodeService, 
            ContentService contentService,
            RuleConfig ruleConfig)
    {
        this.nodeService = nodeService;
        this.contentService = contentService;
        this.ruleConfig = ruleConfig;
        
        // TODO need to register interest in the change event for any config folder (and its children!!)
        // TODO need to register interest in update to check for node changes that might effect the cache
        // TODO need to register interest in move as this would also effect the cache
    }
    
    public boolean hasRules(NodeRef nodeRef)
    {
        RuleCacheEntry ruleCacheEntry = getRuleCacheEntry(nodeRef);
        return ruleCacheEntry.hasRules();
    }
    
    public List<? extends Rule> getByRuleType(NodeRef nodeRef, RuleType ruleType)
    {
        RuleCacheEntry ruleCacheEntry = getRuleCacheEntry(nodeRef);
        return ruleCacheEntry.getRulesByRuleType(ruleType);
    }
    
    /**
     * Get a list of rules from the store.
     * 
     * @param nodeRef               the node reference
     * @param includeInherited      true if list includes inherited rules, false otherwise
     * @return                      a list of rules
     */
    public List<? extends Rule> get(NodeRef nodeRef, boolean includeInherited)
    {
        List<RuleImpl> result = null;
        RuleCacheEntry ruleCacheEntry = getRuleCacheEntry(nodeRef);
        
        if (includeInherited == true)
        {
            result = ruleCacheEntry.getRules();
        }
        else
        {
            result = ruleCacheEntry.getMyRules();
        }
        
        return result;
    }
    
    /**
     * 
     * @param nodeRef
     * @param rule
     */
    public void put(NodeRef nodeRef, RuleImpl rule)
    {
        // Write the rule to the repository
        writeRule(getConfigFolder(nodeRef), rule);
        
        RuleCacheEntry ruleCacheEntry = this.ruleCache.get(nodeRef);
        if (ruleCacheEntry != null)
        {
            ruleCacheEntry.dirtyMyRules();
        }
    }
    
    /**
     * 
     * @param nodeRef
     * @param rule
     */
    public void remove(NodeRef nodeRef, RuleImpl rule)
    {
        // Remove the entry from the cache
        this.ruleCache.remove(nodeRef);
        // TODO what do we do about the children
        
        // Delete the rule content from the repository
        NodeRef ruleContent = rule.getRuleContentNodeRef();
        if (ruleContent != null && this.nodeService.exists(ruleContent) == false)
        {
            this.nodeService.deleteNode(ruleContent);
        }
    }   
    
    private RuleCacheEntry getRuleCacheEntry(NodeRef nodeRef)
    {
        RuleCacheEntry ruleCacheEntry = this.ruleCache.get(nodeRef);
        if (ruleCacheEntry == null)
        {
            ruleCacheEntry = new RuleCacheEntry(nodeRef);
            this.ruleCache.put(nodeRef, ruleCacheEntry);
        }
        return ruleCacheEntry;
    }

    /**
     * 
     * @param configFolder
     * @param rule
     */
    private void writeRule(NodeRef configFolder, RuleImpl rule)
    {
        NodeRef ruleContent = rule.getRuleContentNodeRef();
        
        // Check that the rule content node still exists
        if (ruleContent != null && this.nodeService.exists(ruleContent) == false)
        {
            ruleContent = null;
        }
        
        if (ruleContent == null)
        {
            // Set the mime type and encoding
            Map<QName, Serializable> properties = new HashMap<QName, Serializable>(2);
            properties.put(DictionaryBootstrap.PROP_QNAME_MIME_TYPE, "text/xml");
            properties.put(DictionaryBootstrap.PROP_QNAME_ENCODING, "UTF-8");
            
            // Create the rule content node
            ruleContent = this.nodeService.createNode(
                    configFolder, 
                    null, 
                    DictionaryBootstrap.CHILD_ASSOC_QNAME_CONTAINS, 
                    DictionaryBootstrap.TYPE_QNAME_RULE_CONTENT,
                    properties).getChildRef();
            
            // Set the ruleContent node on the rule
            rule.setRuleContentNodeRef(ruleContent);
        }
        
        // Write the rule's XML representation to the node
        ContentWriter contentWriter = this.contentService.getUpdatingWriter(ruleContent);
        contentWriter.putContent(RuleXMLUtil.ruleToXML(rule));
    }
    
    /**
     * 
     * @param configFolder
     * @return
     */
    private List<RuleImpl> readRules(NodeRef configFolder)
    {
        // TODO need to cope with any folder structure containing rule content
        
        List<RuleImpl> rules = new ArrayList<RuleImpl>();
        List<ChildAssocRef> childAssocRefs = this.nodeService.getChildAssocs(configFolder);
        for (ChildAssocRef childAssocRef : childAssocRefs)
        {
            NodeRef nodeRef = childAssocRef.getChildRef();
            ContentReader contentReader = this.contentService.getReader(nodeRef);
            if (contentReader != null)
            {
                String ruleXML = contentReader.getContentString();
                RuleImpl rule = RuleXMLUtil.XMLToRule(this.ruleConfig, ruleXML);
                rules.add(rule);
            }
        }
        return rules;
    }

    /**
     * Get the node reference of the configuration folder for an actionable node.
     * 
     * @param nodeRef       the node reference to the actionable node
     * @return              the node reference to the configuration folder
     */
    private NodeRef getConfigFolder(NodeRef nodeRef)
    {
        // Get the configurations folder
        List<NodeAssocRef> nodeAssocRefs = this.nodeService.getTargetAssocs(
                                               nodeRef, 
                                               DictionaryBootstrap.ASSOC_QNAME_CONFIGURATIONS);
        if (nodeAssocRefs.size() == 0)
        {
            throw new RuleServiceException("The configuaration folder for this actionable node has not been set.");
        }
        
        return nodeAssocRefs.get(0).getTargetRef();
    }       
    
    /**
     * Rule cache entry
     * 
     * @author Roy Wetherall
     */
    private class RuleCacheEntry
    {
        private NodeRef nodeRef;
        
        private List<RuleImpl> myRules;
        private List<RuleImpl> allRules;
        private List<RuleImpl> inheritedRules;
        private List<RuleCacheEntry> parentEntries;
        private List<RuleCacheEntry> childEntries;
        private Map<String, List<RuleImpl>> allRulesByRuleType;
        
        /**
         * Constructor
         * 
         * @param nodeRef
         */
        public RuleCacheEntry(NodeRef nodeRef)
        {
            this.nodeRef = nodeRef;
            
            // TODO sort out how this links up with parents (and children??)
        }
        
        public void dirtyMyRules()
        {
            this.myRules = null;
            this.allRules = null;
            this.allRulesByRuleType = null;
            
            // TODO ... this has implications for the cached inherited rules ...
        }
        
        public boolean hasRules()
        {
            return (getRules().isEmpty() == false);
        }
        
        public List<RuleImpl> getRules()
        {
            if (this.allRules == null)
            {
                this.allRules = new ArrayList<RuleImpl>(getMyRules());
                this.allRules.addAll(getInheritedRules());
            }
            
            return this.allRules;
        }
        
        /**
         * 
         * @param ruleType
         * @return
         */
        public List<RuleImpl> getRulesByRuleType(RuleType ruleType)
        {
            if (this.allRulesByRuleType == null)
            {
                this.allRulesByRuleType = new HashMap<String, List<RuleImpl>>();
            }
            
            List<RuleImpl> result = this.allRulesByRuleType.get(ruleType.getName());
            if (result == null)
            {
                result = new ArrayList<RuleImpl>();
                for (RuleImpl rule : getRules())
                {
                    if (ruleType.getName().equals(rule.getRuleType().getName())== true)
                    {
                        result.add(rule);
                    }
                }
                
                this.allRulesByRuleType.put(ruleType.getName(), result);
            }
            
            return result;
        }

        public List<RuleImpl> getMyRules()
        {
            if (this.myRules == null)
            {
                this.myRules = readRules(getConfigFolder(this.nodeRef));
            }
            return this.myRules;
        }
        
        public List<RuleImpl> getInheritedRules()
        {
            if (this.inheritedRules == null)
            {
                // TODO
                
                this.inheritedRules = new ArrayList<RuleImpl>();
            }
            return this.inheritedRules;
        }
    }
}
