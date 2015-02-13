package org.alfreso.po.share.steps;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.GroupsPage;
import org.alfresco.webdrone.WebDrone;

public class AdminActions extends DashBoardActions
{
    /**
     * @param driver WebDriver Instance
     *            Navigate to Groups page
     * @return Groups page
     */

    public GroupsPage navigateToGroup(WebDrone driver)
    {
        DashBoardPage dashBoard = openUserDashboard(driver);
        GroupsPage page = dashBoard.getNav().getGroupsPage();
        return page;
    }
}
