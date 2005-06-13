package org.alfresco.repo.ref.qname;

import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.QNamePattern;

import junit.framework.TestCase;

/**
 * Tests the various implementations of the
 * {@link org.alfresco.service.namespace.QNamePattern}.
 * 
 * @author Derek Hulley
 */
public class QNamePatternTest extends TestCase
{
    private static final String TEST_NAMESPACE = "http://www.alfresco.org/QNamePatternTest";
    
    QName check1;
    QName check2;
    QName check3;

    public QNamePatternTest(String name)
    {
        super(name);
    }
    
    public void setUp() throws Exception
    {
        check1 = QName.createQName(null, "ABC");
        check2 = QName.createQName(TEST_NAMESPACE, "XYZ");
        check3 = QName.createQName(TEST_NAMESPACE, "ABC");
    }
    
    public void testSimpleQNamePattern() throws Exception
    {
        QNamePattern pattern = QName.createQName(TEST_NAMESPACE, "ABC");
        
        // check
        assertFalse("Simple match failed: " + check1, pattern.isMatch(check1));
        assertFalse("Simple match failed: " + check2, pattern.isMatch(check2));
        assertTrue("Simple match failed: " + check3, pattern.isMatch(check3));
    }
    
    public void testRegexQNamePatternMatcher() throws Exception
    {
        QNamePattern pattern = new RegexQNamePattern(".*alfresco.*", "A.?C");
        
        // check
        assertFalse("Regex match failed: " + check1, pattern.isMatch(check1));
        assertFalse("Regex match failed: " + check2, pattern.isMatch(check2));
        assertTrue("Regex match failed: " + check3, pattern.isMatch(check3));
        
        assertTrue("All match failed: " + check1, RegexQNamePattern.MATCH_ALL.isMatch(check1));
        assertTrue("All match failed: " + check2, RegexQNamePattern.MATCH_ALL.isMatch(check2));
        assertTrue("All match failed: " + check3, RegexQNamePattern.MATCH_ALL.isMatch(check3));
    }
}
