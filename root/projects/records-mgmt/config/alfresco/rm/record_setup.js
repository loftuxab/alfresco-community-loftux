/**
  * Record Setup
  * 
  * Set up the rma:record aspect to attach to a document and make it a record.
  * Also automatically fill in meta-data and change the name to reflect the
  * record id.
  *
  * Created by: John Newton
  *
  * (c) 2006 Alfresco Software, Inc.
  */

var dbid = "";
var catid;
var recordCounter = 0;
var now = new Date();
var originator;
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

logger.log(document.name + ": " +"My name is '" + name + "'");
logger.log(document.name + ": " +"Mimetype='" + document.properties.content.mimetype + "'");
logger.log(document.name + ": " +"Email=" + document.hasAspect("cm:emailed"));

if (document.name.substring(0, 1) != "~") 
{
   if (document.hasAspect("rma:record") == false) 
   {
      // Add the record aspect if its missing 
      logger.log(document.name + ": " +"Adding record aspect");
      document.addAspect("rma:record");
   }

   // Handle record id
   // Use the system id if there is no fileplan container

   // this should be space, but that seems to be the space that rules are in.
   filePlan = document.parent; 
   if (filePlan.hasAspect("rma:filePlan") == true) 
   {
      logger.log(document.name + ": " +"filePlan.filePlan");

      lifeCycle = true; // Set up a life cycle on the document

      catid = filePlan.properties["rma:recordCategoryIdentifier"];
      if (filePlan.hasAspect("rma:record"))
      {
         catid = filePlan.properties["rma:recordIdentifier"];
      }
      
      // Get the current record count
      var countAction = actions.create("counter");
      countAction.execute(filePlan);
      recordCounter = filePlan.properties["cm:counter"];      
      dbid = utils.pad(String(recordCounter), 4);

      document.properties["rma:recordIdentifier"] = catid + "-" + dbid;
      document.properties["cm:title"] = document.properties["cm:name"];
      document.properties["cm:name"] = catid + "-" + dbid + " " + document.properties["cm:name"];
   }
   else if (filePlan.parent.hasAspect("rma:filePlan") == true) 
   {
      logger.log(document.name + ": " +"filePlan.parent.filePlan");

      // Don't set up a life cycle since this is in a record folder 
      lifeCycle = false; 

      if (filePlan.hasAspect("rma:record")) 
      {
         catid = filePlan.properties["rma:recordIdentifier"];
         logger.log(document.name + ": " + filePlan.name + ".recordIdentifier=" + catid);
      }
      else 
      {
         catid = filePlan.parent.properties["rma:recordCategoryIdentifier"];
         logger.log(document.name + ": " + filePlan.parent.name + ".recordCategoryIdentifier=" + catid);
      }
      
      dbid = utils.pad(String(document.properties["sys:node-dbid"]), 5);
      filePlan = filePlan.parent;

      document.properties["rma:recordIdentifier"] = catid + "-" + dbid;
      document.properties["cm:title"] = document.properties["cm:name"];
      document.properties["cm:name"] = catid + "-" + dbid + " " + document.properties["cm:name"];
   }
   else 
   {
      logger.log(document.name + ": " +"other.filePlan");

      lifeCycle = false; // Don't set up a life cycle since this is in a record folder

      catid = "0000-00";
      dbid = pad (String(document.properties["sys:node-dbid"]), 4);

      document.properties["rma:recordIdentifier"] = catid + "-" + dbid;
      document.properties["cm:title"] = document.properties["cm:name"];
      document.properties["cm:name"] = catid + "-" + dbid + " " + document.properties["cm:name"];
   }

   /*
   ** Process defaults from FilePlan
   */
   document.properties["rma:originatingOrganization"] = filePlan.properties["rma:defaultOriginatingOrganization"];
   document.properties["rma:recordNote"]              = filePlan.properties["rma:filePlanNote"];
   document.properties["rma:supplementalMarkingList"] = filePlan.properties["rma:defaultMarkingList"];
   document.properties["rma:mediaFormat"]             = filePlan.properties["rma:defaultMediaType"];
   document.properties["rma:originatingOrganization"] = filePlan.properties["rma:defaultOriginatingOrganization"];

   // User-defined Data - currently privacy information
   document.properties["rma:privacyActSystem"] = filePlan.properties["rma:privacyActSystem"];

   logger.log(document.name + ": " +"Setting up description");

   // setup the description
   if (description == "" || description == null) 
   {
      description = title;
      document.properties["cm:description"] = description;
   }

   reviewInterval = filePlan.properties["rma:vitalRecordReviewPeriod"].name;
   document.properties["rma:subject"] = description;

   document.properties["rma:format"] = document.mimetype;
   document.properties["rma:dateFiled"] = document.properties["cm:modified"];
   document.properties["rma:publicationDate"] = document.properties["cm:modified"];

   /*
   ** Process receipt information for emails and documents
   */
   if (document.hasAspect("cm:emailed")) 
   {
      document.properties["rma:dateReceived"] = document.properties["cm:sentdate"];
      if (document.properties["rma:dateReceived"] == null)
      {
         document.properties["rma:dateReceived"] = document.properties["cm:modified"];
      }
      
      originator = document.properties["cm:originator"];
      if (originator == null || originator == "")
      {
          document.properties["rma:originator"] = document.properties["cm:author"] = person.name;
      }
      else
      {
          document.properties["rma:originator"] = document.properties["cm:author"] = originator;
      }
      
      if (document.properties["cm:subjectline"] != null)
      {
         document.properties["rma:subject"] = document.properties["cm:description"] = document.properties["cm:subjectline"];
      }
      
      var addressees = "";
      var strarray = document.properties["cm:addressees"];
      if (strarray != null) 
      {
         for (var i = 0; i<strarray.length; i++) 
         {
            if (i != 0) addressees += "; ";
            addressees += strarray[i];
         }
      }
      else 
      {
         addressees = document.properties["cm:addressee"];
      }
      
      if (addressees == "")
      {
         document.properties["rma:addressee"] = document.properties["cm:creator"];
      }
      else
      {
         document.properties["rma:addressee"] = addressees;
      }
   }
   else 
   {
      if (document.properties["cm:author"] == null || document.properties["cm:author"] == "") 
      {
         document.properties["rma:originator"] = document.properties["cm:creator"];
         document.properties["rma:addressee"] = document.properties["cm:creator"];
      }
      else 
      {
         document.properties["rma:originator"] = document.properties["cm:author"];
         document.properties["rma:addressee"] = document.properties["cm:author"];
      }

      document.properties["rma:dateReceived"] = document.properties["cm:modified"].getTime();

      logger.log(document.name + ": " +"Person='" + person.username + "'");
      logger.log(document.name + ": " +"Creator='" + document.properties["cm:creator"] + "'");
      logger.log(document.name + ": " +"Author='" + document.properties["cm:author"] + "'");
      logger.log(document.name + ": " +"Addressee='" + document.properties["rma:addressee"] + "'");
      logger.log(document.name + ": " +"originator='" + originator + "'");

   }

   logger.log(document.name + ": " +"About to save (properties)");
   document.save();
   logger.log(document.name + ": " +"Saved (properties)");

   // Process Life Cycle on a Document
   if (lifeCycle) 
   {
      // If there is a vital record indicator, then set up review

      logger.log(document.name + ": " +"Setting up vital record");
      logger.log(document.name + ": " +"vital=" + filePlan.properties["rma:vitalRecordIndicator"]);

      if (filePlan.properties["rma:vitalRecordIndicator"] == true) 
      {
         logger.log(document.name + ": " +"Adding vital record");

         document.addAspect("rma:vitalrecord"); // Set-up the aspect

         document.properties["rma:isVitalRecord"] = true;
         document.properties["rma:prevReviewDate"] = document.properties["cm:modified"];
         //reviewPeriod = new Date(document.properties["cm:modified"].getTime());

         // Calculate the next review period.
         reviewInterval = filePlan.properties["rma:vitalRecordReviewPeriod"].name;
         reviewYear = reviewPeriod.getFullYear();
         reviewMonth = reviewPeriod.getMonth();

         logger.log(document.name + ": " +"reviewInterval=\"" + reviewInterval);
         logger.log(document.name + ": " +"reviewYear=" + reviewYear);
         logger.log(document.name + ": " +"reviewMonth=" + reviewMonth);

         if (reviewInterval == "Bi-annually") 
         {
            logger.log(document.name + ": Bi-annually");
            reviewYear += 2;
         }
         else if (reviewInterval == "Annually" || reviewInterval == "Fiscal Year End" || reviewInterval == "Calendar Year") 
         {
            logger.log(document.name + ": Annually");
            // TODO: Proper calculation of fiscal year end (US Govt) and Calendar Year End
            reviewYear += 1;
         }
         else if (reviewInterval == "Semi-annually") 
         {
            logger.log(document.name + ": Semi-annually");
            if (reviewMonth >= 6) 
            {
               reviewYear += 1;
               reviewMonth -= 6;
            }
            else 
            {
               reviewMonth += 6;
            }
         }
         else if (reviewInterval == "Quarterly") 
         {
            logger.log(document.name + ": Quarterly");
            if (reviewMonth >= 9) 
            {
               reviewYear += 1;
               reviewMonth -= 9;
            }
            else 
            {
               reviewMonth += 3;
            }
         }
         else if (reviewInterval == "Monthly") 
         {
            logger.log(document.name + ": Monthly");
            if (reviewMonth >= 11) 
            {
               reviewYear += 1;
               reviewMonth -= 11;
            }
            else 
            {
               reviewMonth += 1;
            }
         }
         else 
         {
            reviewYear += 0;
            reviewMonth += 0;
         }

         logger.log(document.name + ": " +"Updated reviewInterval=" + reviewInterval);
         logger.log(document.name + ": " +"Updated reviewYear=" + reviewYear);
         logger.log(document.name + ": " +"Updated reviewMonth=" + reviewMonth);

         reviewPeriod.setMonth(reviewMonth);
         reviewPeriod.setYear(reviewYear);
         document.properties["rma:nextReviewDate"] = reviewPeriod.getTime();

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

         // Calculate the next cutoff period.
         //cutoffPeriod = new Date(document.properties["cm:modified"].getTime());
         cutoffInterval = filePlan.properties["rma:cutoffPeriod"].name;
         cutoffYear = cutoffPeriod.getFullYear();
         cutoffMonth = cutoffPeriod.getMonth();

         logger.log(document.name + ": " +"cutoffInterval=\"" + cutoffInterval);
         logger.log(document.name + ": " +"cutoffYear=" + cutoffYear);
         logger.log(document.name + ": " +"cutoffMonth=" + cutoffMonth);

         if (cutoffInterval == "Bi-annually") 
         {
            logger.log(document.name + ": Bi-annually");
            cutoffYear += 2;
         }
         else if (cutoffInterval == "Annually" || cutoffInterval == "Fiscal Year End" || cutoffInterval == "Calendar Year") 
         {
            // TODO: Proper calculation of fiscal year end (US Govt) and Calendar Year End
            logger.log(document.name + ": Annually");
            cutoffYear += 1;
         }
         else if (cutoffInterval == "Semi-annually") 
         {
            logger.log(document.name + ": Semi-annually");
            if (cutoffMonth >= 6) 
            {
               cutoffYear += 1;
               cutoffMonth -= 6;
            }
            else 
            {
               cutoffMonth += 6;
            }
         }
         else if (cutoffInterval == "Quarterly") 
         {
            logger.log(document.name + ": Quarterly");
            if (cutoffMonth >= 9) 
            {
               cutoffYear += 1;
               cutoffMonth -= 9;
            }
            else 
            {
               cutoffMonth += 3;
            }
         }
         else if (cutoffInterval == "Monthly") 
         {
            logger.log(document.name + ": Monthly");
            if (cutoffMonth >= 11) 
            {
               cutoffYear += 1;
               cutoffMonth -= 11;
            }
            else 
            {
               cutoffMonth += 1;
            }
         }
         else 
         {
            logger.log(document.name + ": No change");
            cutoffYear += 0;
            cutoffMonth += 0;
         }
         cutoffPeriod.setMonth(cutoffMonth);
         cutoffPeriod.setFullYear(cutoffYear);

         logger.log(document.name + ": " +"Updated cutoffInterval=" + cutoffInterval);
         logger.log(document.name + ": " +"Updated cutoffYear=" + cutoffYear);
         logger.log(document.name + ": " +"Updated cutoffMonth=" + cutoffMonth);

         document.properties["rma:cutoffDateTime"] = cutoffPeriod.getTime();

         logger.log(document.name + ": " +"Complete cutoff");
      } 
   }

   document.save();
}