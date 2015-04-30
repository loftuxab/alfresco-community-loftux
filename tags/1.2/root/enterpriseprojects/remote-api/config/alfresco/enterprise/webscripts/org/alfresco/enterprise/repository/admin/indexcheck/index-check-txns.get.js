script:
{
   if(search.searchSubsystem == 'lucene')
   {
       if ((args.fromTime) && (args.fromTime != ""))
       {
          var indexTxnInfo;
          
          if ((args.toTime) && (args.toTime != ""))
          {
             indexTxnInfo = admIndexCheckService.checkTxnsFromToTime(utils.fromISO8601(args.fromTime), utils.fromISO8601(args.toTime));
          }
          else
          {
             indexTxnInfo = admIndexCheckService.checkTxnsFromTime(utils.fromISO8601(args.fromTime));
          }
        
          if (indexTxnInfo == undefined)
          {
             status.code = 404;
             status.message = "Failed to check txns fromTime: "+args.fromTime;
             status.redirect = true;
             break script;
          }
       }
       else if ((args.fromTxnId) && (args.fromTxnId != ""))
       {
          var indexTxnInfo;
          
          if ((args.toTxnId) && (args.toTxnId != ""))
          {
             indexTxnInfo = admIndexCheckService.checkTxnsFromToTxn(args.fromTxnId, args.toTxnId);
          }
          else
          {
             indexTxnInfo = admIndexCheckService.checkTxnsFromTxn(args.fromTxnId);
          }
        
          if (indexTxnInfo == undefined)
          {
             status.code = 404;
             status.message = "Failed to check txns fromTxnId: "+args.fromTxnId;
             status.redirect = true;
             break script;
          }
       }
       else
       {
          status.code = 404;
          status.message = "Must specify a fromTime (in ISO 8601 format) or a fromTxnId (numeric DB id)";
          status.redirect = true;
          break script;
       }
    
             
       // setup model for templates
       model.indexTxnInfo = indexTxnInfo;   
       model.ipAddress = indexTxnInfo.ipAddress;
       
       model.runStartTime = utils.toISO8601(indexTxnInfo.runStartTime);
       model.runEndTime = utils.toISO8601(indexTxnInfo.runEndTime);
       model.minTxnTime = utils.toISO8601(indexTxnInfo.minTxnTime);
       model.maxTxnTime = utils.toISO8601(indexTxnInfo.maxTxnTime);
   }
   else
   {
       status.code = 404;
       status.message = "The index checker is only supported for Lucene.";
       status.redirect = true;
   }
}