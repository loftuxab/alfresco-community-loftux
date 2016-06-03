package org.alfresco.share.util.api;

import java.util.Map;

import org.alfresco.rest.api.tests.client.PublicApiClient.ListResponse;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.workflow.api.model.Deployment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * REST-API for handling /deployment requests.
 * 
 * @author Abhijeet Bharade
 * 
 */
public class DeploymentAPI extends PublicAPIAbstract
{

    private static Log logger = LogFactory.getLog(DeploymentAPI.class);

    /**
     * Gets a list {@link Deployment} object for a particular person Id.
     * 
     * @param authUser
     * @param domain
     * @param params
     * @return {@link ListResponse} of {@link Deployment}
     * @throws PublicApiException
     */
    public ListResponse<Deployment> getDeployments(String authUser, String domain, Map<String, String> params) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        ListResponse<Deployment> response = deploymentsClient.getDeployments(params);
        logger.info("Received response: " + response);
        return response;
    }

    /**
     * Gets a {@link Deployment} object for a particular deploymentId 
     * @param authUser
     * @param domain
     * @param deploymentId
     * @return {@link Deployment}
     * @throws PublicApiException
     */
    public Deployment getDeploymentById(String authUser, String domain, String deploymentId) throws PublicApiException
    {
        workflowApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));
        Deployment response = deploymentsClient.findDeploymentById(deploymentId);
        logger.info("Received response: " + response);
        return response;
    }

}
