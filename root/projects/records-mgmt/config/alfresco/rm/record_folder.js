/**
  * Record Folder Setup
  * 
  * Set up the rma:record aspect to attach to a records folder.
  * Also automatically fill in meta-data and change the name to reflect the
  * record id.
  *
  * Created by: John Newton
  *
  * (c) 2006 Alfresco Software, Inc.
  */

var logfile = ""; // Set to the name of your logfile if you want debugging
var logger = null;

/**
 * Pad a string with zero's to the specified length
 * s   - the string to pad
 * len - the length of the string to pad to
 */
function pad(s, len)
{
   var result = s;
   for (var i=0; i<(len - s.length); i++)
   {
      result = "0" + result;
   }
   return result;
}

var dbid = "";
var catid;
var recordCounter = 0;
var description = document.properties["cm:description"];
var title = document.properties["cm:title"];
var name = document.name;

var reviewPeriod = new Date();
var reviewInterval = "";
var reviewMonth = 0;
var reviewYear = 0;

var cutoffPeriod = new Date();
var cutoffInterval = "";
var cutoffMonth = 0;
var cutoffYear = 0;

var lifeCycle = false;

var filePlan = document.parent;

if (logfile != null && logfile != "") {
   logger = companyhome.childByNamePath(logfile);
   if (logger == null)
    logger = companyhome.createFile(logfile);
}

// Line 50 

if (logger != null) logger.content += document.name + ": " +"My folder name is '" + name + "'" + " \r\n";

