function getFilterParams(filter, parsedArgs, favourites)
{
   var filterParams =
   {
      query: "+PATH:\"" + parsedArgs.parentNode.qnamePath + "/*\"",
      limitResults: null,
      sortBy: "@{http://www.alfresco.org/model/content/1.0}name",
      sortByAscending: true,
      language: "lucene",
      templates: null,
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
   filterQueryDefaults += " -TYPE:\"{http://www.alfresco.org/model/recordsmanagement/1.0}dispositionSchedule\"";

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
            var ssNode = searchNode.childByNamePath(filterData);

            if (ssNode != null)
            {
               var ssJson = eval('(' + ssNode.content + ')');
               var filterQuery = ssJson.query;
               // Wrap the query so that only items within the filePlan are returned
               filterParams.query = 'PATH:"' + parsedArgs.rootNode.qnamePath + '//*" AND ASPECT:"rma:record" AND (' + filterQuery + ')';
               filterParams.templates = [
               {
                  field: "KEYWORDS",
                  template: "%(cm:name cm:title cm:description TEXT)"
               }];
               filterParams.language = "fts-alfresco";
            }
         }
         break;
      
      case "transfers":
         filterParams.variablePath = true;
         /**
          * Return list of Transfers which are hopefully nodeRefs to which we can attach the "transfer complete" action
          */
         filterParams.query = "";
         break;
      
      case "holds":
         if (filterData == "")
         {
            /**
             * Return list of Hold folders - might need special-case handling in doclist.get.js (let's hope not)
             */
         }
         else
         {
            filterParams.variablePath = true;
            /**
             * Get the Hold name from the filterData and then put together a query to list all the Record Folders
             * and Records associated with that Hold.
             */
         }
         filterParams.query = "";
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
