
package org.alfresco.module.vti.web.ws;

import org.alfresco.module.vti.handler.MeetingServiceHandler;
import org.alfresco.module.vti.handler.SiteTypeException;
import org.alfresco.module.vti.metadata.model.MeetingBean;

/**
 * Class for handling UpdateMeeting soap method
 * 
 * @author Nick Burch
 */
public class UpdateMeetingEndpoint extends AbstractMeetingEndpoint
{
    public UpdateMeetingEndpoint(MeetingServiceHandler handler)
    {
        super(handler);
    }
    
    @Override
    protected void executeMeetingAction(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse, String siteName,
            MeetingBean meetingBean, int sequence, int recurrenceId, boolean ignoreAttendees, boolean cancelMeeting) throws Exception
    {
        // Perform the deletion
        try
        {
            handler.updateMeeting(siteName, meetingBean);
        }
        catch (SiteTypeException ste)
        {
            throw new VtiSoapException(ste.getMsgId(), 6l);
        }
        
        // Build the response
        buildMeetingResponse(soapResponse);
    }
}