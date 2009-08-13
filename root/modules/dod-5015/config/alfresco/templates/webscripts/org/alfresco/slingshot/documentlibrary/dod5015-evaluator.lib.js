var Evaluator =
{
   /**
    * Asset type evaluator
    */
   getAssetType: function Evaluator_getAssetType(asset)
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
         case "rma:transfer":
            assetType = "transfer-container";
            break;
         case "rma:hold":
            assetType = "hold-container";
            break;
         default:
            assetType = asset.isContainer ? "folder" : "document";
            break;
      }

      return assetType;
   },

   /**
    * Records Management metadata extracter
    */
   getMetadata: function Evaluator_getMetadata(asset)
   {
      var metadata = {};

      var fnExtract = function(p_asset)
      {
         for (var index in p_asset.properties)
         {
            if (index.indexOf("{http://www.alfresco.org/model/recordsmanagement/1.0}") == 0)
            {
               metadata[index.replace("{http://www.alfresco.org/model/recordsmanagement/1.0}", "rma:")] = p_asset.properties[index];
            }
            else if (index.indexOf("{http://www.alfresco.org/model/dod5015/1.0}") == 0)
            {
               metadata[index.replace("{http://www.alfresco.org/model/dod5015/1.0}", "dod:")] = p_asset.properties[index];
            }
         }
      };

      // General Records Management properties
      fnExtract(asset);

      // Disposition Instructions, if relevant
      if (asset.hasAspect("rma:scheduled"))
      {
         var dsNode = asset.childAssocs["rma:dispositionSchedule"][0];
         if (dsNode !== null)
         {
            fnExtract(dsNode);
         }
      }

      return metadata;
   },

   /**
    * Disposition evaluator
    */
   filterDispositionActions: function Evaluator_filterDispositionActions(asset, permissions, status)
   {
      var actionName = asset.properties["rma:recordSearchDispositionActionName"];

      if (actionName !== null)
      {
         if (actionName !== "accession")
         {
            delete permissions["accession"];
         }
         
         if (actionName !== "cutoff")
         {
            delete permissions["cutoff"];
         }
         
         if (actionName !== "destroy")
         {
            delete permissions["destroy"];
         }
         
         if (actionName !== "retain")
         {
            delete permissions["retain"];
         }
         
         if (actionName !== "transfer")
         {
            delete permissions["transfer"];
         }
      }
   },

   /**
    * Asset Evaluator
    */
   run: function Evaluator_run(asset)
   {
      var assetType = Evaluator.getAssetType(asset),
         actionSet = "empty",
         permissions = {},
         status = {};

      var now = new Date();

      /**
       * COMMON FOR ALL TYPES
       */

      /**
       * Basic permissions - replace these with RM equivalents
       */
      if (asset.hasPermission("CreateChildren"))
      {
         permissions["create"] = true;
      }
      if (asset.hasPermission("Write"))
      {
         permissions["edit"] = true;
      }
      if (asset.hasPermission("Delete"))
      {
         permissions["delete"] = true;
      }
      if (asset.hasPermission("ChangePermissions"))
      {
         permissions["permissions"] = true;
      }

      /**
       * Multiple parent assocs
       */
      var parents = asset.parentAssocs["contains"];
      if (parents !== null && parents.length > 1)
      {
         status["multi-parent " + parents.length] = true;
      }

      switch (assetType)
      {
         /**
          * SPECIFIC TO: FILEPLAN
          */
         case "fileplan":
            permissions["new-series"] = true;
            break;


         /**
          * SPECIFIC TO: RECORD SERIES
          */
         case "record-series":
            actionSet = "recordSeries";
            permissions["new-category"] = true;
            break;


         /**
          * SPECIFIC TO: RECORD CATEGORY
          */
         case "record-category":
            actionSet = "recordCategory";
            permissions["new-folder"] = true;
            break;


         /**
          * SPECIFIC TO: RECORD FOLDER
          */
         case "record-folder":
            actionSet = "recordFolder";

            /**
             * Open/Close
             */
            if (asset.properties["rma:isClosed"])
            {
               permissions["reopen"] = true;
               status["closed"] = true;
            }
            else
            {
               permissions["file"] = true;
               permissions["close"] = true;
               status["open"] = true;
            }

            /**
             * Disposition Actions
             */
            permissions["accession"] = true;
            permissions["cutoff"] = true;
            permissions["destroy"] = true;
            permissions["retain"] = true;
            permissions["transfer"] = true;

            /**
             * Freeze/Unfreeze
             */
            if (asset.hasAspect("rma:frozen"))
            {
               permissions["Unfreeze"] = true;
               status["frozen"] = true;
            }
            else
            {
               permissions["ExtendRetentionPeriodOrFreeze"] = true;
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
                  permissions["reviewed"] = true;
               }
            }

            /**
             * Disposition Actions
             *
             * Note: Development values currently
             */
            if (!asset.hasAspect("rma:cutOff"))
            {
               permissions["accession"] = true;
               permissions["cutoff"] = true;
               permissions["destroy"] = true;
               permissions["retain"] = true;
               permissions["transfer"] = true;
            }

            /**
             * Freeze/Unfreeze
             */
            if (asset.hasAspect("rma:frozen"))
            {
               permissions["Unfreeze"] = true;
               status["frozen"] = true;
            }
            else
            {
               permissions["ExtendRetentionPeriodOrFreeze"] = true;
            }
            permissions["UndeclareRecords"] = true;
            break;


         /**
          * SPECIFIC TO: UNDECLARED RECORD
          */
         case "undeclared-record":
            actionSet = "undeclaredRecord";
            permissions["DeclareRecords"] = true;
            break;


         /**
          * SPECIFIC TO: TRANSFER CONTAINERS
          */
         case "transfer-container":
            actionSet = "transferContainer";
            permissions["download-zip"] = true;
            permissions["file-report"] = true;
            permissions["transfer-complete"] = true;
            break;


         /**
          * SPECIFIC TO: HOLD CONTAINERS
          */
         case "hold-container":
            actionSet = "holdContainer";
            permissions["Unfreeze"] = true;
            permissions["ViewUpdateReasonsForFreeze"] = true;
            break;

         /**
          * SPECIFIC TO: LEGACY TYPES
          */
         default:
            actionSet = assetType;
            break;
      }

      // Filter by next Disposition action
      Evaluator.filterDispositionActions(asset, permissions, status);

      return (
      {
         assetType: assetType,
         actionSet: actionSet,
         permissions: permissions,
         status: status,
         metadata: Evaluator.getMetadata(asset, assetType)
      });
   }
};
