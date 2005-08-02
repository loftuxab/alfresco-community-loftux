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

import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.rule.common.RuleImpl;
import org.alfresco.repo.rule.common.RuleTypeImpl;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.springframework.util.StopWatch;

/**
 * Rule store teset
 * 
 * @author Roy Wetherall
 */
public class RuleStoreTest extends BaseRuleTest
{
    /**
     * Rule id
     */
    private static final String RULE_ID = "1";
	
	/**
	 * The rule store
	 */
	private RuleStoreImpl ruleStore;

    /**
     * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpInTransaction()
     */
    @Override
    protected void onSetUpInTransaction() throws Exception
    {
        super.onSetUpInTransaction();
		
        
		this.ruleStore = (RuleStoreImpl)applicationContext.getBean("ruleStore");
        
        // Make the test node actionable
        makeTestNodeActionable();
    }
    
    /**
     * Helper method to easily create a new node which can be actionable (or not)
     * 
     * @param parent        the parent node
     * @param isActionable  indicates whether the node is actionable or not
     */
    private NodeRef createNewNode(NodeRef parent, boolean isActionable)
    {
        NodeRef newNodeRef = this.nodeService.createNode(parent,
                ContentModel.ASSOC_CHILDREN,
                QName.createQName("{test}testnode"),
                ContentModel.TYPE_CONTAINER).getChildRef();
        
        if (isActionable == true)
        {
            // Make the node actionable
        	this.configService.makeConfigurable(newNodeRef);
        	this.nodeService.addAspect(newNodeRef, ContentModel.ASPECT_ACTIONABLE, null);        	
        }
        
        return newNodeRef;
    }
    
    /**
     * Test get
     */
    public void testGet()
    {
        testPut();
        
        List<? extends Rule> rules = this.ruleStore.get(this.nodeRef, true);
        assertNotNull(rules);
        assertEquals(1, rules.size());
        
        RuleImpl rule = (RuleImpl)rules.get(0);
        checkRule(rule, RULE_ID);
    }
	
	/**
	 * Test getById
	 */
	public void testGetById()
	{
		Rule rule1 = this.ruleStore.getById(this.nodeRef, RULE_ID);
		assertNull(rule1);
		
		testPut();
		
		Rule rule2 = this.ruleStore.getById(this.nodeRef, RULE_ID);
		assertNotNull(rule2);
		assertEquals(RULE_ID, rule2.getId());
	}
    
    /**
     * Test put
     */
    public void testPut()
    {
        RuleImpl newRule = createTestRule(RULE_ID);
        this.ruleStore.put(this.nodeRef, newRule);
        
        NodeRef ruleContent = newRule.getRuleContentNodeRef();
        assertNotNull(ruleContent);
        
        ContentReader contentReader = this.contentService.getReader(ruleContent);
        assertNotNull(contentReader);
        String ruleXML = contentReader.getContentString();
        assertNotNull(ruleXML);
    }
    
    /**
     * Test getByRuleType
     */
    public void testGetByRuleType()
    {
        List<? extends Rule> empty = this.ruleStore.getByRuleType(this.nodeRef, this.ruleType);
        assertNotNull(empty);
        assertTrue(empty.isEmpty());
        
        testPut();
        List<? extends Rule> rules = this.ruleStore.getByRuleType(this.nodeRef, this.ruleType);
        assertNotNull(rules);
        assertEquals(1, rules.size());
        assertEquals(RULE_TYPE_NAME, ((RuleImpl)rules.get(0)).getRuleType().getName());
        
        List<? extends Rule> empty2 = this.ruleStore.getByRuleType(this.nodeRef, new RuleTypeImpl("anOtherRuleType"));
        assertNotNull(empty2);
        assertTrue(empty2.isEmpty());        
    }
    
    /**
     * Test hasRules
     */
    public void testHasRules()
    {
        // Check that the node does not have any rules
        assertFalse(this.ruleStore.hasRules(this.nodeRef));
        
        // Put some rules and check that the value is now true
        testPut();
        assertTrue(this.ruleStore.hasRules(this.nodeRef));
    }
    
    /**
     * Test remove
     */
    public void testRemove()
    {
        RuleImpl newRule = createTestRule(RULE_ID);
        this.ruleStore.put(this.nodeRef, newRule);
        List<? extends Rule> rules = this.ruleStore.get(this.nodeRef, true);
        assertNotNull(rules);
        assertEquals(1, rules.size());
        
        this.ruleStore.remove(nodeRef, newRule);
        
        List<? extends Rule> moreRules = this.ruleStore.get(this.nodeRef, true);
        assertNotNull(moreRules);
        assertEquals(0, moreRules.size());       
        
        //System.out.println(NodeStoreInspector.dumpNodeStore(this.nodeService, this.testStoreRef));
    }
    