if (filePlan.hasAspect("rma:filePlan")) {

   if (logger != null) if (logger != null) logger.content += document.name + ": " +"Adding record aspect" + " \r\n";

   document.addAspect("rma:record"); // Add just to be sure

   // Handle record id

   catid = filePlan.properties["rma:recordCategoryIdentifier"];
   recordCounter = filePlan.properties["rma:recordCounter"];
   filePlan.properties["rma:recordCounter"] = recordCounter + 1;
   filePlan.save();
   dbid = pad (String(recordCounter), 3);


   if (logger != null) logger.content += document.name + ": " +"catid = " + catid + " and dbid = " +dbid+ " \r\n";
   document.properties["rma:recordIdentifier"] = catid + "-" + dbid;
   document.properties["cm:title"] = document.properties["cm:name"];
   document.properties["cm:name"] = "Folder " + dbid + " - " + document.properties["cm:name"];
   document.properties["rma:originatingOrganization"] = filePlan.properties["rma:defaultOriginatingOrganization"];
   document.properties["rma:recordNote"]              = filePlan.properties["rma:filePlanNote"];
   document.properties["rma:supplementalMarkingList"] = filePlan.properties["rma:defaultMarkingList"];
   document.properties["rma:mediaFormat"]             = filePlan.properties["rma:defaultMediaType"];
   document.properties["rma:originatingOrganization"] = filePlan.properties["rma:defaultOriginatingOrganization"];

   // User-defined Data - currently privacy information
   document.properties["rma:privacyActSystem"] = filePlan.properties["rma:privacyActSystem"];

   if (logger != null) logger.content += document.name + ": " +"About to save (properties)" + " \r\n";
   document.save(); // Multiple saves to solve bug
   if (logger != null) logger.content += document.name + ": " +"Saved (properties)" + " \r\n";

   if (logger != null) logger.content += document.name + ": " +"Setting up description" + " \r\n";

   // setup the description as the disposition instructions for browse view
   document.properties["cm:description"] = filePlan.properties["rma:dispositionInstructions"];

// Line 100

   document.properties["rma:subject"] = description;

   document.properties["rma:dateFiled"] = document.properties["cm:modified"];
   document.properties["rma:publicationDate"] = document.properties["cm:modified"];

   document.properties["rma:originator"] = "N/A";

   if (logger != null) logger.content += document.name + ": " +"About to save (properties)" + " \r\n";
   document.save();
   if (logger != null) logger.content += document.name + ": " +"Saved (properties)" + " \r\n";

   // If there is a vital record indicator, then set up review

   if (logger != null) logger.content += document.name + ": " +"Setting up vital record" + " \r\n";
   if (logger != null) logger.content += document.name + ": " +"vital=" + filePlan.properties["rma:vitalRecordIndicator"] + " \r\n";

   if (filePlan.properties["rma:vitalRecordIndicator"] == true) {

      if (logger != null) logger.content += document.name + ": " +"Adding vital record" + " \r\n";

      document.addAspect("rma:vitalrecord"); // Set-up the aspect

      document.properties["rma:isVitalRecord"] = true;
      document.properties["rma:prevReviewDate"] = document.properties["cm:modified"];

      // Calculate the next review period.
      reviewInterval = filePlan.properties["rma:vitalRecordReviewPeriod"].name;
      reviewYear = reviewPeriod.getFullYear();
      reviewMonth = reviewPeriod.getMonth();

      if (logger != null) logger.content += document.name + ": " +"reviewInterval=" + reviewInterval + " \r\n";
      if (logger != null) logger.content += document.name + ": " +"reviewYear=" + reviewYear + " \r\n";
      if (logger != null) logger.content += document.name + ": " +"reviewMonth=" + reviewMonth + " \r\n";

      if (reviewInterval == "Bi-annually") {
         reviewPeriod.setYear(reviewYear+2);
      }
      else if (reviewInterval == "Annually" || reviewInterval == "Fiscal Year End" || reviewInterval == "Calendar Year End") {
         // TODO: Proper calculation of fiscal year end (US Govt) and Calendar Year End
         reviewPeriod.setYear(reviewYear+1);
      }
      else if (reviewInterval == "Semi-annually") {
         if (reviewMonth >= 6) {
            reviewYear += 1;
            reviewMonth -= 6;
         }
         else {
            reviewMonth += 6;
         }
         reviewPeriod.setMonth(reviewMonth);
         reviewPeriod.setYear(reviewYear);
      }
      else if (reviewInterval == "Quarterly") {
         if (reviewMonth >= 9) {
            reviewYear += 1;
            reviewMonth -= 9;
         }
         else {
            reviewMonth += 3;
         }
         reviewPeriod.setMonth(reviewMonth);
         reviewPeriod.setYear(reviewYear);
      }
      else if (reviewInterval == "Monthly") {
         if (reviewMonth >= 11) {
            reviewYear += 1;
            reviewMonth -= 11;
         }
         else {
            reviewMonth += 1;
         }
         reviewPeriod.setMonth(reviewMonth);
         reviewPeriod.setYear(reviewYear);
      }
      else {
         reviewYear += 0;
         reviewMonth += 0;
         reviewPeriod.setMonth(reviewMonth);
         reviewPeriod.setYear(reviewYear);
      }
      document.properties["rma:nextReviewDate"] = reviewPeriod.getTime();
      if (logger != null) logger.content += document.name + ": " +"Complete vital" + " \r\n";
      if (logger != null) logger.content += document.name + ": " +"About to save (vital)" + " \r\n";
      document.save();
      if (logger != null) logger.content += document.name + ": " +"Saved (vital)" + " \r\n";
      if (logger != null) logger.save();

   }

   if (logger != null) logger.content += document.name + ": " +"Setting up cutoff" + " \r\n";
   if (logger != null) logger.content += document.name + ": " +"cutoff=" + filePlan.properties["rma:processCutoff"] + " \r\n";

   if (filePlan.properties["rma:processCutoff"] == true) {

      if (logger != null) logger.content += document.name + ": " +"Setting up cutoff" + " \r\n";

      document.addAspect("rma:cutoffable");
      document.properties["rma:cutoffEvent"] = filePlan.properties["rma:eventTrigger"];
      document.properties["rma:cutoffExecuted"] = false;
      document.properties["rma:cutoffNow"] = false;
      document.properties["rma:cutoffDateTime"] = filePlan.properties["rma:cutoffInterval"];

      // Calculate the next cutoff period.
      //cutoffPeriod = new Date(document.properties["cm:modified"].getTime());
      cutoffInterval = filePlan.properties["rma:cutoffPeriod"].name;
      cutoffYear = cutoffPeriod.getFullYear();
      cutoffMonth = cutoffPeriod.getMonth();

      if (logger != null) logger.content += document.name + ": " +"cutoffInterval=" + cutoffInterval + " \r\n";
      if (logger != null) logger.content += document.name + ": " +"cutoffYear=" + cutoffYear + " \r\n";
      if (logger != null) logger.content += document.name + ": " +"cutoffMonth=" + cutoffMonth + " \r\n";

      if (cutoffInterval == "Bi-annually") {
         cutoffPeriod.setYear(cutoffYear+2);
      }
      else if (cutoffInterval == "Annually" || cutoffInterval == "Fiscal Year End" || cutoffInterval == "Calendar Year End") {
        // TODO: Proper calculation of fiscal year end (US Govt) and Calendar Year End
        cutoffPeriod.setYear(cutoffYear+1);
      }
      else if (cutoffInterval == "Semi-annually") {
         if (cutoffMonth >= 6) {
            cutoffYear += 1;
            cutoffMonth -= 6;
         }
         else {
            cutoffMonth += 6;
         }
         cutoffPeriod.setMonth(cutoffMonth);
         cutoffPeriod.setYear(cutoffYear);
      }
      else if (cutoffInterval == "Quarterly") {
         if (cutoffMonth >= 9) {
            cutoffYear += 1;
            cutoffMonth -= 9;
         }
         else {
            cutoffMonth += 3;
         }
         cutoffPeriod.setMonth(cutoffMonth);
         cutoffPeriod.setYear(cutoffYear);
      }
      else if (cutoffInterval == "Monthly") {
         if (cutoffMonth >= 11) {
            cutoffYear += 1;
            cutoffMonth -= 11;
         }
         else {
            cutoffMonth += 1;
         }
         cutoffPeriod.setMonth(cutoffMonth);
         cutoffPeriod.setYear(cutoffYear);
      }
      else {
         cutoffYear += 0;
         cutoffMonth += 0;
         cutoffPeriod.setMonth(cutoffMonth);
         cutoffPeriod.setYear(cutoffYear);
      }
      document.properties["rma:cutoffDateTime"] = cutoffPeriod.getTime();
      if (logger != null) logger.content += document.name + ": " +"Complete cutoff" + " \r\n";
      if (logger != null) logger.save();
   } 

   if (logger != null) logger.content += document.name + ": " +"About to save (last)" + " \r\n";
   document.save();
   if (logger != null) logger.content += document.name + ": " +"Saved (last)" + " \r\n";
}