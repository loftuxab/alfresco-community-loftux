function runEvaluator(asset, assetType)
{
   var actionSet = "empty",
      actionPerms = [],
      status = [];
   
   var now = new Date();

   /**
    * COMMON FOR ALL TYPES
    */
   
   /**
    * Review
    * Rules: Has rma:pendingReview aspect and rma:reviewAsOf date exists and is before now
    */
   if (asset.hasAspect("rma:pendingReview"))
   {
      if (asset.properties["rma:reviewAsOf"] !== null && asset.properties["rma:reviewAsOf"] < now)
      {
         actionPerms.push("reviewed");
      }
   }

   /**
    * Cutoff
    * Rules: Has rma:pendingCutOff aspect and rma:cutOffAsOf date exists and is before now
    */
   if (asset.hasAspect("rma:pendingCutOff"))
   {
      if (asset.properties["rma:cutOffAsOf"] !== null && asset.properties["rma:cutOffAsOf"] < now)
      {
         actionPerms.push("cutoff");
      }
   }

   switch (assetType)
   {
      /**
       * SPECIFIC TO: RECORD SERIES
       */
      case "record-series":
         actionSet = "recordSeries";
         break;


      /**
       * SPECIFIC TO: RECORD CATEGORY
       */
      case "record-category":
         actionSet = "recordCategory";
         break;
 

      /**
       * SPECIFIC TO: RECORD FOLDER
       */
      case "record-folder":
         actionSet = "recordFolder";
         /**
          * Open or Closed?
          * Rules: rma:isClosed flag
          */
         actionPerms.push(asset.properties["rma:isClosed"] ? "reopen" : "close");
         status.push(asset.properties["rma:isClosed"] ? "closed" : "open");
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
      case "document":
         /**
          * Test values for demo - add these to "rma:record" only
          */
         if (asset.hasAspect("rma:record"))
         {
            actionSet = "record";
            actionPerms.push("transfer");
            actionPerms.push("add-reference");
         }
         else
         {
            actionSet = "undeclaredRecord";
         }
         break;
   }
   
   return (
   {
      actionSet: actionSet,
      actionPermissions: actionPerms,
      status: status
   });
}