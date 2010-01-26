<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary/dod5015-evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary/dod5015-filters.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary/parse-args.lib.js">

const THUMBNAIL_NAME = "doclib";

var PeopleCache = {};

/**
 * Gets / caches a person object
 * @method getPerson
 * @param username {string} User name
 */
function getPerson(username)
{
   if (typeof PeopleCache[username] == "undefined")
   {
      var person = people.getPerson(username);
      if (person == null && username == "System")
      {
         person =
         {
            properties:
            {
               userName: "System",
               firstName: "System",
               lastName: "User"
            }
         }
      }
      PeopleCache[username] =
      {
         userName: person.properties.userName,
         firstName: person.properties.firstName,
         lastName: person.properties.lastName,
         displayName: (person.properties.firstName + " " + person.properties.lastName).replace(/^\s+|\s+$/g, "")
      };
   }
   return PeopleCache[username];
}

/**
 * Main entry point: Create collection of documents and folders in the given space
 * @method main
 */
function main()
{
   var filter = args.filter,
      items = [],
      assets;

   // Is our thumbnail type registered?
   var haveThumbnails = thumbnailService.isThumbnailNameRegistered(THUMBNAIL_NAME);

   // Use helper function to get the arguments
   var parsedArgs = ParseArgs.getParsedArgs("dod:filePlan");
   if (parsedArgs === null)
   {
      return;
   }

   // "node" Type implies single nodeRef requested
   if (!filter && parsedArgs.type === "node")
   {
      filter = "node";
   }

   // Try to find a filter query based on the passed-in arguments
   var allAssets = [],
      filterParams = Filters.getFilterParams(filter, parsedArgs),
      query = filterParams.query;

   // Query the assets - passing in sort and result limit parameters
   if (query !== "")
   {
      allAssets = search.query(
      {
         query: query,
         language: filterParams.language,
         page:
         {
            maxItems: (filterParams.limitResults ? parseInt(filterParams.limitResults, 10) : 0)
         },
         sort: filterParams.sort,
         templates: filterParams.templates,
         namespace: (filterParams.namespace ? filterParams.namespace : null)
      });
   }

   // Ensure folders appear at the top of the list
   var folderAssets = [],
      documentAssets = [];
   
   for each (asset in allAssets)
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
   
   if (parsedArgs.type === "documents")
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
   var pageSize = args.size || assets.length,
      pagePos = args.pos || "1",
      startIndex = (pagePos - 1) * pageSize;

   assets = assets.slice(startIndex, pagePos * pageSize);
   
   var thumbnail, createdBy, modifiedBy, activeWorkflows, assetEvaluator,
      defaultLocation, location, qnamePaths, displayPaths;

   // Location if we're in a site
   defaultLocation =
   {
      site: parsedArgs.location.site,
      container: parsedArgs.location.container,
      path: parsedArgs.location.path,
      file: null
   };
   
   // Evaluate parent container
   var parent = Evaluator.run(parsedArgs.parentNode);
   
   // User permissions and role
   var user =
   {
      permissions: parent.permissions
   };
   if (defaultLocation.site !== null)
   {
      user.role = parsedArgs.location.siteNode.getMembersRole(person.properties.userName);
   }

   // Populate location and other properties
   for each (asset in assets)
   {
      createdBy = null;
      modifiedBy = null;
      assetEvaluator = {};
      activeWorkflows = [];

      // Get users
      createdBy = getPerson(asset.properties["cm:creator"]);
      modifiedBy = getPerson(asset.properties["cm:modifier"]);
      
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
               site: displayPaths[3],
               container: displayPaths[4],
               path: "/" + displayPaths.slice(5, displayPaths.length).join("/"),
               file: asset.name
            };
         }
         else
         {
            location =
            {
               site: null,
               container: null,
               path: null,
               file: asset.name
            };
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
         };
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
      assetEvaluator = Evaluator.run(asset);
      
      items.push(
      {
         asset: asset,
         type: assetEvaluator.assetType,
         createdBy: createdBy,
         modifiedBy: modifiedBy,
         status: assetEvaluator.status,
         actionSet: assetEvaluator.actionSet,
         actionPermissions: assetEvaluator.permissions,
         suppressRoles: assetEvaluator.suppressRoles,
         dod5015: jsonUtils.toJSONString(assetEvaluator.metadata),
         tags: asset.tags,
         location: location
      });
   }

   var parentMeta = filterParams.variablePath ? null :
   {
      nodeRef: String(parsedArgs.parentNode.nodeRef),
      type: parent.assetType
   };

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
      items: items,
      filePlan: parsedArgs.location.containerNode,
      parent: parentMeta
   });
}

/**
 * Document List Component: doclist
 */
model.doclist = main();