function addCloudInvitationService()
{
   model.jsonModel.services.push("cloud/services/InvitationService");
}

function removeCustomizeSite()
{
   var popup = widgetUtils.findObject(model.jsonModel.widgets, "id", "HEADER_SITE_CONFIGURATION_DROPDOWN");
   if (popup && popup.config && popup.config.widgets)
   {
      var widgets = popup.config.widgets;
      for (var i = 0, il = widgets.length; i < il; i++)
      {
         if (widgets[i].config && widgets[i].config.id == "HEADER_CUSTOMIZE_SITE")
         {
            widgets.splice(i, 1);
            return;
         }
      }
   }
}

function addNetworkAdminTools()
{
   if (user.properties["isNetworkAdmin"])
   {
      var popup = widgetUtils.findObject(model.jsonModel.widgets, "id", "HEADER_USER_MENU");
      if (popup && popup.config && popup.config.widgets)
      {
         var widgets = popup.config.widgets;
         for (var i=0; i<widgets.length; i++)
         {
            if (widgets[i].id === "HEADER_USER_MENU_LOGOUT")
            {
               widgets.splice(i, 0, {
                  id: "CLOUD__NetworkAdminToolsLink",
                  name: "alfresco/header/AlfMenuItem",
                  config: {
                     id: "CLOUD__NetworkAdminToolsLink",
                     label: "header.menu.networkAdmin.label",
                     iconClass: "alf-admin-icon",
                     targetUrl: "console/cloud-console/account-summary"
                  }
               });
               break;
            }
         }
      }
   }
}

function addInvitePeople()
{
   var popup = widgetUtils.findObject(model.jsonModel.widgets, "id", "HEADER_USER_MENU_BAR");
   if (popup && popup.config && popup.config.widgets)
   {
      var widgets = popup.config.widgets;
      widgets.splice(0, 0,
      {
         id: "CLOUD__InvitePeople",
         name: "alfresco/menus/AlfMenuBarItem",
         config: {
            id: "CLOUD__InvitePeople",
            label: "header.invitePeople.label",
            publishTopic: "CLOUD_INVITE_PEOPLE",
            publishPayload: {}
         }
      });
   }
}

function addSiteInvite()
{
   var inviteLink = widgetUtils.findObject(model.jsonModel.widgets, "id", "HEADER_SITE_INVITE");
   if (inviteLink)
   {
      inviteLink.config = {
         label: "",
         iconClass: "alf-user-icon",
         targetUrl: null,
         publishTopic: "CLOUD_SITE_INVITE",
         publishPayload: {
            site: page.url.templateArgs.site
         }
      };
   }
}

function addNetworks()
{
   var homeTenant = user.properties.homeTenant,
      otherTenants = user.properties.secondaryTenants;

   // The URLContext includes the authenticated users home tenant, since we wish to construct
   // URLs that map to different tenants we need to remove the tenant from the URLContext to
   // create an application context. This will then be appended with each tenant as required.
   var currentTenantContext = url.context,
      tenantIndex = currentTenantContext.lastIndexOf('/'),
      applicationContext = currentTenantContext.substring(0, tenantIndex),
      currentNetwork = currentTenantContext.substring(tenantIndex +1);

   var networks = {
      id: "CLOUD__Networks",
      name: "alfresco/header/AlfMenuBarPopup",
      config: {
         id: "CLOUD__Networks",
         label: currentNetwork,
         widgets: []
      }
   };

   var popup = widgetUtils.findObject(model.jsonModel.widgets, "id", "HEADER_USER_MENU_BAR");
   if (popup && popup.config && popup.config.widgets)
   {
      popup.config.widgets.splice(0, 0, networks);
   }

   if (homeTenant && homeTenant != "" && homeTenant != "null")
   {
      var homeNetworkUrl = applicationContext + "/" + encodeURIComponent(homeTenant) + "/page/user/" +
            encodeURIComponent(user.name) + "/dashboard?refreshMetadata=true";
      var homeGroup = {
         id: "CLOUD__Networks_HomeGroup",
         name: "alfresco/menus/AlfMenuGroup",
         config:
         {
            label: msg.get("label.home-networks"),
            widgets: [
               {
                  id: "CLOUD__Networks_HomeGroup_HomeNetwork",
                  name: "alfresco/header/AlfMenuItem",
                  config:
                  {
                     id: "CLOUD__Networks_HomeGroup_HomeNetwork",
                     label: homeTenant,
                     targetUrl: homeNetworkUrl,
                     targetUrlType: "FULL_PATH"
                  }
               }
            ]
         }
      };
      networks.config.widgets.push(homeGroup);
   }

   if (otherTenants && otherTenants.length > 0)
   {
      var otherGroup = {
         id: "CLOUD__Networks_HomeGroup",
         name: "alfresco/menus/AlfMenuGroup",
         config:
         {
            id: "CLOUD__Networks_HomeGroup",
            label: msg.get("label.invited-networks"),
            widgets: []
         }
      };
      networks.config.widgets.push(otherGroup);

      for (var i = 0; i < otherTenants.length; i++)
      {
         var otherUrl = applicationContext + "/" + encodeURIComponent(otherTenants[i]) + "/page/user/" +
               encodeURIComponent(user.name) + "/dashboard?refreshMetadata=true";
         otherGroup.config.widgets.push({
            id: "CLOUD__Networks_OtherGroup_OtherNetwork",
            name: "alfresco/header/AlfMenuItem",
            config:
            {
               id: "CLOUD__Networks_OtherGroup_OtherNetwork_" + i,
               label: otherTenants[i],
               targetUrl: otherUrl,
               targetUrlType: "FULL_PATH"
            }
         });
      }
   }
}

