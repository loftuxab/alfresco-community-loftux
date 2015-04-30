package org.alfresco.enterprise.repo.officeservices.service;

public class Const
{

    private Const()
    {
        // prevent instantiation
    }
    
    // used to build the default site name automatically based on the context
    // keep this in sync with org.alfresco.enterprise.repo.jscript.app.AosCustomResponse.DEFAULT_SITE_PATH_IN_CONTEXT
    public static final String DEFAULT_SITE_PATH_IN_CONTEXT = "/aos";
    
    public static final String SERVICE_MAPPING_IN_CONTEXT = "/aos";
    
    public static final String HISTORY_PATH_ELEMENT = "/_aos_history";
    
    public static final String NODEID_PATH_ELEMENT = "/_aos_nodeid";

}
