package org.alfresco.module.org_alfresco_module_cloud.util;

public class CloudUtil
{
    public static String generateSiteShortName(String email)
    {
    	return email.replace('@', '-').replace('.', '-');
    }
}