    /**
     * Test the performace of the cache with non hierarchical rules.
     */
    public void xtestCacheNonHierarchical()
    {
        StopWatch sw = new StopWatch();
        
        // Create actionable nodes
        sw.start("create actionable nodes");
        NodeRef[] nodes = new NodeRef[100];
        for (int i = 0; i < 100; i++)
        {
            NodeRef nodeRef = this.nodeService.createNode(
                    rootNodeRef,
					ContentModel.ASSOC_CONTAINS,
                    ContentModel.ASSOC_CONTAINS,
                    ContentModel.TYPE_CONTAINER).getChildRef();
            NodeRef configFolder = this.nodeService.createNode(
                    rootNodeRef,
					ContentModel.ASSOC_CONTAINS,
                    ContentModel.ASSOC_CONTAINS,
                    ContentModel.TYPE_CONFIGURATIONS).getChildRef();
            this.nodeService.addAspect(nodeRef, ContentModel.ASPECT_ACTIONABLE, null);
            this.nodeService.createAssociation(
                    nodeRef, 
                    configFolder, 
                    ContentModel.ASSOC_CONFIGURATIONS);
            nodes[i] = nodeRef;
        }
        sw.stop();
        
        sw.start("put 10 rules on each node");
        try
        {
            // Put rules
            for (int i = 0; i < 100; i++)
            {
                NodeRef nodeRef = nodes[i];
                for (int j = 0; j < 10; j++)
                {
                    RuleImpl newRule = createTestRule(Integer.toString(i) + "." + Integer.toString(j));
                    this.ruleStore.put(nodeRef, newRule);
                }
            }
        }
        finally
        {
            sw.stop();
        }
        
        sw.start("get rules (not cached)");
        try
        {
            // Get rules (not cached)
            for (int i = 0; i < 100; i++)
            {
                NodeRef nodeRef = nodes[i];
                List<? extends Rule> rules = this.ruleStore.get(nodeRef, true);
                assertNotNull(rules);
                assertEquals(10, rules.size());
            }
        }
        finally
        {
            sw.stop();
        }
        
        sw.start("get rules (cached)");
        try
        {
            // Get rules (should now be cached)
            for (int i = 0; i < 100; i++)
            {
                NodeRef nodeRef = nodes[i];
                List<? extends Rule> rules = this.ruleStore.get(nodeRef, true);
                assertNotNull(rules);
                assertEquals(10, rules.size());
            }
        }
        finally
        {
            sw.stop();
        }
        
        sw.start("put & get rules (put one, get five)");
        try
        {
            // Put and get
            for (int i = 0; i < 95; i++)
            {
                NodeRef nodeRef = nodes[i];
                RuleImpl newRule = createTestRule(Integer.toString(i) + ".new");
                this.ruleStore.put(nodeRef, newRule);
                
                for (int j = 0; j < 5; j++)
                {
                    NodeRef getNodeRef = nodes[i+j];
                    List<? extends Rule> rules = this.ruleStore.get(getNodeRef, true);    
                    assertNotNull(rules);
                    if (j == 0)
                    {
                        assertEquals(11, rules.size());
                    }
                    else
                    {
                        assertEquals(10, rules.size());
                    }
                }
            }
        }
        finally
        {
            sw.stop();
        }
        
        
        System.out.println(sw.prettyPrint());
    }
    
