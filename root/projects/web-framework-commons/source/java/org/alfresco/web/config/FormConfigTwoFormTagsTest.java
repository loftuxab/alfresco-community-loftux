/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigElement;

public class FormConfigTwoFormTagsTest extends FormConfigBasicTest
{
    // This class inherits all of its test messages from the superclass and simply
    // overrides a number of changed properties - mimicking the changes in the xml.
    
    private FormConfigElement cmContentFormConfigElement;

    @Override
    protected List<String> getConfigFiles()
    {
        List<String> result = new ArrayList<String>(1);
        result.add("test-config-forms-twoFormTags.xml");
        return result;
    }
    
    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        Config cmContentConfigObj = configService.getConfig("cm:content");
        assertNotNull(cmContentConfigObj);
    
        ConfigElement formConfigObj = cmContentConfigObj.getConfigElement("form");
        assertNotNull(formConfigObj);
        assertTrue("formConfigObj should be instanceof FormConfigElement.",
                formConfigObj instanceof FormConfigElement);
        
        cmContentFormConfigElement = (FormConfigElement)formConfigObj;
    }
    
    public void testGetVisibleFieldsForFormWithoutFieldVisibilityReturnsNull()
    {
        assertNotNull(cmContentFormConfigElement);
        assertNull(cmContentFormConfigElement.getSubmissionURL());
        
        assertEquals(null, cmContentFormConfigElement.getVisibleCreateFieldNames());
        assertEquals(null, cmContentFormConfigElement.getVisibleEditFieldNames());
        assertEquals(null, cmContentFormConfigElement.getVisibleViewFieldNames());
    }
    
    public void testFieldVisibilityForTwoCombinedFormTags()
    {
        Config myExampleConfigObj = configService.getConfig("my:example");
        assertNotNull(myExampleConfigObj);

        Config cmContentConfigObj = configService.getConfig("cm:content");
        assertNotNull(cmContentConfigObj);
        
        FormConfigElement myExampleFormConfig = (FormConfigElement)myExampleConfigObj.getConfigElement("form");
        assertNotNull(myExampleFormConfig);
        
        ConfigElement cmContentFormConfig = cmContentConfigObj.getConfigElement("form");
        assertNotNull(cmContentFormConfig);
        
        FormConfigElement combinedConfig = (FormConfigElement)myExampleFormConfig.combine(cmContentFormConfig);
        
        Set<String> expectedFields = new LinkedHashSet<String>();
        expectedFields.add("cm:name");
        expectedFields.add("my:text");
        expectedFields.add("my:mltext");
        expectedFields.add("my:date");
        expectedFields.add("my:duplicate");
        expectedFields.add("my:int");
        expectedFields.add("my:broken");
        
        assertEquals(new ArrayList<String>(expectedFields), myExampleFormConfig.getVisibleCreateFieldNames());
        assertEquals(new ArrayList<String>(expectedFields), myExampleFormConfig.getVisibleEditFieldNames());
        assertEquals(new ArrayList<String>(expectedFields), myExampleFormConfig.getVisibleViewFieldNames());

        assertEquals(new ArrayList<String>(expectedFields), combinedConfig.getVisibleCreateFieldNames());
        assertEquals(new ArrayList<String>(expectedFields), combinedConfig.getVisibleEditFieldNames());
        assertEquals(new ArrayList<String>(expectedFields), combinedConfig.getVisibleViewFieldNames());
    }
}
