package org.alfresco.web.site;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigService;
import org.alfresco.web.config.DynamicWebsiteConfigElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DynamicWebsite 
{
	private static Log logger = LogFactory.getLog(DynamicWebsite.class);
	
	public static ConfigService getConfigService()
	{
		//return (ConfigService) FrameworkHelper.getApplicationContext().getBean("dynamicwebsite.config");
		return FrameworkHelper.getConfigService();
	}
			
    public static DynamicWebsiteConfigElement getConfig()
    {    	
    	Config config = getConfigService().getConfig("WebFramework");
    	return (DynamicWebsiteConfigElement) config.getConfigElement("dynamic-website");
    }
}