    /**
     * Tests the rule inheritance within the store, checking that the cache is reset correctly when 
     * rules are added and removed.
     */
    public void testRuleInheritance()
    {
        // Create the nodes and rules
        
        NodeRef rootWithRules = createNewNode(this.rootNodeRef, true);
        RuleImpl rule1 = createTestRule("1");
        this.ruleStore.put(rootWithRules, rule1);
        RuleImpl rule2 = createTestRule("2", true);
        this.ruleStore.put(rootWithRules, rule2);
        
        NodeRef nonActionableChild = createNewNode(rootWithRules, false);
        
        NodeRef childWithRules = createNewNode(nonActionableChild, true);
        RuleImpl rule3 = createTestRule("3");
        this.ruleStore.put(childWithRules, rule3);
        RuleImpl rule4 = createTestRule("4", true);
        this.ruleStore.put(childWithRules, rule4);
        
        NodeRef rootWithRules2 = createNewNode(this.rootNodeRef, true);
        this.nodeService.addChild(
                rootWithRules2, 
                childWithRules, 
                ContentModel.ASSOC_CHILDREN,
                QName.createQName("{test}testnode"));
        RuleImpl rule5 = createTestRule("5");
        this.ruleStore.put(rootWithRules2, rule5);
        RuleImpl rule6 = createTestRule("6", true);
        this.ruleStore.put(rootWithRules2, rule6);
                        
        // Check that the rules are inherited in the correct way
        
        List<? extends Rule> allRules = this.ruleStore.get(childWithRules, true);
        assertNotNull(allRules);
        assertEquals(4, allRules.size());
        assertTrue(allRules.contains(rule2));
        assertTrue(allRules.contains(rule3));
        assertTrue(allRules.contains(rule4));
        assertTrue(allRules.contains(rule6));
        
        List<? extends Rule> myRules = this.ruleStore.get(childWithRules, false);
        assertNotNull(myRules);
        assertEquals(2, myRules.size());
        assertTrue(myRules.contains(rule3));
        assertTrue(myRules.contains(rule4));
        
        List<? extends Rule> allRules2 = this.ruleStore.get(nonActionableChild, true);
        assertNotNull(allRules2);
        assertEquals(1, allRules2.size());
        assertTrue(allRules2.contains(rule2));
        
        List<? extends Rule> myRules2 = this.ruleStore.get(nonActionableChild, false);
        assertNotNull(myRules2);
        assertEquals(0, myRules2.size());
        
        List<? extends Rule> allRules3 = this.ruleStore.get(rootWithRules, true);
        assertNotNull(allRules3);
        assertEquals(2, allRules3.size());
        assertTrue(allRules3.contains(rule1));
        assertTrue(allRules3.contains(rule2));
        
        List<? extends Rule> myRules3 = this.ruleStore.get(rootWithRules, false);
        assertNotNull(myRules3);
        assertEquals(2, myRules3.size());
        assertTrue(myRules3.contains(rule1));
        assertTrue(myRules3.contains(rule2));
        
        List<? extends Rule> allRules4 = this.ruleStore.get(rootWithRules2, true);
        assertNotNull(allRules4);
        assertEquals(2, allRules4.size());
        assertTrue(allRules4.contains(rule5));
        assertTrue(allRules4.contains(rule6));
        
        List<? extends Rule> myRules4 = this.ruleStore.get(rootWithRules2, false);
        assertNotNull(myRules4);
        assertEquals(2, myRules4.size());
        assertTrue(myRules4.contains(rule5));
        assertTrue(myRules4.contains(rule6));
        
        // Take the root node and add another rule
        
        RuleImpl rule7 = createTestRule("7", true);
        this.ruleStore.put(rootWithRules, rule7);
        
        List<? extends Rule> allRules5 = this.ruleStore.get(childWithRules, true);
        assertNotNull(allRules5);
        assertEquals(5, allRules5.size());
        assertTrue(allRules5.contains(rule2));
        assertTrue(allRules5.contains(rule3));
        assertTrue(allRules5.contains(rule4));
        assertTrue(allRules5.contains(rule6));
        assertTrue(allRules5.contains(rule7));
        
        List<? extends Rule> allRules6 = this.ruleStore.get(nonActionableChild, true);
        assertNotNull(allRules6);
        assertEquals(2, allRules6.size());
        assertTrue(allRules6.contains(rule2));
        assertTrue(allRules6.contains(rule7));
        
        List<? extends Rule> allRules7 = this.ruleStore.get(rootWithRules, true);
        assertNotNull(allRules7);
        assertEquals(3, allRules7.size());
        assertTrue(allRules7.contains(rule1));
        assertTrue(allRules7.contains(rule2));
        assertTrue(allRules7.contains(rule7));
        
        List<? extends Rule> allRules8 = this.ruleStore.get(rootWithRules2, true);
        assertNotNull(allRules8);
        assertEquals(2, allRules8.size());
        assertTrue(allRules8.contains(rule5));
        assertTrue(allRules8.contains(rule6));
         
        // Take the root node and and remove a rule
        
        this.ruleStore.remove(rootWithRules, rule7);
        
        List<? extends Rule> allRules9 = this.ruleStore.get(childWithRules, true);
        assertNotNull(allRules9);
        assertEquals(4, allRules9.size());
        assertTrue(allRules9.contains(rule2));
        assertTrue(allRules9.contains(rule3));
        assertTrue(allRules9.contains(rule4));
        assertTrue(allRules9.contains(rule6));
        
        List<? extends Rule> allRules10 = this.ruleStore.get(nonActionableChild, true);
        assertNotNull(allRules10);
        assertEquals(1, allRules10.size());
        assertTrue(allRules10.contains(rule2));
        
        List<? extends Rule> allRules11 = this.ruleStore.get(rootWithRules, true);
        assertNotNull(allRules11);
        assertEquals(2, allRules11.size());
        assertTrue(allRules11.contains(rule1));
        assertTrue(allRules11.contains(rule2));
        
        List<? extends Rule> allRules12 = this.ruleStore.get(rootWithRules2, true);
        assertNotNull(allRules12);
        assertEquals(2, allRules12.size());
        assertTrue(allRules12.contains(rule5));
        assertTrue(allRules12.contains(rule6));
        
        // Delete an association
        
        this.nodeService.removeChild(rootWithRules2, childWithRules);
        
        List<? extends Rule> allRules13 = this.ruleStore.get(childWithRules, true);
        assertNotNull(allRules13);
        assertEquals(3, allRules13.size());
        assertTrue(allRules13.contains(rule2));
        assertTrue(allRules13.contains(rule3));
        assertTrue(allRules13.contains(rule4));
        
        List<? extends Rule> allRules14 = this.ruleStore.get(nonActionableChild, true);
        assertNotNull(allRules14);
        assertEquals(1, allRules14.size());
        assertTrue(allRules14.contains(rule2));
        
        List<? extends Rule> allRules15 = this.ruleStore.get(rootWithRules, true);
        assertNotNull(allRules15);
        assertEquals(2, allRules15.size());
        assertTrue(allRules15.contains(rule1));
        assertTrue(allRules15.contains(rule2));
       
        List<? extends Rule> allRules16 = this.ruleStore.get(rootWithRules2, true);
        assertNotNull(allRules16);
        assertEquals(2, allRules16.size());
        assertTrue(allRules16.contains(rule5));
        assertTrue(allRules16.contains(rule6));
        
        // Add an association
        
        this.nodeService.addChild(
                rootWithRules2, 
                childWithRules, 
                ContentModel.ASSOC_CHILDREN,
                QName.createQName("{test}testnode"));
        
        List<? extends Rule> allRules17 = this.ruleStore.get(childWithRules, true);
        assertNotNull(allRules17);
        assertEquals(4, allRules17.size());
        assertTrue(allRules17.contains(rule2));
        assertTrue(allRules17.contains(rule3));
        assertTrue(allRules17.contains(rule4));
        assertTrue(allRules17.contains(rule6));
        
        List<? extends Rule> allRules18 = this.ruleStore.get(nonActionableChild, true);
        assertNotNull(allRules18);
        assertEquals(1, allRules18.size());
        assertTrue(allRules18.contains(rule2));
        
        List<? extends Rule> allRules19 = this.ruleStore.get(rootWithRules, true);
        assertNotNull(allRules19);
        assertEquals(2, allRules19.size());
        assertTrue(allRules19.contains(rule1));
        assertTrue(allRules19.contains(rule2));
        
        List<? extends Rule> allRules20 = this.ruleStore.get(rootWithRules2, true);
        assertNotNull(allRules20);
        assertEquals(2, allRules20.size());
        assertTrue(allRules20.contains(rule5));
        assertTrue(allRules20.contains(rule6));
        
        // Delete node
        
        this.nodeService.deleteNode(rootWithRules2);
        
        List<? extends Rule> allRules21 = this.ruleStore.get(childWithRules, true);
        assertNotNull(allRules21);
        assertEquals(3, allRules21.size());
        assertTrue(allRules21.contains(rule2));
        assertTrue(allRules21.contains(rule3));
        assertTrue(allRules21.contains(rule4));
        
        List<? extends Rule> allRules22 = this.ruleStore.get(nonActionableChild, true);
        assertNotNull(allRules22);
        assertEquals(1, allRules22.size());
        assertTrue(allRules22.contains(rule2));
        
        List<? extends Rule> allRules23 = this.ruleStore.get(rootWithRules, true);
        assertNotNull(allRules23);
        assertEquals(2, allRules23.size());
        assertTrue(allRules23.contains(rule1));
        assertTrue(allRules23.contains(rule2));              
    }
    
