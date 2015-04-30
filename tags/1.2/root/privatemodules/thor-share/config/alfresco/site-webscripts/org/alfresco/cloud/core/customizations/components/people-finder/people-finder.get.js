function extension()
{
   if (model.widgets)
   {
      for (var i = 0, ii = model.widgets.length; i < ii; i++)
      {
         if (model.widgets[i].id == "PeopleFinder")
         {
            model.widgets[i].options.userHomeTenant = (user.properties["homeTenant"] != null) ? user.properties["homeTenant"] : null;
            model.widgets[i].name = "Alfresco.CloudPeopleFinder";
            break;
         };
      }
   }
}

extension();
