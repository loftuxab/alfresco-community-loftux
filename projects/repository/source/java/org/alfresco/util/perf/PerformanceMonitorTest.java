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
package org.alfresco.util.perf;

import java.lang.reflect.Method;

import junit.framework.TestCase;

/**
 * Enabled vm performance monitoring for <b>performance.summary.vm</b> and
 * <b>performance.PerformanceMonitorTest</b> to check.
 * 
 * @see org.alfresco.util.perf.PerformanceMonitor
 * 
 * @author Derek Hulley
 */
public class PerformanceMonitorTest extends TestCase
{
    private PerformanceMonitor testTimingMonitor;
    
    @Override
    public void setUp() throws Exception
    {
        Method testTimingMethod = PerformanceMonitorTest.class.getMethod("testTiming");
        testTimingMonitor = new PerformanceMonitor("PerformanceMonitorTest", "testTiming");
    }
    
    public void testSetUp() throws Exception
    {
        assertNotNull(testTimingMonitor);
    }
    
    public synchronized void testTiming() throws Exception
    {
        testTimingMonitor.start();
        
        wait(50);
        
        testTimingMonitor.stop();
    }
}
