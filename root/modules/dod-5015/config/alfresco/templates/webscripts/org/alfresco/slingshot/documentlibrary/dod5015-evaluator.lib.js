/**
 * Asset type evaluator
 */
function getAssetType(asset)
{
   var assetType = "";
   // More detailed asset type
   switch (String(asset.typeShort))
   {
      case "dod:filePlan":
         assetType = "fileplan";
         break;
      case "dod:recordSeries":
         assetType = "record-series";
         break;
      case "dod:recordCategory":
         assetType = "record-category";
         break;
      case "rma:recordFolder":
         assetType = "record-folder";
         break;
      case "rma:nonElectronicRecord":
         assetType = "non-electronic-record";
         break;
      case "cm:content":
         if (asset.hasAspect("rma:record"))
         {
            assetType = "undeclared-record";
            if (asset.hasAspect("rma:declaredRecord"))
            {
               assetType = "record";
            }
         }
         break;
      default:
         assetType = asset.isContainer ? "folder" : "document";
         break;
   }

   return assetType;
}

/**
 * Records Management metadata extracter
 */
function getMetadata(asset)
{
   var metadata = {},
      index;
      
   for (index in asset.properties)
   {
      if (index.indexOf("{http://www.alfresco.org/model/recordsmanagement/1.0}") == 0)
      {
         metadata[index.replace("{http://www.alfresco.org/model/recordsmanagement/1.0}", "rma:")] = asset.properties[index];
      }
   }
   return metadata;
}


/**
 * Asset Evaluator
 */
function runEvaluator(asset, assetType)
{
   var actionSet = "empty",
      permissions = [],
      status = [];
   
   var now = new Date();

   /**
    * COMMON FOR ALL TYPES
    */

   /**
    * Basic permissions
    */
   if (asset.hasPermission("CreateChildren"))
   {
      permissions.push("create");
   }
   if (asset.hasPermission("Write"))
   {
      permissions.push("edit");
   }
   if (asset.hasPermission("Delete"))
   {
      permissions.push("delete");
   }
   if (asset.hasPermission("ChangePermissions"))
   {
      permissions.push("permissions");
   }
   
   /**
    * Multiple parent assocs
    */
   if (asset.parentAssocs["contains"].length > 1)
   {
      status.push("multi-parent " + asset.parentAssocs["contains"].length);
   }
   
   switch (assetType)
   {
      /**
       * SPECIFIC TO: FILEPLAN
       */
      case "fileplan":
         permissions.push("new-series");
         permissions.push("new-category");
         break;


      /**
       * SPECIFIC TO: RECORD SERIES
       */
      case "record-series":
         actionSet = "recordSeries";
         permissions.push("new-category");
         break;


      /**
       * SPECIFIC TO: RECORD CATEGORY
       */
      case "record-category":
         actionSet = "recordCategory";
         permissions.push("new-folder");
         break;
 

      /**
       * SPECIFIC TO: RECORD FOLDER
       */
      case "record-folder":
         actionSet = "recordFolder";
         if (asset.properties["rma:isClosed"])
         {
            permissions.push("reopen");
            status.push("closed");
         }
         else
         {
            permissions.push("file");
            permissions.push("close");
            status.push("open");
         }
         break;
 

      /**
       * SPECIFIC TO: NON-ELECTRONIC RECORD
       */
      case "non-electronic-record":
         actionSet = "nonElectronicRecord";
         break;
 

      /**
       * SPECIFIC TO: RECORD
       */
      case "record":
         actionSet = "record";
         /**
          * Reviewed
          * Rules: Has rma:vitalRecord aspect and rma:reviewAsOf date exists and is before now
          */
         if (asset.hasAspect("rma:vitalRecord"))
         {
            if (asset.properties["rma:reviewAsOf"] != null && asset.properties["rma:reviewAsOf"] < now)
            {
               permissions.push("reviewed");
            }
         }
         /**
          * Disposition Actions
          */
         if (asset.hasAspect("rma:dispositionSchedule"))
         {
            if (asset.properties["rma:dispositionAsOf"] != null && asset.properties["rma:dispositionAsOf"] < now)
            {
               permissions.push(asset.properties["rma:dispositionAction"]);
            }
         }
         break;


      /**
       * SPECIFIC TO: UNDECLARED RECORD
       */
      case "undeclared-record":
         actionSet = "undeclaredRecord";
         break;


      /**
       * SPECIFIC TO: LEGACY TYPES
       */
      default:
         actionSet = assetType;
         break;
   }
   
   return (
   {
      actionSet: actionSet,
      permissions: permissions,
      status: status,
      metadata: getMetadata(asset)
   });
}
