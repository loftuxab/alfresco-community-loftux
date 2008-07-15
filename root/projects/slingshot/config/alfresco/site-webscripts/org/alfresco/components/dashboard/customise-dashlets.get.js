
// Get available components of family/type dashlet
var webscripts = sitedata.findWebScripts("user-dashlet");
if(!webscripts)
{
   webscripts = [];
}
var tmp = sitedata.findWebScripts("dashlet");
if(tmp || tmp.length > 0)
{
   webscripts = webscripts.concat(tmp);
}


// Transform the webscripts to easy-to-access dashlet items for the template
var availableDashlets = [];
for(var i = 0; i < webscripts.length; i++)
{
   var webscript = webscripts[i];
   var uris = webscript.getURIs();
   if(uris !== null && uris.length > 0 && webscript.shortName !== null)
   {
      availableDashlets[i] = {url: uris[0], shortName: webscript.shortName, description: webscript.description};
   }
   else
   {
      // skip this webscript since it lacks uri or shortName
   }
}

var dashboardUrl = "user/" + user.name + "/dashboard";
var components = sitedata.findComponents("page", null, dashboardUrl, null);
if(components === undefined || components.length === 0)
{
   components = [];
}

// Transform the webscripts to easy-to-access dashlet items for the template
var columns = [[], [], [], []];
for(i = 0; i < components.length; i++)
{
   var comp = components[i];

   var regionId = comp.properties["region-id"];
   var url = comp.properties.url;
   if(regionId !== null && url !== null)
   {
      // Creat dashlet
      var shortName;
      var description;
      for(var j = 0; j < availableDashlets.length; j++)
      {
         var d = availableDashlets[j];
         if(d.url == url)
         {
            shortName = d.shortName;
            description = d.description;
            break;
         }
      }
      var dashlet = {url: url, shortName: shortName, description: description, originalRegionId: regionId};

      // Place it in correct column and in a temporary row literal
      if(regionId.match("^component-\\d+-\\d+$"))
      {
         var column = parseInt(regionId.substring(regionId.indexOf("-") + 1, regionId.lastIndexOf("-")));
         var row = parseInt(regionId.substring(regionId.lastIndexOf("-") + 1));
         columns[column-1][row-1] = dashlet;
      }
   }
   else
   {
      // skip this component since it lacks regionId or shortName
   }
}


function getNoOfColumns(template)
{
   var noOfColumns = 0;
   while(template.properties["gridColumn" + (noOfColumns + 1)] !== null)
   {
      noOfColumns++;
   }
   return noOfColumns;
}

// Get current template
var dashboardPage = "user/" + user.name + "/dashboard";
var currentTemplate = sitedata.findTemplate(dashboardPage);
var currentNoOfColumns = getNoOfColumns(currentTemplate);
var currentLayout = {templateId: currentTemplate.id, noOfColumns: currentNoOfColumns, description: currentTemplate.description};

// Define the model for the template
model.availableDashlets = availableDashlets;
model.dashboardUrl = dashboardUrl;
model.columns = columns;
model.currentLayout = currentLayout;
