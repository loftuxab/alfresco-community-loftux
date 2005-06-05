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
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.rule.RuleServiceException;


/**
 * @author Roy Wetherall
 */
/*package*/ class RuleStore
{
    /**
     * Tempory model constants 
     */
    public static final QName ASSOC_QNAME_CONFIGURATIONS = QName.createQName(NamespaceService.ALFRESCO_URI, "configurations");
    private static final QName CHILD_ASSOC_QNAME_CONTAINS = QName.createQName(NamespaceService.ALFRESCO_URI, "contains");
    
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
    }
    
    public List<RuleImpl> get(NodeRef nodeRef)
    {
        RuleCacheEntry ruleCacheEntry = this.ruleCache.get(nodeRef);
        if (ruleCacheEntry == null)
        {
            ruleCacheEntry = new RuleCacheEntry(nodeRef);
        }
        
        return ruleCacheEntry.getRules();
    }
    
    public void put(NodeRef nodeRef, RuleImpl rule)
    {
        // Write the rule to the repository
        writeRule(getConfigFolder(nodeRef), rule);
        
        //dirtyCache(nodeRef, rule);        
    }
    
    public void remove(NodeRef nodeRef, RuleImpl rule)
    {
        throw new UnsupportedOperationException();
    }
    
    private void dirtyCache(NodeRef nodeRef, RuleImpl rule)
    {
        throw new UnsupportedOperationException();
    }

    private void writeRule(NodeRef configFolder, RuleImpl rule)
    {
        NodeRef ruleContent = rule.getRuleContentNodeRef();
        
        // TODO should check to make sure that the node is valid
        
        if (ruleContent == null)
        {
            // Set the mime type and encoding
            Map<QName, Serializable> properties = new HashMap<QName, Serializable>(2);
            properties.put(DictionaryBootstrap.PROP_QNAME_MIME_TYPE, "text/xml");
            properties.put(DictionaryBootstrap.PROP_QNAME_ENCODING, "UTF-8");
            
            // Create the rule content node
            // TODO should be creating a rule content node a content node
            ruleContent = this.nodeService.createNode(
                    configFolder, 
                    null, 
                    CHILD_ASSOC_QNAME_CONTAINS, 
                    DictionaryBootstrap.TYPE_QNAME_CONTENT,
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
        // TODO use the correct const for the assoc QName
        List<NodeAssocRef> nodeAssocRefs = this.nodeService.getTargetAssocs(
                                               nodeRef, 
                                               ASSOC_QNAME_CONFIGURATIONS);
        if (nodeAssocRefs.size() == 0)
        {
            throw new RuleServiceException("The configuaration folder for this actionable node has not been set.");
        }
        
        return nodeAssocRefs.get(0).getTargetRef();
    }       
    
    private class RuleCacheEntry
    {
        private NodeRef nodeRef;
        private List<RuleImpl> myRules;
        private List<RuleImpl> inheritedRules;
        //private List<RuleImpl> allRules;
        private List<RuleCacheEntry> parentEntries;
        private List<RuleCacheEntry> childEntries;
        
        public RuleCacheEntry(NodeRef nodeRef)
        {
            this.nodeRef = nodeRef;
        }
        
        public List<RuleImpl> getRules()
        {
            //if (this.allRules == null)
            //{
                List<RuleImpl> result = new ArrayList<RuleImpl>(getMyRules());
                result.addAll(getInheritedRules());
            //}
            //return this.allRules;
            return result;
        }

        private List<RuleImpl> getMyRules()
        {
            if (this.myRules == null)
            {
                this.myRules = readRules(getConfigFolder(this.nodeRef));
            }
            return this.myRules;
        }
        
        private List<RuleImpl> getInheritedRules()
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
