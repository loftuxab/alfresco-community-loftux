package org.alfresco.mbeans;

import java.io.IOException;

import javax.management.JMException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jmx.support.ConnectorServerFactoryBean;

/**
 * Factory that creates a JSR-160 <code>JMXConnectorServer</code>, 
 * optionally registers it with the <code>MBeanServer</code> and then starts it.
 * 
 * @author Stas Sokolovsky
 */
public class ConnectorServerFactory extends ConnectorServerFactoryBean
{
    private static Log logger = LogFactory.getLog(ConnectorServerFactory.class);
    
    private boolean enabled = false;
    
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Enables JMX connectivity during initialization, see {@link ConnectorServerFactory#afterPropertiesSet()}
     * @param enabled boolean
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Wraps original initialization method to log errors, rather than having 
     * exceptions occur within the Spring framework itself (this would cause the entire webapp to fail)
     * 
     * @see org.springframework.jmx.support.ConnectorServerFactoryBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws JMException, IOException
    {
        try
        {
            if (enabled)
            {
                super.afterPropertiesSet();

                if (logger.isInfoEnabled())
                {
                    logger.info("Created JMX serverConnector");
                }
            }
            else
            {
                if (logger.isInfoEnabled())
                {
                    logger.info("JMX serverConnector is disabled.");
                }
            }

        }
        catch (Exception e)
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("JMX ServerConnector can't be initialized due to: ", e);
            }
        }
    }
}
