/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   var actionSets = {};
   
   // Actions
   var actionSet, actionSetId;
   var myConfig = new XML(config.script);
   
   for each(var xmlActionSet in myConfig..actionSet)
   {
      actionSet = [];
      actionSetId = xmlActionSet.@id.toString();
      
      for each(var xmlAction in xmlActionSet..action)
      {
         actionSet.push(
         {
            id: xmlAction.@id.toString(),
            type: xmlAction.@type.toString(),
            permission: xmlAction.@permission.toString(),
            href: xmlAction.@href.toString(),
            label: xmlAction.@label.toString()
         });
      }
      actionSets[actionSetId] = actionSet;
   }
   
   model.actionSets = actionSets;
}

main();