/*-----------------------------------------------------------------------------
*  Copyright 2006 Alfresco Inc.
*  
*  Licensed under the Mozilla Public License version 1.1
*  with a permitted attribution clause. You may obtain a
*  copy of the License at:
*  
*      http://www.alfresco.org/legal/license.txt
*  
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
*  either express or implied. See the License for the specific
*  language governing permissions and limitations under the
*  License.
*  
*  
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    AVMUrlValveTest.java
*----------------------------------------------------------------------------*/

package org.alfresco.catalina.valve;

import junit.framework.TestCase;

import java.io.PrintStream;

/**
* @exclude
*/
public class AVMUrlValveTest extends TestCase
{

    public AVMUrlValveTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        System.out.println("set up logic goes here...");
    }

    protected void tearDown() throws Exception
    {
        System.out.println("tear down logic goes here...");
    }
    
    /**
    *  Nil test. 
    */
    public void testNil()
    {
        try
        {
            System.out.println("add some tests here...");
        }
        catch (Exception e)
        {
        }
    }
}
