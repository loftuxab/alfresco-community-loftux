/**
 * Parses the config file and returns an object model of the filters
 */
function getFilters()
{
   // Actions
   var myConfig = new XML(config.script),
      filters = [];

   for each(var xmlFilter in myConfig..filter)
   {
      filters.push(
      {
         id: xmlFilter.@id.toString(),
         data: xmlFilter.@data.toString(),
         label: xmlFilter.@label.toString()
      });
   }

   return filters;
}
