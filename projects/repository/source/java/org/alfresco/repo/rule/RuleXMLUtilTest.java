/**
 * 
 */
package org.alfresco.repo.rule;

import org.alfresco.repo.rule.common.RuleImpl;


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
        "<rule id='1' ruleType='inbound'>" +
        	"<title>title</title>" +
        	"<description>description</description>" +
        	"<conditions>" +
        		"<condition name='match-text'>" +
        			"<parameter name='text'>.doc</parameter>" +
                "</condition>" +
            "</conditions>" +
            "<actions>" +
            	"<action name='add-features'>" +
            		"<parameter name='aspect-name'>{http://www.alfresco.org/1.0}lockable</parameter>" +
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
        RuleImpl rule = RuleXMLUtil.XMLToRule(this.ruleService, XML);
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
		RuleImpl rule = RuleXMLUtil.XMLToRule(this.ruleService, ruleXML);
		assertNull(rule.getTitle());
		assertNull(rule.getDescription());
	}
}
