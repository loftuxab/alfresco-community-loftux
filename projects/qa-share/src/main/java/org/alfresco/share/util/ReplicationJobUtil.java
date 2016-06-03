/*
 * #%L
 * qa-share
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.share.util;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.admin.AdminConsolePage;
import org.alfresco.po.share.adminconsole.replicationjobs.DeleteJobPage;
import org.alfresco.po.share.adminconsole.replicationjobs.NewReplicationJobPage;
import org.alfresco.po.share.adminconsole.replicationjobs.RepeatEveryValue;
import org.alfresco.po.share.adminconsole.replicationjobs.ReplicationJobsPage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.document.ConfirmDeletePage;
import org.alfresco.po.share.workflow.CompanyHome;
import org.alfresco.webdrone.WebDrone;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Holds Util methods for replication jobs' handling
 *
 * @author Marina.Nenadovets
 */
public class ReplicationJobUtil extends AbstractUtils
{


    /**
     * Method to create new replication job
     *
     * @param driver
     * @param name
     * @param desc
     * @param dueDate
     * @param time
     * @param repeatEvery
     * @param value
     * @param isEnabled
     * @return
     */
    public static ReplicationJobsPage createReplicationJob(WebDrone driver, String name, String desc, CompanyHome companyHomePayLoad,
        String targetName, String dueDate, String time, String repeatEvery, RepeatEveryValue value, boolean isEnabled)
    {
        checkNotNull(name, new ShareException("Name must be specified!"));
        SharePage page = ShareUser.getSharePage(driver).render();
        AdminConsolePage adminConsolePage = page.getNav().getAdminConsolePage().render();
        ReplicationJobsPage replicationJobsPage = adminConsolePage.navigateToReplicationJobs().render();
        NewReplicationJobPage newReplicationJobPage = replicationJobsPage.clickNewJob().render();
        newReplicationJobPage.setName(name);
        if (!desc.isEmpty())
            newReplicationJobPage.setDescription(desc);
        if (companyHomePayLoad != null)
            newReplicationJobPage.selectPayLoad(driver, companyHomePayLoad);
        if (targetName != null)
        {
            newReplicationJobPage.selectTransferTarget(driver, targetName);
        }
        if (!(dueDate == null) && !(time == null))
        {
            newReplicationJobPage.setScheduling();
            newReplicationJobPage.setDueDate(dueDate);
            newReplicationJobPage.setTime(time);
            newReplicationJobPage.setRepeatEveryField(repeatEvery);
            newReplicationJobPage.selectIntervalPeriod(value);
        }
        if (isEnabled)
        {
            newReplicationJobPage.selectTransferEnabled();
        }
        newReplicationJobPage.clickSave();
        return driver.getCurrentPage().render();
    }

    public static ReplicationJobsPage editReplicationJob(WebDrone driver, String oldName, String newName, String newDesc, String [] oldSourceItems, CompanyHome companyHomePayload,
        String targetName, String newDueDate, String newTime, String repeatEvery, RepeatEveryValue newValue, boolean isEnabled)
    {
        boolean isEnabledSet;
        SharePage page = ShareUser.getSharePage(driver).render();
        AdminConsolePage adminConsolePage = page.getNav().getAdminConsolePage().render();
        ReplicationJobsPage replicationJobsPage = adminConsolePage.navigateToReplicationJobs().render();
        NewReplicationJobPage newReplicationJobPage = replicationJobsPage.getJobDetails(oldName).clickEditButton().render();
        if(!(newName == null))
        {
            newReplicationJobPage.setName(newName);
        }
        if(!(newDesc == null))
        {
            newReplicationJobPage.setDescription(newDesc);
        }
        if(!(oldSourceItems == null))
        {
            newReplicationJobPage.deleteSourceItems(driver,oldSourceItems );
        }
        if(companyHomePayload != null)
        {
            newReplicationJobPage.selectPayLoad(driver, companyHomePayload );
        }
        if(targetName != null)
        {
            newReplicationJobPage.deleteTransferTarget(driver);
            newReplicationJobPage.selectTransferTarget(driver, targetName);
        }
        if(!(newDueDate == null) && !(newTime == null))
        {
            newReplicationJobPage.setScheduling();
            newReplicationJobPage.setDueDate(newDueDate);
            newReplicationJobPage.setTime(newTime);
            newReplicationJobPage.setRepeatEveryField(repeatEvery);
            newReplicationJobPage.selectIntervalPeriod(newValue);
        }
        isEnabledSet = newReplicationJobPage.isEnabledSet();
        if ((isEnabled && isEnabledSet) || (!isEnabled && !isEnabledSet))
        {
            newReplicationJobPage.clickSave();
        }
        else
        {
            newReplicationJobPage.setEnabled();
            newReplicationJobPage.clickSave();
        }
        return driver.getCurrentPage().render();
    }

    public static ReplicationJobsPage deleteReplicationJob(WebDrone driver, String jobName)
    {
        SharePage page = ShareUser.getSharePage(driver).render();
        AdminConsolePage adminConsolePage = page.getNav().getAdminConsolePage().render();
        ReplicationJobsPage replicationJobsPage = adminConsolePage.navigateToReplicationJobs().render();
        DeleteJobPage deleteJobPage = replicationJobsPage.getJobDetails(jobName).clickDeleteButton().render();
        deleteJobPage.selectAction(ConfirmDeletePage.Action.Delete);
        return driver.getCurrentPage().render();
    }


}
