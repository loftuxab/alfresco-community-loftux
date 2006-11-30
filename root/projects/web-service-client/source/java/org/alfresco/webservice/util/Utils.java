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
package org.alfresco.webservice.util;

import org.alfresco.webservice.types.NamedValue;

/**
 * This class provides a number of common utility methods usful when using the web service API
 * 
 * @author Roy Wetherall
 */
public class Utils
{

    public static NamedValue createNamedValue(String name, String value)
    {
        NamedValue namedValue = new NamedValue();
        namedValue.setName(name);
        namedValue.setIsMultiValue(false);
        namedValue.setValue(value);
        return namedValue;
    }
    
    public static NamedValue createNamedValue(String name, String[] values)
    {
        NamedValue namedValue = new NamedValue();
        namedValue.setName(name);
        namedValue.setIsMultiValue(true);
        namedValue.setValues(values);
        return namedValue;
    }
}
