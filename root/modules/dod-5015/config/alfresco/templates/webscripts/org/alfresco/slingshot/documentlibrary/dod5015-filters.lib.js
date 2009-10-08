var Filters =
{
   TYPE_MAP:
   {
      "documents": '+(TYPE:"{http://www.alfresco.org/model/content/1.0}content" OR TYPE:"{http://www.alfresco.org/model/application/1.0}filelink" OR TYPE:"{http://www.alfresco.org/model/content/1.0}folder")',
      "folders": '+TYPE:"{http://www.alfresco.org/model/content/1.0}folder"',
      "images": "-TYPE:\"{http://www.alfresco.org/model/content/1.0}thumbnail\" +@cm\\:content.mimetype:image/*"
   },
   
   QUERY_TEMPLATES:
   [
      {field: "keywords", template: "%(cm:name cm:title cm:description TEXT)"},
      {field: "name", template: "%(cm:name)"},
      {field: "title", template: "%(cm:title)"},
      {field: "description", template: "%(cm:description)"},
      {field: "creator", template: "%(cm:creator)"},
      {field: "created", template: "%(cm:created)"},
      {field: "modifier", template: "%(cm:modifier)"},
      {field: "modified", template: "%(cm:modified)"},
      {field: "author", template: "%(cm:author)"},
      {field: "markings", template: "%(rmc:supplementalMarkingList)"},
      {field: "dispositionEvents", template: "%(rma:recordSearchDispositionEvents)"},
      {field: "dispositionActionName", template: "%(rma:recordSearchDispositionActionName)"},
      {field: "dispositionActionAsOf", template: "%(rma:recordSearchDispositionActionAsOf)"},
      {field: "dispositionEventsEligible", template: "%(rma:recordSearchDispositionEventsEligible)"},
      {field: "dispositionPeriod", template: "%(rma:recordSearchDispositionPeriod)"},
      {field: "hasDispositionSchedule", template: "%(rma:recordSearchHasDispositionSchedule)"},
      {field: "dispositionInstructions", template: "%(rma:recordSearchDispositionInstructions)"},
      {field: "dispositionAuthority", template: "%(rma:recordSearchDispositionAuthority)"},
      {field: "holdReason", template: "%(rma:recordSearchHoldReason)"},
      {field: "vitalRecordReviewPeriod", template: "%(rma:recordSearchVitalRecordReviewPeriod)"}
   ],
   
   getFilterParams: function Filter_getFilterParams(filter, parsedArgs, favourites)
   {
      var filterParams =
      {
         query: "+PATH:\"" + parsedArgs.parentNode.qnamePath + "/*\"",
         limitResults: null,
         sort: [
         {
            column: "@{http://www.alfresco.org/model/content/1.0}name",
            ascending: true
         }],
         language: "lucene",
         templates: null,
         variablePath: false
      };

      // Max returned results specified?
      var argMax = args["max"];
      if ((argMax != null) && !isNaN(argMax))
      {
         filterParams.limitResults = argMax;
      }

      // Create query based on passed-in arguments
      var filterData = args["filterData"],
         filterQuery = "";

      // Common types and aspects to filter from the UI
      var filterQueryDefaults = " -TYPE:\"{http://www.alfresco.org/model/content/1.0}thumbnail\"" +
                                " -TYPE:\"{http://www.alfresco.org/model/content/1.0}systemfolder\"" +
                                " -TYPE:\"{http://www.alfresco.org/model/recordsmanagement/1.0}dispositionSchedule\"" +
                                " -TYPE:\"{http://www.alfresco.org/model/recordsmanagement/1.0}dispositionActionDefinition\"" +
                                " -TYPE:\"{http://www.alfresco.org/model/recordsmanagement/1.0}dispositionAction\"" +
                                " -TYPE:\"{http://www.alfresco.org/model/recordsmanagement/1.0}hold\"" +
                                " -TYPE:\"{http://www.alfresco.org/model/recordsmanagement/1.0}transfer\"";

      // Create query based on passed-in arguments
      switch (String(filter))
      {
         case "all":
            filterQuery = "+PATH:\"" + parsedArgs.rootNode.qnamePath + "//*\"";
            filterQuery += " -TYPE:\"{http://www.alfresco.org/model/content/1.0}folder\"";
            filterParams.query = filterQuery + filterQueryDefaults;
            break;

         case "node":
            filterParams.query = "+ID:\"" + parsedArgs.rootNode.nodeRef + "\"";
            break;

         case "savedsearch":
            var searchNode = parsedArgs.location.siteNode.getContainer("Saved Searches");
            if (searchNode != null)
            {
               var ssNode = searchNode.childByNamePath(filterData);

               if (ssNode != null)
               {
                  var ssJson = eval('(' + ssNode.content + ')');
                  filterQuery = ssJson.query;
                  // Wrap the query so that only valid items within the filePlan are returned
                  filterParams.variablePath = true;
                  filterParams.query = 'PATH:"' + parsedArgs.rootNode.qnamePath + '//*" AND (' + filterQuery + ')';
                  filterParams.templates = Filters.QUERY_TEMPLATES;
                  filterParams.language = "fts-alfresco";
                  filterParams.namespace = "http://www.alfresco.org/model/recordsmanagement/1.0";
                  // gather up the sort by fields
                  // they are encoded as "property/dir" i.e. "cm:name/asc"
                  if (ssJson.sort && ssJson.sort.length != 0)
                  {
                     var sortPairs = ssJson.sort.split(",");
                     var sort = [];
                     for (var i=0, j; i<sortPairs.length; i++)
                     {
                        if (sortPairs[i].length != 0)
                        {
                           j = sortPairs[i].indexOf("/");
                           sort.push(
                           {
                              column: sortPairs[i].substring(0, j),
                              ascending: (sortPairs[i].substring(j+1) == "asc")
                           });
                        }
                     }
                     filterParams.sort = sort;
                  }
               }
            }
            break;

         case "transfers":
            if (filterData == null)
            {
               filterQuery = "+PATH:\"" + parsedArgs.rootNode.qnamePath + "//*\"";
               filterQuery += " +TYPE:\"{http://www.alfresco.org/model/recordsmanagement/1.0}transfer\"";
               filterParams.query = filterQuery;
            }
            else
            {
               filterParams.variablePath = true;
               filterParams.query = "+PARENT:\"" + filterData + "\"";
            }
            break;

         case "holds":
            if (filterData == null)
            {
               filterQuery = "+PATH:\"" + parsedArgs.rootNode.qnamePath + "//*\"";
               filterQuery += " +TYPE:\"{http://www.alfresco.org/model/recordsmanagement/1.0}hold\"";
               filterParams.query = filterQuery;
            }
            else
            {
               filterParams.variablePath = true;
               filterParams.query = "+PARENT:\"" + filterData + "\"";
            }
            break;

         default:
            filterQuery = "+PATH:\"" + parsedArgs.parentNode.qnamePath + "/*\"";
            filterParams.query = filterQuery + filterQueryDefaults;
            break;
      }

      // Specialise by passed-in type
      if (filterParams.query !== "")
      {
         filterParams.query += " " + (Filters.TYPE_MAP[parsedArgs.type] || "");
      }

      return filterParams;
   }
};
