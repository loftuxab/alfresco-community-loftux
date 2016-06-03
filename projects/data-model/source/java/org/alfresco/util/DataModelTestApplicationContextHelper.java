package org.alfresco.util;

import org.springframework.context.ApplicationContext;

/**
 * @author Andy
 *
 */
public class DataModelTestApplicationContextHelper extends BaseApplicationContextHelper
{
    /** location of required configuration files */
    public static final String[] CONFIG_LOCATIONS = new String[] { "classpath:alfresco/data-model-stand-alone-context.xml" };
    
    /**
     * Provides a static, single instance of the default Alfresco application context.  This method can be
     * called repeatedly.
     * <p/>
     * If the configuration requested differs from one used previously, then the previously-created
     * context is shut down.
     * 
     * @return Returns an application context for the default Alfresco configuration
     */
    public synchronized static ApplicationContext getApplicationContext()
    {
        return BaseApplicationContextHelper.getApplicationContext(CONFIG_LOCATIONS);
    }
}
