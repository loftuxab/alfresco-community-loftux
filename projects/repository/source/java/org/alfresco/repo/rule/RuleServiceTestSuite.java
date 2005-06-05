package org.alfresco.repo.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.alfresco.repo.rule.impl.ParameterDefinitionImplTest;
import org.alfresco.repo.rule.impl.RuleActionDefinitionImplTest;
import org.alfresco.repo.rule.impl.RuleActionImplTest;
import org.alfresco.repo.rule.impl.RuleConditionDefinitionImplTest;
import org.alfresco.repo.rule.impl.RuleConditionImplTest;
import org.alfresco.repo.rule.impl.RuleServiceImplTest;
import org.alfresco.repo.rule.impl.RuleStoreTest;
import org.alfresco.repo.rule.impl.RuleTypeImplTest;
import org.alfresco.repo.rule.impl.RuleXMLUtilTest;

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
        return suite;
    }
}
