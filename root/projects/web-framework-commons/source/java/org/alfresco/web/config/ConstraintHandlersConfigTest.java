/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigException;

/**
 * JUnit tests to exercise the forms-related capabilities in to the web client
 * config service. These tests only include those that require a single config
 * xml file. Override-related tests, which use multiple config xml files, are
 * located in peer classes in this package.
 * 
 * @author Neil McErlean
 */
public class ConstraintHandlersConfigTest extends AbstractFormConfigTest
{
    @Override
    protected String getConfigXmlFile()
    {
        return "test-config-forms.xml";
    }
    
    @SuppressWarnings("unchecked")
	public void testReadConstraintHandlersFromConfigXml()
    {
        // Test that the constraint-handlers' constraints are read from the
        // config file
        Map<String, String> expectedValidationHandlers = new HashMap<String, String>();
        expectedValidationHandlers.put("REGEX",
                "Alfresco.forms.validation.regexMatch");
        expectedValidationHandlers.put("NUMERIC",
                "Alfresco.forms.validation.numericMatch");

        ConstraintHandlersConfigElement chConfigElement
            = (ConstraintHandlersConfigElement) globalConstraintHandlers;
        List<String> actualTypes = chConfigElement.getConstraintTypes();
        assertEquals("Incorrect type count.",
                expectedValidationHandlers.size(), actualTypes.size());
        
        assertEquals(expectedValidationHandlers.keySet(), new HashSet(actualTypes));

        // Test that the types map to the expected validation handler.
        for (String nextKey : expectedValidationHandlers.keySet())
        {
            String nextExpectedValue = expectedValidationHandlers.get(nextKey);
            String nextActualValue = chConfigElement
                    .getValidationHandlerFor(nextKey);
            assertTrue("Incorrect handler for " + nextKey + ": "
                    + nextActualValue, nextExpectedValue
                    .equals(nextActualValue));
        }

        // Test that the constraint-handlers' messages are read from the config
        // file
        Map<String, String> expectedMessages = new HashMap<String, String>();
        expectedMessages.put("REGEX", "");
        expectedMessages.put("NUMERIC", "Test Message");

        // Test that the types map to the expected message.
        for (String nextKey : expectedValidationHandlers.keySet())
        {
            String nextExpectedValue = expectedMessages.get(nextKey);
            String nextActualValue = chConfigElement.getMessageFor(nextKey);
            assertEquals("Incorrect message for " + nextKey + ".",
                    nextExpectedValue, nextActualValue);
        }

        // Test that the constraint-handlers' message-ids are read from the
        // config file
        Map<String, String> expectedMessageIDs = new HashMap<String, String>();
        expectedMessageIDs.put("REGEX", "");
        expectedMessageIDs.put("NUMERIC", "regex_error");

        // Test that the types map to the expected message-id.
        for (String nextKey : expectedValidationHandlers.keySet())
        {
            String nextExpectedValue = expectedMessageIDs.get(nextKey);
            String nextActualValue = chConfigElement.getMessageIdFor(nextKey);
            assertEquals("Incorrect message-id for " + nextKey + ".",
                    nextExpectedValue, nextActualValue);
        }
    }

    public void testConstraintHandlerElementShouldHaveNoChildren()
    {
        try
        {
            ConstraintHandlersConfigElement chConfigElement = (ConstraintHandlersConfigElement) globalConstraintHandlers;
            chConfigElement.getChildren();
            fail("getChildren() did not throw an exception");
        } catch (ConfigException ce)
        {
            // expected exception
        }

    }

    /**
     * Tests the combination of a ConstraintHandlersConfigElement with another that
     * contains additional data.
     */
    public void testCombineConstraintHandlersWithAddedParam()
    {
        ConstraintHandlersConfigElement basicElement = new ConstraintHandlersConfigElement();
        basicElement.addDataMapping("REGEX", "foo.regex", null, null);

        // This element is the same as the above, but adds message & message-id.
        ConstraintHandlersConfigElement elementWithAdditions = new ConstraintHandlersConfigElement();
        elementWithAdditions.addDataMapping("REGEX", "foo.regex", "msg", "msg-id");

        ConfigElement combinedElem = basicElement.combine(elementWithAdditions);
        assertEquals("Combined elem incorrect.", elementWithAdditions,
                combinedElem);
    }

    /**
     * Tests the combination of a ConstraintHandlersConfigElement with another that
     * contains modified data.
     */
    public void testCombineConstraintHandlersWithModifiedParam()
    {
        ConstraintHandlersConfigElement initialElement = new ConstraintHandlersConfigElement();
        initialElement.addDataMapping("REGEX", "foo.regex", null, null);

        // This element is the same as the above, but adds message & message-id.
        ConstraintHandlersConfigElement modifiedElement = new ConstraintHandlersConfigElement();
        modifiedElement.addDataMapping("REGEX", "bar.regex", "msg", "msg-id");

        ConfigElement combinedElem = initialElement.combine(modifiedElement);
        assertEquals("Combined elem incorrect.", modifiedElement, combinedElem);
    }
    
    /**
     * Tests the combination of a ConstraintHandlersConfigElement with another that
     * contains deleted data.
     */
    public void testCombineConstraintHandlersWithDeletedParam()
    {
        ConstraintHandlersConfigElement initialElement = new ConstraintHandlersConfigElement();
        initialElement.addDataMapping("REGEX", "bar.regex", "msg", "msg-id");

        // This element is the same as the above, but adds message & message-id.
        ConstraintHandlersConfigElement modifiedElement = new ConstraintHandlersConfigElement();
        modifiedElement.addDataMapping("REGEX", "bar.regex", null, null);

        ConfigElement combinedElem = initialElement.combine(modifiedElement);
        assertEquals("Combined elem incorrect.", modifiedElement, combinedElem);
    }
}
