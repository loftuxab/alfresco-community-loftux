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
import java.util.List;
import java.util.Map;

import org.alfresco.repo.rule.common.RuleImpl;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.cmr.rule.RuleCondition;


/**
 * @author Roy Wetherall
 */
public class RuleXMLUtilTest extends RuleBaseTest
{
    private static final String TITLE2 = "title<>&*"; 
    protected static final String RULE_ID = "1";
    
    /**
     * Rule XML
     */
    private static final String XML = 
        "<rule id='1' ruleType='inbound'>" +
        	"<title><![CDATA[" + TITLE2 +"]]></title>" +
        	"<description><![CDATA[description]]></description>" +
        	"<conditions>" +
        		"<condition name='match-text'>" +
        			"<parameter name='text'><![CDATA[.doc]]></parameter>" +
                "</condition>" +
            "</conditions>" +
            "<actions>" +
            	"<action name='add-features'>" +
            		"<parameter name='aspect-name'><![CDATA[{http://www.alfresco.org/1.0}lockable]]></parameter>" +
                "</action>" +
            "</actions>" +
        "</rule>";
    
    /**
     * Test rule
     */
    private RuleImpl rule;
    
    /**
     * The dictionary service
     */
    private DictionaryService dictionaryService;
    
    /**
     * Setup the tests
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void onSetUpInTransaction() throws Exception
    {
        super.onSetUpInTransaction();
        
        // Set the dictionary service
        this.dictionaryService = (DictionaryService)this.applicationContext.getBean("dictionaryService");
        
        // Create the test rule
        this.rule = createTestRule(RULE_ID);
        this.rule.setTitle(TITLE2);
    }
    
    /**
     * Test XMLToRule
     */
    public void testXMLToRule()
    {
        // Get the rule from the XML
        RuleImpl rule = RuleXMLUtil.XMLToRule(this.ruleService, XML, this.dictionaryService);
        assertNotNull(rule);
        
        // Check the basic details of the rule
        assertEquals(RULE_ID, rule.getId());
        assertEquals(this.ruleType.getName(), rule.getRuleType().getName());
        assertEquals(TITLE2, rule.getTitle());
        assertEquals(DESCRIPTION, rule.getDescription());

        // Check conditions
        List<RuleCondition> ruleConditions = rule.getRuleConditions();
        assertNotNull(ruleConditions);
        assertEquals(1, ruleConditions.size());
        assertEquals(CONDITION_DEF_NAME, ruleConditions.get(0)
                .getRuleConditionDefinition().getName());
        Map<String, Serializable> condParams = ruleConditions.get(0)
                .getParameterValues();
        assertNotNull(condParams);
        assertEquals(1, condParams.size());
        assertTrue(condParams.containsKey(COND_PROP_NAME_1));
        assertEquals(COND_PROP_VALUE_1, condParams.get(COND_PROP_NAME_1));

        // Check the actions
        List<RuleAction> ruleActions = rule.getRuleActions();
        assertNotNull(ruleActions);
        assertEquals(1, ruleActions.size());
        assertEquals(ACTION_DEF_NAME, ruleActions.get(0)
                .getRuleActionDefinition().getName());
        Map<String, Serializable> actionParams = ruleActions.get(0)
                .getParameterValues();
        assertNotNull(actionParams);
        assertEquals(1, actionParams.size());
        assertTrue(actionParams.containsKey(ACTION_PROP_NAME_1));
        assertEquals(ACTION_PROP_VALUE_1, actionParams.get(ACTION_PROP_NAME_1));
    }
    
    /**
     * Test ruleToXML
     */
    public void testRuleToXML()
    {
        // Get the XML from the rule
        String ruleXML = RuleXMLUtil.ruleToXML(this.rule);
        assertEquals(XML, ruleXML);		
    }

	/**
	 * Test that the title and description are returned correctly when they are not set
	 *
	 */
	public void testNoTitleNoDescription()
	{
		this.rule.setTitle(null);
		this.rule.setDescription(null);
		String ruleXML = RuleXMLUtil.ruleToXML(this.rule);
		RuleImpl rule = RuleXMLUtil.XMLToRule(this.ruleService, ruleXML, this.dictionaryService);
		assertNull(rule.getTitle());
		assertNull(rule.getDescription());
	}
}
