function extension()
{
   if (model.widgets)
   {
      for (var i = 0; i < model.widgets.length; i++)
      {
         if (model.widgets[i].id == "Search")
         {
            model.widgets[i].name = "Alfresco.CloudSearch";
            break;
         }
      }
   }
}

extension();
