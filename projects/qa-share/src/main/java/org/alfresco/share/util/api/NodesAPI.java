package org.alfresco.share.util.api;

import java.util.Map;

import org.alfresco.rest.api.tests.client.PublicApiClient.ListResponse;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.api.tests.client.data.NodeRating;
import org.alfresco.rest.api.tests.client.data.Tag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Client for the REST API that deals with "/nodes" requests.
 * 
 * @author Abhijeet Bharade
 * 
 */
public class NodesAPI extends PublicAPIAbstract
{

    private static Log logger = LogFactory.getLog(NodesAPI.class);

    /**
     * Gets the {@link ListResponse} of {@link Tag} for particular node id.
     * 
     * @param authUser
     * @param domain
     * @param nodeId
     * @param params
     * @return a {@link ListResponse} of {@link Tag}
     * @throws PublicApiException
     */
    public ListResponse<Tag> getNodeTags(String authUser, String domain, String nodeId, Map<String, String> params) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        ListResponse<Tag> response = nodesClient.getNodeTags(nodeId, params);
        logger.info("Site found received: /n" + response);
        return response;
    }

    /**
     * Creates a {@link Tag} for a particular node id.
     * 
     * @param authUser
     * @param domain
     * @param nodeId
     * @param tag
     * @return {@link Tag}
     * @throws PublicApiException
     */
    public Tag createTag(String authUser, String domain, String nodeId, Tag tag) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        Tag response = nodesClient.createNodeTag(nodeId, tag);
        logger.info("Site found received: /n" + response);
        return response;
    }

    /**
     * Gets the {@link ListResponse} of {@link NodeRating} for particular node
     * id.
     * 
     * @param authUser
     * @param domain
     * @param nodeId
     * @param params
     * @return a {@link ListResponse} of {@link NodeRating}
     * @throws PublicApiException
     */
    public ListResponse<NodeRating> getNodeRatings(String authUser, String domain, String nodeId, Map<String, String> params) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        ListResponse<NodeRating> response = nodesClient.getNodeRatings(nodeId, params);
        logger.info("Site found received: /n" + response);
        return response;
    }

    /**
     * Creates a {@link NodeRating} for a particular node id.
     * 
     * @param authUser
     * @param domain
     * @param nodeId
     * @param nodeRating
     * @return
     * @throws PublicApiException
     */
    public NodeRating createNodeRating(String authUser, String domain, String nodeId, NodeRating nodeRating) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        NodeRating response = nodesClient.createNodeRating(nodeId, nodeRating);
        logger.info("Site found received: /n" + response);
        return response;
    }

}
