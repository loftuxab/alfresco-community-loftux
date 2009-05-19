function getFilterParams(filter, parsedArgs)
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
   switch (String(filter))
   {
      case "all":
         var filterQuery = "+PATH:\"" + parsedArgs.rootNode.qnamePath + "//*\"";
         filterQuery += " -TYPE:\"{http://www.alfresco.org/model/content/1.0}thumbnail\"";
         filterQuery += " -TYPE:\"{http://www.alfresco.org/model/content/1.0}folder\"";
         filterQuery += " -TYPE:\"{http://www.alfresco.org/model/forum/1.0}forums\"";
         filterQuery += " -TYPE:\"{http://www.alfresco.org/model/forum/1.0}forum\"";
         filterQuery += " -TYPE:\"{http://www.alfresco.org/model/forum/1.0}topic\"";
         filterQuery += " -TYPE:\"{http://www.alfresco.org/model/forum/1.0}post\"";
         filterParams.query = filterQuery;
         break;
         
      case "node":
         filterParams.variablePath = true;
         filterParams.query = "+ID:\"" + parsedArgs.parentNode.nodeRef + "\"";
         break;
      
      default:
         var filterQuery = "+PATH:\"" + parsedArgs.parentNode.qnamePath + "/*\"";
         filterQuery += " -ASPECT:\"{http://www.alfresco.org/model/content/1.0}workingcopy\"";
         filterQuery += " -TYPE:\"{http://www.alfresco.org/model/forum/1.0}forums\"";
         filterQuery += " -TYPE:\"{http://www.alfresco.org/model/forum/1.0}forum\"";
         filterQuery += " -TYPE:\"{http://www.alfresco.org/model/forum/1.0}topic\"";
         filterQuery += " -TYPE:\"{http://www.alfresco.org/model/forum/1.0}post\"";
         
         filterParams.query = filterQuery;
         break;
   }
   
   return filterParams;
}

const TYPE_MAP =
{
   "all": '+(TYPE:"{http://www.alfresco.org/model/content/1.0}content" OR TYPE:"{http://www.alfresco.org/model/application/1.0}filelink" OR TYPE:"{http://www.alfresco.org/model/content/1.0}folder")',
   "documents": '+(TYPE:"{http://www.alfresco.org/model/content/1.0}content" OR TYPE:"{http://www.alfresco.org/model/application/1.0}filelink" OR TYPE:"{http://www.alfresco.org/model/content/1.0}folder")',
   "folders": '+TYPE:"{http://www.alfresco.org/model/content/1.0}folder"',
   "images": "-TYPE:\"{http://www.alfresco.org/model/content/1.0}thumbnail\" +@cm\\:content.mimetype:image/*"
};

function getTypeFilterQuery(type)
{
   return TYPE_MAP[type] || "";
}
