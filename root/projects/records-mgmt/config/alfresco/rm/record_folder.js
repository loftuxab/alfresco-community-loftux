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

// Debug log
logger.log(document.name + ": " +"My folder name is '" + name + "'");

if (filePlan.hasAspect("rma:filePlan")) 
{
   if (document.hasAspect("rma:record") == false)       
   {
      // Add the record aspect as it is missing 
      logger.log(document.name + ": Adding record aspect");
      document.addAspect("rma:record");
   }

   // Get the category id
   var catid = filePlan.properties["rma:recordCategoryIdentifier"];
   
   // Get the current record count
   var countAction = actions.create("counter");
   countAction.execute(filePlan);
   var recordCounter = filePlan.properties["cm:counter"];
   logger.log(document.name +": The record counter for this file plan is " + recordCounter);
   
   // Padd toget the db id
   var dbid = utils.pad(String(recordCounter), 3);

   // Debug log
   logger.log(document.name + ": " + "catid = " + catid + " and dbid = " + dbid);
     
   // Set the property values based on the parent file plan
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

   // Multiple saves to solve bug
   logger.log(document.name + ": " +"About to save (properties)");
   document.save(); 
   logger.log(document.name + ": " +"Saved (properties)");
   
   // Setup the description as the disposition instructions for browse view
   logger.log(document.name + ": " +"Setting up description");   
   document.properties["cm:description"] = filePlan.properties["rma:dispositionInstructions"];

   document.properties["rma:subject"] = description;

   document.properties["rma:dateFiled"] = document.properties["cm:modified"];
   document.properties["rma:publicationDate"] = document.properties["cm:modified"];

   document.properties["rma:originator"] = "N/A";

   // Multiple saves to solve bug
   logger.log(document.name + ": " +"About to save (originator)");
   document.save(); 
   logger.log(document.name + ": " +"Saved (originator)");

   // If there is a vital record indicator, then set up review
   logger.log(document.name + ": " +"Setting up vital record");
   logger.log(document.name + ": " +"vital=" + filePlan.properties["rma:vitalRecordIndicator"]);

   if (filePlan.properties["rma:vitalRecordIndicator"] == true) 
   {

      logger.log(document.name + ": " +"Adding vital record");

      document.addAspect("rma:vitalrecord"); // Set-up the aspect

      document.properties["rma:isVitalRecord"] = true;
      document.properties["rma:prevReviewDate"] = document.properties["cm:modified"];

      // Calculate the next review period.
      reviewInterval = filePlan.properties["rma:vitalRecordReviewPeriod"].name;
      reviewYear = reviewPeriod.getFullYear();
      reviewMonth = reviewPeriod.getMonth();

      logger.log(document.name + ": " +"reviewInterval=" + reviewInterval);
      logger.log(document.name + ": " +"reviewYear=" + reviewYear);
      logger.log(document.name + ": " +"reviewMonth=" + reviewMonth);

      if (reviewInterval == "Bi-annually") 
      {
         reviewPeriod.setYear(reviewYear+2);
      }
      else if (reviewInterval == "Annually" || reviewInterval == "Fiscal Year End" || reviewInterval == "Calendar Year End") 
      {
         // TODO: Proper calculation of fiscal year end (US Govt) and Calendar Year End
         reviewPeriod.setYear(reviewYear+1);
      }
      else if (reviewInterval == "Semi-annually") 
      {
         if (reviewMonth >= 6) 
         {
            reviewYear += 1;
            reviewMonth -= 6;
         }
         else 
         {
            reviewMonth += 6;
         }
         reviewPeriod.setMonth(reviewMonth);
         reviewPeriod.setYear(reviewYear);
      }
      else if (reviewInterval == "Quarterly") 
      {
         if (reviewMonth >= 9) 
         {
            reviewYear += 1;
            reviewMonth -= 9;
         }
         else 
         {
            reviewMonth += 3;
         }
         reviewPeriod.setMonth(reviewMonth);
         reviewPeriod.setYear(reviewYear);
      }
      else if (reviewInterval == "Monthly") 
      {
         if (reviewMonth >= 11) 
         {
            reviewYear += 1;
            reviewMonth -= 11;
         }
         else 
         {
            reviewMonth += 1;
         }
         reviewPeriod.setMonth(reviewMonth);
         reviewPeriod.setYear(reviewYear);
      }
      else 
      {
         reviewYear += 0;
         reviewMonth += 0;
         reviewPeriod.setMonth(reviewMonth);
         reviewPeriod.setYear(reviewYear);
      }
      document.properties["rma:nextReviewDate"] = reviewPeriod.getTime();      
      
      logger.log(document.name + ": " +"Complete vital");
      logger.log(document.name + ": " +"About to save (vital)");
      document.save();
      logger.log(document.name + ": " +"Saved (vital)");
   }

   logger.log(document.name + ": " +"Setting up cutoff");
   logger.log(document.name + ": " +"cutoff=" + filePlan.properties["rma:processCutoff"]);

   if (filePlan.properties["rma:processCutoff"] == true) 
   {
      logger.log(document.name + ": " +"Setting up cutoff");

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

      logger.log(document.name + ": " +"cutoffInterval=" + cutoffInterval);
      logger.log(document.name + ": " +"cutoffYear=" + cutoffYear);
      logger.log(document.name + ": " +"cutoffMonth=" + cutoffMonth);

      if (cutoffInterval == "Bi-annually") 
      {
         cutoffPeriod.setYear(cutoffYear+2);
      }
      else if (cutoffInterval == "Annually" || cutoffInterval == "Fiscal Year End" || cutoffInterval == "Calendar Year End") 
      {
        // TODO: Proper calculation of fiscal year end (US Govt) and Calendar Year End
        cutoffPeriod.setYear(cutoffYear+1);
      }
      else if (cutoffInterval == "Semi-annually") 
      {
         if (cutoffMonth >= 6) 
         {
            cutoffYear += 1;
            cutoffMonth -= 6;
         }
         else 
         {
            cutoffMonth += 6;
         }
         cutoffPeriod.setMonth(cutoffMonth);
         cutoffPeriod.setYear(cutoffYear);
      }
      else if (cutoffInterval == "Quarterly") 
      {
         if (cutoffMonth >= 9) 
         {
            cutoffYear += 1;
            cutoffMonth -= 9;
         }
         else 
         {
            cutoffMonth += 3;
         }
         cutoffPeriod.setMonth(cutoffMonth);
         cutoffPeriod.setYear(cutoffYear);
      }
      else if (cutoffInterval == "Monthly") 
      {
         if (cutoffMonth >= 11) 
         {
            cutoffYear += 1;
            cutoffMonth -= 11;
         }
         else 
         {
            cutoffMonth += 1;
         }
         cutoffPeriod.setMonth(cutoffMonth);
         cutoffPeriod.setYear(cutoffYear);
      }
      else 
      {
         cutoffYear += 0;
         cutoffMonth += 0;
         cutoffPeriod.setMonth(cutoffMonth);
         cutoffPeriod.setYear(cutoffYear);
      }
      document.properties["rma:cutoffDateTime"] = cutoffPeriod.getTime();
      logger.log(document.name + ": " +"Complete cutoff");
   } 

   logger.log(document.name + ": " +"About to save (last)");
   document.save();
   logger.log(document.name + ": " +"Saved (last)");
}