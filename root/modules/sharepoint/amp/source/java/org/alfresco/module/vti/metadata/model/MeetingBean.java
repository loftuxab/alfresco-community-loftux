
package org.alfresco.module.vti.metadata.model;

import java.util.Date;
import java.util.List;

import org.alfresco.service.cmr.calendar.CalendarEntry;
import org.alfresco.service.cmr.calendar.CalendarEntryDTO;

/**
 * The meeting bean, which is an extension of the regular
 *  {@link CalendarEntry} object with extra information on
 *  the organiser and attendees
 */
public class MeetingBean extends CalendarEntryDTO
{
    private static final long serialVersionUID = -367359427553658901L;
    private String organizer;
    private List<String> attendees;
    private Date ReccurenceIdDate;
    
    public MeetingBean()
    {
        super();
        
        // We're always Outlook based
        setOutlook(true);
    }

    /**
     * Returns the Subject (title)
     */
    public String getSubject()
    {
        return getTitle();
    }

    /**
     * Sets the Subject (title)
     */
    public void setSubject(String subject)
    {
        setTitle(subject);
    }
    
    public Date getStartDate()
    {
        return getStart(); 
    }
    public Date getEndDate()
    {
        return getEnd();
    }

    public String getOrganizer()
    {
        return organizer;
    }

    public void setOrganizer(String organizer)
    {
        this.organizer = organizer;
    }

    public List<String> getAttendees()
    {
        return attendees;
    }

    public void setAttendees(List<String> attendees)
    {
        this.attendees = attendees;
    }

    public String getId()
    {
        return getOutlookUID();
    }

    public void setId(String id)
    {
        setOutlookUID(id);
    }

    public Date getReccurenceIdDate()
    {
        return ReccurenceIdDate;
    }

    public void setReccurenceIdDate(Date reccurenceIdDate)
    {
        ReccurenceIdDate = reccurenceIdDate;
    }
}