package org.alfresco.module.org_alfresco_module_cloud.aos;

import java.io.IOException;

import org.alfresco.enterprise.repo.officeservices.service.AuthorService;
import org.alfresco.module.org_alfresco_module_cloud.analytics.Analytics;

import com.xaldon.officeservices.exceptions.AuthenticationRequiredException;
import com.xaldon.officeservices.exceptions.VermeerException;
import com.xaldon.officeservices.protocol.VermeerRequest;
import com.xaldon.officeservices.protocol.VermeerResponse;

public class CloudAuthorService extends AuthorService
{

    private static final long serialVersionUID = -2150225211812065840L;

    @Override
    public void openService(VermeerRequest vermeerRequest, VermeerResponse vermeerResponse) throws IOException, VermeerException, AuthenticationRequiredException
    {
        super.openService(vermeerRequest, vermeerResponse);
        String vtiVersion = vermeerRequest.getMethodVersion();
        String userAgent = vermeerRequest.getRequest().getHeader("User-Agent");
        if( (vtiVersion != null) && (vtiVersion.length() > 25) )
        {
            vtiVersion = vtiVersion.substring(0, 25);
        }
        if( (userAgent != null) && (userAgent.length() > 200) )
        {
            userAgent = userAgent.substring(0, 200);
        }
        Analytics.record_SharepointSessionStart(vtiVersion, userAgent);
    }


}
