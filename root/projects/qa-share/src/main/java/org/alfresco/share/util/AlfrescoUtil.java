
package org.alfresco.share.util;

import org.alfresco.po.alfresco.LoginAlfrescoPage;
import org.alfresco.po.alfresco.MyAlfrescoPage;
import org.alfresco.po.alfresco.RepositoryAdminConsolePage;
import org.alfresco.po.alfresco.TenantAdminConsolePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.systemsummary.AdminConsoleLink;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.po.share.systemsummary.TenantConsole;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.webdrone.WebDrone;

/**
 * Created by ivan.kornilov on 23.04.2014.
 */

public class AlfrescoUtil extends AbstractUtils
{
    private static final String ALFRESCO_LOGIN_PAGE = "/alfresco/faces/jsp/login.jsp";
    private static final String TENANT_ADMIN_CONSOLE_PAGE = "/alfresco/faces/jsp/admin/tenantadmin-console.jsp";
    private static final String REPO_ADMIN_CONSOLE_PAGE = "/alfresco/faces/jsp/admin/repoadmin-console.jsp";


    /**
     * This method provides create tenant with random user and password.
     *
     * @param drone
     * @param tenantName
     * @param tenantPassword
     * @return String[] { tenantName, tenantPassword }
     */

    public static String[] createTenant(WebDrone drone, String tenantName, String tenantPassword)
    {
        if (tenantName == null || tenantName.isEmpty())
        {
            tenantName = String.valueOf(RandomUtil.getRandomString(3)+".local");
        }

        if (tenantPassword == null || tenantPassword.isEmpty())
        {
            tenantPassword = String.valueOf(RandomUtil.getRandomString(5));
        }

        if (alfrescoVersion.getVersion() >= 5.0)

        {
            SystemSummaryPage sysSummaryPage = ShareUtil.navigateToSystemSummary(drone, shareUrl, ADMIN_USERNAME, ADMIN_PASSWORD).render();
            TenantConsole tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
            tenantConsole.createTenant (tenantName, tenantPassword);
            tenantConsole.render();
        }

        else
        {

            TenantAdminConsolePage adminConsolePage = new TenantAdminConsolePage(drone);
            adminConsolePage.createTenant(tenantName, tenantPassword);
            adminConsolePage.render();
        }

        return new String[] { tenantName, tenantPassword };
    }

    /**
     * This method provides login to Tenant Admin Console.
     *
     * @param drone
     * @param shareUrl
     * @param username
     * @param password
     * @return TenantAdminConsolePage
     */

    public static TenantAdminConsolePage tenantAdminLogin(WebDrone drone, String shareUrl, String username, String password)
    {
       try
       {
           //Navigate to Alfresco;
           drone.navigateTo(PageUtils.getProtocol(shareUrl) + PageUtils.getAddress(shareUrl) + ALFRESCO_LOGIN_PAGE);
           ShareUser.deleteSiteCookies(drone,shareUrl);
           LoginAlfrescoPage loginPage = new LoginAlfrescoPage(drone);
           loginPage.render();

           //Login to Alfresco;
           MyAlfrescoPage myalfrescoPage = loginPage.login(username, password);
           boolean success = myalfrescoPage.userIsLoggedIn(username);
           if (!success)
           {
               throw new RuntimeException("Method isLoggedIn return false");
           }
           //Navigate to Tenant Admin Console;
           drone.navigateTo(PageUtils.getProtocol(shareUrl) + PageUtils.getAddress(shareUrl) + TENANT_ADMIN_CONSOLE_PAGE);
           return new TenantAdminConsolePage(drone);
       }
       catch (UnsupportedOperationException uso)
       {
           throw new UnsupportedOperationException("Can not navigate to Web Client Config Page");
       }
    }

    public static RepositoryAdminConsolePage repoAdminConsoleLogin (WebDrone drone, String shareUrl, String username, String password)
    {
        try
        {
            //Navigate to Alfresco;
            drone.navigateTo(PageUtils.getProtocol(shareUrl) + PageUtils.getAddress(shareUrl) + ALFRESCO_LOGIN_PAGE);
            ShareUser.deleteSiteCookies(drone,shareUrl);
            LoginAlfrescoPage loginPage = new LoginAlfrescoPage(drone);
            loginPage.render();

            //Login to Alfresco;
            MyAlfrescoPage myalfrescoPage = loginPage.login(username, password);
            boolean success = myalfrescoPage.userIsLoggedIn(username);
            if (!success)
            {
                throw new RuntimeException("Method isLoggedIn return false");
            }

            //Navigate to Tenant Admin Console;
            drone.navigateTo(PageUtils.getProtocol(shareUrl) + PageUtils.getAddress(shareUrl) + REPO_ADMIN_CONSOLE_PAGE);
            return new RepositoryAdminConsolePage(drone);
        }

        catch (UnsupportedOperationException uso)
        {
            throw new UnsupportedOperationException("Can not navigate to Web Client Config Page");
        }

    }

}
