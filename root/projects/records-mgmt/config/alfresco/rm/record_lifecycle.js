/**
  * Record Lifecycle Update
  * 
  * Calculate next lifecycle stages as aspects upon update of a record type.
  *
  * Created by: John Newton
  *
  * (c) 2006 Alfresco Software, Inc.
  */

var logfile = ""; // Set to the name of your logfile if you want debugging
var logger = null;

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


if (logfile != null && logfile != "") {
   logger = companyhome.childByNamePath(logfile); // put in company home to avoid creating a record
   if (logger == null)
    logger = companyhome.createFile(logfile);
}

if (logger != null) logger.content += document.name + ": " +"Update on '" + document.name + "'" + " \r\n";
if (logger != null) logger.content += document.name + ": " +"rma:record=" + document.hasAspect("rma:record") + " \r\n";
if (logger != null) logger.content += document.name + ": " +"rma:cutoffable=" + document.hasAspect("rma:cutoffable") + " \r\n";
if (logger != null) logger.content += document.parent.name + ": " +"rma:record=" + document.parent.hasAspect("rma:record") + " \r\n";

if (document.hasAspect("rma:cutoffable") && filePlan.hasAspect("rma:filePlan")) {

   // Handle record id
   // Use the system id if there is no fileplan container

   // Process Life Cycle on a Document

   if (logger != null) logger.content += document.name + ": " +"Checking cutoff=" + document.properties["rma:cutoffExecuted"] + " \r\n";

   processCutoff = false; // Assume don't process cut-off

// Line 50 

   if (document.properties["rma:cutoffExecuted"] == false) {

      // If now cutoff has been executed, but conditions set off cutoff
      // then execute: Hold, Transfer or Destroy in that order

      if (logger != null) logger.content += document.name + ": " +"Setting up cutoff" + " \r\n";

      if (logger != null) logger.content += document.name + ": " +"Obsolete=(" + filePlan.properties["rma:cutoffOnObsolete"] +","+ document.properties["rma:isObsolete"] + ") \r\n";
      if (filePlan.properties["rma:cutoffOnObsolete"] && document.properties["rma:isObsolete"])
         processCutoff = true;

      if (logger != null) logger.content += document.name + ": " +"Superseded=(" + filePlan.properties["rma:cutoffOnsuperseded"] +","+ (document.assocs["rma:superseded"] != null) + ") \r\n";
      if (filePlan.properties["rma:cutoffOnsuperseded"] & document.assocs["rma:superseded"] != null)
         processCutoff = true;

   }

   if (logger != null) logger.content += document.name + ": " +"processCutoff=" + processCutoff + " \r\n";

   if (processCutoff) {

      document.properties["rma:cutoffExecuted"] = true;

      if (logger != null) logger.content += document.name + ": " +"Processing cutoff" + " \r\n";

      // Is next state Hold?

      if (logger != null) logger.content += document.name + ": " + "rma:processHold=" + filePlan.properties["rma:processHold"] + " \r\n";
      if (logger != null) logger.content += document.name + ": " + "rma:holdable=" + document.hasAspect("rma:holdable") + " \r\n";
      if (logger != null) logger.content += document.name + ": " + "rma:processTransfer=" + filePlan.properties["rma:processTransfer"] + " \r\n";
      if (logger != null) logger.content += document.name + ": " + "rma:tranferable=" + document.hasAspect("rma:tranferable") + " \r\n";
      if (logger != null) logger.content += document.name + ": " + "rma:processDestruction=" + filePlan.properties["rma:processDestruction"] + " \r\n";
      if (logger != null) logger.content += document.name + ": " + "rma:tranferable=" + document.hasAspect("rma:destroyable") + " \r\n";

      if (filePlan.properties["rma:processHold"]) {

         if (logger != null) logger.content += document.name + ": " +"Setting up hold" + " \r\n";

         document.addAspect("rma:holdable");
         document.properties["rma:holdExecuted"] = true;
         document.properties["rma:holdUntilEvent"] = filePlan.properties["rma:dispositionInstructions"];
         document.properties["rma:freeze"] = false;

         nextReview = document.properties["cm:modified"].getTime();
         nextReview += ((365*24*60*60*1000) * filePlan.properties["rma:holdPeriod"]);
         cutoffDateTime = document.properties["rma:holdUntil"]; // Coerce cutoffDateTime to Java Date
         cutoffDateTime = nextReview;
         document.properties["rma:holdUntil"] = cutoffDateTime ;
     
         if (logger != null) logger.content += document.name + ": " +"About to save (hold)" + " \r\n";
         document.save();
         if (logger != null) logger.content += document.name + ": " +"Saved (hold)" + " \r\n";

         if (logger != null) logger.content += document.name + ": " + "Hold complete" + " \r\n";
      }
      else if (filePlan.properties["rma:processTransfer"]) {

         // If hold wasn't executed, then a transfer defined should be immediate upon the cutoff event.

         if (logger != null) logger.content += document.name + ": " +"Setting up transfer" + " \r\n";

         document.addAspect("rma:transferable");

         // Execute now, since there is no hold
         document.properties["rma:transferExecuted"] = true;
         nextReview = document.properties["cm:modified"].getTime();
         cutoffDateTime = document.properties["rma:transferDate"]; // Coerce cutoffDateTime to Java Date
         cutoffDateTime = nextReview;
         document.properties["rma:transferDate"] = cutoffDateTime;

         if (logger != null) logger.content += document.name + ": " +"About to save (transfer)" + " \r\n";
         document.save();
         if (logger != null) logger.content += document.name + ": " +"Saved (transfer)" + " \r\n";

         if (logger != null) logger.content += document.name + ": " + "Transfer complete" + " \r\n";
      }

      else if (filePlan.properties["rma:processDestruction"]) {

         // If hold wasn't executed, then a transfer defined should be immediate upon the cutoff event.

         if (logger != null) logger.content += document.name + ": " +"Setting up destruction" + " \r\n";

         document.addAspect("rma:destroyable");

         // Execute now, since there is no hold
         nextReview = document.properties["cm:modified"].getTime();
         cutoffDateTime = document.properties["rma:transferDate"]; // Coerce cutoffDateTime to Java Date
         cutoffDateTime = nextReview;
         document.properties["rma:destructionDate"] = cutoffDateTime;

         if (logger != null) logger.content += document.name + ": " +"About to save (destruction)" + " \r\n";
         document.save();
         if (logger != null) logger.content += document.name + ": " +"Saved (destruction)" + " \r\n";

         if (logger != null) logger.content += document.name + ": " + "Destruction set-up complete" + " \r\n";
      }

      // TODO: Accession

   }

   if (logger != null) logger.content += document.name + ": " +"About to save (last)" + " \r\n";
   document.save();
   if (logger != null) logger.content += document.name + ": " +"Saved (last)" + " \r\n";

}