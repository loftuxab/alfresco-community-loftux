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

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Version test suite
 * 
 * @author Roy Wetherall
 */
public class RuleTestSuite extends TestSuite
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
       // suite.addTestSuite(RuleXMLUtilTest.class);
       // suite.addTestSuite(RuleStoreTest.class);
        suite.addTestSuite(RuleServiceImplTest.class);
        suite.addTestSuite(RuleServiceCoverageTest.class);
        return suite;
    }
}
