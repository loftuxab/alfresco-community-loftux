function addFreeNetworkAdminTools()
{
   // Free accounts always have access to the console (but upgrade information will be displayed instead of actual tools)
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

function main()
{
   // Only internal users are allowed to access Admin Tools - public addresses are not
   if (!user.properties["isExternal"])
   {
      addFreeNetworkAdminTools();
   }
}

main();