    /**
     * Ensure that the rule store can cope with a cyclic node graph
     * 
     * @throws Exception
     */
    public void testCyclicGraphWithInheritedRules()
        throws Exception
    {
        NodeRef nodeRef1 = createNewNode(this.rootNodeRef, true);
        NodeRef nodeRef2 = createNewNode(nodeRef1, true);
        NodeRef nodeRef3 = createNewNode(nodeRef2, true);
        this.nodeService.addChild(nodeRef3, nodeRef1, ContentModel.ASSOC_CHILDREN, QName.createQName("{test}loop"));
        
        RuleImpl rule1 = createTestRule(GUID.generate(), true);
        this.ruleStore.put(nodeRef1, rule1);
        RuleImpl rule2 = createTestRule(GUID.generate(), true);
        this.ruleStore.put(nodeRef2, rule2);
        RuleImpl rule3 = createTestRule(GUID.generate(), true);
        this.ruleStore.put(nodeRef3, rule3);
                
        // TODO figure out why the store is not if the correct state at this point
        // Manually clear the cache 
        ((RuleStoreImpl)this.ruleStore).cleanRuleCache();
        
        List<? extends Rule> allRules1 = this.ruleStore.get(nodeRef1, true);
        assertNotNull(allRules1);
        assertEquals(3, allRules1.size());
        assertTrue(allRules1.contains(rule1));
        assertTrue(allRules1.contains(rule2));
        assertTrue(allRules1.contains(rule3));
        
        List<? extends Rule> allRules2 = this.ruleStore.get(nodeRef2, true);
        assertNotNull(allRules2);
        assertEquals(3, allRules2.size());
        assertTrue(allRules2.contains(rule1));
        assertTrue(allRules2.contains(rule2));
        assertTrue(allRules2.contains(rule3));
        
        List<? extends Rule> allRules3 = this.ruleStore.get(nodeRef3, true);
        assertNotNull(allRules3);
        assertEquals(3, allRules3.size());
        assertTrue(allRules3.contains(rule1));
        assertTrue(allRules3.contains(rule2));
        assertTrue(allRules3.contains(rule3));            
    }
    
