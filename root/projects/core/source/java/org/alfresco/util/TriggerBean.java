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

    public long getStartDelay()
    {
        return startDelay;
    }

    public void setStartDelay(long startDelay)
    {
        this.startDelay = startDelay;
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
