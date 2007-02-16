/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.util;

import java.util.Date;

import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

public class TriggerBean extends AbstractTriggerBean
{
    public long startDelay = 0;

    public long repeatInterval = 0;

    public int repeatCount = SimpleTrigger.REPEAT_INDEFINITELY;

    public TriggerBean()
    {
        super();
    }

    public int getRepeatCount()
    {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount)
    {
        this.repeatCount = repeatCount;
    }

    public long getRepeatInterval()
    {
        return repeatInterval;
    }

    public void setRepeatInterval(long repeatInterval)
    {
        this.repeatInterval = repeatInterval;
    }

    public void setRepeatIntervalMinutes(long repeatIntervalMinutes)
    {
        this.repeatInterval = repeatIntervalMinutes * 60L * 1000L;
    }

    public long getStartDelay()
    {
        return startDelay;
    }

    public void setStartDelay(long startDelay)
    {
        this.startDelay = startDelay;
    }

    public void setStartDelayMinutes(long startDelayMinutes)
    {
        this.startDelay = startDelayMinutes * 60L * 1000L;
    }

    @Override
    public Trigger getTrigger() throws Exception
    {
        SimpleTrigger trigger = new SimpleTrigger(getBeanName(), Scheduler.DEFAULT_GROUP);
        trigger.setStartTime(new Date(System.currentTimeMillis() + this.startDelay));
        trigger.setRepeatCount(repeatCount);
        trigger.setRepeatInterval(repeatInterval);
        return trigger;
    }
}
