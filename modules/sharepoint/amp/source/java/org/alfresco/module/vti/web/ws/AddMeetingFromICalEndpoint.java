
package org.alfresco.module.vti.web.ws;

import org.alfresco.module.vti.handler.MeetingServiceHandler;
import org.alfresco.module.vti.metadata.model.MeetingBean;
import org.dom4j.Element;

/**
 * Class for handling AddMeetingFromICal soap method
 * 
 * @author PavelYur
 */
public class AddMeetingFromICalEndpoint extends AbstractMeetingFromICalEndpoint
{
    public AddMeetingFromICalEndpoint(MeetingServiceHandler handler)
    {
        super(handler);
    }

    /**
     * Add new meeting to Meeting Workspace
     * 
     * @param soapRequest Vti soap request ({@link VtiSoapRequest})
     * @param soapResponse Vti soap response ({@link VtiSoapResponse})
     */
    @Override
    protected void executeMeetingAction(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse, String siteName,
            MeetingBean meetingBean, int sequence, int recurrenceId, boolean ignoreAttendees, boolean cancelMeeting) throws Exception
    {
        // Have the meeting added
        handler.addMeetingFromICal(siteName, meetingBean); 

        // Report what we did
        Element root = soapResponse.getDocument().addElement("AddMeetingFromICalResponse", namespace);
        Element result = root.addElement("AddMeetingFromICalResult");
        Element meetingICal = result.addElement("AddMeetingFromICal");
        meetingICal.addAttribute("Url", getHost(soapRequest) + soapRequest.getAlfrescoContextName() + "/" + siteName + "?calendar=calendar")
                   .addAttribute("HostTitle", meetingBean.getSubject()).addAttribute("UniquePermissions", "true")
                   .addAttribute("MeetingCount", "1").addAttribute("AnonymousAccess", "false")
                   .addAttribute("AllowAuthenticatedUsers", "false");
        meetingICal.addElement("AttendeeUpdateStatus").addAttribute("Code", "0").addAttribute("Detail", "").addAttribute("ManageUserPage", "");

        soapResponse.setContentType("text/xml");
    }
}