    /**
     * Ensures that rules are not duplicated when inherited    
     */
    public void testRuleDuplication()
    {
        NodeRef nodeRef1 = createNewNode(this.rootNodeRef, true);
        NodeRef nodeRef2 = createNewNode(nodeRef1, true);
        NodeRef nodeRef3 = createNewNode(nodeRef2, true);
        NodeRef nodeRef4 = createNewNode(nodeRef1, true);
        this.nodeService.addChild(nodeRef4, nodeRef3, ContentModel.ASSOC_CHILDREN, QName.createQName("{test}test"));
        
        RuleImpl rule1 = createTestRule(GUID.generate(), true);
        this.ruleStore.put(nodeRef1, rule1);
        RuleImpl rule2 = createTestRule(GUID.generate(), true);
        this.ruleStore.put(nodeRef2, rule2);
        RuleImpl rule3 = createTestRule(GUID.generate(), true);
        this.ruleStore.put(nodeRef3, rule3);
        RuleImpl rule4 = createTestRule(GUID.generate(), true);
        this.ruleStore.put(nodeRef4, rule4);
        
        List<? extends Rule> allRules1 = this.ruleStore.get(nodeRef1, true);
        assertNotNull(allRules1);
        assertEquals(1, allRules1.size());
        assertTrue(allRules1.contains(rule1));
        
        List<? extends Rule> allRules2 = this.ruleStore.get(nodeRef2, true);
        assertNotNull(allRules2);
        assertEquals(2, allRules2.size());
        assertTrue(allRules2.contains(rule1));
        assertTrue(allRules2.contains(rule2));
        
        List<? extends Rule> allRules3 = this.ruleStore.get(nodeRef3, true);
        assertNotNull(allRules3);
        assertEquals(4, allRules3.size());
        assertTrue(allRules3.contains(rule1));
        assertTrue(allRules3.contains(rule2));
        assertTrue(allRules3.contains(rule3));
        assertTrue(allRules3.contains(rule4));
        
        List<? extends Rule> allRules4 = this.ruleStore.get(nodeRef4, true);
        assertNotNull(allRules4);
        assertEquals(2, allRules4.size());
        assertTrue(allRules4.contains(rule1));
        assertTrue(allRules4.contains(rule4));
        
    }
}
