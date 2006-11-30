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

/**
 * Utility class providing helper methods for various types of <code>equals</code> functionality
 * 
 * @author Derek Hulley
 */
public class EqualsHelper
{
    /**
     * Performs an equality check <code>left.equals(right)</code> after checking for null values
     * 
     * @param left the Object appearing in the left side of an <code>equals</code> statement
     * @param right the Object appearing in the right side of an <code>equals</code> statement
     * @return Return true or false even if one or both of the objects are null
     */
	public static boolean nullSafeEquals(Object left, Object right)
    {
        return (left == right) || (left != null && right != null && left.equals(right));
    }
}
