function disableFacetedSearch()
{
   var searchMenuBase = widgetUtils.findObject(model.jsonModel.widgets, "id", "HEADER_SEARCH");
   if (searchMenuBase && searchMenuBase.config)
   {
      searchMenuBase.config.linkToFacetedSearch = false;
   }
}

function main()
{
   disableFacetedSearch();
}

main();
