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

    /**
     * @param driver WebDriver Instance
     *            This method is called when the user is on groups page
     *            Click on browse button in Groups page
     * @return Groups page
     */
    public GroupsPage browseGroups(WebDrone driver)
    {
        GroupsPage page = null;

        if (getSharePage(driver) instanceof GroupsPage)
        {
            // Right Page
        }
        else
        {
            page = navigateToGroup(driver);
        }

        GroupsPage groupsPage = page.clickBrowse().render();
        return groupsPage;
    }
}
