package org.alfresco.repo.content;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.alfresco.repo.content.encoding.CharsetFinderTest;

/**
 * Suite for content-related tests.
 * 
 * This includes all the tests that need a full context, the
 *  rest are in {@link ContentMinimalContextTestSuite}
 * 
 * @author Derek Hulley
 */
public class DataModelContentTestSuite extends TestSuite
{
    public static Test suite() 
    {
        TestSuite suite = new TestSuite();
        
        suite.addTestSuite(CharsetFinderTest.class);
        suite.addTestSuite(MimetypeMapTest.class);
        return suite;
    }
}
