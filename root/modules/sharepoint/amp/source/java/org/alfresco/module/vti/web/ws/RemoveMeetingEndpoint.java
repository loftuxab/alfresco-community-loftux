
package org.alfresco.module.vti.web.ws;

import org.alfresco.module.vti.handler.MeetingServiceHandler;
import org.alfresco.module.vti.metadata.model.MeetingBean;

/**
 * Class for handling RemoveMeeting soap method
 * 
 * @author PavelYur
 */
public class RemoveMeetingEndpoint extends AbstractMeetingEndpoint
{
    public RemoveMeetingEndpoint(MeetingServiceHandler handler)
    {
        super(handler);
    }

    @Override
    protected void executeMeetingAction(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse, String siteName,
            MeetingBean meetingBean, int sequence, int recurrenceId, boolean ignoreAttendees, boolean cancelMeeting) throws Exception
    {
        // Perform the deletion
        handler.removeMeeting(siteName, recurrenceId, meetingBean.getId(), sequence, null, cancelMeeting);
        
        // Build the response
        buildMeetingResponse(soapResponse);
    }
}
