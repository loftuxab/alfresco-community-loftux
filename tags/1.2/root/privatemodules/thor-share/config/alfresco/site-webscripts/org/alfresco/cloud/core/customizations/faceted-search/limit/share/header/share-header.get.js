function disableFacetedSearch()
{
   // Disable live search for all tenants except Alfresco since we are testing out this feature
   if (user.properties.homeTenant !== 'alfresco.com')
   {
      var searchMenuBase = widgetUtils.findObject(model.jsonModel.widgets, "id", "HEADER_SEARCH");
      if (searchMenuBase && searchMenuBase.config)
      {
         searchMenuBase.config.linkToFacetedSearch = false;
      }
   }
}

function main()
{
   disableFacetedSearch();
}

main();
