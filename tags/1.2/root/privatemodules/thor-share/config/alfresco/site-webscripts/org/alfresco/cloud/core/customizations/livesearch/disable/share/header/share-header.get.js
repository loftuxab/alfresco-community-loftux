function disableLiveSearch()
{
   var searchMenuBase = widgetUtils.findObject(model.jsonModel.widgets, "id", "HEADER_SEARCH");
   if (searchMenuBase && searchMenuBase.config)
   {
      searchMenuBase.config.liveSearch = false;
   }
}

function main()
{
   disableLiveSearch();
}

main();
