/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


/**
 * Formatting support for ISO 8601 dates
 * 
 * sYYYY-MM-DDThh:mm:ss.sssTZD
 * 
 * where:
 *   sYYYY  Four-digit year with optional leading positive (‘+’) or negative (‘-’) sign. 
 *          A negative sign indicates a year BCE. The absence of a sign or the presence of a 
 *          positive sign indicates a year CE (for example, -0055 would indicate the year 55 BCE, 
 *          while +1969 and 1969 indicate the year 1969 CE).
 *   MM     Two-digit month (01 = January, etc.)
 *   DD     Two-digit day of month (01 through 31)
 *   hh     Two digits of hour (00 through 23)
 *   mm     Two digits of minute (00 through 59)
 *   ss.sss Seconds, to three decimal places (00.000 through 59.999)
 *   TZD    Time zone designator (either Z for Zulu, i.e. UTC, or +hh:mm or -hh:mm, i.e. an offset from UTC)
 */
public class ISO8601DateFormat
{
    
    /**
     * Format date into ISO format
     * 
     * @param isoDate  the date to format
     * @return  the ISO formatted string
     */
    public static String format(Date isoDate)
    {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(isoDate);
        
        StringBuffer formatted = new StringBuffer();
        padInt(formatted, calendar.get(Calendar.YEAR), 4);
        formatted.append('-');
        padInt(formatted, calendar.get(Calendar.MONTH) + 1, 2);
        formatted.append('-');
        padInt(formatted, calendar.get(Calendar.DAY_OF_MONTH), 2);
        formatted.append('T');
        padInt(formatted, calendar.get(Calendar.HOUR_OF_DAY), 2);
        formatted.append(':');
        padInt(formatted, calendar.get(Calendar.MINUTE), 2);
        formatted.append(':');
        padInt(formatted, calendar.get(Calendar.SECOND), 2);
        formatted.append('.');
        padInt(formatted, calendar.get(Calendar.MILLISECOND), 3);

        TimeZone tz = calendar.getTimeZone();
        int offset = tz.getOffset(calendar.getTimeInMillis());
        if (offset != 0)
        {
            int hours = Math.abs((offset / (60 * 1000)) / 60);
            int minutes = Math.abs((offset / (60 * 1000)) % 60);
            formatted.append(offset < 0 ? '-' : '+');
            padInt(formatted, hours, 2);
            formatted.append(':');
            padInt(formatted, minutes, 2);
        } 
        else
        {
            formatted.append('Z');
        }
        
        return formatted.toString();
    }
    
    
    /**
     * Parse date from ISO formatted string
     * 
     * @param isoDate  ISO string to parse
     * @return  the date
     */
    public static Date parse(String isoDate)
    {
        Date parsed = null;
        
        try
        {
            int offset = 0;
        
            // extract year
            int year = Integer.parseInt(isoDate.substring(offset, offset += 4));
            if (isoDate.charAt(offset) != '-')
            {
                throw new IndexOutOfBoundsException("Expected - character but found " + isoDate.charAt(offset));
            }
            
            // extract month
            int month = Integer.parseInt(isoDate.substring(offset += 1, offset += 2));
            if (isoDate.charAt(offset) != '-')
            {
                throw new IndexOutOfBoundsException("Expected - character but found " + isoDate.charAt(offset));
            }

            // extract day
            int day = Integer.parseInt(isoDate.substring(offset += 1, offset += 2));
            if (isoDate.charAt(offset) != 'T')
            {
                throw new IndexOutOfBoundsException("Expected T character but found " + isoDate.charAt(offset));
            }

            // extract hours, minutes, seconds and milliseconds
            int hour = Integer.parseInt(isoDate.substring(offset += 1, offset += 2));
            if (isoDate.charAt(offset) != ':')
            {
                throw new IndexOutOfBoundsException("Expected T character but found " + isoDate.charAt(offset));
            }
            int minutes = Integer.parseInt(isoDate.substring(offset += 1, offset += 2));
            if (isoDate.charAt(offset) != ':')
            {
                throw new IndexOutOfBoundsException("Expected : character but found " + isoDate.charAt(offset));
            }
            int seconds = Integer.parseInt(isoDate.substring(offset += 1 , offset += 2));
            if (isoDate.charAt(offset) != '.')
            {
                throw new IndexOutOfBoundsException("Expected . character but found " + isoDate.charAt(offset));
            }
            int milliseconds = Integer.parseInt(isoDate.substring(offset += 1, offset += 3));

            // extract timezone
            String timezoneId;
            char timezoneIndicator = isoDate.charAt(offset);
            if (timezoneIndicator == '+' || timezoneIndicator == '-')
            {
                timezoneId = "GMT" + isoDate.substring(offset);
            }
            else if (timezoneIndicator == 'Z')
            {
                timezoneId = "GMT";
            }
            else
            {
                throw new IndexOutOfBoundsException("Invalid time zone indicator " + timezoneIndicator);
            }
            TimeZone timezone = TimeZone.getTimeZone(timezoneId);
            if (!timezone.getID().equals(timezoneId))
            {
                throw new IndexOutOfBoundsException();
            }

            // initialize Calendar object
            Calendar calendar = Calendar.getInstance(timezone);
            calendar.setLenient(false);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minutes);
            calendar.set(Calendar.SECOND, seconds);
            calendar.set(Calendar.MILLISECOND, milliseconds);

            // extract the date
            parsed = calendar.getTime();
        }
        catch(IndexOutOfBoundsException e)
        {
        }
        catch(NumberFormatException e)
        {
        }
        catch(IllegalArgumentException e)
        {
        }
        
        return parsed;
    }
    
    /**
     * Helper to zero pad a number to specified length 
     */
    private static void padInt(StringBuffer buffer, int value, int length)
    {
        String strValue = Integer.toString(value);
        for (int i = length - strValue.length(); i > 0; i--)
        {
            buffer.append('0');
        }
        buffer.append(strValue);
    }
    
}