function addCloudSitesMenu()
{
   var sitesMenu = widgetUtils.findObject(model.jsonModel.widgets, "id", "HEADER_SITES_MENU");
   if (sitesMenu)
   {
      // Make sure we use cloud specific site menu
      sitesMenu.name = "cloud/header/CloudSitesMenu";

      // See if we shall remove the create site menu item or not
      var currentTenantContext = url.context,
         tenantIndex = currentTenantContext.lastIndexOf('/'),
         currentTenant = currentTenantContext.substring(tenantIndex +1),
         homeTenant = user.properties.homeTenant;
      sitesMenu.config = sitesMenu.config || {};
      sitesMenu.config.removeCreateSiteMenuItem = currentTenant != homeTenant;
   }
}

function removeAdvancedSearchMenu()
{
   var searchBox = widgetUtils.findObject(model.jsonModel.widgets, "id", "HEADER_SEARCH");
   if (searchBox)
   {
      searchBox.config.advancedSearch = false;
   }
}

function forceAllSitesSearchMenu()
{
   var searchBox = widgetUtils.findObject(model.jsonModel.widgets, "id", "HEADER_SEARCH");
   if (searchBox)
   {
      searchBox.config.allsites = true;
   }
}

function removeMyFilesSharedFiles()
{
   var appMenu = widgetUtils.findObject(model.jsonModel.widgets, "id", "HEADER_APP_MENU_BAR");
   if (appMenu)
   {
      if (appMenu && appMenu.config && appMenu.config.widgets)
      {
         var widgets = appMenu.config.widgets;
         for (var i=0; i<widgets.length; i++)
         {
            if (widgets[i].id === "HEADER_MY_FILES" || widgets[i].id === "HEADER_SHARED_FILES")
            {
               widgets.splice(i--, 1);
            }
         }
      }
   }
}

function removeMyTasksMenu()
{
   var appMenu = widgetUtils.findObject(model.jsonModel.widgets, "id", "HEADER_APP_MENU_BAR");
   if (appMenu)
   {
      if (appMenu && appMenu.config && appMenu.config.widgets)
      {
         var widgets = appMenu.config.widgets;
         for (var i=0; i<widgets.length; i++)
         {
            if (widgets[i].id === "HEADER_TASKS")
            {
               widgets.splice(i--, 1);
            }
         }
      }
   }
}

function addMyTasksLink()
{
   var popup = widgetUtils.findObject(model.jsonModel.widgets, "id", "HEADER_APP_MENU_BAR");
   if (popup && popup.config && popup.config.widgets)
   {
      var widgets = popup.config.widgets;
      widgets.splice(1, 0,
      {
         id: "HEADER_MY_TASKS",
         name: "alfresco/menus/AlfMenuBarItem",
         config:
         {
            id: "HEADER_MY_TASKS",
            label: "header.menu.tasks.label",
            targetUrl: "my-tasks#filter=workflows|active"
         }
      });
   }
}

function limitLiveSearch()
{
   // Disable live search for all tenants except Alfresco since we are testing out this feature
   if (user.properties.homeTenant !== 'alfresco.com')
   {
      var searchMenuBase = widgetUtils.findObject(model.jsonModel.widgets, "id", "HEADER_SEARCH");
      searchMenuBase.config.liveSearch = false;
   }
}

function setFacetedSearchScope() {
   var searchMenuBase = widgetUtils.findObject(model.jsonModel.widgets, "id", "HEADER_SEARCH");
   searchMenuBase.config.defaultSearchScope = "all_sites";
}

function main()
{
   addCloudInvitationService();
   //removeCustomizeSite();
   addNetworkAdminTools();
   addNetworks();
   // remove the invite people link (CLOUD-2080)
   //addInvitePeople();
   addSiteInvite();
   addCloudSitesMenu();
   //removeAdvancedSearchMenu();
   //forceAllSitesSearchMenu();
   removeMyFilesSharedFiles();
   removeMyTasksMenu();
   addMyTasksLink();
   // ACE-1670 - enable live search for all networks
   // limitLiveSearch();
   setFacetedSearchScope();
}

main();
