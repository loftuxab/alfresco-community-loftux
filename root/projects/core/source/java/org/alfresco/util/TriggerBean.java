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

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.springframework.scheduling.quartz.SimpleTriggerBean;

/**
 * Extended trigger bean to allow the setting of first firing
 * using hours past midnight and minutes past the hour.
 * <p>
 * The default start time for the trigger will be next midnight
 * after the bean is initialised.  The <code>delay</code> property
 * can still be used as an offset from this time.
 * 
 * @author Derek Hulley
 */
public class TriggerBean extends SimpleTriggerBean
{
    private static final long serialVersionUID = 6526305743899044951L;

    private int hour = 0;
    private int minute = 0;

    /**
     * @param hour the hour in the day: 0 - 23.
     */
    public void setHour(int hour)
    {
        this.hour = hour;
    }
    
    /**
     * @param minute the minute in the hour: 0 - 59.
     */
    public void setMinute(int minute)
    {
        this.minute = minute;
    }
    
    @Override
    public void afterPropertiesSet() throws ParseException
    {
        // set the start time
        Calendar calendar = new GregorianCalendar();
        calendar.setLenient(true);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
        int nowMinute = calendar.get(Calendar.MINUTE);
        
        // advance the day if the hour and minute are behind the current
        if (hour < nowHour || (hour == nowHour && minute <= nowMinute))
        {
            calendar.set(Calendar.DAY_OF_MONTH, day + 1);
        }
        // set the hour and minute
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        // set the bean start time
        setStartTime(calendar.getTime());
        
        // now do the default start
        super.afterPropertiesSet();
    }
}
