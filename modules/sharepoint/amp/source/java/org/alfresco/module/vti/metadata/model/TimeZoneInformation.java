
package org.alfresco.module.vti.metadata.model;

import org.alfresco.service.cmr.calendar.CalendarTimezoneHelper;

/**
 * Information on a TimeZone to be used by an Event or a Meeting Workspace.
 * This is simple and POJO based, to facilitate easy creation from
 *  Web Service requests.
 * When working with iCal feeds, you would more usually work with 
 *  {@link CalendarTimezoneHelper}, which is able to generate
 *  Java TimeZone objects.
 * TODO When fully implemented on the WS side, offer to create
 *  Java TimeZone objects too
 */
public class TimeZoneInformation
{
    private String id;
    
    private int bias;

    private TimeZoneInformationDate standardDate;

    private int standardBias;

    private TimeZoneInformationDate daylightDate;

    private int daylightBias;
    
    /**
     * @return The TimeZone ID, eg "Canberra, Melbourne, Sydney"
     */
    public String getID()
    {
        return id;
    }
    
    public void setID(String id)
    {
        this.id = id;
    }

    public int getBias()
    {
        return bias;
    }

    public void setBias(int bias)
    {
        this.bias = bias;
    }

    public TimeZoneInformationDate getStandardDate()
    {
        return standardDate;
    }

    public void setStandardDate(TimeZoneInformationDate standardDate)
    {
        this.standardDate = standardDate;
    }

    public int getStandardBias()
    {
        return standardBias;
    }

    public void setStandardBias(int standardBias)
    {
        this.standardBias = standardBias;
    }

    public TimeZoneInformationDate getDaylightDate()
    {
        return daylightDate;
    }

    public void setDaylightDate(TimeZoneInformationDate daylightDate)
    {
        this.daylightDate = daylightDate;
    }

    public int getDaylightBias()
    {
        return daylightBias;
    }

    public void setDaylightBias(int daylightBias)
    {
        this.daylightBias = daylightBias;
    }
}