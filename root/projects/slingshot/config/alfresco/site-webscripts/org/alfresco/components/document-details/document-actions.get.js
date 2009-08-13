/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   var nodeRef, result, currentActionSet = "empty", type = "", actionSet = [];
   
   // Request the document's doclib metadata
   nodeRef = page.url.args.nodeRef.replace(":/", "");
   result = remote.call("/slingshot/doclib/doclist/node/" + nodeRef);
   if (result.status == 200)
   {
      var data = eval('(' + result + ')');
      currentActionSet = data.items[0].actionSet;
      type = data.items[0].type;
   }

   // Actions
   var myConfig = new XML(config.script),
      xmlActionSet = myConfig..actionSet.(@id == currentActionSet)
   
   // Found match?
   if (xmlActionSet.@id == currentActionSet)
   {
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
   }
   
   model.actionSet = actionSet;
   model.type = type;
}

main();