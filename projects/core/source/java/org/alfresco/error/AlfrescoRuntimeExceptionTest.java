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
package org.alfresco.error;

import java.util.Locale;

import junit.framework.TestCase;

import org.alfresco.i18n.I18NUtil;

/**
 * Alfresco runtime exception test
 * 
 * @author Roy Wetherall
 */
public class AlfrescoRuntimeExceptionTest extends TestCase
{
    private static final String BASE_RESOURCE_NAME = "org.alfresco.i18n.testMessages";
    private static final String PARAM_VALUE = "television";
    private static final String MSG_PARAMS = "msg_params";
    private static final String MSG_ERROR = "msg_error";
    private static final String VALUE_ERROR = "This is an error message.";
    private static final String VALUE_FR_ERROR = "Ceci est un message derreur.";
    private static final String VALUE_PARAMS = "What no " + PARAM_VALUE + "?";
    private static final String VALUE_FR_PARAMS = "Que non " + PARAM_VALUE + "?";
    private static final String NON_I18NED_MSG = "This is a non i18ned error message.";
   
    @Override
    protected void setUp() throws Exception
    {
        // Re-set the current locale to be the default
        I18NUtil.setLocale(Locale.getDefault());
    }
    
    public void testI18NBehaviour()
    {
        // Register the bundle
        I18NUtil.registerResourceBundle(BASE_RESOURCE_NAME);
        
        AlfrescoRuntimeException exception1 = new AlfrescoRuntimeException(MSG_PARAMS, new Object[]{PARAM_VALUE});
        assertEquals(VALUE_PARAMS, exception1.getMessage());
        AlfrescoRuntimeException exception3 = new AlfrescoRuntimeException(MSG_ERROR);
        assertEquals(VALUE_ERROR, exception3.getMessage());
            
        // Change the locale and re-test
        I18NUtil.setLocale(new Locale("fr", "FR"));
        
        AlfrescoRuntimeException exception2 = new AlfrescoRuntimeException(MSG_PARAMS, new Object[]{PARAM_VALUE});
        assertEquals(VALUE_FR_PARAMS, exception2.getMessage());   
        AlfrescoRuntimeException exception4 = new AlfrescoRuntimeException(MSG_ERROR);
        assertEquals(VALUE_FR_ERROR, exception4.getMessage());  
        
        AlfrescoRuntimeException exception5 = new AlfrescoRuntimeException(NON_I18NED_MSG);
        assertEquals(NON_I18NED_MSG, exception5.getMessage());
    }
}
