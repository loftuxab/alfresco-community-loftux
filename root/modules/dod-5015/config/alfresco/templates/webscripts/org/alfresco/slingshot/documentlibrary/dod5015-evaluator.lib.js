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
         case "rma:nonElectronicDocument":
            // Fall-through
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
            /**
             * TODO: Determine whether this is a "transfer" or "accession" container (metadata?)
             */
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
   nextDispositionAction: function Evaluator_nextDispositionAction(asset, permissions, status)
   {
      // Does the asset have a disposition lifecycle?
      if (!asset.hasAspect("rma:dispositionLifecycle"))
      {
         return;
      }
      
      var actionName = asset.properties["rma:recordSearchDispositionActionName"],
         actionAsOf = asset.properties["rma:recordSearchDispositionActionAsOf"],
         now = new Date();

      // Check action asOf date
      if (actionAsOf != null && actionAsOf < now)
      {
         permissions[actionName] = true;
         return;
      }

      // Next action could become eligible based on event completion
      if (asset.properties["rma:recordSearchDispositionEventsEligible"] == true)
      {
         permissions[actionName] = true;
      }
   },

   /**
    * Asset Evaluator
    */
   run: function Evaluator_run(asset)
   {
      var assetType = Evaluator.getAssetType(asset),
         rmNode = rmService.getRecordsManagementNode(asset),
         capabilities = {},
         actions = {},
         actionSet = "empty",
         permissions = {},
         status = {};

      var now = new Date();

      /**
       * Capabilities and Actions
       */
      var cap, act;
      for each (cap in rmNode.capabilities)
      {
         capabilities[cap.name] = true;
         for each (act in cap.actions)
         {
            actions[act] = true;
         }
      }

      /**
       * COMMON FOR ALL TYPES
       */

      /**
       * Basic permissions - start from entire capabiltiies list
       * TODO: Filter-out the ones non relevant to DocLib
       */
      permissions = capabilities;

      /**
       * Multiple parent assocs
       */
      var parents = asset.parentAssocs["contains"];
      if (parents !== null && parents.length > 1)
      {
         status["multi-parent " + parents.length] = true;
      }
      
      /**
       * E-mail type
       */
      if (asset.mimetype == "message/rfc822")
      {
         permissions["split-email"] = true;
      }

      switch (assetType)
      {
         /**
          * SPECIFIC TO: FILE PLAN
          */
         case "fileplan":
            permissions["new-series"] = capabilities["Create"];
            break;


         /**
          * SPECIFIC TO: RECORD SERIES
          */
         case "record-series":
            actionSet = "recordSeries";
            permissions["new-category"] = capabilities["Create"];
            break;


         /**
          * SPECIFIC TO: RECORD CATEGORY
          */
         case "record-category":
            actionSet = "recordCategory";
            permissions["new-folder"] = capabilities["Create"];
            break;


         /**
          * SPECIFIC TO: RECORD FOLDER
          */
         case "record-folder":
            actionSet = "recordFolder";

            /* Disposition Actions */
            Evaluator.nextDispositionAction(asset, permissions, status);

            /* File new Records */
            permissions["file"] = capabilities["Create"];

            /* Cut Off status */
            if (asset.hasAspect("rma:cutOff"))
            {
               status["cutoff-folder"] = true;
            }

            /* Open/Closed */
            if (asset.properties["rma:isClosed"])
            {
               // Cutoff implies closed, so no need to duplicate
               if (!status["cutoff-folder"])
               {
                  status["closed"] = true;
               }
               if (capabilities["ReOpenFolders"])
               {
                  permissions["openFolder"] = true;
               }
            }
            else
            {
               status["open"] = true;
               if (capabilities["CloseFolders"])
               {
                  permissions["closeFolder"] = true;
               }
            }

            /* Frozen/Unfrozen */
            if (asset.hasAspect("rma:frozen"))
            {
               status["frozen"] = true;
               if (capabilities["Unfreeze"])
               {
                  permissions["unfreeze"] = true;
               }
            }
            else
            {
               if (capabilities["ExtendRetentionPeriodOrFreeze"])
               {
                  permissions["freeze"] = true;
               }
            }
            break;


         /**
          * SPECIFIC TO: RECORD
          */
         case "record":
            actionSet = "record";

            /* Disposition Actions */
            Evaluator.nextDispositionAction(asset, permissions, status);

            /* Cut Off status */
            if (asset.hasAspect("rma:cutOff"))
            {
               status["cutoff"] = true;
            }

            /* Frozen/Unfrozen */
            if (asset.hasAspect("rma:frozen"))
            {
               status["frozen"] = true;
               if (capabilities["Unfreeze"])
               {
                  permissions["unfreeze"] = true;
               }
            }
            else
            {
               if (capabilities["ExtendRetentionPeriodOrFreeze"])
               {
                  permissions["freeze"] = true;
               }
            }
            
            /* Electronic/Non-electronic documents */
            if (asset.typeShort == "rma:nonElectronicDocument")
            {
               assetType = "record-nonelec";
            }
            else
            {
               permissions["download"] = true;
            }
            break;


         /**
          * SPECIFIC TO: UNDECLARED RECORD
          */
         case "undeclared-record":
            actionSet = "undeclaredRecord";

            /* Electronic/Non-electronic documents */
            if (asset.typeShort == "rma:nonElectronicDocument")
            {
               assetType = "undeclared-record-nonelec";
            }
            else
            {
               permissions["download"] = true;
            }
            break;


         /**
          * SPECIFIC TO: TRANSFER CONTAINERS
          */
         case "transfer-container":
            actionSet = "transferContainer";
            break;


         /**
          * SPECIFIC TO: ACCESSION CONTAINERS
          */
         case "accession-container":
            actionSet = "accessionContainer";
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
