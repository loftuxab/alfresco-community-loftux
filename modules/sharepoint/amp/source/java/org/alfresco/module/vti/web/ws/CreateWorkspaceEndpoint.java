
package org.alfresco.module.vti.web.ws;

import org.alfresco.module.vti.handler.MeetingServiceHandler;
import org.alfresco.repo.SessionUser;
import org.alfresco.repo.webdav.auth.SharepointConstants;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;

/**
 * Class for handling CreateWorkspace soap method
 * 
 * @author PavelYur
 */
public class CreateWorkspaceEndpoint extends AbstractWorkspaceEndpoint
{
    public CreateWorkspaceEndpoint(MeetingServiceHandler handler)
    {
        super(handler);
    }

    /**
     * A site should not be given
     */
    @Override
    protected long getSiteRequired()
    {
        return -1;
    }

    /**
     * Create new Meeting Workspace on Alfresco server
     */
    @Override
    protected void executeWorkspaceAction(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse,
            Element requestElement, SimpleNamespaceContext nc, String siteName, String title, String templateName,
            int lcid) throws Exception
    {
        // A title is required
        if (title == null || title.length() == 0)
        {
            throw new RuntimeException("Site name is not specified. Please fill up subject field.");
        }

        // Have the site created
        siteName = handler.createWorkspace(title, templateName, lcid, getTimeZoneInformation(requestElement),
                (SessionUser) soapRequest.getSession().getAttribute(SharepointConstants.USER_SESSION_ATTRIBUTE));

        // Create the soap response
        Element workspace = buildWorkspaceResponse(soapResponse);
        workspace.addAttribute("Url", getHost(soapRequest) + soapRequest.getAlfrescoContextName() + "/" + siteName);
    }
}