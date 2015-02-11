package org.alfresco.po.share.steps;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.AddUserGroupPage;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.GroupsPage;
import org.alfresco.po.share.NewGroupPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.UserSearchPage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AdminToolsTest extends AbstractTest
{
    private DashBoardPage dashBoard;
    private String groupName = "Add_Group";
    private String ADD_BUTTON = "td[class*='yui-dt-col-actions'] button";
    private String user = "user" + System.currentTimeMillis() + "@test.com";
    
    @BeforeClass(groups = "Enterprise-only")
    public void setup() throws Exception
    {
        dashBoard = loginAs("admin", "admin");
        UserSearchPage userPage = dashBoard.getNav().getUsersPage().render();
        NewUserPage newPage = userPage.selectNewUser().render();
        newPage.createEnterpriseUser(user, user, user, user, user);

    }

    @Test(groups = "Enterprise-only")
    public void testsnavigateToGroup() throws Exception
    {
        GroupsPage groupsPage = AdminTools.navigateToGroup(drone);
        groupsPage = groupsPage.clickBrowse().render();
        NewGroupPage newGroupPage = groupsPage.navigateToNewGroupPage().render();
        newGroupPage.createGroup(groupName, groupName, NewGroupPage.ActionButton.CREATE_GROUP).render();
        groupsPage = drone.getCurrentPage().render();
        groupsPage.selectGroup(groupName);
        AddUserGroupPage addUser = groupsPage.selectAddUser();
        addUser.searchUser(user).render(3000);
        Assert.assertTrue(drone.isElementDisplayed(By.cssSelector(ADD_BUTTON)));
        addUser.clickClose();

    }
}
