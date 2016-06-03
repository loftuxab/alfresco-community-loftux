package org.alfresco.share.util.api;

import java.util.Map;

import org.alfresco.rest.api.tests.client.PublicApiClient.ListResponse;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.api.tests.client.data.PersonNetwork;
import org.alfresco.rest.api.tests.client.data.Preference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gdata.data.Person;

/**
 * Client for the REST API that deals with "/people" requests.
 * 
 * @author Abhijeet Bharade
 * 
 */
public class PeopleAPI extends PublicAPIAbstract
{

    private static Log logger = LogFactory.getLog(PeopleAPI.class);

    /**
     * Gets {@link Preference} of {@link Person} for a particular person id and
     * {@link Preference} id.
     * 
     * @param authUser
     * @param domain
     * @param personId
     * @param preferenceId
     * @return {@link Preference}
     * @throws PublicApiException
     */
    public Preference getPersonPreference(String authUser, String domain, String personId, String preferenceId) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        Preference response = peopleClient.getPreference(personId, preferenceId);
        logger.info("Site found received: /n" + response);
        return response;
    }

    /**
     * Gets list of {@link Preference} of {@link Person} for a particular person
     * id.
     * 
     * @param authUser
     * @param domain
     * @param personId
     * @param params
     * @return {@link Preference}
     * @throws PublicApiException
     */
    public ListResponse<Preference> getPersonPreferences(String authUser, String domain, String personId, Map<String, String> params) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        ListResponse<Preference> response = peopleClient.getPreferences(personId, params);
        logger.info("Site found received: /n" + response);
        return response;
    }

    public ListResponse<PersonNetwork> getNetworkMemberships(String authUser, String domain, String personId, Map<String, String> params) throws PublicApiException
    {
        publicApiClient.setRequestContext(new RequestContext(domain, getAuthDetails(authUser)[0], getAuthDetails(authUser)[1]));

        ListResponse<PersonNetwork> response = peopleClient.getNetworkMemberships(personId, params);
        logger.info("Site found received: /n" + response);
        return response;
    }
}
