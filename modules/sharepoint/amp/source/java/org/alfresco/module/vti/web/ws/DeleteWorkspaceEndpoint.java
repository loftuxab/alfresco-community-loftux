
package org.alfresco.module.vti.web.ws;

import org.alfresco.module.vti.handler.MeetingServiceHandler;
import org.alfresco.module.vti.handler.SiteTypeException;
import org.alfresco.repo.SessionUser;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.alfresco.repo.webdav.auth.SharepointConstants;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;

/**
 * Class for handling DeleteWorkspace soap method
 * 
 * @author PavelYur
 */
public class DeleteWorkspaceEndpoint extends AbstractWorkspaceEndpoint
{
    public DeleteWorkspaceEndpoint(MeetingServiceHandler handler)
    {
        super(handler);
    }

    /**
     * A site is always required
     */
    @Override
    protected long getSiteRequired()
    {
        return 4l;
    }

    @Override
    protected void executeWorkspaceAction(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse,
            Element requestElement, SimpleNamespaceContext nc, String siteName, String title, String templateName,
            int lcid) throws Exception
    {
        // Perform the deletion
        try
        {
            handler.deleteWorkspace(siteName, (SessionUser) soapRequest.getSession().getAttribute(SharepointConstants.USER_SESSION_ATTRIBUTE));
        }
        catch (SiteDoesNotExistException se)
        {
            throw new VtiSoapException("vti.meeting.error.no_site", 0x4l); // TODO Is this the right code?
        }
        catch (SiteTypeException ste)
        {
            throw new VtiSoapException(ste.getMsgId(), 0x4l);
        }

        // Create the soap response
        soapResponse.setContentType("text/xml");
        soapResponse.getDocument().addElement("DeleteWorkspaceResponse", namespace);
    }
}