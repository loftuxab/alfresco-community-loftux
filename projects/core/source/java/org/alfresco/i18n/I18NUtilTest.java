package org.alfresco.i18n;

import java.util.Locale;

import junit.framework.TestCase;

/**
 * I18NUtil unit tests
 * 
 * @author Roy Wetherall
 */
public class I18NUtilTest extends TestCase
{
    private static final String BASE_RESOURCE_NAME = "org.alfresco.i18n.testMessages";
    private static final String PARAM_VALUE = "television";
    private static final String MSG_YES = "msg_yes";    
    private static final String MSG_NO = "msg_no";
    private static final String MSG_PARAMS = "msg_params";
    private static final String VALUE_YES = "Yes";
    private static final String VALUE_NO = "No";
    private static final String VALUE_PARAMS = "What no " + PARAM_VALUE + "?";
    private static final String VALUE_FR_YES = "Oui";
    private static final String VALUE_FR_NO = "Non";
    private static final String VALUE_FR_PARAMS = "Que non " + PARAM_VALUE + "?";
   
    @Override
    protected void setUp() throws Exception
    {
        // Re-set the current locale to be the default
        I18NUtil.setLocale(Locale.getDefault());
    }
    
    /**
     * Test the set and get methods
     */
    public void testSetAndGet()
    {
        // Check that the default locale is returned 
        assertEquals(Locale.getDefault(), I18NUtil.getLocale());
        
        // Set the locals
        I18NUtil.setLocale(Locale.CANADA_FRENCH);
        assertEquals(Locale.CANADA_FRENCH, I18NUtil.getLocale());
        
        // Reset the locale
        I18NUtil.setLocale(null);
        assertEquals(Locale.getDefault(), I18NUtil.getLocale());
    }
    
    /**
     * Test getMessage
     */
    public void testGetMessage()
    {
        // Check with no bundles loaded
        assertNull(I18NUtil.getMessage(MSG_NO));        
        
        // Register the bundle
        I18NUtil.registerResourceBundle(BASE_RESOURCE_NAME);

        // Check default values
        assertEquals(VALUE_YES, I18NUtil.getMessage(MSG_YES));
        assertEquals(VALUE_NO, I18NUtil.getMessage(MSG_NO));
        
        // Check not existant value
        assertNull(I18NUtil.getMessage("bad_key"));        
        
        // Change the locale and re-test
        I18NUtil.setLocale(new Locale("fr", "FR"));
        
        // Check values
        assertEquals(VALUE_FR_YES, I18NUtil.getMessage(MSG_YES));
        assertEquals(VALUE_FR_NO, I18NUtil.getMessage(MSG_NO));
        
        // Check values when overriding the locale
        assertEquals(VALUE_YES, I18NUtil.getMessage(MSG_YES, Locale.getDefault()));
        assertEquals(VALUE_NO, I18NUtil.getMessage(MSG_NO, Locale.getDefault()));
    }
    
    /**
     * Test getting a parameterised message
     */
    public void testGetMessageWithParams()
    {
        // Register the bundle
        I18NUtil.registerResourceBundle(BASE_RESOURCE_NAME);
        
        // Check the default value
        assertEquals(VALUE_PARAMS, I18NUtil.getMessage(MSG_PARAMS, new Object[]{PARAM_VALUE}));
            
        // Change the locale and re-test
        I18NUtil.setLocale(new Locale("fr", "FR"));
        
        // Check the default value
        assertEquals(VALUE_FR_PARAMS, I18NUtil.getMessage(MSG_PARAMS, new Object[]{PARAM_VALUE}));       
        
        // Check values when overriding the locale    
        assertEquals(VALUE_PARAMS, I18NUtil.getMessage(MSG_PARAMS, new Object[]{PARAM_VALUE}, Locale.getDefault()));
    }
}
