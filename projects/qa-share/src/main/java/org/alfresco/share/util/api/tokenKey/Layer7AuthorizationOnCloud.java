/*
 * #%L
 * qa-share
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.share.util.api.tokenKey;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.api.tokenKey.oauth.LocalServerReceiver;
import org.alfresco.share.util.api.tokenKey.oauth.VerificationCodeReceiver;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains only the logic specific to using the Alfresco Public API
 * against Alfresco in the cloud.
 * 
 * @author dyukhnovets
 */
public class Layer7AuthorizationOnCloud extends AbstractUtils
{
    private WebDrone drone;
    private String API_HOST;
    private String API_KEY;
    private String API_SECRET_KEY;

    public Layer7AuthorizationOnCloud(WebDrone drone)
    {
        this.drone = drone;
        API_HOST = getAPIURL(drone);
        API_KEY = dronePropertiesMap.get(drone).getApiKey();
        API_SECRET_KEY = dronePropertiesMap.get(drone).getApiSecretKey();
    }

    private final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private final JsonFactory JSON_FACTORY = new JacksonFactory();
    private final String TOKEN_SERVER_URL = "auth/oauth/versions/2/token";
    private final String AUTHORIZATION_SERVER_URL = "auth/oauth/versions/2/authorize";
    private final String SCOPE = "public_api";
    private final List<String> SCOPES = Arrays.asList(SCOPE);
    private HttpRequestFactory requestFactory;
    private Credential credential;

    public WebDrone launchInBrowser(String redirectUrl, String clientId, String scope) throws IOException
    {

        String authorizationUrl = new AuthorizationCodeRequestUrl(API_HOST + AUTHORIZATION_SERVER_URL, clientId).setRedirectUri(redirectUrl)
                .setScopes(Arrays.asList(scope)).build();

        drone.navigateTo(authorizationUrl);
        return drone;
    }

    public Credential authorize(String code, String redirectUri) throws IOException
    {

        AuthorizationCodeFlow codeFlow = new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(), HTTP_TRANSPORT, JSON_FACTORY,
                new GenericUrl(API_HOST + TOKEN_SERVER_URL), new ClientParametersAuthentication(API_KEY, API_SECRET_KEY), API_KEY, API_HOST
                        + AUTHORIZATION_SERVER_URL).setScopes(SCOPES).build();

        TokenResponse response = codeFlow.newTokenRequest(code).setRedirectUri(redirectUri).setScopes(SCOPES).execute();

        return codeFlow.createAndStoreCredential(response, null);

    }

    public String getUserTokenKey(final String userName, final String password)
    {
        String accessToken = "";
        if (this.requestFactory == null)
        {
            VerificationCodeReceiver receiver = new LocalServerReceiver();
            try
            {
                receiver.startJetty();
                String redirectUri = receiver.getRedirectUri();
                drone = launchInBrowser(redirectUri, API_KEY, SCOPE);
                drone.findAndWait(By.xpath("//input[@type='submit']"));
                drone.findAndWait(By.xpath("//input[@id='username']")).sendKeys(userName);
                drone.findAndWait(By.xpath("//input[@id='password']")).sendKeys(password);
                drone.findAndWait(By.xpath("//input[@value='Grant']")).click();
                drone.findAndWait(By.xpath("//body"));
                String currentUrl = drone.getCurrentUrl();
                String code;
                try
                {
                    code = currentUrl.substring(currentUrl.indexOf("code=") + 5, currentUrl.lastIndexOf("&"));

                }
                catch (ArrayIndexOutOfBoundsException ex)
                {
                    throw new Exception("Looks like Jetty is not started");
                }
                if (!code.equals(""))
                {
                    this.credential = authorize(code, redirectUri);
                    this.requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer()
                    {
                        @Override
                        public void initialize(HttpRequest request) throws IOException
                        {
                            credential.initialize(request);
                            request.setParser(new JsonObjectParser(new JacksonFactory()));
                        }
                    });

                    accessToken = credential.getAccessToken();
                }
                else
                {
                    accessToken = null; // something is wrong
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            // finally {
            // try {
            // //it will be stopped when java process is closed
            // //receiver.stop();
            // } catch (Exception ignored) {}
            // }
        }
        return accessToken;
    }
}
