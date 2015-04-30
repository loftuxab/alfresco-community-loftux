function extension()
{
   if (model.widgets)
   {
      for (var i = 0, ii = model.widgets.length; i < ii; i++)
      {
         if (model.widgets[i].id == "ManagePermissions")
         {
            model.widgets[i].options.showGroups = false;
            break;
         }
      }
   }
}

extension();
