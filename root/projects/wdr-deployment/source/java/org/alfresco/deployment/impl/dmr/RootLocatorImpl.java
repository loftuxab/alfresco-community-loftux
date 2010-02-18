package org.alfresco.deployment.impl.dmr;

import java.util.Map;

/**
 * A simple implemention of root locator that puts all deployed web projects 
 * into a single web directory.
 * 
 * Additionally an additional map of mappings between web project 
 * and root query can be specified.  If the web project matches 
 * one of the mappings then that is used, else the default localtion 
 * is used. 
 *
 * @author Mark Rogers
 */
public class RootLocatorImpl implements RootLocator
{
    private String defaultLocation = "/app:company_home";
    
    private Map<String, String> projectToQueryMap;

    public String getRootQuery(String projectName)
    {
        // If there is a project specific mapping
        if(projectToQueryMap != null)
        {
            String query = projectToQueryMap.get(projectName);
            if(query != null)
            {
                return query;
            }
        }
        // return the 
        return defaultLocation;
    }

    public void setDefaultLocation(String rootQuery)
    {
        this.defaultLocation = rootQuery;
    }

    public String getDefaultLocation()
    {
        return defaultLocation;
    }

    public void setProjectToQueryMap(Map<String, String> projectToQueryMap)
    {
        this.projectToQueryMap = projectToQueryMap;
    }

    public Map<String, String> getProjectToQueryMap()
    {
        return projectToQueryMap;
    }
}
