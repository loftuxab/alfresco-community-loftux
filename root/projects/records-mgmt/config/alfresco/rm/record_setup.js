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

if (logfile != "") {
   logger = companyhome.childByNamePath(logfile); // put in companyhome to avoid making a record
   if (logger == null)
    logger = companyhome.createFile(logfile);
}

if (logger != null) logger.content += document.name + ": " +"My name is '" + name + "'" + " \r\n";
if (logger != null) logger.content += document.name + ": " +"Mimetype='" + document.properties.content.mimetype + "'" + " \r\n";
if (logger != null) logger.content += document.name + ": " +"Email=" + document.hasAspect("cm:emailed") + " \r\n";

if (document.name.substring(0, 1) != "~" && document.name != logfile) {

   if (logger != null) if (logger != null) logger.content += document.name + ": " +"Adding record aspect" + " \r\n";
   document.addAspect("rma:record"); // Add just to be sure

   // Handle record id
   // Use the system id if there is no fileplan container

   filePlan = document.parent; // this should be space, but that seems to be the space that rules are in.
   if (filePlan.hasAspect("rma:filePlan")) {
      if (logger != null) logger.content += document.name + ": " +"filePlan.filePlan" + " \r\n";

      lifeCycle = true; // Set up a life cycle on the document

      catid = filePlan.properties["rma:recordCategoryIdentifier"];
      if (filePlan.hasAspect("rma:record"))
         catid = filePlan.properties["rma:recordIdentifier"];

      // need to check for permissions here, otherwise use node-dbid
      recordCounter = filePlan.properties["rma:recordCounter"];
      filePlan.properties["rma:recordCounter"] = recordCounter + 1;
      filePlan.save();
      dbid = pad (String(recordCounter), 4);

      document.properties["rma:recordIdentifier"] = catid + "-" + dbid;
      document.properties["cm:title"] = document.properties["cm:name"];
      document.properties["cm:name"] = catid + "-" + dbid + " " + document.properties["cm:name"];
   }
   else if (filePlan.parent.hasAspect("rma:filePlan")) {
      if (logger != null) logger.content += document.name + ": " +"filePlan.parent.filePlan" + " \r\n";

      lifeCycle = false; // Don't set up a life cycle since this is in a record folder

      if (filePlan.hasAspect("rma:record")) {
         catid = filePlan.properties["rma:recordIdentifier"];
         if (logger != null) logger.content += document.name + ": " + filePlan.name + ".recordIdentifier=" + catid + " \r\n";
      }
      else {
         catid = filePlan.parent.properties["rma:recordCategoryIdentifier"];
         if (logger != null) logger.content += document.name + ": " + filePlan.parent.name + ".recordCategoryIdentifier=" + catid + " \r\n";
      }
      dbid = pad (String(document.properties["sys:node-dbid"]), 5);
      filePlan = filePlan.parent;

      document.properties["rma:recordIdentifier"] = catid + "-" + dbid;
      document.properties["cm:title"] = document.properties["cm:name"];
      document.properties["cm:name"] = catid + "-" + dbid + " " + document.properties["cm:name"];
   }
   else {
      if (logger != null) logger.content += document.name + ": " +"other.filePlan" + " \r\n";

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

   if (logger != null) logger.content += document.name + ": " +"Setting up description" + " \r\n";

   // setup the description
   if (description == "" || description == null) {
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
   if (document.hasAspect("cm:emailed")) {
      document.properties["rma:dateReceived"] = document.properties["cm:sentdate"];
      if (document.properties["rma:dateReceived"] == null)
         document.properties["rma:dateReceived"] = document.properties["cm:modified"];
      originator = document.properties["cm:originator"];
      if (originator == null || originator == "")
          document.properties["rma:originator"] = document.properties["cm:author"] = person.name;
      else
          document.properties["rma:originator"] = document.properties["cm:author"] = originator;
      if (document.properties["cm:subjectline"] != null)
         document.properties["rma:subject"] = document.properties["cm:description"] = document.properties["cm:subjectline"];
      var addressees = "";
      var strarray = document.properties["cm:addressees"];
      if (strarray != null) {
         for (var i = 0; i<strarray.length; i++) {
            if (i != 0) addressees += "; ";
            addressees += strarray[i];
         }
      }
      else {
         addressees = document.properties["cm:addressee"];
      }
      if (addressees == "")
         document.properties["rma:addressee"] = document.properties["cm:creator"];
      else
         document.properties["rma:addressee"] = addressees;
   }
   else {
      if (document.properties["cm:author"] == null || document.properties["cm:author"] == "") {
         document.properties["rma:originator"] = document.properties["cm:creator"];
         document.properties["rma:addressee"] = document.properties["cm:creator"];
      }
      else {
         document.properties["rma:originator"] = document.properties["cm:author"];
         document.properties["rma:addressee"] = document.properties["cm:author"];
      }

      document.properties["rma:dateReceived"] = document.properties["cm:modified"].getTime();

      if (logger != null) logger.content += document.name + ": " +"Person='" + person.username + "'" + " \r\n";
      if (logger != null) logger.content += document.name + ": " +"Creator='" + document.properties["cm:creator"] + "'" + " \r\n";
      if (logger != null) logger.content += document.name + ": " +"Author='" + document.properties["cm:author"] + "'" + " \r\n";
      if (logger != null) logger.content += document.name + ": " +"Addressee='" + document.properties["rma:addressee"] + "'" + " \r\n";
      if (logger != null) logger.content += document.name + ": " +"originator='" + originator + "'" + " \r\n";

   }

   if (logger != null) logger.content += document.name + ": " +"About to save (properties)" + " \r\n";
   document.save();
   if (logger != null) logger.content += document.name + ": " +"Saved (properties)" + " \r\n";
   if (logger != null) logger.save();

   // Process Life Cycle on a Document

   if (lifeCycle) {
      // If there is a vital record indicator, then set up review

      if (logger != null) logger.content += document.name + ": " +"Setting up vital record" + " \r\n";
      if (logger != null) logger.content += document.name + ": " +"vital=" + filePlan.properties["rma:vitalRecordIndicator"] + " \r\n";

      if (filePlan.properties["rma:vitalRecordIndicator"] == true) {

         if (logger != null) logger.content += document.name + ": " +"Adding vital record" + " \r\n";

         document.addAspect("rma:vitalrecord"); // Set-up the aspect

         document.properties["rma:isVitalRecord"] = true;
         document.properties["rma:prevReviewDate"] = document.properties["cm:modified"];
         //reviewPeriod = new Date(document.properties["cm:modified"].getTime());

         // Calculate the next review period.
         reviewInterval = filePlan.properties["rma:vitalRecordReviewPeriod"].name;
         reviewYear = reviewPeriod.getFullYear();
         reviewMonth = reviewPeriod.getMonth();

         if (logger != null) logger.content += document.name + ": " +"reviewInterval=\"" + reviewInterval + "\" \r\n";
         if (logger != null) logger.content += document.name + ": " +"reviewYear=" + reviewYear + " \r\n";
         if (logger != null) logger.content += document.name + ": " +"reviewMonth=" + reviewMonth + " \r\n";

         if (reviewInterval == "Bi-annually") {
            if (logger != null) logger.content += document.name + ": Bi-annually \r\n";
            reviewYear += 2;
         }
         else if (reviewInterval == "Annually" || reviewInterval == "Fiscal Year End" || reviewInterval == "Calendar Year") {
            if (logger != null) logger.content += document.name + ": Annually \r\n";
            // TODO: Proper calculation of fiscal year end (US Govt) and Calendar Year End
            reviewYear += 1;
         }
         else if (reviewInterval == "Semi-annually") {
            if (logger != null) logger.content += document.name + ": Semi-annually \r\n";
            if (reviewMonth >= 6) {
               reviewYear += 1;
               reviewMonth -= 6;
            }
            else {
               reviewMonth += 6;
            }
         }
         else if (reviewInterval == "Quarterly") {
            if (logger != null) logger.content += document.name + ": Quarterly \r\n";
            if (reviewMonth >= 9) {
               reviewYear += 1;
               reviewMonth -= 9;
            }
            else {
               reviewMonth += 3;
            }
         }
         else if (reviewInterval == "Monthly") {
            if (logger != null) logger.content += document.name + ": Monthly \r\n";
            if (reviewMonth >= 11) {
               reviewYear += 1;
               reviewMonth -= 11;
            }
            else {
               reviewMonth += 1;
            }
         }
         else {
            reviewYear += 0;
            reviewMonth += 0;
         }

         if (logger != null) logger.content += document.name + ": " +"Updated reviewInterval=" + reviewInterval + " \r\n";
         if (logger != null) logger.content += document.name + ": " +"Updated reviewYear=" + reviewYear + " \r\n";
         if (logger != null) logger.content += document.name + ": " +"Updated reviewMonth=" + reviewMonth + " \r\n";

         reviewPeriod.setMonth(reviewMonth);
         reviewPeriod.setYear(reviewYear);
         document.properties["rma:nextReviewDate"] = reviewPeriod.getTime();

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

         // Calculate the next cutoff period.
         //cutoffPeriod = new Date(document.properties["cm:modified"].getTime());
         cutoffInterval = filePlan.properties["rma:cutoffPeriod"].name;
         cutoffYear = cutoffPeriod.getFullYear();
         cutoffMonth = cutoffPeriod.getMonth();

         if (logger != null) logger.content += document.name + ": " +"cutoffInterval=\"" + cutoffInterval + "\" \r\n";
         if (logger != null) logger.content += document.name + ": " +"cutoffYear=" + cutoffYear + " \r\n";
         if (logger != null) logger.content += document.name + ": " +"cutoffMonth=" + cutoffMonth + " \r\n";

         if (cutoffInterval == "Bi-annually") {
            if (logger != null) logger.content += document.name + ": Bi-annually \r\n";
            cutoffYear += 2;
         }
         else if (cutoffInterval == "Annually" || cutoffInterval == "Fiscal Year End" || cutoffInterval == "Calendar Year") {
            // TODO: Proper calculation of fiscal year end (US Govt) and Calendar Year End
            if (logger != null) logger.content += document.name + ": Annually \r\n";
            cutoffYear += 1;
         }
         else if (cutoffInterval == "Semi-annually") {
            if (logger != null) logger.content += document.name + ": Semi-annually \r\n";
            if (cutoffMonth >= 6) {
               cutoffYear += 1;
               cutoffMonth -= 6;
            }
            else {
               cutoffMonth += 6;
            }
         }
         else if (cutoffInterval == "Quarterly") {
            if (logger != null) logger.content += document.name + ": Quarterly \r\n";
            if (cutoffMonth >= 9) {
               cutoffYear += 1;
               cutoffMonth -= 9;
            }
            else {
               cutoffMonth += 3;
            }
         }
         else if (cutoffInterval == "Monthly") {
            if (logger != null) logger.content += document.name + ": Monthly \r\n";
            if (cutoffMonth >= 11) {
               cutoffYear += 1;
               cutoffMonth -= 11;
            }
            else {
               cutoffMonth += 1;
            }
         }
         else {
            if (logger != null) logger.content += document.name + ": No change \r\n";
            cutoffYear += 0;
            cutoffMonth += 0;
         }
         cutoffPeriod.setMonth(cutoffMonth);
         cutoffPeriod.setFullYear(cutoffYear);

         if (logger != null) logger.content += document.name + ": " +"Updated cutoffInterval=" + cutoffInterval + " \r\n";
         if (logger != null) logger.content += document.name + ": " +"Updated cutoffYear=" + cutoffYear + " \r\n";
         if (logger != null) logger.content += document.name + ": " +"Updated cutoffMonth=" + cutoffMonth + " \r\n";

         document.properties["rma:cutoffDateTime"] = cutoffPeriod.getTime();

         if (logger != null) logger.content += document.name + ": " +"Complete cutoff" + " \r\n";
         if (logger != null) logger.save();
      } 
   }

   if (logger != null) logger.content += document.name + ": " +"About to save (last)" + " \r\n";
   document.save();
   if (logger != null) logger.content += document.name + ": " +"Saved (last)" + " \r\n";
   if (logger != null) logger.save();
}