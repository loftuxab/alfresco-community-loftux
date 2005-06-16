package org.alfresco.repo.rule;

import org.alfresco.repo.rule.common.ParameterDefinitionImplTest;
import org.alfresco.repo.rule.common.RuleActionDefinitionImplTest;
import org.alfresco.repo.rule.common.RuleActionImplTest;
import org.alfresco.repo.rule.common.RuleConditionDefinitionImplTest;
import org.alfresco.repo.rule.common.RuleConditionImplTest;
import org.alfresco.repo.rule.common.RuleTypeImplTest;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Version test suite
 * 
 * @author Roy Wetherall
 */
public class RuleServiceTestSuite extends TestSuite
{
    /**
     * Creates the test suite
     * 
     * @return  the test suite
     */
    public static Test suite() 
    {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(RuleTypeImplTest.class);
        suite.addTestSuite(ParameterDefinitionImplTest.class);
        suite.addTestSuite(RuleActionDefinitionImplTest.class);
        suite.addTestSuite(RuleConditionDefinitionImplTest.class);
        suite.addTestSuite(RuleActionImplTest.class);
        suite.addTestSuite(RuleConditionImplTest.class);
        suite.addTestSuite(RuleXMLUtilTest.class);
        suite.addTestSuite(RuleStoreTest.class);
        suite.addTestSuite(RuleServiceImplTest.class);
        suite.addTestSuite(RuleServiceSystemTest.class);
        return suite;
    }
}
