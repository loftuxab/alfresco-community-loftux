package org.alfresco.po.share.steps;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.GroupsPage;
import org.alfresco.webdrone.WebDrone;

public class AdminTools
{
    /**
     * @param driver WebDriver Instance
     *            Navigate to Groups page
     * @return Groups page
     */

    public static GroupsPage navigateToGroup(WebDrone driver)
    {
        DashBoardPage dashBoard = ShareUser.openUserDashboard(driver);
        GroupsPage page = dashBoard.getNav().getGroupsPage();
        return page;
    }
}
