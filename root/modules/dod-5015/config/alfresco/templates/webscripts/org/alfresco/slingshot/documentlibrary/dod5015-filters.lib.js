function getFilterParams(filter, parsedArgs, favourites)
{
   var filterParams =
   {
      query: "+PATH:\"" + parsedArgs.parentNode.qnamePath + "/*\"",
      limitResults: null,
      sortBy: "@{http://www.alfresco.org/model/content/1.0}name",
      sortByAscending: true,
      variablePath: false
   }

   // Max returned results specified?
   var argMax = args["max"];
   if ((argMax != null) && !isNaN(argMax))
   {
      filterParams.limitResults = argMax;
   }

   // Create query based on passed-in arguments
   var filterId = String(filter),
      filterData = String(args["filterData"]);

   // Common types and aspects to filter from the UI
   var filterQueryDefaults = " -ASPECT:\"{http://www.alfresco.org/model/content/1.0}workingcopy\"";
   filterQueryDefaults += " -TYPE:\"{http://www.alfresco.org/model/content/1.0}thumbnail\"";
   filterQueryDefaults += " -TYPE:\"{http://www.alfresco.org/model/content/1.0}systemfolder\"";
   filterQueryDefaults += " -TYPE:\"{http://www.alfresco.org/model/forum/1.0}forums\"";
   filterQueryDefaults += " -TYPE:\"{http://www.alfresco.org/model/forum/1.0}forum\"";
   filterQueryDefaults += " -TYPE:\"{http://www.alfresco.org/model/forum/1.0}topic\"";
   filterQueryDefaults += " -TYPE:\"{http://www.alfresco.org/model/forum/1.0}post\"";

   // Create query based on passed-in arguments
   switch (String(filter))
   {
      case "all":
         var filterQuery = "+PATH:\"" + parsedArgs.rootNode.qnamePath + "//*\"";
         filterQuery += " -TYPE:\"{http://www.alfresco.org/model/content/1.0}folder\"";
         filterParams.query = filterQuery + filterQueryDefaults;
         break;
         
      case "node":
         filterParams.variablePath = true;
         filterParams.query = "+ID:\"" + parsedArgs.parentNode.nodeRef + "\"";
         break;
      
      case "savedsearch":
         var searchNode = parsedArgs.location.siteNode.getContainer("Saved Searches");
         if (searchNode != null)
         {
            var ssNode = searchNode.childByNamePath(filterData),
               cleanXml, e4x;

            if (ssNode != null)
            {
               cleanXml = new String(ssNode.content);
               cleanXml = cleanXml.replace(/^<\?xml\s+version\s*=\s*(["'])[^\1]+\1[^?]*\?>/, ""); // Rhino E4X bug 336551
               cleanXml = cleanXml.replace(/^\s+|\s+$/g, ""); // General Rhino E4X inability to handle leading whitespace
               e4x = new XML(cleanXml);
               filterParams.query = e4x.query.toString();
            }
         }
         break;
      
      default:
         var filterQuery = "+PATH:\"" + parsedArgs.parentNode.qnamePath + "/*\"";
         filterParams.query = filterQuery + filterQueryDefaults;
         break;
   }
   
   return filterParams;
}

const TYPE_MAP =
{
   "documents": '+(TYPE:"{http://www.alfresco.org/model/content/1.0}content" OR TYPE:"{http://www.alfresco.org/model/application/1.0}filelink" OR TYPE:"{http://www.alfresco.org/model/content/1.0}folder")',
   "folders": '+TYPE:"{http://www.alfresco.org/model/content/1.0}folder"',
   "images": "-TYPE:\"{http://www.alfresco.org/model/content/1.0}thumbnail\" +@cm\\:content.mimetype:image/*"
};

function getTypeFilterQuery(type)
{
   return TYPE_MAP[type] || "";
}
