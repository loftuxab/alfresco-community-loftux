package org.alfresco.deployment.impl.dmr;

/**
 * Root Locator for mapping AVM Web Projects onto
 * DM paths.   
 * 
 * The root mapper specifies where in a DM repository the project should go.
 * 
 * For example it may say all projects go in "app:company_home" or there may be a 
 * more complex mapping.
 * 
 *
 * @author Mark Rogers
 */
public interface RootLocator
{
    
    /**
     * Get The x-path pattern for the root of the deployment
     * This part of the path must exist prior to the first deployment
     */
     String getRootQuery(String projectName);
}
