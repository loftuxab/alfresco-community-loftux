/**
 * 
 */
package org.alfresco.repo.rule.impl;


/**
 * @author Roy Wetherall
 */
public class RuleXMLUtilTest extends RuleBaseTest
{
    protected static final String RULE_ID = "1";
    
    /**
     * Rule XML
     */
    private static final String XML = 
        "<rule id='" + RULE_ID + "' ruleType='ruleType'>" +
            "<title>title</title>" +
            "<description>description</description>" +
            "<conditions>" +
                "<condition name='conditionDefinition'>" +
                    "<parameter name='condPropName1'>condPropValue1</parameter>" +
                "</condition>" +
            "</conditions>" +
            "<actions>" +
                "<action name='actionDefinition'>" +
                    "<parameter name='actionPropName1'>actionPropValue1</parameter>" +
                "</action>" +
            "</actions>" +
        "</rule>";
    
    /**
     * Test rule
     */
    private RuleImpl rule;   
    
    /**
     * Config services used
     */
    private RuleConfig ruleConfig;
    
    /**
     * Setup the tests
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void onSetUpInTransaction() throws Exception
    {
        super.onSetUpInTransaction();
        
        // Sort out the required config
        this.ruleConfig = new RuleConfig(this.configService);
        
        // Create the test rule
        this.rule = createTestRule(RULE_ID);
    }
    
    /**
     * Test XMLToRule
     */
    public void testXMLToRule()
    {
        // Get the rule from the XML
        RuleImpl rule = RuleXMLUtil.XMLToRule(this.ruleConfig, XML);
        assertNotNull(rule);
        
        // Check the rule
        checkRule(rule, RULE_ID);
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
}
