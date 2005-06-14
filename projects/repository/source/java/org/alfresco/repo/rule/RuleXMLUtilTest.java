/**
 * 
 */
package org.alfresco.repo.rule;


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
        "<rule id='" + RULE_ID + "' ruleType='" + RULE_TYPE_NAME + "'>" +
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
     * Setup the tests
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void onSetUpInTransaction() throws Exception
    {
        super.onSetUpInTransaction();
        
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

	/**
	 * Test that the title and description are returned correctly when they are not set
	 *
	 */
	public void testNoTitleNoDescription()
	{
		this.rule.setTitle(null);
		this.rule.setDescription(null);
		String ruleXML = RuleXMLUtil.ruleToXML(this.rule);
		RuleImpl rule = RuleXMLUtil.XMLToRule(this.ruleConfig, ruleXML);
		assertNull(rule.getTitle());
		assertNull(rule.getDescription());
	}
}
