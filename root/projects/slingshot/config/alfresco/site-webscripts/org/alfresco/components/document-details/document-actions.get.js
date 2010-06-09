/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   // Actions
   var actionSets = {}, actionSet, actionSetId, actionId,
      myConfig = new XML(config.script);

   for each (var xmlActionSet in myConfig..actionSet)
   {
      actionSet = [];
      actionSetId = xmlActionSet.@id.toString();
      
      for each (var xmlAction in xmlActionSet..action)
      {
         actionId = xmlAction.@id.toString();
         
         actionSet.push(
         {
            id: actionId,
            type: xmlAction.@type.toString(),
            permission: xmlAction.@permission.toString(),
            href: xmlAction.@href.toString(),
            label: xmlAction.@label.toString()
         });
      }
      actionSets[actionSetId] = actionSet;
   }

   model.actionSets = actionSets;

   // Discover SharePoint (Vti) server port
   var vtiServerJson = "{}";

   result = remote.call("/api/vti/serverDetails");
   if (result.status == 200 && result != "")
   {
      vtiServerJson = result;
   }

   model.vtiServer = vtiServerJson;
}

main();