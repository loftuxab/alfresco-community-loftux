// return an array of tool information
var toolInfo = [];

// the current tool may have been specified on the URL
var currentToolId = page.url.templateArgs["toolid"];

// family of tools to use for this console is linked to the current pageId from the URL
var family = page.url.templateArgs["pageid"];
if (family != null)
{
   // collect the tools required for this console
   var tools = sitedata.findWebScripts(family);
   
   // process each tool and generate the data so that a label+link can
   // be output by the component template for each tool required
   for (var i = 0; i < tools.length; i++)
   {
      var tool = tools[i];
      
      // use the webscript ID to generate message bundle IDs
      var id = tool.id;
      var scriptName = id.substring(id.lastIndexOf('/') + 1, id.lastIndexOf('.'));
      var labelId = "tool." + scriptName + ".label";
      var descId = "tool." + scriptName + ".description";
      toolInfo[i] =
      {
         id: scriptName,
         url: tool.getURIs()[0],
         label: (msg.get(labelId) != labelId ? msg.get(labelId) : tool.shortName),
         description: (msg.get(descId) != descId ? msg.get(descId) : tool.description),
         selected: (currentToolId == scriptName)
      };
   }
}
model.tools = toolInfo;