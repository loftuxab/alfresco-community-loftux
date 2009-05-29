<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary/dod5015-evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary/dod5015-filters.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary/parse-args.lib.js">
var THUMBNAIL_NAME = "doclib";

/**
 * Document List Component: doclist
 */
model.doclist = getDocList(args["filter"]);

/* Create collection of documents and folders in the given space */
function getDocList(filter)
{
   // Is our thumbnail tpe registered?
   var haveThumbnails = thumbnailService.isThumbnailNameRegistered(THUMBNAIL_NAME);

   // Use helper function to get the arguments
   var parsedArgs = getParsedArgs("rma:filePlan");
   if (parsedArgs === null)
   {
      return;
   }

   // Type missing implies single nodeRef requested
   if (url.templateArgs.type === null)
   {
      filter = "node";
   }

   // Try to find a filter query based on the passed-in arguments
   var allAssets, filterQuery, query;
   var filterParams = getFilterParams(filter, parsedArgs);
   query = filterParams.query;

   // Specialise by passed-in type
   var typeQuery = getTypeFilterQuery(url.templateArgs.type);
   query += " " + typeQuery;

   // Sort the list before trimming to page chunks 
   allAssets = search.luceneSearch(query, filterParams.sortBy, filterParams.sortByAscending);

   // Limit the resultset?
   if (filterParams.limitResults)
   {
      /**
       * This isn't a true results trim (page-trimming is done below), as we haven't yet filtered by type.
       * However, it's useful for a quick slimming-down of the "recently..." queries.
       */
      allAssets = allAssets.slice(0, filterParams.limitResults);
   }
      
   // Ensure folders appear at the top of the list
   folderAssets = [];
   documentAssets = [];
   for each(asset in allAssets)
   {
      try
      {
         if (asset.isContainer)
         {
            folderAssets.push(asset);
         }
         else
         {
            documentAssets.push(asset);
         }
      }
      catch (e)
      {
         // Possibly an old indexed node - ignore it
      }
   }
   
   var folderAssetsCount = folderAssets.length,
      documentAssetsCount = documentAssets.length;
   
   var assets;
   if (url.templateArgs.type === "documents")
   {
      assets = documentAssets;
   }
   else
   {
      assets = folderAssets.concat(documentAssets);
   }
   
   // Make a note of totalRecords before trimming the assets array
   var totalRecords = assets.length;

   // Pagination
   var pageSize = args["size"] || assets.length,
      pagePos = args["pos"] || "1",
      startIndex = (pagePos - 1) * pageSize;

   assets = assets.slice(startIndex, pagePos * pageSize);
   
   var thumbnail, createdBy, modifiedBy, activeWorkflows, assetType, assetEvaluator,
      defaultLocation, location, qnamePaths, displayPaths, locationAsset;

   // Location if we're in a site
   defaultLocation =
   {
      site: parsedArgs.location.site,
      container: parsedArgs.location.container,
      path: parsedArgs.location.path,
      file: null
   }
   
   // User permissions and role
   var user =
   {
      permissions: runEvaluator(parsedArgs.parentNode, getAssetType(parsedArgs.parentNode)).permissions
   };
   if (defaultLocation.site !== null)
   {
      user.role = parsedArgs.location.siteNode.getMembersRole(person.properties["userName"]);
   }

   var items = [];
   
   // Locked/working copy status defines action set
   for each (asset in assets)
   {
      createdBy = null;
      modifiedBy = null;
      assetEvaluator = {};
      activeWorkflows = [];

      // Get users
      createdBy = people.getPerson(asset.properties["cm:creator"]);
      modifiedBy = people.getPerson(asset.properties["cm:modifier"]);
      
      // Asset type
      assetType = getAssetType(asset);

      // Does this collection of assets have potentially differering paths?
      if (filterParams.variablePath)
      {
         qnamePaths = asset.qnamePath.split("/");
         displayPaths = asset.displayPath.split("/");

         if ((qnamePaths.length > 5) && (qnamePaths[2] == "st:sites"))
         {
            // This asset belongs to a site
            location =
            {
               site: qnamePaths[3].substr(3),
               container: qnamePaths[4].substr(3),
               path: "/" + displayPaths.slice(5, displayPaths.length).join("/"),
               file: asset.name
            }
         }
         else
         {
            location =
            {
               site: null,
               container: null,
               path: null,
               file: asset.name
            }
         }
      }
      else
      {
         location =
         {
            site: defaultLocation.site,
            container: defaultLocation.container,
            path: defaultLocation.path,
            file: asset.name
         }
      }

      // Make sure we have a thumbnail
      if (haveThumbnails)
      {
         thumbnail = asset.getThumbnail(THUMBNAIL_NAME);
         if (thumbnail === null)
         {
            // No thumbnail, so queue creation
            asset.createThumbnail(THUMBNAIL_NAME, true);
         }
      }
      
      // Get evaluated properties
      assetEvaluator = runEvaluator(asset, assetType);
      
      items.push(
      {
         asset: asset,
         type: assetType,
         createdBy: createdBy,
         modifiedBy: modifiedBy,
         status: assetEvaluator.status,
         actionSet: assetEvaluator.actionSet,
         actionPermissions: assetEvaluator.permissions,
         dod5015: jsonUtils.toJSONString(assetEvaluator.metadata),
         tags: asset.tags,
         location: location
      });
   }

   return (
   {
      luceneQuery: query,
      onlineEditing: utils.moduleInstalled("org.alfresco.module.vti"),
      itemCount:
      {
         folders: folderAssetsCount,
         documents: documentAssetsCount
      },
      paging:
      {
         startIndex: startIndex,
         totalRecords: totalRecords
      },
      user: user,
      items: items
   });
}
