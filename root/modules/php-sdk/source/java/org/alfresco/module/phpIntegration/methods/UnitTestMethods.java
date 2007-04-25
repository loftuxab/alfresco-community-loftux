/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.phpIntegration.methods;

import org.alfresco.module.phpIntegration.PHPMethodExtension;
import org.alfresco.module.phpIntegration.PHPProcessorException;
import org.alfresco.module.phpIntegration.lib.Node;

/**
 * @author Roy Wetherall
 */
public class UnitTestMethods extends PHPMethodExtension
{
    public String dump_node(Node node)
    {
        return "NODE";
    }
    
    public void assertEquals(Object expected, Object value)
    {
        if (expected.equals(value) == false)
        {
            throw new PHPProcessorException("Expected value '" + expected + "' was '" + value + "'");
        }
    }
    
    public void assertNotNull(Object value, String message)
    {
        if (value == null)
        {
            if (message == null)
            {
                message = "Unexpected null value encountered.";
            }
            throw new PHPProcessorException(message);
        }
    }
    
    public void assertTrue(boolean value)
    {
        if (value == false)
        {
            throw new PHPProcessorException("Value is not True");
        }
    }
    
    public void assertFalse(boolean value)
    {
        if (value == true)
        {
            throw new PHPProcessorException("Value is not False");
        }
    }
    
    public void fail(String message)
    {
        throw new PHPProcessorException(message);
    }
}
