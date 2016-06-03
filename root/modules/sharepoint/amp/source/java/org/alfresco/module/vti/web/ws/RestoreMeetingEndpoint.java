
package org.alfresco.module.vti.web.ws;

import org.alfresco.module.vti.handler.MeetingServiceHandler;
import org.alfresco.module.vti.handler.ObjectNotFoundException;
import org.alfresco.module.vti.metadata.model.MeetingBean;
import org.alfresco.repo.site.SiteDoesNotExistException;

/**
 * Class for handling RestoreMeeting soap method
 * 
 * @author PavelYur
 */
public class RestoreMeetingEndpoint extends AbstractMeetingEndpoint
{
    public RestoreMeetingEndpoint(MeetingServiceHandler handler)
    {
        super(handler);
    }

    @Override
    protected void executeMeetingAction(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse, String siteName,
            MeetingBean meetingBean, int sequence, int recurrenceId, boolean ignoreAttendees, boolean cancelMeeting) throws Exception
    {
        // Perform the restoration, if possible
        String uid = meetingBean.getId();
        try
        {
            handler.restoreMeeting(siteName, uid);
        }
        catch (SiteDoesNotExistException sne)
        {
            throw new VtiSoapException("Site '" + siteName + "' not found", 0x8102003el);
        }
        catch (ObjectNotFoundException onfe)
        {
            throw new VtiSoapException("Meeting with UID '" + uid + "' not found", 0x8102003el);
        }
        
        // Build the response
        buildMeetingResponse(soapResponse);
    }
}
