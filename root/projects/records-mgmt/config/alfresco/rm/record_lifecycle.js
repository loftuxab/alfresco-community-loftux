/**
  * Record Lifecycle Update
  * 
  * Calculate next lifecycle stages as aspects upon update of a record type.
  *
  * Created by: John Newton
  *
  * (c) 2006 Alfresco Software, Inc.
  */

var reviewPeriod = new Date();
var reviewInterval = "";
var yearPeriod = 0.0;
var reviewMonth = 0;
var reviewYear = 0;
var cutoffDateTime;
var lifeCycle = false;
var processCutoff = false;
var processHold = false;
var processTransfer = false;
var processDestroy = false;

var filePlan = document.parent; // this should be space, but that seems to be the space that rules are in.

logger.log(document.name + ": " +"Update on '" + document.name + "'");
logger.log(document.name + ": " +"rma:record=" + document.hasAspect("rma:record"));
logger.log(document.name + ": " +"rma:cutoffable=" + document.hasAspect("rma:cutoffable"));
logger.log(document.parent.name + ": " +"rma:record=" + document.parent.hasAspect("rma:record"));

if (document.hasAspect("rma:cutoffable") && filePlan.hasAspect("rma:filePlan")) 
{

   // Handle record id
   // Use the system id if there is no fileplan container

   // Process Life Cycle on a Document

   logger.log(document.name + ": " +"Checking cutoff=" + document.properties["rma:cutoffExecuted"]);

   processCutoff = false; // Assume don't process cut-off   

   if (document.properties["rma:cutoffExecuted"] == false) 
   {

      // If now cutoff has been executed, but conditions set off cutoff
      // then execute: Hold, Transfer or Destroy in that order

      logger.log(document.name + ": " +"Setting up cutoff");

      logger.log(document.name + ": " +"Obsolete=(" + filePlan.properties["rma:cutoffOnObsolete"] +","+ document.properties["rma:isObsolete"] + ")");
      if (filePlan.properties["rma:cutoffOnObsolete"] && document.properties["rma:isObsolete"])
      {
         processCutoff = true;
      }

      logger.log(document.name + ": " +"Superseded=(" + filePlan.properties["rma:cutoffOnsuperseded"] +","+ (document.assocs["rma:superseded"] != null) + ")");
      if (filePlan.properties["rma:cutoffOnsuperseded"] & document.assocs["rma:superseded"] != null)
      {
         processCutoff = true;
      }
   }

   logger.log(document.name + ": " +"processCutoff=" + processCutoff);

   if (processCutoff) {

      document.properties["rma:cutoffExecuted"] = true;

      logger.log(document.name + ": " +"Processing cutoff");

      // Is next state Hold?

      logger.log(document.name + ": " + "rma:processHold=" + filePlan.properties["rma:processHold"]);
      logger.log(document.name + ": " + "rma:holdable=" + document.hasAspect("rma:holdable"));
      logger.log(document.name + ": " + "rma:processTransfer=" + filePlan.properties["rma:processTransfer"]);
      logger.log(document.name + ": " + "rma:tranferable=" + document.hasAspect("rma:tranferable"));
      logger.log(document.name + ": " + "rma:processDestruction=" + filePlan.properties["rma:processDestruction"]);
      logger.log(document.name + ": " + "rma:tranferable=" + document.hasAspect("rma:destroyable"));

      if (filePlan.properties["rma:processHold"]) 
      {

         logger.log(document.name + ": " +"Setting up hold");

         document.addAspect("rma:holdable");
         document.properties["rma:holdExecuted"] = true;
         document.properties["rma:holdUntilEvent"] = filePlan.properties["rma:dispositionInstructions"];
         document.properties["rma:freeze"] = false;

         nextReview = document.properties["cm:modified"].getTime();
         nextReview += ((365*24*60*60*1000) * filePlan.properties["rma:holdPeriod"]);
         cutoffDateTime = document.properties["rma:holdUntil"]; // Coerce cutoffDateTime to Java Date
         cutoffDateTime = nextReview;
         document.properties["rma:holdUntil"] = cutoffDateTime ;
     
         document.save();

         logger.log(document.name + ": " + "Hold complete");
      }
      else if (filePlan.properties["rma:processTransfer"]) 
      {

         // If hold wasn't executed, then a transfer defined should be immediate upon the cutoff event.

         logger.log(document.name + ": " +"Setting up transfer");

         document.addAspect("rma:transferable");

         // Execute now, since there is no hold
         document.properties["rma:transferExecuted"] = true;
         nextReview = document.properties["cm:modified"].getTime();
         cutoffDateTime = document.properties["rma:transferDate"]; // Coerce cutoffDateTime to Java Date
         cutoffDateTime = nextReview;
         document.properties["rma:transferDate"] = cutoffDateTime;

         document.save();

         logger.log(document.name + ": " + "Transfer complete");
      }

      else if (filePlan.properties["rma:processDestruction"]) 
      {

         // If hold wasn't executed, then a transfer defined should be immediate upon the cutoff event.

         logger.log(document.name + ": " +"Setting up destruction");

         document.addAspect("rma:destroyable");

         // Execute now, since there is no hold
         nextReview = document.properties["cm:modified"].getTime();
         cutoffDateTime = document.properties["rma:transferDate"]; // Coerce cutoffDateTime to Java Date
         cutoffDateTime = nextReview;
         document.properties["rma:destructionDate"] = cutoffDateTime;

         document.save();

         logger.log(document.name + ": " + "Destruction set-up complete");
      }

      // TODO: Accession

   }

   // Save document
   document.save();
}