/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.util;

import java.util.Date;

import junit.framework.TestCase;

public class ISO8601DateFormatTest extends TestCase
{
    
    public void testConversion()
    {
        System.out.println(Math.PI);
        String test = "2005-09-16T17:01:03.456+01:00";
        Date date = ISO8601DateFormat.parse(test);
        String strDate = ISO8601DateFormat.format(date);
        assertEquals(test, strDate);
    }
    
}
