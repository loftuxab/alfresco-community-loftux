// Disable faceted results for advanced search for all tenants except Alfresco...
if (user.properties.homeTenant !== 'alfresco.com')
{
   model.searchPath = "{site}search?t={terms}&q={query}";
   for (var i=0; i<model.widgets.length; i++)
   {
      if (model.widgets[i].id == "AdvancedSearch")
      {
         model.widgets[i].options.searchPath = model.searchPath;
      }
   }
}