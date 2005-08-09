/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.configuration.ConfigurableService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.cmr.rule.RuleServiceException;
import org.alfresco.service.cmr.rule.RuleType;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Rule store implementation
 * 
 * @author Roy Wetherall
 */
public class RuleStoreImpl implements RuleStore
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
	 * The rule service
	 */
	private RuleService ruleService;
    
    /**
     * The dictionary service
     */
    private DictionaryService dictionaryService;
    
    /**
     * The policy component
     */
    private PolicyComponent policyComponent;
    
    /**
     * The configurable service
     */
    private ConfigurableService configService;
    
    /**
     * The action service
     */
    private ActionService actionService;
    
    /**
     * Rule cache entries indexed by node reference
     */
    private Map<NodeRef, RuleCacheEntry> ruleCache = new HashMap<NodeRef, RuleCacheEntry>();
    
	/**
	 * Set the node service
	 * 
	 * @param nodeService  the node service
	 */
	public void setNodeService(NodeService nodeService) 
	{
		this.nodeService = nodeService;
	}
	
	/**
	 * Set the content service
	 * 
	 * @param contentService  the content service
	 */
	public void setContentService(ContentService contentService) 
	{
		this.contentService = contentService;
	}
	
	/**
	 * Set the rule service
	 * 
	 * @param ruleService  the rule service
	 */
	public void setRuleService(RuleService ruleService)
	{
		this.ruleService = ruleService;
	}
    
    /**
     * Set the dictionary service
     * 
     * @param dictionaryService  the dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    
    /**
     * Set the policy component
     * 
     * @param policyComponent  the policy component
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
    
    /**
     * Set the configurable service
     * 
     * @param configService  the configurable service
     */
    public void setConfigService(ConfigurableService configService)
	{
		this.configService = configService;
	}
    
    /**
     * Set the action service 
     * 
     * @param actionService  the action service
     */
    public void setActionService(ActionService actionService)
	{
		this.actionService = actionService;
	}
    
    /**
     * Initilalise method
     */
    public void init()
    {
        // Register policy behaviour
        this.policyComponent.bindAssociationBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateChildAssociation"),
                this,
                new JavaBehaviour(this, "onCreateChildAssociation"));
        this.policyComponent.bindAssociationBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onDeleteChildAssociation"),
                this,
                new JavaBehaviour(this, "onDeleteChildAssociation"));
        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onDeleteNode"),
                this,
                new JavaBehaviour(this, "onDeleteNode"));
    }
    
    /**
     * OnDeleteNode policy behaviour implementation
     * 
     * @param childAssocRef  the child association reference
     */
    public void onDeleteNode(ChildAssociationRef childAssocRef)
    {
        // TODO we should remove the associated rules from the repo ...
        //      should be done onBeforeDeleteNode so we can still get hold of the folder
        
        RuleCacheEntry deleted = this.ruleCache.get(childAssocRef.getChildRef());
        if (deleted != null)
        {
            deleted.prepForDelete();
            this.ruleCache.remove(childAssocRef.getChildRef());                        
        }
    }
    
    /**
     * OnCreaeteChildAssociation policy behaviour implementation
     * 
     * @param childAssocRef  the child association reference
     */
    public void onCreateChildAssociation(ChildAssociationRef childAssocRef)
    {
        RuleCacheEntry parent = this.ruleCache.get(childAssocRef.getParentRef());
        if (parent != null)
        {
            RuleCacheEntry child = this.ruleCache.get(childAssocRef.getChildRef());
            if (child != null)
            {
                child.addParent(parent);
            }
        }
    }
    
    /**
     * OnDeleteChildAssociation policy behaviour implementation
     * 
     * @param childAssocRef  the child association reference
     */
    public void onDeleteChildAssociation(ChildAssociationRef childAssocRef)
    {
        RuleCacheEntry parent = this.ruleCache.get(childAssocRef.getParentRef());
        if (parent != null)
        {
            RuleCacheEntry child = this.ruleCache.get(childAssocRef.getChildRef());
            if (child != null)
            {
                child.removeParent(parent);
            }
        }
    }
    
    /**
     * Completely clean the rule cache
     */
    public void cleanRuleCache()
    {
        this.ruleCache.clear();
    }
    
	/**
	 * @see org.alfresco.repo.rule.RuleStore#hasRules(org.alfresco.service.cmr.repository.NodeRef)
	 */
    public boolean hasRules(NodeRef nodeRef)
    {
        RuleCacheEntry ruleCacheEntry = getRuleCacheEntry(nodeRef);
        return ruleCacheEntry.hasRules();
    }
    
	/**
	 * @see org.alfresco.repo.rule.RuleStore#getByRuleType(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.cmr.rule.RuleType)
	 */
    public List<? extends Rule> getByRuleType(NodeRef nodeRef, RuleType ruleType)
    {
        RuleCacheEntry ruleCacheEntry = getRuleCacheEntry(nodeRef);
        return ruleCacheEntry.getRulesByRuleType(ruleType);
    }
    
    /**
	 * @see org.alfresco.repo.rule.RuleStore#get(org.alfresco.service.cmr.repository.NodeRef, boolean)
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
	 * @see org.alfresco.repo.rule.RuleStore#getById(org.alfresco.service.cmr.repository.NodeRef, java.lang.String)
	 */
	public Rule getById(NodeRef nodeRef, String ruleId)
	{
		Rule result = null;
		List<? extends Rule> rules = get(nodeRef, true);
		for (Rule rule : rules) 
		{
			if (ruleId.equals(rule.getId()) == true)
			{
				result = rule;
				break;
			}
		}
		return result;
	}
    
    /**
	 * @see org.alfresco.repo.rule.RuleStore#put(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.repo.rule.RuleImpl)
	 */
    public void put(NodeRef nodeRef, RuleImpl rule)
    {
        // Write the rule to the repository
        NodeRef configFolder = getRuleFolder(nodeRef);
        if (configFolder == null)
        {
            throw new RuleServiceException("The configuration folder for the acitonable node has not been set.");
        }
        writeRule(configFolder, rule);
        
        RuleCacheEntry ruleCacheEntry = this.ruleCache.get(nodeRef);
        if (ruleCacheEntry != null)
        {
            ruleCacheEntry.dirtyMyRules();
        }
    }
    
    /**
	 * @see org.alfresco.repo.rule.RuleStore#remove(org.alfresco.service.cmr.repository.NodeRef, org.alfresco.repo.rule.RuleImpl)
	 */
    public void remove(NodeRef nodeRef, RuleImpl rule)
    {
        // Dirty the cache entry
        RuleCacheEntry ruleCacheEntry = this.ruleCache.get(nodeRef);
        if (ruleCacheEntry != null)
        {
            ruleCacheEntry.dirtyMyRules();
        }
        
        // Delete the rule content from the repository
        NodeRef ruleContent = rule.getRuleContentNodeRef();
        if (ruleContent != null && this.nodeService.exists(ruleContent) == true)
        {
            this.nodeService.deleteNode(ruleContent);
        }
    }   
    
	/**
	 * Gets the cache entry for the node refernece, creating one if it doen't exist.
	 * 
	 * @param nodeRef	the node reference
	 * @return			the rule cache entry
	 */
    private RuleCacheEntry getRuleCacheEntry(NodeRef nodeRef)
    {
		// First check that the node reference is valid
		if (this.nodeService.exists(nodeRef) == false)
		{
			throw new RuleServiceException("Can not get rule cache entry since node does not exist.");
		}
		
        RuleCacheEntry ruleCacheEntry = this.ruleCache.get(nodeRef);
        if (ruleCacheEntry == null)
        {
            ruleCacheEntry = new RuleCacheEntry(nodeRef);           
        }
        return ruleCacheEntry;
    }

    /**
     * Write the rule to the repository
     * 
     * @param configFolder	the config folder
     * @param rule			the rule
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
            properties.put(ContentModel.PROP_MIME_TYPE, "text/xml");
            properties.put(ContentModel.PROP_ENCODING, "UTF-8");
            
            // Create the rule content node
            ruleContent = this.nodeService.createNode(
                    configFolder, 
					ContentModel.ASSOC_CONTAINS, 
                    ContentModel.ASSOC_CONTAINS, 
                    ContentModel.TYPE_RULE_CONTENT,
                    properties).getChildRef();
            
            // Set the ruleContent node on the rule
            rule.setRuleContentNodeRef(ruleContent);
        }
        
        // Write the rule's XML representation to the node
        ContentWriter contentWriter = this.contentService.getUpdatingWriter(ruleContent);
        contentWriter.putContent(RuleXMLUtil.ruleToXML(this.actionService, rule));
    }
    
    /**
     * Read the rules from the repository
     * 
     * @param configFolder	the config folder
     * @return				list of rules
     */
    private List<RuleImpl> readRules(NodeRef configFolder)
    {
        // TODO need to cope with any folder structure containing rule content
        
        List<RuleImpl> rules = new ArrayList<RuleImpl>();
        List<ChildAssociationRef> childAssocRefs = this.nodeService.getChildAssocs(configFolder);
        for (ChildAssociationRef childAssocRef : childAssocRefs)
        {
            NodeRef nodeRef = childAssocRef.getChildRef();
            if(this.nodeService.exists(nodeRef) == true)
            {
                ContentReader contentReader = this.contentService.getReader(nodeRef);
                if (contentReader != null)
                {
                    // Create the rule from the XML content
                    String ruleXML = contentReader.getContentString();
                    RuleImpl rule = RuleXMLUtil.XMLToRule(this.actionService, this.ruleService, ruleXML, this.dictionaryService);
                    
                    // Set the rule content id
                    rule.setRuleContentNodeRef(nodeRef);
                    
                    // Add the created date and modified date (they come from the auditable aspect)
                    rule.setCreatedDate((Date)this.nodeService.getProperty(nodeRef, ContentModel.PROP_CREATED));
                    rule.setModifiedDate((Date)this.nodeService.getProperty(nodeRef, ContentModel.PROP_MODIFIED));
                    
                    // Add the rule to the list
                    rules.add(rule);
                }
            }
        }
        return rules;
    }

    /**
     * Get the node reference of the folder where the rule content nodes are stored
     * 
     * @param nodeRef       the node reference to the actionable node
     * @return              the node reference to the configuration folder
     */
    private NodeRef getRuleFolder(NodeRef nodeRef)
    {
        NodeRef ruleFolder = null;
        
		if (this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_ACTIONABLE) == true)
		{
            NodeRef configFolder = this.configService.getConfigurationFolder(nodeRef);
			if (configFolder != null)
			{
				List<ChildAssociationRef> childAssocRefs = this.nodeService.getChildAssocs(
														configFolder, 
														QName.createQName(NamespaceService.ALFRESCO_URI, "rules"));
				if (childAssocRefs.size() == 0)
				{
					ruleFolder = this.nodeService.createNode(
														configFolder,
														ContentModel.ASSOC_CONTAINS,
														QName.createQName(NamespaceService.ALFRESCO_URI, "rules"),
														ContentModel.TYPE_SYSTEM_FOLDER).getChildRef();
				}
				else
				{
					ruleFolder = childAssocRefs.get(0).getChildRef();
				}
			}
		}
		
        return ruleFolder;
    }       
    
    /**
     * Rule cache entry
     * 
     * @author Roy Wetherall
     */
    private class RuleCacheEntry
    {
        /**
         * The node reference that is associated with the rule cache
         */
        private NodeRef nodeRef;
        
        /**
         * The rules that origionate with this node
         */
        private List<RuleImpl> myRules;
        
        /**
         * The rules inhertied from parent nodes
         */
        private List<RuleImpl> inheritedRules;
        
        private List<RuleImpl> inheritableRules;
        
        private List<RuleCacheEntry> parentEntries;
        private List<RuleCacheEntry> childEntries;
        
        private Map<String, List<RuleImpl>> allRulesByRuleType;
        private List<RuleImpl> allRules;        
        
        /**
         * Constructor
         * 
         * @param nodeRef   the node reference
         */
        public RuleCacheEntry(NodeRef nodeRef)
        {
            this.nodeRef = nodeRef;
            RuleStoreImpl.this.ruleCache.put(this.nodeRef, this);
            
            this.parentEntries = new ArrayList<RuleCacheEntry>();
            this.childEntries = new ArrayList<RuleCacheEntry>();
            List<ChildAssociationRef> parentAssocRefs = RuleStoreImpl.this.nodeService.getParentAssocs(this.nodeRef);
            for (ChildAssociationRef parentAssocRef : parentAssocRefs)
            {
                RuleCacheEntry parentCacheEntry = RuleStoreImpl.this.getRuleCacheEntry(parentAssocRef.getParentRef());
                this.parentEntries.add(parentCacheEntry);
                parentCacheEntry.childEntries.add(this);
            }
        }
        
        public void addParent(RuleCacheEntry ruleCacheEntry)
        {
            this.parentEntries.add(ruleCacheEntry);
            ruleCacheEntry.childEntries.add(this);
            
            dirtyInheritedRules();
        }
        
        public void removeParent(RuleCacheEntry ruleCacheEntry)
        {
            this.parentEntries.remove(ruleCacheEntry);
            ruleCacheEntry.childEntries.remove(this);
            
            dirtyInheritedRules();
        }
        
        public void prepForDelete()
        {
            // Dirty all my children inherited cache's
            dirtyInheritedRules();
            
            // Remove reference for all parents
            for (RuleCacheEntry parent : this.parentEntries)
            {
                parent.childEntries.remove(this);
            }
            
            // Remove reference form all children
            for (RuleCacheEntry child : this.childEntries)
            {
                child.parentEntries.remove(this);
            }
        }
        
        /**
         * 
         */
        public void dirtyMyRules()
        {
            // Clean all the caches leaving the inherited in place
            this.myRules = null;
            this.allRules = null;
            this.allRulesByRuleType = null;
            this.inheritableRules = null;
           
            List<RuleCacheEntry> dirtied = new ArrayList<RuleCacheEntry>();
            dirtied.add(this);
            for (RuleCacheEntry childCacheEntry : this.childEntries)
            {
                childCacheEntry.dirtyInheritedRules(dirtied);
            }
        }
        
        /**
         * 
         *
         */
        private void dirtyInheritedRules()
        {
            dirtyInheritedRules(new ArrayList<RuleCacheEntry>());
        }
        
        /**
         * 
         * @param dirtied
         */
        private void dirtyInheritedRules(List<RuleCacheEntry> dirtied)
        {
            if (dirtied.contains(this) == false)
            {
                // Clean all the caches leaving myRules in place
                this.allRules = null;
                this.allRulesByRuleType = null;
                this.inheritedRules = null;
                
                dirtied.add(this);
                
                for (RuleCacheEntry childCacheEntry : this.childEntries)
                {
                    childCacheEntry.dirtyInheritedRules(dirtied);
                }
            }
        }                      
        
        /**
         * Indicates whether there are any rules specified, including inherited.
         * 
         * @return  true if there are rules specified, false otherwise
         */
        public boolean hasRules()
        {
            return (getRules().isEmpty() == false);
        }
        
        /**
         * 
         * @return
         */
        public List<RuleImpl> getRules()
        {
            if (this.allRules == null)
            {
                this.allRules = new ArrayList<RuleImpl>(getMyRules());
                
                // Add all the inherited rule ensuring no duplicates are added
                for (RuleImpl rule : getInheritedRules())
                {
                    if (this.allRules.contains(rule) == false)
                    {
                        this.allRules.add(rule);
                    }
                }
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

        /**
         * 
         * @return
         */
        public List<RuleImpl> getMyRules()
        {
            if (this.myRules == null)
            {
                NodeRef configFolder = getRuleFolder(this.nodeRef);
                if (configFolder == null)
                {
                    this.myRules = new ArrayList<RuleImpl>();
                }
                else
                {
                    this.myRules = readRules(configFolder);
                }
            }
            return this.myRules;
        }
        
        /**
         * 
         * @return
         */
        public List<RuleImpl> getInheritedRules()
        {
            if (this.inheritedRules == null)
            {
                List<RuleCacheEntry> visited = new ArrayList<RuleCacheEntry>();
                visited.add(this);
                
                this.inheritedRules = new ArrayList<RuleImpl>();
                for (RuleCacheEntry parentCacheEntry : this.parentEntries)
                {
                    parentCacheEntry.buildInheritedRules(this.inheritedRules, visited);                     
                }
            }
            return this.inheritedRules;
        }
        
        /**
         * 
         * @param rules
         * @param visited
         */
        private void buildInheritedRules(List<RuleImpl> rules, List<RuleCacheEntry> visited)
        {
            if (visited.contains(this) == false)
            {
                visited.add(this);
                rules.addAll(this.getInheritableRules());
                for (RuleCacheEntry parentCacheEntry : this.parentEntries)
                {
                    parentCacheEntry.buildInheritedRules(rules, visited);                     
                }
            }
        }
        
        /**
         * 
         * @return
         */
        public List<RuleImpl> getInheritableRules()
        {
            if (this.inheritableRules == null)
            {
                this.inheritableRules = new ArrayList<RuleImpl>();
                for (RuleImpl rule : getMyRules())
                {
                    if (rule.isAppliedToChildren() == true)
                    {
                        this.inheritableRules.add(rule);
                    }
                }
            }
            
            return this.inheritableRules;                        
        }    
        
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj instanceof RuleCacheEntry)
            {
                RuleCacheEntry that = (RuleCacheEntry) obj;
                return (this.nodeRef.equals(that.nodeRef));
            }
            else
            {
                return false;
            }
        }
        
        @Override
        public int hashCode()
        {
            return this.nodeRef.hashCode();
        }
    }
}
