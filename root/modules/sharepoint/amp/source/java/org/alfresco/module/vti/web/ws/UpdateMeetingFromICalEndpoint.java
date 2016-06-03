
package org.alfresco.module.vti.web.ws;

import org.alfresco.module.vti.handler.MeetingServiceHandler;
import org.alfresco.module.vti.metadata.model.MeetingBean;
import org.dom4j.Element;

/**
 * Class for handling UpdateMeetingFromICal soap method
 * 
 * @author PavelYur
 */
public class UpdateMeetingFromICalEndpoint extends AbstractMeetingFromICalEndpoint
{
    public UpdateMeetingFromICalEndpoint(MeetingServiceHandler handler)
    {
        super(handler);
    }

    /**
     * Update meeting in Meeting Workspace
     * 
     * @param soapRequest Vti soap request ({@link VtiSoapRequest})
     * @param soapResponse Vti soap response ({@link VtiSoapResponse})
     */
    @Override
    protected void executeMeetingAction(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse, String siteName,
            MeetingBean meetingBean, int sequence, int recurrenceId, boolean ignoreAttendees, boolean cancelMeeting) throws Exception
    {
        // Have the meeting updated
        handler.updateMeetingFromICal(siteName, meetingBean, ignoreAttendees);

        // creating soap response
        Element updateStatus = soapResponse.getDocument().addElement("UpdateMeetingFromICalResponse", namespace).addElement("UpdateMeetingFromICalResult").addElement(
                "UpdateMeetingFromICal").addElement("AttendeeUpdateStatus");
        updateStatus.addAttribute("Code", "0");
        updateStatus.addAttribute("Detail", "");
        updateStatus.addAttribute("ManageUserPage", "");

        soapResponse.setContentType("text/xml");
    }
}
