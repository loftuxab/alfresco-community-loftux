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
                  if (asset.hasAspect("dod:ghosted"))
                  {
                     assetType = "metadata-stub";
                  }
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
            if (index.indexOf("{http://www.alfresco.org/model/recordsmanagement/1.0}") === 0)
            {
               metadata[index.replace("{http://www.alfresco.org/model/recordsmanagement/1.0}", "rma:")] = p_asset.properties[index];
            }
            else if (index.indexOf("{http://www.alfresco.org/model/dod5015/1.0}") === 0)
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
    * Record and Record Folder common evaluators
    */
   recordAndRecordFolder: function Evaluator_recordAndRecordFolder(asset, permissions, status)
   {
      /* Frozen/Unfrozen */
      if (asset.hasAspect("rma:frozen"))
      {
         status["frozen"] = true;
         if (permissions["Unfreeze"])
         {
            permissions["unfreeze"] = true;
         }
      }
      else
      {
         if (permissions["ExtendRetentionPeriodOrFreeze"])
         {
            permissions["freeze"] = true;
         }
      }

      /* Cut Off status */
      if (asset.hasAspect("rma:cutOff"))
      {
         status["cutoff"] = true;
         if (asset.hasAspect("rma:dispositionLifecycle"))
         {
            permissions["undo-cutoff"] = true;
         }
      }

      /* Transferred status */
      if (asset.hasAspect("rma:transferred"))
      {
         status["transferred"] = true;
      }
      
      /* Accessioned status */
      if (asset.hasAspect("rma:ascended"))
      {
         status["accessioned"] = true;
      }
      
      /* Review As Of Date */
      if (asset.hasAspect("rma:vitalRecord"))
      {
         if (asset.properties["rma:reviewAsOf"] != null)
         {
            permissions["review-as-of"] = true;
         }
      }
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

      if (actionAsOf != null)
      {
         permissions["disposition-as-of"] = true;
         
         // Check if action asOf date has passed
         if (actionAsOf < now)
         {
            permissions[actionName] = true;
            return;
         }
      }

      // Next action could become eligible based on event completion
      if (asset.properties["rma:recordSearchDispositionEventsEligible"] == true)
      {
         permissions[actionName] = true;
      }
   },

   /**
    * Record Type evaluator
    */
   recordType: function Evaluator_recordType(asset)
   {
      /* Supported Record Types */
      var recordTypes =
      [
         "digitalPhotographRecord",
         "pdfRecord",
         "scannedRecord",
         "webRecord"
      ],
         currentRecordType = null;

      for (var i = 0; i < recordTypes.length; i++)
      {
         if (asset.hasAspect("dod:" + recordTypes[i]))
         {
            currentRecordType = recordTypes[i];
            break;
         }
      }
      
      return currentRecordType;
   },

   /**
    * Asset Evaluator - main entrypoint
    */
   run: function Evaluator_run(asset)
   {
      var assetType = Evaluator.getAssetType(asset),
         rmNode = rmService.getRecordsManagementNode(asset),
         recordType = null,
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

            /* Record and Record Folder common evaluator */
            Evaluator.recordAndRecordFolder(asset, permissions, status);

            /* Update Cut Off status to folder-specific status */
            if (status["cutoff"] == true)
            {
               delete status["cutoff"];
               status["cutoff-folder"] = true;
            }
            
            /* File new Records */
            permissions["file"] = capabilities["Create"];

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
                  permissions["open-folder"] = true;
               }
            }
            else
            {
               status["open"] = true;
               if (capabilities["CloseFolders"])
               {
                  permissions["close-folder"] = true;
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

            /* Record and Record Folder common evaluator */
            Evaluator.recordAndRecordFolder(asset, permissions, status);

            /* Electronic/Non-electronic documents */
            if (asset.typeShort == "rma:nonElectronicDocument")
            {
               assetType = "record-nonelec";
            }
            else
            {
               permissions["download"] = true;
            }
            
            /* Record Type evaluator */
            recordType = Evaluator.recordType(asset);
            if (recordType != null)
            {
               status[recordType] = true;
            }
            
            /* Undeclare Record */
            if (asset.hasAspect("rma:cutOff") == false)
            {
               permissions["undeclare"] = true;
            }
            
            break;


         /**
          * SPECIFIC TO: GHOSTED RECORD (Metadata Stub)
          */
         case "metadata-stub":
            actionSet = "metadataStub";

            /* Record and Record Folder common evaluator */
            Evaluator.recordAndRecordFolder(asset, permissions, status);

            /* Record Type evaluator */
            recordType = Evaluator.recordType(asset);
            if (recordType != null)
            {
               status[recordType] = true;
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

               /* Record Type evaluator */
               recordType = Evaluator.recordType(asset);
               if (recordType != null)
               {
                  status[recordType] = true;
               }
               else
               {
                  permissions["set-record-type"] = true;
               }
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
