
package org.alfresco.module.vti.web.ws;

import org.alfresco.module.vti.handler.MeetingServiceHandler;
import org.alfresco.module.vti.handler.SiteTypeException;
import org.alfresco.module.vti.metadata.model.MeetingBean;
import org.dom4j.Element;

/**
 * Class for handling AddMeeting soap method
 * 
 * @author Nick Burch
 */
public class AddMeetingEndpoint extends AbstractMeetingEndpoint
{
    public AddMeetingEndpoint(MeetingServiceHandler handler)
    {
        super(handler);
    }
    
    @Override
    protected void executeMeetingAction(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse, String siteName,
            MeetingBean meetingBean, int sequence, int recurrenceId, boolean ignoreAttendees, boolean cancelMeeting) throws Exception
    {
        // Perform the addition of the meeting
        try
        {
            handler.addMeeting(siteName, meetingBean);
        }
        catch (SiteTypeException ste)
        {
            throw new VtiSoapException(ste.getMsgId(), 6l);
        }
        catch (Exception e)
        {
            throw new VtiSoapException(e.getMessage(), 7l, e);
        }
        
        // Build the response
        Element e = buildMeetingResponse(soapResponse);
        Element result = e.addElement("AddMeetingResult");
        Element meeting = result.addElement("AddMeeting");
        meeting.addAttribute("Url", getHost(soapRequest) + soapRequest.getAlfrescoContextName() + "/" + siteName + "?calendar=calendar")
               .addAttribute("HostTitle", meetingBean.getSubject()).addAttribute("UniquePermissions", "true")
               .addAttribute("MeetingCount", "1").addAttribute("AnonymousAccess", "false")
               .addAttribute("AllowAuthenticatedUsers", "false");
    }
}