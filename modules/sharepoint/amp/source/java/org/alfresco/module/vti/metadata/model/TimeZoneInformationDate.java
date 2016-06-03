
package org.alfresco.module.vti.metadata.model;

/**
 * @author EugeneZh
 */
public class TimeZoneInformationDate
{
    private int year;
    private int month;
    private int dayOfWeek;
    private int day;
    private int hour;
    private int minute;
    private int second;
    private int milliseconds;

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public int getMonth()
    {
        return month;
    }

    public void setMonth(int month)
    {
        this.month = month;
    }

    public int getDayOfWeek()
    {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek)
    {
        this.dayOfWeek = dayOfWeek;
    }

    public int getDay()
    {
        return day;
    }

    public void setDay(int day)
    {
        this.day = day;
    }

    public int getHour()
    {
        return hour;
    }

    public void setHour(int hour)
    {
        this.hour = hour;
    }

    public int getMinute()
    {
        return minute;
    }

    public void setMinute(int minute)
    {
        this.minute = minute;
    }

    public int getSecond()
    {
        return second;
    }

    public void setSecond(int second)
    {
        this.second = second;
    }

    public int getMilliseconds()
    {
        return milliseconds;
    }

    public void setMilliseconds(int milliseconds)
    {
        this.milliseconds = milliseconds;
    }
}